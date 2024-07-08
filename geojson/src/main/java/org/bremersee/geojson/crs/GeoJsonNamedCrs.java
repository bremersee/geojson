/*
 * Copyright 2015-2022 the original author or authors.
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

package org.bremersee.geojson.crs;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.io.Serial;

/**
 * A reference to the coordinate reference system (CRS) of a GeoJSON object by it's name.
 *
 * <p>The specification of coordinate reference systems has been removed, see
 * <a href="https://tools.ietf.org/html/rfc7946#appendix-B">rfc7946 appendix B</a>,
 * the "crs" member is no longer used.
 *
 * @author Christian Bremer
 */
@JsonTypeName("name")
public class GeoJsonNamedCrs extends AbstractGeoJsonCrs {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * Default constructor.
   */
  public GeoJsonNamedCrs() {
  }

  /**
   * Constructs a coordinate reference system (CRS) with it's name.
   *
   * @param crsName the name of the coordinate reference system (CRS)
   */
  public GeoJsonNamedCrs(String crsName) {
    setCrs(crsName);
  }

  /**
   * Return the name of the coordinate reference system (CRS).
   *
   * @return the name of the coordinate reference system (CRS)
   */
  @JsonIgnore
  public String getCrs() {
    Object tmp = getProperties().get("name");
    return tmp == null ? null : tmp.toString();
  }

  /**
   * Set the name of the coordinate reference system (CRS).
   *
   * @param crs the name
   */
  @JsonIgnore
  public void setCrs(String crs) {
    if (crs == null || crs.trim().isEmpty()) {
      getProperties().remove("name");
    } else {
      getProperties().put("name", crs);
    }
  }

}
