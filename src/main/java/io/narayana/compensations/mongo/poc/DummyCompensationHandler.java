package io.narayana.compensations.mongo.poc;

import org.jboss.narayana.compensations.api.CompensationHandler;

import java.io.Serializable;
import java.time.LocalTime;

/**
 * @author <a href="mailto:gytis@redhat.com">Gytis Trikleris</a>
 */
public class DummyCompensationHandler implements CompensationHandler, Serializable {

    private final LocalTime timestamp;

    public DummyCompensationHandler(LocalTime timestamp) {
        this.timestamp = timestamp;
    }

    public static DummyCompensationHandler valueOf(String string) {
        String[] parts = string.split(" - ");

        return new DummyCompensationHandler(LocalTime.parse(parts[1]));
    }

    @Override
    public void compensate() {
        System.out.println("Dummy compensation handler compensating at " + timestamp);
    }

    public String toString() {
        return DummyCompensationHandler.class.getSimpleName() + " - " + timestamp;
    }
}