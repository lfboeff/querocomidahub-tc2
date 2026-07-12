package br.com.fiap.querocomidahub.restaurant.infrastructure.config;

import br.com.fiap.querocomidahub.menuitem.domain.gateway.IMenuItemGateway;
import br.com.fiap.querocomidahub.restaurant.application.controller.RestaurantController;
import br.com.fiap.querocomidahub.restaurant.application.usecase.CreateRestaurantUseCase;
import br.com.fiap.querocomidahub.restaurant.application.usecase.DeleteRestaurantUseCase;
import br.com.fiap.querocomidahub.restaurant.application.usecase.GetRestaurantByIdUseCase;
import br.com.fiap.querocomidahub.restaurant.application.usecase.ListRestaurantsUseCase;
import br.com.fiap.querocomidahub.restaurant.application.usecase.UpdateRestaurantUseCase;
import br.com.fiap.querocomidahub.restaurant.domain.gateway.IRestaurantGateway;
import br.com.fiap.querocomidahub.shared.infrastructure.gateway.LoggerGatewayFactory;
import br.com.fiap.querocomidahub.user.domain.gateway.IUserGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestaurantConfig {

    @Bean
    public ListRestaurantsUseCase listRestaurantsUseCase(IRestaurantGateway restaurantGateway) {
        return ListRestaurantsUseCase.create(restaurantGateway,
                LoggerGatewayFactory.forClass(ListRestaurantsUseCase.class));
    }

    @Bean
    public GetRestaurantByIdUseCase getRestaurantByIdUseCase(IRestaurantGateway restaurantGateway,
                                                             IMenuItemGateway menuItemGateway) {
        return GetRestaurantByIdUseCase.create(restaurantGateway, menuItemGateway,
                LoggerGatewayFactory.forClass(GetRestaurantByIdUseCase.class));
    }

    @Bean
    public CreateRestaurantUseCase createRestaurantUseCase(IRestaurantGateway restaurantGateway,
                                                           IUserGateway userGateway) {
        return CreateRestaurantUseCase.create(restaurantGateway, userGateway,
                LoggerGatewayFactory.forClass(CreateRestaurantUseCase.class));
    }

    @Bean
    public UpdateRestaurantUseCase updateRestaurantUseCase(IRestaurantGateway restaurantGateway,
                                                           IUserGateway userGateway) {
        return UpdateRestaurantUseCase.create(restaurantGateway, userGateway,
                LoggerGatewayFactory.forClass(UpdateRestaurantUseCase.class));
    }

    @Bean
    public DeleteRestaurantUseCase deleteRestaurantUseCase(IRestaurantGateway restaurantGateway,
                                                           IUserGateway userGateway) {
        return DeleteRestaurantUseCase.create(restaurantGateway, userGateway,
                LoggerGatewayFactory.forClass(DeleteRestaurantUseCase.class));
    }

    @Bean
    public RestaurantController restaurantController(ListRestaurantsUseCase listRestaurantsUseCase,
                                                     GetRestaurantByIdUseCase getRestaurantByIdUseCase,
                                                     CreateRestaurantUseCase createRestaurantUseCase,
                                                     UpdateRestaurantUseCase updateRestaurantUseCase,
                                                     DeleteRestaurantUseCase deleteRestaurantUseCase) {
        return RestaurantController.create(
                listRestaurantsUseCase,
                getRestaurantByIdUseCase,
                createRestaurantUseCase,
                updateRestaurantUseCase,
                deleteRestaurantUseCase
        );
    }
}
