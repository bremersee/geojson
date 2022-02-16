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

package org.bremersee.geojson.converter.serialization;

import static java.util.Collections.unmodifiableList;
import static org.springframework.util.ObjectUtils.isEmpty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.CoordinateSequence;
import org.springframework.lang.NonNull;

/**
 * @author Christian Bremer
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
class CoordinateSequenceToListConverter implements Serializable {

  private static final long serialVersionUID = 1L;

  private final CoordinateToListConverter coordinateConverter;

  List<List<Number>> convert(@NonNull CoordinateSequence source) {
    int size = isEmpty(source) ? 0 : source.size();
    List<List<Number>> list = new ArrayList<>(size);
    if (size > 0) {
      for (int n = 0; n < source.size(); n++) {
        list.add(coordinateConverter.convert(source.getCoordinate(n)));
      }
    }
    return unmodifiableList(list);
  }
}
