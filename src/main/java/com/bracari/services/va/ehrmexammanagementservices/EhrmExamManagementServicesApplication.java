package com.bracari.services.va.ehrmexammanagementservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class,
		MailSenderAutoConfiguration.class})
@ComponentScan({"com.bracari.services.va.ehrmexammanagementservices.controllers"})
public class EhrmExamManagementServicesApplication {

	public static void main(String[] args) {
		System.setProperty("server.ssl.key-store-password", "${HTTP_CLIENT_SSL_KEYSTORE_PASSWORD}");
		System.setProperty("server.ssl.trust-store-password","${HTTP_CLIENT_SSL_TRUSTSTORE_PASSWORD}");
		System.setProperty("management.server.ssl.key-store-password","${HTTP_CLIENT_SSL_KEYSTORE_PASSWORD}");
		System.setProperty("management.server.ssl.trust-store-password","${HTTP_CLIENT_SSL_TRUSTSTORE_PASSWORD}");

		SpringApplication.run(EhrmExamManagementServicesApplication.class, args);
	}

}
