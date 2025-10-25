package interfaces;

public interface MetadataResource {
    void updateMetadata(String username);
    String getCreatedBy();
    long getCreatedAt();
    String getLastModifiedBy();
    long getLastModifiedAt();
}
