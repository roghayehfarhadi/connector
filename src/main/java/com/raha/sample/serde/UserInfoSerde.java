package com.raha.sample.serde;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raha.sample.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class UserInfoSerde implements Serde<UserInfo> {

    private final ObjectMapper objectMapper;

    @Override
    public Serializer<UserInfo> serializer() {
        return (String key, UserInfo data) -> {
            try {
                return objectMapper.writeValueAsBytes(data);
            } catch (JsonProcessingException e) {
                return new byte[]{0};
            }
        };
    }

    @Override
    public Deserializer<UserInfo> deserializer() {
        return (String key, byte[] data) -> {
            try {
                return this.objectMapper.readValue(data, UserInfo.class);
            } catch (IOException e) {
                return null;
            }
        };
    }
}
