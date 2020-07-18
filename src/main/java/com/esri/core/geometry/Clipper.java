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
package com.esri.core.geometry;

import com.esri.core.geometry.VertexDescription.Persistence;
import java.nio.ByteBuffer;
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


/*
 Copyright 1995-2017 Esri
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 For additional information, contact:
 Environmental Systems Research Institute, Inc.
 Attn: Contracts Dept
 380 New York Street
 Redlands, California, USA 92373
 email: contracts@esri.com
 */


package com.esri.core.geometry;

import com.esri.core.geometry.VertexDescription.Persistence;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static com.esri.core.geometry.SizeOf.SIZE_OF_ATTRIBUTE_STREAM_OF_DBL;
import static com.esri.core.geometry.SizeOf.SIZE_OF_ATTRIBUTE_STREAM_OF_INT32;
import static com.esri.core.geometry.SizeOf.sizeOfDoubleArray;

final class AttributeStreamOfDbl extends AttributeStreamBase {

	private double[] m_buffer = null;
	private int m_size;

	public int size() {
		return m_size;
	}

	public void reserve(int reserve)
	{
		if (reserve <= 0)
			return;
		if (m_buffer == null)
			m_buffer = new double[reserve];
		else {
			if (reserve <= m_buffer.length)
				return;
			double[] buf = new double[reserve];
			System.arraycopy(m_buffer, 0, buf, 0, m_size);
			m_buffer = buf;
		}

	}

	public int capacity() {
		return m_buffer != null ? m_buffer.length : 0;
	}
	
	public AttributeStreamOfDbl(int size) {
		int sz = size;
		if (sz < 2)
			sz = 2;
		m_buffer = new double[sz];
		m_size = size;
	}

	public AttributeStreamOfDbl(int size, double defaultValue) {
		int sz = size;
		if (sz < 2)
			sz = 2;
		m_buffer = new double[sz];
		m_size = size;
		Arrays.fill(m_buffer, 0, size, defaultValue);
	}

	public AttributeStreamOfDbl(AttributeStreamOfDbl other) {
		m_buffer = other.m_buffer.clone();
		m_size = other.m_size;
	}

	public AttributeStreamOfDbl(AttributeStreamOfDbl other, int maxSize) {
		m_size = other.size();
		if (m_size > maxSize)
			m_size = maxSize;
		int sz = m_size;
		if (sz < 2)
			sz = 2;
		m_buffer = new double[sz];
		System.arraycopy(other.m_buffer, 0, m_buffer, 0, m_size);
	}

	/**
	 * Reads a value from the buffer at given offset.
	 *
	 * @param offset
	 *            is the element number in the stream.
	 */
	public double read(int offset) {
		return m_buffer[offset];
	}

	public double get(int offset) {
		return m_buffer[offset];
	}

	/**
	 * Overwrites given element with new value.
	 *
	 * @param offset
	 *            is the element number in the stream.
	 * @param value
	 *            is the value to write.
	 */
	public void write(int offset, double value) {
		if (m_bReadonly) {
			throw new RuntimeException("invalid_call");
		}
		m_buffer[offset] = value;
	}

	public void set(int offset, double value) {
		if (m_bReadonly) {
			throw new RuntimeException("invalid_call");
		}
		m_buffer[offset] = value;
	}

	/**
	 * Reads a value from the buffer at given offset.
	 *
	 * @param offset
	 *            is the element number in the stream.
	 */
	public void read(int offset, Point2D outPoint) {
		outPoint.x = m_buffer[offset];
		outPoint.y = m_buffer[offset + 1];
	}

	/**
	 * Overwrites given element with new value.
	 *
	 * @param offset
	 *            is the element number in the stream.
	 * @param value
	 *            is the value to write.
	 */
	void write(int offset, Point2D point) {
		if (m_bReadonly) {
			throw new RuntimeException("invalid_call");
		}
		m_buffer[offset] = point.x;
		m_buffer[offset + 1] = point.y;
	}

	/**
	 * Adds a new value at the end of the stream.
	 */
	public void add(double v) {
		resize(m_size + 1);
		m_buffer[m_size - 1] = v;
	}

	@Override
	public AttributeStreamBase restrictedClone(int maxsize) {
		AttributeStreamOfDbl clone = new AttributeStreamOfDbl(this, maxsize);
		return clone;
	}

