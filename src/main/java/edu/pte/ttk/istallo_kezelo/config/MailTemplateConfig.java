package edu.pte.ttk.istallo_kezelo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Konfiguráció, amely UTF-8 mail sablonokat tölt be properties fájlból.
 */
@Configuration
@PropertySource(value = "classpath:mail-templates.properties", encoding = "UTF-8")
public class MailTemplateConfig {
}
