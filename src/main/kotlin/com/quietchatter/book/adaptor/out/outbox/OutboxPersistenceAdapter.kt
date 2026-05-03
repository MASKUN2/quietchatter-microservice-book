package com.quietchatter.book.adaptor.out.outbox

import com.quietchatter.book.application.port.out.OutboxEventPersistable
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class OutboxPersistenceAdapter(
    private val outboxEventRepository: OutboxEventRepository
) : OutboxEventPersistable {
    override fun save(event: OutboxEvent): OutboxEvent = outboxEventRepository.save(event)

    override fun findUnprocessed(limit: Int): List<OutboxEvent> =
        outboxEventRepository.findByProcessedAtIsNullOrderByCreatedAtAsc(PageRequest.of(0, limit))

    override fun deleteProcessedBefore(cutoff: LocalDateTime): Long =
        outboxEventRepository.deleteByProcessedAtIsNotNullAndProcessedAtBefore(cutoff)
}
