package com.x.server.console.server.storage;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.ftpserver.ftplet.Authentication;
import org.apache.ftpserver.ftplet.AuthenticationFailedException;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.usermanager.AnonymousAuthentication;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;

public class StorageUserManager implements UserManager {

	private List<? extends User> users = new ArrayList<>();

	public StorageUserManager(List<? extends User> users) {
		// super(adminName, passwordEncryptor);
		this.users = users;
	}

	@Override
	public User authenticate(Authentication authentication) throws AuthenticationFailedException {
		if (authentication instanceof UsernamePasswordAuthentication) {
			UsernamePasswordAuthentication upauth = (UsernamePasswordAuthentication) authentication;
			String name = upauth.getUsername();
			String password = upauth.getPassword();
			if (name == null) {
				throw new AuthenticationFailedException("Authentication failed");
			}
			if (password == null) {
				password = "";
			}
			User user;
			try {
				user = getUserByName(name);
			} catch (FtpException e) {
				throw new AuthenticationFailedException("Authentication failed");
			}
			if (null == user) {
				throw new AuthenticationFailedException("Authentication failed");
			}
			if (StringUtils.equals(user.getPassword(), password)) {
				return user;
			} else {
				throw new AuthenticationFailedException("Authentication failed");
			}
		} else if (authentication instanceof AnonymousAuthentication) {
			throw new AuthenticationFailedException("Authentication failed");
		} else {
			throw new IllegalArgumentException("Authentication not supported by this user manager");
		}
	}

	@Override
	public void delete(String arg0) throws FtpException {
	}

	@Override
	public boolean doesExist(String arg) throws FtpException {
		User user = this.getUserByName(arg);
		if (null != user) {
			return true;
		}
		return false;
	}

	@Override
	public String getAdminName() {
		return "xadmin";
	}

	@Override
	public String[] getAllUserNames() throws FtpException {
		String[] names = new String[users.size()];
		for (int i = 0; i < users.size(); i++) {
			names[i] = users.get(i).getName();
		}
		return names;
	}

	@Override
	public User getUserByName(String arg) throws FtpException {
		for (User o : users) {
			if (StringUtils.equals(o.getName(), arg)) {
				return o;
			}
		}
		return null;
	}

	@Override
	public boolean isAdmin(String arg) throws FtpException {
		if (StringUtils.equals("xadmin", arg)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void save(User arg0) throws FtpException {

	}

}
