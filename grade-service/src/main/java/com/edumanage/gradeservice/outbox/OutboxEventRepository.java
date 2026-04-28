package com.edumanage.gradeservice.outbox;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
    // Pageable overload prevents loading unbounded result sets into memory
    org.springframework.data.domain.Slice<OutboxEvent> findByPublishedFalseOrderByCreatedAtAsc(
            org.springframework.data.domain.Pageable pageable);
}
