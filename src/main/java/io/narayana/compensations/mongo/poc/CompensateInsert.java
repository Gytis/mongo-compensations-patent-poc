package io.narayana.compensations.mongo.poc;

import org.bson.Document;
import org.jboss.narayana.compensations.api.CompensationHandler;

/**
 * @author <a href="mailto:gytis@redhat.com">Gytis Trikleris</a>
 */
public class CompensateInsert implements CompensationHandler {

    private String key;

    private String value;

    public CompensateInsert(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Recreates handler from string.
     *
     * @param string
     * @return
     */
    public static CompensateInsert valueOf(String string) {
        String[] parts = string.split(" - ");
        parts = parts[1].split("=");

        return new CompensateInsert(parts[0], parts[1]);
    }

    /**
     * Remove previously created document.
     */
    @Override
    public void compensate() {
        System.out.println("Compensating. " + this);
        new Resources().getMongoCollection().deleteOne(new Document(key, value));
    }

    /**
     * Serialises handler to string so that we could append it to the Mongo document.
     * @return
     */
    public String toString() {
        return CompensateInsert.class.getSimpleName() + " - " + key + "=" + value;
    }
}