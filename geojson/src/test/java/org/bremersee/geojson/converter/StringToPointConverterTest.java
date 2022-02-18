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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.bremersee.geojson.GeoJsonGeometryFactory;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;

/**
 * The type String to point converter test.
 *
 * @author Christian Bremer
 */
class StringToPointConverterTest {

  /**
   * Convert.
   */
  @Test
  void convert() {
    Point expected = mock(Point.class);
    GeoJsonGeometryFactory geometryFactory = mock(GeoJsonGeometryFactory.class);
    when(geometryFactory.createGeometryFromWellKnownText(anyString()))
        .thenReturn(expected);

    StringToPointConverter target = new StringToPointConverter(
        geometryFactory);
    //noinspection unchecked
    assertThat(target.convert("LEGAL_WKT")).isEqualTo(expected);
  }
}