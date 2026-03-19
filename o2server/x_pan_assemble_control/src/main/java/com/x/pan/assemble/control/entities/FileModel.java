package com.x.pan.assemble.control.entities;

import com.google.gson.annotations.SerializedName;
import com.x.base.core.project.gson.GsonPropertyObject;

/**
 * @author sword
 * @date 2023/01/12 16:26
 **/
public class FileModel extends GsonPropertyObject {

    /**
     * file name
     */
    @SerializedName("BaseFileName")
    private String baseFileName;
    /**
     * A 256 bit SHA-2-encoded hash of the file contents, as a Base64-encoded string.
     * Used for caching purposes in WOPI clients.
     */
    @SerializedName("SHA256")
    private String sha256;
    /**
     * A string that uniquely identifies the owner of the file
     */
    @SerializedName("OwnerId")
    private String ownerId;
    /**
     * File size in bytes
     */
    @SerializedName("Size")
    private Long size;
    /**
     * The current version of the file based on the server’s file version schema
     * This value must change when the file changes, and version values must never repeat for a given file.
     */
    @SerializedName("Version")
    private String version;
    @SerializedName("UserId")
    private String userId;
    @SerializedName("UserFriendlyName")
    private String userFriendlyName;
    @SerializedName("ReadOnly")
    private Boolean readOnly = true;
    /**
     * indicates that the WOPI client if allow the user to edit the file
     */
    @SerializedName("UserCanWrite")
    private Boolean userCanWrite = false;
    /**
     * if the host supports the update operations
     */
    @SerializedName("SupportsUpdate")
    private Boolean supportsUpdate = true;
    /**
     * if the host supports the GetLock operation.
     */
    @SerializedName("SupportsGetLock")
    private boolean supportsGetLock = true;
    /**
     * indicates that the host supports the following WOPI operations:
     * Lock, Unlock, RefreshLock, UnlockAndRelock
     */
    @SerializedName("SupportsLocks")
    private Boolean supportsLocks = true;
    /**
     * user does not have sufficient permission to create new files on the WOPI server
     */
    @SerializedName("UserCanNotWriteRelative")
    private Boolean userCanNotWriteRelative = true;

    public String getBaseFileName() {
        return baseFileName;
    }

    public void setBaseFileName(String baseFileName) {
        this.baseFileName = baseFileName;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Boolean getUserCanWrite() {
        return userCanWrite;
    }

    public void setUserCanWrite(Boolean userCanWrite) {
        this.userCanWrite = userCanWrite;
        this.readOnly = !userCanWrite;
    }

    public Boolean getSupportsUpdate() {
        return supportsUpdate;
    }

    public void setSupportsUpdate(Boolean supportsUpdate) {
        this.supportsUpdate = supportsUpdate;
    }

    public Boolean getSupportsLocks() {
        return supportsLocks;
    }

    public void setSupportsLocks(Boolean supportsLocks) {
        this.supportsLocks = supportsLocks;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserFriendlyName() {
        return userFriendlyName;
    }

    public void setUserFriendlyName(String userFriendlyName) {
        this.userFriendlyName = userFriendlyName;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    public Boolean getUserCanNotWriteRelative() {
        return userCanNotWriteRelative;
    }

    public void setUserCanNotWriteRelative(Boolean userCanNotWriteRelative) {
        this.userCanNotWriteRelative = userCanNotWriteRelative;
    }

    public String getSha256() {
        return sha256;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    public boolean getSupportsGetLock() {
        return supportsGetLock;
    }

    public void setSupportsGetLock(boolean supportsGetLock) {
        this.supportsGetLock = supportsGetLock;
    }
}
