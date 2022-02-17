package org.bremersee.geojson.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;

class GeometryToStringConverterTest {

  private static final GeometryToStringConverter target = new GeometryToStringConverter();

  @Test
  void convert() {
    String expected = "POINT (1 0)";
    Geometry geometry = mock(Geometry.class);
    when(geometry.toText()).thenReturn(expected);
    assertThat(target.convert(geometry)).isEqualTo(expected);
  }
}