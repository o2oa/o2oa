package test.o2.collect.assemble;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.tools.ListTools;

import o2.collect.core.entity.Unit;

public class TestClient {

	@Test
	public void test() {
		System.out.println(Wi.copier.getEraseFields());
	}

	public static class Wi extends Unit {

		private static final long serialVersionUID = -7839216278338852396L;

		static WrapCopier<Wi, Unit> copier = WrapCopierFactory.wi(Wi.class, Unit.class, null,
				ListTools.toList(JpaObject.FieldsUnmodify, Unit.pinyin_FIELDNAME, Unit.pinyinInitial_FIELDNAME));

		@FieldDescribe("手机号码")
		private String mobile;

		@FieldDescribe("验证码答案")
		private String codeAnswer;

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}

		public String getCodeAnswer() {
			return codeAnswer;
		}

		public void setCodeAnswer(String codeAnswer) {
			this.codeAnswer = codeAnswer;
		}
	}

	@Test
	public void test1() {
		List<String> aaa = null;
		System.out.println(StringUtils.trimToEmpty(StringUtils.join(aaa, ",")));
	}
}
