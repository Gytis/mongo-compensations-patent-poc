package io.narayana.compensations.mongo.poc;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import javax.enterprise.inject.Produces;

/**
 * @author <a href="mailto:gytis@redhat.com">Gytis Trikleris</a>
 */
public class Resources {

    public static final String IP = "192.168.99.100";

    public static final int PORT = 27017;

    public static final String DATABASE = "poc";

    public static final String COLLECTION = "poc";

    @Produces
    public MongoCollection<Document> getMongoCollection() {
        return new MongoClient(IP, PORT).getDatabase(DATABASE).getCollection(COLLECTION);
    }

}

