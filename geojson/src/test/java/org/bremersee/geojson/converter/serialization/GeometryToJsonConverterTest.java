package org.bremersee.geojson.converter.serialization;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

class GeometryToJsonConverterTest {

  @Test
  void convertAndExpectIllegalArgumentException() {
    GeometryToJsonConverter target = new GeometryToJsonConverter();
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> target.convert(mock(IllegalGeometry.class)));
  }

  private static abstract class IllegalGeometry extends Geometry {

    /**
     * Creates a new <code>Geometry</code> via the specified GeometryFactory.
     *
     * @param factory the geometry factory
     */
    private IllegalGeometry(GeometryFactory factory) {
      super(factory);
    }
  }
}