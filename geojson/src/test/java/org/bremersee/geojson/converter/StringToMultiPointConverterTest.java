package org.bremersee.geojson.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.bremersee.geojson.GeoJsonGeometryFactory;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.MultiPoint;

class StringToMultiPointConverterTest {

  @Test
  void convert() {
    MultiPoint expected = mock(MultiPoint.class);
    GeoJsonGeometryFactory geometryFactory = mock(GeoJsonGeometryFactory.class);
    when(geometryFactory.createGeometryFromWellKnownText(anyString()))
        .thenReturn(expected);

    StringToMultiPointConverter target = new StringToMultiPointConverter(
        geometryFactory);
    //noinspection unchecked
    assertThat(target.convert("LEGAL_WKT")).isEqualTo(expected);
  }
}