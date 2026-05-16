# Rapport technique - MindMate AI

## 1. Introduction

MindMate AI est une application Android de bien-etre mental developpee en Java avec des interfaces XML modernes. Elle aide l'utilisateur a suivre son humeur, ecrire un journal personnel, consulter des statistiques, recevoir des rappels, pratiquer des exercices de respiration et utiliser des fonctionnalites d'intelligence artificielle pour obtenir des analyses informatives.

L'application est pensee comme un assistant de bien-etre general. Elle ne remplace pas un professionnel de sante, ne fournit pas de diagnostic medical et ne doit pas etre utilisee comme outil medical.

## 2. Objectif du projet

L'objectif principal de MindMate AI est d'offrir une plateforme mobile simple et ergonomique permettant a l'utilisateur de mieux comprendre son etat emotionnel au quotidien.

Les objectifs techniques sont :

- proposer une application Android native en Java et XML ;
- utiliser une architecture MVVM claire ;
- integrer Firebase pour l'authentification, Firestore et les notifications ;
- assurer un mode hors ligne avec Room ;
- consommer des API REST avec Retrofit ;
- afficher une interface moderne avec Material Components ;
- integrer des modules IA pour l'analyse et le chat bien-etre.

## 3. Problematique

Beaucoup d'utilisateurs souhaitent suivre leur humeur et leurs habitudes de bien-etre, mais les outils existants sont parfois trop complexes, trop medicalises ou peu personnalises. MindMate AI cherche a repondre a cette problematique en centralisant plusieurs outils utiles dans une seule application :

- suivi d'humeur ;
- journal personnel ;
- conseils simples ;
- statistiques visuelles ;
- rappels ;
- exercices de respiration ;
- assistance IA informative.

La difficulte technique principale est de combiner synchronisation cloud, cache local, navigation multi-ecrans, appels API et gestion d'etats sans rendre l'application difficile a maintenir.

## 4. Fonctionnalites principales

Les principales fonctionnalites implementees sont :

- authentification email/password avec Firebase Auth ;
- inscription et sauvegarde du profil utilisateur dans Firestore ;
- reinitialisation du mot de passe ;
- navigation principale avec Bottom Navigation et Drawer Navigation ;
- suivi d'humeur avec score de stress et note courte ;
- sauvegarde des humeurs dans Firestore et Room ;
- journal personnel avec ajout, modification, suppression et affichage des entrees ;
- cache local Room pour le mode hors ligne ;
- affichage de la meteo actuelle dans HomeFragment ;
- statistiques sur l'humeur, le stress et les entrees journal ;
- chat IA avec historique sauvegarde dans Firestore ;
- analyse IA d'une entree de journal ;
- module meditation et respiration avec timer ;
- notifications push FCM et notification locale de rappel journal ;
- profil utilisateur avec modification du nom et deconnexion ;
- parametres pour notifications, rappel journal, dark mode et suppression des donnees locales.

## 5. Fonctionnalites bonus

Plusieurs fonctionnalites enrichissent l'application au-dela du strict minimum :

- association de la meteo au mood tracking ;
- animation Lottie pendant les exercices de respiration ;
- graphiques statistiques avec MPAndroidChart ;
- notifications motivationnelles ;
- rappels journaliers planifies ;
- support du dark mode ;
- ecrans d'etat vide et d'erreur ;
- interface Material Design coherente ;
- synchronisation partielle des donnees locales non synchronisees ;
- replanification du rappel journal apres redemarrage du telephone.

## 6. Technologies utilisees

Le projet utilise les technologies suivantes :

- Java pour la logique Android ;
- XML pour les layouts ;
- Android Jetpack ;
- ViewBinding ;
- LiveData ;
- ViewModel ;
- Navigation Component ;
- Firebase Auth ;
- Firebase Firestore ;
- Firebase Cloud Messaging ;
- Room Database ;
- Retrofit ;
- Gson Converter ;
- OkHttp Logging Interceptor ;
- Material Components ;
- Lottie ;
- MPAndroidChart ;
- Gradle Kotlin DSL.

## 7. Architecture MVVM

MindMate AI suit une architecture MVVM :

- les Fragments representent la couche UI ;
- les ViewModels exposent les donnees a l'interface via LiveData ;
- les Repositories centralisent l'acces aux donnees locales, distantes et Firebase ;
- les Models representent les donnees metier ;
- Room gere la persistance locale ;
- Retrofit gere les appels API REST ;
- Firebase gere l'authentification, Firestore et FCM.

