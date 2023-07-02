package com.x.program.center.jaxrs.appstyle;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.AppStyle.Image;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;

class ActionImageLaunchLogoErase extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		if (!effectivePerson.isManager()) {
			throw new ExceptionAccessDenied(effectivePerson.getName());
		}
		// 由于getImages设置了检查,所以只能对images进行处理
		Set<Image> images = Config.appStyle().getImages();
		images = images.stream().filter(img -> {
			return (!StringUtils.equals(img.getName(), Image.name_launch_logo));
		}).collect(Collectors.toSet());
		Config.appStyle().setImages(new TreeSet<>(images));
		Config.appStyle().save();
		Config.flush();
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {

	}

}