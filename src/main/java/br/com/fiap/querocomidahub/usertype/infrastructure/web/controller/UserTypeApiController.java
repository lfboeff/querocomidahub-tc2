package br.com.fiap.querocomidahub.usertype.infrastructure.web.controller;

import br.com.fiap.querocomidahub.shared.domain.gateway.ILoggerGateway;
import br.com.fiap.querocomidahub.shared.infrastructure.gateway.LoggerGatewayFactory;
import br.com.fiap.querocomidahub.usertype.application.controller.UserTypeController;
import br.com.fiap.querocomidahub.usertype.application.dto.UserTypeInputDTO;
import br.com.fiap.querocomidahub.usertype.application.dto.UserTypeOutputDTO;
import br.com.fiap.querocomidahub.usertype.infrastructure.web.json.UserTypeRequestJson;
import br.com.fiap.querocomidahub.usertype.infrastructure.web.json.UserTypeResponseJson;
import br.com.fiap.querocomidahub.usertype.infrastructure.web.mapper.UserTypeJSONMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/api/v1/user-types")
public class UserTypeApiController implements UserTypeApi {

    private final UserTypeController userTypeController;
    private final ILoggerGateway logger;

    public UserTypeApiController(UserTypeController userTypeController) {
        this.userTypeController = userTypeController;
        this.logger = LoggerGatewayFactory.forClass(UserTypeApiController.class);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<UserTypeResponseJson>> findAll() {
        logRequestReceived(HttpMethod.GET);

        List<UserTypeResponseJson> response = userTypeController.findAll()
                .stream()
                .map(UserTypeJSONMapper::toResponseJson)
                .toList();

        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<UserTypeResponseJson> findById(@PathVariable Long id) {
        logRequestReceived(HttpMethod.GET);

        UserTypeOutputDTO outputDTO = userTypeController.findById(id);
        UserTypeResponseJson response = UserTypeJSONMapper.toResponseJson(outputDTO);

        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid UserTypeRequestJson request) {
        logRequestReceived(HttpMethod.POST, request);

        UserTypeInputDTO inputDTO = UserTypeJSONMapper.toInputDTO(request);
        Long userTypeId = userTypeController.create(inputDTO);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(userTypeId)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id,
                                       @RequestBody @Valid UserTypeRequestJson request) {
        logRequestReceived(HttpMethod.PUT, request);

        UserTypeInputDTO inputDTO = UserTypeJSONMapper.toInputDTO(request);
        userTypeController.update(id, inputDTO);

        return ResponseEntity.noContent().build();
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logRequestReceived(HttpMethod.DELETE);

        userTypeController.delete(id);

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
