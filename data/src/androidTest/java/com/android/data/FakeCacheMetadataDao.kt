package com.android.data

import com.android.events.data.local.dao.CacheMetadataDao
import com.android.events.data.local.entity.CacheMetadataEntity

class FakeCacheMetadataDao : CacheMetadataDao {

    private val metadata = mutableMapOf<String, CacheMetadataEntity>()

    override suspend fun getMetadata(key: String): CacheMetadataEntity? {
        return metadata[key]
    }

    override suspend fun insertMetadata(metadata: CacheMetadataEntity) {
        this.metadata[metadata.cacheKey] = metadata
    }
}