/**
 * zoneland.net Inc.
 * Copyright (c) 2002-2013 All Rights Reserved.
 */
package o2.a.build.lib;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;


/**
 * 
 * @author ypz
 * @version $Id: FtpFileHelper.java, v 0.1 2013-6-6 下午4:57:38 ypz Exp $
 */
public class SFtpFileHelper {
    
    private static final Logger logger = LoggerFactory.getLogger(SFtpFileHelper.class);
    private ChannelSftp sftp;
    private Session sshSession;
    private String ftpIp;
    private String username;
    private String password;
    private int port;
    private static final int DEFAULT_PORT = 22;
    
    
    public SFtpFileHelper(String ftpIp,String username,String password,int port){
        this.ftpIp = ftpIp;
        this.username = username;
        this.password = password;
        this.port = port;
    }
    
    public SFtpFileHelper(String ftpIp,String username,String password){
        this(ftpIp,username,password,DEFAULT_PORT);
    }
    
    public boolean uploadFile(File file,String fileName,String directory){
        if(file != null && StringUtils.isNotBlank(fileName) && StringUtils.isNotBlank(directory)){
            try {
                connectFtp();
                if(!changeDir(directory)){// 如果该目录文件夹不存在，则创建一个文件夹
                	sftp.mkdir(directory);
                }
                this.sftp.put(new FileInputStream(file), file.getName());
                return true;
            }catch (Exception e) {
                logger.error("上传文件异常", e);
            }finally{
                try {
                    disConnect();
                } catch (Exception e) {
                    logger.error("释放FTP连接异常", e);
                }
            }
        }
        return false;
    }
    
    public boolean updloadFile(byte[] byteArray,String fileName,String directory){
        if(byteArray != null && byteArray.length > 0 && StringUtils.isNotBlank(fileName) && StringUtils.isNotBlank(directory)){
            try {
                connectFtp();
                if(!changeDir(directory)){
                	// 如果该目录文件夹不存在，则创建一个文件夹,如果有多级则一层一层创建目录
                	String[] directorys=directory.split("/");
                	String  workHome="";
                	for(String dir:directorys){
                		workHome=workHome+"/"+dir;//windows
                		if(!changeDir(workHome)){
                			sftp.mkdir(workHome);
                		}
                	}
                }
                InputStream in = new ByteArrayInputStream(byteArray);
                this.sftp.put(in, fileName);
                return true;
            }catch (Exception e) {
                logger.error("上传文件异常", e);
            }finally{
                try {
                    disConnect();
                } catch (Exception e) {
                    logger.error("释放FTP连接异常", e);
                }
            }
        }
        return false;
    }
    
    public byte[] downloadAsByteArray(String fileName,String directory){
        if(StringUtils.isNotBlank(fileName)){
            try {
                connectFtp();
                
                //进入保存目录
                changeDir(directory);
                InputStream in = this.sftp.get(fileName);
                byte[] bytes = new byte[1024];
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                int c;
                while ( (c=in.read(bytes))!=-1) {
                	bout.write(bytes, 0 ,c);
				}
                byte[] b= bout.toByteArray();
                in.close();
                bout.close();
                return b;
            }catch (Exception e) {
                logger.warn("下载文件异常"+e.getMessage());
            }finally{
                try {
                    disConnect();
                } catch (Exception e) {
                    logger.error("释放FTP连接异常", e);
                }
            }
            
        }
        return null;
    }
    
    public boolean deleteFtpFiles(List<String> fileNames,String directory) throws Exception{
        boolean deleteStatus = false;
        try{
            connectFtp();
            //进入保存目录
            changeDir(directory);
            if(fileNames != null && fileNames.size()>0) {
                for(String fileName : fileNames) {
                    if(StringUtils.isNotBlank(fileName)) {
                    	sftp.rm(fileName);
                    }
                }
            }
        }finally{
            disConnect();
        }
        deleteStatus = true;
        return deleteStatus;
    }
    
    private void connectFtp() throws Exception{
        if(sftp != null && sftp.isConnected()){
            return;
        }
        
        JSch jsch = new JSch(); 
    	this.sshSession = jsch.getSession(this.username, this.ftpIp, this.port); 
    	logger.debug("sftp Session created."); 

    	this.sshSession.setPassword(password); 
    	Properties sshConfig = new Properties(); 
    	sshConfig.put("StrictHostKeyChecking", "no"); 
    	this.sshSession.setConfig(sshConfig); 
    	this.sshSession.connect(20000); 
    	logger.debug("sftp Session connected."); 

    	Channel channel = this.sshSession.openChannel("sftp"); 
    	channel.connect();
    	this.sftp = (ChannelSftp)channel;
    	logger.debug("sftp Connected to " + this.ftpIp + ".");
    }
    
    private void disConnect() throws Exception{
        if(sftp != null && sftp.isConnected()){
        	sftp.disconnect();
        }
        if(sshSession != null && sshSession.isConnected()){
        	sshSession.disconnect();
        }
    }
    
    private boolean changeDir(String directory){
    	boolean flag = false;;
        try {
			if(sftp != null && sftp.isConnected()){
			    if(StringUtils.isNotBlank(directory)){
			    	sftp.cd(directory);
			    	flag = true;
			    }
			}
		} catch (Exception e) {
			logger.warn(directory+"目录不存在");
		}
        return flag;
    }
}
