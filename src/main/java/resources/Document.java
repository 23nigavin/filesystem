package resources;

import java.util.Arrays;
import interfaces.DocumentResource;
import interfaces.MetadataResource;

public class Document implements DocumentResource{
    private final String name;
    private byte[] rawBytes;
    private MetadataResource metadata;

    public Document(String name, byte[] rawBytes, MetadataResource metadata) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("invalid name");
        }
        this.name = name;
        this.rawBytes = Arrays.copyOf(rawBytes, rawBytes.length);
        this.metadata = metadata;
    }

    public String getName() {
        return name;
    }

    public byte[] getRawBytes() {
        return Arrays.copyOf(rawBytes, rawBytes.length);
    }

    public MetadataResource getMetadata() {
        return metadata;
    }

    public void update(String user, byte[] newBytes) {
        this.rawBytes = Arrays.copyOf(newBytes, newBytes.length);
        metadata.updateMetadata(user);
    }

}
