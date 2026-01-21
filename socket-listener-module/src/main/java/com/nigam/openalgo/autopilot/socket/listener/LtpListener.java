package com.nigam.openalgo.autopilot.socket.listener;

import com.nigam.openalgo.autopilot.socket.service.KafkaProducerService;
import in.openalgo.OpenAlgo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Listener for Last Traded Price (LTP) updates from OpenAlgo
 * This class provides space for custom logic before forwarding messages to Kafka
 */
@Component
public class LtpListener {

    private static final Logger logger = LogManager.getLogger(LtpListener.class);

    @Autowired
    private OpenAlgo openAlgoClient;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    /**
     * Subscribe to LTP updates for the given instruments
     * 
     * @param instruments List of instruments to subscribe to
     *                   Format: List of Maps with "exchange" and "symbol" keys
     */
    public void subscribe(List<Map<String, String>> instruments) {
        logger.info("Subscribing to LTP updates for {} instruments", instruments.size());
        
        openAlgoClient.subscribeLtp(instruments, data -> {
            try {
                // ============================================
                // ADD YOUR CUSTOM LOGIC HERE
                // ============================================
                // Example: Process, transform, validate, or enrich the data
                // processLtpData(data);
                // transformLtpData(data);
                // validateLtpData(data);
                
                logger.debug("Received LTP update: {}", data);
                
                // Forward to Kafka queue
                kafkaProducerService.sendLtpData(data);
                
            } catch (Exception e) {
                logger.error("Error processing LTP data", e);
            }
        });
    }

    /**
     * Unsubscribe from LTP updates for the given instruments
     */
    public void unsubscribe(List<Map<String, String>> instruments) {
        logger.info("Unsubscribing from LTP updates for {} instruments", instruments.size());
        openAlgoClient.unsubscribeLtp(instruments);
    }

    /**
     * Get cached LTP data for a specific instrument
     * 
     * @param exchange Exchange name (e.g., "MCX")
     * @param symbol Symbol name (e.g., "CRUDEOIL16JAN26FUT")
     * @return Cached LTP data
     */
    public Map<String, Object> getCachedLtp(String exchange, String symbol) {
        return openAlgoClient.getLtp(exchange, symbol);
    }

    /**
     * Placeholder for custom LTP data processing logic
     * Override this method to add your custom processing
     */
    protected void processLtpData(Map<String, Object> data) {
        // Add your custom logic here
        // Example: Calculate indicators, store in cache, trigger alerts, etc.
    }

    /**
     * Placeholder for custom LTP data transformation
     * Override this method to transform data before sending to Kafka
     */
    protected void transformLtpData(Map<String, Object> data) {
        // Add your custom transformation logic here
        // Example: Add metadata, format data, etc.
    }

    /**
     * Placeholder for custom LTP data validation
     * Override this method to validate data before processing
     */
    protected void validateLtpData(Map<String, Object> data) {
        // Add your custom validation logic here
        // Example: Check for required fields, validate ranges, etc.
    }
}
