package com.edumanage.reportservice.consumer;

import com.edumanage.reportservice.event.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ReportKafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private Map<String, Object> baseProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "report-service-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }

    private <T> ConcurrentKafkaListenerContainerFactory<String, T> factory(Class<T> type) {
        JsonDeserializer<T> deser = new JsonDeserializer<>(type);
        deser.addTrustedPackages("com.edumanage.*");
        ConsumerFactory<String, T> cf = new DefaultKafkaConsumerFactory<>(
                baseProps(), new StringDeserializer(), deser);
        ConcurrentKafkaListenerContainerFactory<String, T> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(cf);
        return factory;
    }

    @Bean public ConcurrentKafkaListenerContainerFactory<String, StudentEnrolledEvent> studentEnrolledReportFactory() { return factory(StudentEnrolledEvent.class); }
    @Bean public ConcurrentKafkaListenerContainerFactory<String, AttendanceMarkedEvent> attendanceMarkedReportFactory() { return factory(AttendanceMarkedEvent.class); }
    @Bean public ConcurrentKafkaListenerContainerFactory<String, GradePublishedEvent>   gradePublishedReportFactory()   { return factory(GradePublishedEvent.class); }
    @Bean public ConcurrentKafkaListenerContainerFactory<String, FeePaidEvent>          feePaidReportFactory()          { return factory(FeePaidEvent.class); }
}
