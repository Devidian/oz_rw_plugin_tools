
package de.omegazirkel.risingworld;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import de.omegazirkel.tools.Logger;
import net.risingworld.api.Plugin;
import net.risingworld.api.events.Listener;

/**
 *
 * @author Maik 'Devidian' Laschober
 */
public class OZTools extends Plugin implements Listener {

    public static final String pluginVersion = "0.1.0";
    public static final String pluginName = "Omega Zirkel - Tools";
    public static final String pluginNameShort = "OZ.Tools";
    private static final Logger log = new Logger("[" + pluginNameShort + "]");

    static int logLevel = 0;

    /**
     *
     */
    @Override
    public void onEnable() {
        initSettings();
        log.out(pluginName+" enabled");
    }

    /**
     *
     */
    @Override
    public void onDisable() {
        log.out(pluginName+" disabled");
    }

    /**
     *
     */
    private void initSettings() {
        Properties settings = new Properties();
        FileInputStream in;
        try {
            in = new FileInputStream(getPath() + "/settings.properties");
            settings.load(new InputStreamReader(in, "UTF8"));
            in.close();
            // fill global values
            logLevel = Integer.parseInt(settings.getProperty("logLevel", "0"));
        } catch (IOException ex) {
            log.out("IOException on initSettings: " + ex.getMessage(), 100);
            // e.printStackTrace();
        } catch (NumberFormatException ex) {
            log.out("NumberFormatException on initSettings: " + ex.getMessage(), 100);
        } catch (Exception ex) {
            log.out("Exception on initSettings: " + ex.getMessage(), 100);
        }
    }

}