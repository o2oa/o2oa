package com.x.hotpic.assemble.control.jaxrs.hotpic;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.hotpic.assemble.control.service.HotPictureInfoServiceAdv;
import com.x.hotpic.entity.HotPictureInfo;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import java.util.ArrayList;
import java.util.List;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;

public class ActionCipherGet extends BaseAction{
	ActionResult<Wo> execute(EffectivePerson effectivePerson,String id) throws Exception {		
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		HotPictureInfo hotPictureInfo = null;

		if (id == null || id.isEmpty() || "(0)".equals(id)) {
				throw new InfoIdEmptyException();
		}

		Ehcache cache = ApplicationCache.instance().getCache(HotPictureInfo.class);
	    HotPictureInfoServiceAdv hotPictureInfoService = new HotPictureInfoServiceAdv();

		String cacheKey = "base64#" + id;
		Element element = cache.get(cacheKey);

		if (null != element) {
				wo = (Wo) element.getObjectValue();
				result.setData(wo);
			} else {
				try {
					hotPictureInfo = hotPictureInfoService.get(id);
					if (hotPictureInfo == null) {
						throw new InfoNotExistsException(id);
					} else {
						 wo = Wo.copier.copy(hotPictureInfo);			
						cache.put(new Element(cacheKey, wo));
						result.setData(wo);
					}
				} catch (Exception e) {
					throw new InfoQueryByIdException(e, id);
				}
			}

		return result;
	}
	
	public static class Wi {
		
	}
	
	public static class Wo extends HotPictureInfo {
		public static List<String> Excludes = new ArrayList<String>();
		public static WrapCopier<HotPictureInfo, Wo> copier = WrapCopierFactory.wo(HotPictureInfo.class, Wo.class, null,JpaObject.FieldsInvisible);
		
	}
	
}
