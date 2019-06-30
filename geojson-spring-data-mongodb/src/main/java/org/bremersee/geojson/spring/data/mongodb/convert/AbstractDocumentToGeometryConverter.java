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

import org.bremersee.geojson.utils.ConvertHelper;
import org.bson.Document;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.core.convert.converter.Converter;

/**
 * The abstract document to geometry converter.
 *
 * @param <G> the geometry type parameter
 * @author Christian Bremer
 */
abstract class AbstractDocumentToGeometryConverter<G extends Geometry>
    implements Converter<org.bson.Document, G> {

  private final ConvertHelper convertHelper;

  /**
   * Instantiates a new abstract document to geometry converter.
   */
  AbstractDocumentToGeometryConverter() {
    this(null);
  }

  /**
   * Instantiates a new abstract document to geometry converter.
   *
   * @param geometryFactory the geometry factory
   */
  AbstractDocumentToGeometryConverter(final GeometryFactory geometryFactory) {
    this.convertHelper = new ConvertHelper(geometryFactory, false);
  }

  /**
   * Gets convert helper.
   *
   * @return the convert helper
   */
  ConvertHelper getConvertHelper() {
    return convertHelper;
  }

  @Override
  public G convert(final Document document) {
    if (document == null || !document.containsKey("type")) {
      return null;
    }
    return doConvert(document);
  }

  /**
   * Do convert the geometry.
   *
   * @param document the document
   * @return the geometry
   */
  abstract G doConvert(Document document);

}
