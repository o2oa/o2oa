package com.x.base.core.entity;

import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemConfigBuilder;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.http.HttpFileSystem;

/**
 * The config builder for various MinIo cloud oss configuration options.
 * @author sword
 */
public class MinFileSystemConfigBuilder extends FileSystemConfigBuilder {
    private static final String SERVER_SIDE_ENCRYPTION   = "serverSideEncryption";
    private static final String DISABLE_CHUNKED_ENCODING = "disableChunkedEncoding";
    private static final String USE_HTTPS                = "useHttps";
    private static final String TASK_TIME_OUT            = "taskTimeOut";
    private static final String CREATE_BUCKET            = "createBucket";

    private static final MinFileSystemConfigBuilder BUILDER = new MinFileSystemConfigBuilder();

    /**
     * Gets the singleton builder.
     *
     * @return the singleton builder.
     */
    public static MinFileSystemConfigBuilder getInstance(){
        return BUILDER;
    }

    private MinFileSystemConfigBuilder() {
        super("min.");
    }

    @Override
    protected Class<? extends FileSystem> getConfigClass() {
        return HttpFileSystem.class;
    }

    void setOption(FileSystemOptions opts, String name, Object value) {
        setParam(opts, name, value);
    }

    Object getOption(FileSystemOptions opts, String name) {
        return getParam(opts, name);
    }

    boolean getBooleanOption(FileSystemOptions opts, String name, boolean defaultValue) {
        return getBoolean(opts, name, defaultValue);
    }

    String getStringOption(FileSystemOptions opts, String name, String defaultValue) {
        return getString(opts, name, defaultValue);
    }

    int getIntegerOption(FileSystemOptions opts, String name, int defaultValue) {
        return getInteger(opts, name, defaultValue);
    }

    public boolean getServerSideEncryption(FileSystemOptions opts) {
        return getBooleanOption(opts, SERVER_SIDE_ENCRYPTION, false);
    }

    public void setServerSideEncryption(FileSystemOptions opts, final boolean serverSideEncryption) {
        setOption(opts, SERVER_SIDE_ENCRYPTION, serverSideEncryption);
    }

    /**
     * Don't use chunked encoding for AWS calls - useful for localstack because it doesn't support it.
     *
     * @return true if use https for all communications
     */
    public boolean getDisableChunkedEncoding(FileSystemOptions opts) {
        final MinFileSystemConfigBuilder builder = new MinFileSystemConfigBuilder();

        return builder.getBooleanOption(opts, DISABLE_CHUNKED_ENCODING, false);
    }

    /**
     * Don't use chunked encoding for AWS calls - useful for localstack because it doesn't support it.
     *
     * @param disableChunkedEncoding
     */
    public void setDisableChunkedEncoding(FileSystemOptions opts, boolean disableChunkedEncoding) {
        final MinFileSystemConfigBuilder builder = new MinFileSystemConfigBuilder();

        builder.setOption(opts, DISABLE_CHUNKED_ENCODING, disableChunkedEncoding);
    }

    /**
     * Use https for endpoint calls. true by default
     *
     * @return true if use https for all communications
     */
    public boolean isUseHttps(FileSystemOptions opts) {
        final MinFileSystemConfigBuilder builder = new MinFileSystemConfigBuilder();

        return builder.getBooleanOption(opts, USE_HTTPS, true);
    }

    /**
     * Use https for endpoint calls. true by default
     *
     * @param opts
     * @param useHttps
     */
    public void setUseHttps(FileSystemOptions opts, boolean useHttps) {
        final MinFileSystemConfigBuilder builder = new MinFileSystemConfigBuilder();

        builder.setOption(opts, USE_HTTPS, useHttps);
    }

    /**
     * download upload task time out
     * @param opts
     * @param timeOut unit seconds
     */
    public void setTaskTimeOut(FileSystemOptions opts, Long timeOut) {
        final MinFileSystemConfigBuilder builder = new MinFileSystemConfigBuilder();

        builder.setOption(opts, TASK_TIME_OUT, timeOut);
    }

    public long getTaskTimeOut(FileSystemOptions opts) {
        final MinFileSystemConfigBuilder builder = new MinFileSystemConfigBuilder();

        return builder.getLong(opts, TASK_TIME_OUT, 120);
    }

    /**
     * Should we do 'create bucket' call in case of missed bucket.
     *
     * @return
     */
    public boolean isCreateBucket(FileSystemOptions opts) {
        final MinFileSystemConfigBuilder builder = new MinFileSystemConfigBuilder();

        return builder.getBooleanOption(opts, CREATE_BUCKET, false);
    }

    /**
     * Should we do 'create bucket' call in case of missed bucket.
     *
     * @param opts
     * @param createBucket
     */
    public void setCreateBucket(FileSystemOptions opts, boolean createBucket) {
        final MinFileSystemConfigBuilder builder = new MinFileSystemConfigBuilder();

        builder.setOption(opts, CREATE_BUCKET, createBucket);
    }
}
