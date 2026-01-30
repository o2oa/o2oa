package com.x.teamwork.assemble.control.jaxrs.project;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.DateTools;
import com.x.general.core.entity.GeneralFile;
import com.x.teamwork.assemble.control.ThisApplication;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.ProjectStatusEnum;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author sword
 */
public class ActionExportWithFilter extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionExportWithFilter.class);

	private static final String[] PROJECT_ARRAY = new String[]{"项目名称","负责人","开始时间","结束时间","项目状态","任务总数",
			"已完成任务数","进行中任务数","已搁置任务数","已取消任务数"};

	protected ActionResult<Wo> execute( EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		logger.info("{}操作excel导出", effectivePerson.getDistinguishedName());
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		Wi wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		String person = this.isSysManager(effectivePerson) ? "" : effectivePerson.getDistinguishedName();
		QueryFilter queryFilter = wrapIn.getQueryFilter(effectivePerson);
		List<Project> projectList = projectQueryService.listWithCondition(person,
				wrapIn.getOrderField(), wrapIn.getOrderType(), queryFilter);
		List<WrapProject> list = new ArrayList<>();
		for( Project project : projectList ) {
			WrapProject wrapProject = WrapProject.copier.copy(project);
			wrapProject.setTaskStatistics(this.statTask(person, project.getId()));
			list.add( wrapProject );
		}
		String excelName = wrapIn.getExcelName();

		String id = this.taskWriteToExcel(effectivePerson, list, excelName);
		wo.setId(id);
		result.setData(wo);
		return result;
	}

	protected String taskWriteToExcel(EffectivePerson effectivePerson, List<WrapProject> list,
									  String excelName) throws Exception {
		try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			XSSFSheet sheet = workbook.createSheet("项目列表");
			XSSFRow row = sheet.createRow(0);
			for(int i=0; i<PROJECT_ARRAY.length; i++){
				XSSFCell cell = row.createCell(i);
				cell.setCellValue(PROJECT_ARRAY[i]);
			}
			for(int i=0; i<list.size(); i++){
				WrapProject project = list.get(i);
				row = sheet.createRow(i+1);
				int j = 0;

				XSSFCell cell = row.createCell(j++);
				cell.setCellValue(project.getTitle());

				cell = row.createCell(j++);
				final List<String> nameList = new ArrayList<>();
				project.getManageablePersonList().stream().forEach(p -> {
					nameList.add(OrganizationDefinition.name(p));
				});
				cell.setCellValue(StringUtils.join(nameList, ","));

				cell = row.createCell(j++);
				cell.setCellValue(project.getStartTime()!=null ? DateTools.format(project.getStartTime()) : "");

				cell = row.createCell(j++);
				cell.setCellValue(project.getEndTime()!=null ? DateTools.format(project.getEndTime()) : "");

				cell = row.createCell(j++);
				cell.setCellValue(ProjectStatusEnum.getNameByValue(project.getWorkStatus()));

				cell = row.createCell(j++);
				cell.setCellValue(project.getTaskStatistics().getAllCount());

				cell = row.createCell(j++);
				cell.setCellValue(project.getTaskStatistics().getCompletedCount());

				cell = row.createCell(j++);
				cell.setCellValue(project.getTaskStatistics().getProcessingCount());

				cell = row.createCell(j++);
				cell.setCellValue(project.getTaskStatistics().getDelayCount());

				cell = row.createCell(j++);
				cell.setCellValue(project.getTaskStatistics().getDeleteCount());
			}
			String excel = ".xlsx";
			if(StringUtils.isBlank(excelName)){
				excelName = DateTools.compact(new Date()) + excel;
			}else if (!excelName.toLowerCase().endsWith(excel)) {
				excelName = excelName + excel;
				excelName = StringUtils.replaceEach(excelName,
						new String[] { "/", ":", "*", "?", "<<", ">>", "|", "<", ">", "\\" },
						new String[] { "", "", "", "", "", "", "", "", "", "" });
			}
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

	public static class Wi extends WrapInQueryProject{

		@FieldDescribe("导出的excel名称.")
		private String excelName;

		public String getExcelName() {
			return excelName;
		}

		public void setExcelName(String excelName) {
			this.excelName = excelName;
		}
	}

	public static class Wo extends WoId {

	}

	public static class WrapProject extends Project {

		private static final long serialVersionUID = -6848667759835373940L;

		@FieldDescribe("项目任务的统计信息:allCount|completedCount|processingCount|delayCount|deleteCount")
		private TaskStatistics taskStatistics;

		static WrapCopier<Project, WrapProject> copier = WrapCopierFactory.wo( Project.class, WrapProject.class, null, JpaObject.FieldsInvisible);

		public TaskStatistics getTaskStatistics() {
			return taskStatistics;
		}

		public void setTaskStatistics(TaskStatistics taskStatistics) {
			this.taskStatistics = taskStatistics;
		}
	}

}
