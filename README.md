# SwipeHire - PROG7314 POE Final Part

Native Kotlin + Jetpack Compose frontend for **SwipeHire**, the reverse job-matching app from the
PROG7314 Part 1 design where companies swipe on student profiles, students swipe on job postings,
mutual interest opens a chat that could possibly link them to their next job/candidate.

- What is the Purpose of the application?
To bridge the gap between companies who are looking for new employees, and students who are actively looking
for job openings.
- What makes our App Different?
The easy interaction of swiping left and right to match companies and students. Its an easier, more fun, and effective way of meeting
with a potential new Employee and Company. 

## Group Members
|Name|Surname|StudentNo|Group|
|---|---|---|---|
|Ruan Helgaard|Hartman|ST10453370|Group 1|
|Alexandro|Ornelas|ST10437348|Group 1|
|Joe Leo|Van Niekerk|ST10445055|Group 3|
|Clifford|Naude|ST10258513|Group 1|

## YouTube Video Link
https://www.youtube.com/watch?v=XUOiXUlPuo4

## Classroom50 Repository Link
https://github.com/EMGPRS/prog7314-2026-poe-final-naudeclifford.git

## Repository Link for Part 2
This i where we did a lot of the work for part 3 and worked in separate branches for the different features and changes that were being made. The final releases, workflows and documents were done in the the Final Poe repository. 
https://github.com/EMGPRS/prog7314-2026-poe-part-2-group-naudeclifford

## Part 3 New Features:
We had added biometric login, and also a session state that includes offline sync with the application and the api.
This allows the user to use the application even when the device is offline.
We included a notification service to allow for 2 factor authentication and other uses for the application.
We had multi-language working in part 2 but refined it and ensured it is working.
(Please see Screenshots in the Screens&Features Section)

We have our user defined feature 4 that has been added: 
Interview Scheduler

· What it does: Allows a Company to propose an interview date, time, duration, and mode (Video, Phone, In-Person). The Student can then Accept, Decline, or the Company can Cancel. If accepted, it provides an "Add to Calendar" button.
· Where to find it in the app: Inside a Chat/Match, in the collapsible "Application status" panel.
(Please see Screenshots in the Screens&Features Section)

We have our user defined feature 5 that has been added: 
Application Pipeline Tracker

· What it does: A visual timeline that tracks a candidate's progress: Matched → Interview → Offer → Hired (or Declined). It has role-based rules (e.g., only the company can move a candidate to "Offer", only the student can "Accept Offer"). It shows live updates on the Matches screen and in the Chat timeline.
· Where to find it in the app: The timeline inside the Chat panel, and the colored status chips on the main Matches screen list.
(Please see Screenshots in the Screens&Features Section)

We had kept the UI from part 2 and had updated it after finding any inconsistances.
We have prepaid the application to be sent to google playstore and ready for publishication.


--- 


## Screens&Features

