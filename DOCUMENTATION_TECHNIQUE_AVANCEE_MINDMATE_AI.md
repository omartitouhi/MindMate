# Documentation technique avancee - MindMate AI

## 1. Vue d'ensemble

MindMate AI est une application Android native developpee en Java + XML. Elle utilise une architecture MVVM basee sur `Fragment`, `ViewModel`, `LiveData`, `Repository`, Firebase, Room et Retrofit.

L'application contient les modules suivants :

- authentification Firebase ;
- profil utilisateur ;
- mood tracking ;
- journal personnel ;
- cache offline Room ;
- meteo via API REST ;
- analyse IA d'une entree de journal ;
- chatbot IA ;
- statistiques ;
- meditation / respiration ;
- notifications push FCM et rappels locaux ;
- settings avec SharedPreferences ;
- navigation principale avec Bottom Navigation et Drawer Navigation.

Important : MindMate AI est une application de bien-etre informatif. Elle ne remplace pas un professionnel de sante, ne donne pas de diagnostic medical et ne doit pas etre utilisee comme outil medical.

## 2. Stack technique

### Langage et UI

- Java
- XML
- ViewBinding
- Material Components
- RecyclerView
- Navigation Component
- Lottie
- MPAndroidChart

### Donnees

- Firebase Authentication
- Firebase Firestore
- Firebase Cloud Messaging
- Room Database
- SharedPreferences

### Reseau

- Retrofit
- Gson Converter
- OkHttp
- OkHttp Logging Interceptor en debug uniquement

### Build

- Gradle Kotlin DSL
- AGP `9.1.0`
- `compileSdk 36`
- `minSdk 24`
- `targetSdk 36`

## 3. Architecture generale MVVM

Le projet suit ce flux :

```text
Fragment XML/UI
    -> ViewModel
        -> Repository
            -> Firebase Auth / Firestore
            -> Room DAO
            -> Retrofit API
            -> SharedPreferences
```

Les Fragments ne doivent pas appeler directement Firebase, Room ou Retrofit. Ils observent des `LiveData` exposes par les ViewModels.

Les ViewModels :

- valident les entrees utilisateur ;
- exposent les etats UI ;
- appellent les repositories ;
- publient les resultats avec `LiveData`.

Les repositories :

- executent les appels Firebase ;
- executent les appels Retrofit ;
- lisent/ecrivent Room ;
- gerent la synchronisation locale/cloud ;
- transforment les erreurs en messages lisibles.

## 4. Gestion globale des etats avec Resource

Fichier :

```text
app/src/main/java/com/omartitouhi/mindmate/utils/Resource.java
```

`Resource<T>` encapsule trois etats :

```text
LOADING
SUCCESS
ERROR
```

Utilisation typique :

```java
callback.onResult(Resource.loading());
callback.onResult(Resource.success(data));
callback.onResult(Resource.error("Message d'erreur"));
```

Dans les fragments, cet etat controle :

- ProgressBar ;
- activation/desactivation des boutons ;
- affichage des messages d'erreur ;
- affichage des messages de succes.

Modules utilisant `Resource<T>` :

- Auth ;
- Mood ;
- Journal ;
- Weather ;
- AI Analysis ;
- AI Chat ;
- Profile ;
- Settings.

## 5. Configuration des APIs Retrofit

Fichier principal :

```text
app/src/main/java/com/omartitouhi/mindmate/data/remote/ApiClient.java
```

`ApiClient` centralise la creation des clients Retrofit.

### URLs configurees

Dans :

```text
app/build.gradle.kts
```

```kotlin
MINDMATE_API_BASE_URL = providers.gradleProperty("MINDMATE_API_BASE_URL")
    .orElse("https://mindmate-api.local/api/")

WEATHER_API_BASE_URL = "https://api.open-meteo.com/v1/"
```

Donc :

```text
API IA / MindMate backend :
https://mindmate-api.local/api/

API meteo :
https://api.open-meteo.com/v1/
```

Pour utiliser une vraie API IA, il faut definir la propriete Gradle :

