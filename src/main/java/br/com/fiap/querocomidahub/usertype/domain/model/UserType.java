package br.com.fiap.querocomidahub.usertype.domain.model;

import br.com.fiap.querocomidahub.shared.domain.exception.DomainValidationException;
import br.com.fiap.querocomidahub.usertype.domain.exception.UserTypeIsSystemException;

import java.time.LocalDateTime;
import java.util.Objects;

public final class UserType {

    private final Long id;
    private final String name;
    private final boolean isSystem;
    private final boolean canManageRestaurants;
    private final LocalDateTime createdAt;
    private final LocalDateTime lastModifiedAt;

    private UserType(String name, boolean canManageRestaurants) {
        this.id = null;
        this.name = sanitizeName(name);
        this.isSystem = false;
        this.canManageRestaurants = canManageRestaurants;
        this.createdAt = null;
        this.lastModifiedAt = null;
    }

    private UserType(Long id, String name, boolean isSystem, boolean canManageRestaurants,
                     LocalDateTime createdAt, LocalDateTime lastModifiedAt) {
        this.id = Objects.requireNonNull(id, "User type 'id' cannot be null");
        this.name = Objects.requireNonNull(name, "User type 'name' cannot be null");
        this.isSystem = isSystem;
        this.canManageRestaurants = canManageRestaurants;
        this.createdAt = Objects.requireNonNull(createdAt, "User type 'createdAt' cannot be null");
        this.lastModifiedAt = Objects.requireNonNull(lastModifiedAt, "User type 'lastModifiedAt' cannot be null");
    }

    public static UserType create(String name, boolean canManageRestaurants) {
        return new UserType(name, canManageRestaurants);
    }

    public static UserType reconstitute(Long id, String name, boolean isSystem, boolean canManageRestaurants,
                                        LocalDateTime createdAt, LocalDateTime lastModifiedAt) {
        return new UserType(id, name, isSystem, canManageRestaurants, createdAt, lastModifiedAt);
    }

    private static String sanitizeName(String name) {
        Objects.requireNonNull(name, "User type 'name' cannot be null");
        String sanitizedName = name.trim();

        if (sanitizedName.isBlank()) {
            throw new DomainValidationException("User type 'name' cannot be blank");
        }
        return sanitizedName;
    }

    public void ensureModifiable() {
        if (this.isSystem) {
            throw new UserTypeIsSystemException(this.id);
        }
    }

    public UserType withUpdatedParams(String name, boolean canManageRestaurants) {
        return new UserType(
                this.id,
                sanitizeName(name),
                this.isSystem,
                canManageRestaurants,
                this.createdAt,
                this.lastModifiedAt);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public boolean canManageRestaurants() {
        return canManageRestaurants;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastModifiedAt() {
        return lastModifiedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserType other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "UserType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isSystem=" + isSystem +
                ", canManageRestaurants=" + canManageRestaurants +
                ", createdAt=" + createdAt +
                ", lastModifiedAt=" + lastModifiedAt +
                '}';
    }
}
