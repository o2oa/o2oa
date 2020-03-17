//package com.x.processplatform.assemble.designer.jaxrs.input;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//
//import javax.persistence.EntityManager;
//import javax.persistence.criteria.CriteriaBuilder;
//import javax.persistence.criteria.CriteriaQuery;
//import javax.persistence.criteria.Predicate;
//import javax.persistence.criteria.Root;
//
//import org.apache.commons.beanutils.BeanUtils;
//import org.apache.commons.collections4.ListUtils;
//import org.apache.commons.lang3.StringUtils;
//
//import com.google.gson.JsonElement;
//import com.x.base.core.container.EntityManagerContainer;
//import com.x.base.core.container.factory.EntityManagerContainerFactory;
//import com.x.base.core.entity.JpaObject;
//import com.x.base.core.entity.item.ItemConverter;
//import com.x.base.core.project.annotation.FieldDescribe;
//import com.x.base.core.project.bean.WrapCopier;
//import com.x.base.core.project.gson.GsonPropertyObject;
//import com.x.base.core.project.http.ActionResult;
//import com.x.base.core.project.http.EffectivePerson;
//import com.x.base.core.project.jaxrs.WoId;
//import com.x.base.core.project.logger.Logger;
//import com.x.base.core.project.logger.LoggerFactory;
//import com.x.base.core.project.tools.ListTools;
//import com.x.base.core.project.tools.StringTools;
//import com.x.processplatform.assemble.designer.Business;
//import com.x.processplatform.assemble.designer.WrapAgent;
//import com.x.processplatform.assemble.designer.WrapApplication;
//import com.x.processplatform.assemble.designer.WrapApplicationDict;
//import com.x.processplatform.assemble.designer.WrapBegin;
//import com.x.processplatform.assemble.designer.WrapCancel;
//import com.x.processplatform.assemble.designer.WrapChoice;
//import com.x.processplatform.assemble.designer.WrapDelay;
//import com.x.processplatform.assemble.designer.WrapEmbed;
//import com.x.processplatform.assemble.designer.WrapEnd;
//import com.x.processplatform.assemble.designer.WrapForm;
//import com.x.processplatform.assemble.designer.WrapInvoke;
//import com.x.processplatform.assemble.designer.WrapManual;
//import com.x.processplatform.assemble.designer.WrapMerge;
//import com.x.processplatform.assemble.designer.WrapMessage;
//import com.x.processplatform.assemble.designer.WrapParallel;
//import com.x.processplatform.assemble.designer.WrapProcess;
//import com.x.processplatform.assemble.designer.WrapRoute;
//import com.x.processplatform.assemble.designer.WrapScript;
//import com.x.processplatform.assemble.designer.WrapService;
//import com.x.processplatform.assemble.designer.WrapSplit;
//import com.x.processplatform.core.entity.element.Agent;
//import com.x.processplatform.core.entity.element.Application;
//import com.x.processplatform.core.entity.element.ApplicationDict;
//import com.x.processplatform.core.entity.element.ApplicationDictItem;
//import com.x.processplatform.core.entity.element.ApplicationDictItem_;
//import com.x.processplatform.core.entity.element.ApplicationDictLobItem;
//import com.x.processplatform.core.entity.element.Begin;
//import com.x.processplatform.core.entity.element.Cancel;
//import com.x.processplatform.core.entity.element.Choice;
//import com.x.processplatform.core.entity.element.Delay;
//import com.x.processplatform.core.entity.element.Embed;
//import com.x.processplatform.core.entity.element.End;
//import com.x.processplatform.core.entity.element.Form;
//import com.x.processplatform.core.entity.element.Invoke;
//import com.x.processplatform.core.entity.element.Manual;
//import com.x.processplatform.core.entity.element.Merge;
//import com.x.processplatform.core.entity.element.Message;
//import com.x.processplatform.core.entity.element.Parallel;
//import com.x.processplatform.core.entity.element.Process;
//import com.x.processplatform.core.entity.element.Route;
//import com.x.processplatform.core.entity.element.Script;
//import com.x.processplatform.core.entity.element.Service;
//import com.x.processplatform.core.entity.element.Split;
//
//import net.sf.ehcache.Element;
//
//class ActionWrite extends BaseAction {
//
//	private static Logger logger = LoggerFactory.getLogger(ActionWrite.class);
//
//	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
//		logger.debug(effectivePerson, "jsonElement:{}.", jsonElement);
//		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//			ActionResult<Wo> result = new ActionResult<>();
//			Wo wo = new Wo();
//			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
//			String flag = wi.getFlag();
//			if (StringUtils.isEmpty(flag)) {
//				throw new ExceptionFlagEmpty();
//			}
//			Business business = new Business(emc);
//			Element element = this.inputCache.get(flag);
//			if (null == element) {
//				throw new ExceptionFlagNotExist(flag);
//			}
//			WrapApplication wrapApplication = (WrapApplication) element.getObjectValue();
//			emc.beginTransaction(Application.class);
//			emc.beginTransaction(Script.class);
//			emc.beginTransaction(Form.class);
//			emc.beginTransaction(ApplicationDict.class);
//			emc.beginTransaction(ApplicationDictItem.class);
//			emc.beginTransaction(ApplicationDictLobItem.class);
//			emc.beginTransaction(Process.class);
//			emc.beginTransaction(Agent.class);
//			emc.beginTransaction(Begin.class);
//			emc.beginTransaction(Cancel.class);
//			emc.beginTransaction(Choice.class);
//			emc.beginTransaction(Delay.class);
//			emc.beginTransaction(Embed.class);
//			emc.beginTransaction(End.class);
//			emc.beginTransaction(Invoke.class);
//			emc.beginTransaction(Manual.class);
//			emc.beginTransaction(Merge.class);
//			emc.beginTransaction(Message.class);
//			emc.beginTransaction(Parallel.class);
//			emc.beginTransaction(Service.class);
//			emc.beginTransaction(Split.class);
//			emc.beginTransaction(Route.class);
//			if (Objects.equals(Method.create, wi.getMethod()) || Objects.equals(Method.cover, wi.getMethod())) {
//				switch (wi.getMethod()) {
//				case create:
//					this.adjustForCreate(business, wrapApplication);
//					this.create(business, wrapApplication);
//					wo.setId(wrapApplication.getId());
//					break;
//				case cover:
//					this.adjustForCover(business, wrapApplication);
//					this.cover(business, wrapApplication);
//					wo.setId(wrapApplication.getId());
//					break;
//				default:
//					break;
//				}
//			}
//			emc.commit();
//			result.setData(wo);
//			return result;
//		}
//	}
//
//	private void adjustForCover(Business business, WrapApplication wrapApplication) throws Exception {
//		Application exist = business.entityManagerContainer().find(wrapApplication.getId(), Application.class);
//		if (null == exist) {
//			throw new ExceptionApplicationNotExist(wrapApplication.getId());
//		}
//		if (!StringUtils.equals(wrapApplication.getId(), exist.getId())) {
//			wrapApplication.changeId(wrapApplication.getId(), exist.getId());
//			wrapApplication
//					.setName(this.idleName(business, wrapApplication.getName(), Application.class, exist.getId()));
//			wrapApplication
//					.setAlias(this.idleAlias(business, wrapApplication.getAlias(), Application.class, exist.getId()));
//		}
//		for (CompareElement c : this.compare(business, wrapApplication.getFormList(),
//				business.form().listObjectWithApplication(exist.getId()))) {
//			if ((null != c.getW()) && (null != c.getT())) {
//				if (!StringUtils.equals(c.getW().getId(), c.getT().getId())) {
//					wrapApplication.changeId(c.getW().getId(), c.getT().getId());
//					BeanUtils.setProperty(c.getW(), "name", this.idleNameWithApplication(business, exist.getId(),
//							BeanUtils.getProperty(c.getW(), "name"), c.getT().getClass(), c.getT().getId()));
//					BeanUtils.setProperty(c.getW(), "alias", this.idleAliasWithApplication(business, exist.getId(),
//							BeanUtils.getProperty(c.getW(), "alias"), c.getT().getClass(), c.getT().getId()));
//				}
//			}
//		}
//		for (CompareElement c : this.compare(business, wrapApplication.getScriptList(),
//				business.script().listObjectWithApplication(exist.getId()))) {
//			if ((null != c.getW()) && (null != c.getT())) {
//				if (!StringUtils.equals(c.getW().getId(), c.getT().getId())) {
//					wrapApplication.changeId(c.getW().getId(), c.getT().getId());
//					BeanUtils.setProperty(c.getW(), "name", this.idleNameWithApplication(business, exist.getId(),
//							BeanUtils.getProperty(c.getW(), "name"), c.getT().getClass(), c.getT().getId()));
//					BeanUtils.setProperty(c.getW(), "alias", this.idleAliasWithApplication(business, exist.getId(),
//							BeanUtils.getProperty(c.getW(), "alias"), c.getT().getClass(), c.getT().getId()));
//				}
//			}
//		}
//		for (CompareElement c : this.compare(business, wrapApplication.getProcessList(),
//				business.process().listObjectWithApplication(exist.getId()))) {
//			if ((null != c.getW()) && (null != c.getT())) {
//				WrapProcess wrapProcess = (WrapProcess) c.getW();
//				Process process = (Process) c.getT();
//				if (!StringUtils.equals(wrapProcess.getId(), process.getId())) {
//					wrapApplication.changeId(wrapProcess.getId(), process.getId());
//					wrapProcess.setName(this.idleNameWithApplication(business, exist.getId(), wrapProcess.getName(),
//							Process.class, process.getId()));
//					wrapProcess.setAlias(this.idleAliasWithApplication(business, exist.getId(), wrapProcess.getName(),
//							Process.class, process.getId()));
//				}
//				for (CompareElement ce : this.compare(business, wrapProcess.getAgentList(),
//						business.agent().listObjectWithProcess(process.getId()))) {
//					if (!StringUtils.equals(ce.getW().getId(), ce.getT().getId())) {
//						wrapApplication.changeId(ce.getW().getId(), ce.getT().getId());
//					}
//				}
//				for (CompareElement ce : this.compare(business,
//						ListTools.trim(ListTools.toList(wrapProcess.getBegin()), true, true),
//						ListTools.trim(ListTools.toList(business.begin().getObjectWithProcess(process.getId())), true,
//								true))) {
//					if (!StringUtils.equals(ce.getW().getId(), ce.getT().getId())) {
//						wrapApplication.changeId(ce.getW().getId(), ce.getT().getId());
//					}
//				}
//				for (CompareElement ce : this.compare(business, wrapProcess.getCancelList(),
//						business.cancel().listObjectWithProcess(process.getId()))) {
//					if (!StringUtils.equals(ce.getW().getId(), ce.getT().getId())) {
//						wrapApplication.changeId(ce.getW().getId(), ce.getT().getId());
//					}
//				}
//				for (CompareElement ce : this.compare(business, wrapProcess.getChoiceList(),
//						business.choice().listObjectWithProcess(process.getId()))) {
//					if (!StringUtils.equals(ce.getW().getId(), ce.getT().getId())) {
//						wrapApplication.changeId(ce.getW().getId(), ce.getT().getId());
//					}
//				}
//				for (CompareElement ce : this.compare(business, wrapProcess.getDelayList(),
//						business.delay().listObjectWithProcess(process.getId()))) {
//					if (!StringUtils.equals(ce.getW().getId(), ce.getT().getId())) {
//						wrapApplication.changeId(ce.getW().getId(), ce.getT().getId());
//					}
//				}
//				for (CompareElement ce : this.compare(business, wrapProcess.getEmbedList(),
//						business.embed().listObjectWithProcess(process.getId()))) {
//					if (!StringUtils.equals(ce.getW().getId(), ce.getT().getId())) {
//						wrapApplication.changeId(ce.getW().getId(), ce.getT().getId());
//					}
//				}
//				for (CompareElement ce : this.compare(business, wrapProcess.getEndList(),
//						business.end().listObjectWithProcess(process.getId()))) {
//					if (!StringUtils.equals(ce.getW().getId(), ce.getT().getId())) {
//						wrapApplication.changeId(ce.getW().getId(), ce.getT().getId());
//					}
//				}
//				for (CompareElement ce : this.compare(business, wrapProcess.getInvokeList(),
//						business.invoke().listObjectWithProcess(process.getId()))) {
//					if (!StringUtils.equals(ce.getW().getId(), ce.getT().getId())) {
//						wrapApplication.changeId(ce.getW().getId(), ce.getT().getId());
//					}
//				}
//				for (CompareElement ce : this.compare(business, wrapProcess.getManualList(),
//						business.manual().listObjectWithProcess(process.getId()))) {
//					if (!StringUtils.equals(ce.getW().getId(), ce.getT().getId())) {
//						wrapApplication.changeId(ce.getW().getId(), ce.getT().getId());
//					}
//				}
//				for (CompareElement ce : this.compare(business, wrapProcess.getMergeList(),
//						business.merge().listObjectWithProcess(process.getId()))) {
//					if (!StringUtils.equals(ce.getW().getId(), ce.getT().getId())) {
//						wrapApplication.changeId(ce.getW().getId(), ce.getT().getId());
//					}
//				}
//				for (CompareElement ce : this.compare(business, wrapProcess.getMessageList(),
//						business.message().listObjectWithProcess(process.getId()))) {
//					if (!StringUtils.equals(ce.getW().getId(), ce.getT().getId())) {
//						wrapApplication.changeId(ce.getW().getId(), ce.getT().getId());
//					}
//				}
//				for (CompareElement ce : this.compare(business, wrapProcess.getParallelList(),
//						business.parallel().listObjectWithProcess(process.getId()))) {
//					if (!StringUtils.equals(ce.getW().getId(), ce.getT().getId())) {
//						wrapApplication.changeId(ce.getW().getId(), ce.getT().getId());
//					}
//				}
//				for (CompareElement ce : this.compare(business, wrapProcess.getServiceList(),
//						business.service().listObjectWithProcess(process.getId()))) {
//					if (!StringUtils.equals(ce.getW().getId(), ce.getT().getId())) {
//						wrapApplication.changeId(ce.getW().getId(), ce.getT().getId());
//					}
//				}
//				for (CompareElement ce : this.compare(business, wrapProcess.getSplitList(),
//						business.split().listObjectWithProcess(process.getId()))) {
//					if (!StringUtils.equals(ce.getW().getId(), ce.getT().getId())) {
//						wrapApplication.changeId(ce.getW().getId(), ce.getT().getId());
//					}
//				}
//				for (CompareElement ce : this.compare(business, wrapProcess.getRouteList(),
//						business.route().listObjectWithProcess(process.getId()))) {
//					if (!StringUtils.equals(ce.getW().getId(), ce.getT().getId())) {
//						wrapApplication.changeId(ce.getW().getId(), ce.getT().getId());
//					}
//				}
//			}
//		}
//
//		for (CompareElement c : this.compare(business, wrapApplication.getApplicationDictList(),
//				business.applicationDict().listObjectWithApplication(exist.getId()))) {
//			if ((null != c.getW()) && (null != c.getT())) {
//				if (!StringUtils.equals(c.getW().getId(), c.getT().getId())) {
//					wrapApplication.changeId(c.getW().getId(), c.getT().getId());
//					BeanUtils.setProperty(c.getW(), "name", this.idleNameWithApplication(business, exist.getId(),
//							BeanUtils.getProperty(c.getW(), "name"), c.getT().getClass(), c.getT().getId()));
//					BeanUtils.setProperty(c.getW(), "alias", this.idleAliasWithApplication(business, exist.getId(),
//							BeanUtils.getProperty(c.getW(), "alias"), c.getT().getClass(), c.getT().getId()));
//				}
//			}
//		}
//		wrapApplication.consistentApplication();
//	}
//
//	private void cover(Business business, WrapApplication wrapApplication) throws Exception {
//		Application application = business.entityManagerContainer().find(wrapApplication.getId(), Application.class);
//		List<JpaObject> persistObjects = new ArrayList<>();
//		List<JpaObject> removeObjects = new ArrayList<>();
//		if (null == application) {
//			throw new ExceptionApplicationNotExist(wrapApplication.getId());
//		}
//		WrapApplication.copier.copy(wrapApplication, application);
//		List<String> ids = new ArrayList<>();
//		/** form清除 */
//		ids = ListTools.extractProperty(wrapApplication.getFormList(), JpaObject.id_FIELDNAME, String.class, true,
//				true);
//		for (Form o : this.listObjectWithApplication(business, application, Form.class)) {
//			if (!ListTools.contains(ids, o.getId())) {
//				removeObjects.add(o);
//			}
//		}
//		/** script清除 */
//		ids = ListTools.extractProperty(wrapApplication.getScriptList(), JpaObject.id_FIELDNAME, String.class, true,
//				true);
//		for (Script o : this.listObjectWithApplication(business, application, Script.class)) {
//			if (!ListTools.contains(ids, o.getId())) {
//				removeObjects.add(o);
//			}
//		}
//		/** applicationDict清除 */
//		ids = ListTools.extractProperty(wrapApplication.getApplicationDictList(), JpaObject.id_FIELDNAME, String.class,
//				true, true);
//		for (ApplicationDict o : this.listObjectWithApplication(business, application, ApplicationDict.class)) {
//			if (!ListTools.contains(ids, o.getId())) {
//				removeObjects.add(o);
//				EntityManager em = business.entityManagerContainer().get(ApplicationDictItem.class);
//				CriteriaBuilder cb = em.getCriteriaBuilder();
//				CriteriaQuery<ApplicationDictItem> cq = cb.createQuery(ApplicationDictItem.class);
//				Root<ApplicationDictItem> root = cq.from(ApplicationDictItem.class);
//				Predicate p = cb.equal(root.get(ApplicationDictItem_.applicationDict), o.getId());
//				cq.select(root).where(p);
//				List<ApplicationDictItem> os = em.createQuery(cq).getResultList();
//				for (ApplicationDictItem item : os) {
//					if (item.isLobItem()) {
//						/** 删除关联的lob */
//						ApplicationDictLobItem lob = business.entityManagerContainer().find(item.getLobItem(),
//								ApplicationDictLobItem.class);
//						if (null != lob) {
//							removeObjects.add(lob);
//						}
//					}
//					removeObjects.add(item);
//				}
//			}
//		}
//		/** process清除 */
//		ids = ListTools.extractProperty(wrapApplication.getProcessList(), JpaObject.id_FIELDNAME, String.class, true,
//				true);
//		for (Process o : this.listObjectWithApplication(business, application, Process.class)) {
//			if (!ListTools.contains(ids, o.getId())) {
//				removeObjects.add(o);
//				removeObjects.addAll(business.agent().listObjectWithProcess(o.getId()));
//				removeObjects.add(business.begin().getObjectWithProcess(o.getId()));
//				removeObjects.addAll(business.cancel().listObjectWithProcess(o.getId()));
//				removeObjects.addAll(business.choice().listObjectWithProcess(o.getId()));
//				removeObjects.addAll(business.delay().listObjectWithProcess(o.getId()));
//				removeObjects.addAll(business.embed().listObjectWithProcess(o.getId()));
//				removeObjects.addAll(business.end().listObjectWithProcess(o.getId()));
//				removeObjects.addAll(business.invoke().listObjectWithProcess(o.getId()));
//				removeObjects.addAll(business.manual().listObjectWithProcess(o.getId()));
//				removeObjects.addAll(business.merge().listObjectWithProcess(o.getId()));
//				removeObjects.addAll(business.message().listObjectWithProcess(o.getId()));
//				removeObjects.addAll(business.parallel().listObjectWithProcess(o.getId()));
//				removeObjects.addAll(business.service().listObjectWithProcess(o.getId()));
//				removeObjects.addAll(business.split().listObjectWithProcess(o.getId()));
//				removeObjects.addAll(business.route().listObjectWithProcess(o.getId()));
//			} else {
//				for (WrapProcess wrapProcess : wrapApplication.getProcessList()) {
//					if (StringUtils.equals(wrapProcess.getId(), o.getId())) {
//						List<String> processElementIds = new ArrayList<>();
//						processElementIds = ListTools.extractProperty(wrapProcess.getAgentList(),
//								JpaObject.id_FIELDNAME, String.class, true, true);
//						for (Agent el : business.agent().listObjectWithProcess(o.getId())) {
//							if (!processElementIds.contains(el.getId())) {
//								removeObjects.add(el);
//							}
//						}
//						processElementIds = ListTools.extractProperty(
//								ListTools.trim(null, true, true, wrapProcess.getBegin()), JpaObject.id_FIELDNAME,
//								String.class, true, true);
//						for (Begin el : ListTools.trim(null, true, true,
//								business.begin().getObjectWithProcess(o.getId()))) {
//							if (!processElementIds.contains(el.getId())) {
//								removeObjects.add(el);
//							}
//						}
//						processElementIds = ListTools.extractProperty(wrapProcess.getCancelList(),
//								JpaObject.id_FIELDNAME, String.class, true, true);
//						for (Cancel el : business.cancel().listObjectWithProcess(o.getId())) {
//							if (!processElementIds.contains(el.getId())) {
//								removeObjects.add(el);
//							}
//						}
//						processElementIds = ListTools.extractProperty(wrapProcess.getChoiceList(),
//								JpaObject.id_FIELDNAME, String.class, true, true);
//						for (Choice el : business.choice().listObjectWithProcess(o.getId())) {
//							if (!processElementIds.contains(el.getId())) {
//								removeObjects.add(el);
//							}
//						}
//						processElementIds = ListTools.extractProperty(wrapProcess.getDelayList(),
//								JpaObject.id_FIELDNAME, String.class, true, true);
//						for (Delay el : business.delay().listObjectWithProcess(o.getId())) {
//							if (!processElementIds.contains(el.getId())) {
//								removeObjects.add(el);
//							}
//						}
//						processElementIds = ListTools.extractProperty(wrapProcess.getEmbedList(),
//								JpaObject.id_FIELDNAME, String.class, true, true);
//						for (Embed el : business.embed().listObjectWithProcess(o.getId())) {
//							if (!processElementIds.contains(el.getId())) {
//								removeObjects.add(el);
//							}
//						}
//						processElementIds = ListTools.extractProperty(wrapProcess.getEndList(), JpaObject.id_FIELDNAME,
//								String.class, true, true);
//						for (End el : business.end().listObjectWithProcess(o.getId())) {
//							if (!processElementIds.contains(el.getId())) {
//								removeObjects.add(el);
//							}
//						}
//						processElementIds = ListTools.extractProperty(wrapProcess.getInvokeList(),
//								JpaObject.id_FIELDNAME, String.class, true, true);
//						for (Invoke el : business.invoke().listObjectWithProcess(o.getId())) {
//							if (!processElementIds.contains(el.getId())) {
//								removeObjects.add(el);
//							}
//						}
//						processElementIds = ListTools.extractProperty(wrapProcess.getManualList(),
//								JpaObject.id_FIELDNAME, String.class, true, true);
//						for (Manual el : business.manual().listObjectWithProcess(o.getId())) {
//							if (!processElementIds.contains(el.getId())) {
//								removeObjects.add(el);
//							}
//						}
//						processElementIds = ListTools.extractProperty(wrapProcess.getMergeList(),
//								JpaObject.id_FIELDNAME, String.class, true, true);
//						for (Merge el : business.merge().listObjectWithProcess(o.getId())) {
//							if (!processElementIds.contains(el.getId())) {
//								removeObjects.add(el);
//							}
//						}
//						processElementIds = ListTools.extractProperty(wrapProcess.getMessageList(),
//								JpaObject.id_FIELDNAME, String.class, true, true);
//						for (Message el : business.message().listObjectWithProcess(o.getId())) {
//							if (!processElementIds.contains(el.getId())) {
//								removeObjects.add(el);
//							}
//						}
//						processElementIds = ListTools.extractProperty(wrapProcess.getParallelList(),
//								JpaObject.id_FIELDNAME, String.class, true, true);
//						for (Parallel el : business.parallel().listObjectWithProcess(o.getId())) {
//							if (!processElementIds.contains(el.getId())) {
//								removeObjects.add(el);
//							}
//						}
//						processElementIds = ListTools.extractProperty(wrapProcess.getServiceList(),
//								JpaObject.id_FIELDNAME, String.class, true, true);
//						for (Service el : business.service().listObjectWithProcess(o.getId())) {
//							if (!processElementIds.contains(el.getId())) {
//								removeObjects.add(el);
//							}
//						}
//						processElementIds = ListTools.extractProperty(wrapProcess.getSplitList(),
//								JpaObject.id_FIELDNAME, String.class, true, true);
//						for (Split el : business.split().listObjectWithProcess(o.getId())) {
//							if (!processElementIds.contains(el.getId())) {
//								removeObjects.add(el);
//							}
//						}
//						processElementIds = ListTools.extractProperty(wrapProcess.getRouteList(),
//								JpaObject.id_FIELDNAME, String.class, true, true);
//						for (Route el : business.route().listObjectWithProcess(o.getId())) {
//							if (!processElementIds.contains(el.getId())) {
//								removeObjects.add(el);
//							}
//						}
//					}
//				}
//			}
//		}
//		/** form生成 */
//		for (WrapForm w : wrapApplication.getFormList()) {
//			Form o = business.entityManagerContainer().find(w.getId(), Form.class);
//			if (null != o) {
//				WrapForm.copier.copy(w, o);
//			} else {
//				persistObjects.add(WrapForm.copier.copy(w));
//			}
//		}
//		/** script生成 */
//		for (WrapScript w : wrapApplication.getScriptList()) {
//			Script o = business.entityManagerContainer().find(w.getId(), Script.class);
//			if (null != o) {
//				WrapScript.copier.copy(w, o);
//			} else {
//				persistObjects.add(WrapScript.copier.copy(w));
//			}
//		}
//		/** applicationDict生成 */
//		for (WrapApplicationDict w : wrapApplication.getApplicationDictList()) {
//			ApplicationDict o = business.entityManagerContainer().find(w.getId(), ApplicationDict.class);
//			ItemConverter<ApplicationDictItem> converter = new ItemConverter<>(ApplicationDictItem.class);
//			List<ApplicationDictItem> items = converter.disassemble(w.getData());
//			if (null != o) {
//				WrapApplicationDict.copier.copy(w, o);
//				for (ApplicationDictItem item : this.listApplicationDictItem(business, o)) {
//					removeObjects.add(item);
//				}
//			} else {
//				o = WrapApplicationDict.copier.copy(w);
//				persistObjects.add(o);
//			}
//			for (ApplicationDictItem item : items) {
//				item.setApplicationDict(o.getId());
//				/** 将数据字典和数据存放在同一个分区 */
//				item.setDistributeFactor(w.getDistributeFactor());
//				item.setApplication(w.getApplication());
//				if (item.isLobItem()) {
//					/** 同步创建lob */
//					ApplicationDictLobItem lob = new ApplicationDictLobItem();
//					lob.setDistributeFactor(item.getDistributeFactor());
//					lob.setData(item.getStringLobValue());
//					item.setLobItem(lob.getId());
//					persistObjects.add(lob);
//				}
//				persistObjects.add(item);
//			}
//		}
//		/** process生成 */
//		for (WrapProcess w : wrapApplication.getProcessList()) {
//			Process o = business.entityManagerContainer().find(w.getId(), Process.class);
//			if (null != o) {
//				WrapProcess.copier.copy(w, o);
//			} else {
//				o = WrapProcess.copier.copy(w);
//			}
//			this.coverProcessElement(business, WrapAgent.copier, w.getAgentList(), Agent.class, persistObjects);
//			this.coverProcessElement(business, WrapBegin.copier, w.getBegin(), Begin.class, persistObjects);
//			this.coverProcessElement(business, WrapCancel.copier, w.getCancelList(), Cancel.class, persistObjects);
//			this.coverProcessElement(business, WrapChoice.copier, w.getChoiceList(), Choice.class, persistObjects);
//			this.coverProcessElement(business, WrapDelay.copier, w.getDelayList(), Delay.class, persistObjects);
//			this.coverProcessElement(business, WrapEmbed.copier, w.getEmbedList(), Embed.class, persistObjects);
//			this.coverProcessElement(business, WrapEnd.copier, w.getEndList(), End.class, persistObjects);
//			this.coverProcessElement(business, WrapInvoke.copier, w.getInvokeList(), Invoke.class, persistObjects);
//			this.coverProcessElement(business, WrapManual.copier, w.getManualList(), Manual.class, persistObjects);
//			this.coverProcessElement(business, WrapMerge.copier, w.getMergeList(), Merge.class, persistObjects);
//			this.coverProcessElement(business, WrapMessage.copier, w.getMessageList(), Message.class, persistObjects);
//			this.coverProcessElement(business, WrapParallel.copier, w.getParallelList(), Parallel.class,
//					persistObjects);
//			this.coverProcessElement(business, WrapService.copier, w.getServiceList(), Service.class, persistObjects);
//			this.coverProcessElement(business, WrapSplit.copier, w.getSplitList(), Split.class, persistObjects);
//			this.coverProcessElement(business, WrapRoute.copier, w.getRouteList(), Route.class, persistObjects);
//			for (JpaObject obj : removeObjects) {
//				business.entityManagerContainer().remove(obj);
//			}
//			for (JpaObject obj : persistObjects) {
//				business.entityManagerContainer().persist(obj);
//			}
//		}
//	}
//
//	private <T extends JpaObject, W extends JpaObject> void coverProcessElement(Business business,
//			WrapCopier<W, T> copier, List<W> list, Class<T> cls, List<JpaObject> persistObjects) throws Exception {
//		for (W w : list) {
//			this.coverProcessElement(business, copier, w, cls, persistObjects);
//		}
//	}
//
//	private <T extends JpaObject, W extends JpaObject> void coverProcessElement(Business business,
//			WrapCopier<W, T> copier, W w, Class<T> cls, List<JpaObject> persistObjects) throws Exception {
//		T t = business.entityManagerContainer().find(w.getId(), cls);
//		if (null != t) {
//			copier.copy(w, t);
//		} else {
//			persistObjects.add(copier.copy(w));
//		}
//	}
//
//	private void adjustForCreate(Business business, WrapApplication wrapApplication) throws Exception {
//		String newApplicationId = this.idleId(business, wrapApplication.getId(), Application.class);
//		if (!StringUtils.equals(newApplicationId, wrapApplication.getId())) {
//			wrapApplication.changeId(wrapApplication.getId(), newApplicationId);
//		}
//		wrapApplication.setName(this.idleName(business, wrapApplication.getName(), Application.class, null));
//		wrapApplication.setAlias(this.idleAlias(business, wrapApplication.getAlias(), Application.class, null));
//		for (WrapProcess wrapProcess : ListTools.nullToEmpty(wrapApplication.getProcessList())) {
//			String newId = this.idleId(business, wrapProcess.getId(), Process.class);
//			if (!StringUtils.equals(newId, wrapProcess.getId())) {
//				wrapApplication.changeId(wrapProcess.getId(), newId);
//			}
//			wrapProcess.setName(this.idleNameWithApplication(business, wrapApplication.getId(), wrapProcess.getName(),
//					Process.class, null));
//			wrapProcess.setAlias(this.idleAliasWithApplication(business, wrapApplication.getId(),
//					wrapProcess.getAlias(), Process.class, null));
//			this.adjustProcessElementForCreate(business, wrapApplication, wrapProcess, wrapProcess.getAgentList(),
//					Agent.class);
//			this.adjustProcessElementForCreate(business, wrapApplication, wrapProcess, wrapProcess.getBegin(),
//					Begin.class);
//			this.adjustProcessElementForCreate(business, wrapApplication, wrapProcess, wrapProcess.getCancelList(),
//					Cancel.class);
//			this.adjustProcessElementForCreate(business, wrapApplication, wrapProcess, wrapProcess.getChoiceList(),
//					Choice.class);
//			this.adjustProcessElementForCreate(business, wrapApplication, wrapProcess, wrapProcess.getDelayList(),
//					Delay.class);
//			this.adjustProcessElementForCreate(business, wrapApplication, wrapProcess, wrapProcess.getEmbedList(),
//					Embed.class);
//			this.adjustProcessElementForCreate(business, wrapApplication, wrapProcess, wrapProcess.getEndList(),
//					End.class);
//			this.adjustProcessElementForCreate(business, wrapApplication, wrapProcess, wrapProcess.getInvokeList(),
//					Invoke.class);
//			this.adjustProcessElementForCreate(business, wrapApplication, wrapProcess, wrapProcess.getManualList(),
//					Manual.class);
//			this.adjustProcessElementForCreate(business, wrapApplication, wrapProcess, wrapProcess.getMergeList(),
//					Merge.class);
//			this.adjustProcessElementForCreate(business, wrapApplication, wrapProcess, wrapProcess.getMessageList(),
//					Message.class);
//			this.adjustProcessElementForCreate(business, wrapApplication, wrapProcess, wrapProcess.getParallelList(),
//					Parallel.class);
//			this.adjustProcessElementForCreate(business, wrapApplication, wrapProcess, wrapProcess.getServiceList(),
//					Service.class);
//			this.adjustProcessElementForCreate(business, wrapApplication, wrapProcess, wrapProcess.getSplitList(),
//					Split.class);
//			this.adjustProcessElementForCreate(business, wrapApplication, wrapProcess, wrapProcess.getRouteList(),
//					Route.class);
//		}
//		for (WrapForm wrapForm : ListTools.nullToEmpty(wrapApplication.getFormList())) {
//			String newId = this.idleId(business, wrapForm.getId(), Form.class);
//			if (!StringUtils.equals(newId, wrapForm.getId())) {
//				wrapApplication.changeId(wrapForm.getId(), newId);
//			}
//			wrapForm.setName(this.idleNameWithApplication(business, wrapApplication.getId(), wrapForm.getName(),
//					Form.class, null));
//			wrapForm.setAlias(this.idleAliasWithApplication(business, wrapApplication.getId(), wrapForm.getAlias(),
//					Form.class, null));
//		}
//		for (WrapScript wrapScript : ListTools.nullToEmpty(wrapApplication.getScriptList())) {
//			String newId = this.idleId(business, wrapScript.getId(), Script.class);
//			if (!StringUtils.equals(newId, wrapScript.getId())) {
//				wrapApplication.changeId(wrapScript.getId(), newId);
//			}
//			wrapScript.setName(this.idleNameWithApplication(business, wrapApplication.getId(), wrapScript.getName(),
//					Script.class, null));
//			wrapScript.setAlias(this.idleAliasWithApplication(business, wrapApplication.getId(), wrapScript.getAlias(),
//					Script.class, null));
//		}
//		for (WrapApplicationDict wrapApplicationDict : ListTools
//				.nullToEmpty(wrapApplication.getApplicationDictList())) {
//			String newId = this.idleId(business, wrapApplicationDict.getId(), ApplicationDict.class);
//			if (!StringUtils.equals(newId, wrapApplicationDict.getId())) {
//				wrapApplication.changeId(wrapApplicationDict.getId(), newId);
//			}
//			wrapApplicationDict.setName(this.idleNameWithApplication(business, wrapApplication.getId(),
//					wrapApplicationDict.getName(), ApplicationDict.class, null));
//			wrapApplicationDict.setAlias(this.idleAliasWithApplication(business, wrapApplication.getId(),
//					wrapApplicationDict.getAlias(), ApplicationDict.class, null));
//		}
//		wrapApplication.consistentApplication();
//	}
//
//	private <T extends JpaObject, W extends JpaObject> void adjustProcessElementForCreate(Business business,
//			WrapApplication wrapApplication, WrapProcess wrapProcess, List<W> list, Class<T> cls) throws Exception {
//		for (W w : ListTools.nullToEmpty(list)) {
//			this.adjustProcessElementForCreate(business, wrapApplication, wrapProcess, w, cls);
//		}
//	}
//
//	private <T extends JpaObject, W extends JpaObject> void adjustProcessElementForCreate(Business business,
//			WrapApplication wrapApplication, WrapProcess wrapProcess, W w, Class<T> cls) throws Exception {
//		if (null != w) {
//			String newId = this.idleId(business, w.getId(), cls);
//			if (!StringUtils.equals(newId, w.getId())) {
//				wrapApplication.changeId(w.getId(), newId);
//			}
//		}
//	}
//
//	private void create(Business business, WrapApplication wrapApplication) throws Exception {
//		Application exist = business.entityManagerContainer().find(wrapApplication.getId(), Application.class);
//		if (null != exist) {
//			throw new ExceptionApplicationExist(wrapApplication.getId());
//		}
//		List<JpaObject> persistObjects = new ArrayList<>();
//		persistObjects.add(WrapApplication.copier.copy(wrapApplication));
//		for (WrapForm wrapForm : wrapApplication.getFormList()) {
//			persistObjects.add(WrapForm.copier.copy(wrapForm));
//		}
//		for (WrapScript wrapScript : wrapApplication.getScriptList()) {
//			persistObjects.add(WrapScript.copier.copy(wrapScript));
//		}
//		for (WrapApplicationDict wrapApplicationDict : wrapApplication.getApplicationDictList()) {
//			ApplicationDict applicationDict = WrapApplicationDict.copier.copy(wrapApplicationDict);
//			persistObjects.add(applicationDict);
//			ItemConverter<ApplicationDictItem> converter = new ItemConverter<>(ApplicationDictItem.class);
//			List<ApplicationDictItem> list = converter.disassemble(wrapApplicationDict.getData());
//			for (ApplicationDictItem o : list) {
//				o.setApplicationDict(applicationDict.getId());
//				/** 将数据字典和数据存放在同一个分区 */
//				o.setDistributeFactor(applicationDict.getDistributeFactor());
//				o.setApplication(applicationDict.getApplication());
//				if (o.isLobItem()) {
//					/** 同步创建lob */
//					ApplicationDictLobItem lob = new ApplicationDictLobItem();
//					lob.setDistributeFactor(o.getDistributeFactor());
//					lob.setData(o.getStringLobValue());
//					o.setLobItem(lob.getId());
//					persistObjects.add(lob);
//				}
//				persistObjects.add(o);
//			}
//		}
//		for (WrapProcess wrapProcess : wrapApplication.getProcessList()) {
//			Process process = WrapProcess.copier.copy(wrapProcess);
//			persistObjects.add(process);
//			for (WrapAgent w : ListTools.nullToEmpty(wrapProcess.getAgentList())) {
//				persistObjects.add(WrapAgent.copier.copy(w));
//			}
//			for (WrapBegin w : ListTools.trim(null, true, true, wrapProcess.getBegin())) {
//				persistObjects.add(WrapBegin.copier.copy(w));
//			}
//			for (WrapCancel w : ListTools.nullToEmpty(wrapProcess.getCancelList())) {
//				persistObjects.add(WrapCancel.copier.copy(w));
//			}
//			for (WrapChoice w : ListTools.nullToEmpty(wrapProcess.getChoiceList())) {
//				persistObjects.add(WrapChoice.copier.copy(w));
//			}
//			for (WrapDelay w : ListTools.nullToEmpty(wrapProcess.getDelayList())) {
//				persistObjects.add(WrapDelay.copier.copy(w));
//			}
//			for (WrapEmbed w : ListTools.nullToEmpty(wrapProcess.getEmbedList())) {
//				persistObjects.add(WrapEmbed.copier.copy(w));
//			}
//			for (WrapEnd w : ListTools.nullToEmpty(wrapProcess.getEndList())) {
//				persistObjects.add(WrapEnd.copier.copy(w));
//			}
//			for (WrapInvoke w : ListTools.nullToEmpty(wrapProcess.getInvokeList())) {
//				persistObjects.add(WrapInvoke.copier.copy(w));
//			}
//			for (WrapManual w : ListTools.nullToEmpty(wrapProcess.getManualList())) {
//				persistObjects.add(WrapManual.copier.copy(w));
//			}
//			for (WrapMerge w : ListTools.nullToEmpty(wrapProcess.getMergeList())) {
//				persistObjects.add(WrapMerge.copier.copy(w));
//			}
//			for (WrapMessage w : ListTools.nullToEmpty(wrapProcess.getMessageList())) {
//				persistObjects.add(WrapMessage.copier.copy(w));
//			}
//			for (WrapParallel w : ListTools.nullToEmpty(wrapProcess.getParallelList())) {
//				persistObjects.add(WrapParallel.copier.copy(w));
//			}
//			for (WrapService w : ListTools.nullToEmpty(wrapProcess.getServiceList())) {
//				persistObjects.add(WrapService.copier.copy(w));
//			}
//			for (WrapSplit w : ListTools.nullToEmpty(wrapProcess.getSplitList())) {
//				persistObjects.add(WrapSplit.copier.copy(w));
//			}
//			for (WrapRoute w : ListTools.nullToEmpty(wrapProcess.getRouteList())) {
//				persistObjects.add(WrapRoute.copier.copy(w));
//			}
//		}
//		for (JpaObject o : persistObjects) {
//			business.entityManagerContainer().persist(o);
//		}
//	}
//
//	private <T extends JpaObject> String idleNameWithApplication(Business business, String applicationId, String name,
//			Class<T> cls, String excludeId) throws Exception {
//		if (StringUtils.isEmpty(name)) {
//			return "";
//		}
//		List<String> list = new ArrayList<>();
//		list.add(name);
//		for (int i = 0; i < 99; i++) {
//			list.add(name + String.format("%02d", i));
//		}
//		list.add(StringTools.uniqueToken());
//		EntityManager em = business.entityManagerContainer().get(cls);
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<String> cq = cb.createQuery(String.class);
//		Root<T> root = cq.from(cls);
//		Predicate p = root.get("name").in(list);
//		p = cb.and(p, cb.equal(root.get("application"), applicationId));
//		if (StringUtils.isNotEmpty(excludeId)) {
//			p = cb.and(p, cb.notEqual(root.get(JpaObject.id_FIELDNAME), excludeId));
//		}
//		cq.select(root.get("name")).where(p);
//		List<String> os = em.createQuery(cq).getResultList();
//		list = ListUtils.subtract(list, os);
//		return list.get(0);
//	}
//
//	private <T extends JpaObject> String idleAliasWithApplication(Business business, String applicationId, String alias,
//			Class<T> cls, String excludeId) throws Exception {
//		if (StringUtils.isEmpty(alias)) {
//			return "";
//		}
//		List<String> list = new ArrayList<>();
//		list.add(alias);
//		for (int i = 0; i < 99; i++) {
//			list.add(alias + String.format("%02d", i));
//		}
//		list.add(StringTools.uniqueToken());
//		EntityManager em = business.entityManagerContainer().get(cls);
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<String> cq = cb.createQuery(String.class);
//		Root<T> root = cq.from(cls);
//		Predicate p = root.get("alias").in(list);
//		p = cb.and(p, cb.equal(root.get("application"), applicationId));
//		if (StringUtils.isNotEmpty(excludeId)) {
//			p = cb.and(p, cb.notEqual(root.get(JpaObject.id_FIELDNAME), excludeId));
//		}
//		cq.select(root.get("alias")).where(p);
//		List<String> os = em.createQuery(cq).getResultList();
//		list = ListUtils.subtract(list, os);
//		return list.get(0);
//	}
//
//	private <T extends JpaObject> List<T> listObjectWithApplication(Business business, Application application,
//			Class<T> cls) throws Exception {
//		EntityManager em = business.entityManagerContainer().get(cls);
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<T> cq = cb.createQuery(cls);
//		Root<T> root = cq.from(cls);
//		Predicate p = cb.equal(root.get("application"), application.getId());
//		cq.select(root).where(p);
//		List<T> os = em.createQuery(cq).getResultList();
//		return os;
//	}
//
//	private <T extends JpaObject> String idleId(Business business, String id, Class<T> cls) throws Exception {
//		T t = business.entityManagerContainer().find(id, cls);
//		if (null == t) {
//			return id;
//		} else {
//			return JpaObject.createId();
//		}
//	}
//
//	private List<ApplicationDictItem> listApplicationDictItem(Business business, ApplicationDict applicationDict)
//			throws Exception {
//		EntityManager em = business.entityManagerContainer().get(ApplicationDictItem.class);
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<ApplicationDictItem> cq = cb.createQuery(ApplicationDictItem.class);
//		Root<ApplicationDictItem> root = cq.from(ApplicationDictItem.class);
//		Predicate p = cb.equal(root.get(ApplicationDictItem_.applicationDict), applicationDict.getId());
//		cq.select(root).where(p);
//		return em.createQuery(cq).getResultList();
//	}
//
//	private <W extends JpaObject, T extends JpaObject> List<CompareElement> compare(Business business, List<W> ws,
//			List<T> ts) throws Exception {
//		List<CompareElement> list = new ArrayList<>();
//		List<W> findWs = new ArrayList<>();
//		List<T> findTs = new ArrayList<>();
//		loop: for (W w : ListTools.nullToEmpty(ws)) {
//			if (!findWs.contains(w)) {
//				for (T t : ListTools.nullToEmpty(ts)) {
//					if (!findTs.contains(t)) {
//						if (StringUtils.equals(w.getId(), t.getId())) {
//							CompareElement c = new CompareElement();
//							c.setW(w);
//							c.setT(t);
//							list.add(c);
//							findWs.add(w);
//							findTs.add(t);
//							continue loop;
//						}
//					}
//				}
//			}
//		}
//		loop: for (W w : ListTools.nullToEmpty(ws)) {
//			if (!findWs.contains(w)) {
//				for (T t : ListTools.nullToEmpty(ts)) {
//					if (!findTs.contains(t)) {
//						if (StringUtils.isNotEmpty(BeanUtils.getProperty(w, "name")) && StringUtils
//								.equals(BeanUtils.getProperty(w, "name"), BeanUtils.getProperty(t, "name"))) {
//							CompareElement c = new CompareElement();
//							c.setW(w);
//							c.setT(t);
//							list.add(c);
//							findWs.add(w);
//							findTs.add(t);
//							continue loop;
//						}
//					}
//				}
//			}
//		}
//		loop: for (W w : ListTools.nullToEmpty(ws)) {
//			if (!findWs.contains(w)) {
//				for (T t : ListTools.nullToEmpty(ts)) {
//					if (!findTs.contains(t)) {
//						if (StringUtils.isNotEmpty(BeanUtils.getProperty(w, "alias")) && StringUtils
//								.equals(BeanUtils.getProperty(w, "alias"), BeanUtils.getProperty(t, "alias"))) {
//							CompareElement c = new CompareElement();
//							c.setW(w);
//							c.setT(t);
//							list.add(c);
//							findWs.add(w);
//							findTs.add(t);
//							continue loop;
//						}
//					}
//				}
//			}
//		}
//		for (W w : ListTools.nullToEmpty(ws)) {
//			if (!findWs.contains(w)) {
//				CompareElement c = new CompareElement();
//				c.setW(w);
//				c.setT(null);
//				list.add(c);
//			}
//		}
//		for (T t : ListTools.nullToEmpty(ts)) {
//			if (!findTs.contains(t)) {
//				CompareElement c = new CompareElement();
//				c.setW(null);
//				c.setT(t);
//				list.add(c);
//			}
//		}
//		return list;
//	}
//
//	public static class CompareElement {
//		private JpaObject w;
//		private JpaObject t;
//
//		public JpaObject getW() {
//			return w;
//		}
//
//		public void setW(JpaObject w) {
//			this.w = w;
//		}
//
//		public JpaObject getT() {
//			return t;
//		}
//
//		public void setT(JpaObject t) {
//			this.t = t;
//		}
//
//	}
//
//	public static class Wo extends WoId {
//
//	}
//
//	public static class Wi extends GsonPropertyObject {
//
//		@FieldDescribe("标识")
//		private String flag;
//
//		@FieldDescribe("方式")
//		private Method method = Method.ignore;
//
//		public String getFlag() {
//			return flag;
//		}
//
//		public void setFlag(String flag) {
//			this.flag = flag;
//		}
//
//		public Method getMethod() {
//			return method;
//		}
//
//		public void setMethod(Method method) {
//			this.method = method;
//		}
//
//	}
//
//}