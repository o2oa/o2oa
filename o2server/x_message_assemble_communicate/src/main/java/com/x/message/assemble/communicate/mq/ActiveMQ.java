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
import com.google.gson.Gson;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.MQActive;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.message.core.entity.Message;

public class ActiveMQ implements MQInterface {
	
	private static Logger logger = LoggerFactory.getLogger(ActiveMQ.class);
	private Connection connection = null;
	private MessageProducer producer = null;
	private Session session = null;
			
	private ActiveMQ() {
		try {
			    MQActive configMQ = Config.mq().getActiveMQ();
			    logger.info("MqActive initialize.....");
			    
			    String url=configMQ.getUrl();
			    String queueName=configMQ.getQueueName();
	
				ConnectionFactory factory=new ActiveMQConnectionFactory(url);
			    this.connection= factory.createConnection();
				this.connection.start();
				this.session= this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
				Destination destination=session.createQueue(queueName);
				this.producer = session.createProducer(destination);
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
		   //System.out.println(MQClient.getTopic());
		   Message msg = new Message();
		   msg.setBody("body");
		   msg.setConsumed(false);
		   msg.setCreateTime(new Date());
		   msg.setPerson("person");
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
		 System.out.println("MqActive destroy.....");
		  try {
			this.connection.close();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			 logger.error(e);
		}
	   }
}