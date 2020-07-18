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
public void sort(AttributeStreamOfInt32 indices, int begin, int end,
            ClassicSort sorter) 
            {
        if (end - begin < 32) 
        {
            sorter.userSort(begin, end, indices);
            return;
        }
        boolean b_fallback = true;
        try 
        {
            double miny = NumberUtils.positiveInf();
            double maxy = NumberUtils.negativeInf();
            for (int i = begin; i < end; i++) 
            {
                double y = sorter.getValue(indices.get(i));
                if (y < miny)
                    miny = y;
                if (y > maxy)
                    maxy = y;
            }

            if (reset(end - begin, miny, maxy, end - begin)) 
            {
                for (int i = begin; i < end; i++) 
                {
                    int vertex = indices.get(i);
                    double y = sorter.getValue(vertex);
                    int bucket = getBucket(y);
                    m_buckets.set(bucket, m_buckets.get(bucket) + 1);// counting
                                                                        // values
                                                                        // in a
                                                                        // bucket.
                    m_bucketed_indices.write(i - begin, vertex);
                }

                // Recalculate buckets to contain start positions of buckets.
                int c = m_buckets.get(0);
                m_buckets.set(0, 0);
                for (int i = 1, n = m_buckets.size(); i < n; i++) 
                {
                    int b = m_buckets.get(i);
                    m_buckets.set(i, c);
                    c += b;
                }

                for (int i = begin; i < end; i++) 
                {
                    int vertex = m_bucketed_indices.read(i - begin);
                    double y = sorter.getValue(vertex);
                    int bucket = getBucket(y);
                    int bucket_index = m_buckets.get(bucket);
                    indices.set(bucket_index + begin, vertex);
                    m_buckets.set(bucket, bucket_index + 1);
                }

                b_fallback = false;
            }
        } 
        catch (Exception e) {
            m_buckets.resize(0);
            m_bucketed_indices.resize(0);
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

