package com.x.teamwork.assemble.control.jaxrs.project;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.assemble.control.service.BatchOperationPersistService;
import com.x.teamwork.assemble.control.service.BatchOperationProcessService;
import com.x.teamwork.core.entity.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author sword
 */
public class ActionSave extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionSave.class);

    protected ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
        Wo wo = new Wo();
        if(StringUtils.isBlank(wi.getTitle())){
            throw new ProjectPersistException("标题不允许为空!");
        }
        Project old_project = projectQueryService.get(wi.getId());
        if (old_project == null) {
            if (!projectPersistService.checkPermissionForPersist(effectivePerson, systemConfigQueryService.getValueByCode(SystemConfig.ROLE_PROJECT_CREATOR))) {
                throw new ProjectPersistException("权限不足!");
            }
        }

        this.changePerson(wi, effectivePerson);
        Project project = this.saveProject(effectivePerson, wi, old_project);

        try {
            List<Dynamic> dynamics = dynamicPersistService.projectSaveDynamic(old_project, project, effectivePerson, jsonElement.toString());
            if (dynamics == null) {
                dynamics = new ArrayList<>();
            }
            if (wo != null) {
                wo.setDynamics(WoDynamic.copier.copy(dynamics));
            }
        } catch (Exception e) {
            logger.error(e);
        }
        // 更新缓存
        CacheManager.notify(Project.class);
        CacheManager.notify(Task.class);

        wo.setId(project.getId());

        result.setData(wo);
        return result;
    }

    private void changePerson(Wi wi, EffectivePerson effectivePerson) throws Exception{
        List<String> managerList = new ArrayList<>();
        for (String participant : wi.getManageablePersonList()){
            if (OrganizationDefinition.isIdentityDistinguishedName(wi.getExecutor())) {
                participant = this.userManagerService.getPersonNameWithIdentity(wi.getExecutor());
            }
            if(OrganizationDefinition.isPersonDistinguishedName(participant) && !managerList.contains(participant)){
                managerList.add(participant);
            }
        }
        if(managerList.isEmpty()){
            managerList.add(effectivePerson.getDistinguishedName());
        }
        wi.setManageablePersonList(managerList);
        wi.setExecutor(managerList.get(0));

        if(ListTools.isNotEmpty(wi.getParticipantList())){
            List<String> participantList = new ArrayList<>();
            for (String participant : wi.getParticipantList()){
                if (OrganizationDefinition.isIdentityDistinguishedName(wi.getExecutor())) {
                    participant = this.userManagerService.getPersonNameWithIdentity(wi.getExecutor());
                }
                if(OrganizationDefinition.isPersonDistinguishedName(participant) && !participantList.contains(participant)){
                    participantList.add(participant);
                }
            }
            wi.setParticipantList(participantList);
        }

    }

    private Project saveProject(EffectivePerson effectivePerson, Wi wi, Project old_project) throws Exception{
        Project project;
        boolean savePermission;
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            if (old_project != null) {
                project = emc.find(wi.getId(), Project.class);
                if( !this.isManager(wi.getId(), effectivePerson)) {
                    throw new ProjectPersistException("权限不足!");
                }
                if(project.getPublishTime() == null) {
                    project.setPublishTime(project.getCreateTime());
                }

                wi.copyTo(project, Wi.excludes);
                emc.beginTransaction(Project.class);
                emc.check(project, CheckPersistType.all);

                List<String> managerList = old_project.getManageablePersonList();
                boolean flag = managerList.size() == project.getManageablePersonList().size() && managerList.containsAll(project.getManageablePersonList());
                managerList = flag ? null : project.getManageablePersonList();
                List<String> participantList = old_project.getParticipantList();
                flag = participantList.size() == project.getParticipantList().size() && participantList.containsAll(project.getParticipantList());
                participantList = flag ? null : project.getParticipantList();
                savePermission = this.savePermission(business, project.getId(), managerList, participantList);
            } else {
                project = Wi.copier.copy(wi);
                project.setCreatorPerson(effectivePerson.getDistinguishedName());
                if(project.getPublishTime() == null) {
                    project.setPublishTime(new Date());
                }
                if (StringTools.utf8Length(project.getTitle()) > JpaObject.length_255B) {
                    project.setTitle(StringTools.utf8SubString(project.getTitle(), JpaObject.length_255B - 3) + "...");
                }
                if (StringUtils.isEmpty(project.getType())) {
                    project.setType("普通项目");
                }
                emc.beginTransaction(Project.class);
                emc.persist(project, CheckPersistType.all);

                savePermission = this.savePermission(business, project.getId(), project.getManageablePersonList(), project.getParticipantList());
            }
            emc.commit();
        }
        if(savePermission){
            try {
                new BatchOperationPersistService().addOperation(
                        BatchOperationProcessService.OPT_OBJ_PROJECT,
                        BatchOperationProcessService.OPT_TYPE_PERMISSION, project.getId(), project.getId(), "刷新文档权限：ID=" + project.getId());
            } catch (Exception e) {
                logger.error(e);
            }
        }
        return project;
    }

    private boolean savePermission(Business business, String id, List<String> managerList, List<String> participantList) throws Exception {
        if(managerList == null && participantList == null){
            return false;
        }
        EntityManagerContainer emc = business.entityManagerContainer();
        emc.beginTransaction(ProjectPermission.class);
        if(managerList != null){
            List<ProjectPermission> list = business.permissionFactory().listPermission(id, ProjectRoleEnum.MANAGER.getValue());
            Set<String> managerSet = new HashSet<>(managerList);
            for (ProjectPermission permission : list){
                if(managerList.contains(permission.getName())) {
                    managerSet.remove(permission.getName());
                }else{
                    emc.remove(permission);
                }
            }
            for (String participant : managerSet){
                ProjectPermission permission = new ProjectPermission(participant, ProjectRoleEnum.MANAGER.getValue(), id, id);
                emc.persist(permission);
            }
        }
        if(participantList != null){
            List<ProjectPermission> list = business.permissionFactory().listPermission(id, ProjectRoleEnum.READER.getValue());
            Set<String> participantSet = new HashSet<>(participantList);
            for (ProjectPermission permission : list){
                if(participantList.contains(permission.getName())) {
                    participantSet.remove(permission.getName());
                }else{
                    emc.remove(permission);
                }
            }
            for (String participant : participantSet){
                ProjectPermission permission = new ProjectPermission(participant, ProjectRoleEnum.READER.getValue(), id, id);
                emc.persist(permission);
            }
        }
        return true;
    }

    public static class Wi extends Project {

        private static final long serialVersionUID = -5718926114202220417L;

        public static final List<String> excludes = ListTools.toList(JpaObject.FieldsInvisible, Project.workStatus_FIELDNAME, Project.creatorPerson_FIELDNAME,
                Project.deleted_FIELDNAME, Project.groupCount_FIELDNAME, Project.starPersonList_FIELDNAME, Project.groupCount_FIELDNAME);

        public static WrapCopier<Wi, Project> copier = WrapCopierFactory.wi(Wi.class, Project.class, null, excludes);

    }

    public static class Wo extends WoId {

        @FieldDescribe("操作引起的动态内容")
        List<WoDynamic> dynamics = new ArrayList<>();

        public List<WoDynamic> getDynamics() {
            return dynamics;
        }

        public void setDynamics(List<WoDynamic> dynamics) {
            this.dynamics = dynamics;
        }

    }

    public static class WoDynamic extends Dynamic {

        private static final long serialVersionUID = -5076990764713538973L;

        public static WrapCopier<Dynamic, WoDynamic> copier = WrapCopierFactory.wo(Dynamic.class, WoDynamic.class, null, JpaObject.FieldsInvisible);

        private Long rank = 0L;

        public Long getRank() {
            return rank;
        }

        public void setRank(Long rank) {
            this.rank = rank;
        }
    }

}
