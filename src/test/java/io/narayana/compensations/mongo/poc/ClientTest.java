package io.narayana.compensations.mongo.poc;

import com.mongodb.MongoClient;
import org.bson.Document;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.narayana.compensations.impl.BAControllerFactory;
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
import static org.junit.Assert.assertNull;

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
        new MongoClient(Resources.MONGO_IP, Resources.MONGO_PORT).getDatabase(Resources.DATABASE).drop();
    }

    @Test
    public void testClose() throws Exception {
        BAControllerFactory.getInstance().beginBusinessActivity();
        Document original = new Document("timestamp", LocalTime.now().toString());
        client.insertDocuments(original);
        BAControllerFactory.getInstance().closeBusinessActivity();

        Document fromDatabase = client.getDocument(original);
        assertEquals(original.getString("timestamp"), fromDatabase.getString("timestamp"));
        assertNull(fromDatabase.getString("txdata"));
        assertNotNull(fromDatabase.getString("id"));
    }

    @Test
    public void testCancel() throws Exception {
        BAControllerFactory.getInstance().beginBusinessActivity();
        Document original = new Document("timestamp", LocalTime.now().toString());
        client.insertDocuments(original);
        BAControllerFactory.getInstance().cancelBusinessActivity();

        assertNull(client.getDocument(original));
    }

}
