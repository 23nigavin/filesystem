package interfaces;

public interface FolderResource {
    String getName();
    byte[] toBytes();

    FolderResource getFolder(String name);
    boolean putFolder(String name); 
    boolean deleteFolder(String name);

    DocumentResource getDocument(String name);
    boolean putDocument(String name, DocumentResource doc);
    boolean deleteDocument(String name);
}
