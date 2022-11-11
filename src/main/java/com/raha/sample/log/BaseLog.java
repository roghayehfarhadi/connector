package com.raha.sample.log;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class BaseLog {
    private final LogType logType;
}
