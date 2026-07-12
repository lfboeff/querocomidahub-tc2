package br.com.fiap.querocomidahub.menuitem.infrastructure.web.controller;

import br.com.fiap.querocomidahub.menuitem.MenuItemTestFixtures;
import br.com.fiap.querocomidahub.menuitem.application.controller.MenuItemController;
import br.com.fiap.querocomidahub.menuitem.application.dto.MenuItemOutputDTO;
import br.com.fiap.querocomidahub.menuitem.domain.exception.MenuItemNotFoundException;
import br.com.fiap.querocomidahub.restaurant.domain.exception.RestaurantAccessDeniedException;
import br.com.fiap.querocomidahub.restaurant.domain.exception.RestaurantNotFoundException;
import br.com.fiap.querocomidahub.shared.infrastructure.security.UserIdentityResolver;
import br.com.fiap.querocomidahub.user.UserTestFixtures;
import br.com.fiap.querocomidahub.user.domain.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static br.com.fiap.querocomidahub.menuitem.MenuItemTestFixtures.PIZZA_MARGHERITA;
import static br.com.fiap.querocomidahub.menuitem.MenuItemTestFixtures.outputDTOList;
import static br.com.fiap.querocomidahub.menuitem.MenuItemTestFixtures.toOutputDTO;
import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MenuItemApiController.class)
@DisplayName("MenuItemApiController")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MenuItemApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MenuItemController menuItemController;

    @MockitoBean
    private UserIdentityResolver userIdentityResolver;

    private static final Long RESTAURANT_ID = 1L;
    private static final String BASE_URL = "/api/v1/restaurants/" + RESTAURANT_ID + "/menu-items";
    private static final String VALID_BODY = """
            {"name":"Pizza","description":"Delicious","price":29.90,"dineInOnly":false,"photoPath":"/img.jpg"}""";
    private static final String X_USER_ID = "X-User-Id";
    private static final String OWNER_ID = "2";
    private static final String NON_OWNER_ID = "3";

    @BeforeEach
    void setUp() {
        when(userIdentityResolver.resolve(any())).thenReturn(UserTestFixtures.MARIA_DONA);
    }

    @Nested
    @DisplayName("findAll(Long restaurantId)")
    class FindAllByRestaurantTest {

        @Test
        void returns_200_with_list_of_menu_items() throws Exception {
            when(menuItemController.findAll(RESTAURANT_ID)).thenReturn(outputDTOList());

            mockMvc.perform(get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(outputDTOList().size()))
                    .andExpect(jsonPath("$[0].name").value(PIZZA_MARGHERITA.getName()));

            verify(menuItemController).findAll(RESTAURANT_ID);
        }

        @Test
        void returns_200_with_empty_array_when_there_are_no_menu_items() throws Exception {
            when(menuItemController.findAll(RESTAURANT_ID)).thenReturn(List.of());

            mockMvc.perform(get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        void returns_404_when_restaurant_not_found() throws Exception {
            when(menuItemController.findAll(RESTAURANT_ID))
                    .thenThrow(new RestaurantNotFoundException(RESTAURANT_ID));

            mockMvc.perform(get(BASE_URL))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTest {

        @Test
        void returns_200_with_menu_item_when_found() throws Exception {
            MenuItemOutputDTO dto = toOutputDTO(PIZZA_MARGHERITA);

            when(menuItemController.findById(RESTAURANT_ID, PIZZA_MARGHERITA.getId())).thenReturn(dto);

            mockMvc.perform(get(BASE_URL + "/" + PIZZA_MARGHERITA.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(PIZZA_MARGHERITA.getId()))
                    .andExpect(jsonPath("$.name").value(PIZZA_MARGHERITA.getName()));

            verify(menuItemController).findById(RESTAURANT_ID, PIZZA_MARGHERITA.getId());
        }

        @Test
        void returns_404_when_menu_item_not_found() throws Exception {
            Long id = 99L;

            when(menuItemController.findById(RESTAURANT_ID, id)).thenThrow(new MenuItemNotFoundException(id));

            mockMvc.perform(get(BASE_URL + "/" + id))
                    .andExpect(status().isNotFound());
        }

        @Test
        void returns_400_when_id_is_not_a_number() throws Exception {
            mockMvc.perform(get(BASE_URL + "/abc"))
                    .andExpect(status().isBadRequest());

            verify(menuItemController, never()).findById(any(), any());
        }
    }

    @Nested
    @DisplayName("create()")
    class CreateTest {

        @Test
        void returns_201_with_location_header_when_created() throws Exception {
            Long id = PIZZA_MARGHERITA.getId();

            when(menuItemController.create(eq(RESTAURANT_ID), any(), eq(UserTestFixtures.MARIA_DONA.getId())))
                    .thenReturn(id);

            mockMvc.perform(post(BASE_URL)
                            .header(X_USER_ID, OWNER_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_BODY))
                    .andExpect(status().isCreated())
                    .andExpect(header().string(HttpHeaders.LOCATION, endsWith(BASE_URL + "/" + id)));
        }

        @Test
        void returns_401_when_X_User_Id_header_is_missing() throws Exception {
            when(userIdentityResolver.resolve(any()))
                    .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Header 'X-User-Id' is required"));

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_BODY))
                    .andExpect(status().isUnauthorized());

            verify(menuItemController, never()).create(any(), any(), any());
        }

        @Test
        void returns_403_when_caller_is_not_the_owner_of_restaurant() throws Exception {
            when(menuItemController.create(eq(RESTAURANT_ID), any(), any()))
                    .thenThrow(new RestaurantAccessDeniedException(3L, RESTAURANT_ID));

            mockMvc.perform(post(BASE_URL)
                            .header(X_USER_ID, NON_OWNER_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_BODY))
                    .andExpect(status().isForbidden());
        }

        @Test
        void returns_404_when_restaurant_not_found() throws Exception {
            when(menuItemController.create(eq(RESTAURANT_ID), any(), any()))
                    .thenThrow(new RestaurantNotFoundException(RESTAURANT_ID));

            mockMvc.perform(post(BASE_URL)
                            .header(X_USER_ID, OWNER_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_BODY))
                    .andExpect(status().isNotFound());
        }

        @Test
        void returns_404_when_caller_user_does_not_exist() throws Exception {
            when(userIdentityResolver.resolve(any())).thenThrow(new UserNotFoundException(99L));

            mockMvc.perform(post(BASE_URL)
                            .header(X_USER_ID, "99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_BODY))
                    .andExpect(status().isNotFound());
        }

        @Test
        void returns_400_when_name_is_blank() throws Exception {
            String body = """
                    {"name":"","description":"d","price":10.00,"dineInOnly":false}""";

            mockMvc.perform(post(BASE_URL)
                            .header(X_USER_ID, OWNER_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());

            verify(menuItemController, never()).create(any(), any(), any());
        }

        @Test
        void returns_400_when_price_is_below_minimum() throws Exception {
            String body = """
                    {"name":"n","description":"d","price":0.00,"dineInOnly":false}""";

            mockMvc.perform(post(BASE_URL)
                            .header(X_USER_ID, OWNER_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void returns_400_when_body_is_missing() throws Exception {
            mockMvc.perform(post(BASE_URL)
                            .header(X_USER_ID, OWNER_ID)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTest {

        @Test
        void returns_204_when_updated_successfully() throws Exception {
            Long id = PIZZA_MARGHERITA.getId();

            mockMvc.perform(put(BASE_URL + "/" + id)
                            .header(X_USER_ID, OWNER_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_BODY))
                    .andExpect(status().isNoContent());

            verify(menuItemController).update(eq(RESTAURANT_ID), eq(id), any(),
                    eq(UserTestFixtures.MARIA_DONA.getId()));
        }

        @Test
        void returns_404_when_menu_item_not_found() throws Exception {
            Long id = 99L;

            doThrow(new MenuItemNotFoundException(id))
                    .when(menuItemController).update(eq(RESTAURANT_ID), eq(id), any(), any());

            mockMvc.perform(put(BASE_URL + "/" + id)
                            .header(X_USER_ID, OWNER_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_BODY))
                    .andExpect(status().isNotFound());
        }

        @Test
        void returns_403_when_caller_is_not_the_owner() throws Exception {
            Long id = PIZZA_MARGHERITA.getId();

            doThrow(new RestaurantAccessDeniedException(3L, RESTAURANT_ID))
                    .when(menuItemController).update(eq(RESTAURANT_ID), eq(id), any(), any());

            mockMvc.perform(put(BASE_URL + "/" + id)
                            .header(X_USER_ID, NON_OWNER_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_BODY))
                    .andExpect(status().isForbidden());
        }

        @Test
        void returns_401_when_X_User_Id_header_is_missing() throws Exception {
            when(userIdentityResolver.resolve(any()))
                    .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Header 'X-User-Id' is required"));

            mockMvc.perform(put(BASE_URL + "/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_BODY))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void returns_400_when_body_is_missing() throws Exception {
            mockMvc.perform(put(BASE_URL + "/1")
                            .header(X_USER_ID, OWNER_ID)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void returns_400_when_id_is_not_a_number() throws Exception {
            mockMvc.perform(put(BASE_URL + "/abc")
                            .header(X_USER_ID, OWNER_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_BODY))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("delete()")
    class DeleteTest {

        @Test
        void returns_204_when_deleted_successfully() throws Exception {
            Long id = MenuItemTestFixtures.PIZZA_MARGHERITA.getId();

            mockMvc.perform(delete(BASE_URL + "/" + id).header(X_USER_ID, OWNER_ID))
                    .andExpect(status().isNoContent());

            verify(menuItemController).delete(RESTAURANT_ID, id, UserTestFixtures.MARIA_DONA.getId());
        }

        @Test
        void returns_404_when_menu_item_not_found() throws Exception {
            Long id = 99L;

            doThrow(new MenuItemNotFoundException(id))
                    .when(menuItemController).delete(eq(RESTAURANT_ID), eq(id), any());

            mockMvc.perform(delete(BASE_URL + "/" + id).header(X_USER_ID, OWNER_ID))
                    .andExpect(status().isNotFound());
        }

        @Test
        void returns_403_when_caller_is_not_the_owner() throws Exception {
            Long id = PIZZA_MARGHERITA.getId();

            doThrow(new RestaurantAccessDeniedException(3L, RESTAURANT_ID))
                    .when(menuItemController).delete(eq(RESTAURANT_ID), eq(id), any());

            mockMvc.perform(delete(BASE_URL + "/" + id).header(X_USER_ID, NON_OWNER_ID))
                    .andExpect(status().isForbidden());
        }

        @Test
        void returns_401_when_X_User_Id_header_is_missing() throws Exception {
            when(userIdentityResolver.resolve(any()))
                    .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Header 'X-User-Id' is required"));

            mockMvc.perform(delete(BASE_URL + "/1"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void returns_400_when_id_is_not_a_number() throws Exception {
            mockMvc.perform(delete(BASE_URL + "/abc").header(X_USER_ID, OWNER_ID))
                    .andExpect(status().isBadRequest());
        }
    }
}
