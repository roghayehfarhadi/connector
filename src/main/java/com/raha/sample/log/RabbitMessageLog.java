package com.raha.sample.log;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RabbitMessageLog extends BaseLog {

    private final String value;

    public RabbitMessageLog(LogType logType, String value) {
        super(logType);
        this.value = value;
    }
}
