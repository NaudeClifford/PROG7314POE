# SwipeHire — PROG7314 POE Final Part

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
 https://www.youtube.com/watch?v=MQEapbOJT-U

## Classroom50 Repository Link
https://github.com/EMGPRS/prog7314-2026-poe-final-naudeclifford.git


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
| Chat | `ui/screens/ChatDetailScreen.kt` | Per-match conversation, local send |<img width="378" height="792" alt="Screenshot 2026-09-22 092543" src="https://github.com/user-attachments/assets/49d78bbd-9b5b-4077-aee6-51c5b0a22756" />|
| Profile | `ui/screens/ProfileScreen.kt` | Adapts content to Student vs Company account type |<img width="373" height="791" alt="Screenshot 2026-09-22 092620" src="https://github.com/user-attachments/assets/ce04fc50-fdf2-4f8e-a9ce-43ff0f57b821" />|
| **Settings** | `ui/screens/SettingsScreen.kt` | **20-mark deliverable — see below** |<img width="384" height="796" alt="Screenshot 2026-09-22 090826" src="https://github.com/user-attachments/assets/f96ef047-fbf5-4dbe-bf12-9cd081e3e0d5" />|
| Jobs near you | `ui/screens/NearbyJobsScreen.kt` | Google Map + distance-sorted list, from Discover's "Near you" button (student view) |<img width="267" height="537" alt="Screenshot 2026-09-22 135603" src="https://github.com/user-attachments/assets/2b6a4b42-5571-456e-ab45-c7bb3c9a330c" />|
| Job location | `ui/screens/JobLocationScreen.kt` | Single job on a map with a "Get directions" deep link into Google Maps | <img width="275" height="537" alt="image" src="https://github.com/user-attachments/assets/99566277-a37b-441c-9f47-017bf42e5ff6" />|
| Application Status | `ui/screens/ChatDetailScreen.kt` | Track Application Status | <img width="406" height="904" alt="WhatsApp Image 2026-11-05 at 10 40 03 AM (1)" src="https://github.com/user-attachments/assets/8f933b20-28aa-404d-aab1-57307064d6a0" />|
| Push Notifications | `Firebase Cloud Messaging` | Get Real-Time updates on Matches, New Job Listings, and More! | <img width="738" height="1600" alt="WhatsApp Image 2026-11-05 at 10 40 03 AM" src="https://github.com/user-attachments/assets/49d015df-c453-4af4-850e-7da0119e8e34" />|
| Schedule Interview | `Feature 4 & 5` | Propose an Interview with a Date, Time, Duration, Format, and More. Check the Pipeline for tracking. | <img width="403" height="907" alt="WhatsApp Image 2026-11-05 at 10 43 08 AM" src="https://github.com/user-attachments/assets/2ee4b79b-1829-431e-aed6-e0675695c813" /> <img width="403" height="902" alt="WhatsApp Image 2026-11-05 at 10 44 34 AM" src="https://github.com/user-attachments/assets/eab6684e-bfbb-4889-98b4-a22b579883a2" />|
| Biometric Lock | `Security` | Biometric Security Feature | <img width="402" height="381" alt="WhatsApp Image 2026-11-05 at 10 44 59 AM" src="https://github.com/user-attachments/assets/98c9d2d9-0587-4b92-8704-4f885700fcf5" />|


## Main Features

The following three features were described in the Part 1 planning document and implemented in SwipeHire:

1. Profiles and CVs: Students can create and edit profiles containing their course, study year and skills, upload a PDF CV and view a profile strength score. Companies can create profiles describing their industry and hiring needs.

2. Swipe-based discovery and matching: Students browse job postings while companies browse student profiles. The app highlights overlapping skills, records each decision and creates a match when both sides express interest.

3. Chat and in-app alerts: Matched students and companies can exchange messages in a private conversation. The app also displays alerts for activity such as new matches and messages.

4. Interview Scheduler: What it does: Allows a Company to propose an interview date, time, duration, and mode (Video, Phone, In-Person). The Student can then Accept, Decline, or the Company can Cancel. If accepted, it provides an "Add to Calendar" button. Where to find it in the app: Inside a Chat/Match, in the collapsible "Application status" panel.

5. Application Pipeline Tracker: What it does: A visual timeline that tracks a candidate's progress: Matched → Interview → Offer → Hired (or Declined). It has role-based rules (e.g., only the company can move a candidate to "Offer", only the student can "Accept Offer"). It shows live updates on the Matches screen and in the Chat timeline. Where to find it in the app: The timeline inside the Chat panel, and the colored status chips on the main Matches screen list.

