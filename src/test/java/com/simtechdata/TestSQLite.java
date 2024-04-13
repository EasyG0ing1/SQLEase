package com.simtechdata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSQLite {

    /**
     * Main method for this test class
     * @param args from command line - not used
     * @throws SQLException Error to be thrown back to the console
     * @throws ClassNotFoundException Error to be thrown back to the console
     */
    public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {
        Path path = Paths.get(System.getProperty("user.home"), ".SQLEaseTest");
        if(!path.toFile().exists()) {
            Files.createDirectories(path);
        }
        String filename = "test.sqlite";
        SQLite sqLite = new SQLite.Builder(path.toAbsolutePath().toString(), filename, getSchema()).build();
        System.out.println("SQLite file path:                  " + sqLite.getFilePath());
        Path filePath = path.resolve(filename);
        String expected = filePath.toAbsolutePath().toString();
        String actual = sqLite.getFilePath();
        assertEquals(expected, actual, "The file path should be '/Users/Michael/temp/test.sqlite'");

        String SQL = "INSERT INTO TestTable1 (Item1, Item2) VALUES('TestItem1','TestItem2');";
        boolean expectedSuccess = true;
        boolean success = sqLite.execUpdate(SQL);
        System.out.println("Result of data insert:             " + success);
        assertEquals(expectedSuccess, success, "The response should have been true");

        SQL = "SELECT Item1 FROM TestTable1";
        expected = "TestItem1";
        actual = sqLite.readData(SQL);
        System.out.println("Result of reading data from table: " + actual);
        assertEquals(expected, actual, "Result should have been TestItem1 but it was " + actual);

        success = sqLite.deleteFile();
        System.out.println("Result of deleting database file:  " + success);
        assertEquals(expectedSuccess, success, "The database file was not deleted");
    }



    /**
     * This is used to create a table that is used for writing and reading of data to and from the table to make sure everything is working correctly
     * @return String
     */
    private static String getSchema() {
        return """
                  CREATE TABLE "TestTable1" (
                  "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                  "Item1" TEXT NOT NULL ON CONFLICT IGNORE,
                  "Item2" TEXT NOT NULL ON CONFLICT IGNORE
                );
                SPLIT
                  CREATE TABLE "TestTable2" (
                  "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                  "Item1" TEXT NOT NULL ON CONFLICT IGNORE,
                  "Item2" TEXT NOT NULL ON CONFLICT IGNORE
                );
                SPLIT
                  CREATE TABLE "TestTable3" (
                  "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                  "Item1" TEXT NOT NULL ON CONFLICT IGNORE,
                  "Item2" TEXT NOT NULL ON CONFLICT IGNORE
                );
                """;
    }

}
