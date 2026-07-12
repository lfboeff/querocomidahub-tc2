package br.com.fiap.querocomidahub.restaurant.application.controller;

import br.com.fiap.querocomidahub.restaurant.application.dto.RestaurantInputDTO;
import br.com.fiap.querocomidahub.restaurant.application.dto.RestaurantOutputDTO;
import br.com.fiap.querocomidahub.restaurant.application.mapper.RestaurantDTOMapper;
import br.com.fiap.querocomidahub.restaurant.application.usecase.CreateRestaurantUseCase;
import br.com.fiap.querocomidahub.restaurant.application.usecase.DeleteRestaurantUseCase;
import br.com.fiap.querocomidahub.restaurant.application.usecase.GetRestaurantByIdUseCase;
import br.com.fiap.querocomidahub.restaurant.application.usecase.ListRestaurantsUseCase;
import br.com.fiap.querocomidahub.restaurant.application.usecase.UpdateRestaurantUseCase;

import java.util.List;

public final class RestaurantController {

    private final ListRestaurantsUseCase listRestaurantsUseCase;
    private final GetRestaurantByIdUseCase getRestaurantByIdUseCase;
    private final CreateRestaurantUseCase createRestaurantUseCase;
    private final UpdateRestaurantUseCase updateRestaurantUseCase;
    private final DeleteRestaurantUseCase deleteRestaurantUseCase;

    private RestaurantController(ListRestaurantsUseCase listRestaurantsUseCase,
                                 GetRestaurantByIdUseCase getRestaurantByIdUseCase,
                                 CreateRestaurantUseCase createRestaurantUseCase,
                                 UpdateRestaurantUseCase updateRestaurantUseCase,
                                 DeleteRestaurantUseCase deleteRestaurantUseCase) {
        this.listRestaurantsUseCase = listRestaurantsUseCase;
        this.getRestaurantByIdUseCase = getRestaurantByIdUseCase;
        this.createRestaurantUseCase = createRestaurantUseCase;
        this.updateRestaurantUseCase = updateRestaurantUseCase;
        this.deleteRestaurantUseCase = deleteRestaurantUseCase;
    }

    public static RestaurantController create(ListRestaurantsUseCase listRestaurantsUseCase,
                                              GetRestaurantByIdUseCase getRestaurantByIdUseCase,
                                              CreateRestaurantUseCase createRestaurantUseCase,
                                              UpdateRestaurantUseCase updateRestaurantUseCase,
                                              DeleteRestaurantUseCase deleteRestaurantUseCase) {
        return new RestaurantController(
                listRestaurantsUseCase,
                getRestaurantByIdUseCase,
                createRestaurantUseCase,
                updateRestaurantUseCase,
                deleteRestaurantUseCase
        );
    }

    public List<RestaurantOutputDTO> findAll() {
        return listRestaurantsUseCase.run()
                .stream()
                .map(RestaurantDTOMapper::toOutputDTO)
                .toList();
    }

    public RestaurantOutputDTO findById(Long id) {
        return getRestaurantByIdUseCase.run(id);
    }

    public Long create(RestaurantInputDTO inputDTO, Long callerId) {
        return createRestaurantUseCase.run(inputDTO, callerId);
    }

    public void update(Long id, RestaurantInputDTO inputDTO, Long callerId) {
        updateRestaurantUseCase.run(id, inputDTO, callerId);
    }

    public void delete(Long id, Long callerId) {
        deleteRestaurantUseCase.run(id, callerId);
    }
}
