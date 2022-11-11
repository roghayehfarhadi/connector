package com.raha.sample.log;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KafkaMessageLog extends BaseLog {

    private final String key;
    private final String value;
    private final String topic;
    private final int partition;
    private final long offset;

    public KafkaMessageLog(String key, String value, LogType logType, String topic, int partition, long offset) {
        super(logType);
        this.key = key;
        this.value = value;
        this.topic = topic;
        this.partition = partition;
        this.offset = offset;
    }
}
