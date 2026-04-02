package edu.pte.ttk.istallo_kezelo.repository;

import edu.pte.ttk.istallo_kezelo.model.AppSetting;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository a(z) AppSetting entitásokhoz.
 */
public interface AppSettingRepository extends JpaRepository<AppSetting, String> {
}
