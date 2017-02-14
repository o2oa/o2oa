package x.collaboration.service.message;

import javax.servlet.annotation.WebListener;

import com.x.base.core.project.AbstractApplicationServletContextListener;
import com.x.base.core.project.ThisApplicationClass;
import com.x.base.core.project.x_collaboration_service_message;

@WebListener
@ThisApplicationClass(ThisApplication.class)
public class ApplicationServletContextListener extends AbstractApplicationServletContextListener {

	public Class<?> getThis() {
		return x_collaboration_service_message.class;
	}

}
