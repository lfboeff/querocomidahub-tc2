package br.com.fiap.querocomidahub.user.application.dto;

public record CreateUserInputDTO(
        String name,
        String email,
        String address,
        Long userTypeId
) {
}
