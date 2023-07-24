package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapStringList;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.service.ReviewService;
import com.x.cms.assemble.control.service.UserManagerService;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;

public class ActionQueryListVisiblePersons extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionQueryListVisiblePersons.class);

	/**
	 * 根据文档ID，获取该文档所有的可见者列表
	 * @param request
	 * @param id
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	protected ActionResult<Wo> execute(HttpServletRequest request, String id, EffectivePerson effectivePerson)
			throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		AppInfo appInfo = null;
		CategoryInfo categoryInfo = null;
		Document document = null;
		Boolean check = true;
		String personName = effectivePerson.getDistinguishedName();
		
		if ( StringUtils.isEmpty(id)) {
			check = false;
			Exception exception = new ExceptionDocumentIdEmpty();
			result.error(exception);
		}
		
		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), id );
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );

		if (optional.isPresent()) {
			wo = ( Wo ) optional.get();
			result.setData(wo);
		} else {
			if (check) {
				try {
					document = documentQueryService.get(id);
					if (document == null) {
						check = false;
						Exception exception = new ExceptionDocumentNotExists(id);
						result.error(exception);
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionDocumentInfoProcess(e, "文档信息获取操作时发生异常。Id:" + id + ", Name:" + personName);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			if (check) {
				try {
					categoryInfo = categoryInfoServiceAdv.get( document.getCategoryId() );
					if (categoryInfo == null) {
						check = false;
						Exception exception = new ExceptionCategoryInfoNotExists(document.getCategoryId());
						result.error(exception);
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionDocumentInfoProcess(e, "系统在根据ID查询分类信息时发生异常！ID：" + document.getCategoryId());
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}

			if (check) {
				try {
					appInfo = appInfoServiceAdv.get( document.getAppId() );
					if (appInfo == null) {
						check = false;
						Exception exception = new ExceptionAppInfoNotExists(document.getAppId());
						result.error(exception);
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionDocumentInfoProcess(e, "系统在根据ID查询应用栏目信息时发生异常！ID：" + document.getAppId());
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			
			if (check) {
				//计算该文档有多少阅读者
				ReviewService reviewService = new ReviewService();
				List<String> persons = reviewService.listPermissionPersons( appInfo, categoryInfo, document );
				
				if( ListTools.isNotEmpty( persons )) {
					//有可能是*， 一般是所有的人员标识列表
					if( persons.contains( "*" )) {
						List<String> allPersons = listPersonWithUnit( document.getCreatorTopUnitName() );
						if( ListTools.isNotEmpty( allPersons )) {
							for( String person : persons ) {
								if( StringUtils.equals( "*" , person ) && allPersons.contains( person )) {
									allPersons.add( person );
								}
							}
						}
						persons = allPersons;
					}
				}
				if( persons == null ) {
					persons = new ArrayList<>();
				}
				
				//去一下重复
		        HashSet<String> set = new HashSet<String>( persons );
		        persons.clear();
		        persons.addAll(set);
				
				wo.setValueList(persons);
				result.setData(wo);
				result.setCount( Long.parseLong( persons.size() + ""));
				CacheManager.put(cacheCategory, cacheKey, wo );
			}
		}
		return result;
	}
	
	/**
	 * 根据组织名称，获取该组织下所有的人员标识
	 * @param unitName
	 * @return
	 */
	private List<String> listPersonWithUnit(String unitName) {
		UserManagerService  userManagerService = new UserManagerService();
		List<String> persons = null;
		try {
			persons = userManagerService.listPersonWithUnit(unitName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return persons;
	}

	public static class Wo extends WrapStringList {

	}

}