Cette separation permet de reduire le couplage entre l'interface et les sources de donnees. Les ecrans observent les etats fournis par les ViewModels, tandis que les repositories encapsulent la logique de sauvegarde, lecture, synchronisation et appels reseau.

## 8. Structure du projet

La structure principale est organisee comme suit :

```text
app/src/main/java/com/omartitouhi/mindmate/
|-- data/
|   |-- local/          # Room database, DAO, entities
|   |-- model/          # Models metier
|   |-- remote/         # Retrofit services et DTO
|   |-- repository/     # Repositories MVVM
|-- ui/
|   |-- auth/           # Login, Register, ForgotPassword
|   |-- home/           # Accueil et meteo
|   |-- mood/           # Mood tracking
|   |-- journal/        # Journal personnel
|   |-- ai/             # AI Chat et AI Analysis
|   |-- meditation/     # Meditation et respiration
|   |-- statistics/     # Statistiques
|   |-- profile/        # Profil utilisateur
|   |-- settings/       # Parametres
|   |-- about/          # A propos
|-- utils/              # Resource, notifications, constantes
|-- MainActivity.java
|-- AuthActivity.java
|-- SplashActivity.java
```

Les ressources XML sont separees dans :

```text
app/src/main/res/
|-- layout/             # Interfaces XML
|-- navigation/         # Graphes de navigation
|-- menu/               # Bottom navigation et drawer
|-- drawable/           # Icônes, backgrounds, etats
|-- anim/               # Animations XML
|-- raw/                # Animations Lottie
|-- values/             # Strings, colors, themes, dimens
```

## 9. Firebase Auth et Firestore

Firebase Auth est utilise pour :

- connecter un utilisateur avec email/password ;
- inscrire un nouvel utilisateur ;
- reinitialiser le mot de passe ;
- verifier l'utilisateur connecte au demarrage via SplashActivity ;
- deconnecter l'utilisateur depuis le profil.

Firestore est utilise pour sauvegarder :

- les profils utilisateurs ;
- les humeurs ;
- les entrees journal ;
- l'historique du chat IA ;
- les tokens FCM.

Les donnees sont organisees sous le document utilisateur :

```text
users/{userId}
|-- moods/{moodId}
|-- journal_entries/{entryId}
|-- chat_messages/{messageId}
|-- fcm_tokens/{tokenId}
```

Des regles Firestore basiques sont fournies afin de limiter l'acces aux donnees d'un utilisateur authentifie uniquement a son propre `userId`.

## 10. Room offline cache

Room est utilise pour sauvegarder localement les donnees importantes :

- humeurs ;
- entrees journal.

Le cache local permet a l'application d'afficher les donnees meme lorsque le reseau est indisponible. Les entites locales contiennent un champ `synced` pour indiquer si la donnee a ete synchronisee avec Firestore.

Les DAO permettent :

- d'inserer ou mettre a jour des donnees ;
- de lire les donnees d'un utilisateur ;
- de retrouver les elements non synchronises ;
- de marquer les elements comme synchronises.

Cette approche assure une experience plus robuste en cas de connexion instable.

## 11. Retrofit API REST

Retrofit est utilise pour consommer les API REST :

- API meteo ;
- API IA pour l'analyse ;
- API IA pour le chat.

Les services Retrofit sont declares dans le package `data.remote`. Le client API centralise la configuration Retrofit et OkHttp. Les URLs sont exposees via `BuildConfig`, ce qui facilite la configuration par environnement.

Le logging HTTP est limite au mode debug afin de reduire les risques d'exposition d'informations sensibles en production.

## 12. Navigation Component

L'application utilise Navigation Component avec :

- un graphe principal `nav_graph.xml` ;
- un graphe d'authentification `auth_nav_graph.xml` ;
- une Bottom Navigation pour les sections principales ;
- un Drawer Navigation pour les sections secondaires ;
- une Toolbar Material connectee au NavController.

Les destinations principales sont :

- Home ;
- Journal ;
- AI Chat ;
- Statistics ;
- Profile ;
- Meditation ;
- Settings ;
- About ;
- Mood Check-In ;
- AI Analysis.

Les ecrans journal utilisent des actions explicites et un argument `entry_id` pour ouvrir le detail d'une entree.

## 13. Gestion des etats Loading/Error/Success

Le projet utilise une classe generique `Resource<T>` pour representer les etats d'une operation :

- `Loading` : operation en cours ;
- `Success` : operation reussie ;
- `Error` : operation echouee.

Cette classe est utilisee dans les ViewModels et repositories pour uniformiser l'affichage des ProgressBar, messages d'erreur et messages de succes.

Ce mecanisme est applique notamment dans :

