# Bremersee GeoJSON

[![codecov](https://codecov.io/gh/bremersee/geojson/branch/develop/graph/badge.svg)](https://codecov.io/gh/bremersee/geojson)

This project contains modules for reading and writing GeoJSON.
The GeoJSON format is specified in [rfc7946](https://tools.ietf.org/html/rfc7946).

#### Maven Site

- [Release](https://bremersee.github.io/geojson/index.html)

- [Snapshot](https://nexus.bremersee.org/repository/maven-sites/geojson/2.0.8-SNAPSHOT/index.html)

### Usage

GeoJSON can be read or written with adding the GeoJsonObjectMapperModule to the ObjectMapper or 
without.
  
##### Without adding the GeoJsonObjectMapperModule to the ObjectMapper

If you do not add the GeoJsonObjectMapperModule to the ObjectMapper, you'll have to use the GeometryWrapper:

```java
public class Example {
  
  public static void main(String[] args) {
    ObjectMapper om = new ObjectMapper();
    Point p = GeometryUtils.createPoint(10.2, 52.4);
    GeometryWrapper gw = new GeometryWrapper(p);
    String json = om.writeValueAsString(gw);
    System.out.println(json);
  }
}
```

GeoJsonFeature and GeoJsonFeatureCollection can be used without adding the module to the object 
mapper, too.

```java
public class Example {
  
  public static void main(String[] args) {
    ObjectMapper om = new ObjectMapper();
    Point p = GeometryUtils.createPoint(10.2, 52.4);
    GeoJsonFeature f = new GeoJsonFeature("id", p, false, null);
    String json = om.writeValueAsString(f);
    System.out.println(json);
  }
}
```
  
##### With adding the GeoJsonObjectMapperModule to the ObjectMapper

If you add the GeoJsonObjectMapperModule to the ObjectMapper, you'll be able to process the geometry
object directly:

```java
public class Example {
  
  public static void main(String[] args) {
    ObjectMapper om = new ObjectMapper();
    om.registerModule(new GeoJsonObjectMapperModule());
    Point p = GeometryUtils.createPoint(10.2, 52.4);
    String json = om.writeValueAsString(p);
    System.out.println(json);
  }
}
```

### Spring Data MongoDB Support

If you want to persist the JTS geometry objects to a MongoDB with Spring Data you'll have to 
register these converters:

```java
@Configuration
public class PersistenceConfiguration {

  @Primary
  @Bean
  public MongoCustomConversions customConversions() {
    final List<Object> converters = new ArrayList<>(
        GeoJsonConverters.getConvertersToRegister(null));
    // add more custom converters
    return new MongoCustomConversions(converters);
  }
}
```

An entity may look like this:

```java
@Document(collection = "feature")
@TypeAlias("Route")
public class RouteEntity {

  @Id
  private String id;

  @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
  private MultiLineString geometry; // this is org.locationtech.jts.geom.MultiLineString

  private double[] bbox;

  private RouteProperties properties;

  // getter and setter
}
```
