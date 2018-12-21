package x_organization_core_entity;

import java.util.Date;

import org.junit.Test;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.x.base.core.project.tools.DateTools;

public class Bar {
	@Test
	public void test() throws Exception {
		System.out.println(PinyinHelper.convertToPinyinString("张三", "", PinyinFormat.WITHOUT_TONE));
		System.out.println(PinyinHelper.getShortPinyin("张三"));
	}

	@Test
	public void test1() throws Exception {
		Date d1 = DateTools.parse("1980-01-01  22:22:22");
		Date d2 = DateTools.parse("2006-02-11 12:12:12");
		long i = d1.getTime() - d2.getTime();
		System.out.println(i / (1000 * 60 * 60 * 24 * 365));

	}
}
