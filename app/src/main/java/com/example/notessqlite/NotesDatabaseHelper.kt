package com.example.notessqlite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class NotesDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    companion object{
        private const val DATABASE_NAME = "notesapp.db"
        private const val DATABASE_VERSION = 2
        private const val TABLE_NAME = "allnotes"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_CONTENT = "content"
        private const val COLUMN_CREATED_AT = "created_at"
        private const val COLUMN_UPDATED_AT = "updated_at"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery =
            "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_TITLE TEXT NOT NULL, " +
                "$COLUMN_CONTENT TEXT, " +
                "$COLUMN_CREATED_AT INTEGER NOT NULL, " +
                "$COLUMN_UPDATED_AT INTEGER NOT NULL" +
            ")"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (db == null) return

        // v1 -> v2: add timestamps without losing existing notes.
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_CREATED_AT INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_UPDATED_AT INTEGER NOT NULL DEFAULT 0")

            val now = System.currentTimeMillis()
            val values = ContentValues().apply {
                put(COLUMN_CREATED_AT, now)
                put(COLUMN_UPDATED_AT, now)
            }
            db.update(
                TABLE_NAME,
                values,
                "$COLUMN_CREATED_AT = ? OR $COLUMN_UPDATED_AT = ?",
                arrayOf("0", "0"),
            )
        }
    }

    fun insertNote(note: Note){
        val db = writableDatabase
        val now = System.currentTimeMillis()
        val values = ContentValues().apply {
            put(COLUMN_TITLE, note.title)
            put(COLUMN_CONTENT, note.content)
            put(COLUMN_CREATED_AT, now)
            put(COLUMN_UPDATED_AT, now)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getAllNotes(searchQuery: String? = null): List<Note> {
        val notesList = mutableListOf<Note>()
        val db = readableDatabase
        val orderBy = "$COLUMN_UPDATED_AT DESC, $COLUMN_ID DESC"

        val trimmedQuery = searchQuery?.trim().orEmpty()
        val (selection, selectionArgs) = if (trimmedQuery.isNotEmpty()) {
            val like = "%$trimmedQuery%"
            ("$COLUMN_TITLE LIKE ? OR $COLUMN_CONTENT LIKE ?" to arrayOf(like, like))
        } else {
            (null to null)
        }

        val cursor = db.query(
            TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            orderBy,
        )

        while (cursor.moveToNext()){
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
            val createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT))
            val updatedAt = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_UPDATED_AT))

            val note = Note(id, title, content, createdAt, updatedAt)
            notesList.add(note)
        }
        cursor.close()
        db.close()
        return notesList
    }

    fun updateNote(note: Note){
        val db = writableDatabase
        val now = System.currentTimeMillis()
        val values = ContentValues().apply {
            put(COLUMN_TITLE, note.title)
            put(COLUMN_CONTENT, note.content)
            put(COLUMN_UPDATED_AT, now)
        }
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(note.id.toString())
        db.update(TABLE_NAME, values, whereClause, whereArgs)
        db.close()
    }

    fun getNoteByID(noteId: Int): Note?{
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null,
            "$COLUMN_ID = ?",
            arrayOf(noteId.toString()),
            null,
            null,
            null,
            "1",
        )

        val note = if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
            val createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT))
            val updatedAt = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_UPDATED_AT))

            Note(id, title, content, createdAt, updatedAt)
        } else {
            null
        }

        cursor.close()
        db.close()
        return note

    }
    fun deleteNote(noteId: Int){
        val db = writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(noteId.toString())
        db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()
    }

}