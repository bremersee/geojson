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

package org.bremersee.geojson.converter.serialization;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

/**
 * The type Geometry to json converter test.
 *
 * @author Christian Bremer
 */
class GeometryToJsonConverterTest {

  /**
   * Convert and expect illegal argument exception.
   */
  @Test
  void convertAndExpectIllegalArgumentException() {
    GeometryToJsonConverter target = new GeometryToJsonConverter();
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> target.convert(mock(IllegalGeometry.class)));
  }

  private abstract static class IllegalGeometry extends Geometry {

    /**
     * Creates a new <code>Geometry</code> via the specified GeometryFactory.
     *
     * @param factory the geometry factory
     */
    private IllegalGeometry(GeometryFactory factory) {
      super(factory);
    }
  }
}