```properties
MINDMATE_API_BASE_URL=https://votre-backend.com/api/
```

### Clients exposes

`ApiClient` expose trois services :

```java
getApiService()
getWeatherApiService()
getAiApiService()
```

`getApiService()` utilise `MindMateApiService`.

`getWeatherApiService()` utilise `WeatherApiService`.

`getAiApiService()` utilise `AiApiService`.

### Logging reseau

Le logging OkHttp est active uniquement en debug :

```java
if (BuildConfig.DEBUG) {
    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
}
```

Cela evite d'exposer des informations reseau dans les builds release.

## 6. API meteo

### Service Retrofit

Fichier :

```text
app/src/main/java/com/omartitouhi/mindmate/data/remote/WeatherApiService.java
```

Endpoint :

```http
GET https://api.open-meteo.com/v1/forecast
```

Declaration Retrofit :

```java
@GET("forecast")
Call<WeatherResponse> getCurrentWeather(
    @Query("latitude") double latitude,
    @Query("longitude") double longitude,
    @Query("current_weather") boolean currentWeather,
    @Query("timezone") String timezone
);
```

### Parametres envoyes

Dans `WeatherRepository` :

```text
latitude = 36.8065
longitude = 10.1815
current_weather = true
timezone = auto
```

Ville par defaut :

```text
Tunis
```

URL finale equivalente :

```http
https://api.open-meteo.com/v1/forecast?latitude=36.8065&longitude=10.1815&current_weather=true&timezone=auto
```

### Repository

Fichier :

```text
app/src/main/java/com/omartitouhi/mindmate/data/repository/WeatherRepository.java
```

Methode principale :

```java
getCurrentWeather(WeatherCallback callback)
```

Flux :

```text
WeatherViewModel.loadCurrentWeather()
    -> WeatherRepository.getCurrentWeather()
        -> callback Resource.loading()
        -> Retrofit GET /forecast
        -> si success : conversion WeatherResponse -> WeatherInfo
        -> sauvegarde cache SharedPreferences
        -> callback Resource.success(weatherInfo)
        -> si erreur reseau : tentative retour cache
        -> sinon Resource.error(...)
```

### Mapping condition meteo

Les codes meteo sont convertis :

```text
0 -> Clear
1,2,3 -> Cloudy
45-48 -> Fog
51-67, 80-82 -> Rain
71-77 -> Snow
>=95 -> Storm
autre -> Mild
```

### Conseils generes

Exemples :

```text
Clear -> courte marche mindful
Cloudy/Fog -> rythme doux
Rain/Storm -> respiration calme ou journal
Snow -> pause reparatrice
Mild -> check-in simple
```

### Cache local meteo

SharedPreferences :

```text
PREFS_NAME = weather_cache
KEY_CITY = city
KEY_TEMPERATURE = temperature
KEY_CONDITION = condition
```

La derniere meteo est aussi utilisee lors de la sauvegarde d'une humeur.

## 7. API IA - Analyse de journal

### Service Retrofit

Fichier :

```text
app/src/main/java/com/omartitouhi/mindmate/data/remote/AiApiService.java
```

Endpoint :

```http
POST {MINDMATE_API_BASE_URL}/ai/analyze-journal
```

Avec l'URL par defaut actuelle :

```http
POST https://mindmate-api.local/api/ai/analyze-journal
```

Declaration Retrofit :

```java
@POST("ai/analyze-journal")
Call<AiAnalysisResult> analyzeJournal(@Body AiAnalysisRequest request);
```

### Payload envoye

Fichier :

```text
app/src/main/java/com/omartitouhi/mindmate/data/remote/AiAnalysisRequest.java
```

JSON attendu :

```json
{
  "journal_entry_id": "id-de-l-entree",
  "journal_text": "contenu texte du journal",
  "safety_instruction": "Provide supportive, non-medical wellness analysis. Do not provide medical diagnosis."
}
```

### Reponse attendue

Fichier :

```text
app/src/main/java/com/omartitouhi/mindmate/data/model/AiAnalysisResult.java
```

JSON attendu :

