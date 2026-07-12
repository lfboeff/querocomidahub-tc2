package br.com.fiap.querocomidahub.usertype.application.dto;

public record UserTypeInputDTO(
        String name,
        boolean canManageRestaurants
) {
}

