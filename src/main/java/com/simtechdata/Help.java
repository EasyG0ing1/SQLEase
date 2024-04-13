package com.simtechdata;

/**
 * This is a class that provides the essential things you will need in order to use this class
 */
public class Help {


    /**
     * Calling this method will show you which dependencies are needed and what you need to add to your module-info file if your program is modular
     */
    public static void show() {
        String msg = """
        You will need these dependencies in order to use this library:
        
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.33</version>
        </dependency>
        <dependency>
            <groupId>org.xerial</groupId>
            <artifactId>sqlite-jdbc</artifactId>
            <version>3.45.2.0</version>
        </dependency>
        
        
        For modular programs, you will need these in your module-info file:
        
        requires SQLEase;
        requires java.sql;
        """;

        System.out.println(msg);
    }

}
