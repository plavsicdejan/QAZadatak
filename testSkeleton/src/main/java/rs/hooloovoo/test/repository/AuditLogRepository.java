package rs.hooloovoo.test.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rs.hooloovoo.test.entities.AuditLogEntry;

@Repository
public interface AuditLogRepository extends CrudRepository<AuditLogEntry, Long> {

}
