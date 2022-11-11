package com.raha.sample.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageLogger {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(MessageLogger.class);

    private final ObjectMapper objectMapper;

    public void log(BaseLog logObject) {
        try {
            log.info(objectMapper.writeValueAsString(logObject));
        } catch (Exception ignored) {
        }
    }
}
