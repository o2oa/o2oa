package com.x.general.assemble.control.jaxrs.excel;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.annotation.FieldTypeDescribe;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.general.assemble.control.ThisApplication;
import com.x.general.assemble.control.tools.ExcelUtils;
import com.x.general.assemble.control.tools.SheetData;
import com.x.general.core.entity.GeneralFile;
import java.io.ByteArrayOutputStream;
import java.util.List;
import org.apache.commons.lang3.StringUtils;


class ActionExcelExportWithTemplate extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionExcelExportWithTemplate.class);
	private static final String EXCEL_EXTENSION = ".xlsx";

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("jsonElement:{}.", () -> jsonElement);

		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if(StringUtils.isBlank(wi.getTemplateUrl())){
			throw new ExceptionFieldEmpty("templateUrl");
		}

		String excelName = wi.getExcelName();
		if (StringUtils.isEmpty(excelName)) {
			excelName = "无标题" + EXCEL_EXTENSION;
		}
		if (!excelName.toLowerCase().endsWith(EXCEL_EXTENSION)) {
			excelName = excelName + EXCEL_EXTENSION;
		}
		if (ListTools.isEmpty(wi.getSheetDataList())) {
			throw new ExceptionCustom("excel内容不能为空");
		}
		byte[] tempBytes = CipherConnectionAction.getBinary(false, wi.getTemplateUrl());
		if (tempBytes == null || tempBytes.length == 0) {
			throw new ExceptionCustom("模板文件下载失败.");
		}
		byte[] bytes;
		try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			ExcelUtils.writeDataToTemplateFile(tempBytes, wi.getSheetDataList(), os);
			bytes = os.toByteArray();
		}

		Wo wo = new Wo();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			StorageMapping gfMapping = ThisApplication.context().storageMappings().random(GeneralFile.class);
			GeneralFile generalFile = new GeneralFile(gfMapping.getName(), excelName,
					effectivePerson.getDistinguishedName());
			generalFile.saveContent(gfMapping, bytes, excelName);
			emc.beginTransaction(GeneralFile.class);
			emc.persist(generalFile, CheckPersistType.all);
			emc.commit();
			wo.setId(generalFile.getId());
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = 1123515948467557694L;

	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -51802802412712709L;

		@FieldDescribe("模板文件的url")
		private String templateUrl;

		@FieldDescribe("excel文件名")
		private String excelName;

		@FieldDescribe("转换为excel的内容，列表第1个值对应excel第一个sheet内容，列表第2个值对应第2个sheet内容，依此类推")
		@FieldTypeDescribe(fieldType = "class", fieldTypeName = "SheetData",
				fieldValue = "{'sheetName':'excel的sheet名称，可以为空','startRow':'数字类型，表示数据开始行，从第0行开始','dataList':'二维数据列表'}")
		private List<SheetData> sheetDataList;

		public String getTemplateUrl() {
			return templateUrl;
		}
		public void setTemplateUrl(String templateUrl) {
			this.templateUrl = templateUrl;
		}

		public String getExcelName() {
			return excelName;
		}

		public void setExcelName(String excelName) {
			this.excelName = excelName;
		}

		public List<SheetData> getSheetDataList() {
			return sheetDataList;
		}

		public void setSheetDataList(
				List<SheetData> sheetDataList) {
			this.sheetDataList = sheetDataList;
		}
	}

}
