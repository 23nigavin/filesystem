package resources;

import interfaces.FolderResource;
import interfaces.DocumentResource;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Folder implements FolderResource {
    private final String name;
    private final Map<String, Folder> folders = new ConcurrentHashMap<>();
    private final Map<String, DocumentResource> documents = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public Folder(String name) {
        validateName(name);
        this.name = name;
    }

    public Folder getFolder(String child) {
        validateName(child);
        lock.readLock().lock();
        try {
            Folder f = folders.get(child);
            if (f == null) throw new IllegalArgumentException("folder does not exist");
            return f;
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean putFolder(String child) {
        validateName(child);
        lock.writeLock().lock();
        try {
            return folders.put(child, new Folder(child)) != null;
        } finally {
            lock.writeLock().unlock();
        }
    }    

    public boolean deleteFolder(String child) {
        validateName(child);
        lock.writeLock().lock();
        try {
            return folders.remove(child) != null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public DocumentResource getDocument(String name) {
        validateName(name);
        lock.readLock().lock();
        try {
            DocumentResource doc = documents.get(name);
            if (doc == null) throw new IllegalArgumentException("document does not exist");
            return doc;
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean putDocument(String name, DocumentResource doc) {
        validateName(name);
        if (doc == null) throw new IllegalArgumentException("null document");
        lock.writeLock().lock();
        try {
            return documents.put(name, doc) != null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean deleteDocument(String name) {
        validateName(name);
        lock.writeLock().lock();
        try {
            return documents.remove(name) != null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public String getName() {
        return name;
    }

    public byte[] toBytes() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"folders\":[");
        boolean first = true;
        for (String f : folders.keySet()) {
            if (!first) sb.append(',');
            first = false;
            sb.append('"').append(f).append('"');
        }
        sb.append("],\"documents\":[");
        first = true;
        for (String d : documents.keySet()) {
            if (!first) sb.append(',');
            first = false;
            sb.append('"').append(d).append('"');
        }
        sb.append("]}");
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private void validateName(String n) {
        if (n == null || n.isEmpty() || n.contains("/")) {
            throw new IllegalArgumentException("invalid name");
        }
    }
}
