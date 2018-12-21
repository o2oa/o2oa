package com.x.okr.assemble.control.jaxrs.okrconfigworktype;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.WorkCommonQueryFilter;
import com.x.okr.assemble.control.jaxrs.okrconfigworktype.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrconfigworktype.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.okrconfigworktype.exception.ExceptionWorkTypeConfigListTypeCount;
import com.x.okr.entity.OkrConfigWorkType;

public class ExcuteListTypeCount extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ExcuteListTypeCount.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request,EffectivePerson effectivePerson, WorkCommonQueryFilter wrapIn ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = null;
		List<OkrConfigWorkType> okrConfigWorkTypeList = null;
		List<String> workTypes = new ArrayList<>();
		Long total = 0L;
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;

		if( wrapIn == null ){
			wrapIn = new WorkCommonQueryFilter();
		}
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getDistinguishedName() );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionGetOkrUserCache( e, effectivePerson.getDistinguishedName() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new ExceptionUserNoLogin( effectivePerson.getDistinguishedName() );
			result.error( exception );
			//logger.error( e, effectivePerson, request, null);
		}		
		if( check ){
			try {
				okrConfigWorkTypeList = okrConfigWorkTypeService.listAll();
				if( okrConfigWorkTypeList != null && !okrConfigWorkTypeList.isEmpty() ){
					wraps = Wo.copier.copy( okrConfigWorkTypeList );
					for( Wo wrap : wraps ){
						//统计用户可以看到的每一个类别的中心工作数量
						workTypes.clear();
						if( wrap.getWorkTypeName() != null && !wrap.getWorkTypeName().isEmpty() ){
							workTypes.add( wrap.getWorkTypeName() );
						}
						wrapIn.setDefaultWorkTypes( workTypes );
						if( !okrUserCache.isOkrManager() ){
							wrapIn.setIdentity( okrUserCache.getLoginIdentityName() );
							total = okrWorkPersonSearchService.getCenterCountWithFilter( wrapIn );
						}else{
							WorkCommonQueryFilter wrpaIn_admin = new WorkCommonQueryFilter();
							wrpaIn_admin.setDefaultWorkTypes( workTypes );
							total = okrCenterWorkQueryService.getCountWithFilter( wrpaIn_admin );
						}
						wrap.setCenterCount( total );
					}
					result.setData( wraps );
				}
			} catch ( Exception e) {
				Exception exception = new ExceptionWorkTypeConfigListTypeCount(e);
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}	
		return result;
	}
	
	public static class Wo extends OkrConfigWorkType{

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier<OkrConfigWorkType, Wo> copier = WrapCopierFactory.wo( OkrConfigWorkType.class, Wo.class, null, JpaObject.FieldsInvisible);
		
		private Long centerCount = 0L;

		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		public Long getCenterCount() {
			return centerCount;
		}

		public void setCenterCount(Long centerCount) {
			this.centerCount = centerCount;
		}
	}
}