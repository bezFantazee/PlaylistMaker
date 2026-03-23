<h1>🎵 PlaylistMaker — Музыкальный плеер и поиск треков</h1>

<p>
Android-приложение для поиска и воспроизведения музыки через iTunes API.<br>
Проект демонстрирует работу с Clean Architecture, MVVM, MediaPlayer и REST API.
</p>

<h2>📸 Скриншоты</h2>

<p>
<img src="screenshots/search.png" width="250"/>
<img src="screenshots/player.png" width="250"/>
<img src="screenshots/settings.png" width="250"/>
</p>

<h2>🚀 Основной функционал</h2>
<ul>
  <li><b>Поиск треков</b> — поиск музыки через iTunes API с debounce-защитой</li>
  <li><b>Воспроизведение</b> — аудиоплеер с управлением (play/pause) и таймером</li>
  <li><b>История поиска</b> — сохранение треков через SharedPreferences</li>
  <li><b>Настройки</b> — поддержка и пользовательское соглашение, возможность поделиться приложением, переключение темы</li>
</ul>

<h2>🏗 Архитектура</h2>

<p>
Приложение построено с использованием <b>Clean Architecture</b> и <b>MVVM</b>.
</p>

<h3>Data Layer</h3>
<ul>
  <li>Работа с iTunes API (Retrofit)</li>
  <li>AudioPlayerClient — обёртка над MediaPlayer</li>
  <li>StorageClient — SharedPreferences</li>
  <li>DTO - Domain преобразование</li>
</ul>

<h3>Domain Layer</h3>
<ul>
  <li>UseCases / Interactors</li>
  <li>Бизнес-логика плеера и поиска</li>
  <li>Независимость от Android SDK</li>
</ul>

<h3>Presentation Layer</h3>
<ul>
  <li>ViewModel + LiveData</li>
  <li>Activity / Fragment</li>
  <li>State-модели</li>
</ul>

<h2>🛠 Технологии</h2>

<ul>
  <li>Kotlin</li>
  <li>Android SDK (minSdk 29)</li>
  <li>Clean Architecture + MVVM</li>
  <li>Retrofit 2</li>
  <li>Koin (DI)</li>
  <li>Glide</li>
  <li>MediaPlayer</li>
  <li>Firebase Crashlytics</li>
</ul>

<h2>🧠 Ключевые паттерны</h2>
<ul>
  <li>Debounce (поиск — 2000 мс, клики — 1000 мс)</li>
  <li>Observer Pattern (LiveData)</li>
  <li>Repository Pattern</li>
  <li>Dependency Injection (Koin)</li>
  <li>Strategy Pattern (переключение темы)</li>
</ul>

<h2>🎓 Полученные навыки</h2>
<ul>
  <li>Проектирование приложения с Clean Architecture</li>
  <li>Работа с REST API (Retrofit, iTunes API)</li>
  <li>Реализация аудиоплеер (MediaPlayer)</li>
  <li>Управление состоянием через MVVM</li>
  <li>Использование Dependency Injection</li>
  <li>Работа с локальным хранилищем (SharedPreferences)</li>
  <li>Оптимизация UX (debounce, состояния)</li>
  <li>Реализыция тёмную тему</li>
</ul>

<h2>🚧 В планах</h2>
<ul>
  <li><b>BottomNavigationView</b> — улучшение навигации между основными разделами приложения</li>
  <li><b>Локальная база данных (Room)</b> — добавление избранных треков и создание пользовательских плейлистов</li>
</ul>

<h2>▶️ Сборка и запуск</h2>

<pre>
<code>
# Клонирование
git clone &lt;repository-url&gt;
cd PlaylistMaker

# Сборка
./gradlew assembleDebug

# Установка
./gradlew installDebug
</code>
</pre>

<h2>📌 Назначение проекта</h2>

<p>
Учебный проект для демонстрации навыков Android-разработки,<br>
архитектуры, работы с API и мультимедиа.
</p>
