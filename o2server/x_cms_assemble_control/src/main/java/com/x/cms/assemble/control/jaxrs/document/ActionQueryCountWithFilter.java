package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.tools.filter.QueryFilter;
import com.x.cms.core.entity.tools.filter.term.InTerm;
import com.x.cms.core.entity.tools.filter.term.NotInTerm;

public class ActionQueryCountWithFilter extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionQueryCountWithFilter.class);

	protected ActionResult<Wo> execute( HttpServletRequest request, JsonElement jsonElement, EffectivePerson effectivePerson ) {
		ActionResult<Wo> result = new ActionResult<>();
		
		Long total = 0L;
		Wi wi = null;
		Wo wo = new Wo();		
		Boolean check = true;		
		String personName = effectivePerson.getDistinguishedName();
		QueryFilter queryFilter = null;
		
		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionDocumentInfoProcess( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if ( wi == null ) { wi = new Wi(); }
		
		if( StringUtils.isEmpty( wi.getDocumentType() )) {
			wi.setDocumentType( "信息" );
		}
		
//		if( check ) {
//			try {
//				unitNames = userManagerService.listUnitNamesWithPerson( personName );
//				groupNames = userManagerService.listGroupNamesByPerson( personName );
//				permissionObjs = getPermissionObjs(personName, unitNames, groupNames );
//			} catch (Exception e) {
//				check = false;
//				Exception exception = new ExceptionDocumentInfoProcess(e, "系统在根据过滤条件查询用户可访问的文档ID列表时发生异常。");
//				result.error(exception);
//				logger.error(e, effectivePerson, request, null);
//			}
//		}
		
		if (check) {
			try {
				queryFilter = wi.getQueryFilter();
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "系统在获取查询条件信息时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if( check ) {
			//查询是否已读，需要使用相应的ID进行IN操作，效率有一些低
			List<String> readDocIds = null;
			if( "READ".equals( wi.getReadFlag() )) { //只查询阅读过的
				try {//查询出该用户所有已经阅读过的文档ID列表
					readDocIds = documentViewRecordServiceAdv.listByPerson( personName, 5000 );
					queryFilter.addInTerm( new InTerm( "docId", new ArrayList<>( readDocIds ) ) );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionDocumentInfoProcess(e, "系统在查询用户已经阅读过的文档ID列表时发生异常。");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}else if("UNREAD".equals( wi.getReadFlag() )) { //只查询未阅读过的
				try {//查询出该用户所有已经阅读过的文档ID列表
					readDocIds = documentViewRecordServiceAdv.listByPerson( personName, 5000 );
					queryFilter.addNotInTerm( new NotInTerm( "docId", new ArrayList<>( readDocIds ) ) );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionDocumentInfoProcess(e, "系统在查询用户已经阅读过的文档ID列表时发生异常。");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		
		if (check) {
			try {// 从Review表中查询符合条件的对象总数
				total = documentQueryService.countWithConditionInReview( personName, queryFilter );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "系统在获取用户可查询到的文档数据条目数量时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if( total == null || total < 0 ) {
			total = 0L;
		}
		wo.setDocCount(total);
		result.setCount(total);	
		result.setData(wo);
		return result;
	}
	
	public static class Wi extends WrapInDocumentFilter{
		
	}

	public static class Wo {
		
		@FieldDescribe( "查询到的文档数量" )
		Long docCount = 0L;

		public Long getDocCount() {
			return docCount;
		}

		public void setDocCount(Long docCount) {
			this.docCount = docCount;
		}
		
	}
}