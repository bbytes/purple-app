package com.bbytes.purple;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PurpleApplication.class)
@WebAppConfiguration
@ActiveProfiles("dev")
public class PurpleApplicationTests {

	@Test
	@Ignore
	public void contextLoads() {
	}

	
}
