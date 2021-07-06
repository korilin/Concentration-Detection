package com.korilin.concentration_detection.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.icu.text.SimpleDateFormat
import android.util.Log
import java.util.*

const val databaseName = "Concentration.DB"
const val databaseVersion = 20210705

class ConcentrationSQLiteHelper(val context: Context) :
    SQLiteOpenHelper(context, databaseName, null, databaseVersion) {

    private val recordTableName = "Record"

    private val createSQL = """
        create table $recordTableName(
            id integer primary key autoincrement,
            time text,
            duration integer
        )
    """.trimIndent()

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createSQL)
        Log.i("SQLite $databaseName", "Create table ConcentrationRecord:")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // 老爷保好
    }

    fun insertRecord(duration: Int) =
        writableDatabase.insert(recordTableName, null, ContentValues().apply {
            put("time", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))
            put("duration", duration)
        })
}