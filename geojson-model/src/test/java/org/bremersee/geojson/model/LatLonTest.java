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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The latitude longitude test.
 *
 * @author Christian Bremer
 */
@ExtendWith(SoftAssertionsExtension.class)
class LatLonTest {

  /**
   * Gets latitude longitude.
   *
   * @param softly the softly
   */
  @Test
  void getLatitudeLongitude(SoftAssertions softly) {
    LatLon model = new LatLon(
        new BigDecimal("1.2345"),
        new BigDecimal("5.6789"));
    softly.assertThat(model.getLatitude())
        .isEqualTo(new BigDecimal("1.2345"));
    softly.assertThat(model.getLongitude())
        .isEqualTo(new BigDecimal("5.6789"));

    softly.assertThat(model)
        .isNotEqualTo(null);
    softly.assertThat(model)
        .isNotEqualTo(new Object());
    softly.assertThat(model)
        .isEqualTo(model);
    softly.assertThat(model.hashCode())
        .isEqualTo(model.hashCode());

    softly.assertThat(model)
        .isEqualTo(new LatLon(LatLonAware.builder()
            .latitude(new BigDecimal("1.2345"))
            .longitude(new BigDecimal("5.6789"))
            .build()));

    softly.assertThat(model.toString())
        .contains("1.2345");
    softly.assertThat(model.toString())
        .contains("5.6789");
  }

  /**
   * Json.
   *
   * @param softly the softly
   * @throws IOException the io exception
   */
  @Test
  void json(SoftAssertions softly) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    LatLon model = new LatLon(
        new BigDecimal("1.2345"),
        new BigDecimal("5.6789"));
    String json = objectMapper.writeValueAsString(model);
    softly.assertThat(json)
        .contains("1.2345");
    softly.assertThat(json)
        .contains("5.6789");

    LatLon actual = objectMapper.readValue(json, LatLon.class);
    softly.assertThat(actual)
        .isEqualTo(model);
  }
}