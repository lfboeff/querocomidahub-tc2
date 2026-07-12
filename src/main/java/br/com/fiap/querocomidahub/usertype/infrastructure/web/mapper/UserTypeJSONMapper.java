package br.com.fiap.querocomidahub.usertype.infrastructure.web.mapper;

import br.com.fiap.querocomidahub.usertype.application.dto.UserTypeInputDTO;
import br.com.fiap.querocomidahub.usertype.application.dto.UserTypeOutputDTO;
import br.com.fiap.querocomidahub.usertype.infrastructure.web.json.UserTypeRequestJson;
import br.com.fiap.querocomidahub.usertype.infrastructure.web.json.UserTypeResponseJson;

public final class UserTypeJSONMapper {

    private UserTypeJSONMapper() {
    }

    public static UserTypeInputDTO toInputDTO(UserTypeRequestJson request) {
        return new UserTypeInputDTO(
                request.name(),
                request.canManageRestaurants()
        );
    }

    public static UserTypeResponseJson toResponseJson(UserTypeOutputDTO output) {
        return new UserTypeResponseJson(
                output.id(),
                output.name(),
                output.isSystem(),
                output.canManageRestaurants(),
                output.createdAt(),
                output.lastModifiedAt()
        );
    }
}
