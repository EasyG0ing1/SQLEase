package com.simtechdata;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

/**
 * This is the class that you will use to create a SQLite database and interact with it
 */
public class SQLite {

    private static final String baseConnString = "jdbc:sqlite:";
    private final String databaseName;
    private final String filePath;
    private final String filename;
    private final String schema;
    private final boolean useForeignKeys;
    private File sqlIteFile;

    /**
     * Builder class that you will use to set up your database connection and interact with the database.
     */
    public static class Builder {
        private String databaseName;
        private String folderPath;
        private String filename;
        private String schema;
        private boolean useForeignKeys = false;

        /**
         * Standard Builder constructor.
         * @param folderPath String
         * @param filename String
         * @param schema String
         */
        public Builder(String folderPath, String filename, String schema) {
            this.folderPath = folderPath;
            this.filename = filename;
            this.schema = schema;
        }

        /**
         * Standard Builder constructor.
         * @param folderPath String
         * @param filename String
         * @param schema String
         * @param useForeignKeys String
         */
        public Builder(String folderPath, String filename, String schema, boolean useForeignKeys) {
            this.folderPath = folderPath;
            this.filename = filename;
            this.schema = schema;
            this.useForeignKeys = useForeignKeys;
        }

        /**
         * Default Builder constructor
         */
        public Builder() {
        }


        /**
         * This sets the name of the database
         * @param databaseName String
         * @return Builder
         */
        public Builder setDatabaseName(String databaseName) {
            this.databaseName = databaseName;
            return this;
        }

        /**
         * Sets the path to the folder that contains the SQLite database file
         * @param folderPath String
         * @return Builder
         */
        public Builder setFolderPath(String folderPath) {
            this.folderPath = folderPath;
            return this;
        }

        /**
         * Sets the filename of the SQLite database filo
         * @param filename String
         * @return Builder
         */
        public Builder setFileName(String filename) {
            this.filename = filename;
            return this;
        }

        /**
         * Sets the name of the SQLite schema
         * @param schema String
         * @return Builder
         */
        public Builder setSchema(String schema) {
            this.schema = schema;
            return this;
        }

        /**
         * If your database has any foreign key relationships in it, set this to true. That way, each time you get a Connection object, it will contain the correct setting so that foreign key relationships and rules are followed
         * @param useForeignKeys boolean
         * @return Builder
         */
        public Builder useForeignKeys(boolean useForeignKeys) {
            this.useForeignKeys = useForeignKeys;
            return this;
        }

        /**
         * This must end your build sentence as it returns the SQLite instance
         * @return SQLite instance
         */
        public SQLite build()  {
            try {
                return new SQLite(this);
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * This private constructor is used by the Builder. It will set all variables and it will check to see if the database file exists and it will attempt to create it if it does not exist.
     * @param b Builder
     * @throws IOException Errors handled in the build() method of Builder
     * @throws SQLException Errors handled in the build() method of Builder
     */
    private SQLite(Builder b) throws IOException, SQLException {
        this.databaseName   = b.databaseName;
        this.filePath       = b.folderPath;
        this.filename       = b.filename;
        this.schema         = b.schema;
        this.useForeignKeys = b.useForeignKeys;

        if(!Paths.get(filePath, filename).toFile().exists()) {
            if (!createNewDatabase()) {
                throw new IOException("Database file does not exist and could not be created: " + sqlIteFile.getAbsolutePath());
            }
        }
        else {
            this.sqlIteFile = Paths.get(filePath, filename).toFile();
        }
    }

    /**
     * This lets you get your Connection object any time you need to interact with the database.
     * @return Connection instance
     * @throws SQLException Error handling to be done in your code
     */
    public Connection getConn() throws SQLException {
        String connString = baseConnString + sqlIteFile.getAbsolutePath();
        Connection conn = DriverManager.getConnection(connString);
        conn.setAutoCommit(true);
        if(databaseName != null && !databaseName.isEmpty())
            conn.setSchema(databaseName);
        if(useForeignKeys)
            conn.prepareStatement("PRAGMA foreign_keys = ON;").execute();
        return conn;
    }

    /**
     * This is used by the private constructor to create the database file if it does not exist.
     * @return true if successful
     * @throws IOException
     * @throws SQLException
     */
    private boolean createNewDatabase() throws IOException, SQLException {
        Path filePath = Paths.get(this.filePath, this.filename);
        if(!filePath.getParent().toFile().exists()) {
            Files.createDirectories(filePath);
        }
        this.sqlIteFile = filePath.toFile();

        if(!sqlIteFile.exists()) {
            if(schema != null && !schema.isEmpty()) {
                Connection conn = getConn();
                String[] tables = schema.split("SPLIT");
                for (String table : tables) {
                    conn.createStatement().executeUpdate(table);
                }
                return true;
            }
            else {
                getConn();
                return true;
            }
        }
        return false;
    }

    /**
     * Use this method to run a SQL statement that affects change in your database. It will return true if successful
     * You can pass in multiple separate SQL commands by separating each command with a new line containing the word SPLIT
     * @param SQL String
     * @return true if successful
     */
    public boolean execUpdate(final String SQL) {
        try (Connection conn = getConn();
             Statement stmt = conn.createStatement()) {
            String[] parts = SQL.split("SPLIT");
            for(String part : parts) {
                stmt.executeUpdate(part);
            }
            return true;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Use this method to get a ResultSet object from a static SQL String.
     *
     * @param SQL String
     * @return ResultSet
     * @throws SQLException           Error handling to be done in your code
     * @throws ClassNotFoundException Error handling to be done in your code
     */
    public ResultSet getResultSet(final String SQL) throws SQLException, ClassNotFoundException {
        Connection conn = getConn();
        return conn.createStatement().executeQuery(SQL);
    }

    /**
     * This is the same method as execUpdate, only this one returns any errors back to you for handling.
     * You can pass in multiple separate SQL commands by separating each command with a new line containing the word SPLIT
     * @param SQL String
     * @return true if successful
     * @throws SQLException           Error handling to be done in your code
     * @throws ClassNotFoundException Error handling to be done in your code
     */
    public boolean writeUpdate(final String SQL) throws SQLException, ClassNotFoundException {
        Connection conn = getConn();
        Statement stmt = conn.createStatement();
        String[] parts = SQL.split("SPLIT");
        for (String part : parts) {
            stmt.executeUpdate(part);
        }
        stmt.close();
        conn.close();
        return true;
    }

    /**
     * This is a very simple method that will only give you the first hit of the first column in your SQL statement
     *
     * @param SQL String
     * @return String
     * @throws SQLException           Error handling to be done in your code
     */
    public String readData(final String SQL) throws SQLException {
        Connection conn = getConn();
        ResultSet rs = conn.createStatement().executeQuery(SQL);
        if(rs.next()) {
            return rs.getString(1);
        }
        return "";
    }

    /**
     * Returns the full path to the database file
     * @return String
     */
    public String getFilePath() {
        if(sqlIteFile != null)
            return sqlIteFile.getAbsolutePath();
        return "";
    }

    /**
     * Use this to delete your SQLite database file.
     * @return true if successful
     * @throws IOException Error handling to be done in your code
     */
    public boolean deleteFile() throws IOException {
        if(sqlIteFile != null && sqlIteFile.exists()) {
            Files.delete(sqlIteFile.toPath());
            return true;
        }
        return false;
    }
}
