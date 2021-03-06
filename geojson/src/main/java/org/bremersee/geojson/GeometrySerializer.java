/*
 * Copyright 2015-2020 the original author or authors.
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

package org.bremersee.geojson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.io.Serializable;
import org.bremersee.geojson.utils.ConvertHelper;
import org.locationtech.jts.geom.Geometry;

/**
 * A Jackson serializer for a {@link Geometry}.
 *
 * @author Christian Bremer
 */
public class GeometrySerializer extends StdSerializer<Geometry> implements Serializable {

  private static final long serialVersionUID = 2L;

  private final ConvertHelper convertHelper;

  /**
   * Default constructor.
   */
  public GeometrySerializer() {
    super(Geometry.class, false);
    this.convertHelper = new ConvertHelper();
  }

  @Override
  public void serialize(
      final Geometry value,
      final JsonGenerator jgen,
      final SerializerProvider provider)
      throws IOException {

    if (value == null) {
      jgen.writeNull();
    } else {
      jgen.writeObject(convertHelper.create(value));
    }
  }

}
