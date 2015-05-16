package com.company.caching;

import com.company.blockfile.ClassInfo;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.io.Closeable;
import java.io.IOException;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkState;

public class ObjectReferenceCache implements Closeable {

    private final Cache<Integer, ClassInfo> cache;

    public ObjectReferenceCache() {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager();
        cache = cacheManager
                .getCache("objectreference", Integer.class, ClassInfo.class);
    }

    @SuppressWarnings("unchecked")
    public <T extends ClassInfo> T get(final int objectId) {
        checkState(cache.containsKey(objectId), "Registered item is not reigstered.  Logic error in parsing dataset for objectId %s.", objectId);
        return (T) cache.get(objectId);
    }

    public <T extends ClassInfo> void put(int objectId, T object) {
        cache.put(objectId, Objects.requireNonNull(object));
    }

    @Override
    public void close() throws IOException {
        cache.close();
        cache.getCacheManager().close();
        cache.getCacheManager().getCachingProvider().close();
    }
}
