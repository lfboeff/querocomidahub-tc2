package br.com.fiap.querocomidahub.user.domain.model;

import br.com.fiap.querocomidahub.shared.domain.exception.DomainValidationException;
import br.com.fiap.querocomidahub.usertype.domain.model.UserType;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.regex.Pattern;

public abstract sealed class UserBase permits ClientUser, RestaurantOwnerUser {

    protected final Long id;
    protected final String name;
    protected final String email;
    protected final String address;
    protected final UserType userType;
    protected final LocalDateTime createdAt;
    protected final LocalDateTime lastModifiedAt;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@.\\s]+\\.[^@\\s]+$");

    protected UserBase(String name, String email, String address, UserType userType) {
        this.id = null;
        this.name = sanitizeName(name);
        this.email = sanitizeEmail(email);
        this.address = sanitizeAddress(address);
        this.userType = Objects.requireNonNull(userType, "User 'userType' cannot be null");
        this.createdAt = null;
        this.lastModifiedAt = null;
    }

    protected UserBase(Long id, String name, String email, String address, UserType userType,
                       LocalDateTime createdAt, LocalDateTime lastModifiedAt) {
        this.id = Objects.requireNonNull(id, "User 'id' cannot be null");
        this.name = Objects.requireNonNull(name, "User 'name' cannot be null");
        this.email = Objects.requireNonNull(email, "User 'email' cannot be null");
        this.address = Objects.requireNonNull(address, "User 'address' cannot be null");
        this.userType = Objects.requireNonNull(userType, "User 'userType' cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "User 'createdAt' cannot be null");
        this.lastModifiedAt = Objects.requireNonNull(lastModifiedAt, "User 'lastModifiedAt' cannot be null");
    }

    protected static String sanitizeName(String name) {
        Objects.requireNonNull(name, "User 'name' cannot be null");
        String sanitizedName = name.trim();

        if (sanitizedName.isBlank()) {
            throw new DomainValidationException("User 'name' cannot be blank");
        }
        return sanitizedName;
    }

    protected static String sanitizeEmail(String email) {
        Objects.requireNonNull(email, "User 'email' cannot be null");
        String sanitizedEmail = email.trim().toLowerCase();

        if (sanitizedEmail.isBlank()) {
            throw new DomainValidationException("User 'email' cannot be blank");
        }

        if (!EMAIL_PATTERN.matcher(sanitizedEmail).matches()) {
            throw new DomainValidationException("User 'email' must be a valid email address");
        }
        return sanitizedEmail;
    }

    protected static String sanitizeAddress(String address) {
        Objects.requireNonNull(address, "User 'address' cannot be null");
        String sanitizedAddress = address.trim();

        if (sanitizedAddress.isBlank()) {
            throw new DomainValidationException("User 'address' cannot be blank");
        }
        return sanitizedAddress;
    }

    public abstract UserBase withUpdatedParams(String name, String email, String address);

    public boolean canManageRestaurants() {
        return this instanceof RestaurantOwnerUser;
    }

    public boolean hasSameUserType(UserType other) {
        return userType.getId().equals(other.getId());
    }

    public boolean isBeingDemotedFrom(UserType newUserType) {
        return canManageRestaurants() && !newUserType.canManageRestaurants();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public UserType getUserType() {
        return userType;
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
        if (!(o instanceof UserBase other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "UserBase{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", userType=" + userType +
                ", createdAt=" + createdAt +
                ", lastModifiedAt=" + lastModifiedAt +
                '}';
    }
}
