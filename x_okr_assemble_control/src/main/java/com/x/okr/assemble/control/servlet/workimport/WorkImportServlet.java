package com.x.okr.assemble.control.servlet.workimport;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.application.servlet.FileUploadServletTools;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.okr.assemble.common.date.DateOperation;
import com.x.okr.assemble.common.excel.reader.ExcelReaderUtil;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.ThisApplication;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.WrapInOkrWorkBaseInfo;
import com.x.okr.assemble.control.service.OkrCenterWorkInfoService;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrUserManagerService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoService;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkBaseInfo;

/**
 * 工作信息导入服务
 * 
 * @author LIYI
 *
 */
@WebServlet(urlPatterns = "/servlet/import/center/*")
@MultipartConfig
public class WorkImportServlet extends HttpServlet {

	private static final long serialVersionUID = 5628571943877405247L;
	private Logger logger = LoggerFactory.getLogger( WorkImportServlet.class );
	private OkrCenterWorkInfoService okrCenterWorkInfoService = new OkrCenterWorkInfoService();
	private OkrWorkBaseInfoService okrWorkBaseInfoService = new OkrWorkBaseInfoService();
	private OkrUserManagerService okrUserManagerService = new OkrUserManagerService();
	private OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
	private DateOperation dateOperation = new DateOperation();

	@HttpMethodDescribe(value = "上传附件 servlet/import/center/{centerId}", response = Object.class)
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ActionResult<Object> result = new ActionResult<Object>();
		EffectivePerson effectivePerson = null;
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		ServletFileUpload upload = null;
		FileItemIterator fileItemIterator = null;
		FileItemStream item = null;
		InputStream input = null;
		String centerId = null;
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		request.setCharacterEncoding("UTF-8");

		// 从请求对象里获取操作用户信息
		if (check) {
			try {
				effectivePerson = FileUploadServletTools.effectivePerson(request);
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统从请求对象里获取操作用户信息发生异常！");
				logger.error("system get effectivePerson from request got an exception.", e);
			}
		}
		
