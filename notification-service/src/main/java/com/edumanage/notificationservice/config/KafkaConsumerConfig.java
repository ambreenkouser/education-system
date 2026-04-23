package com.edumanage.notificationservice.config;

import com.edumanage.notificationservice.event.*;
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
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private Map<String, Object> baseConsumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "notification-service-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }

    private <T> ConcurrentKafkaListenerContainerFactory<String, T> factory(Class<T> targetType) {
        Map<String, Object> props = baseConsumerProps();
        JsonDeserializer<T> deserializer = new JsonDeserializer<>(targetType);
        deserializer.addTrustedPackages("com.edumanage.*");
        ConsumerFactory<String, T> cf = new DefaultKafkaConsumerFactory<>(props,
                new StringDeserializer(), deserializer);
        ConcurrentKafkaListenerContainerFactory<String, T> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(cf);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserCreatedEvent>
    userCreatedKafkaListenerContainerFactory() {
        return factory(UserCreatedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, StudentEnrolledEvent>
    studentEnrolledKafkaListenerContainerFactory() {
        return factory(StudentEnrolledEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AttendanceMarkedEvent>
    attendanceMarkedKafkaListenerContainerFactory() {
        return factory(AttendanceMarkedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, GradePublishedEvent>
    gradePublishedKafkaListenerContainerFactory() {
        return factory(GradePublishedEvent.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FeePaidEvent>
    feePaidKafkaListenerContainerFactory() {
        return factory(FeePaidEvent.class);
    }
}
