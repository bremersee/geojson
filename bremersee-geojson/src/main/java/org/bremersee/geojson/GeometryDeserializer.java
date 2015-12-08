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

package org.bremersee.geojson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.vividsolutions.jts.geom.Coordinate;
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

/**
 * <p>
 * A Jackson deserializer for a {@link Geometry}.
 * </p>
 * 
 * @author Christian Bremer
 */
public class GeometryDeserializer extends StdDeserializer<Geometry> {

    private static final long serialVersionUID = 1L;

    private GeometryFactory geometryFactory = new GeometryFactory();

    /**
     * Default constructor.
     */
    public GeometryDeserializer() {
        super(Geometry.class);
    }

    /**
     * Constructs a deserializer that uses the specified geometry factory.
     * 
     * @param geometryFactory
     *            the geometry factory
     */
    public GeometryDeserializer(GeometryFactory geometryFactory) {
        this();
        setGeometryFactory(geometryFactory);
    }

    /**
     * Gets the geometry factory.
     * 
     * @return the geometry factory
     */
    protected GeometryFactory getGeometryFactory() {
        return geometryFactory;
    }

    /**
     * Returns the geometry factory.
     * 
     * @param geometryFactory
     *            the geometry factory
     */
    public void setGeometryFactory(GeometryFactory geometryFactory) {
        if (geometryFactory != null) {
            this.geometryFactory = geometryFactory;
        }
    }

    //@formatter:off
    /* (non-Javadoc)
     * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext)
     */
    //@formatter:on
    @Override
    public Geometry deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        String type = null;
        List<Object> coordinates = new ArrayList<Object>();
        List<Geometry> geometries = new ArrayList<Geometry>();

        JsonToken currentToken = null;
        while ((currentToken = jp.nextValue()) != null) {
            if (JsonToken.VALUE_STRING.equals(currentToken)) {
                if ("type".equals(jp.getCurrentName())) {
                    type = jp.getText();
                }
            } else if (JsonToken.START_ARRAY.equals(currentToken)) {
                if ("coordinates".equals(jp.getCurrentName())) {
                    parseCoordinates(0, coordinates, jp, ctxt);
                } else if ("geometries".equals(jp.getCurrentName())) {
                    geometries.addAll(parseGeometries(jp, ctxt));
                }
            } else if (JsonToken.END_OBJECT.equals(currentToken)) {
                break;
            }
        }

