package com.x.message.assemble.communicate.mq;

import java.util.Date;
import java.util.Properties;

import com.google.gson.Gson;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.MQ;
import com.x.base.core.project.config.MQKafka;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.message.core.entity.Message;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class KafkaMQ  implements MQInterface {
	
	private static Logger logger = LoggerFactory.getLogger(KafkaMQ.class);
	
	private  Producer<String, String> producer = null;
	private  String topic = "";
	
	public Producer<String, String> getProducer() {
		return producer;
	}

	public void setProducer(Producer<String, String> producer) {
		this.producer = producer;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	private KafkaMQ() {
		try {
			    MQKafka configMQ = Config.mq().getKafka();
			    logger.info("MQ initialize.....");
			    Properties properties = new Properties();
		        properties.put("bootstrap.servers",  configMQ.getBootstrap_servers());
		        properties.put("acks", configMQ.getAcks());
				properties.put("retries", configMQ.getRetries());
		        properties.put("batch.size", configMQ.getBatch_size());
		        properties.put("linger.ms", configMQ.getLinger_ms());
		        properties.put("buffer.memory", configMQ.getBuffer_memory());
		        
		        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		        
		        this.producer = new KafkaProducer<String, String>(properties);
		        
		        this.topic = configMQ.getTopic();
		        
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		
	}
	
	private static class MQHolder{
	      private static KafkaMQ instance = new KafkaMQ();
    }
	
	 
	 public  static  KafkaMQ  getInstance(){
		   return MQHolder.instance;
	 }

	 public static void main(String[] args) {
		   KafkaMQ MQClient = getInstance();
		   Message msg = new Message();
		   msg.setBody("body");
		   msg.setConsumed(false);
		   msg.setCreateTime(new Date());
		   msg.setPerson("person");
		   System.out.println(MQClient.sendMessage(msg));
	 }

	@Override
	public boolean sendMessage(Message message) {
		  try {
			   Gson gson = new Gson();
	           String msg =  gson.toJson(message);
	           this.producer.send(new ProducerRecord<String, String>(this.getTopic(), msg));
	        } catch (Exception e) {
	            e.printStackTrace();
	            logger.error(e);
	            return false;
	        } finally {
	           // this.producer.close();
	        }
		return true;
	}

	public void destroy() {
		   this.producer.close();
	    }
}
