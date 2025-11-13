package de.omegazirkel.risingworld.tools.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.omegazirkel.risingworld.tools.Logger;
import net.risingworld.api.Plugin;
import net.risingworld.api.World;
import net.risingworld.api.database.Database;

/**
 * A wrapper class for SQLite Database
 */
public class SQLite {
    private Plugin plugin = null;
    private Logger log = null;
    private Database db = null;

    /**
     *
     * @param plugin
     */
    public SQLite(Plugin plugin) {
        this.plugin = plugin;
        log = new Logger("[OZ.SQLite]", 0);
        initDatabase();
    }

    /**
     *
     * @param plugin
     * @param logLevel
     */
    public SQLite(Plugin plugin, int logLevel) {
        this.plugin = plugin;
        log = new Logger("[OZ.SQLite]", logLevel);
        initDatabase();
    }

    private void initDatabase() {
        if (db == null) {
            String path = plugin.getPath() + "/" + World.getName() + ".db";
            db = plugin.getSQLiteConnection(path);
            log.out("Connected to " + path, 0);
        }
    }

    public Database getRawDatabase() {
        return db;
    }

    public ResultSet executeQuery(String query) throws SQLException {
        initDatabase();
        try {
            return db.executeQuery(query);
        } catch (Exception e) {
            log.out(e.getMessage(), 911);
        }
        return null;
    }

    public void executeUpdate(String query) {
        initDatabase();
        try {
            db.executeUpdate(query);
        } catch (Exception e) {
            log.out(e.getMessage(), 911);
        }
    }

    public void execute(String query) {
        initDatabase();
        try {
            db.execute(query);
        } catch (Exception e) {
            log.out(e.getMessage(), 911);
        }
    }

    public void destroy() {
        try {
            db.close();
            db = null;
        } catch (Exception e) {
            log.out(e.getMessage(), 911);
        }
    }
}
