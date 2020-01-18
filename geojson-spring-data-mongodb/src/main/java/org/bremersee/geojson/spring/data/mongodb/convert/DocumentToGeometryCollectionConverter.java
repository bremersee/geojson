/*
 * Copyright 2018-2020 the original author or authors.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * The document to geometry collection converter.
 *
 * @author Christian Bremer
 */
@ReadingConverter
class DocumentToGeometryCollectionConverter
    extends AbstractDocumentToGeometryConverter<GeometryCollection> {

  private DocumentToGeometryConverter converter;

  /**
   * Instantiates a new Document to geometry collection converter.
   */
  DocumentToGeometryCollectionConverter() {
    this(null);
  }

  /**
   * Instantiates a new document to geometry collection converter.
   *
   * @param geometryFactory the geometry factory
   */
  DocumentToGeometryCollectionConverter(final GeometryFactory geometryFactory) {
    super(geometryFactory);
    converter = new DocumentToGeometryConverter(geometryFactory);
  }

  @Override
  GeometryCollection doConvert(final Document document) {
    Assert.isTrue(ObjectUtils.nullSafeEquals(document.get("type"), "GeometryCollection"),
        String.format("Cannot convert type '%s' to GeometryCollection.", document.get("type")));

    final List<Geometry> geometries = new ArrayList<>();
    //noinspection unchecked
    for (Map<String, Object> map : (List<Map<String, Object>>) document.get("geometries")) {
      if (map != null) {
        geometries.add(converter.convert(new Document(map)));
      }
    }

    return getConvertHelper().createGeometryCollection(geometries);
  }

}
