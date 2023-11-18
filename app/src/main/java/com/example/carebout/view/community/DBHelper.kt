package com.example.carebout.view.community

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "testdb", null, 2) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "create table TODO_TB(" +
                    "_id integer primary key autoincrement," +
                    "content text not null," +
                    "date text not null," +
                    "day text not null," +
                    "image_uri text)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE TODO_TB ADD COLUMN image_uri TEXT")
        }
    }
}