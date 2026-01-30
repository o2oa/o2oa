package com.x.teamwork.assemble.control.jaxrs.task;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.general.core.entity.GeneralFile;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.assemble.control.ThisApplication;
import com.x.teamwork.assemble.control.service.*;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.ProjectStatusEnum;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskView;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class BaseAction extends StandardJaxrsAction {

    protected Cache.CacheCategory taskCache = new Cache.CacheCategory(Task.class, TaskView.class);

    protected TaskQueryService taskQueryService = new TaskQueryService();

    protected TaskTagQueryService taskTagQueryService = new TaskTagQueryService();

    protected TaskTagPersistService taskTagPersistService = new TaskTagPersistService();

    protected ProjectQueryService projectQueryService = new ProjectQueryService();

    protected CustomExtFieldReleQueryService customExtFieldReleQueryService = new CustomExtFieldReleQueryService();

    protected TaskPersistService taskPersistService = new TaskPersistService();

    protected TaskListPersistService taskListPersistService = new TaskListPersistService();

    protected TaskListQueryService taskListQueryService = new TaskListQueryService();

    protected TaskGroupQueryService taskGroupQueryService = new TaskGroupQueryService();

    protected TaskGroupPersistService taskGroupPersistService = new TaskGroupPersistService();

    protected TaskViewQueryService taskViewQueryService = new TaskViewQueryService();

    protected DynamicPersistService dynamicPersistService = new DynamicPersistService();

    protected UserManagerService userManagerService = new UserManagerService();

    protected static final Business BUSINESS = new Business(null);

    protected boolean isSysManager(EffectivePerson effectivePerson) throws Exception {
        return BUSINESS.isManager(effectivePerson);
    }

    protected boolean isReader(String id, EffectivePerson effectivePerson) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            return business.isManager(effectivePerson) || business.permissionFactory().isReader(id, effectivePerson.getDistinguishedName(), Boolean.FALSE);
        }
    }

    protected boolean isManager(String id, EffectivePerson effectivePerson) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            return business.isManager(effectivePerson) || business.permissionFactory().isManager(id, effectivePerson.getDistinguishedName());
        }
    }

    protected boolean isParentManager(Task task, EffectivePerson effectivePerson) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            String parent = Task.TOP_TASK.equals(task.getParent()) ? task.getProject() : task.getParent();
            return business.isManager(effectivePerson) || business.permissionFactory().isManager(parent, effectivePerson.getDistinguishedName());
        }
    }

    protected boolean canEdit(Task task, EffectivePerson effectivePerson) throws Exception{
        if(this.isManager(task.getId(), effectivePerson)){
            if(Task.TOP_TASK.equals(task.getParent())){
                Project project = projectQueryService.getFromCache(task.getProject());
                if(project != null && !ProjectStatusEnum.isEndStatus(project.getWorkStatus())){
                    return true;
                }
            }else{
                Task parent = taskQueryService.getFromCache(task.getParent());
                if(parent != null && !ProjectStatusEnum.isEndStatus(parent.getWorkStatus())){
                    return true;
                }
            }
        }
        return false;
    }

    private static final String[] TASK_ARRAY = new String[]{"编号","任务名称","责任人","来源",
            "重要程度","紧急程度","开始时间","结束时间","进度","状态","任务说明","工作进展"};
    private static final String[] TASK_ARRAY1 = new String[]{"任务名称","责任人","来源",
            "重要程度","紧急程度","开始时间","结束时间","进度","状态","任务说明","工作进展"};

    protected String taskWriteToExcel(EffectivePerson effectivePerson, List<Task> taskList,
                                      String excelName, String project) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.createSheet("task");
            XSSFRow row = sheet.createRow(0);
            if(StringUtils.isNotBlank(project)) {
                for (int i = 0; i < TASK_ARRAY.length; i++) {
                    XSSFCell cell = row.createCell(i);
                    cell.setCellValue(TASK_ARRAY[i]);
                }
            }else{
                for (int i = 0; i < TASK_ARRAY1.length; i++) {
                    XSSFCell cell = row.createCell(i);
                    cell.setCellValue(TASK_ARRAY1[i]);
                }
            }

            for(int i=0; i<taskList.size(); i++){
                Task task = taskList.get(i);
                row = sheet.createRow(i+1);
                int j = 0;
                XSSFCell cell;
                if(StringUtils.isNotBlank(project)) {
                    cell = row.createCell(j++);
                    cell.setCellValue(Objects.toString(task.getSerialNumberDisplay(), ""));
                }

                cell = row.createCell(j++);
                cell.setCellValue(task.getName());

                cell = row.createCell(j++);
                cell.setCellValue(OrganizationDefinition.name(task.getExecutor()));

                cell = row.createCell(j++);
                cell.setCellValue(Objects.toString(task.getSource(), ""));

                cell = row.createCell(j++);
                cell.setCellValue(Objects.toString(task.getImportant(), ""));

                cell = row.createCell(j++);
                cell.setCellValue(Objects.toString(task.getUrgency(), ""));

                cell = row.createCell(j++);
                cell.setCellValue(task.getStartTime()!=null ? DateTools.format(task.getStartTime()) : "");

                cell = row.createCell(j++);
                cell.setCellValue(task.getEndTime()!=null ? DateTools.format(task.getEndTime()) : "");

                cell = row.createCell(j++);
                cell.setCellValue(task.getProgress() + "%");

                cell = row.createCell(j++);
                cell.setCellValue(ProjectStatusEnum.getNameByValue(task.getWorkStatus()));

                cell = row.createCell(j++);
                cell.setCellValue(Objects.toString(task.getDescription(), ""));

                cell = row.createCell(j++);
                cell.setCellValue(Objects.toString(task.getDetail(), ""));
            }

            String excel = ".xlsx";
            if(StringUtils.isBlank(excelName)){
                excelName = DateTools.compact(new Date()) + excel;
            }else if (!excelName.toLowerCase().endsWith(excel)) {
                excelName = excelName + excel;
            }
            excelName = StringUtils.replaceEach(excelName,
                    new String[] { "/", ":", "*", "?", "<<", ">>", "|", "<", ">", "\\" },
                    new String[] { "", "", "", "", "", "", "", "", "", "" });
            workbook.write(os);
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                StorageMapping gfMapping = ThisApplication.context().storageMappings().random(GeneralFile.class);
                GeneralFile generalFile = new GeneralFile(gfMapping.getName(), excelName,
                        effectivePerson.getDistinguishedName());
                generalFile.saveContent(gfMapping, os.toByteArray(), excelName);
                emc.beginTransaction(GeneralFile.class);
                emc.persist(generalFile, CheckPersistType.all);
                emc.commit();
                return generalFile.getId();
            }
        }
    }

    public static class TaskListChange {

        @FieldDescribe("转移前的列表ID")
        private String source;

        @FieldDescribe("转移后的列表ID")
        private String target;

        public TaskListChange() {
        }

        public TaskListChange(String _source, String _target) {
            this.source = _source;
            this.target = _target;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }
    }

}
