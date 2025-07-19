#!/bin/bash

# Script to run the MatchApp with a UTR API token
# Usage: ./run-with-token.sh <your_utr_api_token>

# Check if token is provided
if [ -z "$1" ]; then
  echo "Error: UTR API token is required"
  echo "Usage: ./run-with-token.sh <your_utr_api_token>"
  exit 1
fi

# Set the token as an environment variable
export UTR_API_TOKEN="$1"

# Build the application
echo "Building the application..."
mvn clean package -DskipTests

# Run the application with the token
echo "Running the application with the provided UTR API token..."
java -jar target/matchapp-*.jar

# Note: The token is only set for this session and will not persist after the script ends
