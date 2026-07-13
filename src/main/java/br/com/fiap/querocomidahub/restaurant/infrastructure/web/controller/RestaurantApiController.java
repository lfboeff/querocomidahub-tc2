package br.com.fiap.querocomidahub.restaurant.infrastructure.web.controller;

import br.com.fiap.querocomidahub.restaurant.application.controller.RestaurantController;
import br.com.fiap.querocomidahub.restaurant.application.dto.RestaurantInputDTO;
import br.com.fiap.querocomidahub.restaurant.application.dto.RestaurantOutputDTO;
import br.com.fiap.querocomidahub.restaurant.infrastructure.web.json.RestaurantRequestJson;
import br.com.fiap.querocomidahub.restaurant.infrastructure.web.json.RestaurantResponseJson;
import br.com.fiap.querocomidahub.restaurant.infrastructure.web.mapper.RestaurantJSONMapper;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static br.com.fiap.querocomidahub.shared.infrastructure.security.UserIdentityResolver.HEADER_USER_ID;

@RestController
@RequestMapping("/api/v1/restaurants")
public class RestaurantApiController implements IRestaurantSwaggerDoc {

    private final RestaurantController restaurantController;
    private final UserIdentityResolver userIdentityResolver;
    private final ILoggerGateway logger;

    public RestaurantApiController(RestaurantController restaurantController,
                                   UserIdentityResolver userIdentityResolver) {
        this.restaurantController = restaurantController;
        this.userIdentityResolver = userIdentityResolver;
        this.logger = LoggerGatewayFactory.forClass(RestaurantApiController.class);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<RestaurantResponseJson>> findAll() {
        logRequestReceived(HttpMethod.GET);

        List<RestaurantResponseJson> response = restaurantController.findAll()
                .stream()
                .map(RestaurantJSONMapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponseJson> findById(@PathVariable Long id) {
        logRequestReceived(HttpMethod.GET);

        RestaurantOutputDTO outputDTO = restaurantController.findById(id);
        RestaurantResponseJson response = RestaurantJSONMapper.toResponse(outputDTO);

        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid RestaurantRequestJson request,
                                       @RequestHeader(value = HEADER_USER_ID, required = false) String userId) {
        logRequestReceived(HttpMethod.POST, request);

        UserBase caller = userIdentityResolver.resolve(userId);

        RestaurantInputDTO inputDTO = RestaurantJSONMapper.toInputDTO(request);
        Long restaurantId = restaurantController.create(inputDTO, caller.getId());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(restaurantId)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id,
                                       @RequestBody @Valid RestaurantRequestJson request,
                                       @RequestHeader(value = HEADER_USER_ID, required = false) String userId) {
        logRequestReceived(HttpMethod.PUT, request);

        UserBase caller = userIdentityResolver.resolve(userId);

        RestaurantInputDTO inputDTO = RestaurantJSONMapper.toInputDTO(request);
        restaurantController.update(id, inputDTO, caller.getId());

        return ResponseEntity.noContent().build();
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @RequestHeader(value = HEADER_USER_ID, required = false) String userId) {
        logRequestReceived(HttpMethod.DELETE);

        UserBase caller = userIdentityResolver.resolve(userId);

        restaurantController.delete(id, caller.getId());

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
