# Events – Android Application

A native Android application that helps users discover nearby events, explore event details, bookmark their favourite events, and navigate to event locations.

Built using **Kotlin, Jetpack Compose, MVVM, Clean Architecture, Room, Hilt, Coroutines, Flow, Coil, and WorkManager**.

The project demonstrates modern Android development practices, modular architecture, local persistence, caching strategies, native location integration, background processing, and testable application design.

Users can:

- Browse available events.
- View event information and images.
- Explore individual event details.
- Bookmark and unbookmark events.
- Access bookmarked events independently of the event listing.
- Request location permission.
- View the distance between their current location and an event.
- Open an event location in an external maps application.

The application also incorporates local data persistence, image caching, TTL-based caching, background refresh scheduling, and graceful handling of data refresh failures.

The application uses a bundled JSON file as its event data source and Room as its persistent local data store.

## Features

### 1. Event Discovery

- Display a list of available events.
- Load event information from a local JSON data source.
- Display event images using Coil.
- Navigate from the event list to individual event details.
- Observe event data reactively using Kotlin Flow.

### 2. Event Details

- Display detailed event information.
- Show event images and relevant information.
- Request runtime location permission.
- Calculate the distance between the user's current location and the event.
- Launch an external maps application for navigation.

### 3. Bookmark Management

- Bookmark and unbookmark events.
- Persist bookmark information using Room.
- Maintain bookmark state when refreshing event data.
- Display bookmarked events separately.
- Preserve bookmarked events across application restarts.

### 4. Offline Data Persistence

- Persist downloaded or loaded events in Room.
- Use Room as the primary source of truth for UI data.
- Observe database changes through Kotlin Flow.
- Retain previously stored events when a refresh fails.
- Support displaying previously cached event information.

### 5. Image Caching

Implemented image caching using Coil.

Caching capabilities include:

- In-memory image caching.
- Disk-based image caching.
- Reuse of previously loaded images.
- Reduced unnecessary image downloads.

### 6. TTL-Based Event Caching

Implemented a persistent cache expiration mechanism using Room.

- Cache metadata is stored separately from event records.
- The last successful fetch timestamp is persisted.
- Cache expiration is checked before refreshing events.
- Fresh cached data is reused without unnecessary refreshes.
- Expired data triggers a refresh.
- Cache metadata is updated only after a successful database update.

**Configured TTL: 15 minutes.**

### 7. Background Refresh

Implemented periodic background refresh scheduling using Android WorkManager.

- Periodic refresh scheduling.
- Low-frequency refresh interval of 6 hours.
- Unique periodic work to prevent duplicate scheduling.
- Network connectivity constraints.
- Background execution independent of the currently visible screen.

### 8. Loading and Error States

The application handles different UI states, including:

- Loading.
- Success.
- Empty results.
- Error conditions.

Previously cached events remain available when a refresh fails instead of unnecessarily replacing existing content with an empty list.

## Technology Stack

| Category | Technology |
|---|---|
| Language | Kotlin |
| Platform | Native Android |
| UI | Jetpack Compose |
| Architecture | Clean Architecture + MVVM |
| Dependency Injection | Hilt |
| Local Database | Room |
| Asynchronous Programming | Kotlin Coroutines |
| Reactive Data | Kotlin Flow |
| Image Loading and Caching | Coil |
| Background Processing | WorkManager |
| Navigation | Navigation Compose |
| Location | Android Location APIs |
| Maps Integration | Android Maps Intent / Deep Linking |
| Data Source | Local JSON |
| Unit Testing | JUnit, Fake Data Sources, In-Memory Room |


## Application Architecture

The application follows **Clean Architecture with MVVM**, organized into three Gradle modules.

### 1. App Module

Responsible for application presentation and Android-specific functionality.

Responsibilities:

- Jetpack Compose screens.
- ViewModels.
- Navigation.
- UI state management.
- Location permission handling.
- Maps integration.
- WorkManager configuration and background scheduling.
- Application initialization.

### 2. Domain Module

Contains the application's business logic and core contracts.

Responsibilities:

- Domain models.
- Repository interfaces.
- Use cases.
- Business rules.
- Abstraction of data operations.

The domain layer does not depend on Android framework or data-layer implementation details.

### 3. Data Module

Responsible for retrieving, storing, and transforming application data.

Responsibilities:

- Local JSON data source.
- Room database.
- DAOs.
- Database entities.
- Data-to-domain mapping.
- Repository implementations.
- Cache metadata management.
- Transactional database operations.

The repository implementation coordinates data operations between the data sources and the domain layer.

