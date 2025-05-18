package com.example.hotelbookingapplication.listener;

import com.example.hotelbookingapplication.model.kafka.BookingEvent;
import com.example.hotelbookingapplication.model.kafka.UserEvent;
import com.example.hotelbookingapplication.model.mongodb.BookingRegistrationStats;
import com.example.hotelbookingapplication.model.mongodb.RegistrationUserStats;
import com.example.hotelbookingapplication.service.mongodb.BookingRegistrationStatService;
import com.example.hotelbookingapplication.service.mongodb.RegistrationUserStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaEventListener {

    private final RegistrationUserStatsService registrationUserStatsService;

    private final BookingRegistrationStatService bookingRegistrationStatService;

    @KafkaListener(topics = "user_registered",
            groupId = "${app.kafka.hotelBookingMessageGroupId}",
            containerFactory = "kafkaUserEventContainer")
    public void listenUser(@Payload UserEvent message,
                       @Header(value = KafkaHeaders.RECEIVED_KEY,required = false) String key,
                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                       @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
                       @Header(KafkaHeaders.RECEIVED_TIMESTAMP) Long timestamp){
        log.info("Новый пользователь зарегистрировался: {}",message);
        createMessageParameterEvent(key,topic,partition,timestamp);
        registrationUserStatsService.save(RegistrationUserStats.builder()
                .id(message.userId())
                .registeredTime(LocalDateTime.now())
                .build());
    }

    @KafkaListener(topics = "hotel_booking",
            groupId = "${app.kafka.hotelBookingMessageGroupId}",
            containerFactory = "kafkaBookingEventContainer")
    public void listenBooking(@Payload BookingEvent message,
                       @Header(value = KafkaHeaders.RECEIVED_KEY,required = false) String key,
                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                       @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
                       @Header(KafkaHeaders.RECEIVED_TIMESTAMP) Long timestamp){
        log.info("Сообщение о новом бронировании: {}",message);
        createMessageParameterEvent(key,topic,partition,timestamp);
        bookingRegistrationStatService.save(BookingRegistrationStats.builder()
                        .arrivalDate(message.arrivalDate())
                        .departureDate(message.departureDate())
                        .userId(message.userId())
                        .registeredTime(LocalDateTime.now())
                .build());
    }

    private void createMessageParameterEvent(String key,String topic,Integer partition,Long timestamp){
        log.info("Key:{} - Topic:{} - Partition: {} - Timestamp: {}",key,topic,partition,timestamp);
    }
}
