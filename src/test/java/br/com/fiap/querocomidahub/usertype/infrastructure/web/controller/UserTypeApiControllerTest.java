package br.com.fiap.querocomidahub.usertype.infrastructure.web.controller;

import br.com.fiap.querocomidahub.usertype.application.controller.UserTypeController;
import br.com.fiap.querocomidahub.usertype.application.dto.UserTypeInputDTO;
import br.com.fiap.querocomidahub.usertype.application.dto.UserTypeOutputDTO;
import br.com.fiap.querocomidahub.usertype.domain.exception.UserTypeDuplicateNameException;
import br.com.fiap.querocomidahub.usertype.domain.exception.UserTypeInUseException;
import br.com.fiap.querocomidahub.usertype.domain.exception.UserTypeIsSystemException;
import br.com.fiap.querocomidahub.usertype.domain.exception.UserTypeNotFoundException;
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

import static br.com.fiap.querocomidahub.usertype.UserTypeTestFixtures.CLIENTE;
import static br.com.fiap.querocomidahub.usertype.UserTypeTestFixtures.MOTOBOY;
import static br.com.fiap.querocomidahub.usertype.UserTypeTestFixtures.outputDTOList;
import static br.com.fiap.querocomidahub.usertype.UserTypeTestFixtures.toOutputDTO;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserTypeApiController.class)
@DisplayName("UserTypeApiController")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UserTypeApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserTypeController userTypeController;

    private static final String BASE_URL = "/api/v1/user-types";
    private static final String VALID_BODY = "{\"name\":\"Motoboy\",\"canManageRestaurants\":false}";

    @Nested
    @DisplayName("findAll()")
    class FindAllTest {

        @Test
        void returns_200_with_list_of_user_types_when_they_exist() throws Exception {
            List<UserTypeOutputDTO> dto = outputDTOList();

            when(userTypeController.findAll()).thenReturn(dto);

            mockMvc.perform(get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(dto.size()))
                    .andExpect(jsonPath("$[0].name").value(dto.get(0).name()))
                    .andExpect(jsonPath("$[1].name").value(dto.get(1).name()))
                    .andExpect(jsonPath("$[2].name").value(dto.get(2).name()));

            verify(userTypeController).findAll();
        }

        @Test
        void returns_200_with_empty_array_when_there_is_no_user_type() throws Exception {
            when(userTypeController.findAll()).thenReturn(List.of());

            mockMvc.perform(get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(userTypeController).findAll();
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTest {

        @Test
        void returns_200_with_user_type_when_found() throws Exception {
            when(userTypeController.findById(MOTOBOY.getId())).thenReturn(toOutputDTO(MOTOBOY));

            mockMvc.perform(get(BASE_URL + "/" + MOTOBOY.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(MOTOBOY.getId()))
                    .andExpect(jsonPath("$.name").value(MOTOBOY.getName()))
                    .andExpect(jsonPath("$.isSystem").value(MOTOBOY.isSystem()))
                    .andExpect(jsonPath("$.canManageRestaurants").value(MOTOBOY.canManageRestaurants()))
                    .andExpect(jsonPath("$.createdAt").exists())
                    .andExpect(jsonPath("$.lastModifiedAt").exists());

            verify(userTypeController).findById(MOTOBOY.getId());
        }

        @Test
        void returns_404_when_user_type_not_found() throws Exception {
            Long id = MOTOBOY.getId();

            when(userTypeController.findById(id)).thenThrow(new UserTypeNotFoundException(id));

            mockMvc.perform(get(BASE_URL + "/" + id))
                    .andExpect(status().isNotFound());

            verify(userTypeController).findById(id);
        }

        @Test
        void returns_400_when_id_is_not_a_number() throws Exception {
            mockMvc.perform(get(BASE_URL + "/abc"))
                    .andExpect(status().isBadRequest());

            verify(userTypeController, never()).findById(any());
        }
    }

    @Nested
    @DisplayName("create()")
    class CreateTest {

        @Test
        void returns_201_with_location_header_when_created() throws Exception {
            Long id = MOTOBOY.getId();

            when(userTypeController.create(any())).thenReturn(id);

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_BODY))
                    .andExpect(status().isCreated())
                    .andExpect(header().string(HttpHeaders.LOCATION, endsWith(BASE_URL + "/" + id)));

            ArgumentCaptor<UserTypeInputDTO> captor = ArgumentCaptor.forClass(UserTypeInputDTO.class);
            verify(userTypeController).create(captor.capture());
            assertThat(captor.getValue())
                    .extracting(UserTypeInputDTO::name, UserTypeInputDTO::canManageRestaurants)
                    .containsExactly("Motoboy", false);
        }

        @Test
        void returns_400_when_name_is_blank() throws Exception {
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"\",\"canManageRestaurants\":false}"))
                    .andExpect(status().isBadRequest());

            verify(userTypeController, never()).create(any());
        }

        @Test
        void returns_400_when_name_is_missing() throws Exception {
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"canManageRestaurants\":false}"))
                    .andExpect(status().isBadRequest());

            verify(userTypeController, never()).create(any());
        }

        @Test
        void returns_400_when_name_exceeds_max_length() throws Exception {
            String longName = "A".repeat(51);

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"" + longName + "\",\"canManageRestaurants\":false}"))
                    .andExpect(status().isBadRequest());

            verify(userTypeController, never()).create(any());
        }

        @Test
        void returns_400_when_body_is_missing() throws Exception {
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(userTypeController, never()).create(any());
        }

        @Test
        void returns_409_when_name_already_exists() throws Exception {
            when(userTypeController.create(any())).thenThrow(new UserTypeDuplicateNameException(MOTOBOY.getName()));

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_BODY))
                    .andExpect(status().isConflict());

            verify(userTypeController).create(any());
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTest {

        @Test
        void returns_204_when_updated_successfully() throws Exception {
            Long id = MOTOBOY.getId();

            mockMvc.perform(put(BASE_URL + "/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_BODY))
                    .andExpect(status().isNoContent());

            verify(userTypeController).update(eq(id), any());
        }

        @Test
        void returns_404_when_user_type_not_found() throws Exception {
            Long id = MOTOBOY.getId();

            doThrow(new UserTypeNotFoundException(id))
                    .when(userTypeController).update(eq(id), any());

            mockMvc.perform(put(BASE_URL + "/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_BODY))
                    .andExpect(status().isNotFound());

            verify(userTypeController).update(eq(id), any());
        }

        @Test
        void returns_409_when_type_is_a_system_type() throws Exception {
            Long id = CLIENTE.getId();

            doThrow(new UserTypeIsSystemException(id))
                    .when(userTypeController).update(eq(id), any());

            mockMvc.perform(put(BASE_URL + "/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_BODY))
                    .andExpect(status().isConflict());

            verify(userTypeController).update(eq(id), any());
        }

        @Test
        void returns_409_when_name_already_exists() throws Exception {
            Long id = MOTOBOY.getId();

            doThrow(new UserTypeDuplicateNameException(MOTOBOY.getName()))
                    .when(userTypeController).update(eq(id), any());

            mockMvc.perform(put(BASE_URL + "/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_BODY))
                    .andExpect(status().isConflict());

            verify(userTypeController).update(eq(id), any());
        }

        @Test
        void returns_400_when_name_is_blank() throws Exception {
            mockMvc.perform(put(BASE_URL + "/" + MOTOBOY.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"\",\"canManageRestaurants\":false}"))
                    .andExpect(status().isBadRequest());

            verify(userTypeController, never()).update(any(), any());
        }

        @Test
        void returns_400_when_name_exceeds_max_length() throws Exception {
            String longName = "A".repeat(51);

            mockMvc.perform(put(BASE_URL + "/" + MOTOBOY.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"" + longName + "\",\"canManageRestaurants\":false}"))
                    .andExpect(status().isBadRequest());

            verify(userTypeController, never()).update(any(), any());
        }

        @Test
        void returns_400_when_body_is_missing() throws Exception {
            mockMvc.perform(put(BASE_URL + "/" + MOTOBOY.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(userTypeController, never()).update(any(), any());
        }

        @Test
        void returns_400_when_id_is_not_a_number() throws Exception {
            mockMvc.perform(put(BASE_URL + "/abc")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_BODY))
                    .andExpect(status().isBadRequest());

            verify(userTypeController, never()).update(any(), any());
        }
    }

    @Nested
    @DisplayName("delete()")
    class DeleteTest {

        @Test
        void returns_204_when_deleted_successfully() throws Exception {
            Long id = MOTOBOY.getId();

            mockMvc.perform(delete(BASE_URL + "/" + id))
                    .andExpect(status().isNoContent());

            verify(userTypeController).delete(id);
        }

        @Test
        void returns_404_when_user_type_not_found() throws Exception {
            Long id = MOTOBOY.getId();

            doThrow(new UserTypeNotFoundException(id))
                    .when(userTypeController).delete(id);

            mockMvc.perform(delete(BASE_URL + "/" + id))
                    .andExpect(status().isNotFound());

            verify(userTypeController).delete(id);
        }

        @Test
        void returns_409_when_type_is_in_use_by_users() throws Exception {
            Long id = MOTOBOY.getId();

            doThrow(new UserTypeInUseException(id))
                    .when(userTypeController).delete(id);

            mockMvc.perform(delete(BASE_URL + "/" + id))
                    .andExpect(status().isConflict());

            verify(userTypeController).delete(id);
        }

        @Test
        void returns_409_when_type_is_a_system_type() throws Exception {
            Long id = CLIENTE.getId();

            doThrow(new UserTypeIsSystemException(id))
                    .when(userTypeController).delete(id);

            mockMvc.perform(delete(BASE_URL + "/" + id))
                    .andExpect(status().isConflict());

            verify(userTypeController).delete(id);
        }

        @Test
        void returns_400_when_id_is_not_a_number() throws Exception {
            mockMvc.perform(delete(BASE_URL + "/abc"))
                    .andExpect(status().isBadRequest());

            verify(userTypeController, never()).delete(any());
        }
    }
}
