package com.github.shanecba.capstone;

import com.plaid.client.ApiClient;
import com.plaid.client.request.PlaidApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;

@SpringBootApplication
public class CapstoneApiApplication {

	@Value("${plaid.clientId}")
	private String clientId;
	@Value("${plaid.secret}")
	private String secret;
	@Value("${plaid.environment}")
	private String environment;

	public static void main(String[] args) {
		SpringApplication.run(CapstoneApiApplication.class, args);
	}

	@Bean("plaidClient")
	public PlaidApi generatePlaidClient() {
		HashMap<String, String> apiKeys = new HashMap<String,String>();

		apiKeys.put("clientId", clientId);
		apiKeys.put("secret", secret);

		ApiClient plaidApiClient = new ApiClient(apiKeys);

		switch (environment) {
			case "Development":
				plaidApiClient.setPlaidAdapter(ApiClient.Development);
			case "Sandbox":
				plaidApiClient.setPlaidAdapter(ApiClient.Sandbox);
		}
		return plaidApiClient.createService(PlaidApi.class);
	}

}
