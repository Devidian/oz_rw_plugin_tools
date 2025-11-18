package de.omegazirkel.risingworld.tools;

import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.NullConfiguration;

public class OZLogger {

    private static final Map<String, OZLogger> INSTANCES = new ConcurrentHashMap<>();
    private static final boolean DEBUG_MODE = false;
    private static final String CONFIG_FILE = "oz-log4j2.xml";

    public static final int LEVEL_VERBOSE = 0;
    public static final int LEVEL_DEBUG = 10;
    public static final int LEVEL_INFO = 20;
    public static final int LEVEL_WARN = 30;
    public static final int LEVEL_ERROR = 40;
    public static final int LEVEL_FATAL = 50;

    private int level = LEVEL_DEBUG;

    private Logger logger() {
        return ctx.getLogger(loggerName);
    }

    private LoggerContext ctx;
    private final String loggerName;

    public OZLogger(String pluginName, boolean registerOnly) {
        this.loggerName = pluginName;
        if (!registerOnly) {
            init();
        }
    }

    public OZLogger(String pluginName) {
        this(pluginName, false);
    }

    private void init() {
        System.setProperty("logPath", "Logs");
        System.setProperty("pluginName", loggerName);
        try {
            ClassLoader cl = OZLogger.class.getClassLoader();

            if (DEBUG_MODE) {
                Enumeration<URL> urls = cl.getResources(CONFIG_FILE);
                while (urls.hasMoreElements()) {
                    System.out.println("[OZLOG] FOUND (candidate): " + urls.nextElement());
                }
            }

            URL configUrl = cl.getResource(CONFIG_FILE);
            if (configUrl == null) {
                throw new IllegalStateException("Log4j2 config not found: " + CONFIG_FILE);
            }

            if (DEBUG_MODE)
                System.out.println("[OZLOG] USING EXACT CONFIG: " + configUrl + " for " + loggerName);

            try (InputStream is = configUrl.openStream()) {
                ConfigurationSource source = new ConfigurationSource(is, configUrl);

                if (DEBUG_MODE)
                    System.setProperty("log4j2.debug", "true");

                LoggerContext ctx = new LoggerContext(loggerName);
                Configuration config = ConfigurationFactory.getInstance().getConfiguration(ctx, source);
                ctx.start(config);
                this.ctx = ctx;
            }

        } catch (Exception e) {
            System.out.println("[!SYS!] Failed to initialize logger: " + e.getMessage());
            e.printStackTrace();
            this.ctx = new LoggerContext(loggerName);
            this.ctx.start(new NullConfiguration());
        }
    }

    public static OZLogger getInstance(String pluginName) {
        return INSTANCES.computeIfAbsent(pluginName, k -> {
            OZLogger logger = new OZLogger(pluginName, true); // "true" = nur Map-Registrierung
            logger.init(); // Init separat, außerhalb der computeIfAbsent
            return logger;
        });
    }

    public OZLogger setLogLevel(int toLevel) {
        level = toLevel;
        return this;
    }

    public void debug(String message) {
        logSafe(logger()::debug, message);
    }

    public void info(String message) {
        logSafe(logger()::info, message);
    }

    public void warn(String message) {
        logSafe(logger()::warn, message);
    }

    public void error(String message) {
        logSafe(logger()::error, message);
    }

    public void fatal(String message) {
        logSafe(logger()::fatal, message);
    }

    private void logSafe(java.util.function.Consumer<String> logMethod, String message) {
        if (ctx == null || !ctx.isStarted()) {
            System.out.println("[!SYS!] " + message);
        } else {
            logMethod.accept(message);
        }
    }

    // for non simple message logs like exceptions
    // maybe create wrapper methods if one kind is often used
    public Logger getLogger() {
        return logger();
    }

    // DEPRECATED

    /**
     *
     * @param message
     */
    public void out(String message) {
        this.out(message, LEVEL_DEBUG);
    }

    /**
     *
     * @param message
     * @param level
     */
    public void out(String message, int lvl) {
        if (lvl >= this.level) {
            if (lvl < LEVEL_INFO)
                debug(message);
            else if (lvl < LEVEL_WARN)
                info(message);
            else if (lvl < LEVEL_ERROR)
                warn(message);
            else if (lvl < LEVEL_FATAL)
                error(message);
            else
                fatal(message);
        }
    }
}