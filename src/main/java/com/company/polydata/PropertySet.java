package com.company.polydata;

import com.company.blockfile.ClassInfo;
import com.company.blockfile.DataConverterByteStream;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class PropertySet extends ClassInfo {
    public enum Key {
        ObjectType,
        FORWARDTABLE,
        BACKWARDTABLE,
        BUCKETBITS,
        NumExtended,
        ExtendedPercentUsage,
        NumRowsPerBucket,
        TotalBuckets,
        TotalNonDeletedRows,
        TotalRows,
        DefaultValue,
        Type,
        IsNullable,
        BucketID,
        Spec,
        Name,
        SwitchSize,
        NumItemsStored,
        CurrentLoadFactor,
        LoadFactor
    }
    private final Map<Key, PolyValue> properties;
    public PropertySet(DataConverterByteStream converter) {
        super(converter);
        final Map<String, PolyValue> stringMap = converter.getStringMap();
        properties = new HashMap<>(stringMap.size());
        for (Map.Entry<String, PolyValue> polyEntry : stringMap.entrySet()) {
            properties.put(Key.valueOf(polyEntry.getKey()), polyEntry.getValue());
        }


    }

    public Map<Key, PolyValue> getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return "PropertySet{" +
                "properties=" + properties +
                '}';
    }
}
