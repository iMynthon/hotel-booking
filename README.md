# Система бронирования отелей

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.11-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Testcontainers](https://img.shields.io/badge/Testcontainers-1.19.3-blueviolet.svg)](https://testcontainers.com/)

Система бронирования отелей с REST API, Spring Security, миграциями баз данных и расширенной функциональностью для управления отелями, номерами и бронированиями.

## Особенности

- **Полноценное REST API** для управления отелями, номерами и бронированиями
- **Spring Security** с аутентификацией и авторизацией
- **Интеграционные тесты** с использованием Testcontainers
- **AOP (Aspect-Oriented Programming)** для централизованной проверки прав пользователей
- **Миграции БД** с помощью Liquibase
- **Административная панель** для управления системой
- **Статистика** с возможностью выгрузки данных
- **Асинхронная обработка** через Kafka
- **Поддержка нескольких БД**: PostgreSQL (транзакционные данные) и MongoDB (документ-ориентированные данные)

## Технологический стек

- **Язык**: Java 21
- **Фреймворк**: Spring Boot 3.3.11
- **Базы данных**: 
  - PostgreSQL (основные данные)
  - MongoDB (документы, логи)
- **Безопасность**: Spring Security
- **Тестирование**: 
  - Testcontainers
  - Spring Security Test
  - JUnit 5
- **Инструменты**: 
  - Liquibase (миграции)
  - MapStruct (маппинг DTO)
  - Lombok
  - Spring Kafka
- **Дополнительно**: AOP, Commons CSV

## Требования

- Java 21
- Docker (для тестов и запуска инфраструктуры)
- Gradle 7+

## Запуск приложения

1. Клонировать репозиторий:
```bash
git clone https://github.com/your-username/hotel-booking-system.git
cd hotel-booking-system
```

2. Запустить зависимости (PostgreSQL, MongoDB, Kafka) через Docker:
```bash
docker-compose up -d
```

3. Собрать и запустить приложение:
```bash
./gradlew bootRun
```

Приложение будет доступно по адресу: `http://localhost:8080`

## Запуск тестов

Для запуска интеграционных тестов с использованием Testcontainers:
```bash
./gradlew test
```

Тесты автоматически запустят все необходимые контейнеры (PostgreSQL, MongoDB, Kafka) через Testcontainers.

Вот красиво оформленная структура проекта для README.md:

```markdown
# Структура проекта системы бронирования отелей

```
├── docker/  
├── gradle/  
└── src/  
    ├── main/  
    │   ├── java/com/example/hotelbookingapplication/  
    │   │   ├── aop/  
    │   │   │   ├── CheckRightService.java         # Сервис проверки прав  
    │   │   │   └── CheckUserRights.java          # Аспект проверки прав пользователя  
    │   │   │  
    │   │   ├── configuration/  
    │   │   │   ├── KafkaConfig.java              # Конфигурация Kafka  
    │   │   │   └── SecurityConfig.java           # Конфигурация безопасности  
    │   │   │  
    │   │   ├── controller/  
    │   │   │   ├── BookingController.java        # API бронирований  
    │   │   │   ├── HotelController.java          # API отелей  
    │   │   │   ├── RoomController.java           # API номеров  
    │   │   │   ├── StatisticsDataController.java # API статистики  
    │   │   │   └── UserController.java           # API пользователей  
    │   │   │  
    │   │   ├── dto/  
    │   │   │   ├── request/                      # DTO запросов  
    │   │   │   └── response/                     # DTO ответов  
    │   │   │  
    │   │   ├── exception/  
    │   │   │   ├── AuthorityUserException.java   # Ошибка прав доступа  
    │   │   │   ├── DuplicateDataException.java    # Ошибка дублирования  
    │   │   │   ├── EntityNotFoundException.java  # Ошибка "Не найдено"  
    │   │   │   └── GlobalExceptionHandler.java   # Глобальный обработчик исключений  
    │   │   │  
    │   │   ├── listener/  
    │   │   │   └── KafkaEventListener.java       # Обработчик событий Kafka  
    │   │   │  
    │   │   ├── logging/request/  
    │   │   │   └── RequestLogging.java           # Логирование запросов  
    │   │   │  
    │   │   ├── mapper/  
    │   │   │   ├── BookingMapper.java            # Маппер бронирований  
    │   │   │   ├── HotelMapper.java              # Маппер отелей  
    │   │   │   ├── RoomMapper.java               # Маппер номеров  
    │   │   │   └── UserMapper.java               # Маппер пользователей  
    │   │   │  
    │   │   ├── model/  
    │   │   │   ├── jpa/                          # JPA-сущности  
    │   │   │   │   ├── Authority.java            # Права доступа  
    │   │   │   │   ├── Booking.java              # Бронирование  
    │   │   │   │   ├── Hotel.java               # Отель  
    │   │   │   │   ├── RoleType.java            # Типы ролей  
    │   │   │   │   ├── Room.java                # Номер  
    │   │   │   │   └── User.java                # Пользователь  
    │   │   │   │  
    │   │   │   ├── kafka/                        # Модели Kafka  
    │   │   │   │   ├── BookingEvent.java         # Событие бронирования  
    │   │   │   │   └── UserEvent.java            # Событие пользователя  
    │   │   │   │  
    │   │   │   └── mongodb/                      # MongoDB-документы  
    │   │   │       ├── BookingRegistrationStats.java  # Статистика бронирований  
    │   │   │       └── RegistrationUserStats.java     # Статистика регистраций  
    │   │   │  
    │   │   ├── repository/  
    │   │   │   ├── jpa/                          # JPA-репозитории  
    │   │   │   └── mongodb/                      # MongoDB-репозитории  
    │   │   │  
    │   │   ├── security/  
    │   │   │   ├── AuthenticatedUser.java        # Аутентифицированный пользователь  
    │   │   │   └── AuthenticatedUserDetails.java # Детали пользователя  
    │   │   │  
    │   │   ├── service/  
    │   │   │   ├── impl/                         # Реализации сервисов  
    │   │   │   └── mongodb/                      # MongoDB-сервисы  
    │   │   │       └── HotelBookingService.java  # Сервис бронирований  
    │   │   │  
    │   │   ├── util/  
    │   │   │   └── InitializeData.java           # Инициализация данных  
    │   │   │  
    │   │   ├── validation/                      # Валидация  
    │   │   └── HotelBookingApplication.java     # Главный класс приложения  
    │   │  
    │   └── resources/                           # Ресурсы приложения  
    │  
    └── test/  
        ├── java/com/example/hotelbookingapplication/  
        │   ├── AbstractTest.java                # Базовый тестовый класс  
        │   ├── BookingControllerTest.java       # Тесты контроллера бронирований  
        │   ├── HotelControllerTest.java         # Тесты контроллера отелей  
        │   ├── RoomControllerTest.java          # Тесты контроллера номеров  
        │   └── UserControllerTest.java          # Тесты контроллера пользователей  
        │  
        └── resources/                          # Тестовые ресурсы  
```

Ключевые особенности структуры:
- Четкое разделение по технологиям (JPA/MongoDB/Kafka)
- Логическая группировка компонентов
- Последовательная вложенность от общего к частному
- Выделение request/response DTO
- Разделение моделей по типам хранилищ

## Основные функциональные возможности

### 1. Управление пользователями
- Регистрация и аутентификация
- Ролевая модель (USER, ADMIN)

### 2. Управление отелями и номерами
- CRUD операции для отелей
- Управление комнатами (добавление, обновление, удаление)
- Поиск отелей по различным критериям

### 3. Бронирование
- Создание, просмотр и отмена бронирований
- Проверка доступности номеров
- История бронирований пользователя

### 4. Административная панель
- Просмотр статистики
- Управление пользователями
- Экспорт данных в CSV

### 5. AOP Проверки
Реализованные аспекты:
///
 @Before("@annotation(CheckUserRights)")
    public void isOwner(JoinPoint joinPoint) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = findByUsername(authentication.getName());
        Object requestParam = getRequestUser(joinPoint);
        if (requestParam instanceof Integer) {
            checkAccessRightById((Integer) requestParam, currentUser);
        } else if (requestParam instanceof String) {
            checkAccessRightsByUsername((String) requestParam, currentUser);
        }
    }
///
Пример использования:
```java
@UserPermissionCheck
@PostMapping("/bookings")
public BookingResponse createBooking(@RequestBody BookingRequest request) {
    // Логика создания бронирования
}
```

### 6. Статистика и выгрузка данных
- Сбор статистики по бронированиям
- Выгрузка данных в CSV формате через REST API
- Административные отчеты

### Миграции баз данных
Для управления миграциями используется Liquibase. Новые изменения добавляются в `src/main/resources/db/changelog`.

Создание новой миграции:
```bash
./gradlew liquibaseDiffChangelog
```

### Сборка
Сборка проекта с запуском тестов:
```bash
./gradlew build
```
