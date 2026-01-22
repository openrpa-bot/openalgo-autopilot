package com.nigam.openalgo.autopilot.dblayer.repository;

import com.nigam.openalgo.autopilot.dblayer.entity.ConfigurationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfigurationRepository extends JpaRepository<ConfigurationEntity, Long> {
    
    /**
     * Find active configuration override by key
     */
    Optional<ConfigurationEntity> findByKeyAndIsActiveTrue(String key);
    
    /**
     * Find configuration override by key (regardless of active status)
     */
    Optional<ConfigurationEntity> findByKey(String key);
    
    /**
     * Find all active configuration overrides
     */
    List<ConfigurationEntity> findByIsActiveTrue();
    
    /**
     * Find all active configuration overrides by category
     */
    List<ConfigurationEntity> findByCategoryAndIsActiveTrue(String category);
    
    /**
     * Check if a configuration key exists
     */
    boolean existsByKey(String key);
    
    /**
     * Find all distinct categories
     */
    @Query("SELECT DISTINCT c.category FROM ConfigurationEntity c WHERE c.isActive = true AND c.category IS NOT NULL")
    List<String> findDistinctCategories();
}
