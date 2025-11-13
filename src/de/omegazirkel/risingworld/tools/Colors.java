package de.omegazirkel.risingworld.tools;

/**
 * This class is used to have a syncronized color theme through different
 * plugins
 */
public class Colors {

    public final String error = "[#FF0000]";
    public final String warning = "[#808000]";
    public final String okay = "[#00FF00]";
    public final String text = "[#EEEEEE]";
    public final String command = "[#997d4a]";
    public final String info = "[#0099ff]";
    public final String comment = "[#478c59]";

    protected static Colors C = new Colors();

    /**
     * 
     * @return
     */
    public static Colors getInstance() {
        return C;
    }

    /**
     * 
     */
    private Colors() {

    }
}