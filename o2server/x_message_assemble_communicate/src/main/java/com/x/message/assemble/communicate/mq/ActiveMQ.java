package com.x.message.assemble.communicate.mq;

import java.util.Date;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSslConnectionFactory;

import com.google.gson.Gson;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.MQActive;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.message.core.entity.Message;

import java.io.FileInputStream;
import java.security.KeyStore;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class ActiveMQ implements MQInterface {
	
	private static Logger logger = LoggerFactory.getLogger(ActiveMQ.class);
	private Connection connection = null;
	private MessageProducer producer = null;
	private Session session = null;
			
	private ActiveMQ() {
		try {
			    MQActive configMQ = Config.mq().getActiveMQ();
			    logger.info("MqActive initialize.....");
			    String queueName=configMQ.getQueueName();
			    String url=configMQ.getUrl();
			    url = url.trim();
			    
			    String protocol = url.substring(0, 3);
			   if(protocol.equalsIgnoreCase("tcp")) {
					ConnectionFactory factory=new ActiveMQConnectionFactory(url);
				    this.connection= factory.createConnection();
					this.connection.start();
					
					this.session= this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
					Destination destination=session.createQueue(queueName);
					this.producer = session.createProducer(destination);
					
			   }else {
				    String keyStore = configMQ.getKeyStore();
				    String keyStorePassword = configMQ.getKeyStorePassword();
				    String trustStore = configMQ.getTrustStore();
				    
			        ActiveMQSslConnectionFactory sslConnectionFactory = new ActiveMQSslConnectionFactory();
			        sslConnectionFactory.setBrokerURL(url);
			        sslConnectionFactory.setKeyAndTrustManagers(this.loadKeyManager(keyStore, keyStorePassword), this.loadTrustManager(trustStore),
			                new java.security.SecureRandom());
			        this.connection = sslConnectionFactory.createConnection();
			        this.connection.start();
			        
			        this.session =  this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			        Destination destination = session.createQueue(queueName);
			        this.producer = session.createProducer(destination);
			   }
			   
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		
	}
	
	private static class MQHolder{
	      private static ActiveMQ instance = new ActiveMQ();
    }
	
	 
	 public  static  ActiveMQ  getInstance(){
		   return MQHolder.instance;
	 }

	 
	 public static void main(String[] args) {
		   ActiveMQ MQClient = getInstance();
		   Message msg = new Message();
		   msg.setBody("body");
		   msg.setConsumed(false);
		   msg.setCreateTime(new Date());
		   msg.setPerson("person");
		   MQClient.sendMessage(msg);
	 }

	@Override
	public boolean sendMessage(Message message) {
		  try {
			   Gson gson = new Gson();
	           String msg =  gson.toJson(message);
               TextMessage textMessage= this.session.createTextMessage(msg);
			    this.producer.send(textMessage);
	        } catch (Exception e) {
	             e.printStackTrace();
	             logger.error(e);
	             return false;
	        } finally {
	            
	        }
		return true;
	}

	public void destroy() {
		try {
			logger.info("MqActive destroy.....");
			this.connection.close();
		} catch (JMSException e) {
			 e.printStackTrace();
			 logger.error(e);
		}
	}
	
	
	  /**
        * 加载证书文件
     * @param trustStore
     * @return
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.security.KeyStoreException
     * @throws java.io.IOException
     * @throws java.security.GeneralSecurityException
     */
    public static TrustManager[] loadTrustManager(String trustStore) throws java.security.NoSuchAlgorithmException, java.security.KeyStoreException,
               java.io.IOException, java.security.GeneralSecurityException {
          KeyStore ks = KeyStore. getInstance("JKS");
          ks.load( new FileInputStream(trustStore), null);
          TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory. getDefaultAlgorithm());
          tmf.init(ks);
          return tmf.getTrustManagers();
    }

    /**
         * 加载密钥文件
     * @param keyStore
     * @param keyStorePassword
     * @return
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.security.KeyStoreException
     * @throws java.security.GeneralSecurityException
     * @throws java.security.cert.CertificateException
     * @throws java.io.IOException
     * @throws java.security.UnrecoverableKeyException
     */
    public static KeyManager[] loadKeyManager(String keyStore, String keyStorePassword) throws java.security.NoSuchAlgorithmException,
               java.security.KeyStoreException, java.security.GeneralSecurityException, java.security.cert.CertificateException, java.io.IOException,
               java.security.UnrecoverableKeyException {
          KeyStore ks = KeyStore. getInstance("JKS");
          ks.load( new FileInputStream(keyStore), keyStorePassword.toCharArray());
          KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory. getDefaultAlgorithm());
          kmf.init(ks, keyStorePassword.toCharArray());
          return kmf.getKeyManagers();
    }
}