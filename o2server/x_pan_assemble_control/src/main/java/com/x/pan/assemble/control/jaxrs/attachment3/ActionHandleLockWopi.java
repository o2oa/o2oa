package com.x.pan.assemble.control.jaxrs.attachment3;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.pan.assemble.control.entities.WopiRequestHeader;
import com.x.pan.assemble.control.entities.WopiResponseHeader;
import com.x.pan.assemble.control.entities.WopiStatus;
import com.x.pan.core.entity.LockInfo;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.Date;

class ActionHandleLockWopi extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionHandleLockWopi.class);
    private static final String EMPTY_STRING = "";

    public Response execute(HttpServletRequest request, EffectivePerson effectivePerson, String id) throws Exception {
        if(effectivePerson.isAnonymous()){
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        Response response;
        String wopiOverride = request.getHeader(WopiRequestHeader.OVERRIDE);
        String oldLock = request.getHeader(WopiRequestHeader.OLD_LOCK);
        switch (wopiOverride) {
            case "LOCK":
                if (oldLock != null) {
                    response = this.unlockAndRelock(request, id);
                } else {
                    response = this.lock(request, id, effectivePerson.getDistinguishedName());
                }
                break;
            case "GET_LOCK":
                response = this.getLock(request, id);
                break;
            case "REFRESH_LOCK":
                response = this.refreshLock(request, id);
                break;
            case "UNLOCK":
                response = this.unlock(request, id);
                break;
            default:
                response = Response.status(WopiStatus.NOT_IMPLEMENTED.value()).build();
                break;
        }

        return response;
    }

    /**
     * Processes a Lock request
     *
     * @param request
     * @param fileId
     * @return
     */
    public Response lock(HttpServletRequest request, String fileId, String person) throws Exception {
        String requestLock = request.getHeader(WopiRequestHeader.LOCK);
        LockInfo lockInfo = this.getLockInfo(fileId);
        // Ensure the file isn't already locked or expired
        if (lockInfo == null || StringUtils.isEmpty(lockInfo.getLockValue()) ||
                lockInfo.isExpired() || lockInfo.getLockValue().equals(requestLock)) {
            this.saveOrUpdate(fileId, requestLock, person);
            return Response.ok().build();
        }
        return setLockMismatch(lockInfo.getLockValue(), "File already locked by another interface");
    }

    /**
     * Processes a GetLock request
     *
     * @param request
     * @param fileId
     * @return
     */
    public Response getLock(HttpServletRequest request, String fileId) throws Exception {
        LockInfo lockInfo = this.getLockInfo(fileId);        // Check for valid lock on file
        if (lockInfo == null || StringUtils.isEmpty(lockInfo.getLockValue())) {
            return Response.ok().header(WopiRequestHeader.LOCK, EMPTY_STRING).build();
        } else if (lockInfo.isExpired()) {
            // File lock expired, so clear it out
            this.deleteLockInfo(fileId);
            // File is not locked...return empty X-WOPI-Lock header
            return Response.ok().header(WopiRequestHeader.LOCK, EMPTY_STRING).build();
        }
        // File has a valid lock, so we need to return it
        return Response.ok().header(WopiRequestHeader.LOCK, lockInfo.getLockValue()).build();
    }

    /**
     * Processes a RefreshLock request
     *
     * @param request
     * @param fileId
     * @return
     */
    public Response refreshLock(HttpServletRequest request, String fileId) throws Exception {
        LockInfo lockInfo = this.getLockInfo(fileId);
        String requestLock = request.getHeader(WopiRequestHeader.LOCK);
        // Ensure the file has a valid lock
        if (lockInfo == null || StringUtils.isEmpty(lockInfo.getLockValue())) {
            return setLockMismatch(EMPTY_STRING, "File isn't locked");
        } else if (lockInfo.isExpired()) {
            // File lock expired, so clear it out
            this.deleteLockInfo(fileId);
            return setLockMismatch(EMPTY_STRING, "File isn't locked");
        } else if (!lockInfo.getLockValue().equals(requestLock)) {
            return setLockMismatch(lockInfo.getLockValue(), "Lock mismatch");
        } else {
            // Extend the expiration
            this.updateLockInfo(fileId, lockInfo.getLockValue());
        }
        return Response.ok().build();
    }

    /**
     * Processes a Unlock request
     *
     * @param request
     * @param fileId
     * @return
     */
    public Response unlock(HttpServletRequest request, String fileId) throws Exception {
        LockInfo lockInfo = this.getLockInfo(fileId);
        String requestLock = request.getHeader(WopiRequestHeader.LOCK);
        // Ensure the file has a valid lock
        if (lockInfo == null || StringUtils.isEmpty(lockInfo.getLockValue())) {
            return setLockMismatch(EMPTY_STRING, "File isn't locked");
        } else if (lockInfo.isExpired()) {
            // File lock expired, so clear it out
            this.deleteLockInfo(fileId);
            return setLockMismatch(EMPTY_STRING, "File isn't locked");
        } else if (!lockInfo.getLockValue().equals(requestLock)) {
            return setLockMismatch(lockInfo.getLockValue(), "Lock mismatch");
        } else {
            // Unlock the file and return success 200
            this.deleteLockInfo(fileId);
        }
        return Response.ok().build();
    }

    /**
     * Processes a UnlockAndRelock request
     *
     * @param request
     * @param fileId
     */
    public Response unlockAndRelock(HttpServletRequest request, String fileId) throws Exception {
        LockInfo lockInfo = this.getLockInfo(fileId);
        String requestLock = request.getHeader(WopiRequestHeader.LOCK);
        String requestOldLock = request.getHeader(WopiRequestHeader.OLD_LOCK);

        // Ensure the file has a valid lock
        if (lockInfo == null || StringUtils.isEmpty(lockInfo.getLockValue())) {
            return setLockMismatch(EMPTY_STRING, "File isn't locked");
        } else if (lockInfo.isExpired()) {
            // File lock expired, so clear it out
            this.deleteLockInfo(fileId);
            return setLockMismatch(EMPTY_STRING, "File isn't locked");
        } else if (!lockInfo.getLockValue().equals(requestOldLock)) {
            return setLockMismatch(lockInfo.getLockValue(), "Lock mismatch");
        } else {
            // Update the file with a LockValue and LockExpiration and return success 200
            this.updateLockInfo(fileId, requestLock);
        }
        return Response.ok().build();
    }

    private Response setLockMismatch(String existingLock, String failReason) {
        return Response.status(Response.Status.CONFLICT).
                header(WopiResponseHeader.LOCK, existingLock).
                header(WopiResponseHeader.LOCK_FAILURE_REASON, failReason).build();
    }

    private LockInfo getLockInfo(String fileId) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            return emc.find(fileId, LockInfo.class);
        }
    }

    private void saveOrUpdate(String fileId, String lockValue, String person) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            LockInfo lockInfo = emc.find(fileId, LockInfo.class);
            emc.beginTransaction(LockInfo.class);
            if(lockInfo != null) {
                lockInfo.setLockValue(lockValue);
                lockInfo.setLockTime(new Date());
                lockInfo.setPerson(person);
            }else{
                lockInfo = new LockInfo(person, fileId, lockValue);
                emc.persist(lockInfo);
            }
            emc.commit();
        }
    }

    private void updateLockInfo(String fileId, String lockValue) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            LockInfo lockInfo = emc.find(fileId, LockInfo.class);
            if(lockInfo != null) {
                emc.beginTransaction(LockInfo.class);
                lockInfo.setLockValue(lockValue);
                lockInfo.setLockTime(new Date());
                emc.commit();
            }
        }
    }

    private void deleteLockInfo(String fileId) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            LockInfo lockInfo = emc.find(fileId, LockInfo.class);
            if(lockInfo != null) {
                emc.beginTransaction(LockInfo.class);
                emc.remove(lockInfo);
                emc.commit();
            }
        }
    }

}
