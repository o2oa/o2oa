package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.ActionSubjectSave.Wi;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSectionNotExists;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSectionSubjectTypeEmpty;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSectionSubjectTypeInvalid;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSectionTypeCategoryEmpty;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSectionTypeCategoryInvalid;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectPropertyEmpty;
import com.x.bbs.entity.BBSSectionInfo;

public class SubjectPropertyValidator{
	
	public static Boolean baseValidate( HttpServletRequest request, Wi wrapIn ) throws Exception {
		wrapIn.setHostIp(request.getRemoteHost());
		if ( wrapIn.getTitle() == null ) {
			Exception exception = new ExceptionSubjectPropertyEmpty("主题标题");
			throw exception;
		}
		if (wrapIn.getType() == null) {
			Exception exception = new ExceptionSubjectPropertyEmpty("主题类别");
			throw exception;
		}
		if ( wrapIn.getContent() == null ) {
			Exception exception = new ExceptionSubjectPropertyEmpty("主题内容");
			throw exception;
		}
		if (wrapIn.getSectionId() == null || wrapIn.getSectionId().isEmpty()) {
			Exception exception = new ExceptionSubjectPropertyEmpty("所属版块ID");
			throw exception;
		}
		return true;
	}
	
	public static Boolean typeCategoryValidate( BBSSectionInfo sectionInfo, Wi wrapIn ) throws Exception {
		if ( wrapIn == null) {
			Exception exception = new Exception( "wrapIn is null!" );
			throw exception;
		}
		if (sectionInfo == null) {
			Exception exception = new ExceptionSectionNotExists( wrapIn.getSectionId() );
			throw exception;
		}
		if ( sectionInfo.getTypeCategory() == null || sectionInfo.getTypeCategory().isEmpty() ) {
			Exception exception = new ExceptionSectionTypeCategoryEmpty(wrapIn.getSectionId());
			throw exception;
		} else { // 判断TypeCategory是否合法
			String[] categories = sectionInfo.getTypeCategory().split("\\|");
			Boolean categoryValid = false;
			if (categories != null && categories.length > 0) {
				for (String category : categories) {
					if (category.equals(wrapIn.getTypeCategory())) {
						categoryValid = true;
					}
				}
				if ( !categoryValid ) {
					Exception exception = new ExceptionSectionTypeCategoryInvalid(categories);
					throw exception;
				}
			}
		}
		return true;
	}
	
	public static Boolean subjectTypeValidate( BBSSectionInfo sectionInfo, Wi wrapIn ) throws Exception {
		if ( wrapIn == null) {
			Exception exception = new Exception( "wrapIn is null!" );
			throw exception;
		}
		if (sectionInfo == null) {
			Exception exception = new ExceptionSectionNotExists(wrapIn.getSectionId());
			throw exception;
		}
		if ( sectionInfo.getSubjectType() == null || sectionInfo.getSubjectType().isEmpty()) {
			Exception exception = new ExceptionSectionSubjectTypeEmpty(wrapIn.getSectionId());
			throw exception;
		} else {
			// 判断Type是否合法
			String[] types = null;
			List<String> subjectTypeList = sectionInfo.getSubjectTypeList();
			if(ListTools.isNotEmpty(subjectTypeList)){
				types = subjectTypeList.toArray(new String[subjectTypeList.size()]);
			}else{
				types = sectionInfo.getSubjectType().split("\\|");
			}

			Boolean typeValid = false;
			if (types != null && types.length > 0) {
				for (String type : types) {
					if (type.equals(wrapIn.getType())) {
						typeValid = true;
					}
				}
				if (!typeValid) {
					Exception exception = new ExceptionSectionSubjectTypeInvalid(types);
					throw exception;
				}
			}
		}
		return true;
	}
}
