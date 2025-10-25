package resources;

import interfaces.MetadataResource;

public class Metadata implements MetadataResource {
    private final String createdBy;
    private final long createdAt;
    private String lastModifiedBy;
    private long lastModifiedAt;

    public Metadata(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("empty username");
        }
        long now = System.currentTimeMillis();
        this.createdBy = username;
        this.createdAt = now;
        this.lastModifiedBy = username;
        this.lastModifiedAt = now;
    }

    public void updateMetadata(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("empty username");
        }
        long now = System.currentTimeMillis();
        this.lastModifiedBy = username;
        this.lastModifiedAt = now;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public long getLastModifiedAt() {
        return lastModifiedAt;
    }
}