package com.x.okr.assemble.control.jaxrs.workimport;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.okr.assemble.common.excel.reader.ExcelReaderUtil;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.ThisApplication;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.ExceptionEmptyExtension;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.URLParameterGetException;
import com.x.okr.assemble.control.jaxrs.workimport.exception.CenterWorkNotExistsException;
import com.x.okr.assemble.control.jaxrs.workimport.exception.CenterWorkQueryByIdException;
import com.x.okr.assemble.control.jaxrs.workimport.exception.ExcelReadException;
import com.x.okr.assemble.control.jaxrs.workimport.exception.GetOkrUserCacheException;
import com.x.okr.assemble.control.jaxrs.workimport.exception.NoDataException;
import com.x.okr.assemble.control.jaxrs.workimport.exception.ReportCycleInvalidException;
import com.x.okr.assemble.control.jaxrs.workimport.exception.ReportDayInCycleEmptyException;
import com.x.okr.assemble.control.jaxrs.workimport.exception.ReportDayInCycleInvalidException;
import com.x.okr.assemble.control.jaxrs.workimport.exception.UserNoLoginException;
import com.x.okr.assemble.control.jaxrs.workimport.exception.UserUnitQueryException;
import com.x.okr.assemble.control.jaxrs.workimport.exception.WorkCompleteDateLimitEmptyException;
import com.x.okr.assemble.control.jaxrs.workimport.exception.WorkCompleteDateLimitFormatException;
import com.x.okr.assemble.control.jaxrs.workimport.exception.WorkCooperateInvalidException;
import com.x.okr.assemble.control.jaxrs.workimport.exception.WorkImportDataCheckException;
import com.x.okr.assemble.control.jaxrs.workimport.exception.WorkImportDataException;
import com.x.okr.assemble.control.jaxrs.workimport.exception.WorkNotExistsException;
import com.x.okr.assemble.control.jaxrs.workimport.exception.WorkReadLeaderInvalidException;
import com.x.okr.assemble.control.jaxrs.workimport.exception.WorkResponsibilityEmptyException;
import com.x.okr.assemble.control.jaxrs.workimport.exception.WorkResponsibilityInvalidException;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkBaseInfo;

