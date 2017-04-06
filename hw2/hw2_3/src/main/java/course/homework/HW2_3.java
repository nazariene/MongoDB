package course.homework;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.mongodb.client.model.Sorts.ascending;
import static course.homework.Helpers.printJson;

public class HW2_3 {

    public static void main(String[] args) {
        MongoClient client = new MongoClient();

        MongoDatabase database = client.getDatabase("students");
        final MongoCollection<Document> collection = database.getCollection("grades");

        Bson sort = ascending("student_id", "score");
        Bson filter = new Document("type", "homework");
        List<Document> all = collection.find(filter).sort(sort).into(new LinkedList<Document>());


        Integer id = null;
        int count = 0;
        for (Document doc : all) {
            Integer curId = doc.getInteger("student_id");
            if (id == null || !curId.equals(id)) {
                id = curId;
                System.out.println("ID = " + id + ", score = " + doc.getDouble("score"));
                collection.deleteOne(new Document("_id", doc.getObjectId("_id")));
                count++;
            }
        }

        System.out.println(count);

        long num = collection.count();
        System.out.println(num);
    }
}
