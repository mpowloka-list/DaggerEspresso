package com.example.mpowloka.dagger_espresso.persistance

import android.content.ContentValues
import android.content.Context
import android.content.res.Resources
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.mpowloka.dagger_espresso.annotations.ApplicationContext
import com.example.mpowloka.dagger_espresso.annotations.DatabaseInfo
import com.example.mpowloka.dagger_espresso.pojo.User
import java.sql.SQLException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton class DbHelper @Inject constructor(
        @ApplicationContext context: Context, @DatabaseInfo dbName: String, @DatabaseInfo version: Int) : SQLiteOpenHelper(context, dbName, null, version) {



    override fun onCreate(db: SQLiteDatabase?) = tableCreateStatements(db)

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $USER_TABLE_NAME")
        onCreate(db)
    }

    private fun tableCreateStatements(db: SQLiteDatabase?) {

        fun getCurrentTimeStamp() = (System.currentTimeMillis() / 1000).toString()

        try {
            db?.execSQL(
                    """CREATE TABLE IF NOT EXISTS $USER_TABLE_NAME (
                            $USER_COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                            $USER_COLUMN_USER_NAME VARCHAR(20),
                            $USER_COLUMN_USER_ADDRESS VARCHAR(50),
                            $USER_COLUMN_USER_CREATED_AT VARCHAR(10) DEFAULT ${getCurrentTimeStamp()},
                            $USER_COLUMN_USER_UPDATED_AT VARCHAR(10) DEFAULT ${getCurrentTimeStamp()})
                        """.trimMargin()
            )
        }
        catch (ex: SQLException) {
            Log.e(TAG, "Cannot create Users table")
        }
    }

    private fun getUser(userId: Long): User {
        val db = this.readableDatabase
        var user: User
        db.use {
            val cursor = it.rawQuery("""
            SELECT * FROM $USER_TABLE_NAME
            WHERE $USER_COLUMN_USER_ID = ?
            """.trimIndent(), arrayOf(userId.toString()))

            if(cursor.count > 0) {
                cursor.moveToFirst()
                user = User(
                        cursor.getLong(cursor.getColumnIndex(USER_COLUMN_USER_ID)),
                        cursor.getString(cursor.getColumnIndex(USER_COLUMN_USER_NAME)),
                        cursor.getString(cursor.getColumnIndex(USER_COLUMN_USER_ADDRESS)),
                        cursor.getString(cursor.getColumnIndex(USER_COLUMN_USER_CREATED_AT)),
                        cursor.getString(cursor.getColumnIndex(USER_COLUMN_USER_UPDATED_AT))
                )
                cursor.close()
                return user
            }
            else throw Resources.NotFoundException("User with id $userId does not exist")
        }
    }

    private fun insertUser(user: User): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(USER_COLUMN_USER_NAME, user.name)
        values.put(USER_COLUMN_USER_ADDRESS, user.address)

        return db.insert(USER_TABLE_NAME, null, values)
    }
}

private val USER_COLUMN_USER_UPDATED_AT = "updated_at"
private val TAG = "DbHelper"
private val USER_TABLE_NAME = "users"
private val USER_COLUMN_USER_ID = "id"
private val USER_COLUMN_USER_NAME = "usr_name"
private val USER_COLUMN_USER_ADDRESS = "usr_add"
private val USER_COLUMN_USER_CREATED_AT = "created_at"
