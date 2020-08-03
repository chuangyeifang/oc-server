package com.oc.produce;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import com.oc.produce.properties.KafkaProperties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

@Component
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaClient {

	private static final Logger log = LoggerFactory.getLogger(KafkaClient.class);
	
	@Autowired
	private KafkaProperties kafkaProperties;

	private static Producer<String, String> producer;
	private static BlockingQueue<KeyedMessage<String, String>> queue = new LinkedBlockingQueue<KeyedMessage<String, String>>();
	private static boolean running = true;

	public void start() {
		if (!kafkaProperties.getOpen()) {
			log.info("未启用   Kafka 生产者");
			return;
		}
		if (null == producer) {
			log.info("开启启动   Kafka 生产者");
			Properties props = new Properties();
			props.put("metadata.broker.list", kafkaProperties.getBrokerList());
			props.put("serializer.class", kafkaProperties.getSerializerClass());
			props.put("request.required.acks", kafkaProperties.getRequestRequiredAcks());
			ProducerConfig config = new ProducerConfig(props);
			producer = new Producer<String, String>(config);
			scheduler();
			log.info("Kafka 生产者    启动成功");
		}
	}
	
	private void scheduler() {
		KafkaMessageScheduler sc = new KafkaMessageScheduler();
		Thread thread = new Thread(sc, "kafka-produce-thread-01");
		thread.setDaemon(true);
		thread.start();
	}

	public void sendMsg(String topic, String message) {
		if (kafkaProperties.getOpen()) {
			if (queue.size() > 100000) {
				return;
			}
			KeyedMessage<String, String> data = new KeyedMessage<String, String>(topic, message);
			queue.offer(data);
		}
	}

	private class KafkaMessageScheduler implements Runnable {

		@Override
		public void run() {
			List<KeyedMessage<String, String>> list = new ArrayList<KeyedMessage<String, String>>(100);
			while (running) {
				try {
					int size = queue.drainTo(list, 100);
					if (size > 0) {
						producer.send(list);
						list.clear();
					} else {
						TimeUnit.MILLISECONDS.sleep(100);
					}
				} catch (InterruptedException e) {
					log.error("Kafka 生产消息发生异常: {}", e);
				}
			}
			
		}
	}
}
