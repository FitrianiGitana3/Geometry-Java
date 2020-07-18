    package com.esri.core.geometry;

import com.esri.core.geometry.VertexDescription.Persistence;
import java.nio.ByteBuffer;

/**
 * Base class for AttributeStream instances.
 */
abstract class AttributeStreamBase {

    protected boolean m_bLockedInSize;
    protected boolean m_bReadonly;

    public AttributeStreamBase() {
        m_bReadonly = false;
        m_bLockedInSize = false;
    }

    /**
     * Returns the number of elements in the stream.
     */
    public abstract int virtualSize();

    /**
     * Returns an estimate of this object size in bytes.
     *
     * @return Returns an estimate of this object size in bytes.
     */
    public abstract long estimateMemorySize();

    /**
     * Returns the Persistence type of the stream.
     */
    public abstract int getPersistence();

    /**
     * Reads given element and returns it as double.
     */
    public abstract double readAsDbl(int offset);

    /**
     * Writes given element as double. The double is cast to the internal
     * representation (truncated when int).
     */
    public abstract void writeAsDbl(int offset, double d);

/**
     * Same as resize(0)
     */
    void clear(boolean bFreeMemory) 
    {
        if (bFreeMemory)
            resize(0);
        else
            resizePreserveCapacity(0);
    }




public static AttributeStreamBase createByteStream(int size) {
        AttributeStreamBase newStream = new AttributeStreamOfInt8(size);
        return newStream;
    }
/**
     * Adds a range of elements from the source stream. The streams must be of
     * the same type.
     * 
     * @param src
     *            The source stream to read elements from.
     * @param srcStart
     *            The index of the element in the source stream to start reading
     *            from.
     * @param count
     *            The number of elements to add.
     * @param bForward
     *            True if adding the elements in order of the incoming source
     *            stream. False if adding the elements in reverse.
     * @param stride
     *            The number of elements to be grouped together if adding the
     *            elements in reverse.
     */
    public abstract void addRange(AttributeStreamBase src, int srcStart,
            int count, boolean bForward, int stride);

    /**
     * Inserts a range of elements from the source stream. The streams must be
     * of the same type.
     * 
     * @param start
     *            The index where to start the insert.
     * @param src
     *            The source stream to read elements from.
     * @param srcStart
     *            The index of the element in the source stream to start reading
     *            from.
     * @param count
     *            The number of elements to read from the source stream.
     * @param validSize
     *            The number of valid elements in this stream.
     */
    public abstract void insertRange(int start, AttributeStreamBase src,
            int srcStart, int count, boolean bForward, int stride, int validSize);

    /**
     * Inserts a range of elements of the given value.
     * 
     * @param start
     *            The index where to start the insert.
     * @param value
     *            The value to be inserted.
     * @param count
     *            The number of elements to be inserted.
     * @param validSize
     *            The number of valid elements in this stream.
     */
    public abstract void insertRange(int start, double value, int count,
            int validSize);

    /**
     * Inserts the attributes of a given semantics from a Point geometry.
     * 
     * @param start
     *            The index where to start the insert.
     * @param pt
     *            The Point geometry holding the attributes to be inserted.
     * @param semantics
     *            The attribute semantics that are being inserted.
     * @param validSize
     *            The number of valid elements in this stream.
     */
    public abstract void insertAttributes(int start, Point pt, int semantics,
            int validSize);

    /**
     * Sets a range of values to given value.
     * 
     * @param value
     *            The value to set stream elements to.
     * @param start
     *            The index of the element to start writing to.
     * @param count
     *            The number of elements to set.
     */
    public abstract void setRange(double value, int start, int count);

    /**
     * Adds a range of elements from the source byte buffer. This stream is
     * resized automatically to accomodate required number of elements.
     * 
     * @param startElement
     *            the index of the element in this stream to start setting
     *            elements from.
     * @param count
     *            The number of AttributeStream elements to read.
     * @param src
     *            The source ByteBuffer to read elements from.
     * @param sourceStart
     *            The offset from the start of the ByteBuffer in bytes.
     * @param bForward
     *            When False, the source is written in reversed order.
     * @param stride
     *            Used for reversed writing only to indicate the unit of
     *            writing. elements inside a stride are not reversed. Only the
     *            strides are reversed.
     */
    public abstract void writeRange(int startElement, int count,
            AttributeStreamBase src, int sourceStart, boolean bForward,
            int stride);

    /**
     * Adds a range of elements from the source byte buffer. The stream is
     * resized automatically to accomodate required number of elements.
     * 
     * @param startElement
     *            the index of the element in this stream to start setting
     *            elements from.
     * @param count
     *            The number of AttributeStream elements to read.
     * @param src
     *            The source ByteBuffer to read elements from.
     * @param offsetBytes
     *            The offset from the start of the ByteBuffer in bytes.
     */
    public abstract void writeRange(int startElement, int count,
            ByteBuffer src, int offsetBytes, boolean bForward);

    /**
     * Write a range of elements to the source byte buffer.
     * 
     * @param srcStart
     *            The element index to start writing from.
     * @param count
     *            The number of AttributeStream elements to write.
     * @param dst
     *            The destination ByteBuffer. The buffer must be large enough or
     *            it will throw.
     * @param dstOffsetBytes
     *            The offset in the destination ByteBuffer to start write
     *            elements from.
     */
    public abstract void readRange(int srcStart, int count, ByteBuffer dst,
            int dstOffsetBytes, boolean bForward);

    /**
     * Erases a range from the buffer and defragments the result.
     * 
     * @param index
     *            The index in this stream where the erasing starts.
     * @param count
     *            The number of elements to be erased.
     * @param validSize
     *            The number of valid elements in this stream.
     */
    public abstract void eraseRange(int index, int count, int validSize);

    /**
     * Reverses a range from the buffer.
     * 
     * @param index
     *            The index in this stream where the reversing starts.
     * @param count
     *            The number of elements to be reversed.
     * @param stride
     *            The number of elements to be grouped together when doing the
     *            reverse.
     */
    public abstract void reverseRange(int index, int count, int stride);

    public static AttributeStreamBase createByteStream(int size,
            byte defaultValue) {
        AttributeStreamBase newStream = new AttributeStreamOfInt8(size,
                defaultValue);
        return newStream;

    }

