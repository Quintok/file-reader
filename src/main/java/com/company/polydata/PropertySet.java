package com.company.polydata;

import com.company.ClassInfo;
import com.company.DataConverterByteStream;

import java.nio.ByteBuffer;
import java.util.Map;

public class PropertySet extends ClassInfo {
    private Map<String, PolyValue> properties;
    public PropertySet(ByteBuffer input) {
        super(input);
        properties = DataConverterByteStream.getStringPointerMap(PolyValue.class, buffer);
    }
}
