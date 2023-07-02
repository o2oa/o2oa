package com.x.program.center.jaxrs.apppack;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.core.entity.AppPackApkFile;
import com.x.program.center.core.entity.AppPackApkFile_;

/**
 * 最新发布的app下载包
 * Created by fancyLou on 11/29/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ActionLastPackFileInfo extends BaseAction  {

    private static Logger logger = LoggerFactory.getLogger(ActionLastPackFileInfo.class);

    ActionResult<Wo> execute() throws Exception {
        ActionResult<Wo> result = new ActionResult<Wo>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            EntityManager em = emc.get(AppPackApkFile.class);
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<AppPackApkFile> query = cb.createQuery(AppPackApkFile.class);
            Root<AppPackApkFile> root = query.from(AppPackApkFile.class);
            Predicate p = cb.equal(root.get(AppPackApkFile_.status), AppPackApkFile.statusCompleted);
            query.orderBy(cb.desc(root.get(AppPackApkFile_.lastUpdateTime)));
            query.select(root).where(p);
            List<AppPackApkFile> list = em.createQuery(query).setMaxResults(1).getResultList();
            if (list != null && !list.isEmpty()) {
                AppPackApkFile file = list.get(0);
                Wo wo = Wo.copier.copy(file);
                result.setData(wo);
            } else {
                logger.info("没有找到最新的打包发布数据");
//                throw new ExceptionFileNotExist(null);
            }
        }
        return result;
    }


    public static class Wo extends AppPackApkFile {
        static WrapCopier<AppPackApkFile, Wo> copier = WrapCopierFactory.wo(AppPackApkFile.class, Wo.class, null,
                JpaObject.FieldsInvisible);
    }
}
