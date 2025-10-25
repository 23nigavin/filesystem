package filesystem;

import interfaces.FolderResource;
import interfaces.DocumentResource;
import interfaces.FileSystemResource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Root implements FileSystemResource {
    private final Map<String, FolderResource> rootFolders;
    private final ReentrantReadWriteLock lock;

    public Root() {
        this.rootFolders = new ConcurrentHashMap<>();
        this.lock = new ReentrantReadWriteLock();
    }

    public boolean putFolder(String path, String name) {
        validateName(name);
        lock.writeLock().lock();
        try {
            FolderResource parent = resolveFolder(path);
            FolderResource newFolder = new resources.Folder(name);
            if (parent == null) {
                rootFolders.put(name, newFolder);
                return true; 
            } else {
                return parent.putFolder(name);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public FolderResource getFolder(String path) {
        lock.readLock().lock();
        try {
            FolderResource folder = resolveFolder(path);
            if (folder == null) {
                throw new IllegalArgumentException("folder not found: " + path);
            }
            return folder;
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean deleteFolder(String path) {
        String[] parts = validatePath(path);
        if (parts.length == 0) throw new IllegalArgumentException("empty path");

        lock.writeLock().lock();
        try {
            if (parts.length == 1) {
                return rootFolders.remove(parts[0]) != null;
            } else {
                FolderResource parent = resolveFolder(join(parts, parts.length - 1));
                if (parent == null) throw new IllegalArgumentException("parent folder not found");
                return parent.deleteFolder(parts[parts.length - 1]);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean putDocument(String path, String name, DocumentResource doc) {
        validateName(name);
        lock.writeLock().lock();
        try {
            FolderResource parent = resolveFolder(path);
            if (parent == null) throw new IllegalArgumentException("folder not found: " + path);
            return parent.putDocument(name, doc);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public DocumentResource getDocument(String path) {
        String[] parts = validatePath(path);
        if (parts.length == 0) throw new IllegalArgumentException("empty path");

        lock.readLock().lock();
        try {
            if (parts.length == 1) throw new IllegalArgumentException("path must include document");
            FolderResource parent = resolveFolder(join(parts, parts.length - 1));
            if (parent == null) throw new IllegalArgumentException("parent folder not found");
            DocumentResource doc = parent.getDocument(parts[parts.length - 1]);
            if (doc == null) throw new IllegalArgumentException("document not found: " + parts[parts.length - 1]);
            return doc;
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean deleteDocument(String path) {
        String[] parts = validatePath(path);
        if (parts.length == 0) throw new IllegalArgumentException("empty path");

        lock.writeLock().lock();
        try {
            FolderResource parent = resolveFolder(join(parts, parts.length - 1));
            if (parent == null) throw new IllegalArgumentException("parent folder not found");
            return parent.deleteDocument(parts[parts.length - 1]);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private FolderResource resolveFolder(String path) {
        if (path == null || path.isEmpty() || path.equals("/")) {
            return null;
        }
        String[] parts = validatePath(path);
        FolderResource current = rootFolders.get(parts[0]);
        if (current == null) return null;

        for (int i = 1; i < parts.length; i++) {
            current = current.getFolder(parts[i]);
            if (current == null) return null;
        }
        return current;
    }

    private String[] validatePath(String path) {
        if (path == null || path.isEmpty()) throw new IllegalArgumentException("invalid path");
        if (path.startsWith("/")) path = path.substring(1);
        if (path.endsWith("/")) path = path.substring(0, path.length() - 1);
        return path.isEmpty() ? new String[0] : path.split("/");
    }

    private void validateName(String name) {
        if (name == null || name.isEmpty() || name.contains("/")) {
            throw new IllegalArgumentException("invalid name");
        }
    }

    private String join(String[] parts, int length) {
        return String.join("/", java.util.Arrays.copyOf(parts, length));
    }
}
