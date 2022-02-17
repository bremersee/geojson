# GeoJSON

This project contains GeoJSON for the [JTS Topology Suite](https://locationtech.github.io/jts/).
The GeoJSON format is specified in [rfc7946](https://tools.ietf.org/html/rfc7946).

### Usage

GeoJSON can be read or written by adding the GeoJsonObjectMapperModule to the ObjectMapper.

```java
public class Example {

  public static void main(String[] args) {
    ObjectMapper om = new ObjectMapper();
    om.registerModule(new GeoJsonObjectMapperModule());
    GeoJsonGeometryFactory geometryFactory = new GeoJsonGeometryFactory();
    Point p = geometryFactory.createPoint(10.2, 52.4);
    String json = om.writeValueAsString(p);
    System.out.println(json);
  }
}
```
