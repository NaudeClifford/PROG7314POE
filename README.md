# SwipeHire — Android Frontend (Part 2 Prototype)

Native Kotlin + Jetpack Compose frontend for **SwipeHire**, the reverse job-matching app from the
PROG7314 Part 1 design where companies swipe on student profiles, students swipe on job postings,
mutual interest opens a chat that could possibly link them to their next job/candidate.

## YouTube Video Link
 https://www.youtube.com/watch?v=MQEapbOJT-U

## Classroom50 Repository Link
https://github.com/EMGPRS/prog7314-2026-poe-part-2-group-naudeclifford.git

## Public Repository Link
https://github.com/VanNiekerkJoe/SwipeHire.git




## Screens

| Screen | File | Notes |
|---|---|---|
| Login | `ui/screens/LoginScreen.kt` | Google SSO + biometric sign-in entry points (stubbed) |
| Role select | `ui/screens/RoleSelectScreen.kt` | Choose Student / Company — same screen serves both |
| Discover | `ui/screens/DiscoverScreen.kt` | Draggable swipe-card stack (`ui/components/SwipeCardStack.kt`) |
| Matches | `ui/screens/MatchesScreen.kt` | List of mutual matches with unread indicator |
| Chat | `ui/screens/ChatDetailScreen.kt` | Per-match conversation, local send |
| Profile | `ui/screens/ProfileScreen.kt` | Adapts content to Student vs Company account type |
| **Settings** | `ui/screens/SettingsScreen.kt` | **20-mark deliverable — see below** |
| Jobs near you | `ui/screens/NearbyJobsScreen.kt` | Google Map + distance-sorted list, from Discover's "Near you" button (student view) |
| Job location | `ui/screens/JobLocationScreen.kt` | Single job on a map with a "Get directions" deep link into Google Maps |

## Settings menu (Application State requirement)

Every control on the Settings screen reads from and writes to Jetpack **DataStore Preferences**
(`data/SettingsDataStore.kt`) through `viewmodel/SettingsViewModel.kt`, so changes persist across
app restarts — this is what satisfies the "Application State: Changing and saving user
preferences via a dedicated settings menu" requirement:

- **Account type** — Student / Company toggle (shared UI, drives Discover + Profile content)
- **Notifications** — push master switch, match alerts, message alerts
- **Privacy** — profile visibility toggle
- **Appearance** — Light / Dark / Match system theme, applied live via `SwipeHireTheme`
- **Security** — biometric lock toggle (UI hook — wire to `BiometricPrompt` once auth lands)
- **Log out**

## Google Maps setup (required to run)


## Stack

- Kotlin, Jetpack Compose (Material 3), Navigation Compose
- DataStore Preferences for persisted settings
- ViewModel + StateFlow for state management
- Custom gesture-driven swipe card stack (no third-party swipe library)
- Maps Compose + Play Services Location for job locations and "Jobs near you"


## REST API Endpoints
| Method | Endpoint | Description |
|---|---|---|
| **GET** | `/api/jobs` | Fetches the full list of job postings to populate the student swipe feed and map views |
| **POST** | `/api/jobs` | Creates a new job posting for hiring companies |
| **DELETE** | `/api/jobs/{id}` | Deletes an existing job posting by its unique ID |
| **GET** | `/api/companies/{companyId}/jobs` | List a company’s jobs |
| **POST** | `/api/companies` | Registers a new company profile |
| **PUT** | `/api/companies/{id}` | Updates company details, location, and industry information |
| **GET** | `/api/students` | Fetches student profiles to populate the company candidate swipe feed |
| **POST** | `/api/students` | Registers a new student profile during initial onboarding |
| **PUT** | `/api/students/{id}` | Updates an existing student's profile information, skills, and blurb |
| **GET** | `/api/swipes/{userId}` | Get previously swiped IDs |
| **POST** | `/api/swipes` | Records a swipe action between users/jobs and determines if a mutual match occurred |
| **DELETE** | `/api/swipes` | Undo a swipe; takes `userId`, `targetId`, and `targetUserId` as query parameters |
| **GET** | `/api/users/{userId}/saved` | Get saved jobs and students |
| **PUT** | `/api/users/{userId}/saved/{kind}/{itemId}` | Save or unsave an item; `kind` is `job` or `student` |
| **GET** | `/api/users/{userId}/settings` | Get account settings |
| **PUT** | `/api/users/{userId}/settings` | Update account settings |
| **POST** | `/api/location/geocode` | Convert an address to coordinates |

## GitHub Workflows (proof that they worked)
<img width="1880" height="762" alt="image" src="https://github.com/user-attachments/assets/3e4491ba-aab5-49aa-9ac8-805200c05e46" />

Android Unit Tests: 
<img width="1845" height="756" alt="image" src="https://github.com/user-attachments/assets/c1ab5424-051d-4740-a673-580aeed55f75" />

.NET API Build:
<img width="1871" height="788" alt="image" src="https://github.com/user-attachments/assets/891d722a-f07c-4107-abc0-b0e1c0cad655" />


