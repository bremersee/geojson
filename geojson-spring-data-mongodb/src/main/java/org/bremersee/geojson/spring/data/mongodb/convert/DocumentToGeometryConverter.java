/*
 * Copyright 2018 the original author or authors.
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

package org.bremersee.geojson.spring.data.mongodb.convert;

import java.util.List;
import org.bson.Document;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.data.convert.ReadingConverter;

/**
 * The document to geometry converter.
 *
 * @author Christian Bremer
 */
@ReadingConverter
class DocumentToGeometryConverter extends AbstractDocumentToGeometryConverter<Geometry> {

  /**
   * Instantiates a new document to geometry converter.
   */
  @SuppressWarnings("unused")
  DocumentToGeometryConverter() {
  }

  /**
   * Instantiates a new document to geometry converter.
   *
   * @param geometryFactory the geometry factory
   */
  DocumentToGeometryConverter(final GeometryFactory geometryFactory) {
    super(geometryFactory);
  }

  @Override
  Geometry doConvert(final Document document) {
    final String type = document.getString("type");

    if ("GeometryCollection".equals(type)) {
      return new DocumentToGeometryCollectionConverter(getConvertHelper().getGeometryFactory())
          .doConvert(document);
    }

    //noinspection unchecked
    final List<Object> coordinates = (List<Object>) document.get("coordinates");

    if ("Point".equals(type)) {
      return getConvertHelper().createPoint(coordinates);
    }
    if ("LineString".equals(type)) {
      return getConvertHelper().createLineString(coordinates);
    }
    if ("Polygon".equals(type)) {
      return getConvertHelper().createPolygon(coordinates);
    }
    if ("MultiPoint".equals(type)) {
      return getConvertHelper().createMultiPoint(coordinates);
    }
    if ("MultiLineString".equals(type)) {
      return getConvertHelper().createMultiLineString(coordinates);
    }
    if ("MultiPolygon".equals(type)) {
      return getConvertHelper().createMultiPolygon(coordinates);
    }
    throw new IllegalArgumentException("Geometry type [" + type + "] is unsupported.");
  }

}
