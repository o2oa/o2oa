package o2.collect.assemble.jaxrs.update;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.DateTools;

import o2.base.core.project.config.Config;

class ActionListNextVersion extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String version) throws Exception {
		/** 如果输入格式不对,强制版本设置 */
		if (!DateTools.isFormat(version, DateTools.patternCompact_yyyyMMddHHmmss)) {
			version = "20180101000000";
		}
		Date dateVersion = DateTools.parse(version, DateTools.formatCompact_yyyyMMddHHmmss);
		ActionResult<List<Wo>> result = new ActionResult<>();
		File dir = new File(Config.base(), "servers/webServer/o2server/update");
		FileFilter fileFilter = new RegexFileFilter(
				"^[1,2][0,9][0-9][0-9][0,1][0-9][0-3][0-9][0-5][0-9][0-5][0-9][0-5][0-9].txt$");
		List<Wo> wos = new ArrayList<>();
		for (File f : dir.listFiles(fileFilter)) {
			Date _d = DateTools.parse(FilenameUtils.getBaseName(f.getName()), DateTools.formatCompact_yyyyMMddHHmmss);
			if (_d.after(dateVersion)) {
				Wo wo = new Wo();
				wo.setVersion(FilenameUtils.getBaseName(f.getName()));
				wo.setDescriptionList(FileUtils.readLines(f));
				wo.setSize(0L);
				File file = new File(dir, FilenameUtils.getBaseName(f.getName()) + ".zip");
				if (file.exists()) {
					wo.setSize(file.length());
				}
				wos.add(wo);
			}
		}
		wos = wos.stream().sorted(Comparator.comparing(Wo::getVersion)).collect(Collectors.toList());
		result.setData(wos);
		return result;
	}

	public static class Wo extends GsonPropertyObject {
		private String version;
		private List<String> descriptionList;
		private Long size;

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public List<String> getDescriptionList() {
			return descriptionList;
		}

		public void setDescriptionList(List<String> descriptionList) {
			this.descriptionList = descriptionList;
		}

		public Long getSize() {
			return size;
		}

		public void setSize(Long size) {
			this.size = size;
		}

	}
}