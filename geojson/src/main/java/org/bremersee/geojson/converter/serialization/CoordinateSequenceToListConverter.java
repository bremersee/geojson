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

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.isNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.CoordinateSequence;

/**
 * The type Coordinate sequence to list converter.
 *
 * @author Christian Bremer
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
class CoordinateSequenceToListConverter implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private final CoordinateToListConverter coordinateConverter;

  /**
   * Convert list.
   *
   * @param source the source
   * @return the list
   */
  List<List<Number>> convert(CoordinateSequence source) {
    int size = isNull(source) ? 0 : source.size();
    List<List<Number>> list = new ArrayList<>(size);
    if (size > 0) {
      for (int n = 0; n < source.size(); n++) {
        list.add(coordinateConverter.convert(source.getCoordinate(n)));
      }
    }
    return unmodifiableList(list);
  }
}