| Screen | File | Notes | Screenshot |
|---|---|---|---|
| Login | `ui/screens/LoginScreen.kt` | Google SSO + biometric sign-in entry points (stubbed) |<img width="383" height="792" alt="Screenshot 2026-09-22 085823" src="https://github.com/user-attachments/assets/a605c62f-4afd-4178-931c-7a973e12ae46" />|
| Role select | `ui/screens/RoleSelectScreen.kt` | Choose Student / Company — same screen serves both |<img width="385" height="788" alt="Screenshot 2026-09-22 091557" src="https://github.com/user-attachments/assets/6cd638d2-c183-46c5-8275-5c8d8f2dd2f2" />|
| Discover | `ui/screens/DiscoverScreen.kt` | Draggable swipe-card stack (`ui/components/SwipeCardStack.kt`) |<img width="391" height="791" alt="Screenshot 2026-09-22 085927" src="https://github.com/user-attachments/assets/ca096d9c-b378-4987-9063-ecfc93e49a55" />|
| Matches | `ui/screens/MatchesScreen.kt` | List of mutual matches with unread indicator |<img width="378" height="792" alt="Screenshot 2026-09-22 092543" src="https://github.com/user-attachments/assets/485f99cf-680d-43fc-92f0-fbcc1187620c" />|
| Chat | `ui/screens/ChatDetailScreen.kt` | Per-match conversation, local send | <img width="180" alt="Chat conversation" src="https://github.com/user-attachments/assets/49d78bbd-9b5b-4077-aee6-51c5b0a22756" /> <img width="180" alt="Chat application status" src="https://github.com/user-attachments/assets/c13c0d40-8d45-452a-870a-5f8bd5702fa6" /> |
| Profile | `ui/screens/ProfileScreen.kt` | Adapts content to Student vs Company account type |<img width="373" height="791" alt="Screenshot 2026-09-22 092620" src="https://github.com/user-attachments/assets/ce04fc50-fdf2-4f8e-a9ce-43ff0f57b821" />|
| **Settings** | `ui/screens/SettingsScreen.kt` | **20-mark deliverable — see below** |<img width="384" height="796" alt="Screenshot 2026-09-22 090826" src="https://github.com/user-attachments/assets/f96ef047-fbf5-4dbe-bf12-9cd081e3e0d5" />|
| Jobs near you | `ui/screens/NearbyJobsScreen.kt` | Google Map + distance-sorted list, from Discover's "Near you" button (student view) | <img width="380" alt="Jobs near you map" src="https://github.com/user-attachments/assets/0696f007-8322-40b9-af53-6cf36fc99fd5" /> |
| Job location | `ui/screens/JobLocationScreen.kt` | Single job on a map with a "Get directions" deep link into Google Maps | <img width="380" alt="Job location map" src="https://github.com/user-attachments/assets/99566277-a37b-441c-9f47-017bf42e5ff6" /> |
| Application Status | `ui/screens/ChatDetailScreen.kt` `Feature 4` | Track Application Status | <img width="380" alt="Application status" src="https://github.com/user-attachments/assets/8f933b20-28aa-404d-aab1-57307064d6a0" /> |
| Push Notifications | `Firebase Cloud Messaging` | Get Real-Time updates on Matches, New Job Listings, and More! | <img width="380" alt="Push notification" src="https://github.com/user-attachments/assets/49d015df-c453-4af4-850e-7da0119e8e34" /> |
| Schedule Interview | `Feature 5` | Propose an Interview with a Date, Time, Duration, Format, and More. Check the Pipeline for tracking. | <img width="180" alt="Interview proposal" src="https://github.com/user-attachments/assets/2ee4b79b-1829-431e-aed6-e0675695c813" /> <img width="180" alt="Interview response" src="https://github.com/user-attachments/assets/eab6684e-bfbb-4889-98b4-a22b579883a2" /> |
| Biometric Lock and Offline sync | `Security` `Offline sync` | Biometric Security Feature and offline sync | <img width="180" alt="Biometric lock" src="https://github.com/user-attachments/assets/acbea5a6-2cc1-4f2e-82fd-dfcd7dc8a0d1" /> <img width="180" alt="Offline sync" src="https://github.com/user-attachments/assets/a1d6f544-40f4-4f92-b26d-5d81956c78e6" /> |


## Main Features

The following three features were described in the Part 1 planning document and implemented in SwipeHire:

1. Profiles and CVs: Students can create and edit profiles containing their course, study year and skills, upload a PDF CV and view a profile strength score. Companies can create profiles describing their industry and hiring needs.

2. Swipe-based discovery and matching: Students browse job postings while companies browse student profiles. The app highlights overlapping skills, records each decision and creates a match when both sides express interest.

3. Chat and in-app alerts: Matched students and companies can exchange messages in a private conversation. The app also displays alerts for activity such as new matches and messages.

4. Interview Scheduler: What it does: Allows a Company to propose an interview date, time, duration, and mode (Video, Phone, In-Person). The Student can then Accept, Decline, or the Company can Cancel. If accepted, it provides an "Add to Calendar" button. Where to find it in the app: Inside a Chat/Match, in the collapsible "Application status" panel.

5. Application Pipeline Tracker: What it does: A visual timeline that tracks a candidate's progress: Matched → Interview → Offer → Hired (or Declined). It has role-based rules (e.g., only the company can move a candidate to "Offer", only the student can "Accept Offer"). It shows live updates on the Matches screen and in the Chat timeline. Where to find it in the app: The timeline inside the Chat panel, and the colored status chips on the main Matches screen list.