## Architectural Principles

- Separation of concerns.
- Dependency inversion.
- Repository pattern.
- Single source of truth using Room.
- Unidirectional data flow.
- Reactive UI updates using Flow.
- Business logic isolated in the domain layer.
- Testable components through dependency abstraction.

## Project Structure

The project is organized into three main Gradle modules:

```text
Events/
│
├── app/
│   └── src/main/
│       ├── java/com/android/events/
│       │   ├── navigation/
│       │   ├── events/
│       │   ├── details/
│       │   ├── bookmarks/
│       │   ├── di/
│       │   └── EventsApplication.kt
│       │
│       ├── assets/
│       │   └── events.json
│       │
│       └── AndroidManifest.xml
│
├── domain/
│   └── src/main/java/
│       └── com/android/events/domain/
│           ├── model/
│           ├── repository/
│           └── usecase/
│
├── data/
│   └── src/main/java/
│       └── com/android/events/data/
│           ├── local/
│           │   ├── dao/
│           │   ├── database/
│           │   └── entity/
│           │
│           ├── remote/
│           │   └── datasource/
│           │
│           ├── mapper/
│           └── repository/
│
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```


## Application Flow

### Event Listing Flow

1. The UI requests events through its ViewModel.
2. The ViewModel invokes the appropriate domain use case.
3. The use case communicates with the EventRepository interface.
4. The repository observes events from Room.
5. Room emits changes through Flow.
6. The ViewModel updates the UI state.
7. Jetpack Compose renders the latest events.

### Event Detail Flow

1. The user selects an event.
2. Navigation passes the event identifier.
3. The detail ViewModel requests the event through a use case.
4. The repository retrieves the event from Room.
5. The event is mapped into its domain model.
6. The UI displays the event details.

### Bookmark Flow

1. The user selects the bookmark action.
2. The ViewModel invokes the bookmark use case.
3. The repository updates the bookmark state through EventDao.
4. Room persists the change.
5. Observed Flow data updates the UI.

## Local Database and Persistence

Room is used for persistent storage.

### Event Storage

Event information is stored in the local database through:

- EventEntity.
- EventDao.
- EventsDatabase.

The DAO supports operations such as:

- Observing all events.
- Retrieving an event by ID.
- Retrieving bookmarked events.
- Inserting events.
- Deleting event records.
- Updating bookmark status.

### Cache Metadata Storage

A separate metadata table tracks cache freshness.

Cache metadata includes:

| Field | Description |
|---|---|
| cacheKey | Identifies the cached dataset |
| lastFetchedAt | Timestamp of the last successful refresh |

This separation allows cache expiration to be managed independently of individual event records.

### Transaction Management

Room transactions are used to ensure related database updates are performed atomically.

During an event refresh:

1. Existing bookmark states are retrieved.
2. New event entities are mapped.
3. Existing bookmark states are preserved.
4. Event records are updated.
5. Cache metadata is updated.

If the transaction fails, Room rolls back the changes.

## Caching Strategy

The application implements two caching mechanisms.

### 1. Image Caching – Coil

Coil provides:

- Memory cache.
- Disk cache.
- Image reuse.
- Efficient image loading in Compose.

### 2. Event Data Caching – Room + TTL

The application uses a 15-minute TTL.

```kotlin
private const val EVENTS_CACHE_KEY = "events"
private const val CACHE_TTL = 15 * 60 * 1000L
```

Cache expiration is determined using the last successful fetch timestamp.

```kotlin
private suspend fun isCacheExpired(): Boolean {
    val metadata = cacheMetadataDao.getMetadata(
        EVENTS_CACHE_KEY
    ) ?: return true

    val currentTime = System.currentTimeMillis()

    return currentTime - metadata.lastFetchedAt > CACHE_TTL
}
```

### Cache Behaviour

| Scenario | Expected Behaviour |
|---|---|
| First application launch | Load events and persist them |
| Cache younger than 15 minutes | Reuse cached events |
| Cache older than 15 minutes | Refresh events |
| Refresh successful | Update Room and cache timestamp |
| Refresh unsuccessful | Retain existing cached data |
| Application restarted | Read persisted events and metadata |

**Note:** The current implementation uses a bundled JSON data source. The TTL mechanism demonstrates persistent cache expiration and refresh behaviour; it is not a live REST response cache.

## Background Refresh

Android WorkManager is used to schedule periodic event refresh operations.

### Configuration

- Refresh interval: 6 hours.
- Network connectivity constraint.
- Unique periodic work.
- Background execution.
- Coroutine-based worker implementation.
- Error and retry handling according to the worker result.

