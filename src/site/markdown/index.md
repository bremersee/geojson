# GeoJSON

This project contains model classes for reading and writing GeoJSON with the Jackson JSON Processor.
The GeoJSON format is specified in [rfc7946](https://tools.ietf.org/html/rfc7946).
  
### Usage

GeoJSON can be read or written with adding the GeoJsonObjectMapperModule to the ObjectMapper or 
without.
  
##### Without adding the GeoJsonObjectMapperModule to the ObjectMapper

If you do not add the GeoJsonObjectMapperModule to the ObjectMapper, you'll have to use the GeometryWrapper:

```java
ObjectMapper om = new ObjectMapper();
Point p = GeometryUtils.createPoint(10.2, 52.4);
GeometryWrapper gw = new GeometryWrapper(p);
String json = om.writeValueAsString(gw);
```  

GeoJsonFeature and GeoJsonFeatureCollection can be used without adding the module to the object 
mapper, too.

```java
ObjectMapper om = new ObjectMapper();
Point p = GeometryUtils.createPoint(10.2, 52.4);
GeoJsonFeature f = new GeoJsonFeature("id", p, false, null);
String json = om.writeValueAsString(f);
```  
  
##### With adding the GeoJsonObjectMapperModule to the ObjectMapper

If you add the GeoJsonObjectMapperModule to the ObjectMapper, you'll be able to process the geometry
object directly:

```java
ObjectMapper om = new ObjectMapper();
om.registerModule(new GeoJsonObjectMapperModule());
Point p = GeometryUtils.createPoint(10.2, 52.4);
String json = om.writeValueAsString(p);
```  



