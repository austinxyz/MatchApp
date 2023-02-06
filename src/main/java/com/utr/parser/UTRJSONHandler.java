package com.utr.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.json.JsonParseException;
import org.springframework.boot.json.JsonParserFactory;

import java.util.List;
import java.util.Map;

public class UTRJSONHandler {

    private static final Logger logger = LoggerFactory.getLogger(UTRJSONHandler.class);

    Map<String, Object> parseJsonMap(String jsonString) {
        Map<String, Object> returnMap = null;
        try {
            returnMap = JsonParserFactory.getJsonParser().parseMap(jsonString);
        } catch (JsonParseException exception) {
            logger.debug(exception.toString());
            logger.debug("Failed to parse Json string :" + jsonString);
        }
        return returnMap;
    }

    List<Object> parseJsonList(String jsonString) {
        List<Object> returnList = null;
        try {
            returnList = JsonParserFactory.getJsonParser().parseList(jsonString);
        } catch (JsonParseException exception) {
            logger.debug(exception.toString());
            logger.debug("Failed to parse Json string :" + jsonString);
        }
        return returnList;
    }
}
