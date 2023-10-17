package com.jobosint.model;

import java.util.UUID;

public enum Manufacturer {
    TOYOTA(UUID.fromString("aeae92d7-b5c3-4cf6-ac20-0cabdf4eed39"));

    private final UUID id;

    Manufacturer(UUID uuid) {
        this.id = uuid;
    }

    public UUID id() {
        return id;
    }
}
