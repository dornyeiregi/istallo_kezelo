package edu.pte.ttk.istallo_kezelo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import edu.pte.ttk.istallo_kezelo.model.NotificationLog;

/**
 * Spring Data repository a(z) NotificationLog entitásokhoz.
 */
@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
    @Query("""
        SELECT CASE WHEN COUNT(n) > 0 THEN TRUE ELSE FALSE END
        FROM NotificationLog n
        WHERE n.eventType = :eventType
          AND n.entityId = :entityId
          AND ((:horseId IS NULL AND n.horseId IS NULL) OR n.horseId = :horseId)
          AND n.daysBefore = :daysBefore
        """)
    boolean existsByEventKey(@Param("eventType") String eventType,
                             @Param("entityId") Long entityId,
                             @Param("horseId") Long horseId,
                             @Param("daysBefore") Integer daysBefore);
}
