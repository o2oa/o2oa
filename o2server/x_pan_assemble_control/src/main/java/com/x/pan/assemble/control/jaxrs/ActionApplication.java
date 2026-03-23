package com.x.pan.assemble.control.jaxrs;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.pan.assemble.control.jaxrs.attachment2.Attachment2Action;
import com.x.pan.assemble.control.jaxrs.attachment3.Attachment3Action;
import com.x.pan.assemble.control.jaxrs.attachment3.AttachmentWopiAction;
import com.x.pan.assemble.control.jaxrs.config.ConfigAction;
import com.x.pan.assemble.control.jaxrs.favorite.FavoriteAction;
import com.x.pan.assemble.control.jaxrs.folder2.Folder2Action;
import com.x.pan.assemble.control.jaxrs.folder3.Folder3Action;
import com.x.pan.assemble.control.jaxrs.recycle.RecycleAction;
import com.x.pan.assemble.control.jaxrs.share.ShareAction;
import com.x.pan.assemble.control.jaxrs.zone.ZoneAction;

import javax.ws.rs.ApplicationPath;
import java.util.Set;

/**
 * Jaxrs服务注册类，在此类中注册的Action会向外提供服务
 * @author sword
 */
@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	@Override
	public Set<Class<?>> getClasses() {

		//提供服务的Action类需要在这里注册，不然无法向外提供服务
		classes.add(Attachment2Action.class);
		classes.add(Folder2Action.class);
		classes.add(Attachment3Action.class);
		classes.add(AttachmentWopiAction.class);
		classes.add(Folder3Action.class);
		classes.add(ZoneAction.class);
		classes.add(ShareAction.class);
		classes.add(RecycleAction.class);
		classes.add(ConfigAction.class);
		classes.add(FavoriteAction.class);

		return this.classes;
	}

}
