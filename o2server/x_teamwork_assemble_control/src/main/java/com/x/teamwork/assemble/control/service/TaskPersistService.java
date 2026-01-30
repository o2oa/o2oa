package com.x.teamwork.assemble.control.service;

import java.util.*;

import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.DateTools;
import com.x.teamwork.core.entity.*;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.common.date.DateOperation;
import com.x.teamwork.assemble.control.Business;

/**
 * 对工作任务信息查询的服务
 *
 * @author O2LEE
 */
public class TaskPersistService {

    private TaskService taskService = new TaskService();
    private TaskGroupService taskGroupService = new TaskGroupService();
    private UserManagerService userManagerService = new UserManagerService();

    /**
     * 删除任务信息
     *
     * @param flag
     * @param effectivePerson
     * @throws Exception
     */
    public void delete(String flag, EffectivePerson effectivePerson) throws Exception {
        if (StringUtils.isEmpty(flag)) {
            throw new Exception("flag is empty.");
        }
        Boolean hasDeletePermission = false;
        if (effectivePerson.isManager()) {
            hasDeletePermission = true;
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Task task = taskService.get(emc, flag);
            //管理员可以删除，创建者可以删除，工作任务创建者、管理者都可以删除
            if (!hasDeletePermission) {
                if (task.getCreatorPerson().equalsIgnoreCase(effectivePerson.getDistinguishedName())) {
                    hasDeletePermission = true;
                } else if (ListTools.isNotEmpty(task.getManageablePersonList()) && task.getManageablePersonList().contains(effectivePerson.getDistinguishedName())) {
                    hasDeletePermission = true;
                }
            }
            if (!hasDeletePermission) {
                throw new Exception("task delete permission denied.");
            } else {
                taskService.remove(emc, flag);
                taskGroupService.refreshTaskCountInTaskGroupWithTaskId(emc, effectivePerson.getDistinguishedName(), task.getId());
            }
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 保存工作任务信息
     *
     * @param task
     * @param taskDetail
     * @param taskExtField
     * @param effectivePerson
     * @return
     * @throws Exception
     */
    public Task save(Task task, TaskDetail taskDetail, TaskExtField taskExtField, EffectivePerson effectivePerson) throws Exception {
        if (task == null) {
            throw new Exception("task is null.");
        }

        if (StringUtils.isEmpty(task.getName())) {
            task.setName("无标题工作任务(" + DateOperation.getNowDateTime() + ")");
        }
        if (StringUtils.isEmpty(task.getParent())) {
            task.setParent("0");
        }

        if (StringUtils.isEmpty(task.getExecutor())) {
            task.setExecutor(effectivePerson.getDistinguishedName());
        }
        if (StringUtils.isEmpty(task.getPriority())) {
            PriorityQueryService priorityQueryService = new PriorityQueryService();
            List<Priority> prioritys = priorityQueryService.listPriorityByPerson(effectivePerson.getDistinguishedName());
            if (ListTools.isNotEmpty(prioritys)) {
                Priority priority = prioritys.get(0);
                if (priority != null) {
                    task.setPriority(priority.getPriority() + "||" + priority.getPriorityColor());
                }
            }
        }

        if (StringUtils.isEmpty(task.getWorkStatus())) {
            task.setWorkStatus(TaskStatuType.processing.name());
        }

        //校验工作任务的状态变更
        if (TaskStatuType.completed.name().equalsIgnoreCase(task.getWorkStatus())) {
            task.setCompleted(true);
            task.setArchive(false);
        } else if (TaskStatuType.processing.name().equalsIgnoreCase(task.getWorkStatus())) {
            task.setCompleted(false);
            task.setArchive(false);
        } else if (TaskStatuType.archived.name().equalsIgnoreCase(task.getWorkStatus())) {
            task.setCompleted(true);
            task.setArchive(true);
        }


        String executor = null;
        String executorIdentity = null;
        String executorUnit = null;

        //前端可能传身份，也可以直接传Person
		/*if( StringUtils.isNotEmpty( task.getExecutorIdentity() ) ) {
			executor = userManagerService.getPersonNameWithIdentity(  task.getExecutorIdentity() );
			if( StringUtils.isEmpty( executor )) {
				throw new Exception("executor  identity invalid , executorIdentity:" + task.getExecutorIdentity());
			}
			task.setExecutor( executor );
		}else {
			if( StringUtils.isNotEmpty( task.getExecutor()) ) {
				executorIdentity = userManagerService.getIdentityWithPerson( task.getExecutor(), "min");
				if( StringUtils.isEmpty( executorIdentity )) {
					throw new Exception("executor  has no identity, please concat manager! person:" + task.getExecutor());
				}
				task.setExecutorIdentity(executorIdentity);
			}else {
				throw new Exception("executor  can not empty! ");
			}
		}

		if( StringUtils.isNotEmpty( task.getExecutorIdentity() ) ) {
			executorUnit = userManagerService.getUnitNameByIdentity( task.getExecutorIdentity() );
			if( StringUtils.isEmpty( executorUnit)) {
				throw new Exception("executor unit not exists with identity:" + task.getExecutorIdentity());
			}
			task.setExecutorUnit( executorUnit );
		}*/

        if (StringUtils.isEmpty(task.getCreatorPerson())) {
            task.setCreatorPerson(effectivePerson.getDistinguishedName());
        }
        if (ListTools.isEmpty(task.getManageablePersonList())) {
            task.addManageablePerson(effectivePerson.getDistinguishedName());
        }
        if (task.getName().length() > 80) {
            task.setName(task.getName().substring(0, 80) + "...");
        }

        if (task.getStartTime() == null) {
            task.setStartTime(new Date());
        }
        if (task.getEndTime() == null) {
            task.setEndTime(new Date((task.getStartTime().getTime() + 7 * 1440 * 60 * 1000))); //30分钟之后
        }

        if (ListTools.isEmpty(task.getParticipantList())) {
            task.addParticipant(effectivePerson.getDistinguishedName());
        }

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            task = taskService.save(emc, task, taskDetail, taskExtField);
        } catch (Exception e) {
            throw e;
        }
        return task;
    }

    /**
     * 更新工作任务的标签信息
     *
     * @param id
     * @param new_tags
     * @param add_tags
     * @param remove_tags
     * @param effectivePerson
     * @return
     * @throws Exception
     */
    public List<String> updateTag(String id, List<String> new_tags, List<String> add_tags, List<String> remove_tags, EffectivePerson effectivePerson) throws Exception {
        if (StringUtils.isEmpty(id)) {
            throw new Exception("id can not empty in operation updateTag.");
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            return taskService.updateTag(emc, id, new_tags, add_tags, remove_tags, effectivePerson);
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * 更新任务的上级任务ID信息
     *
     * @param taskId
     * @param parentId
     * @param effectivePerson
     * @throws Exception
     */
    public void updateParentId(String taskId, String parentId, EffectivePerson effectivePerson) throws Exception {

        if (StringUtils.isEmpty(taskId)) {
            throw new Exception("taskId can not empty in update parentId.");
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Task task = emc.find(taskId, Task.class);
            if (task != null) {
                task.setParent(parentId);
            }
            emc.beginTransaction(Task.class);
            emc.check(task, CheckPersistType.all);
            emc.commit();
        } catch (Exception e) {
            throw e;
        }

    }

    /**
     * 查询用户是否拥有创建工作任务的权限
     *
     * @param effectivePerson
     * @return
     * @throws Exception
     */
    public boolean checkPermissionForPersist(Project project, EffectivePerson effectivePerson) throws Exception {
        if (effectivePerson.isManager()) {
            return true;
        }
        String personName = effectivePerson.getDistinguishedName();
        if (userManagerService.isHasPlatformRole(personName, "TeamWorkManager")) {
            return true;
        }
        //项目的创建者，负责人以及参与者都应该可以在项目内项目任务
        if (project.getCreatorPerson().equalsIgnoreCase(personName)) {
            return true;
        }
        if (project.getExecutor().equalsIgnoreCase(personName)) {
            return true;
        }
        //查询用户所在的所有组织和群组
        if (ListTools.isNotEmpty(project.getParticipantList()) && project.getParticipantList().contains(personName)) {
            return true;
        }

        List<String> groupNames = userManagerService.listGroupNamesByPerson(personName);
        List<String> unitNames = userManagerService.listUnitNamesWithPerson(personName);

        if (ListTools.isNotEmpty(unitNames)) {
            unitNames.retainAll(project.getParticipantList());
            if (ListTools.isNotEmpty(unitNames)) {
                return true;
            }
        }

        if (ListTools.isNotEmpty(groupNames)) {
            groupNames.retainAll(project.getParticipantList());
            if (ListTools.isNotEmpty(groupNames)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 添加工作任务的参与者
     *
     * @param id
     * @param participants_source
     * @return
     * @throws Exception
     */
    public Task addParticipants(String id, List<String> participants_source)
            throws Exception {
        if (StringUtils.isEmpty(id)) {
            throw new Exception("id is empty.");
        }
        Task task = null;
        List<String> participants = new ArrayList<>();
        if (ListTools.isNotEmpty(participants_source)) {
            for (String participant : participants_source) {
                if (!participants.contains(participant)) {
                    participants.add(participant);
                }
            }
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            task = emc.find(id, Task.class);
            if (task != null) {
                task.setParticipantList(participants);
                emc.beginTransaction(Task.class);
                emc.check(task, CheckPersistType.all);
                emc.commit();
            } else {
                throw new Exception("task not exists.id=" + id);
            }
        } catch (Exception e) {
            throw e;
        }
        return task;
    }

    /**
     * 向工作任务管理者中添加指定人员
     *
     * @param id
     * @param managers
     * @param effectivePerson
     * @return
     * @throws Exception
     */
    public Task addManager(String id, List<String> managers, EffectivePerson effectivePerson) throws Exception {
        if (StringUtils.isEmpty(id)) {
            throw new Exception("id is empty.");
        }
        Task task = null;
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            task = emc.find(id, Task.class);
            if (task != null) {
                task.addManageablePerson(managers);

                emc.beginTransaction(Task.class);
                emc.check(task, CheckPersistType.all);
                emc.commit();
            } else {
                throw new Exception("task not exists.id=" + id);
            }
        } catch (Exception e) {
            throw e;
        }
        return task;
    }

    /**
     * 从工作任务管理者中移除指定人员
     *
     * @param id
     * @param managers
     * @param effectivePerson
     * @return
     * @throws Exception
     */
    public Task removeParticipants(String id, List<String> managers, EffectivePerson effectivePerson) throws Exception {
        if (StringUtils.isEmpty(id)) {
            throw new Exception("id is empty.");
        }
        Task task = null;
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            task = emc.find(id, Task.class);
            if (task != null) {
                task.removeManageablePerson(managers);

                emc.beginTransaction(Task.class);
                emc.check(task, CheckPersistType.all);
                emc.commit();
            } else {
                throw new Exception("task not exists.id=" + id);
            }
        } catch (Exception e) {
            throw e;
        }
        return task;
    }

    /**
     * 对指定的工作任务进行归档，递归执行所有的子任务
     *
     * @param taskId
     * @throws Exception
     */
    public void archiveTask(String taskId) throws Exception {
        if (StringUtils.isEmpty(taskId)) {
            throw new Exception("taskId is empty.");
        }
        Task task = null;
        List<Review> subTaskReviews = null;
        List<String> subTaskReviewIds = null;
        List<String> subTaskIds = null;
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            //先归档子任务
            subTaskIds = business.taskFactory().listByParent(taskId);
            if (ListTools.isNotEmpty(subTaskIds)) {
                for (String subTaskId : subTaskIds) {
                    archiveTask(subTaskId);
                }
            }
            task = emc.find(taskId, Task.class);
            subTaskReviewIds = business.reviewFactory().listReviewByTask(taskId, 999);
            //Task
            if (task != null) {
                task.setArchive(true);
                emc.beginTransaction(Task.class);
                emc.check(task, CheckPersistType.all);
                emc.commit();
            }
            //Review
            if (ListTools.isNotEmpty(subTaskReviewIds)) {
                subTaskReviews = emc.list(Review.class, subTaskReviewIds);
                if (ListTools.isNotEmpty(subTaskReviews)) {
                    emc.beginTransaction(Task.class);
                    for (Review review : subTaskReviews) {
                        review.setArchive(true);
                        emc.check(review, CheckPersistType.all);
                    }
                    emc.commit();
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 单独修改与任务相关的单个属性值
     *
     * @param taskId
     * @param property
     * @param mainValue
     * @param secondaryValue
     * @throws Exception
     */
    public void changeTaskProperty(String taskId, String property, String mainValue, String secondaryValue) throws Exception {
        switch (property){
            case Task.workStatus_FIELDNAME:
            case Task.priority_FIELDNAME:
            case Task.executor_FIELDNAME:
            case Task.startTime_FIELDNAME:
            case Task.endTime_FIELDNAME:
            case Task.name_FIELDNAME:
            case Task.detail_FIELDNAME:
            case Task.description_FIELDNAME:
            case Task.important_FIELDNAME:
            case Task.urgency_FIELDNAME:
            case Task.progress_FIELDNAME:
            case Task.source_FIELDNAME:
                changeTaskEntityProperty(taskId, property, mainValue, secondaryValue);
                break;
            default:
                changeTaskExtFieldEntityProperty(taskId, property, mainValue, secondaryValue);
        }
    }

    private void changeTaskEntityProperty(String taskId, String property, String mainValue, String secondaryValue) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Task task = emc.find(taskId, Task.class);

            if (Task.name_FIELDNAME.equalsIgnoreCase(property)) {
                task.setName(mainValue);
            } else if (Task.workStatus_FIELDNAME.equalsIgnoreCase(property)) {
                if(ProjectStatusEnum.isEndStatus(task.getWorkStatus()) && !task.getWorkStatus().equals(mainValue)){
                    task.setProgress(0);
                }
                task.setWorkStatus(mainValue);
                if(ProjectStatusEnum.COMPLETED.getValue().equals(mainValue)){
                    task.setProgress(100);
                    if(task.getEndTime()!=null && task.getEndTime().before(new Date())){
                        task.setOvertime(true);
                    }
                }
                if(ProjectStatusEnum.CANCELED.getValue().equals(mainValue)){
                    task.setOvertime(false);
                }
            } else if (Task.source_FIELDNAME.equalsIgnoreCase(property)) {
                task.setSource(mainValue);
            } else if (Task.priority_FIELDNAME.equalsIgnoreCase(property)) {
                task.setPriority(mainValue);
            } else if (Task.important_FIELDNAME.equalsIgnoreCase(property)) {
                task.setImportant(mainValue);
            } else if (Task.urgency_FIELDNAME.equalsIgnoreCase(property)) {
                task.setUrgency(mainValue);
            } else if (Task.description_FIELDNAME.equalsIgnoreCase(property)) {
                task.setDescription(mainValue);
            } else if (Task.detail_FIELDNAME.equalsIgnoreCase(property)) {
                task.setDetail(mainValue);
            } else if (Task.progress_FIELDNAME.equalsIgnoreCase(property)) {
                if(StringUtils.isNotBlank(mainValue)){
                    Integer process = Integer.valueOf(mainValue);
                    if(process < 0){
                        process = 0;
                    }else if(process > 100){
                        process = 100;
                    }
                    if(process == 100){
                        task.setWorkStatus(ProjectStatusEnum.COMPLETED.getValue());
                    }
                    task.setProgress(process);
                }
            } else if (Task.executor_FIELDNAME.equalsIgnoreCase(property)) {
                if (StringUtils.isNotEmpty(mainValue)) {
                    if (OrganizationDefinition.isIdentityDistinguishedName(mainValue)) {
                        mainValue = userManagerService.getPersonNameWithIdentity(mainValue);
                    }
                    if(!mainValue.equals(task.getExecutor()) && OrganizationDefinition.isPersonDistinguishedName(mainValue)) {
                        task.setExecutor(mainValue);
                        Business business = new Business(emc);
                        this.savePermission(business, task, mainValue, null);
                    }
                }
            } else if (Task.startTime_FIELDNAME.equalsIgnoreCase(property)) {
                if(DateTools.isDateTimeOrDate(mainValue)){
                    task.setStartTime(DateTools.parse(mainValue));
                }
                if (DateTools.isDateTimeOrDate(secondaryValue)) {
                    task.setEndTime(DateTools.parse(mainValue));
                }
            } else if (Task.endTime_FIELDNAME.equalsIgnoreCase(property)) {
                if(DateTools.isDateTimeOrDate(mainValue)){
                    task.setEndTime(DateTools.parse(mainValue));
                    if(task.getEndTime().after(new Date())){
                        task.setOvertime(false);
                    }
                }
            }
            emc.beginTransaction(Task.class);
            emc.check(task, CheckPersistType.all);
            emc.commit();
        } catch (Exception e) {
            throw e;
        }
    }



    /**
     * 变更工作任务详情的属性
     *
     * @param taskId
     * @param property
     * @param mainValue
     * @param secondaryValue
     * @throws Exception
     */
    private void changeTaskDetailEntityProperty(String taskId, String property, String mainValue, String secondaryValue) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            TaskDetail detail = emc.find(taskId, TaskDetail.class);
            if (detail == null) {
                throw new Exception("task detail info not exists.ID=" + taskId);
            }
            if (TaskDetail.detail_FIELDNAME.equalsIgnoreCase(property)) {
                detail.setDetail(mainValue);
            } else if (TaskDetail.description_FIELDNAME.equalsIgnoreCase(property)) {
                detail.setDescription(mainValue);
            }
            emc.beginTransaction(TaskDetail.class);
            emc.check(detail, CheckPersistType.all);
            emc.commit();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 变更工作任务扩展属性信息
     *
     * @param taskId
     * @param property
     * @param mainValue
     * @param secondaryValue
     * @throws Exception
     */
    private void changeTaskExtFieldEntityProperty(String taskId, String property, String mainValue, String secondaryValue) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            TaskExtField taskExtField = emc.find(taskId, TaskExtField.class);
            if (taskExtField == null) {
                throw new Exception("task extField info not exists.ID=" + taskId);
            }
            if (TaskExtField.memoString_1_FIELDNAME.equalsIgnoreCase(property)) {
                taskExtField.setMemoString_1(mainValue);
            } else if (TaskExtField.memoString_2_FIELDNAME.equalsIgnoreCase(property)) {
                taskExtField.setMemoString_2(mainValue);
            } else if (TaskExtField.memoString_3_FIELDNAME.equalsIgnoreCase(property)) {
                taskExtField.setMemoString_3(mainValue);
            } else if (TaskExtField.memoString_4_FIELDNAME.equalsIgnoreCase(property)) {
                taskExtField.setMemoString_4(mainValue);
            } else if (TaskExtField.memoString_5_FIELDNAME.equalsIgnoreCase(property)) {
                taskExtField.setMemoString_5(mainValue);
            } else if (TaskExtField.memoString_6_FIELDNAME.equalsIgnoreCase(property)) {
                taskExtField.setMemoString_6(mainValue);
            } else if (TaskExtField.memoString_7_FIELDNAME.equalsIgnoreCase(property)) {
                taskExtField.setMemoString_7(mainValue);
            } else if (TaskExtField.memoString_8_FIELDNAME.equalsIgnoreCase(property)) {
                taskExtField.setMemoString_8(mainValue);
            } else if (TaskExtField.memoString_1_lob_FIELDNAME.equalsIgnoreCase(property)) {
                taskExtField.setMemoString_1_lob(mainValue);
            } else if (TaskExtField.memoString_2_lob_FIELDNAME.equalsIgnoreCase(property)) {
                taskExtField.setMemoString_2_lob(mainValue);
            } else if (TaskExtField.memoString_3_lob_FIELDNAME.equalsIgnoreCase(property)) {
                taskExtField.setMemoString_3_lob(mainValue);
            } else if (TaskExtField.memoString_4_lob_FIELDNAME.equalsIgnoreCase(property)) {
                taskExtField.setMemoString_4_lob(mainValue);
            }
            emc.beginTransaction(TaskExtField.class);
            emc.check(taskExtField, CheckPersistType.all);
            emc.commit();
        } catch (Exception e) {
            throw e;
        }
    }

    public boolean savePermission(Business business, Task task, String executor, List<String> participantList) throws Exception {
        if(executor == null && participantList == null){
            return false;
        }
        String parentId = task.getParent().equals(Task.TOP_TASK) ? task.getProject() : task.getParent();
        EntityManagerContainer emc = business.entityManagerContainer();
        emc.beginTransaction(ProjectPermission.class);
        if(executor != null){
            List<String> parentList = business.permissionFactory().listPermissionName(parentId, ProjectRoleEnum.MANAGER.getValue());
            Set<String> parentSet = new HashSet<>(parentList);
            parentSet.add(executor);
            List<ProjectPermission> list = business.permissionFactory().listPermission(task.getId(), ProjectRoleEnum.MANAGER.getValue());
            for (ProjectPermission permission : list){
                if(parentSet.contains(permission.getName())){
                    parentSet.remove(permission.getName());
                }else {
                    emc.remove(permission);
                }
            }
            for (String name : parentSet){
                ProjectPermission permission = new ProjectPermission(name, ProjectRoleEnum.MANAGER.getValue(), task.getId(), task.getProject());
                emc.persist(permission);
            }
        }
        if(participantList != null){
            List<String> parentList = business.permissionFactory().listPermissionName(parentId, ProjectRoleEnum.READER.getValue());
            Set<String> parentSet = new HashSet<>(parentList);
            parentSet.addAll(participantList);
            List<ProjectPermission> list = business.permissionFactory().listPermission(task.getId(), ProjectRoleEnum.READER.getValue());
            for (ProjectPermission permission : list){
                if(parentSet.contains(permission.getName())){
                    parentSet.remove(permission.getName());
                }else {
                    emc.remove(permission);
                }
            }
            for (String name : parentSet){
                ProjectPermission permission = new ProjectPermission(name, ProjectRoleEnum.READER.getValue(), task.getId(), task.getProject());
                emc.persist(permission);
            }
        }
        return true;
    }


}