6. Maps, geocoding and jobs near you: In response to lecturer feedback, we added location-based job discovery. The API converts a job’s work address into map coordinates. Students can view job markers on Google Maps, see jobs ordered by distance from their location and open directions to a job.

7. A milestone feature has also been implemented where user can earn badges based off of the number of matches made.


## Offline mode and synchronisation

Signed-in users can save or unsave jobs and student profiles, and change account settings, while their phone is offline. Previously loaded saved items remain available from a local cache, and new choices appear immediately. The app stores pending changes in an on-device SQLite outbox (`app/src/main/java/com/swipehire/app/offline/OfflineActionStore.kt`). Repeated changes to the same saved item are combined so that the latest choice is sent.

`OfflineSyncManager` schedules a WorkManager task that waits for network access. `OfflineSyncWorker` sends queued saved-item and account-setting changes to the REST API when connectivity returns. It removes each change after a successful response and retries failed requests. The **Settings → Offline synchronisation** section shows how many changes are still waiting. Device preferences such as the theme are also stored locally using DataStore.

To demonstrate this feature, sign in and load saved items while online, disconnect the phone, then save or unsave an item or change a notification or privacy setting. Check the pending count in Settings. Reconnect and confirm that the count returns to zero and the change appears in the online data. This SQLite queue covers saved items and account settings; other features may require a connection.


## Settings menu (Application State requirement)

Every control on the Settings screen reads from and writes to Jetpack **DataStore Preferences**
(`data/SettingsDataStore.kt`) through `viewmodel/SettingsViewModel.kt`, so changes persist across
app restarts — this is what satisfies the "Application State: Changing and saving user
preferences via a dedicated settings menu" requirement:

- **Account type** - Student / Company toggle (shared UI, drives Discover + Profile content)
- **Notifications** - push master switch, match alerts, message alerts
- **Privacy** - profile visibility toggle
- **Language** - change language of the application
- **Appearance** - Light / Dark / Match system theme, applied live via `SwipeHireTheme`
- **Security** - biometric lock toggle (A fingerprint or face needs to be added to the device before this feature can be enabled)
- **Offline synchronisation** - shows the pending count for saved-item and account-setting changes; see the offline section above
- **Log out**


<img width="384" height="796" alt="Screenshot 2026-09-22 090826" src="https://github.com/user-attachments/assets/f37b2974-6790-4b89-8d45-eca32eaa78b2" />



<img width="386" height="786"  alt="image" src="https://github.com/user-attachments/assets/30ac5b33-0c39-4000-8cc5-13e7f4bf5600" />




## Stack

- Kotlin, Jetpack Compose (Material 3), Navigation Compose
- DataStore Preferences for persisted settings
- ViewModel + StateFlow for state management
- Custom gesture-driven swipe card stack (no third-party swipe library)
- Maps Compose + Play Services Location for job locations and "Jobs near you"
- Firebase Cloud Messaging and a Firestore-triggered Cloud Function for real-time device alerts
- SQLite outbox and WorkManager for saved-item and account-setting synchronisation



## Cloud Hosted REST API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| 🟢 `GET` | `/api/jobs` | Fetches the full list of job postings to populate the student swipe feed and map views |
| 🔵 `POST` | `/api/jobs` | Creates a new job posting for hiring companies |
| 🔴 `DELETE` | `/api/jobs/{id}` | Deletes an existing job posting by its unique ID |
| 🟢 `GET` | `/api/companies/{companyId}/jobs` | List a company’s jobs |
| 🔵 `POST` | `/api/companies` | Registers a new company profile |
| 🟡 `PUT` | `/api/companies/{id}` | Updates company details, location, and industry information |
| 🟢 `GET` | `/api/students` | Fetches student profiles to populate the company candidate swipe feed |
| 🔵 `POST` | `/api/students` | Registers a new student profile during initial onboarding |
| 🟡 `PUT` | `/api/students/{id}` | Updates an existing student's profile information, skills, and blurb |
| 🟢 `GET` | `/api/swipes/{userId}` | Get previously swiped IDs |
| 🔵 `POST` | `/api/swipes` | Records a swipe action between users/jobs and determines if a mutual match occurred |
| 🔴 `DELETE` | `/api/swipes` | Undo a swipe; takes `userId`, `targetId`, and `targetUserId` as query parameters |
| 🟢 `GET` | `/api/users/{userId}/saved` | Get saved jobs and students |
| 🟡 `PUT` | `/api/users/{userId}/saved/{kind}/{itemId}` | Save or unsave an item; `kind` is `job` or `student` |
| 🟢 `GET` | `/api/users/{userId}/settings` | Get account settings |
| 🟡 `PUT` | `/api/users/{userId}/settings` | Update account settings |
| 🔵 `POST` | `/api/location/geocode` | Convert an address to coordinates |

