/*
 * Copyright 2015 the original author or authors.
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

package org.bremersee.geojson.example.web;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.bremersee.geojson.GeoJsonFeature;
import org.bremersee.geojson.GeoJsonFeatureCollection;
import org.bremersee.geojson.example.service.GeometryService;
import org.bremersee.geojson.utils.GeometryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * @author Christian Bremer
 */
@Controller
public class WebController {

    @Autowired
    protected GeometryService geometryService;

    @RequestMapping(value = { "/index.html","/main.html" }, 
            method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String displayMainPage(HttpServletRequest request, ModelMap model) {

        return "main";
    }

    @RequestMapping(value = "/static-features.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody GeoJsonFeatureCollection getStaticFeatures() {

        //@formatter:off
    	LinearRing linearRing = geometryService.getPositionsRing();
    	Map<String, Object> linearRingProperties = new LinkedHashMap<>();
    	linearRingProperties.put("color", "blue");
    	linearRingProperties.put("width", 2);
    	GeoJsonFeature positionsRingFeature = new GeoJsonFeature(
    			null, 
    			GeometryUtils.MERCATOR_CRS, 
    			GeometryUtils.transformWgs84ToMercator(linearRing, true), 
    			false, 
    			linearRingProperties);
    	
    	Polygon poly = geometryService.getPositionsBoundingBox();
    	Map<String, Object> polyProperties = new LinkedHashMap<>();
    	polyProperties.put("color", "red");
    	polyProperties.put("width", 4);
    	GeoJsonFeature positionsBoundingBoxFeature = new GeoJsonFeature(
    			null, 
    			GeometryUtils.MERCATOR_CRS, 
    			GeometryUtils.transformWgs84ToMercator(poly, true), 
    			false, 
    			polyProperties);
    	
		GeoJsonFeatureCollection col = new GeoJsonFeatureCollection();
		col.getFeatures().add(positionsRingFeature);
		col.getFeatures().add(positionsBoundingBoxFeature);
		return col;
    	//@formatter:on
    }

    @RequestMapping(value = "/current-position.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody GeoJsonFeature getCurrentPosition() {

        //@formatter:off
    	Point position = geometryService.getNextPosition();
    	return new GeoJsonFeature(
    			null, 
    			GeometryUtils.MERCATOR_CRS, 
    			GeometryUtils.transformWgs84ToMercator(position, true), 
    			false, 
    			null);
    	//@formatter:on
    }

}
