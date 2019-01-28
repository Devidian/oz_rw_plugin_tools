package de.omegazirkel.risingworld.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import net.risingworld.api.Plugin;

public class I18n {
    private Map<String, Properties> language = new HashMap<String, Properties>();
    private static final String defaultLanguage = "en";
    private Plugin plugin = null;
    private static Logger log = null;

    /**
     *
     * @param plugin
     */
    public I18n(Plugin plugin) {
        this.plugin = plugin;
        log = new Logger("[OZ.i18n]", 0);
        this.loadLanguageData(plugin.getPath());
    }

    /**
     *
     * @param plugin
     * @param logLevel
     */
    public I18n(Plugin plugin, int logLevel) {
        this.plugin = plugin;
        log = new Logger("[OZ.i18n]", logLevel);
        this.loadLanguageData(this.plugin.getPath());
    }

    /**
     *
     * @param pluginPath
     */
    private void loadLanguageData(String pluginPath) {
        log.out("Loading language files from " + pluginPath + "/i18n'");
        File folder = new File(pluginPath + "/i18n");
        File[] listOfFiles = folder.listFiles();
        FileInputStream in;
        try {
            log.out("Files found: " + listOfFiles.length, 0);
            for (int i = 0; i < listOfFiles.length; i++) {
                log.out("loading: " + listOfFiles[i].getName(), 0);
                if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith("properties")) {
                    String lang = listOfFiles[i].getName().substring(0, 2);
                    // log.out("lang: "+lang);
                    Properties lngProperties = new Properties();
                    try {
                        in = new FileInputStream(listOfFiles[i]);
                        lngProperties.load(new InputStreamReader(in, "UTF8"));
                        in.close();
                        this.language.put(lang.toLowerCase(), lngProperties);
                    } catch (FileNotFoundException e) {
                        log.out("Error: " + e.getMessage());
                        e.printStackTrace();
                    } catch (IOException e) {
                        log.out("Error: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            log.out("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * returns the language that is used for the given language
     *
     * @param lang
     * @return
     */
    public String getLanguageUsed(String lang) {
        if (!this.language.containsKey(lang.toLowerCase())) {
            return defaultLanguage + " (default Language)";
        } else {
            return lang;
        }
    }

    /**
     *
     * @return
     */
    public String getLanguageAvailable() {
        String[] keys = this.language.keySet().toArray(new String[0]);
        return String.join(", ", keys);
    }

    /**
     *
     * @param key
     * @param lang
     * @return
     */
    public String get(String key, String lang) {
        try {
            Properties lngDefaultProperties = this.language.get(defaultLanguage);
            Properties lngProperties = null;
            if (!this.language.containsKey(lang.toLowerCase())) {
                lngProperties = lngDefaultProperties;
            } else {
                lngProperties = this.language.get(lang.toLowerCase());
            }
            return lngProperties.getProperty(key, lngDefaultProperties.getProperty(key, key));
        } catch (Exception e) {
            log.out("Error: " + e.getMessage());
            e.printStackTrace();
            return key;
        }

    }

    /**
     *
     * @param key
     * @return
     */
    public String get(String key) {
        return this.get(key, defaultLanguage);
    }
}