package com.x.cms.assemble.control.jaxrs.categoryinfo;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.CategoryInfo;

public class ActionUpdateReviewForce extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionUpdateReviewForce.class );
	
	protected ActionResult<WoId> execute( HttpServletRequest request, EffectivePerson effectivePerson, String flag ) throws Exception {
		ActionResult<WoId> result = new ActionResult<>();
		CategoryInfo categoryInfo = null;
		Boolean check = true;
		
		if( StringUtils.isEmpty( flag ) ){
			check = false;
			Exception exception = new ExceptionIdEmpty();
			result.error( exception );
		}
		
		if( check ){
			try {
				categoryInfo = categoryInfoServiceAdv.getWithFlag( flag );
				if( categoryInfo == null ){
					check = false;
					Exception exception = new ExceptionCategoryInfoNotExists( flag );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCategoryInfoProcess( e, "根据ID查询分类信息对象时发生异常。Flag:" + flag );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				permissionOperateService.refreshReviewWithCategoryId( categoryInfo.getId() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCategoryInfoProcess( e, "根据指定分类ID强制更新信息存根Review信息时发生异常。ID:" + categoryInfo.getId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				WoId wo = new WoId();
				wo.setId( categoryInfo.getId() );
				result.setData( wo );
			} catch ( Exception e ) {
				check = false;
				Exception exception = new ExceptionCategoryInfoProcess( e, "将查询出来的分类信息对象转换为可输出的数据信息时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}		
		return result;
	}
}