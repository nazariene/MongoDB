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

        OptionTwo(mongoUriString);
    }

    private void OptionOne(String mongoUriString) throws IOException {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoUriString));
        final MongoDatabase mongoDb = mongoClient.getDatabase("school");
        final MongoCollection<Document> studentsCollection = mongoDb.getCollection("students");

        List<Document> students = studentsCollection.find().filter(in("scores.type", Arrays.asList("homework"))).into(new LinkedList<Document>());
        for (Document student : students) {
            List<Document> scores = (List<Document>) student.get("scores");
            Double lowestScore = null;
            for (Document score : scores) {
                if (score.getString("type").equals("homework")) {
                    if (lowestScore == null || score.getDouble("score") < lowestScore) {
                        lowestScore = score.getDouble("score");
                    }
                }
                System.out.println(score.getString("type"));
            }

            Bson updateBson = Updates.pull("scores", new Document("score", lowestScore).append("type", "homework"));
            if (lowestScore != null) {
                studentsCollection.updateOne(new Document("_id", student.getInteger("_id")), updateBson);
            }
        }
    }

    private void OptionTwo(String mongoUriString) throws IOException {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoUriString));
        final MongoDatabase mongoDb = mongoClient.getDatabase("school");
        final MongoCollection<Document> studentsCollection = mongoDb.getCollection("students");

        List<Bson> aggregateOps = new LinkedList<Bson>();
        aggregateOps.add(Aggregates.unwind("$scores"));
        aggregateOps.add(Aggregates.match(new Document("scores.type", "homework")));
        aggregateOps.add(Aggregates.group("$_id", Accumulators.min("minscore", "$scores.score")));

        AggregateIterable<Document> students = studentsCollection.aggregate(aggregateOps);

        for (Document student : students) {
            studentsCollection.updateOne(new Document("_id", student.getInteger("_id")), Updates.pull("scores", new Document("score", student.getDouble("minscore")).append("type", "homework")));
        }
    }
}