```json
{
  "main_emotion": "Sad",
  "estimated_stress_level": 6,
  "short_summary": "Resume court",
  "personalized_advice": "Conseil personnalise",
  "exercise_suggestion": "Respiration 4-4-4"
}
```

Champs Java :

```text
mainEmotion
estimatedStressLevel
shortSummary
personalizedAdvice
exerciseSuggestion
```

### Repository

Fichier :

```text
app/src/main/java/com/omartitouhi/mindmate/data/repository/AiRepository.java
```

Methode :

```java
analyzeJournal(JournalEntity journalEntry, AnalysisCallback callback)
```

Flux :

```text
AIAnalysisFragment
    -> AiViewModel.analyzeJournal(journalEntry)
        -> AiRepository.analyzeJournal(...)
            -> Resource.loading()
            -> creation AiAnalysisRequest(journalEntry.id, journalEntry.content)
            -> POST /ai/analyze-journal
            -> si success : Resource.success(AiAnalysisResult)
            -> si response invalide : Resource.error(...)
            -> si failure reseau : Resource.error(...)
```

### Limite importante

Le repository utilise actuellement :

```java
AppDatabase.getInstance(context).journalDao().getAllJournalEntries()
```

Cela liste toutes les entrees locales pour l'analyse IA, sans filtre utilisateur dans ce repository precis. Les autres modules Journal/Statistics filtrent par utilisateur. Pour durcir ce module, il faudrait remplacer par `getJournalEntriesForUser(userId)`.

## 8. API IA - Chatbot

### Service Retrofit

Fichier :

```text
app/src/main/java/com/omartitouhi/mindmate/data/remote/AiApiService.java
```

Endpoint :

```http
POST {MINDMATE_API_BASE_URL}/ai/chat
```

Avec l'URL par defaut actuelle :

```http
POST https://mindmate-api.local/api/ai/chat
```

Declaration Retrofit :

```java
@POST("ai/chat")
Call<ChatResponse> chat(@Body ChatRequest request);
```

### Payload envoye au chatbot

Fichier :

```text
app/src/main/java/com/omartitouhi/mindmate/data/remote/ChatRequest.java
```

JSON :

```json
{
  "messages": [
    {
      "id": "uuid-message",
      "userId": "uid-firebase",
      "role": "user",
      "content": "Bonjour, je me sens fatigue",
      "createdAt": 1710000000000
    }
  ],
  "safety_instruction": "Stay in a general wellbeing support role. Do not provide medical diagnosis, crisis assessment, or treatment instructions. Encourage professional help when appropriate."
}
```

### Reponse attendue

Fichier :

```text
app/src/main/java/com/omartitouhi/mindmate/data/remote/ChatResponse.java
```

JSON :

```json
{
  "reply": "Je suis desole que tu te sentes fatigue. Tu peux essayer une courte pause..."
}
```

### Modele ChatMessage

Fichier :

```text
app/src/main/java/com/omartitouhi/mindmate/data/model/ChatMessage.java
```

Champs :

```text
id: String
userId: String
role: String
content: String
createdAt: long
```

Roles :

```text
user
assistant
```

### Fonctionnement complet du chatbot

Fichier :

```text
app/src/main/java/com/omartitouhi/mindmate/data/repository/ChatRepository.java
```

#### Lecture de l'historique

Methode :

```java
getMessages()
```

Firestore :

```text
users/{userId}/chat_messages
```

Requete :

```java
orderBy("createdAt", Query.Direction.ASCENDING)
addSnapshotListener(...)
```

Fonctionnement :

```text
AIChatFragment
    -> ChatViewModel.getMessages()
        -> ChatRepository.getMessages()
            -> Firestore listener users/{uid}/chat_messages
            -> conversion snapshot.toObjects(ChatMessage.class)
            -> LiveData<List<ChatMessage>>
            -> RecyclerView ChatAdapter
```

Le listener est conserve dans :

```java
ListenerRegistration messagesRegistration
```

Et nettoye avec :

```java
dispose()
```

Appelee dans :

```java
ChatViewModel.onCleared()
```

