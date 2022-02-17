package org.bremersee.geojson.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.bremersee.geojson.GeoJsonGeometryFactory;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;

class StringToGeometryConverterTest {

  @Test
  void convert() {
    Geometry expected = mock(Geometry.class);
    GeoJsonGeometryFactory geometryFactory = mock(GeoJsonGeometryFactory.class);
    when(geometryFactory.createGeometryFromWellKnownText(anyString()))
        .thenReturn(expected);

    StringToGeometryConverter target = new StringToGeometryConverter(
        geometryFactory);
    //noinspection unchecked
    assertThat(target.convert("LEGAL_WKT")).isEqualTo(expected);
  }
}