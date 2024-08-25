package etu.nic.store.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProductType {
    SMARTPHONE("SMARTPHONE"),
    WASHING_MACHINE("WASHING_MACHINE");

    private final String type;

    ProductType(String type) {
        this.type = type;
    }

    @JsonValue
    public String getType() {
        return type;
    }

    @JsonCreator
    public static ProductType fromString(String type) {
        for (ProductType pt : ProductType.values()) {
            if (pt.type.equalsIgnoreCase(type)) {
                return pt;
            }
        }
        throw new IllegalArgumentException("Unknown product type: " + type);
    }
}
