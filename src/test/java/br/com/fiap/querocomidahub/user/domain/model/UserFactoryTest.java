package br.com.fiap.querocomidahub.user.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static br.com.fiap.querocomidahub.user.UserTestFixtures.NOW;
import static br.com.fiap.querocomidahub.usertype.UserTypeTestFixtures.CLIENTE;
import static br.com.fiap.querocomidahub.usertype.UserTypeTestFixtures.DONO_DE_RESTAURANTE;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserFactory")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UserFactoryTest {

    @Nested
    @DisplayName("create()")
    class CreateTest {

        @Test
        void returns_ClientUser_when_userType_cannot_manage_restaurants() {
            UserBase user = UserFactory.create("Name", "a@b.com", "Rua X", CLIENTE);

            assertThat(user).isInstanceOf(ClientUser.class);
        }

        @Test
        void returns_RestaurantOwnerUser_when_userType_can_manage_restaurants() {
            UserBase user = UserFactory.create("Name", "a@b.com", "Rua X", DONO_DE_RESTAURANTE);

            assertThat(user).isInstanceOf(RestaurantOwnerUser.class);
        }
    }

    @Nested
    @DisplayName("reconstitute()")
    class ReconstituteTest {

        @Test
        void returns_ClientUser_when_userType_cannot_manage_restaurants() {
            UserBase user = UserFactory.reconstitute(1L, "Name", "a@b.com", "Rua X", CLIENTE, NOW, NOW);

            assertThat(user).isInstanceOf(ClientUser.class);
        }

        @Test
        void returns_RestaurantOwnerUser_when_userType_can_manage_restaurants() {
            UserBase user = UserFactory.reconstitute(1L, "Name", "a@b.com", "Rua X", DONO_DE_RESTAURANTE, NOW, NOW);

            assertThat(user).isInstanceOf(RestaurantOwnerUser.class);
        }
    }
}
