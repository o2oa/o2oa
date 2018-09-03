package x.collaboration.service.message.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.application.jaxrs.CipherManagerJaxrsFilter;

@WebFilter(urlPatterns = { "/jaxrs/message/*" })
public class MessageJaxrsFilter extends CipherManagerJaxrsFilter {

}
