package com.x.okr.assemble.control.service;

import java.util.List;

public class OkrWorkProcessIdentityService {

	private OkrWorkPersonService okrWorkPersonService = new OkrWorkPersonService();

	/**
	 * 判断一个工作是否是用户阅知的工作, 用户是否在工作的干系人身份中拥有阅知者身份
	 * @param workId
	 * @return
	 * @throws Exception 
	 */
	public Boolean isMyReadWork( String userIdentity, String workId ) throws Exception {
		if( userIdentity == null || userIdentity.isEmpty() ){
			throw new Exception("user identity is null, can not query work person.");
		}
		if( workId == null || workId.isEmpty() ){
			throw new Exception("workId is null, can not query work person.");
		}
		List<String> ids = okrWorkPersonService.listByWorkAndIdentity( null, workId, userIdentity, "阅知者", null );
		if( ids != null && !ids.isEmpty() ){
			return true;
		}
		return false;
	}

	public boolean isMyCooperateWork( String userIdentity, String workId ) throws Exception {
		if( userIdentity == null || userIdentity.isEmpty() ){
			throw new Exception("user identity is null, can not query work person.");
		}
		if( workId == null || workId.isEmpty() ){
			throw new Exception("workId is null, can not query work person.");
		}
		List<String> ids = okrWorkPersonService.listByWorkAndIdentity( null, workId, userIdentity, "协助者", null );
		if( ids != null && !ids.isEmpty() ){
			return true;
		}
		return false;
	}

	public boolean isMyResponsibilityWork( String userIdentity, String workId ) throws Exception {
		if( userIdentity == null || userIdentity.isEmpty() ){
			throw new Exception("user identity is null, can not query work person.");
		}
		if( workId == null || workId.isEmpty() ){
			throw new Exception("workId is null, can not query work person.");
		}
		List<String> ids = okrWorkPersonService.listByWorkAndIdentity( null, workId, userIdentity, "责任者", null );
		if( ids != null && !ids.isEmpty() ){
			return true;
		}
		return false;
	}

	public boolean isMyAuthorizeWork(String userIdentity, String workId ) throws Exception {
		if( userIdentity == null || userIdentity.isEmpty() ){
			throw new Exception("user identity is null, can not query work person.");
		}
		if( workId == null || workId.isEmpty() ){
			throw new Exception("workId is null, can not query work person.");
		}
		List<String> ids = okrWorkPersonService.listByWorkAndIdentity( null, workId, userIdentity, "授权者", null );
		if( ids != null && !ids.isEmpty() ){
			return true;
		}
		return false;
	}

	public boolean isMyDeployWork(String userIdentity, String workId ) throws Exception {
		if( userIdentity == null || userIdentity.isEmpty() ){
			throw new Exception("user identity is null, can not query work person.");
		}
		if( workId == null || workId.isEmpty() ){
			throw new Exception("workId is null, can not query work person.");
		}
		List<String> ids = okrWorkPersonService.listByWorkAndIdentity( null, workId, userIdentity, "部署者", null );
		if( ids != null && !ids.isEmpty() ){
			return true;
		}
		return false;
	}
	
	/**
	 * 判断一个工作是否是用户阅知的工作, 用户是否在工作的干系人身份中拥有阅知者身份
	 * @param workId
	 * @return
	 * @throws Exception 
	 */
	public Boolean isMyReadCenter( String userIdentity, String centerId ) throws Exception {
		if( userIdentity == null || userIdentity.isEmpty() ){
			throw new Exception("user identity is null, can not query work person.");
		}
		if( centerId == null || centerId.isEmpty() ){
			throw new Exception("centerId is null, can not query work person.");
		}
		List<String> ids = okrWorkPersonService.listByWorkAndIdentity( centerId, null, userIdentity, "阅知者", null );
		if( ids != null && !ids.isEmpty() ){
			return true;
		}
		return false;
	}
	
	public boolean isMyDeployCenter(String userIdentity, String centerId ) throws Exception {
		if( userIdentity == null || userIdentity.isEmpty() ){
			throw new Exception("user identity is null, can not query work person.");
		}
		if( centerId == null || centerId.isEmpty() ){
			throw new Exception("centerId is null, can not query work person.");
		}
		List<String> ids = okrWorkPersonService.listByWorkAndIdentity( centerId, null, userIdentity, "部署者", null );
		if( ids != null && !ids.isEmpty() ){
			return true;
		}
		return false;
	}
	
}