package com.example.hotelbookingapplication.configuration;

import com.example.hotelbookingapplication.model.kafka.BookingEvent;
import com.example.hotelbookingapplication.model.kafka.UserEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {


    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${app.kafka.hotelBookingMessageGroupId}")
    private String kafkaHotelBookingMessageGroup;

    @Bean
    public NewTopic userRegistration(){
        return new NewTopic("user_registered",1,(short) 1);
    }

    @Bean
    public NewTopic hotelBooking(){
        return new NewTopic("hotel_booking",1,(short) 1);
    }

    @Bean
    public <T>ProducerFactory<String, T> kafkaMessageProduceFactory(ObjectMapper objectMapper){
        Map<String,Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(config,new StringSerializer(),new JsonSerializer<>(objectMapper));
    }

    @Bean
    public KafkaTemplate<String,UserEvent> kafkaTemplateUserEvent(ProducerFactory<String,UserEvent> kafkaMessageProduceFactory){
        return new KafkaTemplate<>(kafkaMessageProduceFactory);
    }

    @Bean
    public KafkaTemplate<String,BookingEvent> kafkaTemplateBookingEvent(ProducerFactory<String, BookingEvent> kafkaMessageProduceFactory){
        return new KafkaTemplate<>(kafkaMessageProduceFactory);
    }

    @Bean
    public <T>ConsumerFactory<String,T> kafkaMessageConsumer(ObjectMapper objectMapper){
        Map<String,Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaHotelBookingMessageGroup);
        config.put(JsonDeserializer.TRUSTED_PACKAGES,"*");

        return new DefaultKafkaConsumerFactory<>(config,new StringDeserializer(),new JsonDeserializer<>(objectMapper));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String,UserEvent> kafkaUserEventContainer(ConsumerFactory<String,UserEvent> kafkaMessageConsumer){
        ConcurrentKafkaListenerContainerFactory<String,UserEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(kafkaMessageConsumer);
        return factory;

    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String,BookingEvent> kafkaBookingEventContainer(ConsumerFactory<String,BookingEvent> kafkaMessageConsumer){
        ConcurrentKafkaListenerContainerFactory<String,BookingEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(kafkaMessageConsumer);
        return factory;

    }

}
