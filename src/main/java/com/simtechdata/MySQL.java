package com.simtechdata;

import java.sql.*;

/**
 * This is the class used for accessing a MySQL server on your network or locally on your machine
 */
public class MySQL {

    private String baseConnString = "jdbc:mysql://";
    private String host;
    private String port;
    private String schema;
    private String username;
    private String password;
    private String env;


    /**
     * Builder class
     */
    public static class Builder {
        private String host = "localhost";
        private String port = "3306";
        private String schema;
        private String username;
        private String password;
        private String env;

        /**
         * Fairly standard Builder constructor if you want to pass all parameters in at once. Passing in the value of `env` while leaving Password as "" will use the environment variable if it exists.
         * @param host String
         * @param port String
         * @param schema String
         * @param username String
         * @param password String
         * @param env String
         */
        public Builder(String host, String port, String schema, String username, String password, String env) {
            this.host = host;
            this.port = port;
            this.schema = schema;
            this.username = username;
            this.password = password;
            this.env = env;
        }

        /**
         * Default Builder constructor
         */
        public Builder() {}

        /**
         * Sets the host address. Can be IP address or FQDN
         *
         * @param host String
         * @return Builder
         */
        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        /**
         * Sets the port number for the MySQL Server. Default is 3306
         *
         * @param port String
         * @return Builder
         */
        public Builder setPort(String port) {
            this.port = port;
            return this;
        }

        /**
         * Sets the database schema, which must already exist. Otherwise, if it does not exist, you can leave this blank, then create your schema using the createSchema() method.
         *
         * @param schema String
         * @return Builder
         */
        public Builder setSchema(String schema) {
            this.schema = schema;
            return this;
        }

        /**
         * Sets the logon username for the MySQL Server
         *
         * @param username String
         * @return Builder
         */
        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        /**
         * Sets the password for the database. You can optionally use an environment variable for better security.
         *
         * @param password String
         * @return Builder
         */
        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        /**
         * Sets the environment variable that contains the password. For example, if your environment variable is named `SQL_PASSWORD` you would set it like this .setPasswordEnv("SQL_PASSWORD")
         *
         * @param env String
         * @return Builder
         */
        public Builder setPasswordEnv(String env) {
            this.env = env;
            return this;
        }

        /**
         * This must end your builder sentence as it returns the MySQL class instance.
         *
         * @return MySQL
         */
        public MySQL build() {
            return new MySQL(this);
        }
    }

    /**
     * Private constructor used by the Builder class
     * @param b Builder
     */
    private MySQL(Builder b) {
        this.host = b.host;
        this.port = b.port;
        this.schema = b.schema;
        this.username = b.username;
        this.password = b.password;
        this.env = b.env;
        String NL = System.getProperty("line.separator");
        boolean noPort = this.port == null || this.port.isEmpty();
        boolean noHost = this.host == null || this.host.isEmpty();
        boolean noUsername = this.username == null || this.username.isEmpty();
        boolean noPassword = this.password == null || this.password.isEmpty();
        boolean noEnv = this.env == null || this.env.isEmpty();
        boolean noSecurity = noPassword && noEnv;

        if (!noEnv) {
            password = System.getenv(env);
        }

        StringBuilder sb = new StringBuilder();
        if (noPort)
            sb.append("\t- No port number provided").append(NL);
        if (noHost)
            sb.append("\t- No host provided").append(NL);
        if (noUsername)
            sb.append("\t- No username provided").append(NL);
        if (noSecurity)
            sb.append("\t- No password provided").append(NL);
        if (!sb.toString().isEmpty()) {
            throw new RuntimeException(sb.toString());
        }
    }

    /**
     * This lets you get your Connection object any time you need to interact with the database.
     *
     * @return Connection
     * @throws ClassNotFoundException Error handling to be done in your code
     * @throws SQLException           Error handling to be done in your code
     */
    public Connection getConn() throws ClassNotFoundException, SQLException {
        String password = this.password;
        Class.forName("com.mysql.cj.jdbc.Driver");
        String connString = baseConnString + host + ":" + port + "/" + schema;
        if (env != null) {
            password = System.getenv(env);
        }
        return DriverManager.getConnection(connString, username, password);
    }

    /**
     * Use this to create a schema in your MySQL server. returns true if successful.
     *
     * @param schema Just the name of the schema is all you need to pass into the argument
     * @return boolean
     */
    public boolean createSchema(String schema) {
        this.schema = schema;
        String url = baseConnString + host + ":" + port + "/?user=" + username + "&" + "password=" + password;

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {

            String SQL = "CREATE SCHEMA IF NOT EXISTS " + this.schema + ";";
            stmt.execute(SQL);
            return true;

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Use this method to run a SQL statement that affects change in your database. It will return true if successful
     * You can pass in multiple separate SQL commands by separating each command with a new line containing the word SPLIT
     * @param SQL Your SQL string
     * @return boolean
     */
    public boolean execUpdate(String SQL) {
        try (Connection conn = getConn();
             Statement stmt = conn.createStatement()) {

            String[] parts = SQL.split("SPLIT");
            for (String part : parts) {
                stmt.executeUpdate(part);
            }
            return true;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        catch (ClassNotFoundException e) {
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
    public ResultSet getResultSet(String SQL) throws SQLException, ClassNotFoundException {
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
     * @throws ClassNotFoundException Error handling to be done in your code
     */
    public String readData(final String SQL) throws SQLException, ClassNotFoundException {
        Connection conn = getConn();
        ResultSet rs = conn.createStatement().executeQuery(SQL);
        if (rs.next()) {
            return rs.getString(1);
        }
        return "";
    }
}
