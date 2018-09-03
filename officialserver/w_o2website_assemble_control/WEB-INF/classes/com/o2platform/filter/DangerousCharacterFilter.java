package com.o2platform.filter;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 危险字符过滤器
 * 
 * 不允许在系统内以用户参数的方式传递如HTML标签等特殊符号
 *
 */
public class DangerousCharacterFilter extends OncePerRequestFilter {
	
    @Override
    protected void doFilterInternal( HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

    	/**
    	 * 对传入的参数进行危险字符过滤
    	 * 对请求对象进行包装，在获取请求具体参数时会进行信息过滤
    	 */
        request = new Request((HttpServletRequest) request);
        response.setHeader("Set-Cookie", "name=value; HttpOnly");
        chain.doFilter(request, response);
    }

    /**
     * |（竖线符号）
 & （& 符号）
 ;（分号）
 $（美元符号）
 %（百分比符号）
 @（at 符号）
 '（单引号）
 "（引号）
 \'（反斜杠转义单引号）
 \"（反斜杠转义引号）
 <>（尖括号）
 ()（括号）
 +（加号）
 CR（回车符，ASCII 0x0d）
 LF（换行，ASCII 0x0a）
 ,（逗号）
 \（反斜杠）
 Eval方法
 Document
 Cookie
 Javascript
 Script
 onerror
     * @param value
     * @return
     */
    public String filterDangerString(String value) {
    	
        if (value == null) {  return null; }
        // 字符转换 #[backslash] = /
        value = value.replaceAll("/", "#backslash");
        // 字符转换 #[lt] = <
        value = value.replaceAll("<", "#lt");
        // 字符转换 #[gt] = >
        value = value.replaceAll(">", "#gt");
        // 字符转换 #[at] = @
        value = value.replaceAll("@", "#at");
        // 字符转换 #[semicolon] = ;
        value = value.replaceAll(";", "#semicolon");
        // 字符转换 #[and] = ;
        value = value.replaceAll("&", "#and");
        
        value = value.replaceAll("\\|", "");
        value = value.replaceAll("\\\\", "");
        value = value.replaceAll("\\(", "");
        value = value.replaceAll("\\)", "");
        value = value.replaceAll("\\+", "");
        value = value.replaceAll("%", "");
        value = value.replaceAll("\r", "");
        value = value.replaceAll("\n", "");
        value = value.replaceAll("script", "");
        value = value.replaceAll("SCRIPT", "");
        value = value.replaceAll("=", "");
        
        value = value.replaceAll("Eval", "");
        value = value.replaceAll("Document", "");
        value = value.replaceAll("Cookie", "");
        value = value.replaceAll("Javascript", "");
        value = value.replaceAll("Script", "");
        value = value.replaceAll("onerror", "");

        return value;
    }

    /**
     * 请求包装实现类
     * @author liyi
     *
     */
    class Request extends HttpServletRequestWrapper {
    	
        public Request(HttpServletRequest request) {
            super(request);
        }
        
        @SuppressWarnings("deprecation")
		@Override
        public String getParameter(String name) {
            // 返回值之前 先进行过滤
        	String value = super.getParameter( name );
        	if( value!=null && !value.isEmpty()){
        		value = URLDecoder.decode ( URLDecoder.decode( value ) );       
        	}
            return filterDangerString( value );
        }

        @SuppressWarnings("deprecation")
		@Override
        public String[] getParameterValues(String name) {
            // 返回值之前 先进行过滤
            String[] values = super.getParameterValues(name);            
            if( values != null ){            	
            	for (int i = 0; i < values.length; i++) {
            		if( values[i]!=null && !values[i].isEmpty()){
            			/**
                		 * 有可能会有中文传递，中文传递时会二次使用URL编码，所以此处先URL解码两次，多次解码不影响结果
                		 */
                		values[i] = URLDecoder.decode ( URLDecoder.decode( values[i]) );
                		/**
                		 * 然后对传递的结果进行危险字符过滤
                		 */
                        values[i] = filterDangerString(values[i]);
            		}
                }
            }
            return values;
        }
    }
}
