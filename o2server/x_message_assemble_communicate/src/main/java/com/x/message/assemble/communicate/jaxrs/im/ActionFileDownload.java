package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.message.assemble.communicate.ThisApplication;
import com.x.message.core.entity.IMMsgFile;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import java.io.ByteArrayOutputStream;

/**
 * Created by fancyLou on 2020-06-15.
 * Copyright © 2020 O2. All rights reserved.
 */
public class ActionFileDownload extends BaseAction  {

    private static Logger logger = LoggerFactory.getLogger( ActionFileDownload.class );

    private Ehcache cache = ApplicationCache.instance().getCache(ActionFileDownload.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Wo wo = null;
            /** 确定是否要用application/octet-stream输出 */
            IMMsgFile file = emc.find(id, IMMsgFile.class);
            if (null == file) {
                throw new ExceptionFileNotExist(id);
            }


            String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(), id);
            Element element = cache.get(cacheKey);
            if ((null != element) && (null != element.getObjectValue())) {
                wo = (Wo) element.getObjectValue();
            } else {
                StorageMapping mapping = ThisApplication.context().storageMappings().get(IMMsgFile.class, file.getStorage());
                if (null == mapping) {
                    throw new ExceptionStorageNotExist(file.getStorage());
                }
                try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                    file.readContent(mapping, os);
                    byte[] bs = os.toByteArray();
                    wo = new Wo(bs, this.contentType(false, file.getName()),
                            this.contentDisposition(false, file.getName()));
                    /**
                     * 对10M以下的文件进行缓存
                     */
                    if (bs.length < (1024 * 1024 * 10)) {
                        cache.put(new Element(cacheKey, wo));
                    }
                }catch (Exception e){
                    if(e.getMessage().indexOf("existed") > -1){
                        logger.warn("原始附件{}-{}不存在，删除记录！", file.getId(), file.getName());
                        emc.beginTransaction(IMMsgFile.class);
                        emc.delete(IMMsgFile.class, file.getId());
                        emc.commit();
                    }
                    throw e;
                }
            }
            result.setData(wo);
            return result;
        }
    }


    public static class Wo extends WoFile {

        public Wo(byte[] bytes, String contentType, String contentDisposition) {
            super(bytes, contentType, contentDisposition);
        }

    }
}
