package br.com.fiap.querocomidahub.user.application.dto;

import br.com.fiap.querocomidahub.usertype.application.dto.UserTypeOutputDTO;

import java.time.LocalDateTime;

public record UserOutputDTO(
        Long id,
        String name,
        String email,
        String address,
        UserTypeOutputDTO userType,
        LocalDateTime createdAt,
        LocalDateTime lastModifiedAt
) {
}
