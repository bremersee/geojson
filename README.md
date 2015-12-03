# Bremersee GeoJSON
This project contains Java classes for reading and writing GeoJSON with the Jackson JSON Processor.

The generated maven site is committed to the [gh-pages branch](https://github.com/bremersee/geojson/tree/gh-pages) and visible [here](http://bremersee.github.io/geojson/). There you can find some examples, too.

## Release 1.1.0
Release 1.1.0 is build with Java 7 and the Jackson JSON Processor Version 2.46.

It is available at Maven Central:
```xml
<dependency>
    <groupId>org.bremersee</groupId>
    <artifactId>bremersee-geojson</artifactId>
    <version>1.1.0</version>
</dependency>
```

# Bremersee GeoJSON Example
This project contains a small Spring Boot Application that demonstrates the usage of the GeoJSON library.

It's not available at Maven Central. You may check it out and run the application with
```
$ cd geojson/bremersee-geojson-example
$ mvn spring-boot:run
```
or
```
$ cd geojson/bremersee-geojson-example
$ mvn clean package
$ java -jar target/bremersee-geojson-eaxample-1.1.0.jar
```
After the application is started you can open [http://localhost:8080/main.html](http://localhost:8080/main.html) in your favorite browser and have a look at the demonstration.
