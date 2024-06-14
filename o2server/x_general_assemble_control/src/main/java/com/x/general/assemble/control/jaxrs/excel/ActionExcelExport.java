package com.x.general.assemble.control.jaxrs.excel;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.general.assemble.control.Business;
import com.x.general.assemble.control.ThisApplication;
import com.x.general.core.entity.GeneralFile;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
import java.util.List;



class ActionExcelExport extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionExcelExport.class);
	private static final String EXCEL_EXTENSION = ".xlsx";

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String excelName, JsonElement jsonElement)
			throws Exception {

		LOGGER.debug("jsonElement:{}.", () -> jsonElement);

		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		if (StringUtils.isEmpty(excelName)) {
			excelName = "无标题" + EXCEL_EXTENSION;
		}
		if (!excelName.toLowerCase().endsWith(EXCEL_EXTENSION)) {
			excelName = excelName + EXCEL_EXTENSION;
		}
		if (ListTools.isEmpty(wi.getDataList())) {
			throw new Exception("dataList内容为空");
		}
		byte[] bytes;
		try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			XSSFSheet sheet = workbook.createSheet("Sheet1");

			int rowNum = 0;
			for (List<String> rowList : wi.getDataList()) {
				Row row = sheet.createRow(rowNum++);
				int cellNum = 0;
				for (String value : rowList) {
					Cell cell = row.createCell(cellNum++);
					cell.setCellValue(value);
				}
			}
			workbook.write(os);
			bytes = os.toByteArray();
		}

		Business business;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
		}
		StorageMapping gfMapping = ThisApplication.context().storageMappings().random(GeneralFile.class);
		GeneralFile generalFile = new GeneralFile(gfMapping.getName(), excelName,
				effectivePerson.getDistinguishedName());
		generalFile.saveContent(gfMapping, bytes, excelName);
		business.entityManagerContainer().beginTransaction(GeneralFile.class);
		business.entityManagerContainer().persist(generalFile, CheckPersistType.all);
		business.entityManagerContainer().commit();

		Wo wo = new Wo();
		wo.setId(generalFile.getId());
		result.setData(wo);
		return result;
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = 1123515948467557694L;

	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -51802802412712709L;

		@FieldDescribe("转换为excel的内容，例如：['姓名','性别'],['小明','男']")
		public List<List<String>> dataList;


		public List<List<String>> getDataList() {
			return dataList;
		}

		public void setDataList(List<List<String>> dataList) {
			this.dataList = dataList;
		}

	}

}
