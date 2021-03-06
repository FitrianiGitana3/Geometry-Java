package com.esri.core.geometry;

import java.util.ArrayList;
import java.util.List;

Geometry buffer(Geometry geometry, double distance,
            SpatialReference sr, double densify_dist,
            int max_vertex_in_complete_circle, ProgressTracker progress_tracker) {
        if (geometry == null)
            throw new IllegalArgumentException();

        if (densify_dist < 0)
            throw new IllegalArgumentException();

        if (geometry.isEmpty())
            return new Polygon(geometry.getDescription());

        Envelope2D env2D = new Envelope2D();
        geometry.queryLooseEnvelope2D(env2D);
        if (distance > 0)
            env2D.inflate(distance, distance);

        m_progress_tracker = progress_tracker;

        m_original_geom_type = geometry.getType().value();
        m_geometry = geometry;
        m_tolerance = InternalUtils.calculateToleranceFromGeometry(sr,
                env2D, true);// conservative to have same effect as simplify
        m_small_tolerance = InternalUtils
                .calculateToleranceFromGeometry(null, env2D, true);

        if (max_vertex_in_complete_circle <= 0) {
            max_vertex_in_complete_circle = 96;
        }
        
        m_spatialReference = sr;
        m_distance = distance;
        m_abs_distance = Math.abs(m_distance);
        m_abs_distance_reversed = m_abs_distance != 0 ? 1.0 / m_abs_distance
                : 0;

        if (NumberUtils.isNaN(densify_dist) || densify_dist == 0) {
            densify_dist = m_abs_distance * 1e-5;
        } else {
            if (densify_dist > m_abs_distance * 0.5)
                densify_dist = m_abs_distance * 0.5;
        }

        if (max_vertex_in_complete_circle < 12)
            max_vertex_in_complete_circle = 12;

        
        double max_dd = Math.abs(distance)
                * (1 - Math.cos(Math.PI / max_vertex_in_complete_circle));

        if (max_dd > densify_dist)
            densify_dist = max_dd;
        else {
            double vertex_count = Math.PI
                    / Math.acos(1.0 - densify_dist / Math.abs(distance));
            if (vertex_count < (double) max_vertex_in_complete_circle - 1.0) {
                max_vertex_in_complete_circle = (int) vertex_count;
                if (max_vertex_in_complete_circle < 12) {
                    max_vertex_in_complete_circle = 12;
                    densify_dist = Math.abs(distance)
                            * (1 - Math.cos(Math.PI
                                    / max_vertex_in_complete_circle));
                }
            }
        }

        m_densify_dist = densify_dist;
        m_max_vertex_in_complete_circle = max_vertex_in_complete_circle;
        m_filter_tolerance = Math.min(m_small_tolerance,
                densify_dist * 0.25);
        
        
        m_circle_template_size = calcN_();
        if (m_circle_template_size != m_old_circle_template_size) {
            m_circle_template.clear();
            m_old_circle_template_size = m_circle_template_size;
        }

        Geometry result_geom = buffer_();
        m_geometry = null;
        return result_geom;     
    }

public Geometry next() {
            Point point = new Point();
            while (true) {
                if (m_index == m_mp.getPointCount()) return null;

                m_mp.getPointByVal(m_index, point);
                m_index++;
                if (point.isEmpty()) continue;
                break;
            }

            boolean b_first = false;
            if (m_buffered_polygon == null) {
                m_x = point.getX();
                m_y = point.getY();


            m_buffered_polygon = m_parent.buffer(point, m_distance, m_spatialReference, m_densify_dist, m_max_vertex_in_complete_circle, m_progress_tracker);
            b_first = true;
            }

            Geometry res;
            if (m_index < m_mp.getPointCount()) {
                res = new Polygon();
                m_buffered_polygon.copyTo(res);
            } else {
                res = m_buffered_polygon; // do not clone the last geometry.
            }

            if (!b_first){
                Transformation2D transform = new Transformation2D();
                double dx = point.getX() - m_x;
                double dy = point.getY() - m_y;
                transform.setShift(dx, dy);
                res.applyTransformation(transform);
            }

            return res;
        }




A	private static final class GlueingCursorForPolyline extends GeometryCursor {
        private Polyline m_polyline;
        private int m_current_path_index;

        GlueingCursorForPolyline(Polyline polyline) {
            m_polyline = polyline;
            m_current_path_index = 0;
        }

        @Override
        public Geometry next() {
            if (m_polyline == null)
                return null;

            MultiPathImpl mp = (MultiPathImpl) m_polyline._getImpl();
            int npaths = mp.getPathCount();
            if (m_current_path_index < npaths) {
                int ind = m_current_path_index;
                m_current_path_index++;
                if (!mp.isClosedPathInXYPlane(ind)) {
                    // connect paths that follow one another as an optimization
                    // for buffering (helps when one polyline is split into many
                    // segments).
                    Point2D prev_end = mp.getXY(mp.getPathEnd(ind) - 1);
                    while (m_current_path_index < mp.getPathCount()) {
                        Point2D start = mp.getXY(mp
                                .getPathStart(m_current_path_index));
                        if (mp.isClosedPathInXYPlane(m_current_path_index))
                            break;
                        if (start != prev_end)
                            break;

                        prev_end = mp
                                .getXY(mp.getPathEnd(m_current_path_index) - 1);
                        m_current_path_index++;
                    }
                }

                if (ind == 0
                        && m_current_path_index == m_polyline.getPathCount()) {
                    Polyline pol = m_polyline;
                    m_polyline = null;
                    return pol;
                }

                Polyline tmp_polyline = new Polyline(
                        m_polyline.getDescription());
                tmp_polyline.addPath(m_polyline, ind, true);
                for (int i = ind + 1; i < m_current_path_index; i++) {
                    tmp_polyline.addSegmentsFromPath(m_polyline, i, 0,
                            mp.getSegmentCount(i), false);
                }

                if (false) {
                    OperatorFactoryLocal.saveGeometryToEsriShapeDbg(
                            "c:/temp/_geom.bin", tmp_polyline);
                }

                if (m_current_path_index == m_polyline.getPathCount())
                    m_polyline = null;

                return tmp_polyline;
            } else {
                return null;
            }
        }
private Geometry buffer_() {
        int gt = m_geometry.getType().value();
        if (Geometry.isSegment(gt)) {// convert segment to a polyline and repeat
                                        // the call
            Polyline polyline = new Polyline(m_geometry.getDescription());
            polyline.addSegment((Segment) (m_geometry), true);
            m_geometry = polyline;
            return buffer_();
        }

        if (m_distance <= m_tolerance) {
            if (Geometry.isArea(gt)) {
                if (m_distance <= 0) {
                    Envelope2D env = new Envelope2D();
                    m_geometry.queryEnvelope2D(env);
                    if (env.getWidth() <= -m_distance * 2 || env.getHeight() <= m_distance * 2)
                        return new Polygon(m_geometry.getDescription());
                }
            } else {
                return new Polygon(m_geometry.getDescription());
            }
        }

        // Complex cases:
        switch (m_geometry.getType().value()) {
        case Geometry.GeometryType.Point:
            return bufferPoint_();
        case Geometry.GeometryType.MultiPoint:
            return bufferMultiPoint_();
        case Geometry.GeometryType.Polyline:
            return bufferPolyline_();
        case Geometry.GeometryType.Polygon:
            return bufferPolygon_();
        case Geometry.GeometryType.Envelope:
            return bufferEnvelope_();
        default:
            throw GeometryException.GeometryInternalError();
        }
    }