## GitHub Actions and test evidence

[CI runs](https://github.com/EMGPRS/prog7314-2026-poe-final-naudeclifford/actions/workflows/ci.yml) cover Android unit tests and a debug build, API tests and a Release build, and Cloud Functions tests. CI runs on every push and on pull requests targeting `master`. Android and API test reports are attached to each run as artifacts.

[Azure deployment runs](https://github.com/EMGPRS/prog7314-2026-poe-final-naudeclifford/actions/workflows/deploy-api-azure.yml) run on relevant pushes to `master` or by manual dispatch. Deployment waits for all CI jobs to pass and uses the `AZURE_WEBAPP_PUBLISH_PROFILE` GitHub secret. 



## Set up and run SwipeHire

### Install the signed APK on an Android phone

The signed APK runs on Android 8.0 (API 26) or newer. It connects to the [SwipeHire API hosted on Azure](https://swipehire-api-h7d3aqh2ahb3aqcu.southafricanorth-01.azurewebsites.net/) and to Firebase. You do not need to run the API locally, install Android Studio, or provide API credentials to use the APK. Sign-in and cloud data need an internet connection; saved items and account settings support the offline synchronisation described above.

1. Open the project's [GitHub Releases](https://github.com/EMGPRS/prog7314-2026-poe-final-naudeclifford/releases) page and choose the release containing the signed APK.
2. Under **Assets**, download `swipehire-signed-app-release.apk`. Choose the APK asset, not a **Source code** ZIP file. If you download it on a computer, transfer the APK to the phone.
3. On the phone, open the downloaded APK from **Files** or **Downloads**. If Android asks for permission to install from that source, allow it, then tap **Install**.
4. Open SwipeHire and sign in with one of the demo accounts below.

The APK already contains its build configuration. A Maps API key and `local.properties` file are only needed when building the app from source.

### Demo accounts

| Account | Email | Password |
|---|---|---|
| Student | `lerato.student@swipehire.example.com` | `StudentDemo!2026` |
| Company | `company.demo@swipehire.example.com` | `CompanyDemo!2026` |

### Build from source (contributors only)

Install Android Studio with Android SDK 34 and JDK 17. The repository includes `app/google-services.json`. Android Studio normally creates the gitignored `local.properties` file with `sdk.dir` pointing to your Android SDK. Add the deployed API address and your Android-restricted Maps key to that file:

```properties
API_BASE_URL=https://swipehire-api-h7d3aqh2ahb3aqcu.southafricanorth-01.azurewebsites.net/
MAPS_API_KEY=<your-Android-restricted-Maps-key>
```

A source build without your Maps key will not display the interactive map. For Google sign-in in a newly signed build, register that build's SHA-1 fingerprint in Firebase. The API remains hosted on Azure; no local API process is required.

### Developer checks (optional)

These checks verify the source code and are not required to install the APK:

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug
dotnet test SwipeHire.Api/SwipeHire.Api.slnx --configuration Release
npm.cmd --prefix functions ci
npm.cmd --prefix functions run check
npm.cmd --prefix functions test
```

The same Android, API, and Cloud Functions checks run in GitHub Actions.


## Playstore Publication Readiness

### A Signed APK has been included in the GitHub Releases:

Download the signed APK from the [SwipeHire GitHub Releases page](https://github.com/EMGPRS/prog7314-2026-poe-final-naudeclifford/releases) under **Assets**.

<img width="385" height="142" alt="image" src="https://github.com/user-attachments/assets/f39f3f91-6293-411c-9f2c-191c60941962" />


### Promotional Screenshots: 
<img width="383" height="792" alt="Screenshot 2026-09-22 085823" src="https://github.com/user-attachments/assets/a605c62f-4afd-4178-931c-7a973e12ae46" />

<img width="385" height="788" alt="Screenshot 2026-09-22 091557" src="https://github.com/user-attachments/assets/6cd638d2-c183-46c5-8275-5c8d8f2dd2f2" />

<img width="391" height="791" alt="Screenshot 2026-09-22 085927" src="https://github.com/user-attachments/assets/ca096d9c-b378-4987-9063-ecfc93e49a55" />

<img width="378" height="792" alt="Screenshot 2026-09-22 092543" src="https://github.com/user-attachments/assets/485f99cf-680d-43fc-92f0-fbcc1187620c" />

<img width="380" alt="Promotional screenshot" src="https://github.com/user-attachments/assets/7c4a9bef-8843-4c2a-aa7b-e7333a6d0512" />


## Reference List

- Firebase (2026a) Authenticate with Google on Android. Available at: firebase.google.com/docs/auth/android/google-signin (Accessed: 4 November 2026).

- Firebase (2026b) Google services Gradle plugin and JSON config file. Available at: firebase.google.com/docs/android/google-services-plugin-and-file (Accessed: 4 November 2026).

- Firebase (2026c) Learn about and manage API keys for Firebase. Available at: firebase.google.com/docs/projects/api-keys (Accessed: 5 November 2026).

- Firebase (2026d) Troubleshooting & FAQ for Android and Firebase. Available at: firebase.google.com/docs/android/troubleshooting-faq (Accessed: 5 November 2026).

- GitHub (n.d.a) About protected branches. Available at: docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-protected-branches/about-protected-branches (Accessed: 5 November 2026).

- GitHub (n.d.b) Deploying with GitHub Actions. Available at: docs.github.com/en/actions/how-tos/deploy/configure-and-manage-deployments/control-deployments (Accessed: 5 November 2026).

- GitHub (n.d.c) Workflow artifacts. Available at: docs.github.com/en/actions/concepts/workflows-and-actions/workflow-artifacts (Accessed: 5 November 2026).

- GitHub (n.d.d) Workflow syntax for GitHub Actions. Available at: docs.github.com/en/actions/reference/workflows-and-actions/workflow-syntax (Accessed: 5 November 2026).


- Firebase (2026e) Get real-time updates with Cloud Firestore. Available at: firebase.google.com/docs/firestore/query-data/listen (Accessed: 6 November 2026).

- Firebase (2026f) Write conditions for Cloud Firestore Security Rules. Available at: firebase.google.com/docs/firestore/security/rules-conditions (Accessed: 6 November 2026).

- Firebase (2026g) Add data to Cloud Firestore. Available at: firebase.google.com/docs/firestore/manage-data/add-data (Accessed: 6 November 2026).

- Firebase (2026h) Cloud Firestore data model. Available at: firebase.google.com/docs/firestore/data-model (Accessed: 6 November 2026).

- Firebase (2026i) Access data offline. Available at: firebase.google.com/docs/firestore/manage-data/enable-offline (Accessed: 6 November 2026).

- Firebase (2026j) Get started with Firebase Cloud Messaging in Android apps. Available at: firebase.google.com/docs/cloud-messaging/android/get-started (Accessed: 6 November 2026).


- Android Developers (2026a) Date pickers. Available at: developer.android.com/develop/ui/compose/components/datepickers (Accessed: 6 November 2026).

- Android Developers (2026b) Time pickers. Available at: developer.android.com/develop/ui/compose/components/time-pickers (Accessed: 6 November 2026).

- Android Developers (2026c) Calendar provider overview. Available at: developer.android.com/guide/topics/providers/calendar-provider (Accessed: 6 November 2026).
	
- Android Developers (2026d) ViewModel overview. Available at: developer.android.com/topic/libraries/architecture/viewmodel (Accessed: 6 November 2026).

- Android Developers (2026e) StateFlow and SharedFlow. Available at: developer.android.com/kotlin/flow/stateflow-and-sharedflow (Accessed: 6 November 2026).
	
 - Android Developers (2026f) Log. Available at: developer.android.com/reference/android/util/Log (Accessed: 6 November 2026).
	
  - Android Developers (2026g) Build local unit tests. Available at: developer.android.com/training/testing/local-tests (Accessed: 6 November 2026).

