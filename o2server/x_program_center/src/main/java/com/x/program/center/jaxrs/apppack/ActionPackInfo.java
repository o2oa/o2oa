package com.x.program.center.jaxrs.apppack;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.program.center.jaxrs.apppack.ActionLastPackFileInfo.Wo;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.config.Collect;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.program.center.core.entity.AppPackApkFile;
import com.x.program.center.core.entity.AppPackApkFile_;

/**
 * Created by fancyLou on 6/11/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ActionPackInfo extends BaseAction  {

    private static final Logger logger = LoggerFactory.getLogger(ActionPackInfo.class);


    ActionResult<Wo> execute() throws Exception {
        ActionResult<Wo> result = new ActionResult<Wo>();

        PackInfoFromServer info = getPackInfo();
        if (info != null) {
            Wo wo = Wo.copier.copy(info);
            try {
                // 查询 关键的发布apk信息，
                try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                    EntityManager em = emc.get(AppPackApkFile.class);
                    CriteriaBuilder cb = em.getCriteriaBuilder();
                    CriteriaQuery<AppPackApkFile> query = cb.createQuery(AppPackApkFile.class);
                    Root<AppPackApkFile> root = query.from(AppPackApkFile.class);
                    Predicate p = cb.equal(root.get(AppPackApkFile_.packInfoId), wo.getId());
                    query.orderBy(cb.desc(root.get(AppPackApkFile_.lastUpdateTime)));
                    query.select(root).where(p);
                    List<AppPackApkFile> list = em.createQuery(query).getResultList();
                    if (list != null && !list.isEmpty()) {
                        AppPackApkFile appPackApkFile = list.get(0);
                        wo.setAppFile(appPackApkFile);
                    }
                }
            }catch (Exception e) {
                logger.error(e);
            }
            result.setData(wo);
        } else {
            throw new ExceptionNoPackInfo();
        }
        return result;
    }




    /**
     * 打包信息对象
     */
    public static class Wo extends PackInfoFromServer {

        static WrapCopier<PackInfoFromServer, Wo> copier = WrapCopierFactory.wo(PackInfoFromServer.class, Wo.class, null,
                JpaObject.FieldsInvisible);
    }
}
