package br.com.fiap.querocomidahub.restaurant.domain.model;

import br.com.fiap.querocomidahub.shared.domain.exception.DomainValidationException;

import java.time.LocalDateTime;
import java.util.Objects;

public final class Restaurant {

    private final Long id;
    private final String name;
    private final String address;
    private final String kitchenType;
    private final String openingHours;
    private final Long ownerId;
    private final LocalDateTime createdAt;
    private final LocalDateTime lastModifiedAt;

    private Restaurant(String name, String address, String kitchenType, String openingHours, Long ownerId) {
        this.id = null;
        this.name = sanitizeName(name);
        this.address = sanitizeAddress(address);
        this.kitchenType = sanitizeKitchenType(kitchenType);
        this.openingHours = sanitizeOpeningHours(openingHours);
        this.ownerId = Objects.requireNonNull(ownerId, "Restaurant 'ownerId' cannot be null");
        this.createdAt = null;
        this.lastModifiedAt = null;
    }

    private Restaurant(Long id, String name, String address, String kitchenType, String openingHours,
                       Long ownerId, LocalDateTime createdAt, LocalDateTime lastModifiedAt) {
        this.id = Objects.requireNonNull(id, "Restaurant 'id' cannot be null");
        this.name = Objects.requireNonNull(name, "Restaurant 'name' cannot be null");
        this.address = Objects.requireNonNull(address, "Restaurant 'address' cannot be null");
        this.kitchenType = Objects.requireNonNull(kitchenType, "Restaurant 'kitchenType' cannot be null");
        this.openingHours = Objects.requireNonNull(openingHours, "Restaurant 'openingHours' cannot be null");
        this.ownerId = Objects.requireNonNull(ownerId, "Restaurant 'ownerId' cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Restaurant 'createdAt' cannot be null");
        this.lastModifiedAt = Objects.requireNonNull(lastModifiedAt, "Restaurant 'lastModifiedAt' cannot be null");
    }

    public static Restaurant create(String name, String address, String kitchenType,
                                    String openingHours, Long ownerId) {
        return new Restaurant(name, address, kitchenType, openingHours, ownerId);
    }

    public static Restaurant reconstitute(Long id, String name, String address, String kitchenType,
                                          String openingHours, Long ownerId,
                                          LocalDateTime createdAt, LocalDateTime lastModifiedAt) {
        return new Restaurant(id, name, address, kitchenType, openingHours, ownerId, createdAt, lastModifiedAt);
    }

    public boolean isOwnedBy(Long callerId) {
        return ownerId.equals(callerId);
    }

    public Restaurant withUpdatedParams(String name, String address, String kitchenType, String openingHours) {
        return new Restaurant(
                this.id,
                sanitizeName(name),
                sanitizeAddress(address),
                sanitizeKitchenType(kitchenType),
                sanitizeOpeningHours(openingHours),
                this.ownerId,
                this.createdAt,
                this.lastModifiedAt);
    }

    private static String sanitizeName(String name) {
        Objects.requireNonNull(name, "Restaurant 'name' cannot be null");
        String sanitizedName = name.trim();

        if (sanitizedName.isBlank()) {
            throw new DomainValidationException("Restaurant 'name' cannot be blank");
        }
        return sanitizedName;
    }

    private static String sanitizeAddress(String address) {
        Objects.requireNonNull(address, "Restaurant 'address' cannot be null");
        String sanitizedAddress = address.trim();

        if (sanitizedAddress.isBlank()) {
            throw new DomainValidationException("Restaurant 'address' cannot be blank");
        }
        return sanitizedAddress;
    }

    private static String sanitizeKitchenType(String kitchenType) {
        Objects.requireNonNull(kitchenType, "Restaurant 'kitchenType' cannot be null");
        String sanitizedKitchenType = kitchenType.trim();

        if (sanitizedKitchenType.isBlank()) {
            throw new DomainValidationException("Restaurant 'kitchenType' cannot be blank");
        }
        return sanitizedKitchenType;
    }

    private static String sanitizeOpeningHours(String openingHours) {
        Objects.requireNonNull(openingHours, "Restaurant 'openingHours' cannot be null");
        String sanitizedOpeningHours = openingHours.trim();

        if (sanitizedOpeningHours.isBlank()) {
            throw new DomainValidationException("Restaurant 'openingHours' cannot be blank");
        }
        return sanitizedOpeningHours;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getKitchenType() {
        return kitchenType;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public Long getOwnerId() {
        return ownerId;
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
        if (!(o instanceof Restaurant other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", kitchenType='" + kitchenType + '\'' +
                ", openingHours='" + openingHours + '\'' +
                ", ownerId=" + ownerId +
                ", createdAt=" + createdAt +
                ", lastModifiedAt=" + lastModifiedAt +
                '}';
    }
}
