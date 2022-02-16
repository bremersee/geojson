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

package org.bremersee.geojson.converter;

import org.bremersee.geojson.GeoJsonGeometryFactory;
import org.locationtech.jts.geom.Geometry;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

/**
 * @author Christian Bremer
 */
public class StringToGeometryConverter extends AbstractStringToGeometryConverter
    implements Converter<String, Geometry> {

  public StringToGeometryConverter() {
  }

  public StringToGeometryConverter(GeoJsonGeometryFactory geometryFactory) {
    super(geometryFactory);
  }

  @Override
  public Geometry convert(@NonNull String source) {
    return getGeometryFactory().createGeometryFromWellKnownText(source);
  }
}
