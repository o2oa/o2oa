package o2.base.core.project.jaxrs;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = { "/jaxrs/cache/*" })
public class CacheJaxrsFilter extends CipherManagerJaxrsFilter {

}
