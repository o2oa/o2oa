package x_organization_assemble_authentication.test;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.x.base.core.Crypto;
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
		File file = new File("e:/qrcode_logo-64px.png");
		byte[] bs = FileUtils.readFileToByteArray(file);
		// System.out.println(bs.length);
		for (byte o : bs) {
			System.out.print(o);
			System.out.print(",");
		}

	}

	@Test
	public void test5() throws Exception {
		String str = "U2FsdGVkX1+lbxz80FLX7HDXIAN0jp2/n+YW9WO2erE=";
		String sso = "xplatform";
		str = new String(Base64.encodeBase64(str.getBytes("utf-8")), "utf-8");
		System.out.println(str);
		System.out.println(Crypto.decrypt(str, sso));
		// System.out.println(Base64.decodeBase64(str));
		// String val = new String(Base64.decodeBase64(str),"utf-8");
		// System.out.println(val);
		// System.out.println(Crypto.decrypt(val, sso));

	}

	@Test
	public void test6() throws Exception {
		String str = "zhenglong#1001";
		String sso = "xplatform";
		System.out.println(Crypto.encrypt(str, sso));
		// System.out.println(Base64.decodeBase64(str));
		// String val = new String(Base64.decodeBase64(str),"utf-8");
		// System.out.println(val);
		// System.out.println(Crypto.decrypt(val, sso));

	}

}
