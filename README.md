# PigeonPost Android Client

> **A production-ready Android client for an AI-powered personal knowledge assistant.**

PigeonPost is a native Android application built in Java that allows users to capture, organize, and intelligently retrieve personal knowledge through text and voice notes. The application communicates with a self-hosted Spring Boot backend secured with JWT authentication and leverages Retrieval-Augmented Generation (RAG) to answer natural language questions using the user's own notes.

This repository contains the **Android frontend**. The backend API, vector database, and AI services are maintained in a separate private repository.

---

## Features

### AI Knowledge Assistant

* Ask natural language questions about your notes
* AI-generated responses powered by Retrieval-Augmented Generation (RAG)
* Semantic search using vector embeddings
* Displays supporting source notes with relevance indicators
* View embedding similarity information for AI-generated answers

### Note Creation

* Create notes manually
* Create notes using Android Speech-to-Text
* Assign notes to color-coded categories
* Mark notes as private
* Automatic synchronization with the backend API

### Note Management

* View recent notes
* Edit existing notes
* Delete notes
* Search by keyword
* Filter by category
* Filter by date range

### Authentication & Security

* JWT authentication
* Refresh token support
* Secure user login
* User-specific note isolation
* Local Room cache for offline-friendly performance

### User Experience

* Material Design 3 interface
* Responsive layouts
* Light and Dark Mode support
* RecyclerView-based note browsing
* Voice input integration

---

# Architecture

```text
Android App (Java)
        │
        │ Retrofit
        ▼
Spring Boot REST API
        │
        ├── PostgreSQL
        ├── pgvector
        └── Ollama (LLM)
```

The Android application serves as a lightweight client responsible for:

* Authentication
* Note management
* Voice input
* Local caching
* Displaying AI responses

AI inference, semantic search, embeddings, and data persistence are handled by the backend.

---

# Technology Stack

### Android

* Java
* Android SDK
* Android Studio
* Material Design 3
* RecyclerView
* Retrofit
* Room Persistence Library
* SpeechRecognizer API

### Backend Integration

* Spring Boot REST API
* JWT Authentication
* PostgreSQL
* pgvector
* Docker

---

# Android Deployment

| Item                    | Value                |
| ----------------------- | -------------------- |
| Minimum Android Version | Android 8.0 (API 26) |
| Target Android Version  | Android 16 (API 36)  |
| Language                | Java                 |
| Networking              | Retrofit             |
| Local Database          | Room (SQLite Cache)  |

---

# Screens

The application currently includes:

* Login
* Home Dashboard
* AI Assistant
* Search
* Profile
* Reports

---

# Installation

1. Download the latest signed APK.
2. Transfer the APK to an Android device if necessary.
3. Install the application.
4. Sign in using your PigeonPost account.
5. Begin creating notes and interacting with the AI assistant.

---

# AI Workflow

```text
User asks a question
        │
        ▼
Android App
        │
        ▼
Spring Boot API
        │
        ▼
Semantic Search (pgvector)
        │
        ▼
Relevant Notes Retrieved
        │
        ▼
LLM Generates Response
        │
        ▼
Answer + Source Notes Returned
```

Each AI response includes the supporting notes used during generation, allowing users to understand why the model produced its answer.

---

# Project Structure

```text
com.pigeonpost.android
│
├── adapters
├── data
│   ├── dao
│   ├── db
│   ├── entities
│   ├── remote
│   └── repository
│
├── ui
│
├── security
│
├── utils
│
└── services
```

---

# Screenshots

The portfolio website contains screenshots, architecture diagrams, and demonstration videos of the application.

---

# Backend

The backend repository is private.

It includes:

* Spring Boot
* Spring Security
* JWT Authentication
* PostgreSQL
* pgvector
* Ollama
* Docker deployment
* Cloudflare Tunnel
* Retrieval-Augmented Generation (RAG)

---

# Testing

The application has been validated using:

* Android Emulator
* Physical Android devices
* Functional testing
* Authentication testing
* REST API integration testing
* AI workflow testing
* Voice input testing
* CRUD testing

---

# Future Enhancements

Planned improvements include:

* Conversation memory
* Streaming AI responses
* AI-generated note summaries
* User-defined categories
* Image attachments
* Multi-device synchronization
* Push notifications
* End-to-end encryption for private notes

---

# Portfolio

Portfolio

https://artresume.web.app

---

# Author

**Arthur Modyman IV**

Bachelor of Science — Software Engineering

Western Governors University

---

# License

This repository contains the Android client for PigeonPost and is intended for portfolio and educational demonstration purposes. The backend implementation remains private.

