package com.x.processplatform.assemble.surface.jaxrs.data;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.item.ItemConverter;
import com.x.base.core.entity.item.ItemType;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.DataItem;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.tools.DataHelper;

abstract class ActionBase {

	protected JsonElement getData(Business business, String job, String... paths) throws Exception {
		JsonElement jsonElement = null;
		List<DataItem> list = business.dataItem().listWithJobWithPath(job, paths);
		ItemConverter<DataItem> converter = new ItemConverter<>(DataItem.class);
		jsonElement = converter.assemble(list, paths.length);
		return jsonElement;
	}

	protected void updateData(Business business, Work work, JsonElement jsonElement, String... paths) throws Exception {
		ItemConverter<DataItem> converter = new ItemConverter<>(DataItem.class);
		List<DataItem> exists = business.dataItem().listWithJobWithPath(work.getJob(), paths);
		if (exists.isEmpty()) {
			throw new Exception(
					"data{job:" + work.getJob() + "} on path:" + StringUtils.join(paths, ".") + " is not existed.");
		}
		List<DataItem> currents = converter.disassemble(jsonElement, paths);
		List<DataItem> removes = converter.subtract(exists, currents);
		List<DataItem> adds = converter.subtract(currents, exists);
		business.entityManagerContainer().beginTransaction(DataItem.class);
		for (DataItem o : removes) {
			business.entityManagerContainer().remove(o);
		}
		for (DataItem o : adds) {
			this.fillDataItem(o, work);
			o.setCompleted(false);
			business.entityManagerContainer().persist(o);
		}
	}

	protected void createData(Business business, Work work, JsonElement jsonElement, String... paths) throws Exception {
		String[] parentPaths = new String[] { "", "", "", "", "", "", "", "" };
		String[] cursorPaths = new String[] { "", "", "", "", "", "", "", "" };
		for (int i = 0; i < paths.length - 1; i++) {
			parentPaths[i] = paths[i];
			cursorPaths[i] = paths[i];
		}
		cursorPaths[paths.length - 1] = paths[paths.length - 1];
		DataItem parent = business.dataItem().getWithJobWithPath(work.getJob(), parentPaths[0], parentPaths[1],
				parentPaths[2], parentPaths[3], parentPaths[4], parentPaths[5], parentPaths[6], parentPaths[7]);
		if (null == parent) {
			throw new Exception("parent not existed.");
		}
		DataItem cursor = business.dataItem().getWithJobWithPath(work.getJob(), cursorPaths[0], cursorPaths[1],
				cursorPaths[2], cursorPaths[3], cursorPaths[4], cursorPaths[5], cursorPaths[6], cursorPaths[7]);
		ItemConverter<DataItem> converter = new ItemConverter<>(DataItem.class);
		business.entityManagerContainer().beginTransaction(DataItem.class);
		if ((null != cursor) && cursor.getItemType().equals(ItemType.a)) {
			/* 向数组里面添加一个成员对象 */
			Integer index = business.dataItem().getArrayLastIndexWithJobWithPath(work.getJob(), paths);
			/* 新的路径开始 */
			String[] ps = new String[paths.length + 1];
			for (int i = 0; i < paths.length; i++) {
				ps[i] = paths[i];
			}
			ps[paths.length] = Integer.toString(index + 1);
			List<DataItem> adds = converter.disassemble(jsonElement, ps);
			DataHelper dataHelper = new DataHelper(business.entityManagerContainer(), work);
			for (DataItem o : adds) {
				dataHelper.fill(o);
				o.setCompleted(false);
				business.entityManagerContainer().persist(o);
			}
		} else if ((cursor == null) && parent.getItemType().equals(ItemType.o)) {
			/* 向parent对象添加一个属性值 */
			List<DataItem> adds = converter.disassemble(jsonElement, paths);
			for (DataItem o : adds) {
				this.fillDataItem(o, work);
				o.setCompleted(false);
				business.entityManagerContainer().persist(o);
			}
		} else {
			throw new Exception("unexpected post data with work" + work + ".path:" + StringUtils.join(paths, ".")
					+ "json:" + jsonElement);
		}
	}

	protected void fillDataItem(DataItem o, Work work) throws Exception {
		o.setJob(work.getJob());
		o.setSerial(work.getSerial());
		o.setTitle(work.getTitle());
		o.setApplication(work.getApplication());
		o.setApplicationName(work.getApplicationName());
		o.setProcess(work.getProcess());
		o.setProcessName(work.getProcessName());
		o.setCreatorCompany(work.getCreatorCompany());
		o.setCreatorDepartment(work.getCreatorDepartment());
		o.setCreatorIdentity(work.getCreatorIdentity());
		o.setCreatorPerson(work.getCreatorPerson());
		o.setStartTime(work.getStartTime());
		o.setCompleted(false);
	}

	protected void deleteData(Business business, Work work, String... paths) throws Exception {
		List<DataItem> exists = business.dataItem().listWithJobWithPath(work.getJob(), paths);
		if (exists.isEmpty()) {
			throw new Exception(
					"data{job:" + work.getJob() + "} on path:" + StringUtils.join(paths, ".") + " is not existed.");
		}
		business.entityManagerContainer().beginTransaction(DataItem.class);
		for (DataItem o : exists) {
			business.entityManagerContainer().remove(o);
		}
		if (paths.length > 0) {
			if (NumberUtils.isNumber(paths[paths.length - 1])) {
				int position = paths.length - 1;
				for (DataItem o : business.dataItem().listWithJobWithPathWithAfterLocation(work.getJob(),
						NumberUtils.toInt(paths[position]), paths)) {
					o.path(Integer.toString(o.pathLocation(position) - 1), position);
				}
			}
		}
	}

}