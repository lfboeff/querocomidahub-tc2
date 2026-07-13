package br.com.fiap.querocomidahub.menuitem.infrastructure.web.controller;

import br.com.fiap.querocomidahub.menuitem.application.controller.MenuItemController;
import br.com.fiap.querocomidahub.menuitem.application.dto.MenuItemInputDTO;
import br.com.fiap.querocomidahub.menuitem.application.dto.MenuItemOutputDTO;
import br.com.fiap.querocomidahub.menuitem.infrastructure.web.json.MenuItemRequestJson;
import br.com.fiap.querocomidahub.menuitem.infrastructure.web.json.MenuItemResponseJson;
import br.com.fiap.querocomidahub.menuitem.infrastructure.web.mapper.MenuItemJSONMapper;
import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;
import br.com.fiap.querocomidahub.shared.infrastructure.gateway.LoggerGatewayFactory;
import br.com.fiap.querocomidahub.shared.infrastructure.security.UserIdentityResolver;
import br.com.fiap.querocomidahub.user.domain.model.UserBase;
import jakarta.validation.Valid;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static br.com.fiap.querocomidahub.shared.infrastructure.security.UserIdentityResolver.HEADER_USER_ID;

@RestController
public class MenuItemApiController implements IMenuItemSwaggerDoc {

    private final MenuItemController menuItemController;
    private final UserIdentityResolver userIdentityResolver;
    private final ILoggerGateway logger;

    public MenuItemApiController(MenuItemController menuItemController,
                                 UserIdentityResolver userIdentityResolver) {
        this.menuItemController = menuItemController;
        this.userIdentityResolver = userIdentityResolver;
        this.logger = LoggerGatewayFactory.forClass(MenuItemApiController.class);
    }

    @Override
    @GetMapping("/api/v1/restaurants/{restaurantId}/menu-items")
    public ResponseEntity<List<MenuItemResponseJson>> findAll(@PathVariable Long restaurantId) {
        logRequestReceived(HttpMethod.GET);

        List<MenuItemResponseJson> response = menuItemController.findAll(restaurantId)
                .stream()
                .map(MenuItemJSONMapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/api/v1/restaurants/{restaurantId}/menu-items/{id}")
    public ResponseEntity<MenuItemResponseJson> findById(@PathVariable Long restaurantId,
                                                         @PathVariable Long id) {
        logRequestReceived(HttpMethod.GET);

        MenuItemOutputDTO outputDTO = menuItemController.findById(restaurantId, id);
        MenuItemResponseJson response = MenuItemJSONMapper.toResponse(outputDTO);

        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/api/v1/restaurants/{restaurantId}/menu-items")
    public ResponseEntity<Void> create(@PathVariable Long restaurantId,
                                       @RequestBody @Valid MenuItemRequestJson request,
                                       @RequestHeader(value = HEADER_USER_ID, required = false) String userId) {
        logRequestReceived(HttpMethod.POST, request);

        UserBase caller = userIdentityResolver.resolve(userId);

        MenuItemInputDTO inputDTO = MenuItemJSONMapper.toInputDTO(request);
        Long menuItemId = menuItemController.create(restaurantId, inputDTO, caller.getId());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(menuItemId)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @Override
    @PutMapping("/api/v1/restaurants/{restaurantId}/menu-items/{id}")
    public ResponseEntity<Void> update(@PathVariable Long restaurantId,
                                       @PathVariable Long id,
                                       @RequestBody @Valid MenuItemRequestJson request,
                                       @RequestHeader(value = HEADER_USER_ID, required = false) String userId) {
        logRequestReceived(HttpMethod.PUT, request);

        UserBase caller = userIdentityResolver.resolve(userId);

        MenuItemInputDTO inputDTO = MenuItemJSONMapper.toInputDTO(request);
        menuItemController.update(restaurantId, id, inputDTO, caller.getId());

        return ResponseEntity.noContent().build();
    }

    @Override
    @DeleteMapping("/api/v1/restaurants/{restaurantId}/menu-items/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long restaurantId,
                                       @PathVariable Long id,
                                       @RequestHeader(value = HEADER_USER_ID, required = false) String userId) {
        logRequestReceived(HttpMethod.DELETE);

        UserBase caller = userIdentityResolver.resolve(userId);

        menuItemController.delete(restaurantId, id, caller.getId());

        return ResponseEntity.noContent().build();
    }

    private void logRequestReceived(HttpMethod httpMethod) {
        String uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString();
        logger.info("{} request received at '{}'", httpMethod, uri);
    }

    private void logRequestReceived(HttpMethod httpMethod, Object body) {
        String uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString();
        logger.info("{} request received at '{}' with body='{}'", httpMethod, uri, body);
    }
}
