package com.esri.core.geometry;

final class BucketSort {
    AttributeStreamOfInt32 m_buckets;
    AttributeStreamOfInt32 m_bucketed_indices;
    double m_min_value;
    double m_max_value;
    double m_dy;

    static int MAXBUCKETS = 65536;

    public BucketSort() {
        m_buckets = new AttributeStreamOfInt32(0);
        m_bucketed_indices = new AttributeStreamOfInt32(0);
        m_min_value = 1;
        m_max_value = -1;
        m_dy = NumberUtils.TheNaN;
    }


private boolean reset(int bucket_count, double min_value, double max_value,
            int capacity) {
        if (bucket_count < 2 || max_value == min_value)
            return false;

        int bc = Math.min(MAXBUCKETS, bucket_count);
        m_buckets.reserve(bc);
        m_buckets.resize(bc);
        m_buckets.setRange(0, 0, m_buckets.size());
        m_min_value = min_value;
        m_max_value = max_value;
        m_bucketed_indices.resize(capacity);

        m_dy = (max_value - min_value) / (bc - 1);
        return true;
    }

