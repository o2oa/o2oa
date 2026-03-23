package com.x.teamwork.assemble.control.jaxrs.project;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.assemble.control.jaxrs.task.WrapInQueryTask;
import com.x.teamwork.assemble.control.service.*;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.ProjectStatusEnum;

import java.util.List;


public class BaseAction extends StandardJaxrsAction {

    protected Cache.CacheCategory projectCache = new Cache.CacheCategory(Project.class);

    protected ProjectQueryService projectQueryService = new ProjectQueryService();

    protected ProjectPersistService projectPersistService = new ProjectPersistService();

    protected DynamicPersistService dynamicPersistService = new DynamicPersistService();

    protected ProjectGroupQueryService projectGroupQueryService = new ProjectGroupQueryService();

    protected ProjectGroupPersistService projectGroupPersistService = new ProjectGroupPersistService();

    protected SystemConfigQueryService systemConfigQueryService = new SystemConfigQueryService();

    protected ProjectConfigQueryService projectConfigQueryService = new ProjectConfigQueryService();

    protected UserManagerService userManagerService = new UserManagerService();

    protected TaskPersistService taskPersistService = new TaskPersistService();

    protected TaskQueryService taskQueryService = new TaskQueryService();

    protected static final Business BUSINESS = new Business(null);

	protected boolean isSysManager(EffectivePerson effectivePerson) throws Exception {
		return BUSINESS.isManager(effectivePerson);
	}

    protected boolean isReader(String id, EffectivePerson effectivePerson) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            return business.isManager(effectivePerson) || business.permissionFactory().isReader(id, effectivePerson.getDistinguishedName(), Boolean.TRUE);
        }
    }

    protected boolean isManager(String id, EffectivePerson effectivePerson) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            return business.isManager(effectivePerson) || business.permissionFactory().isManager(id, effectivePerson.getDistinguishedName());
        }
    }

    protected boolean canEdit(Project project, EffectivePerson effectivePerson) throws Exception {
        if (ProjectStatusEnum.ARCHIVED.getValue().equals(project.getWorkStatus())) {
            return false;
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            return business.isManager(effectivePerson) || business.permissionFactory().isManager(project.getId(), effectivePerson.getDistinguishedName());
        }
    }

    protected TaskStatistics statTask(String person, String projectId) throws Exception{
        TaskStatistics taskStatistics = new TaskStatistics();
        WrapInQueryTask wi = new WrapInQueryTask();
        wi.setProject(projectId);
        Long allCount = taskQueryService.countWithCondition(person, wi.getQueryFilter());
        taskStatistics.setAllCount(allCount);

        wi = new WrapInQueryTask();
        wi.setProject(projectId);
        wi.setWorkStatus(ProjectStatusEnum.COMPLETED.getValue());
        Long completedCount = taskQueryService.countWithCondition(person, wi.getQueryFilter());
        taskStatistics.setCompletedCount(completedCount);

        wi = new WrapInQueryTask();
        wi.setProject(projectId);
        wi.setWorkStatus(ProjectStatusEnum.CANCELED.getValue());
        Long deleteCount = taskQueryService.countWithCondition(person, wi.getQueryFilter());
        taskStatistics.setDeleteCount(deleteCount);

        wi = new WrapInQueryTask();
        wi.setProject(projectId);
        wi.setWorkStatus(ProjectStatusEnum.DELAY.getValue());
        Long delayCount = taskQueryService.countWithCondition(person, wi.getQueryFilter());
        taskStatistics.setDelayCount(delayCount);

        Long processingCount = allCount - completedCount - deleteCount - delayCount;
        taskStatistics.setProcessingCount(processingCount);

        return taskStatistics;
    }

    public static class TaskStatistics{
        @FieldDescribe("所有任务数量")
        private Long allCount = 0L;

        @FieldDescribe("已完成任务数量")
        private Long completedCount = 0L;

        @FieldDescribe("进行中的任务数量")
        private Long processingCount = 0L;

        @FieldDescribe("已搁置的任务数量")
        private Long delayCount = 0L;

        @FieldDescribe("已取消的任务数量")
        private Long deleteCount = 0L;

        public Long getAllCount() {
            return allCount;
        }

        public void setAllCount(Long allCount) {
            this.allCount = allCount;
        }

        public Long getCompletedCount() {
            return completedCount;
        }

        public void setCompletedCount(Long completedCount) {
            this.completedCount = completedCount;
        }

        public Long getProcessingCount() {
            return processingCount;
        }

        public void setProcessingCount(Long processingCount) {
            this.processingCount = processingCount;
        }

        public Long getDelayCount() {
            return delayCount;
        }

        public void setDelayCount(Long delayCount) {
            this.delayCount = delayCount;
        }

        public Long getDeleteCount() {
            return deleteCount;
        }

        public void setDeleteCount(Long deleteCount) {
            this.deleteCount = deleteCount;
        }
    }

}
