package com.x.okr.assemble.control.servlet.workimport;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.x.base.core.application.servlet.AbstractServletAction;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.common.date.DateOperation;
import com.x.okr.assemble.common.excel.reader.ExcelReaderUtil;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.ThisApplication;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.WrapInOkrWorkBaseInfo;
import com.x.okr.assemble.control.service.OkrCenterWorkQueryService;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrUserManagerService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoOperationService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoQueryService;
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
public class WorkImportServlet extends AbstractServletAction {

	private static final long serialVersionUID = 5628571943877405247L;
	private Logger logger = LoggerFactory.getLogger( WorkImportServlet.class );
	private OkrCenterWorkQueryService okrCenterWorkInfoService = new OkrCenterWorkQueryService();
	private OkrWorkBaseInfoOperationService okrWorkBaseInfoOperationService = new OkrWorkBaseInfoOperationService();
	private OkrWorkBaseInfoQueryService okrWorkBaseInfoQueryService = new OkrWorkBaseInfoQueryService();
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
				effectivePerson = this.effectivePerson(request);
			} catch (Exception e) {
				check = false;
				result.error(e);
				logger.warn("system get effectivePerson from request url got an exception." ); 
				logger.error(e);
			}
		}
		
		if (check) {
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getName() );
			}catch(Exception e){
				check = false;
				Exception exception = new GetOkrUserCacheException( e, effectivePerson.getName() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
			
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new UserNoLoginException( effectivePerson.getName() );
			result.error( exception );
			//logger.error( e, effectivePerson, request, null);
		}
		
		if (check) {
			if ( okrUserCache.getLoginUserName() == null ) {
				check = false;
				Exception exception = new UserNoLoginException( effectivePerson.getName() );
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		}

		if (check) {
			if (!ServletFileUpload.isMultipartContent(request)) {
				check = false;
				logger.warn("not mulit part request." ); 
				result.error( new Exception( "请求不是Multipart，无法获取文件信息。" ) );
			}
		}

		// 从URL里获取centerId
		if (check) {
			try {
				centerId = this.getURIPart(request.getRequestURI(), "center");
			} catch (Exception e) {
				check = false;
				result.error(e);
				logger.warn("system get centerId from request url got an exception." );
				logger.error(e);
			}
		}

		// 判断中心工作信息是否存在
		if (check) {
			try {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					okrCenterWorkInfo = emc.find(centerId, OkrCenterWorkInfo.class);
					if (null == okrCenterWorkInfo) {// 中心工作不存在
						check = false;
						Exception exception = new CenterWorkNotExistsException( centerId );
						result.error( exception );
						//logger.error( e, effectivePerson, request, null);
					}
				}
			} catch (Exception e) {// 获取中心工作发生异常
				check = false;
				Exception exception = new CenterWorkQueryByIdException( e, centerId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		// 获取文件内容并且对文件进行分析
		if (check) {
			try {
				upload = new ServletFileUpload();
				fileItemIterator = upload.getItemIterator(request);
				while (fileItemIterator.hasNext()) {
					item = fileItemIterator.next();
					if (item != null && item.getName() != null && !item.getName().isEmpty()) {
						input = item.openStream();
						// 读取EXCEL文件中的所有数据
						ThisApplication.getImportFileStatusMap().remove(effectivePerson.getName());
						ExcelReaderUtil.readExcel(new WorkImportExcelReader(), item.getName(), input, effectivePerson.getName(), 1);
					}
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExcelReadException( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			} finally {
				if (input != null) {
					input.close();
				}
			}
		}

		if (check) {
			List<CacheImportRowDetail> importRowList = null;
			// 对从EXCEL文件里获取的所有信息逐一进行数据校验以及保存操作
			CacheImportFileStatus cacheImportFileStatus = null;
			cacheImportFileStatus = ThisApplication.getCacheImportFileStatusElementByKey(effectivePerson.getName());
			if (cacheImportFileStatus != null) {
				importRowList = cacheImportFileStatus.getDetailList();
				if (importRowList != null) {
					result = saveAllImportRow( request, centerId, importRowList, effectivePerson);
				}
			}
		}
		ThisApplication.getImportFileStatusMap().remove(effectivePerson.getName());
		this.result(response, result);
	}

	/**
	 * 检验所有需要保存的工作信息数据、补全数据，并且保存信息
	 * 
	 * @param importRowList
	 * @return
	 */
	private ActionResult<Object> saveAllImportRow( HttpServletRequest request, String centerId, List<CacheImportRowDetail> importRowList, EffectivePerson effectivePerson) {
		ActionResult<Object> result = new ActionResult<Object>();
		if ( importRowList == null || importRowList.isEmpty() ) {
			Exception exception = new NoDataException();
			result.error( exception );
			//logger.error( e, effectivePerson, request, null);
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
		}catch(Exception e){
			check = false;
			Exception exception = new GetOkrUserCacheException( e, effectivePerson.getName() );
			result.error( exception );
			//logger.error( e, effectivePerson, request, null);
		}		
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new UserNoLoginException( effectivePerson.getName() );
			result.error( exception );
			//logger.error( e, effectivePerson, request, null);
		}

		if (check) {
			// 补充中心工作标题
			if (centerId != null && !centerId.isEmpty()) {
				// 根据ID查询中心工作信息
				try {
					okrCenterWorkInfo = okrCenterWorkInfoService.get(centerId);
				} catch (Exception e) {
					check = false;
					Exception exception = new CenterWorkQueryByIdException( e, centerId );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}

		if (check) {
			if (okrCenterWorkInfo == null) {
				check = false;
				Exception exception = new CenterWorkNotExistsException( centerId );
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		}

		if (check) {
			try {
				currentUserIdentityName = okrUserManagerService.getFistIdentityNameByPerson(effectivePerson.getName());
			} catch (Exception e) {
				check = false;
				Exception exception = new UserOrganizationQueryException( e, effectivePerson.getName() );
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				currentUserOrganizationName = okrUserManagerService.getDepartmentNameByIdentity(currentUserIdentityName);
			} catch (Exception e) {
				check = false;
				Exception exception = new UserOrganizationQueryException( e, currentUserIdentityName );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				currentUserCompanyName = okrUserManagerService.getCompanyNameByIdentity(currentUserIdentityName);
			} catch (Exception e) {
				check = false;
				Exception exception = new UserOrganizationQueryException( e, currentUserIdentityName );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		for (CacheImportRowDetail cacheImportRowDetail : importRowList) {
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
						Exception exception = new UserNoLoginException( effectivePerson.getName() );
						result.error( exception );
						//logger.error( e, effectivePerson, request, null);
					}

					if (check) {
						wrapInOkrWorkBaseInfo.setCenterId(centerId);
						wrapInOkrWorkBaseInfo.setCenterTitle(okrCenterWorkInfo.getTitle());
					}

					if (check) {
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
						wrapInOkrWorkBaseInfo.setDeployerName(okrUserCache.getLoginUserName());
						if (effectivePerson.getName().equals(okrUserCache.getLoginUserName())) {
							wrapInOkrWorkBaseInfo.setDeployerOrganizationName(okrUserCache.getLoginUserOrganizationName());
							wrapInOkrWorkBaseInfo.setDeployerCompanyName(okrUserCache.getLoginUserCompanyName());
							wrapInOkrWorkBaseInfo.setDeployerIdentity(okrUserCache.getLoginIdentityName() );
						}
					}

					// 补充部署工作的年份和月份
					if (check) {
						wrapInOkrWorkBaseInfo.setDeployYear(yearString);
						wrapInOkrWorkBaseInfo.setDeployMonth(monthString);
						wrapInOkrWorkBaseInfo.setDeployDateStr(nowDateString);
					}

					if (check) {
						// cacheImportRowDetail.getParentWorkId());
						// 检验上级工作信息
						if (cacheImportRowDetail.getParentWorkId() != null
								&& !cacheImportRowDetail.getParentWorkId().isEmpty()) {
							// 根据ID查询中心工作信息
							okrWorkBaseInfo = okrWorkBaseInfoQueryService.get(cacheImportRowDetail.getParentWorkId());
							if (okrWorkBaseInfo != null) {
								wrapInOkrWorkBaseInfo.setParentWorkId(okrWorkBaseInfo.getId());
								wrapInOkrWorkBaseInfo.setParentWorkTitle(okrWorkBaseInfo.getTitle());
							} else {
								check = false;
								Exception exception = new WorkNotExistsException( cacheImportRowDetail.getParentWorkId() );
								result.error( exception );
								//logger.error( e, effectivePerson, request, null);
								wrapInOkrWorkBaseInfo.setCheckSuccess("failture");
								wrapInOkrWorkBaseInfo.setDescription("上级工作不存在, id:'" + cacheImportRowDetail.getParentWorkId() + "'，无法继续保存工作信息!");
							}
						}
					}

					// 校验工作完成时限数据，补充日期型完成时限数据
					if (check) {
						if (cacheImportRowDetail.getCompleteDateLimitStr() != null
								&& !cacheImportRowDetail.getCompleteDateLimitStr().isEmpty()) {
							try {
								wrapInOkrWorkBaseInfo.setCompleteDateLimit(dateOperation
										.getDateFromString(cacheImportRowDetail.getCompleteDateLimitStr()));
							} catch (Exception e) {
								check = false;
								Exception exception = new WorkCompleteDateLimitFormatException( e, cacheImportRowDetail.getCompleteDateLimitStr() );
								result.error( exception );
								logger.error( e, effectivePerson, request, null);
								wrapInOkrWorkBaseInfo.setCheckSuccess("failture");
								wrapInOkrWorkBaseInfo.setDescription("工作完成时限格式不正确：" + cacheImportRowDetail.getCompleteDateLimitStr() + "，无法继续保存工作信息!");
							}
						} else {
							check = false;
							Exception exception = new WorkCompleteDateLimitEmptyException();
							result.error( exception );
							//logger.error( e, effectivePerson, request, null);
							wrapInOkrWorkBaseInfo.setCheckSuccess("failture");
							wrapInOkrWorkBaseInfo.setDescription("工作完成时限信息为空，无法继续保存工作信息!");
						}
					}

					// 校验责任者数据， 判断责任者组织信息是否存在，如果不存在，则需要补充部门者组织信息
					if (check) {
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
										userName = okrUserManagerService.getUserNameByIdentity(_identity);
									} else {
										userName += ","
												+ okrUserManagerService.getUserNameByIdentity(_identity);
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
								Exception exception = new WorkResponsibilityInvalidException( e, cacheImportRowDetail.getResponsibilityIdentity() );
								result.error( exception );
								logger.error( e, effectivePerson, request, null);
								wrapInOkrWorkBaseInfo.setCheckSuccess("failture");
								wrapInOkrWorkBaseInfo.setDescription("系统校验工作责任人发生异常，" + userName + "!");
							}
						} else {
							check = false;
							Exception exception = new WorkResponsibilityEmptyException();
							result.error( exception );
							//logger.error( e, effectivePerson, request, null);
							wrapInOkrWorkBaseInfo.setCheckSuccess("failture");
							wrapInOkrWorkBaseInfo.setDescription("责任者[responsibilityEmployeeName]信息为空，无法继续保存工作信息!");
						}
					}

					if (check) {
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
										userName = okrUserManagerService.getUserNameByIdentity(_identity);
									} else {
										userName += ","
												+ okrUserManagerService.getUserNameByIdentity(_identity);
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
								Exception exception = new WorkCooperateInvalidException( e, cacheImportRowDetail.getCooperateIdentity() );
								result.error( exception );
								logger.error( e, effectivePerson, request, null);
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
										userName = okrUserManagerService.getUserNameByIdentity(_identity);
									} else {
										userName += ","
												+ okrUserManagerService.getUserNameByIdentity(_identity);
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
								Exception exception = new WorkReadLeaderInvalidException( e, cacheImportRowDetail.getReadLeaderIdentity() );
								result.error( exception );
								logger.error( e, effectivePerson, request, null);
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
						// 校验汇报周期和汇报日期数据，并且补充汇报时间和时间序列
						if (cacheImportRowDetail.getReportCycle() != null && cacheImportRowDetail.getReportCycle().trim().equals("不汇报")) {
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
									String reportTimeQue = okrWorkBaseInfoQueryService.getReportTimeQue(
											dateOperation.getDateFromString(wrapInOkrWorkBaseInfo.getDeployDateStr()),
											wrapInOkrWorkBaseInfo.getCompleteDateLimit(),
											wrapInOkrWorkBaseInfo.getReportCycle(),
											wrapInOkrWorkBaseInfo.getReportDayInCycle(), reportStartTime);
									Date nextReportTime = okrWorkBaseInfoQueryService.getNextReportTime(reportTimeQue,
											wrapInOkrWorkBaseInfo.getLastReportTime());
									wrapInOkrWorkBaseInfo.setReportTimeQue(reportTimeQue);
									wrapInOkrWorkBaseInfo.setNextReportTime(nextReportTime);
								} else {
									check = false;
									Exception exception = new ReportDayInCycleInvalidException( wrapInOkrWorkBaseInfo.getReportDayInCycle() );
									result.error( exception );
									//logger.error( e, effectivePerson, request, null);
									wrapInOkrWorkBaseInfo.setCheckSuccess("failture");
									wrapInOkrWorkBaseInfo.setDescription("每周汇报日选择不正确：" + wrapInOkrWorkBaseInfo.getReportDayInCycle() + "，无法继续保存工作信息!");
								}
							} else {
								check = false;
								Exception exception = new ReportDayInCycleEmptyException();
								result.error( exception );
								//logger.error( e, effectivePerson, request, null);
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
									String reportTimeQue = okrWorkBaseInfoQueryService.getReportTimeQue(
											dateOperation.getDateFromString(wrapInOkrWorkBaseInfo.getDeployDateStr()),
											wrapInOkrWorkBaseInfo.getCompleteDateLimit(),
											wrapInOkrWorkBaseInfo.getReportCycle(),
											wrapInOkrWorkBaseInfo.getReportDayInCycle(), reportStartTime);
									Date nextReportTime = okrWorkBaseInfoQueryService.getNextReportTime(reportTimeQue,
											wrapInOkrWorkBaseInfo.getLastReportTime());
									wrapInOkrWorkBaseInfo.setReportTimeQue(reportTimeQue);
									wrapInOkrWorkBaseInfo.setNextReportTime(nextReportTime);
								} else {
									check = false;
									Exception exception = new ReportDayInCycleInvalidException( wrapInOkrWorkBaseInfo.getReportDayInCycle() );
									result.error( exception );
									//logger.error( e, effectivePerson, request, null);
									wrapInOkrWorkBaseInfo.setCheckSuccess("failture");
									wrapInOkrWorkBaseInfo.setDescription("每月汇报日选择不正确：" + wrapInOkrWorkBaseInfo.getReportDayInCycle() + "，无法继续保存工作信息!");
								}
							} else {
								check = false;
								Exception exception = new ReportDayInCycleEmptyException();
								result.error( exception );
								//logger.error( e, effectivePerson, request, null);
								wrapInOkrWorkBaseInfo.setCheckSuccess("failture");
								wrapInOkrWorkBaseInfo.setDescription("每月汇报日期为空，无法继续保存工作信息!");
							}
						} else {
							check = false;
							Exception exception = new ReportCycleInvalidException( cacheImportRowDetail.getReportCycle() );
							result.error( exception );
							//logger.error( e, effectivePerson, request, null);
							wrapInOkrWorkBaseInfo.setCheckSuccess("failture");
							wrapInOkrWorkBaseInfo.setDescription( "汇报周期选择不正确：" + cacheImportRowDetail.getReportCycle() + "，无法继续保存工作信息!");
						}
					}
					wrapInList.add(wrapInOkrWorkBaseInfo);
				} catch (Exception e) {
					check = false;
					Exception exception = new WorkImportDataCheckException( e );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
					wrapInOkrWorkBaseInfo.setCheckSuccess("failture");
					wrapInOkrWorkBaseInfo.setDescription("系统在校验所有待保存数据信息时发生未知异常!");
				}
			}

			if (!"success".equals( wrapInOkrWorkBaseInfo.getCheckSuccess()) ) {
				errorWrapInList.add(wrapInOkrWorkBaseInfo);
			}
		}

		if (errorWrapInList != null && errorWrapInList.size() > 0) {
			result.error(new Exception("系统数据校验发现不合法数据，需要进一步修改。"));
			result.setData(errorWrapInList);
		} else {
			if (check) {
				if (wrapInList != null && !wrapInList.isEmpty()) {
					for (WrapInOkrWorkBaseInfo wrapIn : wrapInList) {
						// 创建新的工作信息，保存到数据库
						try {
							okrWorkBaseInfo = okrWorkBaseInfoOperationService.save(wrapIn);
						} catch (Exception e) {
							check = false;
							Exception exception = new WorkImportDataException( e );
							result.error( exception );
							logger.error( e, effectivePerson, request, null);
						}
					}
				}
			}
		}
		return result;
	}
}