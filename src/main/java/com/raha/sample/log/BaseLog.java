package com.raha.sample.log;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class BaseLog {
    private final LogType logType;
    private final LocalDateTime creationTime = LocalDateTime.now();
}
