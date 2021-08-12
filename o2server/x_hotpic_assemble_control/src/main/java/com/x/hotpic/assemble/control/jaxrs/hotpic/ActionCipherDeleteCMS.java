package com.x.hotpic.assemble.control.jaxrs.hotpic;

import java.util.List;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.hotpic.assemble.control.service.HotPictureInfoServiceAdv;
import com.x.hotpic.entity.HotPictureInfo;
import com.x.base.core.project.cache.ApplicationCache;

public class ActionCipherDeleteCMS extends BaseAction{
	
	ActionResult<Wo> execute(EffectivePerson effectivePerson,String id) throws Exception {		
		ActionResult<Wo> result = new ActionResult<>();
		List<HotPictureInfo> hotPictureInfos = null;
	    HotPictureInfoServiceAdv hotPictureInfoService = new HotPictureInfoServiceAdv();

		if (id == null || id.isEmpty() || "(0)".equals(id)) {
		    throw new InfoIdEmptyException();
		}

		try {
				hotPictureInfos = hotPictureInfoService.listByApplicationInfoId(HotPictureInfo.APPLICATION_CMS, id );
				if(ListTools.isNotEmpty( hotPictureInfos )){
					for( HotPictureInfo hotPictureInfo : hotPictureInfos ){
						hotPictureInfoService.delete( hotPictureInfo.getId() );
					}
					ApplicationCache.notify(HotPictureInfo.class);
				}
			} catch (Exception e) {
				throw  new InfoQueryByIdException(e, id);
			}
		
		Wo wo =  new Wo();
		wo.setValue( id );
		result.setData( wo );
		return result;
	}
	
	public static class Wi {
		
	}
	
	public static class Wo extends GsonPropertyObject {

		private String value;

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

	}
}
