package com.esri.core.geometry;

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

