package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWithMessage;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveManager;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveRequestEnums.LeaveRequestStatusEnum;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveTransactionEnums.BizTypeEnum;
import com.x.attendance.entity.v2.AttendanceV2LeaveLedger;
import com.x.attendance.entity.v2.AttendanceV2LeaveRequest;
import com.x.attendance.entity.v2.AttendanceV2LeaveTransaction;
import com.x.attendance.entity.v2.AttendanceV2LeaveTransaction_;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;

public class ActionLeaveRequestUpdateStatus extends BaseAction {

    ActionResult<Wo> execute(JsonElement jsonElement, LeaveRequestStatusEnum targetStatus) throws Exception {
        if (!LeaveRequestStatusEnum.REJECTED.equals(targetStatus)
                && !LeaveRequestStatusEnum.CANCELLED.equals(targetStatus)) {
            throw new ExceptionWithMessage("请假申请目标状态不正确");
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (StringUtils.isBlank(wi.getRequestId())) {
                throw new ExceptionEmptyParameter("请假申请ID");
            }
            AttendanceV2LeaveRequest request = emc.find(wi.getRequestId(), AttendanceV2LeaveRequest.class);
            if (request == null) {
                throw new ExceptionNotExistObject("请假申请 " + wi.getRequestId());
            }
            if (targetStatus.getValue().equals(request.getStatus())) {
                result.setData(buildWo(request, targetStatus, 0.0, 0));
                return result;
            }
            if (!LeaveRequestStatusEnum.APPLYING.getValue().equals(request.getStatus())) {
                throw new ExceptionWithMessage("请假申请当前状态为 " + request.getStatus() + "，不能变更为 "
                        + targetStatus.getValue());
            }

            RestoreResult restoreResult = updateStatusAndRestoreLeaveBalance(emc, request, targetStatus);
            if (restoreResult.getTransactionRows() > 0) {
                AttendanceV2LeaveManager.asyncUpdateLeaveAccount(request.getPerson(), request.getLeaveTypeId());
            }

            result.setData(buildWo(request, targetStatus, restoreResult.getRestoredAmount(),
                    restoreResult.getTransactionRows()));
            return result;
        }
    }

    private RestoreResult updateStatusAndRestoreLeaveBalance(EntityManagerContainer emc, AttendanceV2LeaveRequest request,
            LeaveRequestStatusEnum targetStatus) throws Exception {
        List<AttendanceV2LeaveTransaction> useTransactions = listTransactions(emc, request.getId(),
                BizTypeEnum.USE.getValue());
        List<AttendanceV2LeaveTransaction> cancelTransactions = listTransactions(emc, request.getId(),
                BizTypeEnum.CANCEL.getValue());
        emc.beginTransaction(AttendanceV2LeaveRequest.class);
        request.setStatus(targetStatus.getValue());
        if (useTransactions.isEmpty()) {
            emc.commit();
            return new RestoreResult(0.0, 0);
        }
        if (!cancelTransactions.isEmpty()) {
            emc.commit();
            return new RestoreResult(0.0, 0);
        }

        double restoredAmount = 0.0;
        int transactionRows = 0;
        emc.beginTransaction(AttendanceV2LeaveLedger.class);
        emc.beginTransaction(AttendanceV2LeaveTransaction.class);
        for (AttendanceV2LeaveTransaction useTransaction : useTransactions) {
            double amount = useTransaction.getAmount() != null ? useTransaction.getAmount() : 0.0;
            if (amount <= 0) {
                continue;
            }
            AttendanceV2LeaveLedger ledger = emc.find(useTransaction.getLedgerId(), AttendanceV2LeaveLedger.class);
            if (ledger == null) {
                throw new ExceptionNotExistObject("假期发放批次 " + useTransaction.getLedgerId());
            }
            double usedAmount = ledger.getUsedAmount() != null ? ledger.getUsedAmount() : 0.0;
            double remainingAmount = ledger.getRemainingAmount() != null ? ledger.getRemainingAmount() : 0.0;
            ledger.setUsedAmount(Math.max(0.0, usedAmount - amount));
            ledger.setRemainingAmount(remainingAmount + amount);

            AttendanceV2LeaveTransaction cancelTransaction = new AttendanceV2LeaveTransaction();
            cancelTransaction.setPerson(request.getPerson());
            cancelTransaction.setLeaveTypeId(request.getLeaveTypeId());
            cancelTransaction.setLedgerId(ledger.getId());
            cancelTransaction.setLeaveRequestId(request.getId());
            cancelTransaction.setBizType(BizTypeEnum.CANCEL.getValue());
            cancelTransaction.setAmount(amount);
            emc.persist(cancelTransaction, CheckPersistType.all);
            restoredAmount += amount;
            transactionRows++;
        }
        emc.commit();
        return new RestoreResult(restoredAmount, transactionRows);
    }

    private List<AttendanceV2LeaveTransaction> listTransactions(EntityManagerContainer emc, String requestId,
            String bizType) throws Exception {
        EntityManager em = emc.get(AttendanceV2LeaveTransaction.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2LeaveTransaction> cq = cb.createQuery(AttendanceV2LeaveTransaction.class);
        Root<AttendanceV2LeaveTransaction> root = cq.from(AttendanceV2LeaveTransaction.class);
        Predicate p = cb.equal(root.get(AttendanceV2LeaveTransaction_.leaveRequestId), requestId);
        p = cb.and(p, cb.equal(root.get(AttendanceV2LeaveTransaction_.bizType), bizType));
        cq.select(root).where(p);
        List<AttendanceV2LeaveTransaction> list = em.createQuery(cq).getResultList();
        return list == null ? new ArrayList<>() : list;
    }

    private Wo buildWo(AttendanceV2LeaveRequest request, LeaveRequestStatusEnum targetStatus, Double restoredAmount,
            Integer transactionRows) {
        Wo wo = new Wo();
        wo.setRequestId(request.getId());
        wo.setStatus(targetStatus.getValue());
        wo.setRestoredAmount(restoredAmount);
        wo.setTransactionRows(transactionRows);
        return wo;
    }

    public static class Wi extends GsonPropertyObject {

        private static final long serialVersionUID = -1270396609149589793L;

        @FieldDescribe("请假申请ID")
        private String requestId;

        public String getRequestId() {
            return requestId;
        }

        public void setRequestId(String requestId) {
            this.requestId = requestId;
        }
    }

    public static class Wo extends GsonPropertyObject {

        private static final long serialVersionUID = 5371987634131454858L;

        @FieldDescribe("请假申请ID")
        private String requestId;

        @FieldDescribe("请假申请状态")
        private String status;

        @FieldDescribe("还原的请假额度")
        private Double restoredAmount;

        @FieldDescribe("新增的还原流水数量")
        private Integer transactionRows;

        public String getRequestId() {
            return requestId;
        }

        public void setRequestId(String requestId) {
            this.requestId = requestId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Double getRestoredAmount() {
            return restoredAmount;
        }

        public void setRestoredAmount(Double restoredAmount) {
            this.restoredAmount = restoredAmount;
        }

        public Integer getTransactionRows() {
            return transactionRows;
        }

        public void setTransactionRows(Integer transactionRows) {
            this.transactionRows = transactionRows;
        }
    }

    private static class RestoreResult {

        private final Double restoredAmount;
        private final Integer transactionRows;

        private RestoreResult(Double restoredAmount, Integer transactionRows) {
            this.restoredAmount = restoredAmount;
            this.transactionRows = transactionRows;
        }

        private Double getRestoredAmount() {
            return restoredAmount;
        }

        private Integer getTransactionRows() {
            return transactionRows;
        }
    }
}
