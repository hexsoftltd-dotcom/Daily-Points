# Daily Points

Daily Points is an offline-first Android productivity app built with **Kotlin**, **Jetpack Compose**, **Clean Architecture + MVVM**, **Room**, and **Hilt**.

**Publisher:** Hexsoft Limited  
**Contact:** hexsoftltd@gmail.com

## Features

- Task management (add, edit, delete, enable/disable)
- Daily completion tracking (one completion per task per day)
- Automatic daily/weekly/monthly/yearly points aggregation
- Dashboard with goals, streaks, and key performance indicators
- Calendar view with completion status and future planning
- Goals management (daily and monthly)
- Reports and analytics
- CSV report sharing with date range selection
- Settings via DataStore (theme, notifications, reminder time, export preference)
- Daily app reminders and planned-task reminders

## Tech Stack

- **UI:** Jetpack Compose, Navigation Compose
- **Architecture:** Clean Architecture + MVVM
- **DI:** Hilt
- **Persistence:** Room, DataStore
- **Background jobs:** WorkManager
- **Charts:** Vico
- **Export:** CSV
- **Async:** Kotlin Coroutines + Flow
- **Testing:** JUnit, Room instrumentation tests, Compose UI tests

## Project Structure

```text
com.hexsoft.dailypoints
├── data
│   ├── analytics
│   ├── export
│   ├── local
│   │   ├── dao
│   │   └── entities
│   └── repository
├── di
├── domain
│   ├── model
│   ├── repository
│   └── usecase
├── navigation
├── notifications
├── presentation
│   ├── analytics
│   ├── calendar
│   ├── daily
│   ├── dashboard
│   ├── export
│   ├── goals
│   ├── reports
│   ├── settings
│   ├── splash
│   ├── tasks
│   └── theme
└── utils
```

## Build

1. Open the project in the latest stable Android Studio.
2. Let Gradle sync dependencies.
3. Run the `app` configuration on an emulator or device.

## Tests

- Unit tests: `app/src/test`
- Instrumentation and Compose UI tests: `app/src/androidTest`

## Notes

- The app seeds initial tasks on first launch:
  - Exercise (20)
  - Reading (10)
  - Meditation (15)
  - Study (25)
  - Walking (10)
- Database operations run in Room/Coroutine background execution paths.
