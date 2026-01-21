package com.nigam.openalgo.autopilot.socket.example;

import com.nigam.openalgo.autopilot.socket.listener.DepthListener;
import com.nigam.openalgo.autopilot.socket.listener.LtpListener;
import com.nigam.openalgo.autopilot.socket.listener.QuoteListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Example class demonstrating how to use the OpenAlgo listeners
 * This is a reference implementation - you can create your own service/controller
 * to manage subscriptions based on your requirements
 */
@Component
public class OpenAlgoListenerExample {

    @Autowired
    private LtpListener ltpListener;

    @Autowired
    private QuoteListener quoteListener;

    @Autowired
    private DepthListener depthListener;

    /**
     * Example: Subscribe to LTP updates
     */
    public void subscribeToLtp() {
        List<Map<String, String>> instruments = new ArrayList<>();
        instruments.add(Map.of("exchange", "MCX", "symbol", "CRUDEOIL16JAN26FUT"));
        // Add more instruments as needed
        
        ltpListener.subscribe(instruments);
    }

    /**
     * Example: Subscribe to Quote updates
     */
    public void subscribeToQuote() {
        List<Map<String, String>> instruments = new ArrayList<>();
        instruments.add(Map.of("exchange", "MCX", "symbol", "CRUDEOIL16JAN26FUT"));
        // Add more instruments as needed
        
        quoteListener.subscribe(instruments);
    }

    /**
     * Example: Subscribe to Depth updates
     */
    public void subscribeToDepth() {
        List<Map<String, String>> instruments = new ArrayList<>();
        instruments.add(Map.of("exchange", "MCX", "symbol", "CRUDEOIL16JAN26FUT"));
        // Add more instruments as needed
        
        depthListener.subscribe(instruments);
    }

    /**
     * Example: Get cached LTP data
     */
    public Map<String, Object> getCachedLtp(String exchange, String symbol) {
        return ltpListener.getCachedLtp(exchange, symbol);
    }

    /**
     * Example: Get cached Quote data
     */
    public Map<String, Object> getCachedQuote(String exchange, String symbol) {
        return quoteListener.getCachedQuote(exchange, symbol);
    }

    /**
     * Example: Get cached Depth data
     */
    public Map<String, Object> getCachedDepth(String exchange, String symbol) {
        return depthListener.getCachedDepth(exchange, symbol);
    }

    /**
     * Example: Unsubscribe from LTP updates
     */
    public void unsubscribeFromLtp(List<Map<String, String>> instruments) {
        ltpListener.unsubscribe(instruments);
    }

    /**
     * Example: Unsubscribe from Quote updates
     */
    public void unsubscribeFromQuote(List<Map<String, String>> instruments) {
        quoteListener.unsubscribe(instruments);
    }

    /**
     * Example: Unsubscribe from Depth updates
     */
    public void unsubscribeFromDepth(List<Map<String, String>> instruments) {
        depthListener.unsubscribe(instruments);
    }
}
