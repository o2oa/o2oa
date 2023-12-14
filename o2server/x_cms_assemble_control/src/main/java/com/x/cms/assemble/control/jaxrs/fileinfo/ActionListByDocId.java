package com.x.cms.assemble.control.jaxrs.fileinfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.factory.FileInfoFactory;
import com.x.cms.core.entity.FileInfo;

public class ActionListByDocId extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListByDocId.class);

	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String docId) throws Exception {
		LOGGER.debug("execute:{}, docId:{}.", effectivePerson::getDistinguishedName, () -> docId);
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = null;

		Cache.CacheKey cacheKey = new Cache.CacheKey(this.getClass(), docId, effectivePerson.getDistinguishedName());
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);

		if (optional.isPresent()) {
			wos = (List<Wo>) optional.get();
			result.setData(wos);
		} else {
			wos = list(effectivePerson, docId);
			CacheManager.put(cacheCategory, cacheKey, wos);
		}
		result.setData(wos);
		return result;
	}

	private List<Wo> list(EffectivePerson effectivePerson, String docId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			FileInfoFactory fileInfoFactory = business.getFileInfoFactory();
			List<String> ids = fileInfoFactory.listAttachmentByDocument(docId);// 获取指定文档的所有附件列表
			List<FileInfo> fileInfoList = emc.list(FileInfo.class, ids);// 查询ID IN ids 的所有文件或者附件信息列表
			List<String> identities = business.organization().identity().listWithPerson(effectivePerson);
			List<String> units = business.organization().unit().listWithPerson(effectivePerson);
			List<Wo> wos = new ArrayList<>();
			if (ListTools.isNotEmpty(fileInfoList)) {
				for (FileInfo fileInfo : fileInfoList) {
					wos.add(concrete(effectivePerson, business, identities, units, fileInfo));
				}
			}
			wos = wos.stream().sorted(Comparator.comparing(Wo::getSeqNumber, Comparator.nullsLast(Integer::compareTo))
					.thenComparing(Comparator.comparing(Wo::getCreateTime, Comparator.nullsLast(Date::compareTo))))
					.collect(Collectors.toList());
			return wos;
		}
	}

	private Wo concrete(EffectivePerson effectivePerson, Business business, List<String> identities, List<String> units,
			FileInfo fileInfo) throws Exception {
		Wo wo = Wo.copier.copy(fileInfo);
		boolean canControl = this.control(effectivePerson, business, fileInfo, identities, units);
		if (canControl) {
			wo.getControl().setAllowRead(true);
			wo.getControl().setAllowEdit(true);
			wo.getControl().setAllowControl(true);
		} else {
			boolean canEdit = this.edit(effectivePerson, fileInfo, identities, units);
			if (canEdit) {
				wo.getControl().setAllowEdit(true);
				wo.getControl().setAllowRead(true);
			} else {
				boolean canRead = this.read(effectivePerson, fileInfo, identities, units);
				if (canRead) {
					wo.getControl().setAllowRead(true);
				}
			}
		}
		return wo;
	}

	public static class Wo extends FileInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		private WoControl control = new WoControl();

		public WoControl getControl() {
			return control;
		}

		public void setControl(WoControl control) {
			this.control = control;
		}

		public static final WrapCopier<FileInfo, Wo> copier = WrapCopierFactory.wo(FileInfo.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}

	public static class WoControl extends AbstractWoControl {

		private static final long serialVersionUID = -8008657773571124079L;

	}

}
