package com.x.cms.assemble.control.jaxrs.fileinfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.factory.FileInfoFactory;
import com.x.cms.core.entity.FileInfo;

import net.sf.ehcache.Element;

public class ActionListByDocId extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListByDocId.class);
	
	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson, String docId ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = null;
//		AppInfo appInfo = null;
//		CategoryInfo categoryInfo = null;
//		Document document = null;
		Boolean isAnonymous = effectivePerson.isAnonymous();
		Boolean isManager = false;
		Boolean check = true;
		
		if (check) {
			try {
				if ( effectivePerson.isManager() ) {
					isManager = true;
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionFileInfoProcess(e, "判断用户是否是系统管理员时发生异常！user:" + effectivePerson.getDistinguishedName() );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		String cacheKey = ApplicationCache.concreteCacheKey( "document", docId, isAnonymous, isManager, effectivePerson.getDistinguishedName() );
		Element element = cache.get(cacheKey);

		if ( (null != element) && (null != element.getObjectValue()) ) {
			wos = (List<Wo>) element.getObjectValue();
			result.setData(wos);
		} else {
//			if (check) {
//				try {
//					document = documentQueryService.get( docId );
//					if (document == null) {
//						check = false;
//						Exception exception = new ExceptionDocumentNotExists( docId );
//						result.error(exception);
//					}
//				} catch (Exception e) {
//					check = false;
//					Exception exception = new ExceptionFileInfoProcess(e, "文档信息获取操作时发生异常。Id:" + docId + ", Name:" + effectivePerson.getDistinguishedName());
//					result.error(exception);
//					logger.error(e, effectivePerson, request, null);
//				}
//			}
//			
//			if (check) {
//				try {
//					categoryInfo = categoryInfoServiceAdv.get( document.getCategoryId() );
//					if (categoryInfo == null) {
//						check = false;
//						Exception exception = new ExceptionCategoryInfoNotExists(document.getCategoryId());
//						result.error(exception);
//					}
//				} catch (Exception e) {
//					check = false;
//					Exception exception = new ExceptionFileInfoProcess(e, "系统在根据ID查询分类信息时发生异常！ID：" + document.getCategoryId());
//					result.error(exception);
//					logger.error(e, effectivePerson, request, null);
//				}
//			}
//			
//			if (check) {
//				try {
//					appInfo = appInfoServiceAdv.get( categoryInfo.getAppId() );
//					if (appInfo == null) {
//						check = false;
//						Exception exception = new ExceptionAppInfoNotExists(categoryInfo.getAppId());
//						result.error(exception);
//					}
//				} catch (Exception e) {
//					check = false;
//					Exception exception = new ExceptionFileInfoProcess(e, "系统在根据ID查询应用栏目信息时发生异常！ID：" + categoryInfo.getAppId());
//					result.error(exception);
//					logger.error(e, effectivePerson, request, null);
//				}
//			}
			
			if (check) {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					FileInfoFactory fileInfoFactory = business.getFileInfoFactory();					
					List<String> ids = fileInfoFactory.listAttachmentByDocument( docId );// 获取指定文档的所有附件列表
					List<FileInfo> fileInfoList = emc.list( FileInfo.class, ids );// 查询ID IN ids 的所有文件或者附件信息列表				
					List<String> identities = business.organization().identity().listWithPerson(effectivePerson);
					List<String> units = business.organization().unit().listWithPerson(effectivePerson);
					
					Wo wo = null;
					wos = new ArrayList<>();
					if( ListTools.isNotEmpty( fileInfoList )) {
						for ( FileInfo fileInfo : fileInfoList ) {
							wo =  Wo.copier.copy(fileInfo);
							if (this.read(wo, effectivePerson, identities, units )) {
								wo.getControl().setAllowRead(true);
								wo.getControl().setAllowEdit(this.edit(wo, effectivePerson, identities, units));
								wo.getControl().setAllowControl(this.control(wo, effectivePerson, identities, units));
								wos.add(wo);
							}
						}
					}
					wos = wos.stream().sorted(Comparator.comparing(Wo::getCreateTime)).collect(Collectors.toList());
					cache.put(new Element(cacheKey, wos));
					result.setData(wos);
				} catch (Throwable th) {
					th.printStackTrace();
					result.error(th);
				}
			}
		}

		return result;
	}
	
	public static class Wo extends FileInfo {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		private WoControl control = new WoControl();

		public WoControl getControl() {
			return control;
		}

		public void setControl(WoControl control) {
			this.control = control;
		}
		
		public static WrapCopier<FileInfo, Wo> copier = WrapCopierFactory.wo( FileInfo.class, Wo.class, null, JpaObject.FieldsInvisible);
	}
	
	public static class WoControl extends GsonPropertyObject {

		private Boolean allowRead = false;
		private Boolean allowEdit = false;
		private Boolean allowControl = false;

		public Boolean getAllowRead() {
			return allowRead;
		}

		public void setAllowRead(Boolean allowRead) {
			this.allowRead = allowRead;
		}

		public Boolean getAllowEdit() {
			return allowEdit;
		}

		public void setAllowEdit(Boolean allowEdit) {
			this.allowEdit = allowEdit;
		}

		public Boolean getAllowControl() {
			return allowControl;
		}

		public void setAllowControl(Boolean allowControl) {
			this.allowControl = allowControl;
		}
	}
	
	private boolean read(Wo woFileInfo, EffectivePerson effectivePerson, List<String> identities, List<String> units) throws Exception {
		boolean value = false;
		if (effectivePerson.isPerson(woFileInfo.getCreatorUid())) {
			value = true;
		} else if (ListTools.isEmpty(woFileInfo.getReadIdentityList()) && ListTools.isEmpty(woFileInfo.getReadUnitList())) {
			value = true;
		} else if (ListTools.containsAny(identities, woFileInfo.getReadIdentityList()) || ListTools.containsAny(units, woFileInfo.getReadUnitList())) {

			value = true;
		} else if (ListTools.containsAny(identities, woFileInfo.getEditIdentityList()) || ListTools.containsAny(units, woFileInfo.getEditUnitList())) {
			value = true;
		} else {
			if (ListTools.containsAny(identities, woFileInfo.getControllerIdentityList()) || ListTools.containsAny(units, woFileInfo.getControllerUnitList())) {
				value = true;
			}
		}
		return value;
	}

	private boolean edit(Wo wo, EffectivePerson effectivePerson, List<String> identities, List<String> units) throws Exception {
		boolean value = false;
		if (effectivePerson.isPerson(wo.getCreatorUid())) {
			value = true;
		} else if (ListTools.isEmpty(wo.getEditIdentityList()) && ListTools.isEmpty(wo.getEditUnitList())) {
			value = true;
		} else {
			if (ListTools.containsAny(identities, wo.getEditIdentityList()) || ListTools.containsAny(units, wo.getEditUnitList())) {
				value = true;
			}
		}
		return value;
	}

	private boolean control(Wo wo, EffectivePerson effectivePerson, List<String> identities, List<String> units)
			throws Exception {
		boolean value = false;
		if (effectivePerson.isPerson(wo.getCreatorUid())) {
			value = true;
		} else if (ListTools.isEmpty(wo.getControllerUnitList()) && ListTools.isEmpty(wo.getControllerIdentityList())) {
			value = true;
		} else {
			if (ListTools.containsAny(identities, wo.getControllerIdentityList())
					|| ListTools.containsAny(units, wo.getControllerUnitList())) {
				value = true;
			}
		}
		return value;
	}
}