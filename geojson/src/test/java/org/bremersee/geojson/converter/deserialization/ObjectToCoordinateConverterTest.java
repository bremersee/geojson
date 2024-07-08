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

import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;

/**
 * The type Object to coordinate converter test.
 *
 * @author Christian Bremer
 */
@ExtendWith({SoftAssertionsExtension.class})
class ObjectToCoordinateConverterTest {

  /**
   * Convert.
   *
   * @param softly the softly
   */
  @Test
  void convert(SoftAssertions softly) {
    ObjectToCoordinateConverter target = new ObjectToCoordinateConverter();
    softly.assertThat(target.convert(List.of(1., 2., 3.)))
        .isEqualTo(new Coordinate(1., 2., 3.));

    softly.assertThat(target.convert(List.of(1., 2.)))
        .isEqualTo(new Coordinate(1., 2.));

    softly.assertThat(target.convert(List.of(1.)))
        .extracting(Coordinate::getX)
        .isEqualTo(1.);
    softly.assertThat(target.convert(List.of(1.)))
        .extracting(Coordinate::getY)
        .isEqualTo(Double.NaN);

    softly.assertThat(target.convert(List.of()))
        .isNull();
  }
}