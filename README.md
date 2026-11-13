# 🎵 PlaylistMaker

Android-приложение для поиска и прослушивания треков через **iTunes Search API**, возможностью сохранять треки в Избранные и пользовательские плейлисты. Проект построен на Clean Architecture и MVVM.

## Скриншоты

<p align="center">
  <img src="screenshots/search_screen.png" width="150" alt="Экран поиска" />
  <img src="screenshots/track_screen.png" width="150" alt="Экран воспроизведения трека" />
  <img src="screenshots/media_library_screen.png" width="150" alt="Медиатека" />
  <img src="screenshots/settings_screen.png" width="150" alt="Настройки" />
</p>

## Основные возможности

### Поиск и воспроизведение
- Поиск треков через iTunes Search API (Retrofit) с debounce для ограничения частоты запросов
- Предпрослушивание аудиофрагментов через MediaPlayer, отображение информации о треке
- История поиска в SharedPreferences

### Медиатека
- Избранное: добавление и удаление треков (Room)
- Пользовательские плейлисты: создание, редактирование названия, описания и обложки, удаление
- Добавление и удаление треков в плейлистах, подсчёт общей продолжительности
- Шаринг плейлиста в виде текстового списка треков

### Настройки и навигация
- Светлая и тёмная тема
- Навигация между разделами: Navigation Component, BottomNavigationView, ViewPager2, TabLayout
- Пользовательское соглашение, обращение в поддержку, шаринг приложения

## Архитектура

В проекте используется **Clean Architecture** в сочетании с **MVVM**.

Приложение разделено на три основных слоя:

- **Data Layer** — сетевые запросы через Retrofit, локальная база данных Room, SharedPreferences, файловое хранилище обложек и взаимодействие с MediaPlayer.
- **Domain Layer** — модели предметной области, интерфейсы репозиториев и интеракторы, содержащие бизнес-логику.
- **Presentation Layer** — Activity, Fragment, ViewModel, LiveData и модели состояний интерфейса.

Зависимости внедряются через Koin, за изменениями данных в Room следят Flow.

Подробнее: [Архитектура приложения](docs/architecture.md)

## Технологии

| Категория | Инструменты |
|---|---|
| Язык и платформа | Kotlin, Android SDK |
| Минимальная версия Android | minSdk 29 |
| UI | XML, ViewBinding, Activity, Fragment |
| Навигация | Navigation Component, BottomNavigationView, ViewPager2, TabLayout |
| Архитектура | Clean Architecture, MVVM |
| Управление состоянием | ViewModel, LiveData |
| Асинхронность | Kotlin Coroutines, Flow |
| Сеть | Retrofit 2, Gson, REST API, iTunes Search API |
| Локальное хранение | Room, SQLite, SharedPreferences |
| Воспроизведение | MediaPlayer |
| Dependency Injection | Koin |
| Работа с изображениями | Glide |
| Мониторинг ошибок | Firebase Crashlytics |

## Сборка и запуск

1. Клонируйте репозиторий и перейдите на ветку `dev`:

   ```bash
   git clone -b dev https://github.com/bezFantazee/PlaylistMaker.git
   cd PlaylistMaker
   ```

2. Откройте проект в Android Studio и дождитесь завершения Gradle Sync.

3. Соберите Debug-версию:

   **Linux / macOS**
   ```bash
   ./gradlew assembleDebug
   ```

   **Windows**
   ```bat
   gradlew.bat assembleDebug
   ```

4. Для установки на подключённое устройство или запущенный эмулятор:

   **Linux / macOS**
   ```bash
   ./gradlew installDebug
   ```

   **Windows**
   ```bat
   gradlew.bat installDebug
   ```

## Планы развития

Текущие идеи по развитию приложения собраны в [Roadmap](docs/roadmap.md). В частности, рассматриваются улучшение навигации и расширение возможностей медиатеки.
