package io.github._3xhaust.util;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 * Thin wrapper around {@link java.util.logging.Logger} so the framework has a single
 * place to configure log formatting and log levels without pulling in a third-party
 * logging dependency.
 */
public final class Logger {
    private static final java.util.logging.Logger ROOT = java.util.logging.Logger.getLogger("javaui");
    private static volatile boolean configured;

    private final java.util.logging.Logger delegate;

    private Logger(java.util.logging.Logger delegate) {
        this.delegate = delegate;
    }

    public static Logger get(Class<?> cls) {
        ensureConfigured();
        return new Logger(java.util.logging.Logger.getLogger(cls.getName()));
    }

    public static Logger get(String name) {
        ensureConfigured();
        return new Logger(java.util.logging.Logger.getLogger(name));
    }

    public static void setLevel(Level level) {
        ensureConfigured();
        ROOT.setLevel(level);
        for (var h : ROOT.getHandlers()) h.setLevel(level);
    }

    private static synchronized void ensureConfigured() {
        if (configured) return;
        configured = true;
        ROOT.setUseParentHandlers(false);
        for (var h : ROOT.getHandlers()) ROOT.removeHandler(h);

        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);
        ch.setFormatter(new SimpleFormatter() {
            @Override
            public synchronized String format(LogRecord r) {
                return String.format("[%s] %s %s%n",
                        r.getLevel().getName(),
                        r.getLoggerName(),
                        formatMessage(r));
            }
        });
        ROOT.addHandler(ch);
        ROOT.setLevel(Level.INFO);

        // route framework loggers to our root
        java.util.logging.Logger.getLogger("io.github._3xhaust").setParent(ROOT);
    }

    public void info(String msg) { delegate.info(msg); }
    public void warn(String msg) { delegate.warning(msg); }
    public void warn(String msg, Throwable t) { delegate.log(Level.WARNING, msg, t); }
    public void error(String msg) { delegate.severe(msg); }
    public void error(String msg, Throwable t) { delegate.log(Level.SEVERE, msg, t); }
    public void debug(String msg) { delegate.fine(msg); }
}
