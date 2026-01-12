# Notes SQLite (Android)

A simple Notes app built with **Kotlin**, **RecyclerView**, and **SQLite** (via `SQLiteOpenHelper`).

## Features

- Create, edit, and delete notes
- **Search** notes by title/content
- Notes are automatically **sorted by most recently updated**
- **Timestamps** stored in the database (`created_at`, `updated_at`)
- **Delete confirmation** dialog
- **Copy / Share** a note (long-press a note)
- Empty state message when there are no notes

## Project structure

- `app/src/main/java/com/example/notessqlite/`
  - `MainActivity.kt`: list + search
  - `AddNoteActivity.kt`: create note
  - `UpdateNoteActivity.kt`: edit note
  - `NotesAdapter.kt`: RecyclerView adapter (delete confirm + copy/share)
  - `NotesDatabaseHelper.kt`: SQLite schema + CRUD
  - `Note.kt`: model

## Setup (Android Studio)

1. Open the project in **Android Studio**.
2. Make sure the **Android SDK** is installed.
3. If needed, set SDK path:
   - Android Studio usually creates `local.properties` automatically.
   - Or add it manually:

```properties
sdk.dir=/absolute/path/to/Android/Sdk
```

4. Sync Gradle and run the `app` configuration on an emulator/device.

## Notes about the database

- Database name: `notesapp.db`
- Table: `allnotes`
- Columns:
  - `id` (INTEGER PRIMARY KEY)
  - `title` (TEXT)
  - `content` (TEXT)
  - `created_at` (INTEGER, epoch millis)
  - `updated_at` (INTEGER, epoch millis)

On upgrade from v1 → v2, the app **keeps existing notes** and backfills timestamps.

