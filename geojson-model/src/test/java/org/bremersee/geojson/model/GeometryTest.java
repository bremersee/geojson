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

package org.bremersee.geojson.model;

import static org.bremersee.geojson.GeoJsonConstants.BBOX;
import static org.bremersee.geojson.GeoJsonConstants.COORDINATES;
import static org.bremersee.geojson.GeoJsonConstants.GEOMETRIES;
import static org.bremersee.geojson.GeoJsonConstants.TYPE;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.geojson.model.Geometry.TypeEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The geometry test.
 *
 * @author Christian Bremer
 */
@ExtendWith(SoftAssertionsExtension.class)
class GeometryTest {

  /**
   * Test geometries with types.
   *
   * @param softly the softly
   */
  @Test
  void testGeometriesWithTypes(SoftAssertions softly) {
    for (Geometry.TypeEnum geometryType : Geometry.TypeEnum.values()) {
      Geometry.TypeEnum actualGeometryType = Geometry.TypeEnum
          .fromGeometryType(geometryType.toString());

      softly.assertThat(actualGeometryType)
          .isNotNull()
          .isEqualTo(geometryType);

      softly.assertThat(actualGeometryType)
          .isNotNull()
          .extracting(Geometry.TypeEnum::getGeometryJsonValueAttribute)
          .matches(attr -> {
            if (actualGeometryType == TypeEnum.GEOMETRYCOLLECTION) {
              return GEOMETRIES.equals(attr);
            }
            return COORDINATES.equals(attr);
          });

      Geometry geometry = new Geometry() {
        @Override
        Object getGeometryJsonValue() {
          return geometryType;
        }
      };
      geometry.setType(geometryType);
      geometry.setBbox(new BoundingBox(List
          .of(BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.TEN)));
      Map<String, Object> json = geometry.toJson();
      softly.assertThat(json.get(TYPE)).isEqualTo(geometryType.getGeometryType());
      softly.assertThat(json.get(BBOX)).isEqualTo(List.of(1., 0., 10., 10.));
      softly.assertThat(json.get(geometryType.getGeometryJsonValueAttribute()))
          .isEqualTo(geometryType);
    }
    softly.assertThat(Geometry.TypeEnum.fromGeometryType("illegal")).isNull();
  }

}