#### Envoi d'un message utilisateur

Methode :

```java
sendMessage(String content, List<ChatMessage> currentMessages, ChatCallback callback)
```

Flux complet :

```text
1. UI clique sur envoyer.
2. ChatViewModel valide que le texte n'est pas vide.
3. ChatRepository emet Resource.loading().
4. Creation d'un ChatMessage role=user.
5. Sauvegarde du message utilisateur dans Firestore.
6. Creation de ChatRequest avec l'historique + le nouveau message.
7. POST /ai/chat.
8. Si la reponse contient reply :
      creation ChatMessage role=assistant.
      sauvegarde du message assistant dans Firestore.
      Resource.success(assistantMessage).
9. Si erreur :
      Resource.error(message).
```

#### Sauvegarde Firestore

Collection :

```text
users/{userId}/chat_messages/{messageId}
```

Document utilisateur :

```json
{
  "id": "uuid",
  "userId": "uid",
  "role": "user",
  "content": "texte",
  "createdAt": 1710000000000
}
```

Document assistant :

```json
{
  "id": "uuid",
  "userId": "uid",
  "role": "assistant",
  "content": "reponse IA",
  "createdAt": 1710000001000
}
```

### Garde-fou bien-etre

Chaque requete chat contient :

```text
Stay in a general wellbeing support role.
Do not provide medical diagnosis, crisis assessment, or treatment instructions.
Encourage professional help when appropriate.
```

Le chatbot doit donc rester dans le cadre du bien-etre general. Il ne doit pas diagnostiquer, prescrire, evaluer une crise ou remplacer un professionnel de sante.

## 9. Firebase Authentication

Fichier :

```text
app/src/main/java/com/omartitouhi/mindmate/data/repository/AuthRepository.java
```

Instance :

```java
FirebaseAuth.getInstance()
```

### Verification utilisateur connecte

```java
getCurrentUser()
isUserLoggedIn()
```

Utilise par :

```text
SplashActivity
```

Flux :

```text
SplashActivity
    -> AuthViewModel.isUserLoggedIn()
        -> AuthRepository.isUserLoggedIn()
            -> firebaseAuth.getCurrentUser() != null
```

### Connexion

Methode :

```java
login(String email, String password, ResourceCallback<FirebaseUser> callback)
```

Appel Firebase :

```java
firebaseAuth.signInWithEmailAndPassword(email, password)
```

Flux :

```text
LoginFragment
    -> AuthViewModel.login(email, password)
        -> validation email/password
        -> AuthRepository.login(...)
            -> Resource.loading()
            -> signInWithEmailAndPassword
            -> success : Resource.success(FirebaseUser)
            -> failure : Resource.error(message)
```

### Inscription

Methode :

```java
register(String displayName, String email, String password, ResourceCallback<User> callback)
```

Appel Firebase Auth :

```java
firebaseAuth.createUserWithEmailAndPassword(email, password)
```

Puis sauvegarde Firestore :

```text
users/{uid}
```

Document :

```json
{
  "id": "uid",
  "displayName": "Nom",
  "email": "email@example.com",
  "createdAt": 1710000000000
}
```

Flux :

```text
RegisterFragment
    -> AuthViewModel.register(...)
        -> validation nom/email/password/confirmation
        -> AuthRepository.register(...)
            -> Resource.loading()
            -> createUserWithEmailAndPassword
            -> creation User
            -> Firestore users/{uid}.set(user)
            -> Resource.success(user)
```

### Reset password

Methode :

```java
resetPassword(String email, ResourceCallback<Void> callback)
```

Appel Firebase :

```java
firebaseAuth.sendPasswordResetEmail(email)
```

Flux :

```text
ForgotPasswordFragment
    -> AuthViewModel.resetPassword(email)
        -> AuthRepository.resetPassword(email)
            -> Resource.loading()
            -> sendPasswordResetEmail
            -> Resource.success(null)
            -> Resource.error(message)
```

### Deconnexion

```java
firebaseAuth.signOut()
```

Utilisee dans :

- `AuthViewModel.logout()`
- `ProfileRepository.logout()`

