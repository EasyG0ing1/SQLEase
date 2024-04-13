package com.simtechdata;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestMySQL {

    /**
     * Main method for this test class
     * @param args from command line - not used
     * @throws SQLException Error to be thrown back to the console
     * @throws ClassNotFoundException Error to be thrown back to the console
     */
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        MySQL mySQL = new MySQL.Builder()
                .setUsername("michael")
                .setPasswordEnv("SQL_PASS")
                .build();
        boolean expectedSuccess = true;
        boolean success = mySQL.createSchema("TestNewDB");
        System.out.println("Creating schema TestNewDB: " + success);
        assertEquals(expectedSuccess, success, "Creating Schema should have been true");

        success = mySQL.writeUpdate(getSchema());
        System.out.println("Creating Table TestTable:  " + success);
        assertEquals(expectedSuccess, success, "Creating Table should have been true");

        String SQL = "INSERT INTO TestTable (TestItem1) VALUES('TestItem');";
        success = mySQL.writeUpdate(SQL);
        System.out.println("Result of data insert:     " + success);
        assertEquals(expectedSuccess, success, "The response for inserting data into the table should have been true");

        SQL = "SELECT TestItem1 FROM TestTable";
        String expected = "TestItem";
        String actual = mySQL.readData(SQL);
        System.out.println("Data read from table:      " + actual);
        assertEquals(expected, actual, "Result for reading data from table should have been TestItem but it was " + actual);

        SQL = "DROP SCHEMA TestNewDB;";
        success = mySQL.writeUpdate(SQL);
        System.out.println("Schema TestNewDB DROPPED:  " + success);
        assertEquals(expectedSuccess, success, "The response to dropping the schema should have been true");
    }


    /**
     * This is used to create a table that is used for writing and reading of data to and from the table to make sure everything is working correctly
     * @return String
     */
    private static String getSchema() {
        return """
                USE `TestNewDB`;
                SPLIT
                CREATE TABLE IF NOT EXISTS `TestNewDB`.`TestTable` (
                  `id` INT NOT NULL AUTO_INCREMENT,
                  `TestItem1` VARCHAR(45) NULL,
                  PRIMARY KEY (`id`))
                ENGINE = InnoDB;
                """;
    }

}
