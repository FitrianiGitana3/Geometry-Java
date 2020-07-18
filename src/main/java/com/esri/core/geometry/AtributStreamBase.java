    

public static AttributeStreamBase createByteStream(int size) {
        AttributeStreamBase newStream = new AttributeStreamOfInt8(size);
        return newStream;
    }

public static AttributeStreamBase createByteStream(int size) {
        AttributeStreamBase newStream = new AttributeStreamOfInt8(size);
        return newStream;
    }



    public static AttributeStreamBase createByteStream(int size,
            byte defaultValue) {
        AttributeStreamBase newStream = new AttributeStreamOfInt8(size,
                defaultValue);
        return newStream;

    }

