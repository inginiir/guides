package com.kalita.app.utils;

import java.util.logging.LogRecord;

public class AnsiColorConsoleHandler extends BaseColorConsoleHandler {

    @Override
    public void publish(LogRecord record) {
        System.err.print(logRecordToString(record));
        System.err.flush();
    }
}
