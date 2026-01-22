package com.nigam.openalgo.autopilot.configuration.controller;

import com.nigam.openalgo.autopilot.configuration.dto.ConfigurationDto;
import com.nigam.openalgo.autopilot.configuration.service.ConfigurationService;
import com.nigam.openalgo.autopilot.dblayer.entity.ConfigurationEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for configuration management UI
 */
@Controller
@RequestMapping("/configuration")
@RequiredArgsConstructor
@Slf4j
public class ConfigurationController {
    
    private final ConfigurationService configurationService;
    
    /**
     * Display all configurations grouped by category
     */
    @GetMapping
    public String listConfigurations(
            @RequestParam(required = false) String category,
            Model model) {
        
        Map<String, ConfigurationService.ConfigurationSource> allConfigs = 
            configurationService.getAllConfigurations();
        
        // Group by category
        Map<String, List<ConfigurationDto>> configsByCategory = new TreeMap<>();
        
        for (Map.Entry<String, ConfigurationService.ConfigurationSource> entry : allConfigs.entrySet()) {
            String cat = entry.getValue().getCategory() != null ? 
                entry.getValue().getCategory() : "general";
            
            if (category == null || category.equals(cat)) {
                ConfigurationDto dto = ConfigurationDto.builder()
                    .key(entry.getKey())
                    .value(entry.getValue().getValue())
                    .currentValue(entry.getValue().getValue())
                    .category(cat)
                    .description(entry.getValue().getDescription())
                    .source(entry.getValue().getPriority().name())
                    .build();
                
                configsByCategory.computeIfAbsent(cat, k -> new ArrayList<>()).add(dto);
            }
        }
        
        // Sort configurations within each category
        configsByCategory.forEach((cat, configs) -> {
            configs.sort(Comparator.comparing(ConfigurationDto::getKey));
        });
        
        model.addAttribute("configsByCategory", configsByCategory);
        model.addAttribute("categories", configurationService.getAllCategories());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("overrideCount", configurationService.getOverrideCount());
        
        return "configuration/list";
    }
    
    /**
     * Show form to add/edit configuration
     */
    @GetMapping("/edit")
    public String showEditForm(
            @RequestParam(required = false) String key,
            Model model) {
        
        ConfigurationDto dto;
        if (key != null && !key.isEmpty()) {
            // Load existing configuration
            String value = configurationService.getValue(key);
            dto = ConfigurationDto.builder()
                .key(key)
                .value(value)
                .currentValue(value)
                .build();
        } else {
            dto = new ConfigurationDto();
        }
        
        model.addAttribute("config", dto);
        model.addAttribute("categories", configurationService.getAllCategories());
        
        return "configuration/edit";
    }
    
    /**
     * Save configuration override
     */
    @PostMapping("/save")
    public String saveConfiguration(
            @ModelAttribute ConfigurationDto dto,
            @RequestParam(defaultValue = "system") String updatedBy,
            RedirectAttributes redirectAttributes) {
        
        try {
            configurationService.saveOverride(
                dto.getKey(),
                dto.getValue(),
                dto.getCategory(),
                dto.getDescription(),
                updatedBy
            );
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Configuration '" + dto.getKey() + "' saved successfully!");
        } catch (Exception e) {
            log.error("Error saving configuration", e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error saving configuration: " + e.getMessage());
        }
        
        return "redirect:/configuration";
    }
    
    /**
     * Delete configuration override
     */
    @PostMapping("/delete")
    public String deleteConfiguration(
            @RequestParam String key,
            RedirectAttributes redirectAttributes) {
        
        try {
            configurationService.deleteOverride(key);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Configuration '" + key + "' deleted successfully!");
        } catch (Exception e) {
            log.error("Error deleting configuration", e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error deleting configuration: " + e.getMessage());
        }
        
        return "redirect:/configuration";
    }
    
    /**
     * Save to Redis
     */
    @PostMapping("/save-redis")
    public String saveToRedis(
            @RequestParam String key,
            @RequestParam String value,
            RedirectAttributes redirectAttributes) {
        
        try {
            configurationService.saveToRedis(key, value);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Configuration saved to Redis: " + key);
        } catch (Exception e) {
            log.error("Error saving to Redis", e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error saving to Redis: " + e.getMessage());
        }
        
        return "redirect:/configuration";
    }
    
    /**
     * Reset all configuration overrides (delete all UI overrides)
     */
    @PostMapping("/reset-all")
    public String resetAllConfigurations(
            RedirectAttributes redirectAttributes) {
        
        try {
            int deletedCount = configurationService.deleteAllOverrides();
            redirectAttributes.addFlashAttribute("successMessage", 
                "Successfully reset " + deletedCount + " configuration override(s). All configurations restored to default values from application.properties.");
        } catch (Exception e) {
            log.error("Error resetting all configurations", e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error resetting configurations: " + e.getMessage());
        }
        
        return "redirect:/configuration";
    }
    
    /**
     * Get configuration value via AJAX
     */
    @GetMapping("/value/{key}")
    @ResponseBody
    public Map<String, String> getValue(@PathVariable String key) {
        String value = configurationService.getValue(key);
        Map<String, String> response = new HashMap<>();
        response.put("key", key);
        response.put("value", value != null ? value : "");
        return response;
    }
}
