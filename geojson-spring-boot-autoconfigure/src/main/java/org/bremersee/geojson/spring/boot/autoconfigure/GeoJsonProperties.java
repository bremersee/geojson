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

package org.bremersee.geojson.spring.boot.autoconfigure;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties of the GeoJSON module.
 *
 * @author Christian Bremer
 */
@ConfigurationProperties(prefix = "bremersee.geojson")
@Data
@NoArgsConstructor
public class GeoJsonProperties {

  /**
   * Specifies whether the {@link org.bremersee.geojson.GeoJsonObjectMapperModule} should use
   * {@link java.math.BigDecimal} or {@link java.lang.Double} for the coordinates.
   */
  private boolean useBigDecimal = false;

  /**
   * Specifies whether the {@link org.bremersee.geojson.GeoJsonObjectMapperModule} should always
   * include a bounding box at serialization.
   */
  private boolean withBoundingBox = false;

}
