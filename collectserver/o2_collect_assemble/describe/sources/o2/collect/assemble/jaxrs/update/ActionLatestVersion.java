package o2.collect.assemble.jaxrs.update;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
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

class ActionLatestVersion extends BaseAction {

	private static String LATEST = "latest";

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setVersion(LATEST);
		File dir = new File(Config.base(), "servers/webServer/o2server/update");
		FileFilter fileFilter = new RegexFileFilter(
				"^[1,2][0,9][0-9][0-9][0,1][0-9][0-3][0-9][0-5][0-9][0-5][0-9][0-5][0-9].zip$");
		List<File> files = Arrays.stream(dir.listFiles(fileFilter))
				.sorted(Comparator.comparing(File::getName).reversed()).collect(Collectors.toList());
		wo.setSize(files.get(0).length());
		wo.setVersion(FilenameUtils.getBaseName(files.get(0).getName()));
		File urlFile = new File(dir, FilenameUtils.getBaseName(files.get(0).getName()) + ".url");
		if (urlFile.exists()) {
			wo.setUrl(FileUtils.readFileToString(urlFile));
		} else {
			wo.setUrl(Config.update().site() + "/o2server/update/" + FilenameUtils.getBaseName(files.get(0).getName())
					+ ".zip");
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends GsonPropertyObject {

		private String version;

		private Long size;

		private String url;

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public Long getSize() {
			return size;
		}

		public void setSize(Long size) {
			this.size = size;
		}
	}
}