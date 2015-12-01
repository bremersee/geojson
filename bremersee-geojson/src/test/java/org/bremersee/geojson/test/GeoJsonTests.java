/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bremersee.geojson.test;

import java.util.ArrayList;

import org.bremersee.geojson.GeoJsonFeature;
import org.bremersee.geojson.GeoJsonFeatureCollection;
import org.bremersee.geojson.GeoJsonLinkedCrs;
import org.bremersee.geojson.GeoJsonNamedCrs;
import org.bremersee.geojson.GeoJsonObjectMapperModule;
import org.bremersee.geojson.GeometryWrapper;
import org.bremersee.geojson.utils.GeometryUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

import junit.framework.TestCase;

/**
 * <p>
 * JUnit test that writes and reads GeoJSON objects.
 * </p>
 * 
 * @author Christian Bremer
 */
public class GeoJsonTests {

    private static GeometryFactory geometryFactory = new GeometryFactory();

    private static ObjectMapper objectMapper = new ObjectMapper();
    
    private static GeometryFactory getGeometryFactory() {
        return geometryFactory;
    }

    private static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @BeforeClass
    public static void initClass() {
        getObjectMapper().registerModule(new GeoJsonObjectMapperModule(getGeometryFactory()));
    }

    private static LineString createLineString(Coordinate[] coordinates) {
        CoordinateSequence points = new CoordinateArraySequence(coordinates);
        return new LineString(points, getGeometryFactory());
    }

    private static LineString createLineString() {
        return createLineString(new Coordinate[] { new Coordinate(0., 0.),
                new Coordinate(0., 1.), new Coordinate(1., 1.),
                new Coordinate(1., 0.), new Coordinate(0., 0.) });
    }

    private static MultiLineString createMultiLineString() {
        LineString[] lineStrings = new LineString[] {
                createLineString(),
                createLineString(new Coordinate[] { new Coordinate(2., 2.),
                        new Coordinate(2., 4.), new Coordinate(4., 4.),
                        new Coordinate(4., 2.), new Coordinate(2., 2.) }) };
        return new MultiLineString(lineStrings, getGeometryFactory());
    }

    private static Point createPoint(double x, double y) {
        CoordinateSequence coordinates = new CoordinateArraySequence(
                new Coordinate[] { new Coordinate(x, y) });
        return new Point(coordinates, getGeometryFactory());
    }

    private static Point createPoint() {
        return createPoint(3., 6.);
    }

    private MultiPoint createMultiPoint() {
        return new MultiPoint(new Point[] { createPoint(),
                createPoint(11., 12.) }, getGeometryFactory());
    }

    private Polygon createPolygon() {
        return new Polygon(new LinearRing(createLineString()
                .getCoordinateSequence(), getGeometryFactory()), null,
                getGeometryFactory());
    }

    private Polygon createPolygonWithHoles() {
        LineString ls0 = createLineString(new Coordinate[] { new Coordinate(0.1, 0.1),
                new Coordinate(0.1, 0.2), new Coordinate(0.2, 0.2),
                new Coordinate(0.2, 0.1), new Coordinate(0.1, 0.1) });
        LineString ls1 = createLineString(new Coordinate[] { new Coordinate(0.8, 0.8),
                new Coordinate(0.8, 0.9), new Coordinate(0.9, 0.9),
                new Coordinate(0.9, 0.8), new Coordinate(0.8, 0.8) });
        
        LinearRing[] holes = new LinearRing[2];
        holes[0] = new LinearRing(ls0.getCoordinateSequence(), getGeometryFactory());
        holes[1] = new LinearRing(ls1.getCoordinateSequence(), getGeometryFactory());
        
        return new Polygon(new LinearRing(createLineString()
                .getCoordinateSequence(), getGeometryFactory()), holes,
                getGeometryFactory());
    }

    private MultiPolygon createMultiPolygon() {
        Polygon[] polygons = new Polygon[] { createPolygon(), createPolygonWithHoles() };
        return new MultiPolygon(polygons, getGeometryFactory());
    }

    @Test
    public void testPoint() throws Exception {
        Point geometry = createPoint();
        System.out.println("Testing " + geometry.toText());
        String jsonStr = getObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(geometry);
        System.out.println(jsonStr);
        Point readGeometry = getObjectMapper().readValue(jsonStr,
                Point.class);
        TestCase.assertTrue(GeometryUtils.equals(geometry, readGeometry));
        
        GeometryWrapper gjg = new GeometryWrapper(geometry);
        jsonStr = getObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(gjg);
        System.out.println(jsonStr);
        GeometryWrapper readGjg = getObjectMapper().readValue(jsonStr, GeometryWrapper.class);
        TestCase.assertEquals(gjg, readGjg);
        TestCase.assertTrue(GeometryUtils.equals(geometry, readGjg.getGeometry()));
    }

