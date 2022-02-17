package org.bremersee.geojson.converter;

import static org.assertj.core.api.Assertions.assertThat;

import org.bremersee.geojson.GeoJsonGeometryFactory;
import org.junit.jupiter.api.Test;

/**
 * The geometry converters test.
 *
 * @author Christian Bremer
 */
class GeometryConvertersTest {

  /**
   * Gets converters to register.
   */
  @Test
  void getConvertersToRegister() {
    assertThat(GeometryConverters.getConvertersToRegister(new GeoJsonGeometryFactory()))
        .isNotEmpty();
  }
}