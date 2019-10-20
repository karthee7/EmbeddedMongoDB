package com.embedded.mongodb;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.RunWith;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

/**
 * @author Karthee
 *
 */
@SuppressWarnings("deprecation")
@RunWith(JUnit4ClassRunner.class)
public class EmbeddedMongoDBTest {

	private static final String LOCALHOST = "127.0.0.1";
	private static final String DB_NAME = "EMBEDDEDDB";
	private static final int MONGO_DB_PORT = 27028;
	private static DB db;
	private static MongodExecutable mongodExecutable = null;

	@BeforeClass
	public static void initializeDB() throws IOException {

		MongodStarter starter = MongodStarter.getDefaultInstance();

		IMongodConfig mongodConfig = new MongodConfigBuilder().version(Version.Main.PRODUCTION)
				.net(new Net(LOCALHOST, MONGO_DB_PORT, Network.localhostIsIPv6())).build();

		mongodExecutable = starter.prepare(mongodConfig);
		mongodExecutable.start();

		MongoClient mongo = new MongoClient(LOCALHOST, MONGO_DB_PORT);
		db = mongo.getDB(DB_NAME);
		DBCollection col = db.createCollection("COLLECTION", new BasicDBObject());
		col.save(new BasicDBObject("DATA", "TESTING"));

	}

	@AfterClass
	public static void shutdownDB() throws InterruptedException {
		if (mongodExecutable != null)
			mongodExecutable.stop();
	}

	@Test
	public void testFindByKey() {
		DBCollection dBCollection = db.getCollection("COLLECTION");
		DBCursor cursor = dBCollection.find();
		DBObject object = null;
		while (cursor.hasNext()) {
			object = cursor.next();
		}
		Assert.assertEquals(object.get("DATA"), "TESTING");
	}

}
