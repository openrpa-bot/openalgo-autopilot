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
 * Listener for Quote updates from OpenAlgo
 * This class provides space for custom logic before forwarding messages to Kafka
 */
@Component
public class QuoteListener {

    private static final Logger logger = LogManager.getLogger(QuoteListener.class);

    @Autowired
    private OpenAlgo openAlgoClient;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    /**
     * Subscribe to Quote updates for the given instruments
     * 
     * @param instruments List of instruments to subscribe to
     *                   Format: List of Maps with "exchange" and "symbol" keys
     */
    public void subscribe(List<Map<String, String>> instruments) {
        logger.info("Subscribing to Quote updates for {} instruments", instruments.size());
        
        openAlgoClient.subscribeQuote(instruments, data -> {
            try {
                // ============================================
                // ADD YOUR CUSTOM LOGIC HERE
                // ============================================
                // Example: Process, transform, validate, or enrich the data
                // processQuoteData(data);
                // transformQuoteData(data);
                // validateQuoteData(data);
                
                logger.debug("Received Quote update: {}", data);
                
                // Forward to Kafka queue
                kafkaProducerService.sendQuoteData(data);
                
            } catch (Exception e) {
                logger.error("Error processing Quote data", e);
            }
        });
    }

    /**
     * Unsubscribe from Quote updates for the given instruments
     */
    public void unsubscribe(List<Map<String, String>> instruments) {
        logger.info("Unsubscribing from Quote updates for {} instruments", instruments.size());
        openAlgoClient.unsubscribeQuote(instruments);
    }

    /**
     * Get cached Quote data for a specific instrument
     * 
     * @param exchange Exchange name (e.g., "MCX")
     * @param symbol Symbol name (e.g., "CRUDEOIL16JAN26FUT")
     * @return Cached Quote data
     */
    public Map<String, Object> getCachedQuote(String exchange, String symbol) {
        return openAlgoClient.getQuotes(exchange, symbol);
    }

    /**
     * Placeholder for custom Quote data processing logic
     * Override this method to add your custom processing
     */
    protected void processQuoteData(Map<String, Object> data) {
        // Add your custom logic here
        // Example: Calculate indicators, store in cache, trigger alerts, etc.
    }

    /**
     * Placeholder for custom Quote data transformation
     * Override this method to transform data before sending to Kafka
     */
    protected void transformQuoteData(Map<String, Object> data) {
        // Add your custom transformation logic here
        // Example: Add metadata, format data, etc.
    }

    /**
     * Placeholder for custom Quote data validation
     * Override this method to validate data before processing
     */
    protected void validateQuoteData(Map<String, Object> data) {
        // Add your custom validation logic here
        // Example: Check for required fields, validate ranges, etc.
    }
}
