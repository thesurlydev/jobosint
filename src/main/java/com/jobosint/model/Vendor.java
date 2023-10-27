package com.jobosint.model;

import java.util.UUID;

public enum Vendor {
    TOYOTA_PARTS_DEAL(UUID.fromString("3f5500c6-6c57-11ee-bd4f-1b9fe6aaa33a"), "https://www.toyotapartsdeal.com"),
    CRUISER_CORPS(UUID.fromString("c2a15c72-6c57-11ee-932b-6fb92f5a6892"), "https://cruisercorps.com"),
    OEM_PARTS_ONLINE(UUID.fromString("a8a79a3e-6c57-11ee-bb73-dffc585c4a98"), "https://toyota.oempartsonline.com");

    private final UUID id;
    private final String baseUrl;

    Vendor(UUID uuid, String baseUrl) {

        this.id = uuid;
        this.baseUrl = baseUrl;
    }

    public String baseUrl() {
        return this.baseUrl;
    }

    public UUID id() {
        return id;
    }

}
