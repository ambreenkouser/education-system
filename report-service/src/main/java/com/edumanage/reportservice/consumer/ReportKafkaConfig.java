package com.edumanage.reportservice.consumer;

import com.edumanage.reportservice.event.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class ReportKafkaConfig {

    private static final String DLQ_SUFFIX = ".dlq";
    // 3 retries with 2-second gap before routing to DLQ
    private static final FixedBackOff RETRY_POLICY = new FixedBackOff(2000L, 3L);

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private Map<String, Object> baseConsumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "report-service-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        // Commit offset only after successful processing
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        return props;
    }

    /** KafkaTemplate used exclusively by the DLQ recoverer */
    @Bean
    public KafkaTemplate<String, String> dlqKafkaTemplate() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props));
    }

    /** Routes failed messages to <original-topic>.dlq after exhausting retries */
    @Bean
    public CommonErrorHandler reportErrorHandler(KafkaTemplate<String, String> dlqKafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                dlqKafkaTemplate,
                (record, ex) -> {
                    log.error("Routing failed record from topic={} to DLQ. Error: {}",
                            record.topic(), ex.getMessage(), ex);
                    return new org.apache.kafka.common.TopicPartition(
                            record.topic() + DLQ_SUFFIX, record.partition());
                });
        return new DefaultErrorHandler(recoverer, RETRY_POLICY);
    }

    private <T> ConcurrentKafkaListenerContainerFactory<String, T> factory(
            Class<T> type, CommonErrorHandler errorHandler) {
        JsonDeserializer<T> deser = new JsonDeserializer<>(type);
        deser.addTrustedPackages("com.edumanage.*");
        ConsumerFactory<String, T> cf = new DefaultKafkaConsumerFactory<>(
                baseConsumerProps(), new StringDeserializer(), deser);
        ConcurrentKafkaListenerContainerFactory<String, T> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(cf);
        factory.setCommonErrorHandler(errorHandler);
        // RECORD mode: offset committed per message, not per batch
        factory.getContainerProperties()
               .setAckMode(org.springframework.kafka.listener.ContainerProperties.AckMode.RECORD);
        return factory;
    }

    @Bean public ConcurrentKafkaListenerContainerFactory<String, StudentEnrolledEvent> studentEnrolledReportFactory(CommonErrorHandler reportErrorHandler) { return factory(StudentEnrolledEvent.class, reportErrorHandler); }
    @Bean public ConcurrentKafkaListenerContainerFactory<String, AttendanceMarkedEvent> attendanceMarkedReportFactory(CommonErrorHandler reportErrorHandler) { return factory(AttendanceMarkedEvent.class, reportErrorHandler); }
    @Bean public ConcurrentKafkaListenerContainerFactory<String, GradePublishedEvent>   gradePublishedReportFactory(CommonErrorHandler reportErrorHandler)   { return factory(GradePublishedEvent.class, reportErrorHandler); }
    @Bean public ConcurrentKafkaListenerContainerFactory<String, FeePaidEvent>          feePaidReportFactory(CommonErrorHandler reportErrorHandler)          { return factory(FeePaidEvent.class, reportErrorHandler); }
}
