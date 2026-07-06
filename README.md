# FindIt Campus

**Campus Lost & Found Item Reporting Application**

Kigali Independent University ULK — School of Science and Technology
Module: Mobile Application Development · Lecturer: NTIRENGANYA Jean Paul
FAT Project, Academic Year 2025–2026

---

## App Purpose

Campus notice boards and manual admin-office logs make it hard to reunite lost
items with their owners. **FindIt Campus** replaces that process with a simple
Android app where students and staff can report a lost item, report a found
item, browse and search all open reports, and view Lost & Found office
information — all stored locally on the device.

## Main Features

- **Dashboard** — live counts of lost vs. found reports, quick-access buttons
  to every feature.
- **Report a Lost Item** — captures name, category, description, last-seen
  location, date, and contact details, with required-field validation.
- **Report a Found Item** — captures name, category, description, found
  location, date, and where it was handed in/stored, with required-field
  validation.
- **All Reports** — searchable, filterable (All / Lost / Found) list of every
  report with status shown at a glance.
- **Item Detail** — full report details, one-tap status updates (Claimed /
  Returned / Resolved), and delete with confirmation.
- **Lost & Found Office Info** — office location, opening hours, contact, and
  collection instructions.

## Tech Stack

| Layer          | Technology                                   |
|----------------|-----------------------------------------------|
| Language       | Kotlin                                         |
| UI             | XML layouts, Material Components, ViewBinding  |
| Local storage  | Room database (SQLite)                         |
| Architecture   | Repository pattern + LiveData                  |
| Async          | Kotlin Coroutines                              |
| Build          | Gradle (Kotlin DSL), KSP for Room annotation processing |

## Project Structure

```
app/src/main/java/com/ulk/findcampus/
├── MainActivity.kt                  # Dashboard
├── LostItemReportActivity.kt        # Lost item form + validation
├── FoundItemReportActivity.kt       # Found item form + validation
├── ReportsListActivity.kt           # Search, filter, list
├── ItemDetailActivity.kt            # Details, status update, delete
├── OfficeInfoActivity.kt            # Static office info
├── adapter/
│   └── ReportsAdapter.kt            # RecyclerView adapter
└── data/
    ├── ItemReport.kt                # Room entity
    ├── ItemReportDao.kt             # Room DAO (CRUD, search, filter, counts)
    ├── AppDatabase.kt               # Room database singleton
    └── ReportRepository.kt          # Repository layer
```

## Setup / Build Instructions

1. Clone the repository:
   ```
   git clone <repository-url>
   ```
2. Open the project folder in **Android Studio** (Koala or newer recommended).
3. Let Gradle sync automatically (downloads dependencies on first sync).
4. Run on an emulator or physical device with **API 24 (Android 7.0)** or higher.
5. To generate release build artifacts:
   ```
   ./gradlew assembleRelease   # produces app-release.apk
   ./gradlew bundleRelease     # produces app-release.aab
   ```
   Output files are located in `app/build/outputs/apk/release/` and
   `app/build/outputs/bundle/release/`.

## Data Management

All reports are stored locally using a **Room** database (table
`item_reports`). No internet connection or backend server is required — this
matches the FAT project's requirement to manage reports in a realistic,
structured way without a live institutional server.

## Screenshots

*(Add screenshots here after running the app: Dashboard, Lost Item Report,
Found Item Report, Reports List, Item Detail, Search/Filter result.)*

## APK / AAB Release

*(Add a link to the release `.apk` and `.aab`, or note their location in the
repository, once built.)*

## Testing Evidence

| # | Test Area        | Test Case                              | Expected Result                                  |
|---|-------------------|-----------------------------------------|---------------------------------------------------|
| 1 | Lost item form    | Submit without item name                | Validation error shown, form not submitted        |
| 2 | Found item form   | Submit without found/hand-in location   | Validation error shown, form not submitted        |
| 3 | Report saving     | Save valid lost item report             | Report appears in list tagged "Lost"               |
| 4 | Report saving     | Save valid found item report            | Report appears in list tagged "Found"              |
| 5 | Search            | Search using an item keyword            | Only matching reports are displayed               |
| 6 | Filter            | Filter by Lost                          | Only lost reports are displayed                    |
| 7 | Status update     | Mark item as Claimed/Returned/Resolved  | Status updates in both detail screen and list      |
| 8 | Delete report     | Delete a selected report                | Report is removed from the list immediately        |

## Development Reflection

FindIt Campus was built to reflect a realistic, low-friction workflow for a
campus lost-and-found service: report quickly, browse easily, and resolve
items with a clear audit trail of status changes — all without depending on
a live server, so it works reliably offline.

## Author

Xavier — Year 2 Software Engineering, ULK Gisenyi Campus