        if ("GeometryCollection".equals(type)) {
            return createGeometryCollection(geometries);
        } else {
            return createGeometry(type, coordinates);
        }
    }

    private void parseCoordinates(int depth, List<Object> coordinates, JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        JsonToken currentToken = null;
        while ((currentToken = jp.nextValue()) != null) {
            if (JsonToken.END_ARRAY.equals(currentToken)) {
                break;
            } else if (JsonToken.START_ARRAY.equals(currentToken)) {
                List<Object> list = new ArrayList<Object>();
                parseCoordinates(depth + 1, list, jp, ctxt);
                coordinates.add(list);
            } else if (JsonToken.VALUE_NUMBER_FLOAT.equals(currentToken)) {
                coordinates.add(jp.getDoubleValue());
            } else if (JsonToken.VALUE_NUMBER_INT.equals(currentToken)) {
                coordinates.add(Long.valueOf(jp.getLongValue()).doubleValue());
            }
        }
    }

    private Coordinate createCoordinate(List<Object> coordinates) {
        Coordinate coordinate = new Coordinate();
        coordinate.x = coordinates.size() > 0 ? (double) coordinates.get(0) : Double.NaN;
        coordinate.y = coordinates.size() > 1 ? (double) coordinates.get(1) : Double.NaN;
        coordinate.z = coordinates.size() > 2 ? (double) coordinates.get(2) : Double.NaN;
        return coordinate;
    }

    private Coordinate[] createCoordinates(List<Object> coordinates) {
        Coordinate[] coords = new Coordinate[coordinates.size()];
        int i = 0;
        for (Object obj : coordinates) {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) obj;
            coords[i] = createCoordinate(list);
            i++;
        }
        return coords;
    }

    private Point createPoint(List<Object> coordinates) {
        return getGeometryFactory().createPoint(createCoordinate(coordinates));
    }

    private LineString createLineString(List<Object> coordinates) { // List<List<Double>>
        return getGeometryFactory().createLineString(createCoordinates(coordinates));
    }

    private Polygon createPolygon(List<Object> coordinates) { // List<List<List<Double>>>
        List<Coordinate[]> list = new ArrayList<Coordinate[]>();
        for (Object obj : coordinates) {
            @SuppressWarnings("unchecked")
            List<Object> coords = (List<Object>) obj;
            list.add(createCoordinates(coords));
        }
        if (list.size() == 1) {
            return getGeometryFactory().createPolygon(list.get(0));
        } else {
            GeometryFactory gf = getGeometryFactory();
            LinearRing[] holes = new LinearRing[list.size() - 1];
            for (int i = 1; i < list.size(); i++) {
                holes[i - 1] = gf.createLinearRing(list.get(i));
            }
            return gf.createPolygon(gf.createLinearRing(list.get(0)), holes);
        }
    }

    private MultiPoint createMultiPoint(List<Object> coordinates) {
        Point[] points = new Point[coordinates.size()];
        int i = 0;
        for (Object obj : coordinates) {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) obj;
            points[i] = createPoint(list);
            i++;
        }
        return getGeometryFactory().createMultiPoint(points);
    }

    private MultiLineString createMultiLineString(List<Object> coordinates) {
        LineString[] lineStrings = new LineString[coordinates.size()];
        int i = 0;
        for (Object obj : coordinates) {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) obj;
            lineStrings[i] = createLineString(list);
            i++;
        }
        return getGeometryFactory().createMultiLineString(lineStrings);
    }

    private MultiPolygon createMultiPolygon(List<Object> coordinates) {
        Polygon[] polygons = new Polygon[coordinates.size()];
        int i = 0;
        for (Object obj : coordinates) {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) obj;
            polygons[i] = createPolygon(list);
            i++;
        }
        return getGeometryFactory().createMultiPolygon(polygons);
    }

    private GeometryCollection createGeometryCollection(List<Geometry> geometries) {
        GeometryCollection geometryCollection = getGeometryFactory()
                .createGeometryCollection(geometries.toArray(new Geometry[geometries.size()]));
        return geometryCollection;
    }

    private List<Geometry> parseGeometries(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        String type = null;
        List<Object> coordinates = new ArrayList<Object>();
        List<Geometry> geometries = new ArrayList<Geometry>();

        JsonToken currentToken = null;
        while ((currentToken = jp.nextValue()) != null) {
            if (JsonToken.END_OBJECT.equals(currentToken)) {
                if (type != null && !coordinates.isEmpty()) {
                    geometries.add(createGeometry(type, coordinates));
                    type = null;
                    coordinates = new ArrayList<Object>();
                }
            } else if (JsonToken.VALUE_STRING.equals(currentToken)) {
                if ("type".equals(jp.getCurrentName())) {
                    type = jp.getText();
                }
            } else if (JsonToken.START_ARRAY.equals(currentToken)) {
                if ("coordinates".equals(jp.getCurrentName())) {
                    parseCoordinates(0, coordinates, jp, ctxt);
                }
            } else if (JsonToken.END_ARRAY.equals(currentToken)) {
                break;
            }
        }
        return geometries;
    }

    private Geometry createGeometry(String type, List<Object> coordinates) {
        if ("Point".equals(type)) {
            return createPoint(coordinates);
        }
        if ("LineString".equals(type)) {
            return createLineString(coordinates);
        }
        if ("Polygon".equals(type)) {
            return createPolygon(coordinates);
        }
        if ("MultiPoint".equals(type)) {
            return createMultiPoint(coordinates);
        }
        if ("MultiLineString".equals(type)) {
            return createMultiLineString(coordinates);
        }
        if ("MultiPolygon".equals(type)) {
            return createMultiPolygon(coordinates);
        }
        throw new RuntimeException("Geometry type [" + type + "] is unsupported.");
    }

}
