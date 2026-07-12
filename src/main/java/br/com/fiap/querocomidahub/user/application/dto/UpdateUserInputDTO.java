package br.com.fiap.querocomidahub.user.application.dto;

public record UpdateUserInputDTO(
        String name,
        String email,
        String address
) {
}
