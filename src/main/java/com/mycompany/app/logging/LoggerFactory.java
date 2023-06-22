package com.mycompany.app.logging;

import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Change to something else later.
 */
public class LoggerFactory {

    private LoggerFactory() {}

    public static Logger getLogger(Class<?> clazz) {
        return new DelegatingLogger(clazz);
    }

    private static class DelegatingLogger implements Logger {

        private static final String THIS_LOGGER_CLASS = DelegatingLogger.class.getCanonicalName();

        private final Class<?> clazz;
        private final java.util.logging.Logger julLogger;

        private DelegatingLogger(Class<?> clazz) {
            this.clazz = clazz;
            julLogger = java.util.logging.Logger.getLogger(clazz.getCanonicalName());
        }

        @Override
        public void error(Supplier<String> message) {
            log(Level.SEVERE, null, message);
        }

        @Override
        public void error(Throwable throwable, Supplier<String> message) {
            log(Level.SEVERE, throwable, message);
        }

        @Override
        public void warn(Supplier<String> message) {
            log(Level.WARNING, null, message);
        }

        @Override
        public void warn(Throwable throwable, Supplier<String> message) {
            log(Level.WARNING, throwable, message);
        }

        @Override
        public void info(Supplier<String> message) {
            log(Level.INFO, null, message);
        }

        @Override
        public void info(Throwable throwable, Supplier<String> message) {
            log(Level.INFO, throwable, message);
        }

        @Override
        public void config(Supplier<String> message) {
            log(Level.CONFIG, null, message);
        }

        @Override
        public void config(Throwable throwable, Supplier<String> message) {
            log(Level.CONFIG, throwable, message);
        }

        @Override
        public void debug(Supplier<String> message) {
            log(Level.FINE, null, message);
        }

        @Override
        public void debug(Throwable throwable, Supplier<String> message) {
            log(Level.FINE, throwable, message);
        }

        @Override
        public void trace(Supplier<String> message) {
            log(Level.FINER, null, message);
        }

        @Override
        public void trace(Throwable throwable, Supplier<String> message) {
            log(Level.FINER, throwable, message);
        }

        private void log(Level level, Throwable throwable, Supplier<String> message) {
            boolean loggable = julLogger.isLoggable(level);
            if (loggable) {
                LogRecord logRecord = createLogRecord(level, throwable, message);
                julLogger.log(logRecord);
            }
        }

        private LogRecord createLogRecord(Level level, Throwable throwable, Supplier<String> message) {
            StackTraceElement[] stack = new Throwable().getStackTrace();
            String sourceClassName = null;
            String sourceMethodName = null;
            boolean found = false;
            for (StackTraceElement element : stack) {
                String className = element.getClass().getCanonicalName();
                if (THIS_LOGGER_CLASS.equals(className)) {
                    found = true;
                } else if (found) {
                    sourceClassName = className;
                    sourceMethodName = element.getMethodName();
                    break;
                }
            }
            LogRecord logRecord = new LogRecord(level, message == null ? null : message.get());
            logRecord.setLoggerName(clazz.getCanonicalName());
            logRecord.setThrown(throwable);
            logRecord.setSourceClassName(sourceClassName);
            logRecord.setSourceMethodName(sourceMethodName);
            logRecord.setResourceBundleName(julLogger.getResourceBundleName());
            logRecord.setResourceBundle(julLogger.getResourceBundle());
            return logRecord;
        }
    }
}