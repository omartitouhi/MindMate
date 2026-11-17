# MindMate

Native **Android** app for mental well-being. You log your mood, keep a journal, follow your statistics and talk to an AI assistant.

## Features

- **Mood tracking:** daily mood entries, stored offline with Room.
- **Journal:** write, read and browse journal entries.
- **Statistics:** mood trends and summaries drawn with MPAndroidChart.
- **AI assistant:** a chat and an AI analysis of your entries, called over REST with Retrofit.
- **Meditation**, **weather** on the home screen, **profile** and **settings**.
- **Accounts:** Firebase Authentication (login, register, forgot password), profile data in Firestore, push notifications with Firebase Cloud Messaging.

## Architecture

MVVM with a repository layer:

```text
ui/ (Fragments + ViewModels, LiveData)
  └── data/repository/  (Mood, Journal, Statistics, Ai, Chat, Auth, Profile, Weather, Settings)
        ├── data/local/   Room database, DAOs, entities  (offline storage)
        └── data/remote/  Retrofit + OkHttp services     (AI, chat, weather)
```

## Stack

Java · Android SDK (min 24, target 36) · Jetpack Navigation · ViewModel / LiveData · Room · Retrofit + Gson · OkHttp · Firebase Auth, Firestore, Cloud Messaging · Lottie · MPAndroidChart

## Run it

1. Open the project in Android Studio.
2. Use your own Firebase project: replace `app/google-services.json` with yours.
3. Run on an emulator or a device (Android 7.0+).

## Author

**Omar Titouhi**: [LinkedIn](https://linkedin.com/in/omartitouhi) · [GitHub](https://github.com/omartitouhi)
