package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import java.util.List;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.okr.assemble.common.date.DateOperation;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.WrapOutOkrCenterWorkInfo;
import com.x.okr.assemble.control.jaxrs.okrworkauthorizerecord.WrapOutOkrWorkAuthorizeRecord;
import com.x.okr.assemble.control.service.OkrCenterWorkQueryService;
import com.x.okr.assemble.control.service.OkrConfigSystemService;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrUserManagerService;
import com.x.okr.assemble.control.service.OkrWorkAuthorizeRecordService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoQueryService;
import com.x.okr.assemble.control.service.OkrWorkDetailInfoService;
import com.x.okr.assemble.control.service.OkrWorkDynamicsService;
import com.x.okr.assemble.control.service.OkrWorkPersonService;
import com.x.okr.assemble.control.service.OkrWorkProcessIdentityService;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkAuthorizeRecord;
import com.x.okr.entity.OkrWorkBaseInfo;

public class ExcuteBase {

	protected BeanCopyTools<OkrWorkBaseInfo, WrapOutOkrWorkBaseInfo> wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkBaseInfo.class, WrapOutOkrWorkBaseInfo.class, null, WrapOutOkrWorkBaseInfo.Excludes);
	protected BeanCopyTools<OkrCenterWorkInfo, WrapOutOkrCenterWorkInfo> okrCenterWorkInfo_wrapout_copier = BeanCopyToolsBuilder.create( OkrCenterWorkInfo.class, WrapOutOkrCenterWorkInfo.class, null, WrapOutOkrCenterWorkInfo.Excludes);
	protected BeanCopyTools<OkrWorkAuthorizeRecord, WrapOutOkrWorkAuthorizeRecord> okrWorkAuthorizeRecord_wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkAuthorizeRecord.class, WrapOutOkrWorkAuthorizeRecord.class, null, WrapOutOkrWorkAuthorizeRecord.Excludes);
	protected OkrWorkProcessIdentityService okrWorkProcessIdentityService = new OkrWorkProcessIdentityService();
	protected BeanCopyTools<OkrWorkBaseInfo, WrapOutOkrWorkBaseSimpleInfo> wrapout_copier_worksimple = BeanCopyToolsBuilder.create( OkrWorkBaseInfo.class, WrapOutOkrWorkBaseSimpleInfo.class, null, WrapOutOkrWorkBaseSimpleInfo.Excludes);
	protected OkrCenterWorkQueryService okrCenterWorkInfoService = new OkrCenterWorkQueryService();
	protected OkrWorkAuthorizeRecordService okrWorkAuthorizeRecordService = new OkrWorkAuthorizeRecordService();
	protected OkrWorkBaseInfoQueryService okrWorkBaseInfoService = new OkrWorkBaseInfoQueryService();
	protected OkrWorkPersonService okrWorkPersonService = new OkrWorkPersonService();
	protected OkrWorkDetailInfoService okrWorkDetailInfoService = new OkrWorkDetailInfoService();
	protected OkrUserManagerService okrUserManagerService = new OkrUserManagerService();
	protected OkrWorkDynamicsService okrWorkDynamicsService = new OkrWorkDynamicsService();
	protected OkrConfigSystemService okrConfigSystemService = new OkrConfigSystemService();
	protected OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
	protected DateOperation dateOperation = new DateOperation();
	
	/**
	 * 根据用户传入的责任者身份信息查询并补充工作对象的责任者相关组织信息
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	protected WrapInOkrWorkBaseInfo composeResponsibilityInfoByIdentity( WrapInOkrWorkBaseInfo wrapIn ) throws Exception {
		if( wrapIn.getResponsibilityIdentity() != null && !wrapIn.getResponsibilityIdentity().isEmpty() ){
			String userName = "";
			String identity = "";
			String organizationName = "";
			String companyName = "";
			String[] identityNames = null;
			identityNames = wrapIn.getResponsibilityIdentity().split( "," );
			try{
				for( String _identity : identityNames ){
					if( okrUserManagerService.getUserNameByIdentity( _identity ) == null ){
						throw new Exception( "person not exsits, identity:" + _identity );
					}
					if( identity == null || identity.isEmpty() ){
						identity += _identity;
					}else{
						identity += "," + _identity;
					}
					if( userName == null || userName.isEmpty() ){
						userName = okrUserManagerService.getUserNameByIdentity(_identity);
					}else{
						userName += "," + okrUserManagerService.getUserNameByIdentity(_identity);
					}
					if( organizationName == null || organizationName.isEmpty() ){
						organizationName = okrUserManagerService.getDepartmentNameByIdentity( _identity );
					}else{
						organizationName += "," + okrUserManagerService.getDepartmentNameByIdentity( _identity );
					}
					if( companyName == null || companyName.isEmpty() ){
						companyName = okrUserManagerService.getCompanyNameByIdentity(_identity);
					}else{
						companyName += "," + okrUserManagerService.getCompanyNameByIdentity(_identity);
					}
				}
				wrapIn.setResponsibilityEmployeeName(userName);
				wrapIn.setResponsibilityIdentity(identity);
				wrapIn.setResponsibilityOrganizationName(organizationName);
				wrapIn.setResponsibilityCompanyName(companyName);
			}catch(Exception e){
				throw e;
			}
		}else{
			throw new Exception( "wrapIn getResponsibilityIdentity is null!" );
		}
		return wrapIn;
	}
	
	/**
	 * 根据用户传入的协助者身份信息查询并补充工作对象的协助者相关组织信息
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	protected WrapInOkrWorkBaseInfo composeCooperateInfoByIdentity( WrapInOkrWorkBaseInfo wrapIn ) throws Exception {
		if( wrapIn.getCooperateIdentity() != null && !wrapIn.getCooperateIdentity().isEmpty() ){
			String userName = "";
			String identity = "";
			String organizationName = "";
			String companyName = "";
			String[] identityNames = null;
			identityNames = wrapIn.getCooperateIdentity().split( "," );
			try{
				for( String _identity : identityNames ){
					if( okrUserManagerService.getUserNameByIdentity(_identity) == null ){
						throw new Exception( "person not exsits, identity:" + _identity );
					}
					if( identity == null || identity.isEmpty() ){
						identity += _identity;
					}else{
						identity += "," + _identity;
					}
					if( userName == null || userName.isEmpty() ){
						userName = okrUserManagerService.getUserNameByIdentity(_identity);
					}else{
						userName += "," + okrUserManagerService.getUserNameByIdentity(_identity);
					}
					if( organizationName == null || organizationName.isEmpty() ){
						organizationName = okrUserManagerService.getDepartmentNameByIdentity(_identity);
					}else{
						organizationName += "," + okrUserManagerService.getDepartmentNameByIdentity(_identity);
					}
					if( companyName == null || companyName.isEmpty() ){
						companyName = okrUserManagerService.getCompanyNameByIdentity(_identity);
					}else{
						companyName += "," + okrUserManagerService.getCompanyNameByIdentity(_identity);
					}
				}
				wrapIn.setCooperateEmployeeName(userName);
				wrapIn.setCooperateIdentity(identity);
				wrapIn.setCooperateOrganizationName(organizationName);
				wrapIn.setCooperateCompanyName(companyName);
			}catch(Exception e){
				throw e;				
			}
		}else{
			wrapIn.setCooperateEmployeeName( "" );
			wrapIn.setCooperateOrganizationName( "" );
			wrapIn.setCooperateCompanyName( "" );
		}
		return wrapIn;
	}
	
	protected WrapInOkrWorkBaseInfo composeReadLeaderByIdentity(WrapInOkrWorkBaseInfo wrapIn) throws Exception {
		if( wrapIn.getReadLeaderIdentity() != null && !wrapIn.getReadLeaderIdentity().isEmpty() ){
			String userName = "";
			String identity = "";
			String organizationName = "";
			String companyName = "";
			String[] identityNames = null;
			identityNames = wrapIn.getReadLeaderIdentity().split( "," );
			try{
				for( String _identity : identityNames ){
					if( okrUserManagerService.getUserNameByIdentity( _identity ) == null ){
						throw new Exception( "person not exsits, identity:" + _identity );
					}
					if( identity == null || identity.isEmpty() ){
						identity += _identity;
					}else{
						identity += "," + _identity;
					}
					if( userName == null || userName.isEmpty() ){
						userName = okrUserManagerService.getUserNameByIdentity( _identity );
					}else{
						userName += "," + okrUserManagerService.getUserNameByIdentity(_identity);
					}
					if( organizationName == null || organizationName.isEmpty() ){
						organizationName = okrUserManagerService.getDepartmentNameByIdentity(_identity);
					}else{
						organizationName += "," + okrUserManagerService.getDepartmentNameByIdentity(_identity);
					}
					if( companyName == null || companyName.isEmpty() ){
						companyName = okrUserManagerService.getCompanyNameByIdentity(_identity);
					}else{
						companyName += "," + okrUserManagerService.getCompanyNameByIdentity(_identity);
					}
				}
				wrapIn.setReadLeaderName(userName);
				wrapIn.setReadLeaderIdentity(identity);
				wrapIn.setReadLeaderOrganizationName(organizationName);
				wrapIn.setReadLeaderCompanyName(companyName);
			}catch(Exception e){
				throw e;
			}
		}else{
			wrapIn.setReadLeaderName( "" );
			wrapIn.setReadLeaderOrganizationName( "" );
			wrapIn.setReadLeaderCompanyName( "" );
		}
		return wrapIn;
	}
	
	/**
	 * 根据工作信息装配下级工作信息（递归）
	 * @param all_wrapWorkBaseInfoList
	 * @param wrap_work
	 * @return
	 */
	protected WrapOutOkrWorkBaseInfo composeSubWork(List<WrapOutOkrWorkBaseInfo> all_wrapWorkBaseInfoList, WrapOutOkrWorkBaseInfo wrap_work) {
		if( all_wrapWorkBaseInfoList != null && !all_wrapWorkBaseInfoList.isEmpty() ){
			for( WrapOutOkrWorkBaseInfo work : all_wrapWorkBaseInfoList ){
				if( work.getParentWorkId() != null && work.getParentWorkId().equalsIgnoreCase( wrap_work.getId() )){
				   //说明该工作是wrap_work的下级工作
					work = composeSubWork(all_wrapWorkBaseInfoList, work);
					wrap_work.addNewSubWorkBaseInfo( work );
				}
			}
		}
		return wrap_work;
	}
}
