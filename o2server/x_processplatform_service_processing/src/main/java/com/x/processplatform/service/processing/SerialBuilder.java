package com.x.processplatform.service.processing;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.graalvm.polyglot.Source;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.Applications;
import com.x.base.core.project.Context;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.jaxrs.WrapInteger;
import com.x.base.core.project.scripting.GraalvmScriptingFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.core.express.Organization;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.service.processing.processor.AeiObjects;

/**
 * 创建流程序列号
 * 
 * @author sword
 */
public class SerialBuilder {

	private static final String EMPTY_SYMBOL = "(0)";

	private Context context;

	public SerialBuilder(Context context, EntityManagerContainer emc, String processId, String workId)
			throws Exception {
		this.context = context;
		process = emc.find(processId, Process.class);
		if (null == process) {
			throw new ExceptionEntityNotExist(processId, Process.class);
		}
		work = emc.find(workId, Work.class);
		if (null == work) {
			throw new ExceptionEntityNotExist(workId, Work.class);
		}
		serial = new Serial();
		this.date = new Date();
	}

	private Process process;

	private Work work;

	private Date date;

	public Serial serial;

	List<Object> itemResults = new ArrayList<>();

	private Type collectionType = new TypeToken<ArrayList<SerialTextureItem>>() {
	}.getType();

	public String concrete(AeiObjects aeiObjects) throws Exception {
		StringBuilder stringBuilder = new StringBuilder("");
		String data = process.getSerialTexture();
		if (StringUtils.isNotEmpty(data)) {
			List<SerialTextureItem> list = XGsonBuilder.instance().fromJson(data, collectionType);
			if (!list.isEmpty()) {
				GraalvmScriptingFactory.Bindings bindings = aeiObjects.bindings()
						.putMember(GraalvmScriptingFactory.BINDING_NAME_SERIAL, this.serial)
						.putMember(GraalvmScriptingFactory.BINDING_NAME_PROCESS, this.process);
				for (SerialTextureItem o : list) {
					if ((!StringUtils.equalsIgnoreCase(o.getKey(), "number"))
							&& StringUtils.isNotEmpty(o.getScript())) {
						Source source = GraalvmScriptingFactory.functionalization(o.getScript());
						Optional<String> opt = GraalvmScriptingFactory.evalAsString(source, bindings);
						String s = opt.isPresent() ? opt.get() : "";
						itemResults.add(s);
					} else {
						itemResults.add("");
					}
				}
				for (int i = 0; i < list.size(); i++) {
					SerialTextureItem o = list.get(i);
					if ((StringUtils.equalsIgnoreCase(o.getKey(), "number")) && StringUtils.isNotEmpty(o.getScript())) {
						Source source = GraalvmScriptingFactory.functionalization(o.getScript());
						Optional<String> opt = GraalvmScriptingFactory.evalAsString(source, bindings);
						String s = opt.isPresent() ? opt.get() : "";
						itemResults.add(i, s);
					}
				}
				for (Object o : itemResults) {
					stringBuilder.append(Objects.toString(o, ""));
				}
				return stringBuilder.toString();
			}
		}
		return stringBuilder.toString();
	}

	public class Serial {

		// 输入文本,返回文本
		public String text(String str) {
			return str;
		}

		// 输入format日期格式,返回当前时间的中年的部分,按指定格式返回
		public String year(String format) {
			return DateFormatUtils.format(date, format);
		}

		// 输入format日期格式,返回work创建时间的中年的部分,按指定格式返回
		public String createYear(String format) {
			return DateFormatUtils.format(work.getCreateTime(), format);
		}

		// 返回当前时间月的部分,按指定格式返回
		public String month(String format) {
			return DateFormatUtils.format(date, format);
		}

		// 返回创建时间月的部分,按指定格式返回
		public String createMonth(String format) {
			return DateFormatUtils.format(work.getCreateTime(), format);
		}

		// 返回当前时间天的部分,按指定格式返回
		public String day(String format) {
			return DateFormatUtils.format(date, format);
		}

		// 返回创建时间天的部分,按指定格式返回
		public String createDay(String format) {
			return DateFormatUtils.format(work.getCreateTime(), format);
		}

		// 返回创建身份所在的组织
		public String unit() {
			return work.getCreatorUnit();
		}

		// 返回创建人员
		public String person() {
			return work.getCreatorPerson();
		}

		// 返回创建身份
		public String identity() {
			return work.getCreatorIdentity();
		}

		// 返回创建组织的指定组织属性
		public String unitAttribute(String name) throws Exception {
			String result = "";
			Organization org = new Organization(context);
			List<String> attributes = org.unitAttribute().listAttributeWithUnitWithName(work.getCreatorUnit(), name);
			if (ListTools.isNotEmpty(attributes)) {
				result = StringUtils.join(attributes, ",");
			}
			return result;
		}

		// 返回创建人员的指定属性
		public String personAttribute(String name) throws Exception {
			String result = "";
			Organization org = new Organization(context);
			List<String> attributes = org.personAttribute().listAttributeWithPersonWithName(work.getCreatorPerson(),
					name);
			if (ListTools.isNotEmpty(attributes)) {
				result = StringUtils.join(attributes, ",");
			}
			return result;
		}

		// 获取下一个编号值,输入为keys为编号依据,size为返回的长度,不足补零
		public String nextSerialNumber(List<Integer> keys, Integer size) throws Exception {
			String name = "";
			for (Integer i : keys) {
				name += itemResults.get(i).toString();
			}
			Integer number = this.nextNumber(name);
			if (size > 0) {
				return String.format("%0" + size + "d", number);
			} else {
				return number.toString();
			}
		}

		private Integer nextNumber(String name) throws Exception {
			if (StringUtils.isBlank(name)) {
				name = EMPTY_SYMBOL;
			}
			WrapInteger wrapInteger = ThisApplication.context().applications()
					.postQuery(x_processplatform_service_processing.class,
							Applications.joinQueryUri("work", "process", process.getId(), "name", name, "serial"), null,
							process.getApplication())
					.getData(WrapInteger.class);
			return wrapInteger.getValue();
		}
	}

	public class SerialTextureItem {

		private String key;
		private String script;

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getScript() {
			return script;
		}

		public void setScript(String script) {
			this.script = script;
		}
	}
}
