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

package org.bremersee.geojson.example.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.bremersee.geojson.utils.GeometryUtils;
import org.springframework.stereotype.Service;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * @author Christian Bremer
 */
@Service
public class GeometryServiceImpl implements GeometryService {

    private final Long startMillis = System.currentTimeMillis();

    private Point[] positions;

    private long positionChangeInterval = 1000L;

    @PostConstruct
    public void init() {
        initPositions();
    }

    private void initPositions() {
        this.positions = new Point[] { GeometryUtils.createPointWGS84(48.847509, 8.902958),
                GeometryUtils.createPointWGS84(48.847198, 8.903281),
                GeometryUtils.createPointWGS84(48.846831, 8.903581),
                GeometryUtils.createPointWGS84(48.846477, 8.903860),
                GeometryUtils.createPointWGS84(48.846067, 8.904204),
                GeometryUtils.createPointWGS84(48.845643, 8.904483),
                GeometryUtils.createPointWGS84(48.845290, 8.904784),
                GeometryUtils.createPointWGS84(48.844895, 8.905084),
                GeometryUtils.createPointWGS84(48.844471, 8.905385),
                GeometryUtils.createPointWGS84(48.844018, 8.905879),
                GeometryUtils.createPointWGS84(48.843623, 8.906329),
                GeometryUtils.createPointWGS84(48.843313, 8.906780),
                GeometryUtils.createPointWGS84(48.842973, 8.907381),
                GeometryUtils.createPointWGS84(48.842691, 8.907897),
                GeometryUtils.createPointWGS84(48.842365, 8.908348),
                GeometryUtils.createPointWGS84(48.842055, 8.908820),
                GeometryUtils.createPointWGS84(48.841786, 8.909164),
                GeometryUtils.createPointWGS84(48.841672, 8.909787),
                GeometryUtils.createPointWGS84(48.841871, 8.910344),
                GeometryUtils.createPointWGS84(48.842367, 8.910642),
                GeometryUtils.createPointWGS84(48.842918, 8.910725),
                GeometryUtils.createPointWGS84(48.843441, 8.910660),
                GeometryUtils.createPointWGS84(48.843963, 8.910769),
                GeometryUtils.createPointWGS84(48.844781, 8.910726),
                GeometryUtils.createPointWGS84(48.845205, 8.910318),
                GeometryUtils.createPointWGS84(48.845714, 8.909827),
                GeometryUtils.createPointWGS84(48.846151, 8.909205),
                GeometryUtils.createPointWGS84(48.846306, 8.908302),
                GeometryUtils.createPointWGS84(48.846462, 8.907444),
                GeometryUtils.createPointWGS84(48.846902, 8.906738),
                GeometryUtils.createPointWGS84(48.847398, 8.906159),
                GeometryUtils.createPointWGS84(48.847978, 8.905751),
                GeometryUtils.createPointWGS84(48.848560, 8.905536),
                GeometryUtils.createPointWGS84(48.849011, 8.905278),
                GeometryUtils.createPointWGS84(48.849091, 8.904483),
                GeometryUtils.createPointWGS84(48.849212, 8.903691),
                GeometryUtils.createPointWGS84(48.849548, 8.903070),
                GeometryUtils.createPointWGS84(48.849787, 8.902385),
                GeometryUtils.createPointWGS84(48.849729, 8.901721),
                GeometryUtils.createPointWGS84(48.849391, 8.901271),
                GeometryUtils.createPointWGS84(48.848900, 8.901462),
                GeometryUtils.createPointWGS84(48.848593, 8.901888),
                GeometryUtils.createPointWGS84(48.848270, 8.902294),
                GeometryUtils.createPointWGS84(48.847890, 8.902701) };
    }

    @Override
    public Point getNextPosition() {
        synchronized (startMillis) {
            long diff = Math.abs(System.currentTimeMillis() - startMillis);
            int add = Double.valueOf(Math.floor(diff / Long.valueOf(positionChangeInterval).doubleValue())).intValue();
            int index = (positions.length + add) % positions.length;
            return positions[index];
        }
    }

    @Override
    public LinearRing getPositionsRing() {
        List<Coordinate> coords = new ArrayList<>(positions.length);
        for (int i = 0; i < positions.length; i++) {
            coords.add(positions[i].getCoordinate());
        }
        return GeometryUtils.createLinearRing(coords);
    }

    @Override
    public Polygon getPositionsBoundingBox() {
        return GeometryUtils.getBoundingBoxAsPolygon2D(getPositionsRing());
    }

}
