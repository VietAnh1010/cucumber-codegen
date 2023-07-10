package com.github.vanh1010.cucumber.codegen.logging;

import java.util.function.Supplier;

public interface Logger {

    /**
     * Log the {@code message} at error level.
     *
     * @param message The message to log.
     */
    void error(Supplier<String> message);

    /**
     * Log the {@code message} and {@code throwable} at error level.
     *
     * @param throwable The throwable to log.
     * @param message   The message to log.
     */
    void error(Throwable throwable, Supplier<String> message);

    /**
     * Log the {@code message} at warning level.
     *
     * @param message The message to log.
     */
    void warn(Supplier<String> message);

    /**
     * Log the {@code message} and {@code throwable} at warning level.
     *
     * @param throwable The throwable to log.
     * @param message   The message to log.
     */
    void warn(Throwable throwable, Supplier<String> message);

    /**
     * Log the {@code message} at info level.
     *
     * @param message The message to log.
     */
    void info(Supplier<String> message);

    /**
     * Log the {@code message} and {@code throwable} at info level.
     *
     * @param throwable The throwable to log.
     * @param message   The message to log.
     */
    void info(Throwable throwable, Supplier<String> message);

    /**
     * Log the {@code message} at config level.
     *
     * @param message The message to log.
     */
    void config(Supplier<String> message);

    /**
     * Log the {@code message} and {@code throwable} at config level.
     *
     * @param throwable The throwable to log.
     * @param message   The message to log.
     */
    void config(Throwable throwable, Supplier<String> message);

    /**
     * Log the {@code message} at debug level.
     *
     * @param message The message to log.
     */
    void debug(Supplier<String> message);

    /**
     * Log {@code message} and {@code throwable} at debug level.
     *
     * @param throwable The throwable to log.
     * @param message   The message to log.
     */
    void debug(Throwable throwable, Supplier<String> message);

    /**
     * Log the {@code message} at trace level.
     *
     * @param message The message to log.
     */
    void trace(Supplier<String> message);

    /**
     * Log the {@code message} and {@code throwable} at trace level.
     *
     * @param throwable The throwable to log.
     * @param message   The message to log.
     */
    void trace(Throwable throwable, Supplier<String> message);
}
