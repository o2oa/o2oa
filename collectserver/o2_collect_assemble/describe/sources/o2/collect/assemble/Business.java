package o2.collect.assemble;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.tools.Crypto;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import nl.captcha.Captcha;
import o2.base.core.project.config.Config;
import o2.collect.assemble.factory.AccountFactory;
import o2.collect.assemble.factory.CodeFactory;
import o2.collect.assemble.factory.DeviceFactory;
import o2.collect.assemble.factory.UnitFactory;
import o2.collect.assemble.jaxrs.collect.QueueTransmitReceive;
import o2.collect.core.entity.Code;
import o2.collect.core.entity.Code_;
import o2.collect.core.entity.Module;
import o2.collect.core.entity.Unit;
import o2.collect.core.entity.Unit_;

public class Business {

	private EntityManagerContainer emc;

	private Ehcache captchaCache = ApplicationCache.instance().getCache(Captcha.class);

	public Ehcache captchaCache() {
		return this.captchaCache;
	}

	private Ehcache codeCache = ApplicationCache.instance().getCache(Code.class);

	public Ehcache codeCache() {
		return this.codeCache;
	}

	private Ehcache moduleCache = ApplicationCache.instance().getCache(Module.class);

	public Ehcache moduleCache() {
		return this.moduleCache;
	}

	private Ehcache queueTransmitReceiveCache = ApplicationCache.instance()
			.getCache(QueueTransmitReceive.class.getName(), 5000, 60 * 60 * 5, 60 * 60 * 10);

	public Ehcache queueTransmitReceiveCache() {
		return this.queueTransmitReceiveCache;
	}

	public Business(EntityManagerContainer emc) throws Exception {
		this.emc = emc;
	}

	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}

	private CodeFactory code;

	public CodeFactory code() throws Exception {
		if (null == this.code) {
			this.code = new CodeFactory(this);
		}
		return code;
	}

	private UnitFactory unit;

	public UnitFactory unit() throws Exception {
		if (null == this.unit) {
			this.unit = new UnitFactory(this);
		}
		return unit;
	}

	private AccountFactory account;

	public AccountFactory account() throws Exception {
		if (null == this.account) {
			this.account = new AccountFactory(this);
		}
		return account;
	}

	private DeviceFactory device;

	public DeviceFactory device() throws Exception {
		if (null == this.device) {
			this.device = new DeviceFactory(this);
		}
		return device;
	}

	public Boolean validateCaptcha(String key, String answer) {
		if (StringUtils.isEmpty(key) || StringUtils.isEmpty(answer)) {
			return false;
		}
		Element element = captchaCache.get(key);
		if (null == element) {
			return false;
		}
		int distance = StringUtils.getLevenshteinDistance(answer, element.getObjectValue().toString());
		if (distance < 2) {
			captchaCache.remove(key);
			return true;
		} else {
			return false;
		}
	}

	public Unit validateUnit(String name, String password) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Unit.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Unit> cq = cb.createQuery(Unit.class);
		Root<Unit> root = cq.from(Unit.class);
		Predicate p = cb.equal(root.get(Unit_.name), name);
		cq.select(root).where(p);
		List<Unit> list = em.createQuery(cq).getResultList();
		if (list.size() == 1) {
			if (StringUtils.equals(Crypto.encrypt(password, Config.token().getKey()), list.get(0).getPassword())) {
				return list.get(0);
			}
		}
		return null;
	}

	public Boolean validateCode(String mobile, String answer, String meta, Boolean removeAfterUse) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Code.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Code> cq = cb.createQuery(Code.class);
		Root<Code> root = cq.from(Code.class);
		Predicate p = cb.equal(root.get(Code_.answer), answer);
		p = cb.and(p, cb.equal(root.get(Code_.mobile), mobile));
		if (StringUtils.isNotEmpty(meta)) {
			p = cb.and(p, cb.equal(root.get(Code_.meta), meta));
		}
		cq.select(root).where(p);
		List<Code> list = em.createQuery(cq).setMaxResults(1).getResultList();
		if (!list.isEmpty()) {
			if (removeAfterUse) {
				this.entityManagerContainer().beginTransaction(Code.class);
				em.remove(list.get(0));
				em.getTransaction().commit();
			}
			return true;
		}
		return false;
	}
}