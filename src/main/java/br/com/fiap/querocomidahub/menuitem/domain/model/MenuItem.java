package br.com.fiap.querocomidahub.menuitem.domain.model;

import br.com.fiap.querocomidahub.shared.domain.exception.DomainValidationException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public final class MenuItem {

    private final Long id;
    private final Long restaurantId;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final boolean dineInOnly;
    private final String photoPath;
    private final LocalDateTime createdAt;
    private final LocalDateTime lastModifiedAt;

    private MenuItem(Long restaurantId, String name, String description,
                     BigDecimal price, boolean dineInOnly, String photoPath) {
        this.id = null;
        this.restaurantId = Objects.requireNonNull(restaurantId, "Menu item 'restaurantId' cannot be null");
        this.name = sanitizeName(name);
        this.description = sanitizeDescription(description);
        this.price = sanitizePrice(price);
        this.dineInOnly = dineInOnly;
        this.photoPath = sanitizePhotoPath(photoPath);
        this.createdAt = null;
        this.lastModifiedAt = null;
    }

    private MenuItem(Long id, Long restaurantId, String name, String description,
                     BigDecimal price, boolean dineInOnly, String photoPath,
                     LocalDateTime createdAt, LocalDateTime lastModifiedAt) {
        this.id = Objects.requireNonNull(id, "Menu item 'id' cannot be null");
        this.restaurantId = Objects.requireNonNull(restaurantId, "Menu item 'restaurantId' cannot be null");
        this.name = Objects.requireNonNull(name, "Menu item 'name' cannot be null");
        this.description = Objects.requireNonNull(description, "Menu item 'description' cannot be null");
        this.price = Objects.requireNonNull(price, "Menu item 'price' cannot be null");
        this.dineInOnly = dineInOnly;
        this.photoPath = photoPath;
        this.createdAt = Objects.requireNonNull(createdAt, "Menu item 'createdAt' cannot be null");
        this.lastModifiedAt = Objects.requireNonNull(lastModifiedAt, "Menu item 'lastModifiedAt' cannot be null");
    }

    public static MenuItem create(Long restaurantId, String name, String description,
                                  BigDecimal price, boolean dineInOnly, String photoPath) {
        return new MenuItem(restaurantId, name, description, price, dineInOnly, photoPath);
    }

    public static MenuItem reconstitute(Long id, Long restaurantId, String name, String description,
                                        BigDecimal price, boolean dineInOnly, String photoPath,
                                        LocalDateTime createdAt, LocalDateTime lastModifiedAt) {
        return new MenuItem(id, restaurantId, name, description, price, dineInOnly, photoPath,
                createdAt, lastModifiedAt);
    }

    public MenuItem withUpdatedParams(String name, String description, BigDecimal price,
                                      boolean dineInOnly, String photoPath) {
        return new MenuItem(
                this.id,
                this.restaurantId,
                sanitizeName(name),
                sanitizeDescription(description),
                sanitizePrice(price),
                dineInOnly,
                sanitizePhotoPath(photoPath),
                this.createdAt,
                this.lastModifiedAt);
    }

    public boolean belongsTo(Long restaurantId) {
        return this.restaurantId.equals(restaurantId);
    }

    private static String sanitizeName(String name) {
        Objects.requireNonNull(name, "Menu item 'name' cannot be null");
        String sanitizedName = name.trim();

        if (sanitizedName.isBlank()) {
            throw new DomainValidationException("Menu item 'name' cannot be blank");
        }
        return sanitizedName;
    }

    private static String sanitizeDescription(String description) {
        Objects.requireNonNull(description, "Menu item 'description' cannot be null");
        String sanitizedDescription = description.trim();

        if (sanitizedDescription.isBlank()) {
            throw new DomainValidationException("Menu item 'description' cannot be blank");
        }
        return sanitizedDescription;
    }

    private static BigDecimal sanitizePrice(BigDecimal price) {
        Objects.requireNonNull(price, "Menu item 'price' cannot be null");

        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainValidationException("Menu item 'price' must be greater than zero");
        }
        return price;
    }

    private static String sanitizePhotoPath(String photoPath) {
        if (photoPath == null || photoPath.isBlank()) {
            return null;
        }
        return photoPath.trim();
    }

    public Long getId() {
        return id;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public boolean isDineInOnly() {
        return dineInOnly;
    }

    public String getPhotoPath() {
        return photoPath;
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
        if (!(o instanceof MenuItem other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "MenuItem{" +
                "id=" + id +
                ", restaurantId=" + restaurantId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", dineInOnly=" + dineInOnly +
                ", photoPath='" + photoPath + '\'' +
                ", createdAt=" + createdAt +
                ", lastModifiedAt=" + lastModifiedAt +
                '}';
    }
}
