package br.com.fiap.querocomidahub.menuitem.infrastructure.config;

import br.com.fiap.querocomidahub.menuitem.application.controller.MenuItemController;
import br.com.fiap.querocomidahub.menuitem.application.usecase.CreateMenuItemUseCase;
import br.com.fiap.querocomidahub.menuitem.application.usecase.DeleteMenuItemUseCase;
import br.com.fiap.querocomidahub.menuitem.application.usecase.GetMenuItemByIdUseCase;
import br.com.fiap.querocomidahub.menuitem.application.usecase.ListMenuItemsUseCase;
import br.com.fiap.querocomidahub.menuitem.application.usecase.UpdateMenuItemUseCase;
import br.com.fiap.querocomidahub.menuitem.domain.gateway.IMenuItemGateway;
import br.com.fiap.querocomidahub.restaurant.domain.gateway.IRestaurantGateway;
import br.com.fiap.querocomidahub.shared.infrastructure.gateway.LoggerGatewayFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MenuItemConfig {

    @Bean
    public ListMenuItemsUseCase listMenuItemsUseCase(IMenuItemGateway menuItemGateway,
                                                     IRestaurantGateway restaurantGateway) {
        return ListMenuItemsUseCase.create(menuItemGateway, restaurantGateway,
                LoggerGatewayFactory.forClass(ListMenuItemsUseCase.class));
    }

    @Bean
    public GetMenuItemByIdUseCase getMenuItemByIdUseCase(IMenuItemGateway menuItemGateway,
                                                         IRestaurantGateway restaurantGateway) {
        return GetMenuItemByIdUseCase.create(menuItemGateway, restaurantGateway,
                LoggerGatewayFactory.forClass(GetMenuItemByIdUseCase.class));
    }

    @Bean
    public CreateMenuItemUseCase createMenuItemUseCase(IMenuItemGateway menuItemGateway,
                                                       IRestaurantGateway restaurantGateway) {
        return CreateMenuItemUseCase.create(menuItemGateway, restaurantGateway,
                LoggerGatewayFactory.forClass(CreateMenuItemUseCase.class));
    }

    @Bean
    public UpdateMenuItemUseCase updateMenuItemUseCase(IMenuItemGateway menuItemGateway,
                                                       IRestaurantGateway restaurantGateway) {
        return UpdateMenuItemUseCase.create(menuItemGateway, restaurantGateway,
                LoggerGatewayFactory.forClass(UpdateMenuItemUseCase.class));
    }

    @Bean
    public DeleteMenuItemUseCase deleteMenuItemUseCase(IMenuItemGateway menuItemGateway,
                                                       IRestaurantGateway restaurantGateway) {
        return DeleteMenuItemUseCase.create(menuItemGateway, restaurantGateway,
                LoggerGatewayFactory.forClass(DeleteMenuItemUseCase.class));
    }

    @Bean
    public MenuItemController menuItemController(ListMenuItemsUseCase listMenuItemsUseCase,
                                                 GetMenuItemByIdUseCase getMenuItemByIdUseCase,
                                                 CreateMenuItemUseCase createMenuItemUseCase,
                                                 UpdateMenuItemUseCase updateMenuItemUseCase,
                                                 DeleteMenuItemUseCase deleteMenuItemUseCase) {
        return MenuItemController.create(
                listMenuItemsUseCase,
                getMenuItemByIdUseCase,
                createMenuItemUseCase,
                updateMenuItemUseCase,
                deleteMenuItemUseCase
        );
    }
}