	@Override
	public int virtualSize() {
		return size();
	}

	@Override
	public long estimateMemorySize()
	{
		return SIZE_OF_ATTRIBUTE_STREAM_OF_DBL + sizeOfDoubleArray(m_buffer.length);
	}

	// @Override
	// public void addRange(AttributeStreamBase src, int srcStartIndex, int
	// count) {
	// if ((src == this) || !(src instanceof AttributeStreamOfDbl))
	// throw new IllegalArgumentException();
	//
	// AttributeStreamOfDbl as = (AttributeStreamOfDbl) src;
	//
	// int len = as.size();
	// int oldSize = m_size;
	// resize(oldSize + len, 0);
	// for (int i = 0; i < len; i++) {
	// m_buffer[oldSize + i] = as.read(i);
	// }
	// }

	@Override
	public int getPersistence() {
		return Persistence.enumDouble;
	}

	@Override
	public double readAsDbl(int offset) {
		return read(offset);
	}

	@Override
	public int readAsInt(int offset) {
		return (int) read(offset);
	}

	@Override
	public long readAsInt64(int offset) {
		return (long) read(offset);
	}

	@Override
	public void resize(int newSize) {
		if (m_bLockedInSize)
			throw new GeometryException(
					"invalid call. Attribute Stream is locked and cannot be resized.");

		if (newSize <= m_size) {
			if ((newSize * 5) / 4 < m_buffer.length) {// decrease when the 25%
				// margin is exceeded
				double[] newBuffer = new double[newSize];
				System.arraycopy(m_buffer, 0, newBuffer, 0, newSize);
				m_buffer = newBuffer;
			}
			m_size = newSize;
		} else {
			if (newSize > m_buffer.length) {
				int sz = (newSize < 64) ? Math.max(newSize * 2, 4)
						: (newSize * 5) / 4;
				double[] newBuffer = new double[sz];
				System.arraycopy(m_buffer, 0, newBuffer, 0, m_size);
				m_buffer = newBuffer;
			}

			m_size = newSize;
		}
	}

	@Override
	public void resizePreserveCapacity(int newSize)// java only method
	{
		if (m_buffer == null || newSize > m_buffer.length)
			resize(newSize);
		if (m_bLockedInSize)
			throw new GeometryException(
					"invalid call. Attribute Stream is locked and cannot be resized.");

		m_size = newSize;
	}

	@Override
	public void resize(int newSize, double defaultValue) {
		if (m_bLockedInSize)
			throw new GeometryException(
					"invalid call. Attribute Stream is locked and cannot be resized.");
		if (newSize <= m_size) {
			if ((newSize * 5) / 4 < m_buffer.length) {// decrease when the 25%
				// margin is exceeded
				double[] newBuffer = new double[newSize];
				System.arraycopy(m_buffer, 0, newBuffer, 0, newSize);
				m_buffer = newBuffer;
			}
			m_size = newSize;
		} else {
			if (newSize > m_buffer.length) {
				int sz = (newSize < 64) ? Math.max(newSize * 2, 4)
						: (newSize * 5) / 4;
				double[] newBuffer = new double[sz];
				System.arraycopy(m_buffer, 0, newBuffer, 0, m_size);
				m_buffer = newBuffer;
			}

			Arrays.fill(m_buffer, m_size, newSize, defaultValue);

			m_size = newSize;
		}
	}

	@Override
	public void writeAsDbl(int offset, double d) {
		write(offset, d);
	}

	@Override
	public void writeAsInt64(int offset, long d) {
		write(offset, (double) d);
	}

	@Override
	public void writeAsInt(int offset, int d) {
		write(offset, (double) d);
	}

	/**
	 * Sets the envelope from the attribute stream. The attribute stream stores
	 * interleaved x and y. The envelope will be set to empty if the pointCount
	 * is zero.
	 */
	public void setEnvelopeFromPoints(int pointCount, Envelope2D inOutEnv) {
		if (pointCount == 0) {
			inOutEnv.setEmpty();
			return;
		}
		if (pointCount < 0)
			pointCount = size() / 2;
		else if (pointCount * 2 > size())
			throw new IllegalArgumentException();

		inOutEnv.setCoords(read(0), read(1));
		for (int i = 1; i < pointCount; i++) {
			inOutEnv.mergeNE(read(i * 2), read(i * 2 + 1));
		}
	}
