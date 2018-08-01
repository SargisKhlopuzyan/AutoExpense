package com.example.sargiskh.autoexpense.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.sargiskh.autoexpense.ExpenseModel
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Subscriber
import retrofit2.http.Query
import java.util.concurrent.Callable


class ExpenseDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    // Method called when Database is created.
    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(SQL_CREATE_ENTRIES)
    }

    // Method called when Database is upgraded.
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }


    @Throws(SQLiteConstraintException::class)
    fun insertExpenseModel(expenseModel: ExpenseModel): Long {

        // Gets the data repository in write mode
        val db = writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
//        values.put(DBContract.ExpenseModelEntry.COLUMN_ID, expenseModel.id)
        values.put(DBContract.ExpenseModelEntry.COLUMN_SUM, expenseModel.sum)
        values.put(DBContract.ExpenseModelEntry.COLUMN_SUM_TYPE, expenseModel.sumType)
        values.put(DBContract.ExpenseModelEntry.COLUMN_CAPACITY, expenseModel.capacity)
        values.put(DBContract.ExpenseModelEntry.COLUMN_CAPACITY_TYPE, expenseModel.capacityType)
        values.put(DBContract.ExpenseModelEntry.COLUMN_DISTANCE, expenseModel.distance)
        values.put(DBContract.ExpenseModelEntry.COLUMN_DISTANCE_TYPE, expenseModel.distanceType)
        values.put(DBContract.ExpenseModelEntry.COLUMN_PRICE, expenseModel.price)
        values.put(DBContract.ExpenseModelEntry.COLUMN_PRICE_TYPE, expenseModel.priceType)
        values.put(DBContract.ExpenseModelEntry.COLUMN_DATE, expenseModel.date)

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db.insert(DBContract.ExpenseModelEntry.TABLE_NAME, null, values)
        db.close()

        Log.e("LOG_TAG", "newRowId: ${newRowId}")

        return newRowId
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteExpense(id: Int): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase
        // Define 'where' part of query.
        val selection = DBContract.ExpenseModelEntry.COLUMN_ID + " LIKE ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf("${id}")
        // Issue SQL statement.
        db.delete(DBContract.ExpenseModelEntry.TABLE_NAME, selection, selectionArgs)

        return true
    }

    fun readExpense(id: Int): ArrayList<ExpenseModel> {

        val users = ArrayList<ExpenseModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.ExpenseModelEntry.TABLE_NAME + " WHERE " + DBContract.ExpenseModelEntry.COLUMN_ID + "='" + id + "'", null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        var sum : Int
        var sumType : String
        var capacity : Float
        var capacityType : String
        var distance : Int
        var distanceType : String
        var price : Int
        var priceType : String
        var date : String

        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                sum = cursor.getInt(cursor.getColumnIndex(DBContract.ExpenseModelEntry.COLUMN_ID))
                sumType = cursor.getString(cursor.getColumnIndex(DBContract.ExpenseModelEntry.COLUMN_SUM_TYPE))
                capacity = cursor.getFloat(cursor.getColumnIndex(DBContract.ExpenseModelEntry.COLUMN_CAPACITY))
                capacityType = cursor.getString(cursor.getColumnIndex(DBContract.ExpenseModelEntry.COLUMN_CAPACITY_TYPE))
                distance = cursor.getInt(cursor.getColumnIndex(DBContract.ExpenseModelEntry.COLUMN_DISTANCE))
                distanceType = cursor.getString(cursor.getColumnIndex(DBContract.ExpenseModelEntry.COLUMN_DISTANCE_TYPE))
                price = cursor.getInt(cursor.getColumnIndex(DBContract.ExpenseModelEntry.COLUMN_PRICE))
                priceType = cursor.getString(cursor.getColumnIndex(DBContract.ExpenseModelEntry.COLUMN_PRICE_TYPE))
                date = cursor.getString(cursor.getColumnIndex(DBContract.ExpenseModelEntry.COLUMN_DATE))

                users.add(ExpenseModel(id, sum, sumType, capacity, capacityType, distance, distanceType, price, priceType, date))
                cursor.moveToNext()
            }
        }
        return users
    }

    //
    @Throws(SQLiteConstraintException::class)
    fun updateExpenseModel(expenseModel: ExpenseModel): Boolean {

        // Gets the data repository in write mode
        val db = writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
//        values.put(DBContract.ExpenseModelEntry.COLUMN_ID, expenseModel.id)
        values.put(DBContract.ExpenseModelEntry.COLUMN_SUM, expenseModel.sum)
        values.put(DBContract.ExpenseModelEntry.COLUMN_SUM_TYPE, expenseModel.sumType)
        values.put(DBContract.ExpenseModelEntry.COLUMN_CAPACITY, expenseModel.capacity)
        values.put(DBContract.ExpenseModelEntry.COLUMN_CAPACITY_TYPE, expenseModel.capacityType)
        values.put(DBContract.ExpenseModelEntry.COLUMN_DISTANCE, expenseModel.distance)
        values.put(DBContract.ExpenseModelEntry.COLUMN_DISTANCE_TYPE, expenseModel.distanceType)
        values.put(DBContract.ExpenseModelEntry.COLUMN_PRICE, expenseModel.price)
        values.put(DBContract.ExpenseModelEntry.COLUMN_PRICE_TYPE, expenseModel.priceType)
        values.put(DBContract.ExpenseModelEntry.COLUMN_DATE, expenseModel.date)

        Log.e("LOG_TAG", "expenseModel.id: ${expenseModel.id.toString()}")

        // Insert the new row, returning the primary key value of the new row
        val updatedRowId = db.update(DBContract.ExpenseModelEntry.TABLE_NAME, values, "Id=?", arrayOf(expenseModel.id.toString()))
        db.close()

        Log.e("LOG_TAG", "updatedRowId: ${updatedRowId}")

        return true
    }

    fun readAllExpenses(): ArrayList<ExpenseModel> {

        val users = ArrayList<ExpenseModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.ExpenseModelEntry.TABLE_NAME, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        var userid: Int
        var sum : Int
        var sumType : String
        var capacity : Float
        var capacityType : String
        var distance : Int
        var distanceType : String
        var price : Int
        var priceType : String
        var date : String

        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {

                userid = cursor.getInt(cursor.getColumnIndex(DBContract.ExpenseModelEntry.COLUMN_ID))
                sum = cursor.getInt(cursor.getColumnIndex(DBContract.ExpenseModelEntry.COLUMN_SUM))
                sumType = cursor.getString(cursor.getColumnIndex(DBContract.ExpenseModelEntry.COLUMN_SUM_TYPE))
                capacity = cursor.getFloat(cursor.getColumnIndex(DBContract.ExpenseModelEntry.COLUMN_CAPACITY))
                capacityType = cursor.getString(cursor.getColumnIndex(DBContract.ExpenseModelEntry.COLUMN_CAPACITY_TYPE))
                distance = cursor.getInt(cursor.getColumnIndex(DBContract.ExpenseModelEntry.COLUMN_DISTANCE))
                distanceType = cursor.getString(cursor.getColumnIndex(DBContract.ExpenseModelEntry.COLUMN_DISTANCE_TYPE))
                price = cursor.getInt(cursor.getColumnIndex(DBContract.ExpenseModelEntry.COLUMN_PRICE))
                priceType = cursor.getString(cursor.getColumnIndex(DBContract.ExpenseModelEntry.COLUMN_PRICE_TYPE))
                date = cursor.getString(cursor.getColumnIndex(DBContract.ExpenseModelEntry.COLUMN_DATE))
                users.add(ExpenseModel(userid, sum, sumType, capacity, capacityType, distance, distanceType, price, priceType, date))
                cursor.moveToNext()
            }
        }
        return users
    }

    companion object {

        // If you change the database schema, you must increment the database version.
        private val DATABASE_VERSION = 1

        //Database name.
        val DATABASE_NAME = "FeedReader.db"

        private val SQL_CREATE_ENTRIES =
                "CREATE TABLE ${DBContract.ExpenseModelEntry.TABLE_NAME} (" +
                        "${DBContract.ExpenseModelEntry.COLUMN_ID} INTEGER PRIMARY KEY," +
                        "${DBContract.ExpenseModelEntry. COLUMN_SUM} INTEGER," +
                        "${DBContract.ExpenseModelEntry. COLUMN_SUM_TYPE} TEXT," +
                        "${DBContract.ExpenseModelEntry.COLUMN_CAPACITY} REAL," +
                        "${DBContract.ExpenseModelEntry.COLUMN_CAPACITY_TYPE} TEXT," +
                        "${DBContract.ExpenseModelEntry.COLUMN_DISTANCE} INTEGER," +
                        "${DBContract.ExpenseModelEntry.COLUMN_DISTANCE_TYPE} TEXT," +
                        "${DBContract.ExpenseModelEntry.COLUMN_PRICE} INTEGER," +
                        "${DBContract.ExpenseModelEntry.COLUMN_PRICE_TYPE} TEXT," +
                        "${DBContract.ExpenseModelEntry.COLUMN_DATE} TEXT)"

        private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${DBContract.ExpenseModelEntry.TABLE_NAME}"
    }
}