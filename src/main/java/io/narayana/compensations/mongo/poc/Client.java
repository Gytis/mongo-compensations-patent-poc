package io.narayana.compensations.mongo.poc;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.jboss.narayana.compensations.api.Compensatable;

import javax.inject.Inject;
import java.time.LocalTime;

/**
 * @author <a href="mailto:gytis@redhat.com">Gytis Trikleris</a>
 */
public class Client {

    @Inject
    private MongoCollection<Document> collection;

    public Client() {
    }

    @Compensatable
    public void insertSerialisedHandler(Document document) throws Exception {
        DummyCompensationHandler handler = new DummyCompensationHandler(LocalTime.now());
        document.append("txdata", handler.toString());

        System.out.println("Client saving document to the database: " + document);

        collection.insertOne(document);
    }

    public Document getSerialisedHandler(Document filter) {
        Document document = collection.find(filter).first();

        System.out.println("Client returning document from the database: " + document);

        return document;
    }

}