public class ActionWorkImport extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(ActionWorkImport.class);

	protected ActionResult<Object> execute( HttpServletRequest request, EffectivePerson effectivePerson, 
			String centerId, String site, byte[] bytes, FormDataContentDisposition disposition) {
		ActionResult<Object> result = new ActionResult<>();
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		OkrUserCache okrUserCache = null;
		String fileName = null;
		Boolean check = true;	
		
		if( check ){
			if( StringUtils.isEmpty(centerId) ){
				check = false;
				Exception exception = new URLParameterGetException( new Exception("未获取到中心工作ID") );
				result.error( exception );
			}
		}
		
		if (check) {
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName(effectivePerson.getDistinguishedName());
			} catch (Exception e) {
				check = false;
				Exception exception = new GetOkrUserCacheException(e, effectivePerson.getDistinguishedName());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check && (okrUserCache == null || okrUserCache.getLoginIdentityName() == null)) {
			check = false;
			Exception exception = new UserNoLoginException(effectivePerson.getDistinguishedName());
			result.error(exception);
		}
		
		if (check) {
			if (okrUserCache.getLoginUserName() == null) {
				check = false;
				Exception exception = new UserNoLoginException(effectivePerson.getDistinguishedName());
				result.error(exception);
			}
		}
		
		// 判断中心工作信息是否存在
		if (check) {
			try {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					okrCenterWorkInfo = emc.find(centerId, OkrCenterWorkInfo.class);
					if (null == okrCenterWorkInfo) {// 中心工作不存在
						check = false;
						Exception exception = new CenterWorkNotExistsException(centerId);
						result.error(exception);
						// logger.error( e, effectivePerson, request, null);
					}
				}
			} catch (Exception e) {// 获取中心工作发生异常
				check = false;
				Exception exception = new CenterWorkQueryByIdException(e, centerId);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				fileName = FilenameUtils.getName(new String(disposition.getFileName().getBytes(DefaultCharset.name_iso_8859_1), DefaultCharset.name));
				/** 禁止不带扩展名的文件上传 */
				if (StringUtils.isEmpty(fileName)) {
					check = false;
					Exception exception = new ExceptionEmptyExtension( fileName );
					result.error( exception );
				} 
			} catch (Exception e) {
				check = false;
				result.error( e );
			}
		}
		
		if( check ){
			// 读取EXCEL文件中的所有数据
			ThisApplication.getImportFileStatusMap().remove(effectivePerson.getDistinguishedName());
			InputStream input = new ByteArrayInputStream(bytes); 
			try {
				ExcelReaderUtil.readExcel( new WorkImportExcelReader(), fileName, input, effectivePerson.getDistinguishedName(), 1);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExcelReadException(e);
				result.error(exception);
			}
		}
		
		if (check) {
			List<CacheImportRowDetail> importRowList = null;
			// 对从EXCEL文件里获取的所有信息逐一进行数据校验以及保存操作
			CacheImportFileStatus cacheImportFileStatus = null;
			cacheImportFileStatus = ThisApplication.getCacheImportFileStatusElementByKey(effectivePerson.getDistinguishedName());
			if (cacheImportFileStatus != null) {
				importRowList = cacheImportFileStatus.getDetailList();
				if (importRowList != null) {
					result = saveAllImportRow(request, centerId, importRowList, effectivePerson);
				}
			}
		}
		ThisApplication.getImportFileStatusMap().remove(effectivePerson.getDistinguishedName());
		return result;
	}

	/**
	 * 检验所有需要保存的工作信息数据、补全数据，并且保存信息
	 * 
	 * @param importRowList
	 * @return
	 */
	private ActionResult<Object> saveAllImportRow(HttpServletRequest request, String centerId,
			List<CacheImportRowDetail> importRowList, EffectivePerson effectivePerson) {
		ActionResult<Object> result = new ActionResult<Object>();
		if (importRowList == null || importRowList.isEmpty()) {
			Exception exception = new NoDataException();
			result.error(exception);
			// logger.error( e, effectivePerson, request, null);
			return result;
		}
		List<Wi> wrapInList = new ArrayList<Wi>();
		List<Wi> errorWrapInList = new ArrayList<Wi>();
		Wi wrapInOkrWorkBaseInfo = null;
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		// 这里需要去配置表里查询配置的汇报发起具体的时间点，如：10:00:00
		String reportStartTime = "10:00:00";
		String[] identityNames = null;
		String userName = null, unitName = null, topUnitName = null, identity = null;
		String currentUserIdentityName = null;
		String currentUserUnitName = null;
		String currentUserTopUnitName = null;
		String yearString = dateOperation.getYear(new Date()), monthString = dateOperation.getMonth(new Date()), nowDateString = dateOperation.getNowDateTime();
		Boolean check = true;
		OkrUserCache okrUserCache = null;
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName(effectivePerson.getDistinguishedName());
		} catch (Exception e) {
			check = false;
			Exception exception = new GetOkrUserCacheException(e, effectivePerson.getDistinguishedName());
			result.error(exception);
			// logger.error( e, effectivePerson, request, null);
		}

		if( check ){
			try{
				reportStartTime = okrConfigSystemService.getValueWithConfigCode( "REPORT_CREATETIME" );
				if( reportStartTime == null || reportStartTime.isEmpty() ){
					reportStartTime = "10:00:00";
				}
			}catch(Exception e){
				reportStartTime = "10:00:00";
			}
		}
		
		if (check && (okrUserCache == null || okrUserCache.getLoginIdentityName() == null)) {
			check = false;
			Exception exception = new UserNoLoginException(effectivePerson.getDistinguishedName());
			result.error(exception);
			// logger.error( e, effectivePerson, request, null);
		}

		if (check) {
			// 补充中心工作标题
			if (centerId != null && !centerId.isEmpty()) {
				// 根据ID查询中心工作信息
				try {
					okrCenterWorkInfo = okrCenterWorkInfoService.get(centerId);
				} catch (Exception e) {
					check = false;
					Exception exception = new CenterWorkQueryByIdException(e, centerId);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}

		if (check) {
			if (okrCenterWorkInfo == null) {
				check = false;
				Exception exception = new CenterWorkNotExistsException(centerId);
				result.error(exception);
				// logger.error( e, effectivePerson, request, null);
			}
		}

		if (check) {
			try {
				currentUserIdentityName = okrUserManagerService.getIdentityWithPerson(effectivePerson.getDistinguishedName());
			} catch (Exception e) {
				check = false;
				Exception exception = new UserUnitQueryException(e, effectivePerson.getDistinguishedName());
				result.error(exception);
			}
		}
		if (check) {
			try {
				currentUserUnitName = okrUserManagerService.getUnitNameByIdentity(currentUserIdentityName);
			} catch (Exception e) {
				check = false;
				Exception exception = new UserUnitQueryException(e, currentUserIdentityName);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				currentUserTopUnitName = okrUserManagerService.getTopUnitNameByIdentity(currentUserIdentityName);
			} catch (Exception e) {
				check = false;
				Exception exception = new UserUnitQueryException(e, currentUserIdentityName);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		List<String> identities = null;
		for (CacheImportRowDetail cacheImportRowDetail : importRowList) {
			wrapInOkrWorkBaseInfo = new Wi();

			wrapInOkrWorkBaseInfo.setCompleteDateLimit(cacheImportRowDetail.getCompleteDateLimit());
			wrapInOkrWorkBaseInfo.setCompleteDateLimitStr(cacheImportRowDetail.getCompleteDateLimitStr());
			
			identities = new ArrayList<>();
			if(StringUtils.isNotEmpty( cacheImportRowDetail.getCooperateIdentity() )) {
				String[] arr = cacheImportRowDetail.getCooperateIdentity().split( "," );
				for( String _identity : arr ) {
					identities.add( _identity );
				}
			}
			wrapInOkrWorkBaseInfo.setCooperateIdentityList(identities);
			
			wrapInOkrWorkBaseInfo.setCreatorIdentity(currentUserIdentityName);
			wrapInOkrWorkBaseInfo.setDeployerIdentity(okrUserCache.getLoginIdentityName());
			
			wrapInOkrWorkBaseInfo.setDutyDescription(cacheImportRowDetail.getDutyDescription());
			wrapInOkrWorkBaseInfo.setMajorIssuesDescription(cacheImportRowDetail.getMajorIssuesDescription());
			wrapInOkrWorkBaseInfo.setLandmarkDescription(cacheImportRowDetail.getLandmarkDescription());
			wrapInOkrWorkBaseInfo.setParentWorkId(cacheImportRowDetail.getParentWorkId());
			wrapInOkrWorkBaseInfo.setProgressAction(cacheImportRowDetail.getProgressAction());
			wrapInOkrWorkBaseInfo.setProgressPlan(cacheImportRowDetail.getProgressPlan());
			
			identities = new ArrayList<>();
			if(StringUtils.isNotEmpty( cacheImportRowDetail.getReadLeaderIdentity() )) {
				String[] arr = cacheImportRowDetail.getReadLeaderIdentity().split( "," );
				for( String _identity : arr ) {
					identities.add( _identity );
				}
			}
			wrapInOkrWorkBaseInfo.setReadLeaderIdentityList(identities);
			
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
					if (check && okrUserCache.getLoginIdentityName() == null) {
						check = false;
						Exception exception = new UserNoLoginException(effectivePerson.getDistinguishedName());
						result.error(exception);
						// logger.error( e, effectivePerson, request, null);
					}

					if (check) {
						wrapInOkrWorkBaseInfo.setCenterId(centerId);
						wrapInOkrWorkBaseInfo.setCenterTitle(okrCenterWorkInfo.getTitle());
					}

					if (check) {
						// 创建人和部署人信息直接取当前操作人和登录人身份
						wrapInOkrWorkBaseInfo.setCreatorName(effectivePerson.getDistinguishedName());
						if (effectivePerson.getDistinguishedName().equals(okrUserCache.getLoginUserName())) {
							// 如果登录人和代理的身份的姓名是一致的，说明本来就是操作本人身份
							wrapInOkrWorkBaseInfo.setCreatorUnitName(okrUserCache.getLoginUserUnitName());
							wrapInOkrWorkBaseInfo.setCreatorTopUnitName(okrUserCache.getLoginUserTopUnitName());
							wrapInOkrWorkBaseInfo.setCreatorIdentity(okrUserCache.getLoginIdentityName());
						} else {
							// 如果不是操作本人身份，则需要查询创建者的相关身份
							wrapInOkrWorkBaseInfo.setCreatorIdentity(currentUserIdentityName);
							wrapInOkrWorkBaseInfo.setCreatorUnitName(currentUserUnitName);
							wrapInOkrWorkBaseInfo.setCreatorTopUnitName(currentUserTopUnitName);
						}
					}

					if (check) {
						wrapInOkrWorkBaseInfo.setDeployerName(okrUserCache.getLoginUserName());
						if (effectivePerson.getDistinguishedName().equals(okrUserCache.getLoginUserName())) {
							wrapInOkrWorkBaseInfo.setDeployerUnitName(okrUserCache.getLoginUserUnitName());
							wrapInOkrWorkBaseInfo.setDeployerTopUnitName(okrUserCache.getLoginUserTopUnitName());
							wrapInOkrWorkBaseInfo.setDeployerIdentity(okrUserCache.getLoginIdentityName());
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
								Exception exception = new WorkNotExistsException(
										cacheImportRowDetail.getParentWorkId());
								result.error(exception);
								// logger.error( e, effectivePerson, request,
								// null);
								wrapInOkrWorkBaseInfo.setCheckSuccess("failture");
								wrapInOkrWorkBaseInfo.setDescription(
										"上级工作不存在, id:'" + cacheImportRowDetail.getParentWorkId() + "'，无法继续保存工作信息!");
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
								Exception exception = new WorkCompleteDateLimitFormatException(e, cacheImportRowDetail.getCompleteDateLimitStr());
								result.error(exception);
								logger.error(e, effectivePerson, request, null);
								wrapInOkrWorkBaseInfo.setCheckSuccess("failture");
								wrapInOkrWorkBaseInfo.setDescription("工作完成时限格式不正确：" + cacheImportRowDetail.getCompleteDateLimitStr() + "，无法继续保存工作信息!");
							}
						} else {
							check = false;
							Exception exception = new WorkCompleteDateLimitEmptyException();
							result.error(exception);
							// logger.error( e, effectivePerson, request, null);
							wrapInOkrWorkBaseInfo.setCheckSuccess("failture");
							wrapInOkrWorkBaseInfo.setDescription("工作完成时限信息为空，无法继续保存工作信息!");
						}
					}

					// 校验责任者数据， 判断责任者组织信息是否存在，如果不存在，则需要补充组织者组织信息
					if (check) {
						if (cacheImportRowDetail.getResponsibilityIdentity() != null
								&& !cacheImportRowDetail.getResponsibilityIdentity().isEmpty()) {
							userName = "";
							identity = "";
							unitName = "";
							topUnitName = "";
							identityNames = cacheImportRowDetail.getResponsibilityIdentity().split(",");
							try {
								for (String _identity : identityNames) {
									if (okrUserManagerService.getPersonNameByIdentity(_identity) == null) {
										throw new Exception("person not exsits, identity:" + _identity);
									}
									if (identity == null || identity.isEmpty()) {
										identity += _identity;
									} else {
										identity += "," + _identity;
									}
									if (userName == null || userName.isEmpty()) {
										userName = okrUserManagerService.getPersonNameByIdentity(_identity);
									} else {
										userName += "," + okrUserManagerService.getPersonNameByIdentity(_identity);
									}
									if (unitName == null || unitName.isEmpty()) {
										unitName = okrUserManagerService.getUnitNameByIdentity(_identity);
									} else {
										unitName += "," + okrUserManagerService.getUnitNameByIdentity(_identity);
									}
									if (topUnitName == null || topUnitName.isEmpty()) {
										topUnitName = okrUserManagerService.getTopUnitNameByIdentity(_identity);
									} else {
										topUnitName += "," + okrUserManagerService.getTopUnitNameByIdentity(_identity);
									}
								}
								wrapInOkrWorkBaseInfo.setResponsibilityEmployeeName(userName);
								wrapInOkrWorkBaseInfo.setResponsibilityIdentity(identity);
								wrapInOkrWorkBaseInfo.setResponsibilityUnitName(unitName);
								wrapInOkrWorkBaseInfo.setResponsibilityTopUnitName(topUnitName);
							} catch (Exception e) {
								check = false;
								Exception exception = new WorkResponsibilityInvalidException(e,
										cacheImportRowDetail.getResponsibilityIdentity());
								result.error(exception);
								logger.error(e, effectivePerson, request, null);
								wrapInOkrWorkBaseInfo.setCheckSuccess("failture");
								wrapInOkrWorkBaseInfo.setDescription("系统校验工作责任人发生异常，" + userName + "!");
							}
						} else {
							check = false;
							Exception exception = new WorkResponsibilityEmptyException();
							result.error(exception);
							// logger.error( e, effectivePerson, request, null);
							wrapInOkrWorkBaseInfo.setCheckSuccess("failture");
							wrapInOkrWorkBaseInfo.setDescription("责任者[responsibilityEmployeeName]信息为空，无法继续保存工作信息!");
						}
					}

					if (check) {
						if (cacheImportRowDetail.getCooperateIdentity() != null && !cacheImportRowDetail.getCooperateIdentity().isEmpty()) {
							identityNames = cacheImportRowDetail.getCooperateIdentity().split(",");
							List<String> _names = new ArrayList<>();
							List<String> _identities = new ArrayList<>();
							List<String> _unitNames = new ArrayList<>();
							List<String> _topUnitNames = new ArrayList<>();
							try {
								for (String _identity : identityNames) {
									if (okrUserManagerService.getPersonNameByIdentity(_identity) == null) {
										throw new Exception("person not exsits, identity:" + _identity);
									}
									_identities.add( _identity );
									_names.add( okrUserManagerService.getPersonNameByIdentity(_identity) );
									_unitNames.add( okrUserManagerService.getUnitNameByIdentity(_identity) );
									_topUnitNames.add( okrUserManagerService.getTopUnitNameByIdentity(_identity) );
								}
								wrapInOkrWorkBaseInfo.setCooperateEmployeeNameList(_names);
								wrapInOkrWorkBaseInfo.setCooperateIdentityList(_identities);
								wrapInOkrWorkBaseInfo.setCooperateUnitNameList(_unitNames);
								wrapInOkrWorkBaseInfo.setCooperateTopUnitNameList(_topUnitNames);
							} catch (Exception e) {
								check = false;
								Exception exception = new WorkCooperateInvalidException(e,
										cacheImportRowDetail.getCooperateIdentity());
								result.error(exception);
								logger.error(e, effectivePerson, request, null);
								wrapInOkrWorkBaseInfo.setCheckSuccess("failture");
								wrapInOkrWorkBaseInfo.setDescription("系统校验工作协助人发生异常，" + userName + "!");
							}
						} else {
							wrapInOkrWorkBaseInfo.setCooperateIdentityList(new ArrayList<>());
							wrapInOkrWorkBaseInfo.setCooperateEmployeeNameList(new ArrayList<>());
							wrapInOkrWorkBaseInfo.setCooperateUnitNameList(new ArrayList<>());
							wrapInOkrWorkBaseInfo.setCooperateTopUnitNameList(new ArrayList<>());
						}
					}

					if (check) {
						if (cacheImportRowDetail.getReadLeaderIdentity() != null && !cacheImportRowDetail.getReadLeaderIdentity().isEmpty()) {
							userName = "";
							identity = "";
							unitName = "";
							topUnitName = "";
							identityNames = cacheImportRowDetail.getReadLeaderIdentity().split(",");
							List<String> _names = new ArrayList<>();
							List<String> _identities = new ArrayList<>();
							List<String> _unitNames = new ArrayList<>();
							List<String> _topUnitNames = new ArrayList<>();
							try {
								for (String _identity : identityNames) {
									if (okrUserManagerService.getPersonNameByIdentity(_identity) == null) {
										throw new Exception("person not exsits, identity:" + _identity);
									}
									_identities.add( _identity );
									_names.add( okrUserManagerService.getPersonNameByIdentity(_identity) );
									_unitNames.add( okrUserManagerService.getUnitNameByIdentity(_identity) );
									_topUnitNames.add( okrUserManagerService.getTopUnitNameByIdentity(_identity) );
								}
								wrapInOkrWorkBaseInfo.setReadLeaderNameList(_names);
								wrapInOkrWorkBaseInfo.setReadLeaderIdentityList(_identities);
								wrapInOkrWorkBaseInfo.setReadLeaderUnitNameList(_unitNames);
								wrapInOkrWorkBaseInfo.setReadLeaderTopUnitNameList(_topUnitNames);
							} catch (Exception e) {
								check = false;
								Exception exception = new WorkReadLeaderInvalidException(e, cacheImportRowDetail.getReadLeaderIdentity());
								result.error(exception);
								logger.error(e, effectivePerson, request, null);
								wrapInOkrWorkBaseInfo.setCheckSuccess("failture");
								wrapInOkrWorkBaseInfo.setDescription("系统校验工作阅知领导信息发生异常，" + identityNames + "!");
							}
						} else {
							wrapInOkrWorkBaseInfo.setReadLeaderNameList(new ArrayList<>());
							wrapInOkrWorkBaseInfo.setReadLeaderIdentityList(new ArrayList<>());
							wrapInOkrWorkBaseInfo.setReadLeaderUnitNameList(new ArrayList<>());
							wrapInOkrWorkBaseInfo.setReadLeaderTopUnitNameList(new ArrayList<>());
						}
					}

					if (check) {
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
									Exception exception = new ReportDayInCycleInvalidException(
											wrapInOkrWorkBaseInfo.getReportDayInCycle());
									result.error(exception);
									// logger.error( e, effectivePerson,
									// request, null);
									wrapInOkrWorkBaseInfo.setCheckSuccess("failture");
									wrapInOkrWorkBaseInfo.setDescription("每周汇报日选择不正确："
											+ wrapInOkrWorkBaseInfo.getReportDayInCycle() + "，无法继续保存工作信息!");
								}
							} else {
								check = false;
								Exception exception = new ReportDayInCycleEmptyException();
								result.error(exception);
								// logger.error( e, effectivePerson, request,
								// null);
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
									Exception exception = new ReportDayInCycleInvalidException(
											wrapInOkrWorkBaseInfo.getReportDayInCycle());
									result.error(exception);
									// logger.error( e, effectivePerson,
									// request, null);
									wrapInOkrWorkBaseInfo.setCheckSuccess("failture");
									wrapInOkrWorkBaseInfo.setDescription("每月汇报日选择不正确："
											+ wrapInOkrWorkBaseInfo.getReportDayInCycle() + "，无法继续保存工作信息!");
								}
							} else {
								check = false;
								Exception exception = new ReportDayInCycleEmptyException();
								result.error(exception);
								wrapInOkrWorkBaseInfo.setCheckSuccess("failture");
								wrapInOkrWorkBaseInfo.setDescription("每月汇报日期为空，无法继续保存工作信息!");
							}
						} else {
							check = false;
							Exception exception = new ReportCycleInvalidException( cacheImportRowDetail.getReportCycle());
							result.error(exception);
							// logger.error( e, effectivePerson, request, null);
							wrapInOkrWorkBaseInfo.setCheckSuccess("failture");
							wrapInOkrWorkBaseInfo.setDescription( "汇报周期选择不正确：" + cacheImportRowDetail.getReportCycle() + "，无法继续保存工作信息!");
						}
					}
					wrapInList.add(wrapInOkrWorkBaseInfo);
				} catch (Exception e) {
					check = false;
					Exception exception = new WorkImportDataCheckException(e);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
					wrapInOkrWorkBaseInfo.setCheckSuccess("failture");
					wrapInOkrWorkBaseInfo.setDescription("系统在校验所有待保存数据信息时发生未知异常!");
				}
			}

			if (!"success".equals(wrapInOkrWorkBaseInfo.getCheckSuccess())) {
				errorWrapInList.add(wrapInOkrWorkBaseInfo);
			}
		}

		if (errorWrapInList != null && errorWrapInList.size() > 0) {
			result.error(new Exception("系统数据校验发现不合法数据，需要进一步修改。"));
			result.setData(errorWrapInList);
		} else {
			if (check) {
				if (wrapInList != null && !wrapInList.isEmpty()) {
					for ( Wi wrapIn : wrapInList ) {
						// 创建新的工作信息，保存到数据库
						try {
							okrWorkBaseInfo = okrWorkBaseInfoOperationService.save( wrapIn,
									wrapIn.getWorkDetail(), wrapIn.getDutyDescription(), wrapIn.getLandmarkDescription(),
									wrapIn.getMajorIssuesDescription(), wrapIn.getProgressAction(), wrapIn.getProgressPlan(),
									wrapIn.getResultDescription() );
						} catch (Exception e) {
							check = false;
							Exception exception = new WorkImportDataException(e);
							result.error(exception);
							logger.error(e, effectivePerson, request, null);
						}
					}
				}
			}
		}
		return result;
	}

