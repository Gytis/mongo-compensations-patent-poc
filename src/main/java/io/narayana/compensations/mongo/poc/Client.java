package io.narayana.compensations.mongo.poc;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.jboss.narayana.compensations.api.Compensatable;
import org.jboss.narayana.compensations.impl.BAControllerFactory;
import org.jboss.narayana.compensations.impl.ParticipantManager;

import javax.inject.Inject;
import java.util.UUID;

/**
 * @author <a href="mailto:gytis@redhat.com">Gytis Trikleris</a>
 */
public class Client {

    @Inject
    private MongoCollection<Document> collection;

    Client() {
    }

    @Compensatable
    public void insertDocuments(Document... documents) throws Exception {
        for (Document document : documents) {
            insertDocument(new Document(document));
        }
    }

    public Document getDocument(Document filter) {
        return collection.find(filter).first();
    }

    private void insertDocument(Document document) throws Exception {
        System.out.println("Creating txdata");
        // Generate ID so that handlers could find the correct document
        String id = UUID.randomUUID().toString();
        // Initialise confirmation and compensation handlers
        TxData txData = new TxData(new ConfirmInsert("id", id), new CompensateInsert("id", id));
        document.append("id", id);
        document.append("txdata", txData.toString());

        System.out.println("Registering txdata: " + txData);
        // Enlist handlers to the compensating transaction
        ParticipantManager participantManager = BAControllerFactory.getInstance().enlist(txData.getCompensateInsert(),
                txData.getConfirmInsert(), null);

        System.out.println("Saving document: " + document);
        collection.insertOne(document);
        // Notify TM that this participant has completed
        participantManager.completed();
    }

}
