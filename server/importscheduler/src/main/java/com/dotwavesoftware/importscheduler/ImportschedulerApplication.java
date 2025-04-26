package com.dotwavesoftware.importscheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;


@SpringBootApplication
public class ImportschedulerApplication {

		public static void main(String[] args) {
			 ConfigurableApplicationContext context = SpringApplication.run(ImportschedulerApplication.class, args);
			        // Access the environment
					Environment env = context.getEnvironment();

					// Get the property value
					String token = env.getProperty("api.hubspot.accesstoken");
			
					// Test it
				    System.out.println("HubSpot Access Token: " + token);
				
	}

}
