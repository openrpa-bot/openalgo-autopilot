package com.nigam.openalgo.autopilot.socket.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class KafkaProducerService {

    private static final Logger logger = LogManager.getLogger(KafkaProducerService.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${kafka.topic.ltp}")
    private String ltpTopic;

    @Value("${kafka.topic.quote}")
    private String quoteTopic;

    @Value("${kafka.topic.depth}")
    private String depthTopic;

    /**
     * Send LTP data to Kafka
     */
    public void sendLtpData(Map<String, Object> data) {
        try {
            String jsonData = objectMapper.writeValueAsString(data);
            kafkaTemplate.send(ltpTopic, jsonData);
            logger.debug("Sent LTP data to Kafka topic: {}", ltpTopic);
        } catch (JsonProcessingException e) {
            logger.error("Error serializing LTP data to JSON", e);
        } catch (Exception e) {
            logger.error("Error sending LTP data to Kafka", e);
        }
    }

    /**
     * Send Quote data to Kafka
     */
    public void sendQuoteData(Map<String, Object> data) {
        try {
            String jsonData = objectMapper.writeValueAsString(data);
            kafkaTemplate.send(quoteTopic, jsonData);
            logger.debug("Sent Quote data to Kafka topic: {}", quoteTopic);
        } catch (JsonProcessingException e) {
            logger.error("Error serializing Quote data to JSON", e);
        } catch (Exception e) {
            logger.error("Error sending Quote data to Kafka", e);
        }
    }

    /**
     * Send Depth data to Kafka
     */
    public void sendDepthData(Map<String, Object> data) {
        try {
            String jsonData = objectMapper.writeValueAsString(data);
            kafkaTemplate.send(depthTopic, jsonData);
            logger.debug("Sent Depth data to Kafka topic: {}", depthTopic);
        } catch (JsonProcessingException e) {
            logger.error("Error serializing Depth data to JSON", e);
        } catch (Exception e) {
            logger.error("Error sending Depth data to Kafka", e);
        }
    }
}
