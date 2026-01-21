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
 * Listener for Depth (Order Book) updates from OpenAlgo
 * This class provides space for custom logic before forwarding messages to Kafka
 */
@Component
public class DepthListener {

    private static final Logger logger = LogManager.getLogger(DepthListener.class);

    @Autowired
    private OpenAlgo openAlgoClient;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    /**
     * Subscribe to Depth updates for the given instruments
     * 
     * @param instruments List of instruments to subscribe to
     *                   Format: List of Maps with "exchange" and "symbol" keys
     */
    public void subscribe(List<Map<String, String>> instruments) {
        logger.info("Subscribing to Depth updates for {} instruments", instruments.size());
        
        openAlgoClient.subscribeDepth(instruments, data -> {
            try {
                // ============================================
                // ADD YOUR CUSTOM LOGIC HERE
                // ============================================
                // Example: Process, transform, validate, or enrich the data
                // processDepthData(data);
                // transformDepthData(data);
                // validateDepthData(data);
                
                logger.debug("Received Depth update: {}", data);
                
                // Forward to Kafka queue
                kafkaProducerService.sendDepthData(data);
                
            } catch (Exception e) {
                logger.error("Error processing Depth data", e);
            }
        });
    }

    /**
     * Unsubscribe from Depth updates for the given instruments
     */
    public void unsubscribe(List<Map<String, String>> instruments) {
        logger.info("Unsubscribing from Depth updates for {} instruments", instruments.size());
        openAlgoClient.unsubscribeDepth(instruments);
    }

    /**
     * Get cached Depth data for a specific instrument
     * 
     * @param exchange Exchange name (e.g., "MCX")
     * @param symbol Symbol name (e.g., "CRUDEOIL16JAN26FUT")
     * @return Cached Depth data
     */
    public Map<String, Object> getCachedDepth(String exchange, String symbol) {
        return openAlgoClient.getDepth(exchange, symbol);
    }

    /**
     * Placeholder for custom Depth data processing logic
     * Override this method to add your custom processing
     */
    protected void processDepthData(Map<String, Object> data) {
        // Add your custom logic here
        // Example: Calculate order book metrics, detect imbalances, etc.
    }

    /**
     * Placeholder for custom Depth data transformation
     * Override this method to transform data before sending to Kafka
     */
    protected void transformDepthData(Map<String, Object> data) {
        // Add your custom transformation logic here
        // Example: Add metadata, format data, etc.
    }

    /**
     * Placeholder for custom Depth data validation
     * Override this method to validate data before processing
     */
    protected void validateDepthData(Map<String, Object> data) {
        // Add your custom validation logic here
        // Example: Check for required fields, validate ranges, etc.
    }
}
