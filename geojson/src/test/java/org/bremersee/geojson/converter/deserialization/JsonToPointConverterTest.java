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
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

/**
 * The type Json to point converter test.
 *
 * @author Christian Bremer
 */
@ExtendWith(SoftAssertionsExtension.class)
class JsonToPointConverterTest {

  /**
   * Convert coordinates.
   *
   * @param softly the softly
   */
  @Test
  void convertCoordinates(SoftAssertions softly) {
    GeometryFactory geometryFactory = new GeometryFactory();
    JsonToPointConverter target = new JsonToPointConverter(
        geometryFactory,
        new ObjectToCoordinateConverter());

    Point actual = target.convertCoordinates(List.of(1., 2.));
    //noinspection unchecked
    softly.assertThat(actual)
        .isEqualTo(geometryFactory.createPoint(new Coordinate(1., 2.)));
    actual = target.convertCoordinates(List.of());
    //noinspection unchecked
    softly.assertThat(actual)
        .isEqualTo(geometryFactory.createPoint((Coordinate) null));
  }
}