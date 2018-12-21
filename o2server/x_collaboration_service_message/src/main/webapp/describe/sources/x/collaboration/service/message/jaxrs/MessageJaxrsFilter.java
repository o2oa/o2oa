package x.collaboration.service.message.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/message/*", "/jaxrs/sms/*" })
public class MessageJaxrsFilter extends CipherManagerJaxrsFilter {

}
