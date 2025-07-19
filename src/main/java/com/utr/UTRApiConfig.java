package com.utr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import com.utr.parser.TokenCheckParser;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import javax.annotation.PostConstruct;

/**
 * Configuration class for UTR API settings.
 * This class centralizes the configuration for UTR API access.
 */
@Configuration
@Component
public class UTRApiConfig {
    private static final Logger logger = LoggerFactory.getLogger(UTRApiConfig.class);
    
    @Value("${utr.api.token:}")
    private String token;
    
    @Autowired
    private Environment environment;
    
    /**
     * Initialize the configuration.
     * This method is called after dependency injection is done.
     * It checks for environment variables that might override the properties.
     */
    @PostConstruct
    public void init() {
        // Check if the token is provided as an environment variable
        String envToken = environment.getProperty("UTR_API_TOKEN");
        if (envToken != null && !envToken.isEmpty()) {
            logger.info("Using UTR API token from environment variable");
            this.token = envToken;
        }
    }

    /**
     * Get the UTR API token.
     * @return The UTR API token.
     */
    public String getToken() {
        return token;
    }
    
    /**
     * Check if the token is expired.
     * 
     * @param playerId A player ID to use for testing the token
     * @return true if the token is expired, false otherwise
     */
    public boolean isTokenExpired(String playerId) {
        // Create a simple HTTP request to test the token
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.set("Authorization", "Bearer " + token);
        
        String url = "https://app.utrsports.net/api/v1/player/" + playerId + "/profile";
        
        try {
            HttpEntity<String> entity = new HttpEntity<>("{}", headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            
            // If we get a response, check if it contains the expected data
            if (response.getStatusCode() == HttpStatus.OK) {
                TokenCheckParser parser = new TokenCheckParser(playerId);
                return parser.parseResult(response.getBody());
            }
            return true; // If not OK, assume token is expired
        } catch (Exception ex) {
            logger.error("Error checking token: " + ex.getMessage());
            return true; // Assume token is expired on error
        }
    }
    
    /**
     * Instructions for updating the token when it expires.
     * 
     * @return A string with instructions for updating the token
     */
    public String getTokenUpdateInstructions() {
        return "To update the UTR API token, you have two options:\n\n" +
               "Option 1: Using application.properties (less secure, easier to update):\n" +
               "1. Log in to the UTR website (https://app.utrsports.net/)\n" +
               "2. Use browser developer tools to capture the Bearer token from any API request\n" +
               "3. Update the 'utr.api.token' property in application.properties\n" +
               "4. Restart the application\n\n" +
               "Option 2: Using environment variables (more secure, recommended for production):\n" +
               "1. Log in to the UTR website (https://app.utrsports.net/)\n" +
               "2. Use browser developer tools to capture the Bearer token from any API request\n" +
               "3. Set the environment variable 'UTR_API_TOKEN' with the token value\n" +
               "4. Restart the application";
    }
}
