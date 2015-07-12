package com.company.polydata;

import com.company.blockfile.ByteStreamConverter;
import com.company.blockfile.ClassInfo;

import java.util.HashMap;
import java.util.Map;

public class PropertySet extends ClassInfo {
    private final Map<Key, DataType<? extends DataType>> properties;

    public PropertySet(ByteStreamConverter converter) {
        super(converter);
        final Map<String, PolyValue> stringMap = converter.getStringMap();
        properties = new HashMap<>(stringMap.size());
        for (Map.Entry<String, PolyValue> polyEntry : stringMap.entrySet()) {
            properties.put(Key.valueOf(polyEntry.getKey()), polyEntry.getValue().getData());
        }


    }

    public Map<Key, DataType<? extends DataType>> getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return "PropertySet{" +
                "properties=" + properties +
                '}';
    }

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
}
