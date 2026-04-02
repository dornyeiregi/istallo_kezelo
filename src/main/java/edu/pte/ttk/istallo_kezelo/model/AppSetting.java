package edu.pte.ttk.istallo_kezelo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * JPA entity for application-level boolean settings.
 */
@Getter
@Setter
@Entity
@AllArgsConstructor
@Table(name = "app_setting")
public class AppSetting {

    @Id
    @Column(name = "setting_key", nullable = false, length = 100)
    private String key;

    @Column(name = "bool_value", nullable = false)
    private Boolean boolValue;

    /**
     * Üres konstruktor a JPA-hoz.
     */
    public AppSetting() {
    }
}
