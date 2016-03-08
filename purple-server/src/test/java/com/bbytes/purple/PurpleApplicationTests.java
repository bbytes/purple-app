package com.bbytes.purple;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;

import com.mongodb.MongoClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PurpleApplication.class)
@WebAppConfiguration
public class PurpleApplicationTests {

	@Autowired
	MongoClient mongoClient;

	@Test
	public void contextLoads() {
	}

	@After
	/**
	 * Note : local is the default mongo db so dont remove it, if removed mongo will
	 * stop functioning
	 */
	public void clearTestCaseMongoDatabases() {
		for (String db : mongoClient.listDatabaseNames()) {
			if (!db.equalsIgnoreCase("local")) {
				// mongoClient.dropDatabase(db);
			}

		}
	}
}
