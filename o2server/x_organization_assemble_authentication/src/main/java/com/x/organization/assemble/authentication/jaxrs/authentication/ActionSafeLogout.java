package com.x.organization.assemble.authentication.jaxrs.authentication;

import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.CenterServer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.http.TokenType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.core.entity.log.TokenThreshold;
import com.x.organization.core.entity.log.TokenThreshold_;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionSafeLogout extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionSafeLogout.class);

	ActionResult<Wo> execute(HttpServletRequest request, HttpServletResponse response, EffectivePerson effectivePerson)
			throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		TokenThreshold tokenThreshold = new TokenThreshold(effectivePerson.getDistinguishedName(), new Date());

		update(tokenThreshold);
		broadcast(tokenThreshold);

		ActionResult<Wo> result = new ActionResult<>();
		HttpToken httpToken = new HttpToken();
		httpToken.deleteToken(request, response);
		Wo wo = new Wo();
		wo.setTokenType(TokenType.anonymous);
		wo.setName(EffectivePerson.ANONYMOUS);
		result.setData(wo);
		return result;

	}

	private void update(TokenThreshold tokenThreshold) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EntityManager em = emc.beginTransaction(TokenThreshold.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<TokenThreshold> cq = cb.createQuery(TokenThreshold.class);
			Root<TokenThreshold> root = cq.from(TokenThreshold.class);
			Predicate p = cb.equal(root.get(TokenThreshold_.person), tokenThreshold.getPerson());
			cq.select(root).where(p).orderBy(cb.desc(root.get(TokenThreshold_.threshold)));
			List<TokenThreshold> os = em.createQuery(cq).setMaxResults(1).getResultList();
			if (os.isEmpty()) {
				emc.persist(tokenThreshold, CheckPersistType.all);
			} else {
				TokenThreshold fresh = os.get(0);
				if (fresh.getThreshold().before(tokenThreshold.getThreshold())) {
					fresh.setThreshold(tokenThreshold.getThreshold());
					emc.check(fresh, CheckPersistType.all);
				}
			}
			emc.commit();
		}
	}

	private void broadcast(TokenThreshold tokenThreshold) throws Exception {
		for (Entry<String, CenterServer> entry : Config.nodes().centerServers().orderedEntry()) {
			CipherConnectionAction.post(false, 4000, 8000,
					Config.url_x_program_center_jaxrs(entry, "tokenthreshold", "update"), tokenThreshold);
		}
	}

	@Schema(name = "com.x.organization.assemble.authentication.jaxrs.authentication.ActionSafeLogout$Wo")
	public static class Wo extends AbstractWoAuthentication {

		private static final long serialVersionUID = 4883354487268278719L;

	}

}