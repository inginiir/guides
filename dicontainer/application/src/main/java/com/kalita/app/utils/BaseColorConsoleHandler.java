package com.kalita.app.utils;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

abstract class BaseColorConsoleHandler extends ConsoleHandler {

    private static final String COLOR_RESET = "\u001b[0m";
    private static final String COLOR_WARNING = "\u001B[33m";
    private static final String COLOR_FINE = "\u001b[36m";
    private static final String COLOR_SEVERE = "\u001B[31m";

    String logRecordToString(LogRecord record) {
        Formatter f = getFormatter();
        String msg = f.format(record);

        String prefix;
        Level level = record.getLevel();
        if (level == Level.WARNING) {
            prefix = COLOR_WARNING;
        } else if (level == Level.INFO) {
            prefix = COLOR_FINE;
        } else if (level == Level.SEVERE) {
            prefix = COLOR_SEVERE;
        } else {
            prefix = COLOR_FINE;
        }
        return prefix + msg + COLOR_RESET;
    }
}