## 10. Firestore - Structure des donnees

Collections principales :

```text
users/{userId}
users/{userId}/moods/{moodId}
users/{userId}/journal_entries/{entryId}
users/{userId}/chat_messages/{messageId}
users/{userId}/fcm_tokens/{tokenId}
```

### Profil utilisateur

Chemin :

```text
users/{uid}
```

Cree pendant l'inscription.

Mis a jour dans :

```text
ProfileRepository.updateDisplayName()
```

Avec :

```java
set(updates, SetOptions.merge())
```

### Humeurs

Chemin :

```text
users/{userId}/moods/{moodId}
```

Cree dans :

```text
MoodRepository.saveMoodInFirestore()
```

Document :

```json
{
  "id": "uuid",
  "userId": "uid",
  "mood": "Happy",
  "stressScore": 3,
  "note": "Note courte",
  "createdAt": 1710000000000,
  "weatherCity": "Tunis",
  "weatherTemperature": 22.5,
  "weatherCondition": "Clear"
}
```

### Journal

Chemin :

```text
users/{userId}/journal_entries/{entryId}
```

Creation/update :

```text
JournalRepository.saveEntry()
```

Suppression :

```text
JournalRepository.deleteEntry()
```

Document :

```json
{
  "id": "uuid",
  "userId": "uid",
  "title": "Titre",
  "content": "Contenu",
  "mood": "Calm",
  "createdAt": 1710000000000,
  "updatedAt": 1710000000000
}
```

### Chat IA

Chemin :

```text
users/{userId}/chat_messages/{messageId}
```

Lecture temps reel :

```java
addSnapshotListener()
```

Tri :

```java
orderBy("createdAt", ASCENDING)
```

### Tokens FCM

Chemin :

```text
users/{userId}/fcm_tokens/{token}
```

Document :

```json
{
  "token": "fcm-token",
  "updatedAt": 1710000000000
}
```

## 11. Firestore Rules

Fichier :

```text
firestore.rules
```

Principe :

```text
Un utilisateur authentifie peut lire/ecrire uniquement son propre document users/{userId}
et ses sous-collections.
```

Regle principale :

```js
allow read, write: if request.auth != null && request.auth.uid == userId;
```

Sous-collections protegees :

- `moods`
- `journal_entries`
- `chat_messages`
- `fcm_tokens`

## 12. Room Database

Fichier :

```text
app/src/main/java/com/omartitouhi/mindmate/data/local/AppDatabase.java
```

Nom DB :

```text
mindmate.db
```

Version :

```text
4
```

Entities :

```text
MoodEntity
JournalEntity
```

Configuration :

```java
@Database(entities = {MoodEntity.class, JournalEntity.class}, version = 4, exportSchema = true)
```

Il n'y a pas de `fallbackToDestructiveMigration`, ce qui evite les suppressions automatiques silencieuses en cas de migration manquante.

## 13. Room - MoodEntity et MoodDao

Table :

```text
moods
```

Champs :

```text
id
userId
mood
stressScore
note
createdAt
weatherCity
weatherTemperature
weatherCondition
synced
```

DAO :

```java
insert(MoodEntity moodEntity)
markAsSynced(String id)
getAllMoods()
getMoodsForUser(String userId)
getUnsyncedMoodsForUser(String userId)
```

Requetes importantes :

```sql
SELECT * FROM moods WHERE userId = :userId ORDER BY createdAt DESC
```

```sql
SELECT * FROM moods WHERE userId = :userId AND synced = 0 ORDER BY createdAt ASC
```

## 14. Room - JournalEntity et JournalDao

Table :

```text
journal_entries
```

Champs :

```text
id
userId
title
content
mood
createdAt
updatedAt
synced
```

DAO :

```java
insert(JournalEntity journalEntity)
update(JournalEntity journalEntity)
delete(JournalEntity journalEntity)
deleteById(String id)
markAsSynced(String id)
getAllJournalEntries()
getJournalEntriesForUser(String userId)
getJournalEntry(String id)
getUnsyncedJournalEntriesForUser(String userId)
```

