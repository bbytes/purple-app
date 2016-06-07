package com.bbytes.purple;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.bbytes.purple.database.TestMongoDatabaseConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { PurpleApplication.class, TestMongoDatabaseConfig.class })
@WebAppConfiguration
@ActiveProfiles({"test","saas"})
public class PurpleApplicationTests {

	
	@Test
	@Ignore
	public void contextLoads() {
	}

}
