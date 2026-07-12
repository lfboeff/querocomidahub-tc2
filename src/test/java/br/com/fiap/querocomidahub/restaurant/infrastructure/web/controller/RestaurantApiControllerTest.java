package br.com.fiap.querocomidahub.restaurant.infrastructure.web.controller;

import br.com.fiap.querocomidahub.restaurant.application.controller.RestaurantController;
import br.com.fiap.querocomidahub.restaurant.application.dto.RestaurantOutputDTO;
import br.com.fiap.querocomidahub.restaurant.domain.exception.RestaurantAccessDeniedException;
import br.com.fiap.querocomidahub.restaurant.domain.exception.RestaurantManagementNotAllowedException;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Stream;

import static br.com.fiap.querocomidahub.restaurant.RestaurantTestFixtures.PIZZARIA_BELLA_NAPOLI;
import static br.com.fiap.querocomidahub.restaurant.RestaurantTestFixtures.outputDTOList;
import static br.com.fiap.querocomidahub.restaurant.RestaurantTestFixtures.toOutputDTO;
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

@WebMvcTest(RestaurantApiController.class)
@DisplayName("RestaurantApiController")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RestaurantApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RestaurantController restaurantController;

    @MockitoBean
    private UserIdentityResolver userIdentityResolver;

    private static final String BASE_URL = "/api/v1/restaurants";
    private static final String VALID_BODY = """
            {"name":"Pizzaria","address":"Av. X","kitchenType":"Italiana","openingHours":"Seg-Sex 11h-22h"}""";
    private static final String X_USER_ID = "X-User-Id";
    private static final String OWNER_ID = "2";
    private static final String NON_OWNER_ID = "3";

    @BeforeEach
    void setUp() {
        when(userIdentityResolver.resolve(any())).thenReturn(UserTestFixtures.MARIA_DONA);
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTest {

        @Test
        void returns_200_with_list_of_restaurants() throws Exception {
            when(restaurantController.findAll()).thenReturn(outputDTOList());

            mockMvc.perform(get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(outputDTOList().size()))
                    .andExpect(jsonPath("$[0].name").value(PIZZARIA_BELLA_NAPOLI.getName()));

            verify(restaurantController).findAll();
        }

        @Test
        void returns_200_with_empty_array_when_there_are_no_restaurants() throws Exception {
            when(restaurantController.findAll()).thenReturn(List.of());

            mockMvc.perform(get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTest {

        @Test
        void returns_200_with_restaurant_when_found() throws Exception {
            RestaurantOutputDTO dto = toOutputDTO(PIZZARIA_BELLA_NAPOLI);
            when(restaurantController.findById(PIZZARIA_BELLA_NAPOLI.getId())).thenReturn(dto);

            mockMvc.perform(get(BASE_URL + "/" + PIZZARIA_BELLA_NAPOLI.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(PIZZARIA_BELLA_NAPOLI.getId()))
                    .andExpect(jsonPath("$.name").value(PIZZARIA_BELLA_NAPOLI.getName()))
                    .andExpect(jsonPath("$.ownerId").value(PIZZARIA_BELLA_NAPOLI.getOwnerId()));

            verify(restaurantController).findById(PIZZARIA_BELLA_NAPOLI.getId());
        }

        @Test
        void returns_404_when_restaurant_not_found() throws Exception {
            Long id = 99L;
            when(restaurantController.findById(id)).thenThrow(new RestaurantNotFoundException(id));

            mockMvc.perform(get(BASE_URL + "/" + id))
                    .andExpect(status().isNotFound());
        }

        @Test
        void returns_400_when_id_is_not_a_number() throws Exception {
            mockMvc.perform(get(BASE_URL + "/abc"))
                    .andExpect(status().isBadRequest());

            verify(restaurantController, never()).findById(any());
        }
    }

    @Nested
    @DisplayName("create()")
    class CreateTest {

        @Test
        void returns_201_with_location_header_when_created() throws Exception {
            Long id = PIZZARIA_BELLA_NAPOLI.getId();
            when(restaurantController.create(any(), eq(UserTestFixtures.MARIA_DONA.getId()))).thenReturn(id);

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

            verify(restaurantController, never()).create(any(), any());
        }

        @Test
        void returns_403_when_caller_cannot_manage_restaurants() throws Exception {
            when(restaurantController.create(any(), any()))
                    .thenThrow(new RestaurantManagementNotAllowedException(1L));

            mockMvc.perform(post(BASE_URL)
                            .header(X_USER_ID, "1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_BODY))
                    .andExpect(status().isForbidden());
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

        @ParameterizedTest(name = "[{index}] {0}")
        @MethodSource("provideBlankFieldScenarios")
        void returns_400_when_field_is_blank(String fieldName, String body) throws Exception {
            mockMvc.perform(post(BASE_URL)
                            .header(X_USER_ID, OWNER_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());

            verify(restaurantController, never()).create(any(), any());
        }

        private static Stream<Arguments> provideBlankFieldScenarios() {
            return Stream.of(
                    Arguments.of("name", """
                            {"name":"","address":"A","kitchenType":"K","openingHours":"H"}"""),
                    Arguments.of("address", """
                            {"name":"N","address":"","kitchenType":"K","openingHours":"H"}"""),
                    Arguments.of("kitchenType", """
                            {"name":"N","address":"A","kitchenType":"","openingHours":"H"}"""),
                    Arguments.of("openingHours", """
                            {"name":"N","address":"A","kitchenType":"K","openingHours":""}""")
            );
        }

        @Test
        void returns_400_when_body_is_missing() throws Exception {
            mockMvc.perform(post(BASE_URL)
                            .header(X_USER_ID, OWNER_ID)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(restaurantController, never()).create(any(), any());
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTest {

        @Test
        void returns_204_when_updated_successfully() throws Exception {
            Long id = PIZZARIA_BELLA_NAPOLI.getId();

            mockMvc.perform(put(BASE_URL + "/" + id)
                            .header(X_USER_ID, OWNER_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_BODY))
                    .andExpect(status().isNoContent());

            verify(restaurantController).update(eq(id), any(), eq(UserTestFixtures.MARIA_DONA.getId()));
        }

        @Test
        void returns_404_when_restaurant_not_found() throws Exception {
            Long id = 99L;
            doThrow(new RestaurantNotFoundException(id))
                    .when(restaurantController).update(eq(id), any(), any());

            mockMvc.perform(put(BASE_URL + "/" + id)
                            .header(X_USER_ID, OWNER_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_BODY))
                    .andExpect(status().isNotFound());
        }

        @Test
        void returns_403_when_caller_is_not_the_owner() throws Exception {
            Long id = PIZZARIA_BELLA_NAPOLI.getId();
            doThrow(new RestaurantAccessDeniedException(3L, id))
                    .when(restaurantController).update(eq(id), any(), any());

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

            mockMvc.perform(put(BASE_URL + "/" + PIZZARIA_BELLA_NAPOLI.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_BODY))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void returns_400_when_body_is_missing() throws Exception {
            mockMvc.perform(put(BASE_URL + "/" + PIZZARIA_BELLA_NAPOLI.getId())
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
            Long id = PIZZARIA_BELLA_NAPOLI.getId();

            mockMvc.perform(delete(BASE_URL + "/" + id).header(X_USER_ID, OWNER_ID))
                    .andExpect(status().isNoContent());

            verify(restaurantController).delete(id, UserTestFixtures.MARIA_DONA.getId());
        }

        @Test
        void returns_404_when_restaurant_not_found() throws Exception {
            Long id = 99L;
            doThrow(new RestaurantNotFoundException(id))
                    .when(restaurantController).delete(eq(id), any());

            mockMvc.perform(delete(BASE_URL + "/" + id).header(X_USER_ID, OWNER_ID))
                    .andExpect(status().isNotFound());
        }

        @Test
        void returns_403_when_caller_is_not_the_owner() throws Exception {
            Long id = PIZZARIA_BELLA_NAPOLI.getId();
            doThrow(new RestaurantAccessDeniedException(3L, id))
                    .when(restaurantController).delete(eq(id), any());

            mockMvc.perform(delete(BASE_URL + "/" + id).header(X_USER_ID, NON_OWNER_ID))
                    .andExpect(status().isForbidden());
        }

        @Test
        void returns_401_when_X_User_Id_header_is_missing() throws Exception {
            when(userIdentityResolver.resolve(any()))
                    .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Header 'X-User-Id' is required"));

            mockMvc.perform(delete(BASE_URL + "/" + PIZZARIA_BELLA_NAPOLI.getId()))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void returns_400_when_id_is_not_a_number() throws Exception {
            mockMvc.perform(delete(BASE_URL + "/abc").header(X_USER_ID, OWNER_ID))
                    .andExpect(status().isBadRequest());
        }
    }
}
