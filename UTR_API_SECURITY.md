# UTR API Security

This document outlines the secure approach for managing UTR API tokens in the MatchApp application.

## Background

The MatchApp application integrates with the Universal Tennis Rating (UTR) API to fetch player data, ratings, and other tennis-related information. This integration requires an authentication token that must be managed securely.

## Previous Implementation

In the previous implementation, the UTR API token was hardcoded directly in the source code:

```java
private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";
```

This approach had several security issues:
- The token was visible in the source code repository
- Updating the token required code changes
- The token was accessible to anyone with access to the codebase
- No separation between development and production environments

## New Secure Implementation

The new implementation provides multiple layers of security for managing the UTR API token:

### 1. Configuration Class

A dedicated `UTRApiConfig` class centralizes all UTR API configuration:

```java
@Configuration
@Component
public class UTRApiConfig {
    @Value("${utr.api.token:}")
    private String token;
    
    // Methods for token management
}
```

### 2. Externalized Configuration

The token is now stored in the `application.properties` file:

```properties
# UTR API Configuration
utr.api.token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 3. Environment Variable Override

For production environments, the token can be provided via an environment variable:

```java
@PostConstruct
public void init() {
    String envToken = environment.getProperty("UTR_API_TOKEN");
    if (envToken != null && !envToken.isEmpty()) {
        this.token = envToken;
    }
}
```

### 4. Token Validation

The application can now validate if the token is expired:

```java
public boolean isTokenExpired(String playerId) {
    // Implementation to check if token is valid
}
```

### 5. Token Management API

A REST API endpoint is available to check token status:

```
GET /api/utr/token/status
```

Response:
```json
{
  "tokenValid": true,
  "message": "UTR API token is valid"
}
```

Or if the token is expired:
```json
{
  "tokenValid": false,
  "message": "UTR API token is expired or invalid",
  "updateInstructions": "To update the UTR API token, you have two options:..."
}
```

## How to Update the Token

When the UTR API token expires, you have two options to update it:

### Option 1: Using application.properties (less secure, easier to update)

1. Log in to the UTR website (https://app.utrsports.net/)
2. Use browser developer tools to capture the Bearer token from any API request
3. Update the `utr.api.token` property in application.properties
4. Restart the application

### Option 2: Using environment variables (more secure, recommended for production)

1. Log in to the UTR website (https://app.utrsports.net/)
2. Use browser developer tools to capture the Bearer token from any API request
3. Set the environment variable `UTR_API_TOKEN` with the token value
4. Restart the application

## How to Capture the UTR API Token

1. Log in to the UTR website (https://app.utrsports.net/)
2. Open your browser's developer tools (F12 or right-click and select "Inspect")
3. Go to the "Network" tab
4. Perform any action on the UTR website that would trigger an API call
5. Look for requests to UTR API endpoints (e.g., `https://app.utrsports.net/api/v1/...`)
6. In the request headers, find the "Authorization" header
7. The token is the value after "Bearer " in the Authorization header

## Security Best Practices

1. **Never commit tokens to version control**: If using application.properties, consider using a template file in version control and keeping the actual file with secrets local.
2. **Use environment variables in production**: This is the most secure approach for production environments.
3. **Regularly rotate tokens**: UTR tokens expire periodically. Set up a process to update them before expiration.
4. **Limit access**: Only authorized personnel should have access to the token values.
5. **Monitor token usage**: Regularly check the token status using the provided API endpoint.

## Implementation Details

The secure token management is implemented across several files:

1. `UTRApiConfig.java`: Central configuration class for UTR API settings
2. `UTRParser.java`: Modified to use the configuration class instead of hardcoded tokens
3. `UTRApiController.java`: REST API for token management
4. `application.properties`: External configuration file

This implementation follows Spring Boot best practices for configuration management and security.
