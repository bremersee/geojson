# Bremersee GeoJSON

This project contains modules for reading and writing GeoJSON with the Jackson JSON Processor.
The GeoJSON format is specified in [rfc7946](https://tools.ietf.org/html/rfc7946).

- [Documentation](https://nexus.bremersee.org/repository/maven-sites/geojson/2.0.0-SNAPSHOT/index.html)

### Maven

```xml
<dependency>
  <groupId>org.bremersee</groupId>
  <artifactId>bremersee-geojson</artifactId>
  <version>2.0.0-SNAPSHOT</version>
</dependency>
```

### Maven Repository

```xml
<repositories>
  <repository>
    <id>bremersee-releases</id>
    <name>Bremersee Releases</name>
    <url>https://nexus.bremersee.org/repository/maven-releases</url>
    <snapshots>
      <enabled>false</enabled>
    </snapshots>
  </repository>
  <repository>
    <id>bremersee-snapshots</id>
    <name>Bremersee Snapshots</name>
    <url>https://nexus.bremersee.org/repository/maven-snapshots</url>
    <snapshots>
      <enabled>true</enabled>
    </snapshots>
  </repository>
</repositories>
```
