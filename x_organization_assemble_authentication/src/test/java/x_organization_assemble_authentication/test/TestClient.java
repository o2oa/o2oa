package x_organization_assemble_authentication.test;

import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import com.x.base.core.utils.DateTools;
import com.x.organization.core.entity.Person;

public class TestClient {
	@Test
	public void test1() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.x.organization.core.entity.Person");
		EntityManager em = emf.createEntityManager();
		Person person = new Person();
		em.persist(person);
		em.close();
		emf.close();
	}

	@Test
	public void test2() {
		String str = "devewp.vsettan.com.cn";
		if (StringUtils.contains(str, ".")) {
			String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
			Pattern pattern = Pattern.compile(rexp);
			Matcher matcher = pattern.matcher(str);
			if (!matcher.find()) {
				System.out.println(StringUtils.substringAfter(str, "."));
			}
		}
	}

	@Test
	public void test3() throws Exception {
		String url = "http://xa01.ray.local:20080/x_organization_assemble_authentication/jaxrs/oauth2server/info?access_token=yS7kNrdGuQT3X2YTYDi0FfUvBpmrAGeU-PY0cPj301s";
		// String url = "http://www.sohu.com";

		URLConnection connection = new URL(url).openConnection();
		// prepareConnection(connection);

		String sourceResponse = IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);
		System.out.println("!!!!!!!!!!!");
		System.out.println(sourceResponse);
	}

	@Test
	public void test4() throws Exception {
		Date date = new Date();
		Calendar cal = DateUtils.toCalendar(date);
System.out.println(DateTools.format(date));	
System.out.println(date.toGMTString());
		
		
		System.out.println(cal);
	}
}
