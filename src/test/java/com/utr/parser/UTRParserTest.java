package com.utr.parser;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Tests for the UTRParser class
 */
@SpringBootTest
@TestPropertySource(properties = {
    "utr.api.token=test-token-from-properties"
})
public class UTRParserTest {

    @Autowired
    private UTRParser utrParser;

    @MockBean
    private Environment environment;

    /**
     * Test that the token is retrieved from environment variable when available
     */
    @Test
    public void testGetTokenFromEnvironmentVariable() {
        // Mock environment to return a token
        when(environment.getProperty("UTR_API_TOKEN")).thenReturn("test-token-from-env");
        
        // Test the getToken method
        String token = (String) ReflectionTestUtils.invokeMethod(utrParser, "getToken");
        
        // Verify that the environment variable token is used
        assertEquals("test-token-from-env", token);
    }

    /**
     * Test that the token falls back to application.properties when no environment variable is set
     */
    @Test
    public void testGetTokenFromApplicationProperties() {
        // Mock environment to return null (no env variable set)
        when(environment.getProperty("UTR_API_TOKEN")).thenReturn(null);
        
        // Test the getToken method
        String token = (String) ReflectionTestUtils.invokeMethod(utrParser, "getToken");
        
        // Verify that the application.properties token is used as fallback
        assertEquals("test-token-from-properties", token);
    }

    /**
     * Test that an empty string is returned when both sources are empty
     */
    @Test
    public void testGetTokenFallbackWhenBothSourcesEmpty() {
        // Mock environment to return null (no env variable set)
        when(environment.getProperty("UTR_API_TOKEN")).thenReturn(null);
        
        // Set the configuredToken to empty
        ReflectionTestUtils.setField(utrParser, "configuredToken", "");
        
        // Test the getToken method
        String token = (String) ReflectionTestUtils.invokeMethod(utrParser, "getToken");
        
        // Verify that an empty string is returned when both sources are empty
        assertEquals("", token);
    }
}
