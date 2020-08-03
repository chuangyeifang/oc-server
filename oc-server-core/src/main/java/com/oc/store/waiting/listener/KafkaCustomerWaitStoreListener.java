package com.oc.store.waiting.listener;

import com.oc.produce.KafkaClient;
import com.oc.provider.context.SpringContext;
import com.oc.session.Customer;
import com.oc.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 同步kafka
 * @author chuangyeifang
 */
public class KafkaCustomerWaitStoreListener implements CustomerWaitStoreListener {

    private static Logger log = LoggerFactory.getLogger(KafkaCustomerWaitStoreListener.class);

    private static KafkaClient kafkaClient = SpringContext.getBean(KafkaClient.class);

    @Override
    public void enterQueue(Customer customer, int queueSize) {
        try {
            String endChatTopic = "ocim-customer-queue";
            kafkaClient.sendMsg(endChatTopic, JsonUtils.getJson().writeString(customer));
        } catch (IOException e) {
            log.info("Object to json string error: {}", e.getMessage());
        }
    }

    @Override
    public void leaveQueue(Customer customer, int queueSize) {

    }
}
