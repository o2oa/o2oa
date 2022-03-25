package com.x.program.center.jaxrs.apppack;

import java.io.ByteArrayOutputStream;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.ThisApplication;
import com.x.program.center.core.entity.AppPackApkFile;

/**
 * Created by fancyLou on 11/29/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ActionPackFileDownload  extends BaseAction  {

    private static Logger logger = LoggerFactory.getLogger(ActionPackFileDownload.class);


    ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        if (StringUtils.isBlank(id)) {
            throw new ExceptionFileIdEmpty();
        }

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            AppPackApkFile apkFile = emc.find(id, AppPackApkFile.class);
            if (null == apkFile) {
                throw new ExceptionFileNotExist(id);
            }
            Wo wo = null;
            Cache.CacheCategory cacheCategory = new Cache.CacheCategory(ActionPackFileDownload.class);
            Cache.CacheKey cacheKey = new Cache.CacheKey(this.getClass(), id);
            Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
            if (optional.isPresent()) {
                wo = (Wo) optional.get();
            } else {
                StorageMapping mapping = ThisApplication.context().storageMappings().get(AppPackApkFile.class,
                        apkFile.getStorage());
                if (null == mapping) {
                    throw new ExceptionStorageNotExist(apkFile.getStorage());
                }
                try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                    apkFile.readContent(mapping, os);
                    byte[] bs = os.toByteArray();
                    wo = new Wo(bs, this.contentType(false, apkFile.getName()),
                            this.contentDisposition(false, apkFile.getName()));
                    // 对10M以下的文件进行缓存
                    if (bs.length < (1024 * 1024 * 10)) {
                        CacheManager.put(cacheCategory, cacheKey, wo);
                    }
                } catch (Exception e) {
                    if (e.getMessage().contains("existed")) {
                        logger.warn("原始附件{}-{}不存在，删除记录！", apkFile.getId(), apkFile.getName());
                    }
                    throw e;
                }
            }
            result.setData(wo);
        }

        return result;
    }

    public static class Wo extends WoFile {

        public Wo(byte[] bytes, String contentType, String contentDisposition) {
            super(bytes, contentType, contentDisposition);
        }

    }
}
