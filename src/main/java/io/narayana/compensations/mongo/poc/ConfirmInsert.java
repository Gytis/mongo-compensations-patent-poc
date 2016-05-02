package io.narayana.compensations.mongo.poc;

import org.bson.Document;
import org.jboss.narayana.compensations.api.ConfirmationHandler;

/**
 * @author <a href="mailto:gytis@redhat.com">Gytis Trikleris</a>
 */
public class ConfirmInsert implements ConfirmationHandler {

    private String key;

    private String value;

    public ConfirmInsert(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Recreate handler from string.
     *
     * @param string
     * @return
     */
    public static ConfirmInsert valueOf(String string) {
        String[] parts = string.split(" - ");
        parts = parts[1].split("=");

        return new ConfirmInsert(parts[0], parts[1]);
    }

    /**
     * Remove txdata field from the document.
     */
    @Override
    public void confirm() {
        System.out.println("Confirming. " + this);

        new Resources().getMongoCollection().updateOne(new Document(key, value),
                new Document("$unset", new Document("txdata", "")));
    }

    /**
     * Serialise handler to string, so that we could append it to the Mongo document.
     * 
     * @return
     */
    public String toString() {
        return ConfirmInsert.class.getSimpleName() + " - " + key + "=" + value;
    }
}