Requetes importantes :

```sql
SELECT * FROM journal_entries WHERE userId = :userId ORDER BY createdAt DESC
```

```sql
SELECT * FROM journal_entries WHERE userId = :userId AND synced = 0 ORDER BY updatedAt ASC
```

## 15. Mood Tracking - Fonctionnement complet

UI :

```text
MoodCheckInFragment
```

ViewModel :

```text
MoodViewModel
```

Repository :

```text
MoodRepository
```

Flux sauvegarde :

```text
1. L'utilisateur choisit humeur + stress + note.
2. MoodViewModel valide :
      humeur non vide
      stress entre 1 et 10
3. MoodRepository cree un Mood.
4. La derniere meteo cachee est ajoutee si disponible.
5. syncPendingMoods() tente de pousser les anciennes humeurs non synchronisees.
6. Insertion Room avec synced=false.
7. Ecriture Firestore users/{uid}/moods/{moodId}.
8. Si success Firestore :
      MoodDao.markAsSynced(id)
      Resource.success(mood)
9. Si erreur :
      Resource.error(message)
      la ligne Room reste en synced=false
```

## 16. Journal - Fonctionnement complet

UI :

```text
JournalListFragment
AddJournalFragment
JournalDetailsFragment
```

ViewModel :

```text
JournalViewModel
```

Repository :

```text
JournalRepository
```

### Liste

```text
JournalListFragment
    -> JournalViewModel.getJournalEntries()
        -> JournalRepository.getJournalEntries()
            -> JournalDao.getJournalEntriesForUser(userId)
```

### Ajout

```text
AddJournalFragment
    -> JournalViewModel.addEntry(title, content, mood)
        -> validation
        -> JournalRepository.addEntry(...)
            -> creation JournalEntry avec UUID
            -> saveEntry()
```

### Modification

```text
JournalDetailsFragment
    -> JournalViewModel.updateEntry(id, createdAt, title, content, mood)
        -> saveEntry()
```

`saveEntry()` fait :

```text
1. Resource.loading()
2. syncPendingEntries()
3. Room insert avec OnConflictStrategy.REPLACE, synced=false
4. Firestore set(entry)
5. markAsSynced(id)
6. Resource.success(entry)
```

### Suppression

```text
JournalDetailsFragment
    -> JournalViewModel.deleteEntry(currentEntry)
        -> JournalRepository.deleteEntry(entity)
```

Flux :

```text
1. Resource.loading()
2. Firestore users/{uid}/journal_entries/{id}.delete()
3. Si success : Room delete(entity)
4. Resource.success(entry)
5. Si erreur : Resource.error(message)
```

## 17. Statistics

UI :

```text
StatisticsFragment
```

ViewModel :

```text
StatisticsViewModel
```

Repository :

```text
StatisticsRepository
```

Sources :

```text
MoodDao.getMoodsForUser(userId)
JournalDao.getJournalEntriesForUser(userId)
```

Calculs :

- scores d'humeur sur 7 jours ;
- stress moyen ;
- nombre d'entrees journal ;
- humeur la plus frequente ;
- progression hebdomadaire.

Score humeur :

```text
Happy -> 5
Calm -> 4
Tired -> 3
Sad/Stressed -> 2
Angry -> 1
autre -> 3
```

Progression hebdomadaire :

```text
aucune humeur -> Aucune donnee cette semaine
stress <= 4 -> Semaine plutot stable
stress <= 7 -> Semaine a surveiller
stress > 7 -> Stress eleve cette semaine
```

## 18. Profile

UI :

```text
ProfileFragment
```

ViewModel :

```text
ProfileViewModel
```

Repository :

```text
ProfileRepository
```

### Chargement profil

Flux :

```text
1. FirebaseAuth.getCurrentUser()
2. Affichage fallback depuis FirebaseUser
3. Firestore users/{uid}.get()
4. Remplacement par displayName/email/createdAt stockes
```

### Mise a jour nom

Flux :