public class Wi extends OkrWorkBaseInfo {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		private List<String> workIds = null;
		
		//工作详细信息数据
		private String workDetail = null;//事项分解
		
		private String dutyDescription = null;
		
		private String landmarkDescription = null;
		
		private String majorIssuesDescription = null;
		
		private String progressAction = null; 
		
		private String progressPlan = null;
		
		private String resultDescription = null;
		
		private String checkSuccess = "success";
		
		private String description = null;

		public List<String> getWorkIds() {
			return workIds;
		}

		public void setWorkIds(List<String> workIds) {
			this.workIds = workIds;
		}

		public String getWorkDetail() {
			return workDetail;
		}

		public void setWorkDetail(String workDetail) {
			this.workDetail = workDetail;
		}

		public String getDutyDescription() {
			return dutyDescription;
		}

		public void setDutyDescription(String dutyDescription) {
			this.dutyDescription = dutyDescription;
		}

		public String getLandmarkDescription() {
			return landmarkDescription;
		}

		public void setLandmarkDescription(String landmarkDescription) {
			this.landmarkDescription = landmarkDescription;
		}

		public String getMajorIssuesDescription() {
			return majorIssuesDescription;
		}

		public void setMajorIssuesDescription(String majorIssuesDescription) {
			this.majorIssuesDescription = majorIssuesDescription;
		}

		public String getProgressAction() {
			return progressAction;
		}

		public void setProgressAction(String progressAction) {
			this.progressAction = progressAction;
		}

		public String getProgressPlan() {
			return progressPlan;
		}

		public void setProgressPlan(String progressPlan) {
			this.progressPlan = progressPlan;
		}

		public String getResultDescription() {
			return resultDescription;
		}

		public void setResultDescription(String resultDescription) {
			this.resultDescription = resultDescription;
		}

		public String getCheckSuccess() {
			return checkSuccess;
		}

		public void setCheckSuccess(String checkSuccess) {
			this.checkSuccess = checkSuccess;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}
	}
}