- l'authentification ;
- le mood tracking ;
- le journal ;
- le chat IA ;
- l'analyse IA ;
- les parametres ;
- le profil.

## 14. Animations Lottie/XML

L'application utilise des animations pour ameliorer l'experience utilisateur :

- animation Lottie pour la respiration guidee ;
- animations XML simples pour les transitions ;
- etats visuels modernes avec backgrounds XML ;
- ProgressBar sur les operations longues.

Le module Meditation/Breathing combine un timer, des boutons Start/Pause/Reset et une animation Lottie afin de guider l'utilisateur pendant l'exercice.

## 15. Notifications FCM

Firebase Cloud Messaging est integre pour recevoir des notifications push.

Le module notifications comprend :

- `NotificationService` pour recevoir les messages FCM ;
- `NotificationHelper` pour creer les channels et afficher les notifications locales ;
- sauvegarde du token FCM dans Firestore ;
- notifications de rappel journal ;
- notifications de meditation ;
- messages motivationnels ;
- rappel local journalier planifie ;
- replanification du rappel apres redemarrage du telephone.

Android 13+ est pris en compte avec la permission `POST_NOTIFICATIONS`.

## 16. Partie IA

La partie IA contient deux modules principaux :

### AI Analysis

L'utilisateur selectionne une entree de journal. Le texte est envoye a une API IA qui retourne :

- emotion principale ;
- niveau de stress estime ;
- resume court ;
- conseil personnalise ;
- suggestion d'exercice de respiration ou meditation.

Un message de prudence est affiche dans l'interface :

> Cette analyse est fournie a titre informatif et ne remplace pas un professionnel de sante.

### AI Chat

Le module AI Chat permet :

- d'envoyer un message utilisateur ;
- d'afficher les messages utilisateur et assistant ;
- d'appeler une API IA avec Retrofit ;
- de sauvegarder l'historique dans Firestore ;
- de rester dans un role de bien-etre general.

Le chatbot ne doit pas donner de diagnostic medical et doit orienter l'utilisateur vers un professionnel de sante en cas de situation serieuse.

## 17. Captures d'ecran a inserer

Les captures suivantes peuvent etre inserees dans le rapport final :

### Capture 1 - Splash / Auth

Inserer une capture de l'ecran de connexion ou d'inscription.

```text
[Capture d'ecran : LoginFragment ou RegisterFragment]
```

### Capture 2 - Home

Inserer une capture de l'accueil avec la meteo actuelle et le conseil.

```text
[Capture d'ecran : HomeFragment]
```

### Capture 3 - Mood Tracking

Inserer une capture du choix d'humeur, score de stress et note courte.

```text
[Capture d'ecran : MoodCheckInFragment]
```

### Capture 4 - Journal

Inserer une capture de la liste des entrees journal avec titre, extrait, date et mood.

```text
[Capture d'ecran : JournalListFragment]
```

### Capture 5 - AI Chat

Inserer une capture du chat avec messages utilisateur et assistant.

```text
[Capture d'ecran : AIChatFragment]
```

### Capture 6 - AI Analysis

Inserer une capture du resultat d'analyse IA et du message de prudence.

```text
[Capture d'ecran : AIAnalysisFragment]
```

### Capture 7 - Statistics

Inserer une capture du graphe d'humeur, stress moyen et progression hebdomadaire.

```text
[Capture d'ecran : StatisticsFragment]
```

### Capture 8 - Meditation

Inserer une capture de l'exercice de respiration avec animation Lottie et timer.

```text
[Capture d'ecran : MeditationFragment]
```

### Capture 9 - Profile / Settings

Inserer une capture du profil utilisateur et des parametres.

```text
[Capture d'ecran : ProfileFragment / SettingsFragment]
```

## 18. Conclusion

MindMate AI est une application Android complete orientee bien-etre mental. Elle combine plusieurs modules importants : authentification, mood tracking, journal personnel, statistiques, meteo, notifications, meditation et intelligence artificielle.

Le projet respecte une architecture MVVM avec LiveData, ViewModel, repositories, Room, Firebase et Retrofit. L'application offre une experience moderne grace a Material Components, ViewBinding, Navigation Component, Lottie et MPAndroidChart.

Le cache local Room et la synchronisation Firestore permettent une meilleure resilience en cas de connexion instable. Les notifications FCM et les rappels locaux encouragent une utilisation reguliere de l'application.

Enfin, les fonctionnalites IA apportent une aide personnalisee et informative, tout en rappelant clairement que MindMate AI ne remplace pas un professionnel de sante et ne fournit aucun diagnostic medical.
