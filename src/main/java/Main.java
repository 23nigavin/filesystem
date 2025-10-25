import handlers.Handler;
import filesystem.Root;
import interfaces.FileSystemResource;
import resources.Document;
import resources.Metadata;
import spark.Spark;

public class Main {
    public static void main(String[] args) {
        FileSystemResource fs = new Root();

        Handler handler = new Handler(
            fs,
            (name, body) -> new Document(name, body, new Metadata("system")),
            Metadata::new
        );

        Spark.port(8080);
        handler.registerRoutes();

        System.out.println("Server running on http://localhost:8080");
    }
}
