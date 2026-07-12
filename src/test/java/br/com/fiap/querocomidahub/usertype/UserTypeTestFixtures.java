package br.com.fiap.querocomidahub.usertype;

import br.com.fiap.querocomidahub.usertype.application.dto.UserTypeOutputDTO;
import br.com.fiap.querocomidahub.usertype.domain.model.UserType;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

public final class UserTypeTestFixtures {

    private UserTypeTestFixtures() {
    }

    public static final LocalDateTime NOW = LocalDateTime.of(2025, Month.JANUARY, 1, 0, 0);

    public static final UserType DONO_DE_RESTAURANTE =
            UserType.reconstitute(1L, "Dono de Restaurante", true, true, NOW, NOW);

    public static final UserType CLIENTE =
            UserType.reconstitute(2L, "Cliente", true, false, NOW, NOW);

    public static final UserType MOTOBOY =
            UserType.reconstitute(3L, "Motoboy", false, false, NOW, NOW);

    public static List<UserType> userTypeList() {
        return List.of(DONO_DE_RESTAURANTE, CLIENTE, MOTOBOY);
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

    public static List<UserTypeOutputDTO> outputDTOList() {
        return userTypeList().stream()
                .map(UserTypeTestFixtures::toOutputDTO)
                .toList();
    }
}
