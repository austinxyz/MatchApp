@echo off
REM Script to run the MatchApp with a UTR API token
REM Usage: run-with-token.bat your_utr_api_token

REM Check if token is provided
if "%~1"=="" (
  echo Error: UTR API token is required
  echo Usage: run-with-token.bat your_utr_api_token
  exit /b 1
)

REM Set the token as an environment variable
set UTR_API_TOKEN=%~1

REM Build the application
echo Building the application...
call mvn clean package -DskipTests

REM Run the application with the token
echo Running the application with the provided UTR API token...
java -jar target\matchapp-*.jar

REM Note: The token is only set for this session and will not persist after the script ends
