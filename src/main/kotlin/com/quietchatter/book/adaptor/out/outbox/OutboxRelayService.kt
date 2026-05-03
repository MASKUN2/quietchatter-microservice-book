package com.quietchatter.book.adaptor.out.outbox

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.quietchatter.book.adaptor.out.messaging.BookIntegrationEvent
import com.quietchatter.book.application.port.out.OutboxEventPersistable
import org.slf4j.LoggerFactory
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.support.MessageBuilder
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class OutboxRelayService(
    private val outboxEventPersistable: OutboxEventPersistable,
    private val streamBridge: StreamBridge,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(OutboxRelayService::class.java)

    @Scheduled(cron = "\${outbox.cleanup.cron:0 0 * * * *}")
    @Transactional
    fun cleanupProcessedEvents() {
        val cutoff = LocalDateTime.now().minusDays(7)
        val deleted = outboxEventPersistable.deleteProcessedBefore(cutoff)
        if (deleted > 0) log.info("Deleted $deleted processed outbox events older than 7 days")
    }

    @Scheduled(fixedDelayString = "\${outbox.relay.fixed-delay:1000}")
    @Transactional
    fun relayEvents() {
        val events = outboxEventPersistable.findUnprocessed(100)
        events.forEach { event ->
            runCatching {
                val payloadMap: Map<String, Any?> = objectMapper.readValue(event.payload)
                val integrationEvent = BookIntegrationEvent(
                    id = event.id.toString(),
                    source = "/book",
                    type = "com.quietchatter.book.${event.type}",
                    time = event.createdAt.atOffset(ZoneOffset.UTC).toString(),
                    subject = event.aggregateId,
                    data = payloadMap
                )

                val message = MessageBuilder.withPayload(integrationEvent)
                    .setHeader(KafkaHeaders.KEY, event.aggregateId.toByteArray())
                    .build()

                if (streamBridge.send("bookEvents-out-0", message)) {
                    event.markProcessed()
                    outboxEventPersistable.save(event)
                    log.debug("Successfully relayed outbox event: ${event.id}")
                } else {
                    log.error("Failed to relay outbox event: ${event.id}")
                }
            }.onFailure { e ->
                log.error("Error processing outbox event: ${event.id}", e)
            }
        }
    }
}