		if (check) {
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getName() );
			} catch (Exception e1) {
				check = false;
				result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
				result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
				logger.error( "system get login indentity with person name got an exception", e1 );
			}	
		}
			
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + effectivePerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		
		if (check) {
			// logger.debug( ">>>>>>>>>>>>>>>>>>>>>>>>系统正在校验登录用户身份信息......" );
			if ( okrUserCache.getLoginUserName() == null ) {
				check = false;
				result.error(new Exception("can not find login user identity, please reopen ."));
				result.setUserMessage("系统未获取到用户登录身份(登录用户名)，请重新打开应用!");
			}
		}

		if (check) {
			// logger.debug( ">>>>>>>>>>>>>>>>>>>>>>>>系统正在校验上传文件内容是否存在......" );
			if (!ServletFileUpload.isMultipartContent(request)) {
				check = false;
				result.error(new Exception("not mulit part request."));
				result.setUserMessage("请求中未含有文件信息，未发现需要导入的文件！");
			}
		}

		// 从URL里获取centerId
		if (check) {
			// logger.debug(
			// ">>>>>>>>>>>>>>>>>>>>>>>>系统正在从URL里获取需要的参数信息[centerId]......" );
			try {
				centerId = FileUploadServletTools.getURIPart(request.getRequestURI(), "center");
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统从URL获取中心工作ID参数时发生异常。");
				logger.error("system get center id from url got an exception.", e);
			}
		}

		// 判断中心工作信息是否存在
		if (check) {
			// logger.debug(
			// ">>>>>>>>>>>>>>>>>>>>>>>>系统正在判断中心工作{'id':'"+centerId+"'}信息是否存在......"
			// );
			try {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					okrCenterWorkInfo = emc.find(centerId, OkrCenterWorkInfo.class);
					if (null == okrCenterWorkInfo) {// 中心工作不存在
						check = false;
						result.error(new Exception("The center work{'id':'" + centerId + "'} is not exists."));
						result.setUserMessage("中心工作不存在。");
					}
				}
			} catch (Exception e) {// 获取中心工作发生异常
				check = false;
				result.error(e);
				result.setUserMessage("系统从数据库中根据中心工作ID获取中心工作信息时发生异常。");
				logger.error("system get okrCenterWorkInfo{id:" + centerId + "} from database got an exception.", e);
			}
		}

		// 获取文件内容并且对文件进行分析
		if (check) {
			logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>系统获取文件内容并且尝试解析并且收集需要导入的数据内容......");
			try {
				upload = new ServletFileUpload();
				fileItemIterator = upload.getItemIterator(request);
				while (fileItemIterator.hasNext()) {
					item = fileItemIterator.next();
					if (item != null && item.getName() != null && !item.getName().isEmpty()) {
						input = item.openStream();
						// 读取EXCEL文件中的所有数据
						ThisApplication.getImportFileStatusMap().remove(effectivePerson.getName());
						ExcelReaderUtil.readExcel(new WorkImportExcelReader(), item.getName(), input,
								effectivePerson.getName(), 1);
					}
				}
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统从EXCEL文件获取数据时发生异常。");
				logger.error("[UploadServlet]system try to save okrAttachmentFileInfo to Storage got an exception.", e);
			} finally {
				if (input != null) {
					input.close();
				}
			}
		}

		if (check) {
			logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>系统对收集完成的数据进行校验和保存......");
			List<CacheImportRowDetail> importRowList = null;

			// 对从EXCEL文件里获取的所有信息逐一进行数据校验以及保存操作
			CacheImportFileStatus cacheImportFileStatus = null;
			cacheImportFileStatus = ThisApplication.getCacheImportFileStatusElementByKey(effectivePerson.getName());
			if (cacheImportFileStatus != null) {
				importRowList = cacheImportFileStatus.getDetailList();
				if (importRowList != null) {
					result = saveAllImportRow(centerId, importRowList, effectivePerson);
				}
			} else {
				logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>系统中未发现收集完成的数据！");
			}
		}
		ThisApplication.getImportFileStatusMap().remove(effectivePerson.getName());
		FileUploadServletTools.result(response, result);
	}

	/**
	 * 检验所有需要保存的工作信息数据、补全数据，并且保存信息
	 * 
	 * @param importRowList
	 * @return
	 */
	private ActionResult<Object> saveAllImportRow(String centerId, List<CacheImportRowDetail> importRowList,
			EffectivePerson effectivePerson) {
		ActionResult<Object> result = new ActionResult<Object>();
		if (importRowList == null || importRowList.isEmpty()) {
			result.error(new Exception("no data need save, import row list is null."));
			result.setUserMessage("未获取到任何需要保存的数据。");
			return result;
		}
		List<WrapInOkrWorkBaseInfo> wrapInList = new ArrayList<WrapInOkrWorkBaseInfo>();
		List<WrapInOkrWorkBaseInfo> errorWrapInList = new ArrayList<WrapInOkrWorkBaseInfo>();
		WrapInOkrWorkBaseInfo wrapInOkrWorkBaseInfo = null;
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		// 这里需要去配置表里查询配置的汇报发起具体的时间点，如：10:00:00
		String reportStartTime = "10:00:00";
		String[] identityNames = null;
		String userName = null, organizationName = null, companyName = null, identity = null;
		String currentUserIdentityName = null;
		String currentUserOrganizationName = null;
		String currentUserCompanyName = null;
		String yearString = dateOperation.getYear(new Date()), monthString = dateOperation.getMonth(new Date()), nowDateString = dateOperation.getNowDateTime();
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + effectivePerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		// logger.debug( ">>>>>>>>>>>>>>>>>>>>>>>>系统正在为数据校验中心工作信息 :"+centerId );
		if (check) {
			// 补充中心工作标题
			if (centerId != null && !centerId.isEmpty()) {
				// 根据ID查询中心工作信息
				try {
					okrCenterWorkInfo = okrCenterWorkInfoService.get(centerId);
				} catch (Exception e) {
					check = false;
					result.error(e);
					result.setUserMessage("中心工作不存在,id:'" + centerId + "'，无法继续保存工作信息!");
					logger.error("center work info{'id':'" + centerId + "'} is not exists!", e);
				}
			}
		}

		if (check) {
			if (okrCenterWorkInfo == null) {
				check = false;
				result.error(new Exception("center work info{'id':'" + centerId + "'} is not exists."));
				result.setUserMessage("中心工作不存在,id:'" + centerId + "'，无法继续保存工作信息!");
				logger.error("center work info{'id':'" + centerId + "'} is not exists!");
			}
		}

		if (check) {
			try {
				currentUserIdentityName = okrUserManagerService.getFistIdentityNameByPerson(effectivePerson.getName());
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统根据登录者获取身份名称发生异常，无法继续保存工作信息!");
				logger.error("system get identity by effectivePerson got an exception.", e);
			}
		}
		if (check) {
			try {
				currentUserOrganizationName = okrUserManagerService
						.getDepartmentNameByIdentity(currentUserIdentityName);
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统根据登录者获取组织名称发生异常，无法继续保存工作信息!");
				logger.error("system get organization name by effectivePerson got an exception.", e);
			}
		}
		if (check) {
			try {
				currentUserCompanyName = okrUserManagerService.getCompanyNameByIdentity(currentUserIdentityName);
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统根据登录者获取公司名称发生异常，无法继续保存工作信息!");
				logger.error("system get company name by effectivePerson got an exception.", e);
			}
		}

		for (CacheImportRowDetail cacheImportRowDetail : importRowList) {
			logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>系统正在校验工作信息合法性，标题：[" + cacheImportRowDetail.getTitle() + "]");
			wrapInOkrWorkBaseInfo = new WrapInOkrWorkBaseInfo();

			wrapInOkrWorkBaseInfo.setCompleteDateLimit(cacheImportRowDetail.getCompleteDateLimit());
			wrapInOkrWorkBaseInfo.setCompleteDateLimitStr(cacheImportRowDetail.getCompleteDateLimitStr());
			wrapInOkrWorkBaseInfo.setCooperateIdentity(cacheImportRowDetail.getCooperateIdentity());
			wrapInOkrWorkBaseInfo.setCreatorIdentity(cacheImportRowDetail.getCreatorIdentity());
			wrapInOkrWorkBaseInfo.setDeployerIdentity(cacheImportRowDetail.getDeployerIdentity());
			wrapInOkrWorkBaseInfo.setDutyDescription(cacheImportRowDetail.getDutyDescription());
			wrapInOkrWorkBaseInfo.setMajorIssuesDescription(cacheImportRowDetail.getMajorIssuesDescription());
			wrapInOkrWorkBaseInfo.setLandmarkDescription(cacheImportRowDetail.getLandmarkDescription());
			wrapInOkrWorkBaseInfo.setParentWorkId(cacheImportRowDetail.getParentWorkId());
			wrapInOkrWorkBaseInfo.setProgressAction(cacheImportRowDetail.getProgressAction());
			wrapInOkrWorkBaseInfo.setProgressPlan(cacheImportRowDetail.getProgressPlan());
			wrapInOkrWorkBaseInfo.setReadLeaderIdentity(cacheImportRowDetail.getReadLeaderIdentity());
			wrapInOkrWorkBaseInfo.setResultDescription(cacheImportRowDetail.getResultDescription());
			wrapInOkrWorkBaseInfo.setTitle(cacheImportRowDetail.getTitle());
			wrapInOkrWorkBaseInfo.setWorkDateTimeType(cacheImportRowDetail.getWorkDateTimeType());
			wrapInOkrWorkBaseInfo.setWorkDetail(cacheImportRowDetail.getWorkDetail());
			wrapInOkrWorkBaseInfo.setWorkLevel(cacheImportRowDetail.getWorkLevel());
			wrapInOkrWorkBaseInfo.setWorkType(okrCenterWorkInfo.getDefaultWorkType());
			wrapInOkrWorkBaseInfo.setReportCycle(cacheImportRowDetail.getReportCycle());
			wrapInOkrWorkBaseInfo.setReportDayInCycle(cacheImportRowDetail.getReportDayInCycle());
			wrapInOkrWorkBaseInfo.setCheckSuccess(cacheImportRowDetail.getCheckStatus());
			wrapInOkrWorkBaseInfo.setDescription(cacheImportRowDetail.getDescription());

			if ("success".equals(wrapInOkrWorkBaseInfo.getCheckSuccess())) {
				try {
					wrapInOkrWorkBaseInfo.setWorkProcessStatus("草稿");

					// 对wrapIn里的信息进行校验
					if (check && okrUserCache.getLoginIdentityName()  == null) {
						check = false;
						result.error(new Exception("can not find login user identity, please reopen ."));
						result.setUserMessage("系统未获取到用户登录身份(登录用户名)，请重新打开应用!");
					}

					if (check) {
						wrapInOkrWorkBaseInfo.setCenterId(centerId);
						wrapInOkrWorkBaseInfo.setCenterTitle(okrCenterWorkInfo.getTitle());
					}

					if (check) {
						// logger.debug(
						// ">>>>>>>>>>>>>>>>>>>>>>>>系统正在为数据补充创建人身份......" );
						// 创建人和部署人信息直接取当前操作人和登录人身份
						wrapInOkrWorkBaseInfo.setCreatorName(effectivePerson.getName());
						if (effectivePerson.getName().equals(okrUserCache.getLoginUserName())) {
							// 如果登录人和代理的身份的姓名是一致的，说明本来就是操作本人身份
							wrapInOkrWorkBaseInfo.setCreatorOrganizationName( okrUserCache.getLoginUserOrganizationName());
							wrapInOkrWorkBaseInfo.setCreatorCompanyName( okrUserCache.getLoginUserCompanyName());
							wrapInOkrWorkBaseInfo.setCreatorIdentity(okrUserCache.getLoginIdentityName() );
						} else {
							// 如果不是操作本人身份，则需要查询创建者的相关身份
							wrapInOkrWorkBaseInfo.setCreatorIdentity(currentUserIdentityName);
							wrapInOkrWorkBaseInfo.setCreatorOrganizationName(currentUserOrganizationName);
							wrapInOkrWorkBaseInfo.setCreatorCompanyName(currentUserCompanyName);
						}
					}

					if (check) {
						// logger.debug(
						// ">>>>>>>>>>>>>>>>>>>>>>>>系统正在为数据补充部署人身份......" );
						wrapInOkrWorkBaseInfo.setDeployerName(okrUserCache.getLoginUserName());
						if (effectivePerson.getName().equals(okrUserCache.getLoginUserName())) {
							wrapInOkrWorkBaseInfo.setDeployerOrganizationName(okrUserCache.getLoginUserOrganizationName());
							wrapInOkrWorkBaseInfo.setDeployerCompanyName(okrUserCache.getLoginUserCompanyName());
							wrapInOkrWorkBaseInfo.setDeployerIdentity(okrUserCache.getLoginIdentityName() );
						}
					}

					// 补充部署工作的年份和月份
					if (check) {
						// logger.debug(
						// ">>>>>>>>>>>>>>>>>>>>>>>>系统正在为数据补充部署工作的年份和月份......"
						// );
						wrapInOkrWorkBaseInfo.setDeployYear(yearString);
						wrapInOkrWorkBaseInfo.setDeployMonth(monthString);
						wrapInOkrWorkBaseInfo.setDeployDateStr(nowDateString);
					}

					if (check) {
						// logger.debug(
						// ">>>>>>>>>>>>>>>>>>>>>>>>系统正在为数据校验上级工作信息 ："+
						// cacheImportRowDetail.getParentWorkId());
						// 检验上级工作信息
						if (cacheImportRowDetail.getParentWorkId() != null
								&& !cacheImportRowDetail.getParentWorkId().isEmpty()) {
							// 根据ID查询中心工作信息
							okrWorkBaseInfo = okrWorkBaseInfoService.get(cacheImportRowDetail.getParentWorkId());
							if (okrWorkBaseInfo != null) {
								wrapInOkrWorkBaseInfo.setParentWorkId(okrWorkBaseInfo.getId());
								wrapInOkrWorkBaseInfo.setParentWorkTitle(okrWorkBaseInfo.getTitle());
							} else {
								check = false;
								result.error(new Exception("parent work info{'id':'"
										+ cacheImportRowDetail.getParentWorkId() + "'} is not exists."));
								result.setUserMessage(
										"上级工作不存在, id:'" + cacheImportRowDetail.getParentWorkId() + "'，无法继续保存工作信息!");
								logger.error("parent work info{'id':'" + cacheImportRowDetail.getParentWorkId()
										+ "'} is not exists!");
								wrapInOkrWorkBaseInfo.setCheckSuccess("failture");
								wrapInOkrWorkBaseInfo.setDescription(
										"上级工作不存在, id:'" + cacheImportRowDetail.getParentWorkId() + "'，无法继续保存工作信息!");
							}
						}
					}

					// 校验工作完成时限数据，补充日期型完成时限数据
					if (check) {
						// logger.debug(
						// ">>>>>>>>>>>>>>>>>>>>>>>>系统正在为数据校验工作完成时限数据，补充日期型完成时限数据
						// :"+cacheImportRowDetail.getCompleteDateLimitStr() );
						if (cacheImportRowDetail.getCompleteDateLimitStr() != null
								&& !cacheImportRowDetail.getCompleteDateLimitStr().isEmpty()) {
							try {
								wrapInOkrWorkBaseInfo.setCompleteDateLimit(dateOperation
										.getDateFromString(cacheImportRowDetail.getCompleteDateLimitStr()));
							} catch (Exception e) {
								check = false;
								result.error(e);
								result.setUserMessage("工作完成时限格式不正确：" + cacheImportRowDetail.getCompleteDateLimitStr()
										+ "，无法继续保存工作信息!");
								logger.error("complete date limit string is not date! "
										+ cacheImportRowDetail.getCompleteDateLimitStr(), e);
								wrapInOkrWorkBaseInfo.setCheckSuccess("failture");
								wrapInOkrWorkBaseInfo.setDescription("工作完成时限格式不正确："
										+ cacheImportRowDetail.getCompleteDateLimitStr() + "，无法继续保存工作信息!");
							}
						} else {
							check = false;
							result.setUserMessage("工作完成时限信息为空，无法继续保存工作信息!");
							logger.error("complete date limit string is null.");
							wrapInOkrWorkBaseInfo.setCheckSuccess("failture");
							wrapInOkrWorkBaseInfo.setDescription("工作完成时限信息为空，无法继续保存工作信息!");
						}
					}

					// 校验责任者数据， 判断责任者组织信息是否存在，如果不存在，则需要补充部门者组织信息
					if (check) {
						// logger.debug(
						// ">>>>>>>>>>>>>>>>>>>>>>>>系统正在为数据校验责任者数据，
						// 判断责任者组织信息是否存在，如果不存在，则需要补充部门者组织信息
						// :"+cacheImportRowDetail.getResponsibilityIdentity()
						// );
						if (cacheImportRowDetail.getResponsibilityIdentity() != null
								&& !cacheImportRowDetail.getResponsibilityIdentity().isEmpty()) {
							userName = "";
							identity = "";
							organizationName = "";
							companyName = "";
							identityNames = cacheImportRowDetail.getResponsibilityIdentity().split(",");
							try {
								for (String _identity : identityNames) {
									if (okrUserManagerService.getUserNameByIdentity(_identity) == null) {
										throw new Exception("person not exsits, identity:" + _identity);
									}
									if (identity == null || identity.isEmpty()) {
										identity += _identity;
									} else {
										identity += "," + _identity;
									}
									if (userName == null || userName.isEmpty()) {
										userName = okrUserManagerService.getUserNameByIdentity(_identity).getName();
									} else {
										userName += ","
												+ okrUserManagerService.getUserNameByIdentity(_identity).getName();
									}
									if (organizationName == null || organizationName.isEmpty()) {
										organizationName = okrUserManagerService.getDepartmentNameByIdentity(_identity);
									} else {
										organizationName += ","
												+ okrUserManagerService.getDepartmentNameByIdentity(_identity);
									}
									if (companyName == null || companyName.isEmpty()) {
										companyName = okrUserManagerService.getCompanyNameByIdentity(_identity);
									} else {
										companyName += "," + okrUserManagerService.getCompanyNameByIdentity(_identity);
									}
								}
								wrapInOkrWorkBaseInfo.setResponsibilityEmployeeName(userName);
								wrapInOkrWorkBaseInfo.setResponsibilityIdentity(identity);
								wrapInOkrWorkBaseInfo.setResponsibilityOrganizationName(organizationName);
								wrapInOkrWorkBaseInfo.setResponsibilityCompanyName(companyName);
							} catch (Exception e) {
								check = false;
								result.error(e);
								result.setUserMessage("系统校验工作责任人发生异常，" + userName + "!");
								logger.error("system query organization for Responsibility got an exception.", e);
								wrapInOkrWorkBaseInfo.setCheckSuccess("failture");
								wrapInOkrWorkBaseInfo.setDescription("系统校验工作责任人发生异常，" + userName + "!");
							}
						} else {
							check = false;
							result.setUserMessage("责任者[responsibilityEmployeeName]信息为空，无法继续保存工作信息!");
							logger.error("responsibilityEmployeeName is null.");
							wrapInOkrWorkBaseInfo.setCheckSuccess("failture");
							wrapInOkrWorkBaseInfo.setDescription("责任者[responsibilityEmployeeName]信息为空，无法继续保存工作信息!");
						}
					}

					if (check) {
						// logger.debug(
						// ">>>>>>>>>>>>>>>>>>>>>>>>系统正在为数据校验协助者数据，
						// 判断协助者组织信息是否存在，如果不存在，则置空 :"+
						// cacheImportRowDetail.getCooperateIdentity() );
						if (cacheImportRowDetail.getCooperateIdentity() != null
								&& !cacheImportRowDetail.getCooperateIdentity().isEmpty()) {
							userName = "";
							identity = "";
							organizationName = "";
							companyName = "";
							identityNames = cacheImportRowDetail.getCooperateIdentity().split(",");
							try {
								for (String _identity : identityNames) {
									if (okrUserManagerService.getUserNameByIdentity(_identity) == null) {
										throw new Exception("person not exsits, identity:" + _identity);
									}
									if (identity == null || identity.isEmpty()) {
										identity += _identity;
									} else {
										identity += "," + _identity;
									}
									if (userName == null || userName.isEmpty()) {
										userName = okrUserManagerService.getUserNameByIdentity(_identity).getName();
									} else {
										userName += ","
												+ okrUserManagerService.getUserNameByIdentity(_identity).getName();
									}
									if (organizationName == null || organizationName.isEmpty()) {
										organizationName = okrUserManagerService.getDepartmentNameByIdentity(_identity);
									} else {
										organizationName += ","
												+ okrUserManagerService.getDepartmentNameByIdentity(_identity);
									}
									if (companyName == null || companyName.isEmpty()) {
										companyName = okrUserManagerService.getCompanyNameByIdentity(_identity);
									} else {
										companyName += "," + okrUserManagerService.getCompanyNameByIdentity(_identity);
									}
								}
								wrapInOkrWorkBaseInfo.setCooperateEmployeeName(userName);
								wrapInOkrWorkBaseInfo.setCooperateIdentity(identity);
								wrapInOkrWorkBaseInfo.setCooperateOrganizationName(organizationName);
								wrapInOkrWorkBaseInfo.setCooperateCompanyName(companyName);
							} catch (Exception e) {
								check = false;
								result.error(e);
								result.setUserMessage("系统校验工作协助人发生异常，" + userName + "!");
								logger.error("system query organization for Cooperate got an exception.", e);
								wrapInOkrWorkBaseInfo.setCheckSuccess("failture");
								wrapInOkrWorkBaseInfo.setDescription("系统校验工作协助人发生异常，" + userName + "!");
							}
						} else {
							wrapInOkrWorkBaseInfo.setCooperateIdentity(null);
							wrapInOkrWorkBaseInfo.setCooperateEmployeeName(null);
							wrapInOkrWorkBaseInfo.setCooperateOrganizationName(null);
							wrapInOkrWorkBaseInfo.setCooperateCompanyName(null);
						}
					}

					if (check) {
						// logger.debug(
						// ">>>>>>>>>>>>>>>>>>>>>>>>系统正在为数据校验协助者数据，
						// 判断协助者组织信息是否存在，如果不存在，则置空
						// :"+cacheImportRowDetail.getReadLeaderIdentity() );
						if (cacheImportRowDetail.getReadLeaderIdentity() != null
								&& !cacheImportRowDetail.getReadLeaderIdentity().isEmpty()) {
							userName = "";
							identity = "";
							organizationName = "";
							companyName = "";
							identityNames = cacheImportRowDetail.getReadLeaderIdentity().split(",");
							try {
								for (String _identity : identityNames) {
									if (okrUserManagerService.getUserNameByIdentity(_identity) == null) {
										throw new Exception("person not exsits, identity:" + _identity);
									}
									if (identity == null || identity.isEmpty()) {
										identity += _identity;
									} else {
										identity += "," + _identity;
									}
									if (userName == null || userName.isEmpty()) {
										userName = okrUserManagerService.getUserNameByIdentity(_identity).getName();
									} else {
										userName += ","
												+ okrUserManagerService.getUserNameByIdentity(_identity).getName();
									}
									if (organizationName == null || organizationName.isEmpty()) {
										organizationName = okrUserManagerService.getDepartmentNameByIdentity(_identity);
									} else {
										organizationName += ","
												+ okrUserManagerService.getDepartmentNameByIdentity(_identity);
									}
									if (companyName == null || companyName.isEmpty()) {
										companyName = okrUserManagerService.getCompanyNameByIdentity(_identity);
									} else {
										companyName += "," + okrUserManagerService.getCompanyNameByIdentity(_identity);
									}
								}
								wrapInOkrWorkBaseInfo.setReadLeaderName(userName);
								wrapInOkrWorkBaseInfo.setReadLeaderIdentity(identity);
								wrapInOkrWorkBaseInfo.setReadLeaderOrganizationName(organizationName);
								wrapInOkrWorkBaseInfo.setReadLeaderCompanyName(companyName);
							} catch (Exception e) {
								check = false;
								result.error(e);
								result.setUserMessage("系统校验工作阅知领导信息发生异常，" + identityNames + "!");
								logger.error("system query organization for ReadLeader got an exception.", e);
								wrapInOkrWorkBaseInfo.setCheckSuccess("failture");
								wrapInOkrWorkBaseInfo.setDescription("系统校验工作阅知领导信息发生异常，" + identityNames + "!");
							}
						} else {
							wrapInOkrWorkBaseInfo.setReadLeaderIdentity(null);
							wrapInOkrWorkBaseInfo.setReadLeaderName(null);
							wrapInOkrWorkBaseInfo.setReadLeaderOrganizationName(null);
							wrapInOkrWorkBaseInfo.setReadLeaderCompanyName(null);
						}
					}

					if (check) {
						// logger.debug(
						// ">>>>>>>>>>>>>>>>>>>>>>>>系统正在为数据校验汇报周期和汇报日期数据，并且补充汇报时间和时间序列:"+cacheImportRowDetail.getReportCycle()
						// );
						// 校验汇报周期和汇报日期数据，并且补充汇报时间和时间序列
						if (cacheImportRowDetail.getReportCycle() != null
								&& cacheImportRowDetail.getReportCycle().trim().equals("不汇报")) {
							wrapInOkrWorkBaseInfo.setIsNeedReport(false);
							wrapInOkrWorkBaseInfo.setReportDayInCycle(null);
							wrapInOkrWorkBaseInfo.setReportTimeQue(null);
							wrapInOkrWorkBaseInfo.setLastReportTime(null);
							wrapInOkrWorkBaseInfo.setNextReportTime(null);
						} else if (cacheImportRowDetail.getReportCycle() != null
								&& cacheImportRowDetail.getReportCycle().trim().equals("每周汇报")) {
							if (wrapInOkrWorkBaseInfo.getReportDayInCycle() != null) {
								wrapInOkrWorkBaseInfo.setIsNeedReport(true);
								// 检验每周汇报日的选择是否正确
								if (wrapInOkrWorkBaseInfo.getReportDayInCycle() >= 1
										&& wrapInOkrWorkBaseInfo.getReportDayInCycle() <= 7) {
									// 每周1-7
									String reportTimeQue = okrWorkBaseInfoService.getReportTimeQue(
											dateOperation.getDateFromString(wrapInOkrWorkBaseInfo.getDeployDateStr()),
											wrapInOkrWorkBaseInfo.getCompleteDateLimit(),
											wrapInOkrWorkBaseInfo.getReportCycle(),
											wrapInOkrWorkBaseInfo.getReportDayInCycle(), reportStartTime);
									Date nextReportTime = okrWorkBaseInfoService.getNextReportTime(reportTimeQue,
											wrapInOkrWorkBaseInfo.getLastReportTime());
									wrapInOkrWorkBaseInfo.setReportTimeQue(reportTimeQue);
									wrapInOkrWorkBaseInfo.setNextReportTime(nextReportTime);
								} else {
									check = false;
									result.setUserMessage("每周汇报日选择不正确：" + wrapInOkrWorkBaseInfo.getReportDayInCycle()
											+ "，无法继续保存工作信息!");
									logger.error("ReportDayInCycle is not valid.");
									wrapInOkrWorkBaseInfo.setCheckSuccess("failture");
									wrapInOkrWorkBaseInfo.setDescription("每周汇报日选择不正确："
											+ wrapInOkrWorkBaseInfo.getReportDayInCycle() + "，无法继续保存工作信息!");
								}
							} else {
								check = false;
								result.setUserMessage("每周汇报日为空，无法继续保存工作信息!");
								logger.error("ReportDayInCycle is null.");
								wrapInOkrWorkBaseInfo.setCheckSuccess("failture");
								wrapInOkrWorkBaseInfo.setDescription("每周汇报日为空，无法继续保存工作信息!");
							}
						} else if (cacheImportRowDetail.getReportCycle() != null
								&& cacheImportRowDetail.getReportCycle().trim().equals("每月汇报")) {
							if (cacheImportRowDetail.getReportDayInCycle() != null) {
								wrapInOkrWorkBaseInfo.setIsNeedReport(true);
								if (wrapInOkrWorkBaseInfo.getReportDayInCycle() >= 1
										&& wrapInOkrWorkBaseInfo.getReportDayInCycle() <= 31) {
									// 每月1-31，如果选择的日期大于当月最大日期，那么默认定为当月最后一天
									String reportTimeQue = okrWorkBaseInfoService.getReportTimeQue(
											dateOperation.getDateFromString(wrapInOkrWorkBaseInfo.getDeployDateStr()),
											wrapInOkrWorkBaseInfo.getCompleteDateLimit(),
											wrapInOkrWorkBaseInfo.getReportCycle(),
											wrapInOkrWorkBaseInfo.getReportDayInCycle(), reportStartTime);
									Date nextReportTime = okrWorkBaseInfoService.getNextReportTime(reportTimeQue,
											wrapInOkrWorkBaseInfo.getLastReportTime());
									wrapInOkrWorkBaseInfo.setReportTimeQue(reportTimeQue);
									wrapInOkrWorkBaseInfo.setNextReportTime(nextReportTime);
								} else {
									check = false;
									result.setUserMessage("每月汇报日选择不正确：" + wrapInOkrWorkBaseInfo.getReportDayInCycle()
											+ "，无法继续保存工作信息!");
									logger.error("ReportDayInCycle is null.");
									wrapInOkrWorkBaseInfo.setCheckSuccess("failture");
									wrapInOkrWorkBaseInfo.setDescription("每月汇报日选择不正确："
											+ wrapInOkrWorkBaseInfo.getReportDayInCycle() + "，无法继续保存工作信息!");
								}
							} else {
								check = false;
								result.setUserMessage("每月汇报日期为空，无法继续保存工作信息!");
								logger.error("ReportDayInCycle is null.");
								wrapInOkrWorkBaseInfo.setCheckSuccess("failture");
								wrapInOkrWorkBaseInfo.setDescription("每月汇报日期为空，无法继续保存工作信息!");
							}
						} else {
							check = false;
							result.setUserMessage(
									"汇报周期选择不正确：" + cacheImportRowDetail.getReportCycle() + "，无法继续保存工作信息!");
							logger.error("cacheImportRowDetail.getReportCycle() is not valid.");
							wrapInOkrWorkBaseInfo.setCheckSuccess("failture");
							wrapInOkrWorkBaseInfo.setDescription(
									"汇报周期选择不正确：" + cacheImportRowDetail.getReportCycle() + "，无法继续保存工作信息!");
						}
					}
					wrapInList.add(wrapInOkrWorkBaseInfo);
				} catch (Exception e) {
					check = false;
					result.error(e);
					result.setUserMessage("系统在校验所有待保存数据信息时发生未知异常!");
					logger.error("system check object get an exception", e);
					wrapInOkrWorkBaseInfo.setCheckSuccess("failture");
					wrapInOkrWorkBaseInfo.setDescription("系统在校验所有待保存数据信息时发生未知异常!");
				}
			}

			if (!"success".equals(wrapInOkrWorkBaseInfo.getCheckSuccess())) {
				result.setUserMessage(wrapInOkrWorkBaseInfo.getDescription());
				errorWrapInList.add(wrapInOkrWorkBaseInfo);
			}
		}

		if (errorWrapInList != null && errorWrapInList.size() > 0) {
			logger.error(">>>>>>>>>>>>>>>>>>>>>>>>系统数据校验发现不合法数据，需要进一步修改......");
			result.error(new Exception("系统数据校验发现不合法数据，需要进一步修改。"));
			result.setData(errorWrapInList);
		} else {
			logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>系统数据校验完成准备保存各项工作信息......");
			if (check) {
				if (wrapInList != null && !wrapInList.isEmpty()) {
					for (WrapInOkrWorkBaseInfo wrapIn : wrapInList) {
						logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>系统准备保存工作信息:" + wrapIn.getTitle());
						// 创建新的工作信息，保存到数据库
						try {
							okrWorkBaseInfo = okrWorkBaseInfoService.save(wrapIn);
							result.setUserMessage("系统成功导入所有工作信息!");
						} catch (Exception e) {
							check = false;
							result.error(e);
							result.setUserMessage("系统在保存数据信息时发生未知异常!");
							logger.error("system save object get an exception", e);
						}
					}
				}
			}
		}
		return result;
	}
}