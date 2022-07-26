package com.x.query.service.processing;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class Solr {

	private static final Logger LOGGER = LoggerFactory.getLogger(Solr.class);

	private CoreContainer coreContainer;

	private static Map<String, EmbeddedSolrServer> cores = new ConcurrentHashMap<>();

	private Path coreTemplateProcessPlatform;
	private Path coreTemplateCms;
	private Path home;

	private static final String CORENAME_FULLTEXT = "FULLTEXT";

	public EmbeddedSolrServer get(String type, String id) {
		String key = key(type, id);
		return cores.computeIfAbsent(key, s -> {
			try {
				Path source = StringUtils.equals(type, "processPlatfom") ? coreTemplateProcessPlatform
						: coreTemplateCms;
				Path target = home.resolve(key);
				if (!Files.exists(target)) {
					Files.createDirectories(target);
					Files.copy(source, target);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			return new EmbeddedSolrServer(coreContainer, key);
		});
	}

	private String key(String type, String id) {
		return type + "#" + id;
	}

	public void shutdown() {
		cores.entrySet().stream().forEach(o -> {
			try {
				o.getValue().close();
			} catch (IOException e) {
				LOGGER.error(e);
			}
		});
		this.coreContainer.shutdown();
	}

	public EmbeddedSolrServer fullText() {
		return cores.get(CORENAME_FULLTEXT);
	}

	public static class Builder {

		private Path coreTemplateProcessPlatform;
		private Path coreTemplateCms;
		private Path home;

		public Builder() {
			// nothing
		}

		public Builder coreTemplateProcessPlatform(Path path) {
			this.coreTemplateProcessPlatform = path;
			return this;
		}

		public Builder coreTemplateCms(Path path) {
			this.coreTemplateCms = path;
			return this;
		}

		public Builder home(Path path) {
			this.home = path;
			return this;
		}

		public Solr build() throws IOException, URISyntaxException {
			Solr solr = new Solr();
			solr.coreTemplateProcessPlatform = this.coreTemplateProcessPlatform;
			solr.coreTemplateCms = this.coreTemplateCms;
			solr.home = this.home;
			solr.home = Config.pathLocalRepositorySolr(false);
			if (!Files.exists(solr.home)) {
				Files.createDirectories(solr.home);
				Files.copy(Config.pathCommonsSolr(false), solr.home);
			}
			solr.coreContainer = new CoreContainer(solr.home, new Properties());
			solr.coreContainer.load();
			solr.coreContainer.getAllCoreNames()
					.forEach(s -> cores.put(s, new EmbeddedSolrServer(solr.coreContainer, s)));
			return solr;
		}

	}

}
