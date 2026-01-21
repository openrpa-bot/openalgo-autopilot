package com.nigam.openalgo.autopilot.socket.service;

import in.openalgo.OpenAlgo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * Service to manage OpenAlgo connection lifecycle
 */
@Service
public class OpenAlgoConnectionService {

    private static final Logger logger = LogManager.getLogger(OpenAlgoConnectionService.class);

    @Autowired
    private OpenAlgo openAlgoClient;

    @PostConstruct
    public void connect() {
        try {
            logger.info("Connecting to OpenAlgo WebSocket...");
            openAlgoClient.connect();
            logger.info("Successfully connected to OpenAlgo WebSocket");
        } catch (Exception e) {
            logger.error("Failed to connect to OpenAlgo WebSocket", e);
        }
    }

    @PreDestroy
    public void disconnect() {
        try {
            logger.info("Disconnecting from OpenAlgo WebSocket...");
            openAlgoClient.disconnect();
            logger.info("Successfully disconnected from OpenAlgo WebSocket");
        } catch (Exception e) {
            logger.error("Error disconnecting from OpenAlgo WebSocket", e);
        }
    }

    /**
     * Check if the connection is active
     */
    public boolean isConnected() {
        // Note: OpenAlgo client might not expose connection status directly
        // You may need to implement a heartbeat mechanism or check connection state
        return true; // Placeholder - implement based on OpenAlgo API
    }
}
