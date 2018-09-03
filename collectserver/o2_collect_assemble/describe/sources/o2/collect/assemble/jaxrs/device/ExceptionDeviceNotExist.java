package o2.collect.assemble.jaxrs.device;

import java.util.Objects;

import com.x.base.core.project.exception.PromptException;

class ExceptionDeviceNotExist extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionDeviceNotExist(String name) {
		super("设备不存在:" + Objects.toString(name) + ".");
	}
}
