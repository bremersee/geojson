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

package org.bremersee.geojson.converter.deserialization;

import static java.util.Objects.isNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;

/**
 * The object to coordinate sequence converter.
 *
 * @author Christian Bremer
 */
class ObjectToCoordinateSequenceConverter implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private final ObjectToCoordinateConverter coordinateConverter;

  /**
   * Instantiates a new Object to coordinate sequence converter.
   *
   * @param coordinateConverter the coordinate converter
   */
  ObjectToCoordinateSequenceConverter(ObjectToCoordinateConverter coordinateConverter) {
    if (isNull(coordinateConverter)) {
      throw new IllegalArgumentException("Coordinate converter must be present.");
    }
    this.coordinateConverter = coordinateConverter;
  }

  /**
   * Convert coordinate sequence.
   *
   * @param source the source
   * @return the coordinate sequence
   */
  CoordinateSequence convert(Object source) {
    Coordinate[] coords;
    if (isNull(source)) {
      coords = new Coordinate[0];
    } else {
      //noinspection unchecked
      coords = ((List<Object>) source).stream()
          .map(coordinateConverter::convert)
          .toArray(Coordinate[]::new);
    }
    return new CoordinateArraySequence(coords);
  }

}
