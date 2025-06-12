package com.x.program.center.jaxrs.apppack;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import java.util.Optional;

public class ActionPackInfoLogoDownload extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(ActionPackInfoLogoDownload.class);

    ActionResult<Wo> execute() throws Exception {

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            PackInfoFromServer info = getPackInfo();
            if (info == null  ) {
                throw new ExceptionNoPackInfo();
            }
            Wo wo = null;
            Cache.CacheCategory cacheCategory = new Cache.CacheCategory(ActionPackInfoLogoDownload.class);
            Cache.CacheKey cacheKey = new Cache.CacheKey(this.getClass(), info.getId());
            Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
            if (optional.isPresent()) {
                wo = (Wo) optional.get();
            } else {
                String url = Config.collect().appPackServerUrl() + info.getAppLogoPath() + "?token=" + getPackServerSSOToken();
                logger.info("下载 logo 的url： " + url);
                byte[] bytes = ConnectionAction.getBinary(url, null);
                wo = new Wo(bytes, this.contentType(false, "logo.png"),
                        this.contentDisposition(false, "logo.png"));
                // 对10M以下的文件进行缓存
                if (bytes.length < (1024 * 1024 * 10)) {
                    CacheManager.put(cacheCategory, cacheKey, wo);
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
