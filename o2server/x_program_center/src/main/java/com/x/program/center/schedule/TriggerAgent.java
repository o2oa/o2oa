package com.x.program.center.schedule;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.organization.core.express.Organization;
import com.x.program.center.Business;
import com.x.program.center.Context;
import com.x.program.center.ThisApplication;
import com.x.program.center.WebservicesClient;
import com.x.program.center.core.entity.Agent;
import com.x.program.center.core.entity.Agent_;

public class TriggerAgent implements Job {

	private static Logger logger = LoggerFactory.getLogger(TriggerAgent.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			List<Pair> list = this.list(business);
			list.stream().forEach(p -> {
				this.trigger(business, p);
			});
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}

	private void trigger(Business business, Pair pair) {
		try {
			if (StringUtils.isEmpty(pair.getCron())) {
				return;
			}
			Date date = this.cron(pair.getCron(), pair.getLastStartTime());
			if (date.before(new Date())) {
				logger.info("trigger agent : {}, cron: {}, appointment time:{}, last start time: {}.", pair.getName(),
						pair.getCron(), DateTools.format(date),
						(pair.getLastStartTime() == null ? "" : DateTools.format(pair.getLastStartTime())));
				ExecuteThread thread = new ExecuteThread(pair.getId());
				thread.start();
			} else {
				if ((null == pair.getAppointmentTime())
						|| (!DateUtils.truncatedEquals(date, pair.getAppointmentTime(), Calendar.SECOND))) {
					Agent agent = business.entityManagerContainer().find(pair.getId(), Agent.class);
					if (null != agent) {
						business.entityManagerContainer().beginTransaction(Agent.class);
						agent.setAppointmentTime(date);
						business.entityManagerContainer().commit();
					}
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private Date cron(String expression, Date lastEndDate) throws Exception {
		CronExpression cron = new CronExpression(expression);
		return cron.getNextValidTimeAfter(lastEndDate == null ? DateTools.parse("2018-01-01 00:00:00") : lastEndDate);
	}

	private List<Pair> list(Business business) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Agent.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Agent> root = cq.from(Agent.class);
		Path<String> path_id = root.get(Agent_.id);
		Path<String> path_name = root.get(Agent_.name);
		Path<String> path_cron = root.get(Agent_.cron);
		Path<Date> path_lastEndTime = root.get(Agent_.lastEndTime);
		Path<Date> path_lastStartTime = root.get(Agent_.lastStartTime);
		Path<Date> path_appointmentTime = root.get(Agent_.appointmentTime);
		Predicate p = cb.equal(root.get(Agent_.enable), true);
		List<Tuple> list = em.createQuery(cq
				.multiselect(path_id, path_name, path_cron, path_lastEndTime, path_lastStartTime, path_appointmentTime)
				.where(p)).getResultList();
		List<Pair> pairs = list.stream().map(o -> {
			return new Pair(o.get(path_id), o.get(path_name), o.get(path_cron), o.get(path_lastEndTime),
					o.get(path_lastStartTime), o.get(path_appointmentTime));
		}).distinct().collect(Collectors.toList());
		return pairs;
	}

	class Pair {
		Pair(String id, String name, String cron, Date lastEndTime, Date lastStartTime, Date appointmentTime) {
			this.id = id;
			this.name = name;
			this.cron = cron;
			this.lastEndTime = lastEndTime;
			this.lastStartTime = lastStartTime;
			this.appointmentTime = appointmentTime;
		}

		private String id;

		private String name;

		private String cron;

		private Date lastEndTime;

		private Date lastStartTime;

		private Date appointmentTime;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getCron() {
			return cron;
		}

		public void setCron(String cron) {
			this.cron = cron;
		}

		public Date getLastEndTime() {
			return lastEndTime;
		}

		public void setLastEndTime(Date lastEndTime) {
			this.lastEndTime = lastEndTime;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Date getLastStartTime() {
			return lastStartTime;
		}

		public void setLastStartTime(Date lastStartTime) {
			this.lastStartTime = lastStartTime;
		}

		public Date getAppointmentTime() {
			return appointmentTime;
		}

		public void setAppointmentTime(Date appointmentTime) {
			this.appointmentTime = appointmentTime;
		}

	}

	public class ExecuteThread extends Thread {

		private String agentId;

		private static final String BINDING_RESOURCES = "resources";

		public ExecuteThread(String agentId) {
			this.agentId = agentId;
		}

		private void eval(String text) throws Exception {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				ScriptEngineManager manager = new ScriptEngineManager();
				ScriptEngine engine = manager.getEngineByName("JavaScript");
				ScriptContext newContext = new SimpleScriptContext();
				Bindings engineScope = newContext.getBindings(ScriptContext.ENGINE_SCOPE);
				Resources resources = new Resources();
				resources.setEntityManagerContainer(emc);
				resources.setContext(ThisApplication.context());
				resources.setOrganization(new Organization(ThisApplication.context()));
				resources.setWebservicesClient(new WebservicesClient());
				engineScope.put(BINDING_RESOURCES, resources);
				engine.eval(text, newContext);
			}
		}

		public void run() {
			String text = "";
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Agent agent = emc.find(agentId, Agent.class);
				if (null != agent) {
					text = agent.getText();
					emc.beginTransaction(Agent.class);
					agent.setLastStartTime(new Date());
					emc.commit();
				}
			} catch (Exception e) {
				logger.error(e);
			}
			if (StringUtils.isNotEmpty(text)) {
				try {
					this.eval(text);
				} catch (Exception e) {
					logger.error(e);
				}
			}
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Agent agent = emc.find(agentId, Agent.class);
				if (null != agent) {
					emc.beginTransaction(Agent.class);
					agent.setLastEndTime(new Date());
					emc.commit();
				}
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}

	public static class Resources {
		private EntityManagerContainer entityManagerContainer;
		private Context context;
		private Organization organization;
		private WebservicesClient webservicesClient;

		public static String RESOURCES_BINDING_NAME = "resources";

		public EntityManagerContainer getEntityManagerContainer() {
			return entityManagerContainer;
		}

		public void setEntityManagerContainer(EntityManagerContainer entityManagerContainer) {
			this.entityManagerContainer = entityManagerContainer;
		}

		public Context getContext() {
			return context;
		}

		public void setContext(Context context) {
			this.context = context;
		}

		public Organization getOrganization() {
			return organization;
		}

		public void setOrganization(Organization organization) {
			this.organization = organization;
		}

		public WebservicesClient getWebservicesClient() {
			return webservicesClient;
		}

		public void setWebservicesClient(WebservicesClient webservicesClient) {
			this.webservicesClient = webservicesClient;
		}
	}

}