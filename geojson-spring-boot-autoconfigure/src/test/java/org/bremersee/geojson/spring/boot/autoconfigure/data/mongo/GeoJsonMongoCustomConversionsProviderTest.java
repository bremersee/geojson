package org.bremersee.geojson.spring.boot.autoconfigure.data.mongo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.bremersee.geojson.GeoJsonGeometryFactory;
import org.bremersee.geojson.spring.data.mongodb.convert.GeoJsonConverters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;

/**
 * The GeoJSON mongo custom conversions provider test.
 */
class GeoJsonMongoCustomConversionsProviderTest {

  /**
   * The Target.
   */
  GeoJsonMongoCustomConversionsProvider target;

  /**
   * Setup target.
   */
  @BeforeEach
  void setup() {
    @SuppressWarnings("unchecked")
    ObjectProvider<GeoJsonGeometryFactory> provider = mock(ObjectProvider.class);
    doReturn(new GeoJsonGeometryFactory())
        .when(provider)
        .getIfAvailable(any());
    target = new GeoJsonMongoCustomConversionsProvider(provider);
  }

  /**
   * Gets custom conversions.
   */
  @Test
  void getCustomConversions() {
    assertThat(target.getCustomConversions())
        .isNotNull()
        .hasSize(GeoJsonConverters.getConvertersToRegister(new GeoJsonGeometryFactory()).size());

  }
}