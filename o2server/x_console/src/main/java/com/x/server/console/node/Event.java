package com.x.server.console.node;

public interface Event {

	public static final String TYPE_REFRESHAPPLICATIONS = "refreshApplications";

	public static final String TYPE_REGISTAPPLICATIONS = "registApplications";

	public static final String TYPE_REGISTAPPLICATIONLOCAL = "registApplicationLocal";

	public static final String TYPE_UPDATEAPPLICATIONS = "updateApplications";

	public static final String TYPE_VOTECENTER = "voteCenter";

	public abstract void execute();

}
