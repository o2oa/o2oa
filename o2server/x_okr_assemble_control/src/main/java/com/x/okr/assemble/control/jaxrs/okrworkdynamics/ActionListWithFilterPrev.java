package com.x.okr.assemble.control.jaxrs.okrworkdynamics;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkchat.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkchat.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.okrworkchat.exception.ExceptionWrapInConvert;
import com.x.okr.assemble.control.jaxrs.okrworkdynamics.exception.ExceptionDeployWorkIdsQuery;
import com.x.okr.assemble.control.jaxrs.okrworkdynamics.exception.ExceptionViewableWorkIdsQuery;
import com.x.okr.assemble.control.jaxrs.okrworkdynamics.exception.ExceptionWorkDynamicsFilter;
import com.x.okr.entity.OkrWorkDynamics;

public class ActionListWithFilterPrev extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionListWithFilterPrev.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id, Integer count, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<List<Wo>>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<Wo> wrapOutOkrWorkDynamicsList = null;
		List<OkrWorkDynamics> dynamicsList = null;
		List<String> deploy_ids = null;
		List<String> work_ids = null;
		List<String> statuses =  new ArrayList<String>();
		OkrUserCache  okrUserCache  = null;
		String identity = null;
		Long total = 0L;
		Wi wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, currentPerson, request, null);
		}

		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getDistinguishedName() );
			} catch ( Exception e ) {
				check = false;
				Exception exception = new ExceptionGetOkrUserCache( e, currentPerson.getDistinguishedName()  );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}	
		}
		
		if( check && okrUserCache == null ){
			check = false;
			Exception exception = new ExceptionUserNoLogin( currentPerson.getDistinguishedName()  );
			result.error( exception );
		}
		if( count == null ){
			count = 20;
		}
		
		if( check ){
			identity = okrUserCache.getLoginIdentityName();
			if( identity == null ){
				check = false;
				Exception exception = new ExceptionUserNoLogin( currentPerson.getDistinguishedName()  );
				result.error( exception );
			}
		}
		
		if( check ){
			statuses.add( "正常" );
			
			//计算可以查看的范围
			if( okrUserCache.isOkrManager() ){
				logger.debug( "用户是OkrSystemAdmin." );
				//如果是系统管理员，可以查看全部，不需要进行ID过滤
				wrapIn.setCenterIds( null );
				wrapIn.setWorkId( null );
				wrapIn.setOkrSystemAdmin(true);
			}else{
				wrapIn.setOkrSystemAdmin(false);
				//如果不是管理员：
				//先查询用户部署的中心工作ID，这些中心工作可以全部看到
				try {
					deploy_ids = okrWorkPersonService.listDistinctCenterIdsByPersonIdentity( identity, "部署者", statuses );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionDeployWorkIdsQuery( e, identity  );
					result.error( exception );
					logger.error( e, currentPerson, request, null);
				}
				
				//再查询不在deploy_ids这些中心工作下面可以观察的的其他工作的IDS
				try {
					work_ids = okrWorkPersonService.listDistinctWorkIdsByPersonIndentity( null, identity, "观察者", deploy_ids );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionViewableWorkIdsQuery( e, identity  );
					result.error( exception );
					logger.error( e, currentPerson, request, null);
				}
				wrapIn.setWorkIds( work_ids );
				wrapIn.setCenterIds( deploy_ids );
			}			
		}
		
		if( check ){
			wrapIn.setUserIdentity( identity );
			try{
				dynamicsList = okrWorkDynamicsService.listDynamicPrevWithFilter( id, count, wrapIn.getCenterIds(),
						wrapIn.getWorkIds(), wrapIn.getSequenceField(), wrapIn.getOrder(), wrapIn.isOkrManager() );
				wrapOutOkrWorkDynamicsList = Wo.copier.copy( dynamicsList );
				total = okrWorkDynamicsService.getDynamicCountWithFilter( wrapIn.getCenterIds(),
						wrapIn.getWorkIds(), wrapIn.getSequenceField(), wrapIn.getOrder(), wrapIn.isOkrManager() );
				result.setData( wrapOutOkrWorkDynamicsList );
				result.setCount( total );
			}catch(Exception e){
				Exception exception = new ExceptionWorkDynamicsFilter( e );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
		}else{
			result.setCount( 0L );
			result.setData( new ArrayList<Wo>() );
		}	
		return result;
	}
	
	public static class Wi extends GsonPropertyObject {
		
		private String workId;
		
		private List<String> centerIds = null;
		
		private List<String> workIds = null;
		
		private String sequenceField =  JpaObject.sequence_FIELDNAME;
		
		private String userIdentity = null;
		
		private String key;
		
		private String order = "DESC";

		private boolean isOkrManager = false;
		
		public String getWorkId() {
			return workId;
		}

		public void setWorkId(String workId) {
			this.workId = workId;
		}

		public String getSequenceField() {
			return sequenceField;
		}

		public void setSequenceField(String sequenceField) {
			this.sequenceField = sequenceField;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getOrder() {
			return order;
		}

		public void setOrder(String order) {
			this.order = order;
		}

		public String getUserIdentity() {
			return userIdentity;
		}

		public void setUserIdentity(String userIdentity) {
			this.userIdentity = userIdentity;
		}

		public List<String> getCenterIds() {
			return centerIds;
		}

		public void setCenterIds(List<String> centerIds) {
			this.centerIds = centerIds;
		}

		public List<String> getWorkIds() {
			return workIds;
		}

		public void setWorkIds(List<String> workIds) {
			this.workIds = workIds;
		}

		public boolean isOkrManager() {
			return isOkrManager;
		}

		public void setOkrSystemAdmin(boolean isOkrManager) {
			this.isOkrManager = isOkrManager;
		}
		
	}
	
	public static class Wo extends OkrWorkDynamics{

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<OkrWorkDynamics, Wo> copier = WrapCopierFactory.wo( OkrWorkDynamics.class, Wo.class, null, JpaObject.FieldsInvisible);
		
		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
		
	}
}