Feature Added After Lecturer Feedback:

Maps, geocoding and jobs near you: In response to lecturer feedback, we added location-based job discovery. The API converts a job’s work address into map coordinates. Students can view job markers on Google Maps, see jobs ordered by distance from their location and open directions to a job.


## Settings menu (Application State requirement)

Every control on the Settings screen reads from and writes to Jetpack **DataStore Preferences**
(`data/SettingsDataStore.kt`) through `viewmodel/SettingsViewModel.kt`, so changes persist across
app restarts — this is what satisfies the "Application State: Changing and saving user
preferences via a dedicated settings menu" requirement:

- **Account type** — Student / Company toggle (shared UI, drives Discover + Profile content)
- **Notifications** — push master switch, match alerts, message alerts
- **Privacy** — profile visibility toggle
- **Language** - change language of the application
- **Appearance** — Light / Dark / Match system theme, applied live via `SwipeHireTheme`
- **Security** — biometric lock toggle (UI hook — wire to `BiometricPrompt` once auth lands) *Please note that this function does not work yet. Only to be implemented in part 3*
- **Log out**


<img width="384" height="796" alt="Screenshot 2026-09-22 090826" src="https://github.com/user-attachments/assets/f37b2974-6790-4b89-8d45-eca32eaa78b2" />



<img width="386" height="786" alt="Screenshot 2026-09-22 090843" src="https://github.com/user-attachments/assets/b6982c57-92b4-4470-8e0e-c3a1e748388c" />



## Stack

- Kotlin, Jetpack Compose (Material 3), Navigation Compose
- DataStore Preferences for persisted settings
- ViewModel + StateFlow for state management
- Custom gesture-driven swipe card stack (no third-party swipe library)
- Maps Compose + Play Services Location for job locations and "Jobs near you"
- Firebase Cloud Messaging and a Firestore-triggered Cloud Function for real-time device alerts



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



## Setup for Project

### Android app

1. Install Android Studio with Android SDK 34 and JDK 17. Android Studio normally creates `local.properties` with `sdk.dir` pointing to the Android SDK.
2. The committed `app/google-services.json` is the Firebase Android configuration for `com.swipehire.app`. If you use a different Firebase project, replace it with that project's matching Android configuration file. Register the debug and release signing SHA-1 fingerprints in Firebase for Google sign-in.
3. Add these values to the gitignored `local.properties` file:

```properties
API_BASE_URL=https://<your-azure-app>.azurewebsites.net/
MAPS_API_KEY=<your-Android-restricted-Maps-key>
```

The default API URL (`http://10.0.2.2:5000/`) reaches a locally running API from an Android emulator. Use the Azure HTTPS URL for a physical phone. Restrict the Android Maps key by package name and signing certificate.

### API

The API targets .NET 10. Set `Firebase__ProjectId` and `GoogleMaps__ApiKey` in your local environment or Azure App Service settings. Provide Firestore credentials using `GOOGLE_APPLICATION_CREDENTIALS` (a local service account JSON file path) or `GoogleCredentials__Json` (the JSON content). Keep service account credentials and Maps server keys out of the repository.

### Run the checks locally

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug
dotnet test SwipeHire.Api/SwipeHire.Api.slnx --configuration Release
npm.cmd --prefix functions ci
npm.cmd --prefix functions run check
npm.cmd --prefix functions test
```

The same Android, API, and Cloud Functions checks run in GitHub Actions.

## Project Startup Instructions APK
Download the APK to a mobile device for the fully functional mobile app. If trying to run in Android Studio, the project will work, except for the interactive maps
as the API Key is not included in GitHub. Hence, we suggest downloading the APK onto a mobile device for a better experience. 
To Download the APK, simply go to the Tags of this repository.

Demo login details (Student):
- Email - lerato.student@swipehire.example.com
- Password - StudentDemo!2026

Demo login details (Company):
- Email - company.demo@swipehire.example.com
- Password - CompanyDemo!2026


## Playstore Publication Readiness

### A Signed APK has been included in the GitHub Releases:
<img width="385" height="142" alt="image" src="https://github.com/user-attachments/assets/f39f3f91-6293-411c-9f2c-191c60941962" />


### Promotional Screenshots: 