WorkManager is suitable for deferrable background operations and can persist scheduled work across application restarts and device reboots.

The background refresh reuses the repository's cache validation logic rather than maintaining a separate TTL implementation.

## Location and Maps Integration

The application uses native Android location capabilities.

### Location Permission

- Requests runtime location permission.
- Handles permission approval and denial.
- Retrieves the user's current location when permission is available.

### Distance Calculation

The application calculates the distance between the user's current location and the selected event location.

This provides users with additional context when deciding which events to attend.

### Maps Navigation

The application supports launching an external maps application through an Android intent.

Users can open the selected event location for navigation without requiring an embedded maps interface.

## Error Handling

The application uses explicit UI states to represent different screen conditions.

Typical states include:

- Loading.
- Success.
- Empty.
- Error.

The application is designed to preserve existing Room data when a refresh operation fails.

Important behaviours:

- Avoid replacing cached events with empty results after a failed refresh.
- Preserve previously saved bookmarks.
- Update cache timestamps only after successful refresh operations.
- Keep UI rendering independent of data-source implementation details.
- Handle location permission denial gracefully.

## Dependency Injection

Hilt is used for dependency injection.

It provides dependencies for:

- Room database.
- Event DAO.
- Cache metadata DAO.
- Repository implementation.
- Use cases.
- ViewModels.
- Background worker dependencies.

Constructor injection is preferred wherever possible.

This reduces manual object creation and improves testability.

## Testing

The project uses JUnit and test doubles to validate core application logic.

The repository implementation can be tested using:

- Fake local event data source.
- In-memory Room database.
- Actual repository implementation.
- Controlled test event data.

Core test scenarios include:

1. **Event Refresh:** Verify that events are loaded and persisted in Room.
2. **Bookmark Preservation:** Verify that existing bookmark states are retained when events are refreshed.
3. **Bookmark Management:** Verify that toggling bookmark state updates the persisted event record.

The project also supports extending coverage to cache expiration, repository error handling, and ViewModel UI states.

Run the unit tests using:

```bash
./gradlew test
```

---

## Engineering Standards

The following engineering practices are followed throughout the application.

### Architecture and Code Organization

- Multi-module Gradle architecture.
- Clean Architecture.
- MVVM presentation pattern.
- Repository pattern.
- Separation of business logic and framework-specific code.
- Dependency inversion.

### Kotlin Best Practices

- Kotlin Coroutines for asynchronous operations.
- Flow for reactive data streams.
- Constructor-based dependency injection.
- Immutable data models where appropriate.
- Null-safe implementation.
- Meaningful naming conventions.

### Database Standards

- Room for structured local persistence.
- DAO abstraction.
- Transactional database updates.
- Explicit cache metadata.
- Preservation of bookmark state.

### UI Standards

- Jetpack Compose.
- ViewModel-driven UI state.
- Loading, success, empty, and error states.
- Reactive UI updates.
- Separation of UI and business logic.

### Maintainability and Testability

- Modular responsibilities.
- Reusable use cases.
- Testable repository abstractions.
- Fake data sources for unit testing.
- Centralized dependency management.

## Getting Started

### Prerequisites

- Android Studio.
- Android SDK.
- JDK compatible with the project's Gradle configuration.
- Android emulator or physical Android device.

### Clone the Repository

```bash
git clone https://github.com/jyomin26/Events.git
```

Navigate to the project:

```bash
cd Events
```

### Open in Android Studio

1. Open Android Studio.
2. Select Open Project.
3. Choose the cloned Events directory.
4. Allow Gradle synchronization to complete.
5. Run the application using an emulator or physical device.

### Build the Application

```bash
./gradlew build
```

### Run Unit Tests

```bash
./gradlew test
```

---

## Repository

**GitHub:** https://github.com/jyomin26/Events

---

## Summary

The Events application demonstrates practical implementation of modern native Android development concepts, including:

- Kotlin and Jetpack Compose.
- Multi-module Clean Architecture.
- MVVM and Repository patterns.
- Hilt dependency injection.
- Room database and persistent bookmarks.
- Reactive programming using Coroutines and Flow.
- Image caching using Coil.
- TTL-based local data caching.
- Periodic background processing using WorkManager.
- Native Android location permissions.
- Distance calculation and Maps navigation.
- Structured UI state and error handling.
- Unit testing and maintainable code organization.

The primary focus of this project is to demonstrate clean architecture, separation of concerns, reliable local persistence, efficient resource usage, and practical Android platform integration.
