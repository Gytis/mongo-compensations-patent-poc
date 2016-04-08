package io.narayana.compensations.mongo.poc;

import com.mongodb.MongoClient;
import org.bson.Document;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import java.io.File;
import java.time.LocalTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author <a href="mailto:gytis@redhat.com">Gytis Trikleris</a>
 */
@RunWith(Arquillian.class)
public class ClientTest {

    @Inject
    private Client client;

    @Deployment
    public static WebArchive createTestArchive() {
        File lib = Maven.resolver().loadPomFromFile("pom.xml").resolve("org.mongodb:mongo-java-driver:3.2.2")
                .withoutTransitivity().asSingleFile();

        return ShrinkWrap.create(WebArchive.class, "test.war").addPackages(true, Client.class.getPackage().getName())
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml").addAsLibraries(lib);
    }

    @Before
    public void resetAccountData() throws Exception {
        new MongoClient(Resources.IP, Resources.PORT).getDatabase(Resources.DATABASE).drop();
    }

    @Test
    public void dummyTest() throws Exception {
        Document original = new Document("timestamp", LocalTime.now().toString());

        client.insertSerialisedHandler(original);
        Document fromDatabase = client.getSerialisedHandler(original);

        assertEquals(original.getString("timestamp"), fromDatabase.getString("timestamp"));
        assertNotNull(fromDatabase.getString("txdata"));

        DummyCompensationHandler handler = DummyCompensationHandler.valueOf(fromDatabase.getString("txdata"));
        handler.compensate();
    }

}
