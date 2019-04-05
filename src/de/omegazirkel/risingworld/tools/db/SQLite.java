package de.omegazirkel.risingworld.tools.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.omegazirkel.risingworld.tools.Logger;
import net.risingworld.api.Plugin;
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
            String path = plugin.getPath() + "/" + plugin.getWorld().getName() + ".db";
            db = plugin.getSQLiteConnection(path);
            log.out("Connected to " + path, 0);
        }
    }

    public ResultSet executeQuery(String query) throws SQLException {
        initDatabase();
        return db.executeQuery(query);
    }

    public void executeUpdate(String query) {
        initDatabase();
        db.executeUpdate(query);
    }

    public void execute(String query) {
        initDatabase();
        db.execute(query);
    }

    public void destroy() {
        db.close();
        db = null;
    }
}