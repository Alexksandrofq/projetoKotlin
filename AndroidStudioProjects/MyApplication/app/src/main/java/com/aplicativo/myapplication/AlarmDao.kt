// AlarmDao.kt
package com.aplicativo.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AlarmDao(context: Context) : SQLiteOpenHelper(context, "alarms.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE alarms (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "hour INTEGER, " +
                    "minute INTEGER)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS alarms")
        onCreate(db)
    }

    fun insert(alarm: Alarm): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("hour", alarm.hour)
            put("minute", alarm.minute)
        }
        return db.insert("alarms", null, values)
    }

    fun getAll(): List<Alarm> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM alarms", null)
        val list = mutableListOf<Alarm>()
        while (cursor.moveToNext()) {
            val alarm = Alarm(
                id = cursor.getInt(0),
                hour = cursor.getInt(1),
                minute = cursor.getInt(2)
            )
            list.add(alarm)
        }
        cursor.close()
        return list
    }

    fun update(alarm: Alarm): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("hour", alarm.hour)
            put("minute", alarm.minute)
        }
        return db.update("alarms", values, "id=?", arrayOf(alarm.id.toString()))
    }

    fun delete(id: Int): Int {
        val db = writableDatabase
        return db.delete("alarms", "id=?", arrayOf(id.toString()))
    }
}
