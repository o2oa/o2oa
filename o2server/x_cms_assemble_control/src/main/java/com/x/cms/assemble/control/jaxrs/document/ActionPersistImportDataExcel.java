package com.x.cms.assemble.control.jaxrs.document;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.google.gson.Gson;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.assemble.control.queue.DataImportStatus;
import com.x.cms.common.excel.reader.DocumentExcelReader;
import com.x.cms.common.excel.reader.ExcelReadRuntime;
import com.x.cms.common.excel.reader.ExcelReadRuntime.DocTemplate;
import com.x.cms.common.excel.reader.ExcelReaderUtil;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.tools.DateOperation;
import com.x.query.core.entity.View;

public class ActionPersistImportDataExcel extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger( ActionPersistImportDataExcel.class );

	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson,  String categoryId,
			byte[] bytes, String json_data, FormDataContentDisposition disposition) {
		
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = null;
		Wo wo = new Wo();
		DocTemplate template = null;
		CategoryInfo categoryInfo = null;
		View view = null;
		List<String> propertyNames = new ArrayList<>();
		String viewId = null;
		String fileName = null;
		Boolean check = true;
		Gson gson = null;
		
		String importBatchName = categoryId + "_" + DateOperation.getNowTimeChar();
		String personName = effectivePerson.getDistinguishedName();
		
		
		if( StringUtils.isEmpty(categoryId) ){
			check = false;
			Exception exception = new ExceptionCategoryIdEmpty();
			result.error( exception );
		}
		
		if( StringUtils.isNotEmpty( json_data ) ){
			gson = XGsonBuilder.instance();
			wi = gson.fromJson(json_data, Wi.class );
		}else {
			check = false;
			Exception exception = new ExceptionDocumentInfoProcess( "参数不正确：json_data：" + json_data );
			result.error( exception );
		}
		
		if( check ){
			try {
				categoryInfo = categoryInfoServiceAdv.get( categoryId );
				if( categoryInfo == null ){
					check = false;
					Exception exception = new ExceptionCategoryInfoNotExists( categoryId );
					result.error( exception );
				}else {
					viewId = categoryInfo.getImportViewId();
					if( StringUtils.isEmpty( viewId ) ){
						check = false;
						Exception exception = new ExceptionImportViewIdEmpty();
						result.error( exception );
					}
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess( e, "根据ID查询分类信息对象时发生异常。ID:" + categoryId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				fileName = FilenameUtils.getName(new String(disposition.getFileName().getBytes(DefaultCharset.name_iso_8859_1), DefaultCharset.name));
				/** 禁止不带扩展名的文件上传 */
				if (StringUtils.isEmpty(fileName)) {
					check = false;
					Exception exception = new ExceptionEmptyExtension( fileName );
					result.error( exception );
				} 
			} catch (Exception e) {
				check = false;
				result.error( e );
			}
		}
		
		if( check ){
			try {
				view = queryViewService.getQueryView( viewId );
				if( view == null ){
					check = false;
					Exception exception = new ExceptionViewNotExists( viewId );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess( e, "根据ID查询分类信息绑定的导入数据视图信息对象时发生异常。VIEWID:" + viewId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				propertyNames = queryViewService.listColumnsFormQueryView(view);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess( e, "根据VIEWID查询数据视图中所有的列信息时发生异常。VIEWID:" + viewId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){			
			template = new ExcelReadRuntime.DocTemplate();
			template.setAppId( categoryInfo.getAppId() );
			template.setAppName( categoryInfo.getAppName() );
			template.setCategoryId(categoryId);
			template.setCategoryName( categoryInfo.getCategoryName() );
			template.setCategoryAlias( categoryInfo.getCategoryAlias() );
			template.setForm( categoryInfo.getFormId() );
			template.setFormName( categoryInfo.getFormName() );
			template.setReadFormId( categoryInfo.getReadFormId() );
			template.setReadFormName( categoryInfo.getReadFormName() );
			template.setPublishTime( new Date());
			
			if( StringUtils.isNotEmpty( wi.getTitle() ) && !"null".equalsIgnoreCase( wi.getTitle() )) {
				template.setTitle(  wi.getTitle() );
			}else {
				//暂不设置标题
				template.setTitle( "" );
			}
			
			if( StringUtils.isNotEmpty( wi.getIdentity() )) {
				//以传入身份为主
				try {
					template.setCreatorIdentity( wi.getIdentity() );
					template.setCreatorPerson(  personName );
					template.setCreatorUnitName( userManagerService.getUnitNameByIdentity(wi.getIdentity()));
					template.setCreatorTopUnitName( userManagerService.getTopUnitNameByIdentity(wi.getIdentity()) );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionDocumentInfoProcess( e, "根据传入的身份获取组织信息时发生异常。identity:" + wi.getIdentity() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}else {
				//以登录者为主
				if( "xadmin".equalsIgnoreCase( effectivePerson.getName() )) {
					template.setCreatorIdentity( "xadmin" );
					template.setCreatorPerson(  "xadmin" );
					template.setCreatorUnitName( "xadmin" );
					template.setCreatorTopUnitName( "xadmin" );
				}else {
					try {
						template.setCreatorIdentity( userManagerService.getIdentityWithPerson(personName) );
						template.setCreatorPerson(  personName );
						template.setCreatorUnitName( userManagerService.getUnitNameWithPerson(personName));
						template.setCreatorTopUnitName( userManagerService.getTopUnitNameWithPerson(personName) );
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionDocumentInfoProcess( e, "根据登录者获取身份及组织信息时发生异常。personName:" + personName );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}			
				}
			}
			
			if( StringUtils.isNotEmpty( wi.getDocType() )) {
				template.setDocumentType( wi.getDocType() );
			}else {
				template.setDocumentType("数据");
			}
		}
		
		if( check ){
			//根据propertyNames进行数据导入
			InputStream inputStream = new ByteArrayInputStream(bytes); 
			DataImportStatus dataImportStatus = ThisApplication.getDataImportStatus( importBatchName );
			ExcelReadRuntime excelReadRuntime = new ExcelReadRuntime( 
					effectivePerson.getDistinguishedName(), template, propertyNames, 1, wi, wo, importBatchName, dataImportStatus );

			try {
				ExcelReaderUtil.readExcel( new DocumentExcelReader(), fileName, inputStream, excelReadRuntime );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionExcelRead(e);
				logger.error( e, effectivePerson, request, null);
				result.error(exception);
			}
		}
		wo.setImportBatchName(importBatchName);
		result.setData( wo );
		return result;
	}
	
	public static class Wi{
		
		@FieldDescribe( "新建数据的文档标题（前缀），选填" )
		private String title = null;
		
		@FieldDescribe( "新建数据的文档标题（前缀）后面的数据（列名），选填" )
		private String title_column = null;
		
		@FieldDescribe( "创建者身份，选填." )
		private String identity = null;
		
		@FieldDescribe( "文档数据类型：信息|数据，默认：数据" )
		private String docType = null;
		
		@FieldDescribe( "传入的数据，导入的所有文档都会有." )
		private List<WiParam> wiParameters = null;

		public String getTitle_column() {
			return title_column;
		}

		public void setTitle_column(String title_column) {
			this.title_column = title_column;
		}

		public String getTitle() {
			return title;
		}

		public String getDocType() {
			return docType;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public void setDocType(String docType) {
			this.docType = docType;
		}

		public String getIdentity() {
			return identity;
		}

		public List<WiParam> getWiParameters() {
			return wiParameters;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}

		public void setWiParameters(List<WiParam> wiParameters) {
			this.wiParameters = wiParameters;
		}		
	}
	
	public static class WiParam{
		
		@FieldDescribe( "数据路径或者属性名." )
		private String dataPath = null;
		
		@FieldDescribe( "数据类型: String | Integer | Boolean | Date." )
		private String dataType = "String";
		
		@FieldDescribe( "数据值:" )
		private String dataString = null;
		
		@FieldDescribe( "数据值:" )
		private Integer dataInteger = null;
		
		@FieldDescribe( "数据值:" )
		private Integer dataBoolean = null;
		
		@FieldDescribe( "数据值:" )
		private Integer dataDate = null;

		public String getDataPath() {
			return dataPath;
		}

		public String getDataType() {
			return dataType;
		}

		public String getDataString() {
			return dataString;
		}

		public Integer getDataInteger() {
			return dataInteger;
		}

		public Integer getDataBoolean() {
			return dataBoolean;
		}

		public Integer getDataDate() {
			return dataDate;
		}

		public void setDataPath(String dataPath) {
			this.dataPath = dataPath;
		}

		public void setDataType(String dataType) {
			this.dataType = dataType;
		}

		public void setDataString(String dataString) {
			this.dataString = dataString;
		}

		public void setDataInteger(Integer dataInteger) {
			this.dataInteger = dataInteger;
		}

		public void setDataBoolean(Integer dataBoolean) {
			this.dataBoolean = dataBoolean;
		}

		public void setDataDate(Integer dataDate) {
			this.dataDate = dataDate;
		}
		
		public Object getValue() {
			//数据类型: String | Integer | Boolean | Date.
			if( "String".equalsIgnoreCase( this.dataType ) ) {
				return this.dataString;
			}else if( "Integer".equalsIgnoreCase( this.dataType ) ) {
				return this.dataInteger;
			}else if( "Boolean".equalsIgnoreCase( this.dataType ) ) {
				return this.dataBoolean;
			}else if( "Date".equalsIgnoreCase( this.dataType ) ) {
				return this.dataDate;
			}else {
				return this.dataString;
			}
		}
	}

	public static class Wo {
		private List<List<String>> errors = null;
		private Integer total = 0;
		private Integer error_count = 0;
		private Integer success_count = 0;
		private String importBatchName = null;
		private List<String> documentIds = new ArrayList<>();
		
		public List<List<String>> getErrors() {
			return errors;
		}
		public Integer getTotal() {
			return total;
		}
		public Integer getError_count() {
			return error_count;
		}
		public Integer getSuccess_count() {
			return success_count;
		}
		public void setErrors(List<List<String>> errors) {
			this.errors = errors;
		}
		
		public void setTotal(Integer total) {
			this.total = total;
		}
		
		public void setError_count(Integer error_count) {
			this.error_count = error_count;
		}
		public void setSuccess_count(Integer success_count) {
			this.success_count = success_count;
		}
		
		public List<String> getDocumentIds() {
			return documentIds;
		}
		
		public void setDocumentIds(List<String> documentIds) {
			this.documentIds = documentIds;
		}
		
		public synchronized  List<String> addDocumentId( String id ){
			if( this.documentIds == null ) {
				this.documentIds = new ArrayList<>();
			}
			this.documentIds.add( id );
			return this.documentIds;
		}
		
		public synchronized void increaseError_count(Integer count) {
			if( count == null ) {
				count = 1;
			}
			this.total = total + count;
			this.error_count = error_count + count;
		}
		
		public synchronized void increaseSuccess_count(Integer count) {
			if( count == null ) {
				count = 1;
			}
			this.total = total + count;
			this.success_count = success_count + count;
		}
		
		public synchronized void appendErorrData(List<String> data) {
			if( errors == null ) {
				errors = new ArrayList<>();
			}
			errors.add( data );
		}
		public String getImportBatchName() {
			return importBatchName;
		}
		public void setImportBatchName(String importBatchName) {
			this.importBatchName = importBatchName;
		}
	}
}
