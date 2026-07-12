package br.com.fiap.querocomidahub.user.infrastructure.web.mapper;

import br.com.fiap.querocomidahub.user.application.dto.AssignUserTypeInputDTO;
import br.com.fiap.querocomidahub.user.application.dto.CreateUserInputDTO;
import br.com.fiap.querocomidahub.user.application.dto.UpdateUserInputDTO;
import br.com.fiap.querocomidahub.user.application.dto.UserOutputDTO;
import br.com.fiap.querocomidahub.user.infrastructure.web.json.AssignUserTypeRequestJson;
import br.com.fiap.querocomidahub.user.infrastructure.web.json.CreateUserRequestJson;
import br.com.fiap.querocomidahub.user.infrastructure.web.json.UpdateUserRequestJson;
import br.com.fiap.querocomidahub.user.infrastructure.web.json.UserResponseJson;
import br.com.fiap.querocomidahub.usertype.infrastructure.web.mapper.UserTypeJSONMapper;

public final class UserJSONMapper {

    private UserJSONMapper() {
    }

    public static CreateUserInputDTO toCreateInput(CreateUserRequestJson request) {
        return new CreateUserInputDTO(
                request.name(),
                request.email(),
                request.address(),
                request.userTypeId()
        );
    }

    public static UpdateUserInputDTO toUpdateInput(UpdateUserRequestJson request) {
        return new UpdateUserInputDTO(
                request.name(),
                request.email(),
                request.address()
        );
    }

    public static AssignUserTypeInputDTO toAssignInput(AssignUserTypeRequestJson request) {
        return new AssignUserTypeInputDTO(request.userTypeId());
    }

    public static UserResponseJson toResponse(UserOutputDTO output) {
        return new UserResponseJson(
                output.id(),
                output.name(),
                output.email(),
                output.address(),
                UserTypeJSONMapper.toResponseJson(output.userType()),
                output.createdAt(),
                output.lastModifiedAt()
        );
    }
}
