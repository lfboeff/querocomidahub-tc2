package br.com.fiap.querocomidahub.usertype.application.mapper;

import br.com.fiap.querocomidahub.usertype.application.dto.UserTypeOutputDTO;
import br.com.fiap.querocomidahub.usertype.domain.model.UserType;

public final class UserTypeDTOMapper {

    private UserTypeDTOMapper() {
    }

    public static UserTypeOutputDTO toOutputDTO(UserType userType) {
        return new UserTypeOutputDTO(
                userType.getId(),
                userType.getName(),
                userType.isSystem(),
                userType.canManageRestaurants(),
                userType.getCreatedAt(),
                userType.getLastModifiedAt()
        );
    }
}
