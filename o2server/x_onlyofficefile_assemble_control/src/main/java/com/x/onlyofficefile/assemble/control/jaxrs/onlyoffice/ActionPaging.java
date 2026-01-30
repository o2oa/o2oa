package com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.onlyofficefile.core.entity.OnlyOfficeFile;
import com.x.onlyofficefile.core.entity.OnlyOfficeFile_;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;


class ActionPaging extends BaseAction {
    Logger logger = LoggerFactory.getLogger(ActionPaging.class);

    ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement) throws Exception {
        logger.debug(effectivePerson.getDistinguishedName());
        if(effectivePerson.isNotManager()){
            throw new ExceptionAccessDenied(effectivePerson);
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<List<Wo>> result = new ActionResult<>();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

            EntityManager em = emc.get(OnlyOfficeFile.class);
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<OnlyOfficeFile> cq = cb.createQuery(OnlyOfficeFile.class);
            Root<OnlyOfficeFile> root = cq.from(OnlyOfficeFile.class);

            Predicate p = cb.conjunction();
            if (StringUtils.isNotBlank(wi.getFileId())) {
                p = cb.and(p, cb.equal(root.get(OnlyOfficeFile_.id), wi.getFileId()));
            }

            if (StringUtils.isNotBlank(wi.getFileName())) {
                p = cb.like(root.get(OnlyOfficeFile_.fileName), "%" + wi.getFileName() + "%");
            }

            if (StringUtils.isNotBlank(wi.getCreator())) {
                p = cb.and(p, cb.equal(root.get(OnlyOfficeFile_.creator), wi.getCreator()));
            }

            if (StringUtils.isNotBlank(wi.getCategory())) {
                p = cb.and(p, cb.equal(root.get(OnlyOfficeFile_.category), wi.getCategory()));
            }

            if (StringUtils.isNotBlank(wi.getDocId())) {
                p = cb.and(p, cb.equal(root.get(OnlyOfficeFile_.docId), wi.getDocId()));
            }

            if (wi.getStartTime() != null) {
                p = cb.and(p, cb.greaterThanOrEqualTo(root.get(OnlyOfficeFile_.createTime), wi.getStartTime()));
            }

            if (wi.getEndTime() != null) {
                p = cb.and(p, cb.lessThanOrEqualTo(root.get(OnlyOfficeFile_.createTime), wi.getStartTime()));
            }

            String sortField = StringUtils.isBlank(wi.getSortField()) ? JpaObject.sequence_FIELDNAME : wi.getSortField();

            List<Wo> wos;
            String ascSort = "asc";
            if(ascSort.equals(wi.getSortType())){
                wos = emc.fetchAscPaging(OnlyOfficeFile.class, Wo.copier, p, page, size, sortField);
            }else{
                wos = emc.fetchDescPaging(OnlyOfficeFile.class, Wo.copier, p, page, size, sortField);
            }

            result.setData(wos);
            result.setCount(emc.count(OnlyOfficeFile.class, p));
            return result;
        }
    }


    public static class Wo extends OnlyOfficeFile {

        private static final long serialVersionUID = -8003448718472128931L;
        static WrapCopier<OnlyOfficeFile, Wo> copier = WrapCopierFactory.wo(OnlyOfficeFile.class, Wo.class,
                JpaObject.singularAttributeField(OnlyOfficeFile.class, true, true), null);
    }


    public static class Wi extends GsonPropertyObject {

        @FieldDescribe("文件名Id")
        private String fileId;

        @FieldDescribe("文件名")
        private String fileName;

        @FieldDescribe("创建者")
        private String creator;

        @FieldDescribe("开始时间")
        private Date startTime;

        @FieldDescribe("结束时间")
        private Date endTime;

        @FieldDescribe("文档分类")
        private String category;

        @FieldDescribe("关联文档Id")
        private String docId;

        @FieldDescribe("排序字段(createTime|creator)")
        private String sortField;

        @FieldDescribe("排序(desc|asc)")
        private String sortType;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getCreator() {
            return creator;
        }

        public void setCreator(String creator) {
            this.creator = creator;
        }


        public Date getStartTime() {
            return startTime;
        }

        public void setStartTime(Date startTime) {
            this.startTime = startTime;
        }

        public Date getEndTime() {
            return endTime;
        }

        public void setEndTime(Date endTime) {
            this.endTime = endTime;
        }

        public String getSortField() {
            return sortField;
        }

        public void setSortField(String sortField) {
            this.sortField = sortField;
        }

        public String getSortType() {
            return sortType;
        }

        public void setSortType(String sortType) {
            this.sortType = sortType;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getFileId() {
            return fileId;
        }

        public void setFileId(String fileId) {
            this.fileId = fileId;
        }

        public String getDocId() {
            return docId;
        }

        public void setDocId(String docId) {
            this.docId = docId;
        }
    }
}