```text
1. validation nom non vide
2. FirebaseUser.updateProfile(UserProfileChangeRequest)
3. Firestore users/{uid}.set(updates, SetOptions.merge())
4. reload profil
5. Resource.success("Profil mis a jour.")
```

### Deconnexion

```java
FirebaseAuth.signOut()
```

Puis l'UI redirige vers `AuthActivity`.

## 19. Settings

UI :

```text
SettingsFragment
```

ViewModel :

```text
SettingsViewModel
```

Repository :

```text
SettingsRepository
```

SharedPreferences :

```text
mindmate_settings
```

Cles :

```text
notifications_enabled
journal_reminder_hour
journal_reminder_minute
dark_mode
```

Fonctions :

```java
setNotificationsEnabled(boolean)
setReminderTime(int hour, int minute)
setDarkModeEnabled(boolean)
deleteLocalData(...)
```

Suppression locale :

```java
AppDatabase.getInstance(context).clearAllTables()
```

Une confirmation UI est affichee avant suppression.

## 20. Notifications FCM et rappels locaux

### Service FCM

Fichier :

```text
app/src/main/java/com/omartitouhi/mindmate/utils/NotificationService.java
```

Herite de :

```java
FirebaseMessagingService
```

Fonctions :

```java
onMessageReceived(RemoteMessage message)
onNewToken(String token)
```

### Reception notification

Flux :

```text
Firebase Cloud Messaging
    -> NotificationService.onMessageReceived()
        -> lecture data/title/body/type
        -> NotificationHelper.showNotification(type, title, body)
```

Types :

```text
daily_journal
meditation
motivation
```

### Token FCM

Recuperation :

```java
FirebaseMessaging.getInstance().getToken()
```

Sauvegarde :

```text
users/{userId}/fcm_tokens/{token}
```

### Notification locale journal

Planification :

```java
AlarmManager.setInexactRepeating(...)
```

Receiver :

```text
JournalReminderReceiver
```

Au declenchement :

```text
NotificationHelper.showNotification(
    TYPE_DAILY_JOURNAL,
    title,
    body
)
```

Apres redemarrage :

```text
BOOT_COMPLETED
    -> JournalReminderReceiver
        -> relit SharedPreferences
        -> replanifie le rappel
```

## 21. Navigation Component

### MainActivity

`MainActivity` utilise :

```java
NavHostFragment navHostFragment =
    (NavHostFragment) getSupportFragmentManager()
        .findFragmentById(R.id.nav_host_fragment);

NavController navController = navHostFragment.getNavController();
```

Cela evite le crash lie a `Navigation.findNavController()` avec `FragmentContainerView`.

### NavHostFragment

Fichier :

```text
app/src/main/res/layout/content_main.xml
```

```xml
<androidx.fragment.app.FragmentContainerView
    android:id="@+id/nav_host_fragment"
    android:name="androidx.navigation.fragment.NavHostFragment"
    app:defaultNavHost="true"
    app:navGraph="@navigation/nav_graph" />
```

### Bottom Navigation

Menu :

```text
app/src/main/res/menu/bottom_nav_menu.xml
```

Destinations :

```text
homeFragment
journalListFragment
aiChatFragment
statisticsFragment
profileFragment
```

### Drawer Navigation

Menu :

```text
app/src/main/res/menu/drawer_menu.xml
```

Destinations :

```text
moodCheckInFragment
meditationFragment
settingsFragment
aboutFragment
```

### Navigation graph principal

Fichier :

```text
app/src/main/res/navigation/nav_graph.xml
```

Start destination :

```text
homeFragment
```

Destinations :

```text
homeFragment
journalListFragment
addJournalFragment
journalDetailsFragment
aiChatFragment
aiAnalysisFragment
meditationFragment
statisticsFragment
profileFragment
settingsFragment
aboutFragment
moodCheckInFragment
```

### Navigation auth

Fichier :

```text
app/src/main/res/navigation/auth_nav_graph.xml
```

Start destination :

```text
loginFragment
```

Destinations :

```text
loginFragment
registerFragment
forgotPasswordFragment
```

## 22. Meditation / Breathing

UI :

