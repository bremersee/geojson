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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

//@formatter:off
/**
 * <p>
 * A Jackson JSON processor module that provides the processing (serialization
 * and deserialization) of the following types:
 * <ul>
 * <li>{@link Geometry}</li>
 * <li>{@link Point}</li>
 * <li>{@link LineString}</li>
 * <li>{@link Polygon}</li>
 * <li>{@link MultiPoint}</li>
 * <li>{@link MultiLineString}</li>
 * <li>{@link MultiPolygon}</li>
 * <li>{@link GeometryCollection}</li>
 * </ul>
 * </p>
 * 
 * @author Christian Bremer
 */
//@formatter:on
public class GeoJsonObjectMapperModule extends SimpleModule {

    private static final long serialVersionUID = 1L;

    /**
     * Registers this module to the object mapper.
     * 
     * @param objectMapper
     *            the object mapper
     */
    public static void configure(ObjectMapper objectMapper) {
        configure(objectMapper, null);
    }

    /**
     * Registers this module to the object mapper.
     * 
     * @param objectMapper
     *            the object mapper
     * @param geometryFactory
     *            the geometry factory
     */
    public static void configure(ObjectMapper objectMapper, GeometryFactory geometryFactory) {
        if (objectMapper != null) {
            objectMapper.registerModule(new GeoJsonObjectMapperModule(geometryFactory));
        }
    }

    private static Version getVersion() {

        final int defaultMajor = 1;
        final int defaultMinor = 1;
        final int defaultPatchLevel = 0;
        final String defaultSnapshotInfo = "SNAPSHOT";

        int major = defaultMajor;
        int minor = defaultMinor;
        int patchLevel = defaultPatchLevel;
        String snapshotInfo = defaultSnapshotInfo;

        String version = GeoJsonObjectMapperModule.class.getPackage().getImplementationVersion();
        if (version != null) {
            // version = 1.1.0-SNAPSHOT
            try {
                int i = version.indexOf('-');
                if (i < 0) {
                    snapshotInfo = null;
                } else {
                    snapshotInfo = version.substring(i + 1);
                    String[] a = version.substring(0, i).split(".");
                    major = Integer.parseInt(a[0]);
                    minor = Integer.parseInt(a[1]);
                    patchLevel = Integer.parseInt(a[2]);
                }

            } catch (Exception e) {
                major = defaultMajor;
                minor = defaultMinor;
                patchLevel = defaultPatchLevel;
                snapshotInfo = defaultSnapshotInfo;
            }
        }

        return new Version(major, minor, patchLevel, snapshotInfo, "org.bremersee", "bremersee-geojson");
    }

    private static Map<Class<?>, JsonDeserializer<?>> getDeserializers(GeometryFactory geometryFactory) {
        if (geometryFactory == null) {
            geometryFactory = new GeometryFactory();
        }
        HashMap<Class<?>, JsonDeserializer<?>> map = new HashMap<Class<?>, JsonDeserializer<?>>();
        map.put(Geometry.class, new GeometryDeserializer(geometryFactory));
        map.put(Point.class, new GeometryDeserializer(geometryFactory));
        map.put(LineString.class, new GeometryDeserializer(geometryFactory));
        map.put(Polygon.class, new GeometryDeserializer(geometryFactory));
        map.put(MultiPoint.class, new GeometryDeserializer(geometryFactory));
        map.put(MultiLineString.class, new GeometryDeserializer(geometryFactory));
        map.put(MultiPolygon.class, new GeometryDeserializer(geometryFactory));
        map.put(GeometryCollection.class, new GeometryDeserializer(geometryFactory));
        return map;
    }

    private static List<JsonSerializer<?>> getSerializers() {
        ArrayList<JsonSerializer<?>> list = new ArrayList<JsonSerializer<?>>();
        list.add(new GeometrySerializer());
        return list;
    }

    /**
     * Default constructor.
     */
    public GeoJsonObjectMapperModule() {
        this(null);
    }

    /**
     * Constructs a module with the specified geometry factory.
     * 
     * @param geometryFactory
     *            the geometry factory
     */
    public GeoJsonObjectMapperModule(GeometryFactory geometryFactory) {
        super("GeoJsonModule", getVersion(), getDeserializers(geometryFactory), getSerializers());
    }

}
