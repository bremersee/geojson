/*
 * Copyright 2022 the original author or authors.
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

import lombok.NoArgsConstructor;
import org.bremersee.geojson.GeoJsonGeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

/**
 * The string to point converter.
 *
 * @author Christian Bremer
 */
@NoArgsConstructor
public class StringToPointConverter extends AbstractStringToGeometryConverter
    implements Converter<String, Point> {

  /**
   * Instantiates a new string to point converter.
   *
   * @param geometryFactory the geometry factory
   */
  public StringToPointConverter(GeoJsonGeometryFactory geometryFactory) {
    super(geometryFactory);
  }

  @Override
  public Point convert(@NonNull String source) {
    return (Point) getGeometryFactory().createGeometryFromWellKnownText(source);
  }
}