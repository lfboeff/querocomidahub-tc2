package br.com.fiap.querocomidahub.user.domain.model;

import br.com.fiap.querocomidahub.usertype.domain.model.UserType;

import java.time.LocalDateTime;

public final class ClientUser extends UserBase {

    private ClientUser(String name, String email, String address, UserType userType) {
        super(name, email, address, userType);
    }

    private ClientUser(Long id, String name, String email, String address, UserType userType,
                       LocalDateTime createdAt, LocalDateTime lastModifiedAt) {
        super(id, name, email, address, userType, createdAt, lastModifiedAt);
    }

    static ClientUser create(String name, String email, String address, UserType userType) {
        return new ClientUser(name, email, address, userType);
    }

    static ClientUser reconstitute(Long id, String name, String email, String address, UserType userType,
                                   LocalDateTime createdAt, LocalDateTime lastModifiedAt) {
        return new ClientUser(id, name, email, address, userType, createdAt, lastModifiedAt);
    }

    @Override
    public ClientUser withUpdatedParams(String name, String email, String address) {
        return new ClientUser(this.id, sanitizeName(name), sanitizeEmail(email), sanitizeAddress(address),
                this.userType, this.createdAt, this.lastModifiedAt);
    }
}
