package handlers;

import static spark.Spark.*;
import spark.Request;
import spark.Response;

import interfaces.FileSystemResource;
import interfaces.DocumentResource;
import interfaces.FolderResource;
import interfaces.MetadataResource;

public class Handler {
    private final FileSystemResource fs;
    private final java.util.function.BiFunction<String, byte[], DocumentResource> documentFactory;
    private final java.util.function.Function<String, MetadataResource> metadataFactory;

    public Handler(FileSystemResource fs, java.util.function.BiFunction<String, byte[], DocumentResource> documentFactory,
            java.util.function.Function<String, MetadataResource> metadataFactory) {
        this.fs = fs;
        this.documentFactory = documentFactory;
        this.metadataFactory = metadataFactory;
    }

    public void registerRoutes() {
        get("/v1/*", (req, res) -> handleGet(req, res));
        put("/v1/*", (req, res) -> handlePut(req, res));
        delete("/v1/*", (req, res) -> handleDelete(req, res));
    }

    private String pathFrom(Request req) {
        String raw = (req.splat() != null && req.splat().length > 0) ? req.splat()[0] : "";
        return sanitizePath(raw);
    }
    
    private Object handleGet(Request req, Response res) {
        String path = pathFrom(req);
        try {
            DocumentResource doc = fs.getDocument(path);
            res.type("application/octet-stream");
            return new String(doc.getRawBytes());
        } catch (IllegalArgumentException e) {
            try {
                FolderResource folder = fs.getFolder(path);
                res.type("application/json");
                return new String(folder.toBytes());
            } catch (IllegalArgumentException e2) {
                res.status(404);
                return "Resource not found";
            }
        }
    }
    
    private Object handlePut(Request req, Response res) {
        String path = pathFrom(req);
        String mode = req.queryParams("type");
        String name = req.queryParams("name");
    
        if (mode == null || name == null) {
            res.status(400);
            return "Missing 'type' or 'name' parameter";
        }
    
        try {
            if (mode.equalsIgnoreCase("folder")) {
                fs.putFolder(path, name);
                res.status(201);
                return "Folder created";
            } else if (mode.equalsIgnoreCase("document")) {
                byte[] body = req.bodyAsBytes();
                DocumentResource doc = documentFactory.apply(name, body);
                fs.putDocument(path, name, doc);
                res.status(201);
                return "Document created";
            } else {
                res.status(400);
                return "Invalid type";
            }
        } catch (IllegalArgumentException e) {
            res.status(400);
            return e.getMessage();
        } catch (Exception e) {
            res.status(500);
            return "Internal server error";
        }
    }
    
    private Object handleDelete(Request req, Response res) {
        String path = pathFrom(req);
        try {
            if (fs.deleteDocument(path)) {
                res.status(200);
                return "Document deleted";
            }
            if (fs.deleteFolder(path)) {
                res.status(200);
                return "Folder deleted";
            }
            res.status(404);
            return "Resource not found";
        } catch (IllegalArgumentException e) {
            res.status(400);
            return e.getMessage();
        } catch (Exception e) {
            res.status(500);
            return "Internal server error";
        }
    }
    
    private String sanitizePath(String raw) {
        if (raw == null) return "";
        if (raw.startsWith("/")) raw = raw.substring(1);
        if (raw.endsWith("/")) raw = raw.substring(0, raw.length() - 1);
        return raw;
    }
}
