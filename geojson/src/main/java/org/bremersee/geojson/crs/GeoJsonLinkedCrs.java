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
 * A link to the coordinate reference system (CRS) of a GeoJSON object.
 *
 * <p>The specification of coordinate reference systems has been removed, see
 * <a href="https://tools.ietf.org/html/rfc7946#appendix-B">rfc7946 appendix B</a>,
 * the "crs" member is no longer used.
 *
 * @author Christian Bremer
 */
@JsonTypeName("link")
public class GeoJsonLinkedCrs extends AbstractGeoJsonCrs {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * The coordinate reference system (CRS) with type <code>proj4</code>.
   */
  public static final String TYPE_PROJ4 = "proj4";

  /**
   * The coordinate reference system (CRS) with type <code>ogcwkt</code>.
   */
  public static final String TYPE_OGC_WKT = "ogcwkt";


  /**
   * The coordinate reference system (CRS) with type <code>esriwkt</code>.
   */
  public static final String TYPE_ESRI_WKT = "esriwkt";

  /**
   * Default constructor.
   */
  public GeoJsonLinkedCrs() {
  }

  /**
   * Constructs the coordinate reference system (CRS) with the specified link.
   *
   * @param href the link to the coordinate reference system (CRS)
   */
  public GeoJsonLinkedCrs(String href) {
    setHref(href);
  }

  /**
   * Constructs the coordinate reference system (CRS) with the specified link and type.
   *
   * @param href the link to the coordinate reference system (CRS)
   * @param type the type of the coordinate reference system (CRS)
   */
  public GeoJsonLinkedCrs(String href, String type) {
    setHref(href);
    setType(type);
  }

  /**
   * Return the link to the coordinate reference system (CRS).
   *
   * @return the link to the coordinate reference system (CRS)
   */
  @JsonIgnore
  public String getHref() {
    Object tmp = getProperties().get("href");
    return tmp == null ? null : tmp.toString();
  }

  /**
   * Set the link to the coordinate reference system (CRS).
   *
   * @param href the link to the coordinate reference system (CRS)
   */
  @JsonIgnore
  public void setHref(String href) {
    if (href == null || href.trim().isEmpty()) {
      getProperties().remove("href");
    } else {
      getProperties().put("href", href);
    }
  }

  /**
   * Return the type of the coordinate reference system (CRS).
   *
   * @return the type of the coordinate reference system (CRS)
   */
  @JsonIgnore
  public String getType() {
    Object tmp = getProperties().get("type");
    return tmp == null ? null : tmp.toString();
  }

  /**
   * Set the type of the coordinate reference system (CRS).
   *
   * @param type the type of the coordinate reference system (CRS)
   */
  @JsonIgnore
  public void setType(String type) {
    if (type == null || type.trim().isEmpty()) {
      getProperties().remove("type");
    } else {
      getProperties().put("type", type);
    }
  }

}
