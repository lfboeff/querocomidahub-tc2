package br.com.fiap.querocomidahub.user.infrastructure.web.controller;

import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;
import br.com.fiap.querocomidahub.shared.infrastructure.gateway.LoggerGatewayFactory;
import br.com.fiap.querocomidahub.user.application.controller.UserController;
import br.com.fiap.querocomidahub.user.application.dto.AssignUserTypeInputDTO;
import br.com.fiap.querocomidahub.user.application.dto.CreateUserInputDTO;
import br.com.fiap.querocomidahub.user.application.dto.UpdateUserInputDTO;
import br.com.fiap.querocomidahub.user.application.dto.UserOutputDTO;
import br.com.fiap.querocomidahub.user.infrastructure.web.json.AssignUserTypeRequestJson;
import br.com.fiap.querocomidahub.user.infrastructure.web.json.CreateUserRequestJson;
import br.com.fiap.querocomidahub.user.infrastructure.web.json.UpdateUserRequestJson;
import br.com.fiap.querocomidahub.user.infrastructure.web.json.UserResponseJson;
import br.com.fiap.querocomidahub.user.infrastructure.web.mapper.UserJSONMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserApiController implements UserApi {

    private final UserController userController;
    private final ILoggerGateway logger;

    public UserApiController(UserController userController) {
        this.userController = userController;
        this.logger = LoggerGatewayFactory.forClass(UserApiController.class);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<UserResponseJson>> findAll() {
        logRequestReceived(HttpMethod.GET);

        List<UserResponseJson> response = userController.findAll()
                .stream()
                .map(UserJSONMapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseJson> findById(@PathVariable Long id) {
        logRequestReceived(HttpMethod.GET);

        UserOutputDTO outputDTO = userController.findById(id);
        UserResponseJson response = UserJSONMapper.toResponse(outputDTO);

        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid CreateUserRequestJson request) {
        logRequestReceived(HttpMethod.POST, request);

        CreateUserInputDTO inputDTO = UserJSONMapper.toCreateInput(request);
        Long userId = userController.create(inputDTO);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(userId)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id,
                                       @RequestBody @Valid UpdateUserRequestJson request) {
        logRequestReceived(HttpMethod.PUT, request);

        UpdateUserInputDTO inputDTO = UserJSONMapper.toUpdateInput(request);
        userController.update(id, inputDTO);

        return ResponseEntity.noContent().build();
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logRequestReceived(HttpMethod.DELETE);

        userController.delete(id);

        return ResponseEntity.noContent().build();
    }

    @Override
    @PatchMapping("/{id}/user-type")
    public ResponseEntity<Void> assignUserType(@PathVariable Long id,
                                               @RequestBody @Valid AssignUserTypeRequestJson request) {
        logRequestReceived(HttpMethod.PATCH, request);

        AssignUserTypeInputDTO inputDTO = UserJSONMapper.toAssignInput(request);
        userController.assignUserType(id, inputDTO);

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
