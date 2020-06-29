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
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by fancyLou on 2020-06-15.
 * Copyright © 2020 O2. All rights reserved.
 */
public class ActionImageDownloadWidthHeight extends BaseAction  {

    private static Logger logger = LoggerFactory.getLogger( ActionImageDownloadWidthHeight.class );

    private Ehcache cache = ApplicationCache.instance().getCache(ActionImageDownloadWidthHeight.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, Integer width, Integer height) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Wo wo = null;
            /** 确定是否要用application/octet-stream输出 */
            IMMsgFile file = emc.find(id, IMMsgFile.class);
            if (null == file) {
                throw new ExceptionFileNotExist(id);
            }
            if (!ArrayUtils.contains(IMAGE_EXTENSIONS, file.getExtension())) {
                throw new Exception("file is not image file.");
            }
            if (width < 0 || width > 5000) {
                throw new Exception("invalid width:" + width + ".");
            }
            if (height < 0 || height > 5000) {
                throw new Exception("invalid height:" + height + ".");
            }


            String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(), id+width+height);
            Element element = cache.get(cacheKey);

            if ((null != element) && (null != element.getObjectValue())) {
                wo = (Wo) element.getObjectValue();
                result.setData(wo);
            } else {
                StorageMapping mapping = ThisApplication.context().storageMappings().get(IMMsgFile.class, file.getStorage());
                if (null == mapping) {
                    throw new ExceptionStorageNotExist(file.getStorage());
                }
                try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                    file.readContent(mapping, os);
                    try (ByteArrayInputStream input = new ByteArrayInputStream(os.toByteArray())) {
                        BufferedImage src = ImageIO.read(input);
                        int scalrWidth = (width == 0) ? src.getWidth() : width;
                        int scalrHeight = (height == 0) ? src.getHeight() : height;
                        Scalr.Mode mode = Scalr.Mode.FIT_TO_WIDTH;
                        if(src.getWidth()>src.getHeight()){
                            mode = Scalr.Mode.FIT_TO_HEIGHT;
                        }
                        BufferedImage scalrImage = Scalr.resize(src,Scalr.Method.SPEED, mode, NumberUtils.min(scalrWidth, src.getWidth()),
                                NumberUtils.min(scalrHeight, src.getHeight()));
                        try (org.apache.commons.io.output.ByteArrayOutputStream baos = new org.apache.commons.io.output.ByteArrayOutputStream()) {
                            ImageIO.write(scalrImage, "png", baos);
                            byte[] bs = baos.toByteArray();

                            wo = new Wo(bs, this.contentType(false, file.getName()),
                                    this.contentDisposition(false, file.getName()));

                            cache.put(new Element(cacheKey, wo));
                            result.setData(wo);
                        }
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
            return result;
        }
    }


    public static class Wo extends WoFile {

        public Wo(byte[] bytes, String contentType, String contentDisposition) {
            super(bytes, contentType, contentDisposition);
        }

    }
}
