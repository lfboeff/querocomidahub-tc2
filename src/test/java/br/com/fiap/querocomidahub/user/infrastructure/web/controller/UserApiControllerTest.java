package br.com.fiap.querocomidahub.user.infrastructure.web.controller;

import br.com.fiap.querocomidahub.user.application.controller.UserController;
import br.com.fiap.querocomidahub.user.application.dto.CreateUserInputDTO;
import br.com.fiap.querocomidahub.user.domain.exception.InvalidUserTypeException;
import br.com.fiap.querocomidahub.user.domain.exception.UserDuplicateEmailException;
import br.com.fiap.querocomidahub.user.domain.exception.UserInUseInRestaurantsException;
import br.com.fiap.querocomidahub.user.domain.exception.UserNotFoundException;
import br.com.fiap.querocomidahub.user.domain.exception.UserOwnsRestaurantsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static br.com.fiap.querocomidahub.user.UserTestFixtures.JOAO_CLIENTE;
import static br.com.fiap.querocomidahub.user.UserTestFixtures.MARIA_DONA;
import static br.com.fiap.querocomidahub.user.UserTestFixtures.outputDTOList;
import static br.com.fiap.querocomidahub.user.UserTestFixtures.toOutputDTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserApiController.class)
@DisplayName("UserApiController")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UserApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserController userController;

    private static final String BASE_URL = "/api/v1/users";
    private static final String VALID_CREATE_BODY = """
            {"name":"João da Silva","email":"joao.silva@email.com","address":"Rua X","userTypeId":2}""";
    private static final String VALID_UPDATE_BODY = """
            {"name":"João da Silva","email":"joao.silva@email.com","address":"Rua X"}""";
    private static final String VALID_ASSIGN_BODY = """
            {"userTypeId":1}""";

    @Nested
    @DisplayName("findAll()")
    class FindAllTest {

        @Test
        void returns_200_with_list_of_users_when_they_exist() throws Exception {
            when(userController.findAll()).thenReturn(outputDTOList());

            mockMvc.perform(get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(outputDTOList().size()))
                    .andExpect(jsonPath("$[0].name").value(JOAO_CLIENTE.getName()));

            verify(userController).findAll();
        }

        @Test
        void returns_200_with_empty_array_when_there_are_no_users() throws Exception {
            when(userController.findAll()).thenReturn(List.of());

            mockMvc.perform(get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(userController).findAll();
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTest {

        @Test
        void returns_200_with_user_when_found() throws Exception {
            when(userController.findById(JOAO_CLIENTE.getId())).thenReturn(toOutputDTO(JOAO_CLIENTE));

            mockMvc.perform(get(BASE_URL + "/" + JOAO_CLIENTE.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(JOAO_CLIENTE.getId()))
                    .andExpect(jsonPath("$.name").value(JOAO_CLIENTE.getName()))
                    .andExpect(jsonPath("$.email").value(JOAO_CLIENTE.getEmail()))
                    .andExpect(jsonPath("$.userType.id").value(JOAO_CLIENTE.getUserType().getId()));

            verify(userController).findById(JOAO_CLIENTE.getId());
        }

        @Test
        void returns_404_when_user_not_found() throws Exception {
            Long id = 99L;

            when(userController.findById(id)).thenThrow(new UserNotFoundException(id));

            mockMvc.perform(get(BASE_URL + "/" + id))
                    .andExpect(status().isNotFound());

            verify(userController).findById(id);
        }

        @Test
        void returns_400_when_id_is_not_a_number() throws Exception {
            mockMvc.perform(get(BASE_URL + "/abc"))
                    .andExpect(status().isBadRequest());

            verify(userController, never()).findById(any());
        }
    }

    @Nested
    @DisplayName("create()")
    class CreateTest {

        @Test
        void returns_201_with_location_header_when_created() throws Exception {
            Long id = JOAO_CLIENTE.getId();
            when(userController.create(any())).thenReturn(id);

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_CREATE_BODY))
                    .andExpect(status().isCreated())
                    .andExpect(header().string(HttpHeaders.LOCATION, endsWith(BASE_URL + "/" + id)));

            ArgumentCaptor<CreateUserInputDTO> captor = ArgumentCaptor.forClass(CreateUserInputDTO.class);

            verify(userController).create(captor.capture());

            assertThat(captor.getValue())
                    .extracting(CreateUserInputDTO::name, CreateUserInputDTO::email,
                            CreateUserInputDTO::address, CreateUserInputDTO::userTypeId)
                    .containsExactly("João da Silva", "joao.silva@email.com", "Rua X", 2L);
        }

        @Test
        void returns_400_when_name_is_blank() throws Exception {
            String body = """
                    {"name":"","email":"a@b.com","address":"Addr","userTypeId":1}""";

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());

            verify(userController, never()).create(any());
        }

        @Test
        void returns_400_when_email_is_invalid_format() throws Exception {
            String body = """
                    {"name":"Name","email":"not-an-email","address":"Addr","userTypeId":1}""";

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());

            verify(userController, never()).create(any());
        }

        @Test
        void returns_400_when_userTypeId_is_missing() throws Exception {
            String body = """
                    {"name":"Name","email":"a@b.com","address":"Addr"}""";

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());

            verify(userController, never()).create(any());
        }

        @Test
        void returns_400_when_body_is_missing() throws Exception {
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(userController, never()).create(any());
        }

        @Test
        void returns_400_when_userTypeId_does_not_exist() throws Exception {
            when(userController.create(any())).thenThrow(new InvalidUserTypeException(99L));

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_CREATE_BODY))
                    .andExpect(status().isBadRequest());

            verify(userController).create(any());
        }

        @Test
        void returns_409_when_email_already_exists() throws Exception {
            when(userController.create(any())).thenThrow(new UserDuplicateEmailException("joao.silva@email.com"));

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_CREATE_BODY))
                    .andExpect(status().isConflict());

            verify(userController).create(any());
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTest {

        @Test
        void returns_204_when_updated_successfully() throws Exception {
            Long id = JOAO_CLIENTE.getId();

            mockMvc.perform(put(BASE_URL + "/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_UPDATE_BODY))
                    .andExpect(status().isNoContent());

            verify(userController).update(eq(id), any());
        }

        @Test
        void returns_404_when_user_not_found() throws Exception {
            Long id = 99L;
            doThrow(new UserNotFoundException(id))
                    .when(userController).update(eq(id), any());

            mockMvc.perform(put(BASE_URL + "/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_UPDATE_BODY))
                    .andExpect(status().isNotFound());

            verify(userController).update(eq(id), any());
        }

        @Test
        void returns_409_when_email_already_used_by_another_user() throws Exception {
            Long id = JOAO_CLIENTE.getId();
            doThrow(new UserDuplicateEmailException("taken@email.com"))
                    .when(userController).update(eq(id), any());

            mockMvc.perform(put(BASE_URL + "/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_UPDATE_BODY))
                    .andExpect(status().isConflict());

            verify(userController).update(eq(id), any());
        }

        @Test
        void returns_400_when_email_is_invalid_format() throws Exception {
            String body = """
                    {"name":"Name","email":"not-an-email","address":"Addr"}""";

            mockMvc.perform(put(BASE_URL + "/" + JOAO_CLIENTE.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());

            verify(userController, never()).update(any(), any());
        }

        @Test
        void returns_400_when_body_is_missing() throws Exception {
            mockMvc.perform(put(BASE_URL + "/" + JOAO_CLIENTE.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(userController, never()).update(any(), any());
        }

        @Test
        void returns_400_when_id_is_not_a_number() throws Exception {
            mockMvc.perform(put(BASE_URL + "/abc")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_UPDATE_BODY))
                    .andExpect(status().isBadRequest());

            verify(userController, never()).update(any(), any());
        }
    }

    @Nested
    @DisplayName("delete()")
    class DeleteTest {

        @Test
        void returns_204_when_deleted_successfully() throws Exception {
            Long id = JOAO_CLIENTE.getId();

            mockMvc.perform(delete(BASE_URL + "/" + id))
                    .andExpect(status().isNoContent());

            verify(userController).delete(id);
        }

        @Test
        void returns_404_when_user_not_found() throws Exception {
            Long id = 99L;
            doThrow(new UserNotFoundException(id))
                    .when(userController).delete(id);

            mockMvc.perform(delete(BASE_URL + "/" + id))
                    .andExpect(status().isNotFound());

            verify(userController).delete(id);
        }

        @Test
        void returns_409_when_user_owns_restaurants() throws Exception {
            Long id = MARIA_DONA.getId();
            doThrow(new UserInUseInRestaurantsException(id))
                    .when(userController).delete(id);

            mockMvc.perform(delete(BASE_URL + "/" + id))
                    .andExpect(status().isConflict());

            verify(userController).delete(id);
        }

        @Test
        void returns_400_when_id_is_not_a_number() throws Exception {
            mockMvc.perform(delete(BASE_URL + "/abc"))
                    .andExpect(status().isBadRequest());

            verify(userController, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("assignUserType()")
    class AssignUserTypeTest {

        @Test
        void returns_204_when_user_type_assigned_successfully() throws Exception {
            Long id = JOAO_CLIENTE.getId();

            mockMvc.perform(patch(BASE_URL + "/" + id + "/user-type")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_ASSIGN_BODY))
                    .andExpect(status().isNoContent());

            verify(userController).assignUserType(eq(id), any());
        }

        @Test
        void returns_404_when_user_not_found() throws Exception {
            Long id = 99L;
            doThrow(new UserNotFoundException(id))
                    .when(userController).assignUserType(eq(id), any());

            mockMvc.perform(patch(BASE_URL + "/" + id + "/user-type")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_ASSIGN_BODY))
                    .andExpect(status().isNotFound());

            verify(userController).assignUserType(eq(id), any());
        }

        @Test
        void returns_400_when_userType_does_not_exist() throws Exception {
            Long id = JOAO_CLIENTE.getId();
            doThrow(new InvalidUserTypeException(99L))
                    .when(userController).assignUserType(eq(id), any());

            mockMvc.perform(patch(BASE_URL + "/" + id + "/user-type")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_ASSIGN_BODY))
                    .andExpect(status().isBadRequest());

            verify(userController).assignUserType(eq(id), any());
        }

        @Test
        void returns_409_when_owner_is_demoted_while_still_owning_restaurants() throws Exception {
            Long id = MARIA_DONA.getId();
            doThrow(new UserOwnsRestaurantsException(id))
                    .when(userController).assignUserType(eq(id), any());

            mockMvc.perform(patch(BASE_URL + "/" + id + "/user-type")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_ASSIGN_BODY))
                    .andExpect(status().isConflict());

            verify(userController).assignUserType(eq(id), any());
        }

        @Test
        void returns_400_when_userTypeId_is_missing() throws Exception {
            String body = "{}";

            mockMvc.perform(patch(BASE_URL + "/" + JOAO_CLIENTE.getId() + "/user-type")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());

            verify(userController, never()).assignUserType(any(), any());
        }

        @Test
        void returns_400_when_id_is_not_a_number() throws Exception {
            mockMvc.perform(patch(BASE_URL + "/abc/user-type")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_ASSIGN_BODY))
                    .andExpect(status().isBadRequest());

            verify(userController, never()).assignUserType(any(), any());
        }
    }
}
