/*
 * Copyright 2015-2022 the original author or authors.
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

import static java.util.Objects.isNull;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.Serial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.bremersee.geojson.converter.deserialization.JacksonGeometryDeserializer;
import org.bremersee.geojson.converter.serialization.JacksonGeometrySerializer;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

/**
 * A Jackson JSON processor module that provides the processing (serialization and deserialization)
 * of the following types.
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
 *
 * @author Christian Bremer
 */
public class GeoJsonObjectMapperModule extends SimpleModule {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * The constant TYPE_ID.
   */
  public static final String TYPE_ID = GeoJsonObjectMapperModule.class.getName();

  /**
   * Default constructor.
   */
  public GeoJsonObjectMapperModule() {
    this(new GeometryFactory());
  }

  /**
   * Instantiates a new geo json object mapper module.
   *
   * @param geometryFactory the geometry factory
   */
  public GeoJsonObjectMapperModule(GeometryFactory geometryFactory) {
    this(geometryFactory, false, false);
  }

  /**
   * Instantiates a new geo json object mapper module.
   *
   * @param withBoundingBox with bounding box
   * @param useBigDecimal the use big decimal
   */
  public GeoJsonObjectMapperModule(boolean withBoundingBox, boolean useBigDecimal) {
    this(new GeometryFactory(), withBoundingBox, useBigDecimal);
  }

  /**
   * Instantiates a new geo json object mapper module.
   *
   * @param geometryFactory the geometry factory
   * @param withBoundingBox the with bounding box
   * @param useBigDecimal the use big decimal
   */
  public GeoJsonObjectMapperModule(
      GeometryFactory geometryFactory,
      boolean withBoundingBox,
      boolean useBigDecimal) {
    super(
        TYPE_ID,
        getVersion(),
        getDeserializers(geometryFactory),
        getSerializers(withBoundingBox, useBigDecimal));
  }

  private static Version getVersion() {

    int defaultMajor = 3;
    int defaultMinor = 0;
    int defaultPatchLevel = 0;
    String defaultSnapshotInfo = "SNAPSHOT";

    int major = defaultMajor;
    int minor = defaultMinor;
    int patchLevel = defaultPatchLevel;
    String snapshotInfo = defaultSnapshotInfo;

    String version = GeoJsonObjectMapperModule.class.getPackage().getImplementationVersion();
    if (version != null) {
      try {
        int i = version.indexOf('-');
        if (i < 0) {
          snapshotInfo = null;
        } else {
          snapshotInfo = version.substring(i + 1);
          String[] a = version.substring(0, i).split(Pattern.quote("."));
          major = Integer.parseInt(a[0]);
          minor = Integer.parseInt(a[1]);
          patchLevel = Integer.parseInt(a[2]);
        }

      } catch (RuntimeException e) {
        major = defaultMajor;
        minor = defaultMinor;
        snapshotInfo = defaultSnapshotInfo;
      }
    }

    return new Version(major, minor, patchLevel, snapshotInfo, "org.bremersee",
        "geojson");
  }

  private static Map<Class<?>, JsonDeserializer<?>> getDeserializers(
      GeometryFactory geometryFactory) {

    GeometryFactory gf = isNull(geometryFactory)
        ? new GeoJsonGeometryFactory()
        : geometryFactory;
    HashMap<Class<?>, JsonDeserializer<?>> map = new HashMap<>();
    map.put(Geometry.class, new JacksonGeometryDeserializer(gf));
    map.put(Point.class, new JacksonGeometryDeserializer(gf));
    map.put(LineString.class, new JacksonGeometryDeserializer(gf));
    map.put(Polygon.class, new JacksonGeometryDeserializer(gf));
    map.put(MultiPoint.class, new JacksonGeometryDeserializer(gf));
    map.put(MultiLineString.class, new JacksonGeometryDeserializer(gf));
    map.put(MultiPolygon.class, new JacksonGeometryDeserializer(gf));
    map.put(GeometryCollection.class, new JacksonGeometryDeserializer(gf));
    return map;
  }

  private static List<JsonSerializer<?>> getSerializers(boolean withBoundingBox,
      boolean useBigDecimal) {
    ArrayList<JsonSerializer<?>> list = new ArrayList<>();
    list.add(new JacksonGeometrySerializer(withBoundingBox, useBigDecimal));
    return list;
  }

}
