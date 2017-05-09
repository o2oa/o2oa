package com.x.bbs.assemble.control.jaxrs.roleinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.WrapTools;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.ForumIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.ForumInfoNotExistsException;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.RoleINameEmptyException;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.RoleITypeEmptyException;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.RoleITypeInvalidException;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.RoleInfoProcessException;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.SectionIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.SectionNotExistsException;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSRoleInfo;
import com.x.bbs.entity.BBSSectionInfo;

public class ExcuteSave extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteSave.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, WrapInRoleInfo wrapIn ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		BBSForumInfo forumInfo = null;
		BBSSectionInfo sectionInfo = null;
		BBSRoleInfo roleInfo_old = null;
		BBSRoleInfo roleInfo = new BBSRoleInfo();
		List<String> permissionCodes = null;
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();
		String uniCode = null;
		Boolean check = true;
		
		if (check) {
			if ( wrapIn.getRoleName() == null || wrapIn.getRoleName().isEmpty()) {
				check = false;
				Exception exception = new RoleINameEmptyException();
				result.error( exception );
			}
		}
		// 校验角色类别
		if (check) {
			if (wrapIn.getRoleType() == null || wrapIn.getRoleType().isEmpty()) {
				check = false;
				Exception exception = new RoleITypeEmptyException();
				result.error( exception );
			}
		}
		// 检验信息，如果是论坛角色，那么论坛ID为必须填写，如果是版块角色那么版块ID必须填写
		if (check) {
			if ("论坛角色".equals(wrapIn.getRoleType())) {
				if (wrapIn.getForumId() == null || wrapIn.getForumId().isEmpty()) {
					check = false;
					Exception exception = new ForumIdEmptyException();
					result.error( exception );
				}
			} else if ("版块角色".equals(wrapIn.getRoleType())) {
				if (wrapIn.getSectionId() == null || wrapIn.getSectionId().isEmpty()) {
					check = false;
					Exception exception = new SectionIdEmptyException();
					result.error( exception );
				}
			} else {
				check = false;
				Exception exception = new RoleITypeInvalidException( wrapIn.getRoleType() );
				result.error( exception );
			}
		}
		// 检验传入的版块ID和论坛ID是否合法
		if (check) {
			if ("论坛角色".equals(wrapIn.getRoleType())) {
				try {
					forumInfo = forumInfoServiceAdv.get(wrapIn.getForumId());
					if (forumInfo == null) {
						check = false;
						Exception exception = new ForumInfoNotExistsException( wrapIn.getForumId() );
						result.error( exception );
					} else {
						uniCode = forumInfo.getId();
						wrapIn.setForumId(forumInfo.getId());
						wrapIn.setForumName(forumInfo.getForumName());
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new RoleInfoProcessException( e, "系统在根据ID获取BBS论坛分区信息时发生异常！ID:" + wrapIn.getForumId() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			} else if ("版块角色".equals(wrapIn.getRoleType())) {
				try {
					sectionInfo = sectionInfoServiceAdv.get(wrapIn.getSectionId());
					if (sectionInfo == null) {
						check = false;
						Exception exception = new SectionNotExistsException( wrapIn.getSectionId() );
						result.error( exception );
					} else {
						uniCode = sectionInfo.getId();
						wrapIn.setSectionId(sectionInfo.getId());
						wrapIn.setSectionName(sectionInfo.getSectionName());
						wrapIn.setForumId(sectionInfo.getForumId());
						wrapIn.setForumName(sectionInfo.getForumName());
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new RoleInfoProcessException( e, "根据指定ID查询版块信息时发生异常.ID:" + wrapIn.getSectionId() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		if (check) {
			permissionCodes = wrapIn.getPermissionCodes();
			wrapIn.setCreatorName(effectivePerson.getName());
		}
		if (check) {
			if (wrapIn.getRoleCode() == null || wrapIn.getRoleCode().isEmpty()) {
				try {
					// 自动组织一个角色编码：BBS + 角色名称的拼音 + ID
					wrapIn.setRoleCode( "BBS_" + PinyinHelper.getShortPinyin( wrapIn.getRoleName()).toUpperCase() + "_" + uniCode );
				} catch (Exception e) {
					check = false;
					Exception exception = new RoleInfoProcessException( e, "根据指定ID查询版块信息时发生异常.ID:" + wrapIn.getRoleName() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		if (check) {
			try {
				roleInfo = WrapTools.roleInfo_wrapin_copier.copy( wrapIn );
			} catch (Exception e) {
				check = false;
				Exception exception = new RoleInfoProcessException( e, "将用户传入的信息转换为一个角色信息对象时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (roleInfo.getRoleCode() != null && !roleInfo.getRoleCode().isEmpty()) {
				try {
					roleInfo_old = roleInfoService.getByRoleCode(roleInfo.getRoleCode());
				} catch (Exception e) {
					check = false;
					Exception exception = new RoleInfoProcessException( e, "系统在根据编码获取BBS角色信息时发生异常！Code:" + roleInfo.getRoleCode() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		if (check) {
			try {
				roleInfo = roleInfoService.save(roleInfo, permissionCodes);
				wrap = new WrapOutId(roleInfo.getId());
				result.setData(wrap);
				if (roleInfo_old != null) {
					operationRecordService.roleOperation(effectivePerson.getName(), roleInfo, "MODIFY", hostIp, hostName);
				} else {
					operationRecordService.roleOperation(effectivePerson.getName(), roleInfo, "CREATE", hostIp, hostName);
				}
			} catch ( Exception e ) {
				check = false;
				Exception exception = new RoleInfoProcessException( e, "系统在保存BBS角色信息时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}

}