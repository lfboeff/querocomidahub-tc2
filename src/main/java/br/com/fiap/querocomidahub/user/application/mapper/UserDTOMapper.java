package br.com.fiap.querocomidahub.user.application.mapper;

import br.com.fiap.querocomidahub.user.application.dto.UserOutputDTO;
import br.com.fiap.querocomidahub.user.domain.model.UserBase;
import br.com.fiap.querocomidahub.usertype.application.mapper.UserTypeDTOMapper;

public final class UserDTOMapper {

    private UserDTOMapper() {}

    public static UserOutputDTO toOutputDTO(UserBase user) {
        return new UserOutputDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAddress(),
                UserTypeDTOMapper.toOutputDTO(user.getUserType()),
                user.getCreatedAt(),
                user.getLastModifiedAt()
        );
    }
}
