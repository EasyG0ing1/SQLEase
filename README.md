#  SQLEase

SQLEase is a simple library that makes it easy to interact with a MySQL or a SQLite database.


The library contains two main classes: `SQLite` and `MySQL`

The Javadocs explain the methods in the library. This library assumes you have worked with databases before and so the Javadocs should be relatively easy to understand.

# Getting Started

If you're using Maven

```xml
<dependency>
    <groupId>com.simtechdata</groupId>
    <artifactId>SQLEase</artifactId>
    <version>1.0.0</version>
</dependency>
```

Or, if using Gradle to build, add this to your Gradle build file

```groovy
compile group: 'com.simtechdata', name: 'SQLEase', version: 1.0.0
```

You can even use it from a Groovy script!

``` Groovy
@Grapes(
  @Grab(group='com.simtechdata', module='SQLEase', version=1.0.0)
)
```

### Modular Apps
If your app is modular, then add this to your `module-info.java` file

``` Java
requires SQLEase;
requires java.sql;
```

### Dependencies
You will also need these dependencies (update the version information as needed)

``` XML
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
```


### Release Notes

* 1.0.0 - Initial Release
