package com.x.base.core.project.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.jaxrs.WrapClearCacheRequest;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;

public abstract class CacheManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheManager.class);

    private CacheManager() {
    }

    private static Cache cache;

    private static String name = StringTools.uniqueToken();

    public static void init(String name) throws Exception {
        CacheManager.name = name;
        cache();
    }

    private static synchronized Cache cache() throws Exception {
        if (null == cache) {
            if (StringUtils.equals(Config.cache().getType(), Cache.TYPE_REDIS)) {
                cache = new CacheRedisImpl(name);
            } else {
                cache = new CacheGuavaImpl(Cache.TYPE_GUAVA);
            }
        }
        return cache;
    }

    public static void put(CacheCategory category, CacheKey key, Object o) {
        try {
            if (o != null) {
                cache().put(category, key, o);
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    public static Optional<Object> get(CacheCategory category, CacheKey key) {
        try {
            return cache().get(category, key);
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return Optional.empty();
    }

    public static void shutdown() {
        try {
            if (null != cache) {
                cache.shutdown();
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    public static void receive(WrapClearCacheRequest wi) {
        try {
            if (null != cache) {
                cache.receive(wi);
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    public static void notify(Class<?> clz, List<Object> keys) {
        try {
            if (null != cache) {
                cache.notify(clz, keys);
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    public static void notify(Class<?> clz) {
        try {
            if (null != cache) {
                cache.notify(clz, new ArrayList<Object>());
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    public static void notify(Class<?> clz, Object... objects) {
        try {
            if (null != cache) {
                cache.notify(clz, ListTools.toList(objects));
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    public static String detail() throws Exception {
        return cache().detail();
    }
}
