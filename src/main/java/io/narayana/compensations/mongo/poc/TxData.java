package io.narayana.compensations.mongo.poc;

/**
 * @author <a href="mailto:gytis@redhat.com">Gytis Trikleris</a>
 */
public class TxData {

    private final ConfirmInsert confirmInsert;

    private final CompensateInsert compensateInsert;

    public TxData(ConfirmInsert confirmInsert, CompensateInsert compensateInsert) {
        this.confirmInsert = confirmInsert;
        this.compensateInsert = compensateInsert;
    }

    /**
     * Recreate txdata from string.
     *
     * @param string
     * @return
     */
    public static TxData valueOf(String string) {
        String[] parts = string.split("confirmationHandler=");
        parts = parts[1].split(", compensationHandler=");

        if ("null".equals(parts[0]) && "null".equals(parts[1])) {
            return new TxData(null, null);
        }

        if ("null".equals(parts[0])) {
            return new TxData(null, CompensateInsert.valueOf(parts[1]));
        }

        if ("null".equals(parts[1])) {
            return new TxData(ConfirmInsert.valueOf(parts[0]), null);
        }

        return new TxData(ConfirmInsert.valueOf(parts[0]), CompensateInsert.valueOf(parts[1]));
    }

    /**
     * Serialise txdata to string, so that we could append it to the Mongo document.
     *
     * @return
     */
    public String toString() {
        return TxData.class.getSimpleName() + " - confirmationHandler=" + confirmInsert + ", compensationHandler="
                + compensateInsert;
    }

    public ConfirmInsert getConfirmInsert() {
        return confirmInsert;
    }

    public CompensateInsert getCompensateInsert() {
        return compensateInsert;
    }
}
