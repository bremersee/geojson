# GeoJSON for Spring Data MongoDB
This project contains converters for reading and writing GeoJSON into a MongoDB with Spring Data.

### Configuration

If you want to persist the JTS geometry objects to a MongoDB with Spring Data you'll have to 
register these converters:

```java
@Configuration
public class PersistenceConfiguration {

  @Primary
  @Bean
  public MongoCustomConversions customConversions() {
    List<Object> converters = new ArrayList<>(
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

