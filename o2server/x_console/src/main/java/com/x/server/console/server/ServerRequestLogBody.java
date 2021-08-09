package com.x.server.console.server;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.DateCache;
import org.eclipse.jetty.util.annotation.ManagedObject;

import com.x.base.core.project.http.HttpToken;

/**
 * A flexible RequestLog, which produces log strings in a customizable format.
 * The Logger takes a format string where request characteristics can be added
 * using "%" format codes which are replaced by the corresponding value in the
 * log output.
 * <p>
 * The terms server, client, local and remote are used to refer to the different
 * addresses and ports which can be logged. Server and client refer to the
 * logical addresses which can be modified in the request headers. Where local
 * and remote refer to the physical addresses which may be a proxy between the
 * end-user and the server.
 *
 *
 * <br>
 * <br>
 * Percent codes are specified in the format %MODIFIERS{PARAM}CODE
 * 
 * <pre>
* MODIFIERS:
*     Optional list of comma separated HTTP status codes which may be preceded by a single "!" to indicate
*     negation. If the status code is not in the list the literal string "-" will be logged instead of
*     the resulting value from the percent code.
* {PARAM}:
*     Parameter string which may be optional depending on the percent code used.
* CODE:
*     A one or two character code specified by the {@link ServerRequestLogBody} table of format codes.
 * </pre>
 *
 * <table>
 * <caption>Format Codes</caption>
 * <tr>
 * <td><b>Format String</b></td>
 * <td><b>Description</b></td>
 * </tr>
 *
 * <tr>
 * <td>%%</td>
 * <td>The percent sign.</td>
 * </tr>
 *
 * <tr>
 * <td valign="top">%{format}a</td>
 * <td>Address or Hostname. Valid formats are {server, client, local, remote}
 * Optional format parameter which will be server by default. <br>
 * Where server and client are the logical addresses which can be modified in
 * the request headers, while local and remote are the physical addresses so may
 * be a proxy between the end-user and the server.</td>
 * </tr>
 *
 * <tr>
 * <td valign="top">%{format}p</td>
 * <td>Port. Valid formats are {server, client, local, remote} Optional format
 * parameter which will be server by default. <br>
 * Where server and client are the logical ports which can be modified in the
 * request headers, while local and remote are the physical ports so may be to a
 * proxy between the end-user and the server.</td>
 * </tr>
 *
 * <tr>
 * <td valign="top">%{CLF}I</td>
 * <td>Size of request in bytes, excluding HTTP headers. Optional parameter with
 * value of "CLF" to use CLF format, i.e. a '-' rather than a 0 when no bytes
 * are sent.</td>
 * </tr>
 *
 * <tr>
 * <td valign="top">%{CLF}O</td>
 * <td>Size of response in bytes, excluding HTTP headers. Optional parameter
 * with value of "CLF" to use CLF format, i.e. a '-' rather than a 0 when no
 * bytes are sent.</td>
 * </tr>
 *
 * <tr>
 * <td valign="top">%{CLF}S</td>
 * <td>Bytes transferred (received and sent). This is the combination of %I and
 * %O. Optional parameter with value of "CLF" to use CLF format, i.e. a '-'
 * rather than a 0 when no bytes are sent.</td>
 * </tr>
 *
 * <tr>
 * <td valign="top">%{VARNAME}C</td>
 * <td>The contents of cookie VARNAME in the request sent to the server. Only
 * version 0 cookies are fully supported. Optional VARNAME parameter, without
 * this parameter %C will log all cookies from the request.</td>
 * </tr>
 *
 * <tr>
 * <td valign="top">%D</td>
 * <td>The time taken to serve the request, in microseconds.</td>
 * </tr>
 *
 * <tr>
 * <td valign="top">%{VARNAME}e</td>
 * <td>The contents of the environment variable VARNAME.</td>
 * </tr>
 *
 * <tr>
 * <td valign="top">%f</td>
 * <td>Filename.</td>
 * </tr>
 *
 * <tr>
 * <td valign="top">%H</td>
 * <td>The request protocol.</td>
 * </tr>
 *
 * <tr>
 * <td valign="top">%{VARNAME}i</td>
 * <td>The contents of VARNAME: header line(s) in the request sent to the
 * server.</td>
 * </tr>
 *
 * <tr>
 * <td valign="top">%k</td>
 * <td>Number of keepalive requests handled on this connection. Interesting if
 * KeepAlive is being used, so that, for example, a '1' means the first
 * keepalive request after the initial one, '2' the second, etc...; otherwise
 * this is always 0 (indicating the initial request).</td>
 * </tr>
 *
 * <tr>
 * <td valign="top">%m</td>
 * <td>The request method.</td>
 * </tr>
 *
 * <tr>
 * <td valign="top">%{VARNAME}o</td>
 * <td>The contents of VARNAME: header line(s) in the response.</td>
 * </tr>
 *
 * <tr>
 * <td valign="top">%q</td>
 * <td>The query string (prepended with a ? if a query string exists, otherwise
 * an empty string).</td>
 * </tr>
 *
 * <tr>
 * <td valign="top">%r</td>
 * <td>First line of request.</td>
 * </tr>
 *
 * <tr>
 * <td valign="top">%R</td>
 * <td>The handler generating the response (if any).</td>
 * </tr>
 *
 * <tr>
 * <td valign="top">%s</td>
 * <td>Response status.</td>
 * </tr>
 *
 * <tr>
 * <td valign="top">%{format|timeZone|locale}t</td>
 * <td>The time that the request was received. Optional parameter in one of the
 * following formats {format}, {format|timeZone} or
 * {format|timeZone|locale}.<br>
 * <br>
 *
 * <pre>
* Format Parameter: (default format [18/Sep/2011:19:18:28 -0400] where the last number indicates the timezone offset from GMT.)
*     Must be in a format supported by {@link DateCache}
*
* TimeZone Parameter:
*     Default timeZone GMT
*     Must be in a format supported by {@link TimeZone#getTimeZone(String)}
*
* Locale Parameter:
*     Default locale {@link Locale#getDefault()}
*     Must be in a format supported by {@link Locale#forLanguageTag(String)}
 * </pre>
 * 
 * </td>
 * </tr>
 *
 * <tr>
 * <td valign="top">%T</td>
 * <td>The time taken to serve the request, in seconds.</td>
 * </tr>
 *
 * <tr>
 * <td valign="top">%{UNIT}T</td>
 * <td>The time taken to serve the request, in a time unit given by UNIT. Valid
 * units are ms for milliseconds, us for microseconds, and s for seconds. Using
 * s gives the same result as %T without any format; using us gives the same
 * result as %D.</td>
 * </tr>
 *
 * <tr>
 * <td valign="top">%{d}u</td>
 * <td>Remote user if the request was authenticated. May be bogus if return
 * status (%s) is 401 (unauthorized). Optional parameter d, with this parameter
 * deferred authentication will also be checked.</td>
 * </tr>
 *
 * <tr>
 * <td valign="top">%U</td>
 * <td>The URL path requested, not including any query string.</td>
 * </tr>
 *
 * <tr>
 * <td valign="top">%X</td>
 * <td>Connection status when response is completed:
 * 
 * <pre>
* X = Connection aborted before the response completed.
* + = Connection may be kept alive after the response is sent.
* - = Connection will be closed after the response is sent.
 * </pre>
 * 
 * </td>
 * </tr>
 *
 * <tr>
 * <td valign="top">%{VARNAME}^ti</td>
 * <td>The contents of VARNAME: trailer line(s) in the request sent to the
 * server.</td>, boolean appendBodyToRequest
 * </tr>
 *
 * <tr>
 * <td>%{VARNAME}^to</td>
 * <td>The contents of VARNAME: trailer line(s) in the response sent from the
 * server.</td>
 * </tr>
 * </table>
 */
@ManagedObject("Custom format request log with body")
public class ServerRequestLogBody extends ServerRequestLog {

	public ServerRequestLogBody(ServerRequestLog.Writer writer, String formatString) {
		super(writer, formatString);
	}

	@Override
	public void customLog(Request request, StringBuilder sb) throws UnsupportedEncodingException {
		// java8不支持charset
		sb.append(" \"")
				.append(URLEncoder.encode(Objects.toString(request.getAttribute(HttpToken.X_DISTINGUISHEDNAME), ""),
						StandardCharsets.UTF_8.toString()))
				.append("\"");
		Object body = request.getAttribute(HttpToken.X_REQUESTBODY);
		if (null != body) {
			sb.append(" ").append(URLEncoder.encode(body.toString(), StandardCharsets.UTF_8.toString()));
		}
	}

}