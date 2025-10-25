package interfaces;

public interface FileSystemResource {
    boolean putFolder(String path, String name);
    FolderResource getFolder(String path);
    boolean deleteFolder(String path);

    boolean putDocument(String path, String name, DocumentResource doc);
    DocumentResource getDocument(String path);
    boolean deleteDocument(String path);
}
