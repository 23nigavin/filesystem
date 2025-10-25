package interfaces;

public interface DocumentResource {
    String getName();
    byte[] getRawBytes();
    MetadataResource getMetadata();
    void update(String user, byte[] newBytes);
}