    @Test
    public void testLineString() throws Exception {
        LineString geometry = createLineString();
        System.out.println("Testing " + geometry.toText());
        String jsonStr = getObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(geometry);
        System.out.println(jsonStr);
        LineString readGeometry = getObjectMapper().readValue(jsonStr,
                LineString.class);
        TestCase.assertTrue(GeometryUtils.equals(geometry, readGeometry));
        
        GeometryWrapper gjg = new GeometryWrapper(geometry);
        jsonStr = getObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(gjg);
        System.out.println(jsonStr);
        GeometryWrapper readGjg = getObjectMapper().readValue(jsonStr, GeometryWrapper.class);
        TestCase.assertEquals(gjg, readGjg);
        TestCase.assertTrue(GeometryUtils.equals(geometry, readGjg.getGeometry()));
    }

    @Test
    public void testPolygon() throws Exception {
        Polygon geometry = createPolygon();
        System.out.println("Testing " + geometry.toText());
        String jsonStr = getObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(geometry);
        System.out.println(jsonStr);
        Polygon readGeometry = getObjectMapper().readValue(jsonStr,
                Polygon.class);
        TestCase.assertTrue(GeometryUtils.equals(geometry, readGeometry));
        
        GeometryWrapper gjg = new GeometryWrapper(geometry);
        jsonStr = getObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(gjg);
        System.out.println(jsonStr);
        GeometryWrapper readGjg = getObjectMapper().readValue(jsonStr, GeometryWrapper.class);
        TestCase.assertEquals(gjg, readGjg);
        TestCase.assertTrue(GeometryUtils.equals(geometry, readGjg.getGeometry()));
    }

    @Test
    public void testPolygonWithHoles() throws Exception {
        Polygon geometry = createPolygonWithHoles();
        System.out.println("Testing with holes " + geometry.toText());
        String jsonStr = getObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(geometry);
        System.out.println(jsonStr);
        Polygon readGeometry = getObjectMapper().readValue(jsonStr,
                Polygon.class);
        TestCase.assertTrue(GeometryUtils.equals(geometry, readGeometry));
        
        GeometryWrapper gjg = new GeometryWrapper(geometry);
        jsonStr = getObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(gjg);
        System.out.println(jsonStr);
        GeometryWrapper readGjg = getObjectMapper().readValue(jsonStr, GeometryWrapper.class);
        TestCase.assertEquals(gjg, readGjg);
        TestCase.assertTrue(GeometryUtils.equals(geometry, readGjg.getGeometry()));
    }

    @Test
    public void testMultiPoint() throws Exception {
        MultiPoint geometry = createMultiPoint();
        System.out.println("Testing " + geometry.toText());
        String jsonStr = getObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(geometry);
        System.out.println(jsonStr);
        MultiPoint readGeometry = getObjectMapper().readValue(jsonStr,
                MultiPoint.class);
        TestCase.assertTrue(GeometryUtils.equals(geometry, readGeometry));
        
        GeometryWrapper gjg = new GeometryWrapper(geometry);
        jsonStr = getObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(gjg);
        System.out.println(jsonStr);
        GeometryWrapper readGjg = getObjectMapper().readValue(jsonStr, GeometryWrapper.class);
        TestCase.assertEquals(gjg, readGjg);
        TestCase.assertTrue(GeometryUtils.equals(geometry, readGjg.getGeometry()));
    }

    @Test
    public void testMultiLineString() throws Exception {
        MultiLineString geometry = createMultiLineString();
        System.out.println("Testing " + geometry.toText());
        String jsonStr = getObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(geometry);
        System.out.println(jsonStr);
        MultiLineString readGeometry = getObjectMapper().readValue(jsonStr,
                MultiLineString.class);
        TestCase.assertTrue(GeometryUtils.equals(geometry, readGeometry));
        
        GeometryWrapper gjg = new GeometryWrapper(geometry);
        jsonStr = getObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(gjg);
        System.out.println(jsonStr);
        GeometryWrapper readGjg = getObjectMapper().readValue(jsonStr, GeometryWrapper.class);
        TestCase.assertEquals(gjg, readGjg);
        TestCase.assertTrue(GeometryUtils.equals(geometry, readGjg.getGeometry()));
    }

    @Test
    public void testMultiPolygon() throws Exception {
        MultiPolygon geometry = createMultiPolygon();
        System.out.println("Testing " + geometry.toText());
        String jsonStr = getObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(geometry);
        System.out.println(jsonStr);
        MultiPolygon readGeometry = getObjectMapper().readValue(jsonStr,
                MultiPolygon.class);
        TestCase.assertTrue(GeometryUtils.equals(geometry, readGeometry));
        
        GeometryWrapper gjg = new GeometryWrapper(geometry);
        jsonStr = getObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(gjg);
        System.out.println(jsonStr);
        GeometryWrapper readGjg = getObjectMapper().readValue(jsonStr, GeometryWrapper.class);
        TestCase.assertEquals(gjg, readGjg);
        TestCase.assertTrue(GeometryUtils.equals(geometry, readGjg.getGeometry()));
    }

