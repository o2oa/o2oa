package o2.base.core.project.jaxrs;

import javax.servlet.annotation.WebFilter;

@WebFilter(urlPatterns = { "/druid/*" })
public class DruidFilter extends CipherManagerJaxrsFilter {

}