```text
MeditationFragment
```

ViewModel :

```text
MeditationViewModel
```

Liste d'exercices :

```text
Respiration 4-4-4
Relaxation rapide 2 minutes
Focus breathing
Sleep relaxation
```

Fonctions UI :

- RecyclerView des exercices ;
- selection exercice ;
- timer ;
- boutons Start / Pause / Reset ;
- animation Lottie `breathing_circle.json`.

Ce module ne consomme pas d'API distante.

## 23. Securite et confidentialite

### Backup Android

Dans `AndroidManifest.xml` :

```xml
android:allowBackup="false"
```

Les fichiers XML de backup excluent aussi :

```text
mindmate.db
mindmate.db-shm
mindmate.db-wal
mindmate_settings.xml
```

### API keys

`app/google-services.json` est ajoute au `.gitignore`. Si le fichier a deja ete versionne dans Git, il faut le retirer du suivi et restreindre la cle Firebase dans Google Cloud Console.

### Donnees sensibles

Les donnees sensibles sont :

- entrees journal ;
- humeurs ;
- messages chatbot ;
- email utilisateur ;
- token FCM.

Elles sont stockees sous `users/{uid}` dans Firestore et localement dans Room.

## 24. Limitations techniques actuelles

### Backend IA non fourni dans le projet

Le client Android est pret, mais l'URL par defaut :

```text
https://mindmate-api.local/api/
```

necessite un backend reel compatible avec :

```text
POST /ai/chat
POST /ai/analyze-journal
```

### Synchronisation offline simple

La synchronisation des donnees non synchronisees se fait lors des nouvelles sauvegardes Mood/Journal. Il n'y a pas encore de WorkManager qui synchronise automatiquement en arriere-plan.

### Geolocalisation meteo fixe

La meteo utilise Tunis par defaut :

```text
36.8065, 10.1815
```

Il n'y a pas encore de choix de ville ou GPS.

### AI Analysis liste toutes les entrees locales

`AiRepository` utilise encore `getAllJournalEntries()`. Pour une isolation stricte multi-utilisateur, il faut utiliser `getJournalEntriesForUser(userId)`.

## 25. Checklist des fonctionnalites par module

### Auth

- Login email/password : oui
- Register email/password : oui
- Forgot password : oui
- Splash redirect : oui
- Logout : oui
- Profil Firestore apres inscription : oui

### Mood

- Choix humeur : oui
- Score stress : oui
- Note : oui
- Room : oui
- Firestore : oui
- Weather attach : oui
- Offline cache : oui

### Journal

- Liste : oui
- Ajout : oui
- Modification : oui
- Suppression : oui
- Room : oui
- Firestore : oui
- Offline cache : oui

### Weather

- Retrofit : oui
- Open-Meteo : oui
- Cache SharedPreferences : oui
- Conseil meteo : oui

### AI

- Chat UI : oui
- Chat Firestore history : oui
- Chat API Retrofit : oui
- Analysis API Retrofit : oui
- Safety instruction : oui
- Backend reel requis : oui

### Notifications

- FCM service : oui
- Local notification : oui
- Token Firestore : oui
- Daily journal reminder : oui
- Boot reschedule : oui

## 26. Conclusion

MindMate AI possede une base technique complete et modulaire. Les fonctionnalites principales sont structurees autour de MVVM, Firebase, Room et Retrofit. Les APIs sont isolees dans des services Retrofit, les appels Firebase sont centralises dans les repositories, les donnees offline sont stockees avec Room et les etats UI sont unifies avec `Resource<T>`.

La partie chatbot est deja implementee cote Android : elle sauvegarde les messages utilisateur et assistant dans Firestore, envoie l'historique au backend IA, applique une instruction de securite et affiche les reponses dans une RecyclerView. Pour etre operationnelle en production, elle demande simplement un backend IA reel compatible avec les contrats JSON documentes ci-dessus.

Enfin, toutes les fonctionnalites liees au bien-etre doivent rester informatives. L'application ne remplace pas un professionnel de sante et ne fournit pas de diagnostic medical.
