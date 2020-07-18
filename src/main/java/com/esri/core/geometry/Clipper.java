    public static AttributeStreamBase createByteStream(int size,
            byte defaultValue) {
        AttributeStreamBase newStream = new AttributeStreamOfInt8(size,
                defaultValue);
        return newStream;

    }

