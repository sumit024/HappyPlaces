package com.app_devs.happyplaces.databases

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.app_devs.happyplaces.models.HappyPlaceModel

class DatabaseHandler(context: Context) :SQLiteOpenHelper(context, DATABASE_NAME,null, DATABASE_VERSION) {
    companion object{
        private const val TABLE_NAME="HappyPlacesTable"
        private const val DATABASE_NAME="happyPlaces.db"
        private const val DATABASE_VERSION=1

        //column names
        private const val KEY_ID="_id"
        private const val KEY_TITLE="title"
        private const val KEY_DESCRIPTION="description"
        private const val KEY_DATE="date"
        private const val KEY_LATITUDE="latitude"
        private const val KEY_LONGITUDE="longitude"
        private const val KEY_LOCATION="location"
        private const val KEY_IMAGE="image"


    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable= (
                "CREATE TABLE "+ TABLE_NAME +
                        "(" + KEY_ID + " INTEGER PRIMARY KEY,"+ KEY_TITLE + " TEXT,"
                        + KEY_DESCRIPTION + " TEXT," + KEY_DATE + " TEXT," + KEY_LATITUDE + " TEXT,"
                        + KEY_LONGITUDE + " TEXT,"+ KEY_LOCATION + " TEXT,"
                        + KEY_IMAGE +" TEXT" + ")"
                )
        db?.execSQL(createTable)
        Log.d("TABLECHECK","CREATED")

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db);
    }

    fun addHappyPlace(happyPlaceModel: HappyPlaceModel):Long
    {
        val db=this.writableDatabase
        val cv=ContentValues()
        cv.put(KEY_TITLE,happyPlaceModel.title)
        cv.put(KEY_DESCRIPTION,happyPlaceModel.description)
        cv.put(KEY_DATE,happyPlaceModel.date)
        cv.put(KEY_LATITUDE,happyPlaceModel.latitude)
        cv.put(KEY_LONGITUDE,happyPlaceModel.longitude)
        cv.put(KEY_LOCATION,happyPlaceModel.location)
        cv.put(KEY_IMAGE,happyPlaceModel.image)

        val result=db.insert(TABLE_NAME,null,cv)
        db.close()
        Log.d("ADDED","YES")
        return result
    }
    fun updateHappyPlace(happyPlaceModel: HappyPlaceModel):Int
    {
        val db=this.writableDatabase
        val cv=ContentValues()
        cv.put(KEY_TITLE,happyPlaceModel.title)
        cv.put(KEY_DESCRIPTION,happyPlaceModel.description)
        cv.put(KEY_DATE,happyPlaceModel.date)
        cv.put(KEY_LATITUDE,happyPlaceModel.latitude)
        cv.put(KEY_LONGITUDE,happyPlaceModel.longitude)
        cv.put(KEY_LOCATION,happyPlaceModel.location)
        cv.put(KEY_IMAGE,happyPlaceModel.image)

        val result=db.update(TABLE_NAME,cv, KEY_ID+"="+happyPlaceModel.id,null)
        db.close()
        Log.d("Updated","YES")
        return result
    }

    fun getHappyPlaceList(): ArrayList<HappyPlaceModel>
    {
        val list= ArrayList<HappyPlaceModel>()
        val selectQuery="SELECT * FROM $TABLE_NAME"
        val db=this.readableDatabase
        try {
            val cursor: Cursor =db.rawQuery(selectQuery,null)
            if (cursor.moveToFirst()) {
                do {
                    val place = HappyPlaceModel(
                        cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                        cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndex(KEY_IMAGE)),
                        cursor.getString(cursor.getColumnIndex(KEY_LOCATION)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE)),
                        cursor.getString(cursor.getColumnIndex(KEY_DATE))
                    )
                    list.add(place)
                } while (cursor.moveToNext())
            }
            cursor.close()

        }catch (e:SQLiteException)
        {
          db.execSQL(selectQuery)
            return list
        }
        return list

    }

    fun deleteHappyPlace(happyPlaceModel: HappyPlaceModel):Int
    {
        val db=this.writableDatabase
        val success=db.delete(TABLE_NAME, KEY_ID+"="+happyPlaceModel.id,null)
        db.close()
        return success
    }


}