    @Test
    public void testGeometryCollection() throws Exception {
        GeometryCollection geometry = new GeometryCollection(new Geometry[] {
                createLineString(), createMultiLineString() },
                getGeometryFactory());
        System.out.println("Testing " + geometry.toText());
        String jsonStr = getObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(geometry);
        System.out.println(jsonStr);
        GeometryCollection readGeometry = getObjectMapper().readValue(
                jsonStr, GeometryCollection.class);
        TestCase.assertTrue(GeometryUtils.equals(geometry, readGeometry));
        
        GeometryWrapper gjg = new GeometryWrapper(geometry);
        jsonStr = getObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(gjg);
        System.out.println(jsonStr);
        GeometryWrapper readGjg = getObjectMapper().readValue(jsonStr, GeometryWrapper.class);
        TestCase.assertEquals(gjg, readGjg);
        TestCase.assertTrue(GeometryUtils.equals(geometry, readGjg.getGeometry()));
    }

    @Test
    public void testGeoJsonLinkedCrs() throws Exception {

        GeoJsonLinkedCrs crs = new GeoJsonLinkedCrs(
                "http://example.com/crs/42", "proj4");
        System.out.println("Testing " + crs);
        String json = getObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(crs);
        System.out.println(json);
        GeoJsonLinkedCrs readCrs = getObjectMapper().readValue(json,
                GeoJsonLinkedCrs.class);
        TestCase.assertEquals(crs, readCrs);
    }

    @Test
    public void testGeoJsonNamedCrs() throws Exception {

        GeoJsonNamedCrs crs = new GeoJsonNamedCrs(
                "urn:ogc:def:crs:OGC:1.3:CRS84");
        System.out.println("Testing " + crs);
        String json = getObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(crs);
        System.out.println(json);
        GeoJsonNamedCrs readCrs = getObjectMapper().readValue(json,
                GeoJsonNamedCrs.class);
        TestCase.assertEquals(crs, readCrs);
    }

    @Test
    public void testGeoJsonFeature() throws Exception {

        GeometryCollection geometry = new GeometryCollection(new Geometry[] {
                createLineString(), createMultiLineString() },
                getGeometryFactory());

        GeoJsonFeature f = new GeoJsonFeature();
        f.setGeometry(geometry);
        f.setBbox(GeometryUtils.getBoundingBox(geometry));
        f.setCrs(new GeoJsonNamedCrs("urn:ogc:def:crs:OGC:1.3:CRS84"));
        f.setId("100");
        f.getProperties().put("myKey", "myValue");

        System.out.println("Testing " + f);

        String json = getObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(f);

        System.out.println(json);

        GeoJsonFeature readF = getObjectMapper().readValue(json,
                GeoJsonFeature.class);

        System.out.println(getObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(readF));

        TestCase.assertEquals(f, readF);
    }

    @Test
    public void testGeoJsonFeatureCollection() throws Exception {

        GeometryCollection geometry = new GeometryCollection(new Geometry[] {
                createLineString(), createMultiLineString() },
                getGeometryFactory());

        GeoJsonFeature f1 = new GeoJsonFeature();
        f1.setGeometry(geometry);
        f1.setBbox(GeometryUtils.getBoundingBox(geometry));
        f1.setCrs(new GeoJsonNamedCrs("urn:ogc:def:crs:OGC:1.3:CRS84"));
        f1.setId("101");
        f1.getProperties().put("myKey1", "myValue1");

        GeoJsonFeature f2 = new GeoJsonFeature();
        f2.setGeometry(createMultiPolygon());
        f2.setBbox(GeometryUtils.getBoundingBox(geometry));
        f2.setCrs(new GeoJsonNamedCrs("urn:ogc:def:crs:OGC:1.3:CRS84"));
        f2.setId("102");
        f2.getProperties().put("myKey2", "myValue2");

        ArrayList<GeoJsonFeature> features = new ArrayList<GeoJsonFeature>();
        features.add(f1);
        features.add(f2);
        GeoJsonFeatureCollection fc = new GeoJsonFeatureCollection("200",
                new GeoJsonNamedCrs("urn:ogc:def:crs:OGC:1.3:CRS84"), features,
                true, null);

        fc.getProperties().put("fcKey", "fcValue");

        System.out.println("Testing " + fc);

        String json = getObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(fc);

        System.out.println(json);

        GeoJsonFeatureCollection readFc = getObjectMapper().readValue(json,
                GeoJsonFeatureCollection.class);

        System.out.println(getObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(readFc));

        TestCase.assertEquals(fc, readFc);
    }

}