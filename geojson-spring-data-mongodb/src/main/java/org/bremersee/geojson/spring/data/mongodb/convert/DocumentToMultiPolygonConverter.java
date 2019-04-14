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
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * The document to multi polygon converter.
 *
 * @author Christian Bremer
 */
@ReadingConverter
class DocumentToMultiPolygonConverter
    extends AbstractDocumentToGeometryConverter<MultiPolygon> {

  /**
   * Instantiates a new document to multi polygon converter.
   */
  @SuppressWarnings("unused")
  DocumentToMultiPolygonConverter() {
  }

  /**
   * Instantiates a new document to multi polygon converter.
   *
   * @param geometryFactory the geometry factory
   */
  DocumentToMultiPolygonConverter(final GeometryFactory geometryFactory) {
    super(geometryFactory);
  }

  @Override
  MultiPolygon doConvert(final Document document) {
    Assert.isTrue(ObjectUtils.nullSafeEquals(document.get("type"), "MultiPolygon"),
        String.format("Cannot convert type '%s' to MultiPolygon.", document.get("type")));
    //noinspection unchecked
    final List<Object> coordinates = (List<Object>) document.get("coordinates");
    return getConvertHelper().createMultiPolygon(coordinates);
  }

}
