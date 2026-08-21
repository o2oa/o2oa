package com.x.attendance.assemble.control.schedule.v2;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveManager;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeavePolicyEnums.ExpireTypeEnum;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeavePolicyEnums.GrantScopeTypeEnum;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveTransactionEnums.BizTypeEnum;
import com.x.attendance.assemble.control.schedule.v2.model.QueueAttendanceV2LeavePolicyGrantModel;
import com.x.attendance.entity.v2.AttendanceV2LeaveLedger;
import com.x.attendance.entity.v2.AttendanceV2LeavePolicy;
import com.x.attendance.entity.v2.AttendanceV2LeaveTransaction;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.script.AbstractResources;
import com.x.base.core.project.scripting.GraalvmScriptingFactory;
import com.x.base.core.project.tools.CronTools;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.webservices.WebservicesClient;
import com.x.organization.core.express.Organization;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.graalvm.polyglot.Source;

public class QueueAttendanceV2LeavePolicyGrant extends
        AbstractQueue<QueueAttendanceV2LeavePolicyGrantModel> {

    private static final Logger logger = LoggerFactory.getLogger(
            QueueAttendanceV2LeavePolicyGrant.class);

    @Override
    protected void execute(QueueAttendanceV2LeavePolicyGrantModel t) throws Exception {
        if (t == null || t.getPolicy() == null) {
            logger.warn(
                    "QueueAttendanceV2LeavePolicyGrantModel is null or policy is null, ignore this task.");
            return;
        }
        AttendanceV2LeavePolicy policy = t.getPolicy();
        if (StringUtils.isEmpty(policy.getGrantCron())) {
            logger.warn(
                    "Policy {} {} grant cron is empty, ignore this task.",
                    policy.getPolicyName(), policy.getId());
            return ;
        }
        Date today = new Date();
        Date date = CronTools.next(policy.getGrantCron(), policy.getGrantLastExecuteTime());
        if (!BooleanUtils.isTrue(t.getIsImmediately()) && date.after(today)) {
            if (logger.isDebugEnabled()) {
                logger.debug(
                        "Policy {} next execute time is {}, ignore this task.",
                        policy.getId(), date.toString());
            }
            return;
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            List<String> userList = new ArrayList<>();
            // 全员
            if (GrantScopeTypeEnum.ALL.getValue().equals(policy.getGrantScopeType())) {
                userList = business.organization().person().listAll();
            } else if (GrantScopeTypeEnum.DEPARTMENT.getValue()
                    .equals(policy.getGrantScopeType())) {
                // 组织
                if (policy.getGrantScopeList() != null && !policy.getGrantScopeList().isEmpty()) {
                    for (String filter : policy.getGrantScopeList()) {
                        analysisPerson(userList, filter, business);
                    }
                }
            }
            // 排除字段
            List<String> excludeList = Optional.ofNullable(policy.getGrantExcludeList()).orElse(new ArrayList<>());
            // userList 去重
            userList = new ArrayList<>(new java.util.HashSet<>(userList));
            // 移除排除名单中的用户
            if (!excludeList.isEmpty()) {
                userList.removeAll(excludeList);
            }
            // 移除后再判断是否为空
            if (userList == null || userList.isEmpty()) {
                logger.warn("User list is empty for policy {}, skip this task.", policy.getId());
                return;
            }

            // 当前日期作为发放标识
            String grantPeriod = DateTools.format(today, DateTools.formatCompact_yyyyMMdd);
            // 循环发放
            for (String user : userList) {
                // 发放额度
                double grantAmount = 0.0;
                if (BooleanUtils.isTrue(policy.getGrantAmountTypeUseScript())) {
                    Double ret = executeScriptCalGrantAmount(user, policy.getGrantScript(), business);
                    if (ret != null) {
                        grantAmount = ret;
                    }
                } else {
                    grantAmount = policy.getGrantAmount() == null ? 0 : policy.getGrantAmount();
                }
                if (grantAmount <= 0) {
                    logger.warn("Policy {} grant amount is {}, skip granting for user {}.",
                            policy.getId(), grantAmount,
                            user);
                    continue;
                }
                //grantPeriod leaveTypeId person 查询是否已经有记录了，避免重复发放
                List<AttendanceV2LeaveLedger> list = emc.listEqualAndEqualAndEqual(
                        AttendanceV2LeaveLedger.class, AttendanceV2LeaveLedger.leaveTypeId_FIELDNAME,
                        policy.getLeaveTypeId(), AttendanceV2LeaveLedger.grantPeriod_FIELDNAME, grantPeriod,
                        AttendanceV2LeaveLedger.person_FIELDNAME, user);
                if (list != null && !list.isEmpty()) {
                    logger.warn(
                            "Policy {} already granted for user {} in grant period {}, skip this user.",
                            policy.getId(), user, grantPeriod);
                    continue;
                }
                AttendanceV2LeaveLedger ledger = new AttendanceV2LeaveLedger();
                ledger.setLeaveTypeId(policy.getLeaveTypeId());
                ledger.setPerson(user);
                ledger.setPolicyId(policy.getId());
                ledger.setGrantPeriod(grantPeriod);
                ledger.setUsedAmount(0.0);
                ledger.setRemainingAmount(grantAmount);
                ledger.setGrantAmount(grantAmount);
                ledger.setGrantTime(today);
                //TODO 过期时间需要重新设计
                if (ExpireTypeEnum.RELATIVE.getValue().equals(policy.getExpireType())) {
                    int addDay = policy.getExpireValue() != null ? policy.getExpireValue() : 0;
                    if (addDay > 0) {
                        Date expireTime = DateTools.addDay(today, addDay);
                        ledger.setExpireTime(expireTime);
                    }
                } //
                grantLeaveLedgerAndRefreshAccount(emc, ledger);
            }
            logger.info("发放完成，政策ID: {}, 用户数量: {} 。 开始更新下一次执行时间",
                    policy.getId(), userList.size());

            emc.beginTransaction(AttendanceV2LeavePolicy.class);
            AttendanceV2LeavePolicy policyOld = emc.find(policy.getId(),
                    AttendanceV2LeavePolicy.class);
            policyOld.setGrantLastExecuteTime(today); // 更新执行定时器时间
            emc.check(policyOld, CheckPersistType.all);
            emc.commit();
        }
        if (logger.isDebugEnabled()) {
            logger.debug(
                    "======================新版考勤假期管理策略 {} 发放 执行完成==============================",
                    policy.getPolicyName());
        }
    }

    // 执行脚本获取发放额度
    private Double executeScriptCalGrantAmount(String person, String scriptText, Business business) {
        Double ret = null;
        try {
            Person p = business.organization().person().getObject(person);
            Source source = null;
            if (StringUtils.isNotEmpty(scriptText)) {
               source = GraalvmScriptingFactory.functionalization(scriptText);
            }
            if (source != null && p != null) {
                JsonElement element = GraalvmScriptingFactory.eval(source, binding(p));
                ret = Double.valueOf(element.toString());
            } else {
                logger.warn("脚本 或 人员 {} 为空 ，无法执行脚本", person);
            }
        } catch (Exception e) {
            logger.error(e);
        }
        return ret;
    }
    // 绑定一些参数
    private GraalvmScriptingFactory.Bindings binding(Person person) throws Exception {
        Resources resources = new Resources();
        resources.setContext(ThisApplication.context());
        resources.setOrganization(new Organization(ThisApplication.context()));
        resources.setWebservicesClient(new WebservicesClient());
        resources.setApplications(ThisApplication.context().applications());
        GraalvmScriptingFactory.Bindings bindings = new GraalvmScriptingFactory.Bindings();
        bindings.putMember(GraalvmScriptingFactory.BINDING_NAME_SERVICE_RESOURCES, resources);
        bindings.putMember("grantPerson", XGsonBuilder.toJson(person));
        return bindings;
    }

    // 解析人员列表，把组织下人员都查询出来放入 userList 中
    private void analysisPerson(List<String> userList, String filter, Business business)
            throws Exception {
        if (filter.endsWith("@U")) { // 组织转化成人员列表 递归
            List<String> users = business.organization().person().listWithUnitSubNested(filter);
            if (users != null && !users.isEmpty()) {
                userList.addAll(users);
            }
        } else if (filter.endsWith("@P")) {
            userList.add(filter);
        }
    }

    private void grantLeaveLedgerAndRefreshAccount(EntityManagerContainer emc, AttendanceV2LeaveLedger ledger) throws Exception {
        emc.beginTransaction(AttendanceV2LeaveLedger.class);
        emc.beginTransaction(AttendanceV2LeaveTransaction.class);
        emc.persist(ledger, CheckPersistType.all);
        AttendanceV2LeaveTransaction transaction = new AttendanceV2LeaveTransaction();
        transaction.setPerson(ledger.getPerson());
        transaction.setLeaveTypeId(ledger.getLeaveTypeId());
        transaction.setLedgerId(ledger.getId());
        transaction.setBizType(BizTypeEnum.GRANT.getValue());
        transaction.setAmount(ledger.getGrantAmount()); // 发放数量
        emc.persist(transaction, CheckPersistType.all);
        emc.commit();
        // 发放完后异步更新账户余额数据
        AttendanceV2LeaveManager.asyncUpdateLeaveAccount(ledger.getPerson(), ledger.getLeaveTypeId());
    }

    public static class Resources extends AbstractResources {
        private Organization organization;

        public Organization getOrganization() {
            return organization;
        }

        public void setOrganization(Organization organization) {
            this.organization = organization;
        }

    }
}
