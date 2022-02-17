/*
 * Copyright 2020 the original author or authors.
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

package org.bremersee.geojson.converter.deserialization;

import static org.bremersee.geojson.GeoJsonConstants.TYPE;
import static org.bremersee.geojson.GeoJsonConstants.GEOMETRY_COLLECTION;
import static org.springframework.util.ObjectUtils.isEmpty;

import java.util.Map;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.util.Assert;

/**
 * The json to geometry collection converter.
 *
 * @author Christian Bremer
 */
public class JsonToGeometryCollectionConverter extends AbstractJsonToGeometryConverter {

  private static final long serialVersionUID = 1L;

  private final JsonToGeometryConverter geometryConverter;

  /**
   * Instantiates a new json to geometry collection converter.
   */
  public JsonToGeometryCollectionConverter() {
    super();
    this.geometryConverter = new JsonToGeometryConverter(getGeometryFactory());
  }

  /**
   * Instantiates a new json to geometry collection converter.
   *
   * @param geometryFactory the geometry factory
   */
  public JsonToGeometryCollectionConverter(GeometryFactory geometryFactory) {
    super(geometryFactory);
    this.geometryConverter = new JsonToGeometryConverter(getGeometryFactory());
  }

  /**
   * Convert geometry collection.
   *
   * @param source the source
   * @return the geometry collection
   */
  public GeometryCollection convert(Map<String, Object> source) {
    if (isEmpty(source)) {
      return null;
    }
    Assert.isTrue(
        source.get(TYPE).equals(GEOMETRY_COLLECTION),
        String.format("Source is not a %s: %s", GEOMETRY_COLLECTION, source));
    return (GeometryCollection) geometryConverter.convert(source);
  }

}
