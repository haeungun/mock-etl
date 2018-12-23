package com.example.mocketl.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConnectionManager {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);

    private static final String JDBC_DRIVER = "org.sqlite.JDBC";

    private final String dbUrl;

    /**
     *  TODO(haeungun): Remove this lock
     *  if user use SQLite3 of single thread compiled version,
     *  SQLite is unsafe to use in more than a single thread at once.
     *  reference: https://www.sqlite.org/threadsafe.html
     */
    private AtomicBoolean isLocked;

    static {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage());
        }
    }

    public ConnectionManager(String dbUrl) {
        this.dbUrl = dbUrl;
        this.isLocked = new AtomicBoolean(false);
    }

    /**
     * Get jdbc connection by {@code dbUrl}
     *
     * @return connection
     * @throws SQLException if failed to connect
     */
    public Connection getConnection() throws SQLException {
        this.lock();
        return DriverManager.getConnection(this.dbUrl);
    }

    /**
     * Close database resource
     *
     * @param connection to close
     */
    public void close(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
            this.unlock();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Close database resource
     *
     * @param connection to close
     * @param preparedStatement to close
     */
    public void close(Connection connection, PreparedStatement preparedStatement) {
        try {
            if (preparedStatement != null)
                preparedStatement.close();

            this.close(connection);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Close database resource
     *
     * @param connection to close
     * @param preparedStatement to close
     * @param resultSet to close
     */
    public void close(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) {
        try {
            if (resultSet != null)
                resultSet.close();

            this.close(connection, preparedStatement);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    private boolean lock() {
        return this.isLocked.compareAndSet(false, true);
    }

    private boolean unlock() {
        return this.isLocked.compareAndSet(true, false);
    }
}
