package com.x.base.core.project.thread;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.Application;
import com.x.base.core.project.Applications;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.StringTools;

public class ThreadFactory {

	private static Logger logger = LoggerFactory.getLogger(ThreadFactory.class);

	private Applications applications;

	private AbstractContext context;

	private Type parameterType;

	private static final String THREAD = "thread";
	private static final String STOP = "stop";
	private static final String ALIVE = "alive";
	private static final String PARAMETER = "parameter";

	public ThreadFactory(AbstractContext context) throws Exception {
		this.context = context;
		this.applications = context.applications();
		this.group = new ThreadGroup("ThreadFactoryGroup-" + StringTools.uniqueToken());
		this.parameterType = new TypeToken<Map<Object, Object>>() {
		}.getType();
	}

	private static final String TARGET = "target";
	private ThreadGroup group;

	public void start(ParameterRunnable runnable, String name) {
		Thread thread = new Thread(group, runnable::run, name);
		thread.start();
	}

	public String alive(String name) throws Exception {
		List<Application> list = applications.get(context.clazz());
		List<String> values = new CopyOnWriteArrayList<>();
		CompletableFuture.allOf(CompletableFuture.runAsync(() -> {
			if (list.stream().anyMatch(o -> {
				try {
					return StringUtils.equals(o.getNode(), Config.node());
				} catch (Exception e) {
					logger.error(e);
				}
				return false;
			})) {
				values.add(aliveLocal(name));
			}
		}), CompletableFuture.runAsync(() -> values.addAll(aliveRemote(name, list))));
		return values.stream().filter(StringUtils::isNotBlank).collect(Collectors.joining(","));
	}

	public String aliveLocal(String name) {
		try {
			return find(name).isPresent() ? Config.node() : null;
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

	private List<String> aliveRemote(String name, List<Application> list) {
		final List<String> values = new CopyOnWriteArrayList<>();
		list.stream().filter(o -> {
			try {
				return !StringUtils.equals(o.getNode(), Config.node());
			} catch (Exception e) {
				logger.error(e);
			}
			return false;
		}).map(o -> CompletableFuture.supplyAsync(() -> {
			try {
				return applications.getQuery(o, Applications.joinQueryUri(THREAD, ALIVE, name))
						.getData(WrapString.class).getValue();
			} catch (Exception e) {
				logger.error(e);
			}
			return false;
		})).forEach(f -> {
			try {
				values.add(Objects.toString(f.get()));
			} catch (Exception e) {
				logger.error(e);
			}
		});
		return values;
	}

	public Optional<Map<Object, Object>> parameter(String name) throws Exception {
		List<Application> list = applications.get(context.clazz());
		final List<Map<Object, Object>> values = new CopyOnWriteArrayList<>();
		CompletableFuture.allOf(CompletableFuture.runAsync(() -> {
			if (list.stream().anyMatch(o -> {
				try {
					return StringUtils.equals(o.getNode(), Config.node());
				} catch (Exception e) {
					logger.error(e);
				}
				return false;
			})) {
				values.add(this.parameterLocal(name));
			}
		}), CompletableFuture.runAsync(() -> values.add(parameterRemote(name, list))));
		return values.stream().filter(Objects::nonNull).findFirst();
	}

	public Map<Object, Object> parameterLocal(String name) {
		Optional<Thread> optional = find(name);
		if (optional.isPresent()) {
			try {
				Object r = FieldUtils.readField(optional.get(), TARGET, true);
				if (r instanceof ParameterRunnable) {
					return ((ParameterRunnable) r).parameter;
				}
			} catch (IllegalAccessException e) {
				logger.error(e);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private Map<Object, Object> parameterRemote(String name, List<Application> list) {
		final List<Map<Object, Object>> values = new CopyOnWriteArrayList<>();
		list.stream().filter(o -> {
			try {
				return !StringUtils.equals(o.getNode(), Config.node());
			} catch (Exception e) {
				logger.error(e);
			}
			return false;
		}).map(o -> CompletableFuture.supplyAsync(() -> {
			try {
				JsonElement jsonElement = applications.getQuery(o, Applications.joinQueryUri(THREAD, PARAMETER, name))
						.getData();
				return XGsonBuilder.instance().fromJson(jsonElement, parameterType);
			} catch (Exception e) {
				logger.error(e);
			}
			return null;
		})).forEach(o -> {
			try {
				if (null != o.get()) {
					values.add((Map<Object, Object>) o.get());
				}
			} catch (Exception e) {
				logger.error(e);
			}
		});
		return values.stream().filter(Objects::nonNull).findFirst().orElse(null);
	}

	public Optional<Map<Object, Object>> stop(String name) throws Exception {
		List<Application> list = applications.get(context.clazz());
		final List<Map<Object, Object>> values = new CopyOnWriteArrayList<>();
		CompletableFuture.allOf(CompletableFuture.runAsync(() -> {
			if (list.stream().anyMatch(o -> {
				try {
					return StringUtils.equals(o.getNode(), Config.node());
				} catch (Exception e) {
					logger.error(e);
				}
				return false;
			})) {
				try {
					values.add(stopLocal(name));
				} catch (Exception e) {
					logger.error(e);
				}
			}
		}), CompletableFuture.runAsync(() -> values.add(stopRemote(name, list))));
		return values.stream().filter(Objects::nonNull).findFirst();
	}

	public Map<Object, Object> stopLocal(String name) {
		Optional<Thread> optional = find(name);
		if (optional.isPresent()) {
			try {
				Object r = FieldUtils.readField(optional.get(), TARGET, true);
				optional.get().interrupt();
				if (r instanceof ParameterRunnable) {
					return ((ParameterRunnable) r).parameter;
				}
			} catch (IllegalAccessException e) {
				logger.error(e);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private Map<Object, Object> stopRemote(String name, List<Application> list) {
		final List<Map<Object, Object>> values = new CopyOnWriteArrayList<>();
		list.stream().filter(o -> {
			try {
				return !StringUtils.equals(o.getNode(), Config.node());
			} catch (Exception e) {
				logger.error(e);
			}
			return false;
		}).map(o -> CompletableFuture.supplyAsync(() -> {
			try {
				JsonElement jsonElement = applications.getQuery(o, Applications.joinQueryUri(THREAD, STOP, name))
						.getData();
				return XGsonBuilder.instance().fromJson(jsonElement, parameterType);
			} catch (Exception e) {
				logger.error(e);
			}
			return null;
		})).forEach(o -> {
			try {
				if (null != o.get()) {
					values.add((Map<Object, Object>) o.get());
				}
			} catch (Exception e) {
				logger.error(e);
			}
		});
		return values.stream().filter(Objects::nonNull).findFirst().orElse(null);
	}

	private Optional<Thread> find(String name) {
		return Thread.getAllStackTraces().keySet().stream()
				.filter(o -> Objects.equals(o.getThreadGroup(), group) && StringUtils.equals(name, o.getName()))
				.findFirst();
	}

}
