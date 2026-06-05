# Pigeon Post

## Overview

Pigeon Post is an Android note-taking application developed in Java that allows users to quickly create, organize, search, and manage notes. Notes can be created through manual text entry or Android speech-to-text functionality. All note data is stored locally using the Room persistence library, allowing users to access their information without requiring an internet connection.

The application includes note categorization, search and filtering capabilities, private note protection through PIN authentication, recovery email management, and reporting functionality. Pigeon Post was developed as part of a Software Engineering Capstone project and demonstrates Android development concepts including Room databases, RecyclerViews, Material Design components, SpeechRecognizer integration, data validation, and local data persistence.

---

## Features

### Note Creation

* Create notes through manual text entry
* Create notes using speech-to-text
* Assign notes to predefined categories
* Store notes locally using Room Database

### Note Management

* View recently created notes
* Edit existing notes
* Search notes by keyword
* Filter notes by category
* Filter notes by date range

### Security Features

* Mark notes as private
* Protect private notes using a four-digit PIN
* Store PIN values as hashed data
* Configure and update a recovery email address

### Reporting

Generate application statistics including:

* Total number of notes
* Number of notes per category
* Most frequently used category
* Oldest note date
* Most recent note date
* Average note length

### User Experience

* Material Design interface
* Portrait and landscape layouts
* RecyclerView note displays
* Responsive search and filtering

---

## Technology Stack

* Java
* Android SDK
* Android Studio
* Room Persistence Library
* SQLite (via Room)
* RecyclerView
* Material Design Components
* SpeechRecognizer API

---

## Android Deployment Information

| Item                    | Value                |
| ----------------------- | -------------------- |
| Minimum Android Version | Android 8.0 (API 26) |
| Target Android Version  | Android 16 (API 36)  |
| Programming Language    | Java                 |
| Database                | Room (SQLite)        |

---

## Installation

1. Download the Pigeon Post APK file.
2. Transfer the APK file to an Android device if necessary.
3. Open the APK file.
4. If prompted, allow installation from unknown sources.
5. Select **Install**.
6. Wait for installation to complete.
7. Select **Open** to launch the application.

---

## Application Usage

### Creating a Note

1. Open the application.
2. Select a category from the Category dropdown menu.
3. Enter note content into the Note field.
4. Optionally enable the **Private Note** checkbox.
5. Select **Save Note**.
6. The note will be stored and displayed in the Recent Notes section.

### Creating a Note with Speech-to-Text

1. Open the application.
2. Select **Record Note**.
3. Grant microphone permission if prompted.
4. Speak the desired note content.
5. Review the generated text.
6. Select **Save Note**.

### Creating a Security Profile

A security profile is required when creating the first private note.

1. Enable **Private Note**.
2. Select **Save Note**.
3. Enter a valid recovery email address.
4. Enter a four-digit PIN.
5. Select **Save**.

### Viewing Private Notes

1. Select a private note.
2. Enter the configured four-digit PIN.
3. Select **Unlock**.
4. The note contents will be displayed.

### Searching Notes

1. Select **Search Notes**.
2. Enter a keyword.
3. Optionally select a category filter.
4. Optionally select a start date and end date.
5. Search results update automatically.

### Editing Notes

1. Open the Search screen.
2. Select a note.
3. Select **Edit**.
4. Modify the category, content, or privacy setting.
5. Select **Save**.

### Managing Recovery Email

1. Open the Profile screen.
2. Review the currently stored recovery email.
3. Select **Update Email**.
4. Enter a new email address.
5. Select **Save**.

### Running Reports

1. Open the Search screen.
2. Select **Run Report**.
3. Review the generated statistics.

The report displays:

* Total notes
* Notes per category
* Most used category
* Oldest note date
* Newest note date
* Average note length

---

## Project Structure

```text
com.wgu.d424
│
├── adapters
│   └── RecentNotesAdapter.java
│
├── data
│   ├── dao
│   │   ├── NoteDao.java
│   │   └── ProfileDao.java
│   │
│   ├── db
│   │   └── AppDatabase.java
│   │
│   └── entities
│       ├── Note.java
│       ├── Profile.java
│       └── CategoryCount.java
│
├── ui
│   ├── MainActivity.java
│   ├── SearchActivity.java
│   ├── ProfileActivity.java
│   └── ReportActivity.java
│
└── utils
    └── SecurityUtils.java
```

---

## Testing

The application was tested using:

* Android Emulator
* Physical Android device
* Functional testing
* Validation testing
* Unit testing
* Portrait and landscape layout testing

Features tested include:

* Note creation
* Speech-to-text functionality
* Database operations
* Search functionality
* Category filtering
* Date filtering
* Private note security
* Recovery email validation
* Report generation

---

## Future Enhancements

Potential future enhancements include:

* Cloud synchronization
* Data export functionality
* Backup and restore capabilities
* Additional reporting options
* Custom user-defined categories
* Rich text formatting
* Multi-device synchronization

---

## Repository

Git Repository:

[Insert Repository URL Here]

---

## Author

John Matrix

Software Engineering Capstone Project

Western Governors University

---

## License

This project was developed for educational purposes as part of the WGU Software Engineering Capstone.
