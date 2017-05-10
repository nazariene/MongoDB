package course;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.mongodb.client.model.Filters.in;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            new Main("mongodb://localhost");
        } else {
            new Main(args[0]);
        }
    }

    public Main(String mongoUriString) throws IOException {
        //Choose one of the options
        OptionOne(mongoUriString);
    }

    private void OptionOne(String mongoUriString) throws IOException {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoUriString));
        final MongoDatabase mongoDb = mongoClient.getDatabase("exam7");
        final MongoCollection<Document> albumsCollection = mongoDb.getCollection("albums");
        final MongoCollection<Document> imagesCollection = mongoDb.getCollection("images");

        List<Document> images = imagesCollection.find().into(new LinkedList<Document>());

        for (Document image : images) {
            Object id = image.getInteger("_id");
            List<Document> albums = albumsCollection.find(new Document("images", id)).into(new LinkedList<Document>());
            if (albums.isEmpty()) {
                imagesCollection.deleteOne(new Document("_id", id));
            }
        }
    }
}
