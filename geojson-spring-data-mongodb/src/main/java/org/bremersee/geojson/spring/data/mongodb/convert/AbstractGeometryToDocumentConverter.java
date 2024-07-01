/*
 * Copyright 2018-2022 the original author or authors.
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

import java.util.Objects;
import org.bremersee.geojson.converter.serialization.GeometryToJsonConverter;
import org.bson.Document;
import org.locationtech.jts.geom.Geometry;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

/**
 * The abstract geometry to document converter.
 *
 * @param <G> the type parameter
 * @author Christian Bremer
 */
abstract class AbstractGeometryToDocumentConverter<G extends Geometry>
    implements Converter<G, Document> {

  private final GeometryToJsonConverter geometryToJsonConverter = new GeometryToJsonConverter();

  @Override
  public Document convert(@NonNull G geometry) {
    Document document = new Document();
    document.putAll(geometryToJsonConverter.convert(geometry));
    return document;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    return o != null && getClass() == o.getClass();
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getClass());
  }

}
