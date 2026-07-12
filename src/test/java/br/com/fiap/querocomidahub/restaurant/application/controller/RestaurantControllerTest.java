package br.com.fiap.querocomidahub.restaurant.application.controller;

import br.com.fiap.querocomidahub.restaurant.application.dto.RestaurantInputDTO;
import br.com.fiap.querocomidahub.restaurant.application.dto.RestaurantOutputDTO;
import br.com.fiap.querocomidahub.restaurant.application.usecase.CreateRestaurantUseCase;
import br.com.fiap.querocomidahub.restaurant.application.usecase.DeleteRestaurantUseCase;
import br.com.fiap.querocomidahub.restaurant.application.usecase.GetRestaurantByIdUseCase;
import br.com.fiap.querocomidahub.restaurant.application.usecase.ListRestaurantsUseCase;
import br.com.fiap.querocomidahub.restaurant.application.usecase.UpdateRestaurantUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static br.com.fiap.querocomidahub.restaurant.RestaurantTestFixtures.PIZZARIA_BELLA_NAPOLI;
import static br.com.fiap.querocomidahub.restaurant.RestaurantTestFixtures.restaurantList;
import static br.com.fiap.querocomidahub.restaurant.RestaurantTestFixtures.toOutputDTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RestaurantController")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RestaurantControllerTest {

    @Mock
    private CreateRestaurantUseCase createRestaurantUseCase;

    @Mock
    private GetRestaurantByIdUseCase getRestaurantByIdUseCase;

    @Mock
    private ListRestaurantsUseCase listRestaurantsUseCase;

    @Mock
    private UpdateRestaurantUseCase updateRestaurantUseCase;

    @Mock
    private DeleteRestaurantUseCase deleteRestaurantUseCase;

    private RestaurantController restaurantController;

    @BeforeEach
    void setUp() {
        restaurantController = RestaurantController.create(
                listRestaurantsUseCase,
                getRestaurantByIdUseCase,
                createRestaurantUseCase,
                updateRestaurantUseCase,
                deleteRestaurantUseCase);
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTest {

        @Test
        void delegates_to_list_use_case_and_maps_each_domain_to_output_dto() {
            when(listRestaurantsUseCase.run()).thenReturn(restaurantList());

            List<RestaurantOutputDTO> result = restaurantController.findAll();

            assertThat(result).hasSize(restaurantList().size());

            verify(listRestaurantsUseCase).run();
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTest {

        @Test
        void delegates_to_get_by_id_use_case() {
            when(getRestaurantByIdUseCase.run(PIZZARIA_BELLA_NAPOLI.getId())).thenReturn(toOutputDTO(PIZZARIA_BELLA_NAPOLI));

            RestaurantOutputDTO result = restaurantController.findById(PIZZARIA_BELLA_NAPOLI.getId());

            assertThat(result.id()).isEqualTo(PIZZARIA_BELLA_NAPOLI.getId());

            verify(getRestaurantByIdUseCase).run(PIZZARIA_BELLA_NAPOLI.getId());
        }
    }

    @Nested
    @DisplayName("create()")
    class CreateTest {

        @Test
        void delegates_to_create_use_case_with_input_and_caller_id() {
            RestaurantInputDTO dto = new RestaurantInputDTO("N", "A", "K", "H");
            Long callerId = 2L;
            when(createRestaurantUseCase.run(dto, callerId)).thenReturn(99L);

            Long result = restaurantController.create(dto, callerId);

            assertThat(result).isEqualTo(99L);

            verify(createRestaurantUseCase).run(dto, callerId);
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTest {

        @Test
        void delegates_to_update_use_case_with_id_input_and_caller_id() {
            RestaurantInputDTO dto = new RestaurantInputDTO("N", "A", "K", "H");
            restaurantController.update(1L, dto, 2L);

            verify(updateRestaurantUseCase).run(1L, dto, 2L);
        }
    }

    @Nested
    @DisplayName("delete()")
    class DeleteTest {

        @Test
        void delegates_to_delete_use_case_with_id_and_caller_id() {
            restaurantController.delete(1L, 2L);

            verify(deleteRestaurantUseCase).run(1L, 2L);
        }
    }
}
