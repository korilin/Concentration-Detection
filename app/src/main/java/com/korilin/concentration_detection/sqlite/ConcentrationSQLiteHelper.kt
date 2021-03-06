 package com.korilin.concentration_detection.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.icu.text.SimpleDateFormat
import android.util.Log
import java.util.*

const val databaseName = "Concentration.DB"
const val databaseVersion = 20210707

class ConcentrationSQLiteHelper(val context: Context) :
    SQLiteOpenHelper(context, databaseName, null, databaseVersion) {

    private val recordTableName = "Record"

    private val createSQL = """
        create table $recordTableName(
            id integer primary key autoincrement,
            time text,
            duration integer,
            unLockCount integer
        )
    """.trimIndent()

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createSQL)
        Log.i("SQLite $databaseName", "Create table ConcentrationRecord:")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("drop table if exists $recordTableName")
        onCreate(db)
    }

    fun insertRecord(duration: Int, unLockCount: Int) =
        writableDatabase.insert(recordTableName, null, ContentValues().apply {
            put("time", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))
            put("duration", duration)
            put("unLockCount", unLockCount)
        })

    fun selectRecords(): List<Record> {
        val cursor = readableDatabase.rawQuery("select * from $recordTableName", null)
        val mutableListOf = mutableListOf<Record>()
        if (cursor.moveToFirst()) {
            do {
                Record(
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("time")),
                    cursor.getInt(cursor.getColumnIndex("duration")),
                    cursor.getInt(cursor.getColumnIndex("unLockCount")),
                ).also {
                    mutableListOf.add(it)
                }

            } while (cursor.moveToNext())
        }
        cursor.close()
        return mutableListOf.apply { reverse() }
    }
}