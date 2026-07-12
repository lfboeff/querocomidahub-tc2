package br.com.fiap.querocomidahub.user;

import br.com.fiap.querocomidahub.user.application.dto.UserOutputDTO;
import br.com.fiap.querocomidahub.user.domain.model.ClientUser;
import br.com.fiap.querocomidahub.user.domain.model.RestaurantOwnerUser;
import br.com.fiap.querocomidahub.user.domain.model.UserBase;
import br.com.fiap.querocomidahub.user.domain.model.UserFactory;
import br.com.fiap.querocomidahub.usertype.UserTypeTestFixtures;
import br.com.fiap.querocomidahub.usertype.application.dto.UserTypeOutputDTO;
import br.com.fiap.querocomidahub.usertype.domain.model.UserType;

import java.time.LocalDateTime;
import java.util.List;

public final class UserTestFixtures {

    private UserTestFixtures() {
    }

    public static final LocalDateTime NOW = UserTypeTestFixtures.NOW;

    public static final UserBase JOAO_CLIENTE = UserFactory.reconstitute(
            1L,
            "João da Silva",
            "joao.silva@email.com",
            "Rua das Flores, 123 - São Paulo, SP",
            UserTypeTestFixtures.CLIENTE,
            NOW,
            NOW
    );

    public static final UserBase MARIA_DONA = UserFactory.reconstitute(
            2L,
            "Maria Oliveira",
            "maria.oliveira@email.com",
            "Av. Paulista, 1000 - São Paulo, SP",
            UserTypeTestFixtures.DONO_DE_RESTAURANTE,
            NOW,
            NOW
    );

    public static final UserBase CARLOS_DONO = UserFactory.reconstitute(
            3L,
            "Carlos Souza",
            "carlos.souza@email.com",
            "Rua Augusta, 500 - São Paulo, SP",
            UserTypeTestFixtures.DONO_DE_RESTAURANTE,
            NOW,
            NOW
    );

    public static List<UserBase> userList() {
        return List.of(JOAO_CLIENTE, MARIA_DONA, CARLOS_DONO);
    }

    public static UserOutputDTO toOutputDTO(UserBase user) {
        UserType userType = user.getUserType();
        UserTypeOutputDTO userTypeDTO = new UserTypeOutputDTO(
                userType.getId(),
                userType.getName(),
                userType.isSystem(),
                userType.canManageRestaurants(),
                userType.getCreatedAt(),
                userType.getLastModifiedAt()
        );
        return new UserOutputDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAddress(),
                userTypeDTO,
                user.getCreatedAt(),
                user.getLastModifiedAt()
        );
    }

    public static List<UserOutputDTO> outputDTOList() {
        return userList().stream()
                .map(UserTestFixtures::toOutputDTO)
                .toList();
    }

    public static ClientUser sampleClient() {
        return (ClientUser) JOAO_CLIENTE;
    }

    public static RestaurantOwnerUser sampleOwner() {
        return (RestaurantOwnerUser) MARIA_DONA;
    }
}
