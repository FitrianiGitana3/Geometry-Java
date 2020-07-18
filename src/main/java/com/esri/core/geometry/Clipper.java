package com.esri.core.geometry;
package com.esri.core.geometry;

import com.esri.core.geometry.VertexDescription.Persistence;
import java.nio.ByteBuffer;
class Clipper 
{

    int checkSegmentIntersection_(Envelope2D seg_env, 
            int side,
            double clip_value) 
            {
        switch (side) 
        {
        case 0:
            if (seg_env.xmin < clip_value && seg_env.xmax <= clip_value) 
            {
                return 0; // outside (or on the border)
            } 
            else
                return -1; // intersects
        case 1:
            if (seg_env.ymin < clip_value && seg_env.ymax <= clip_value) 
            {
                return 0;
            } 
            else
                return -1;
        case 2:
            if (seg_env.xmin >= clip_value && seg_env.xmax > clip_value) 
            {
                return 0;
            } 
            else
                return -1;
        case 3:
            if (seg_env.ymin >= clip_value && seg_env.ymax > clip_value) 
            {
                return 0;
            } 
            else
                return -1;
        }
        return 0;
    }

class Boundary {

    static boolean hasNonEmptyBoundary(Geometry geom, ProgressTracker progress_tracker) {
        if (geom.isEmpty())return false;

        Geometry.Type gt = geom.getType();
        if (gt == Geometry.Type.Polygon) {
            if (geom.calculateArea2D() == 0) return false;

            return true;
        } else if (gt == Geometry.Type.Polyline) {
            boolean[] b = new boolean[1];
            b[0] = false;
            calculatePolylineBoundary_(geom._getImpl(), progress_tracker, true,b);
            return b[0];
        } else if (gt == Geometry.Type.Envelope) {
            return true;
        } else if (Geometry.isSegment(gt.value())) {
            if (!((Segment) geom).isClosed()) {
                return true;
            }return false;
        } else if (Geometry.isPoint(gt.value())) {
            return false;
        }return false;
    }
