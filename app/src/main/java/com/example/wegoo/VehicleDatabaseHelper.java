package com.example.wegoo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class VehicleDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "wegoo_offline.db";
    private static final int DB_VERSION = 3; // Incremented version for schema change

    public static final String TABLE_VEHICLES = "vehicles";
    public static final String TABLE_DELETED_VEHICLES = "deleted_vehicles";
    // columns
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_TYPE = "type";
    public static final String COL_PRICE = "price";
    public static final String COL_IMAGE_URI = "image_uri";
    public static final String COL_SYNCED = "synced"; // 0 = not synced, 1 = synced
    public static final String COL_FIRESTORE_ID = "firestore_id"; // store doc id after sync
    public static final String COL_DELETED_AT = "deleted_at";

    // Constructor name must match the class name
    public VehicleDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_VEHICLES + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_NAME + " TEXT NOT NULL," + // Added NOT NULL constraint
                COL_TYPE + " TEXT NOT NULL," + // Added NOT NULL constraint
                COL_PRICE + " REAL NOT NULL," + // Added NOT NULL constraint
                COL_IMAGE_URI + " TEXT," +
                COL_SYNCED + " INTEGER NOT NULL DEFAULT 0," +
                COL_FIRESTORE_ID + " TEXT" +
                ");";
        db.execSQL(sql);

        String deletedSql = "CREATE TABLE " + TABLE_DELETED_VEHICLES + " (" +
                COL_ID + " INTEGER PRIMARY KEY," +
                COL_NAME + " TEXT NOT NULL," +
                COL_TYPE + " TEXT NOT NULL," +
                COL_PRICE + " REAL NOT NULL," +
                COL_IMAGE_URI + " TEXT," +
                COL_SYNCED + " INTEGER," +
                COL_FIRESTORE_ID + " TEXT," +
                COL_DELETED_AT + " INTEGER" +
                ");";
        db.execSQL(deletedSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // A simple upgrade policy: drop and recreate.
        // For production apps, you'd migrate data.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VEHICLES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DELETED_VEHICLES);
        onCreate(db);
    }

    /**
     * Inserts a new vehicle record into the database.
     * This method combines the logic for inserting a vehicle.
     * @param name Name of the vehicle.
     * @param type Type of the vehicle.
     * @param price Price of the vehicle.
     * @param imageUri Local or remote URI for the image.
     * @param syncStatus 0 for not synced, 1 for synced.
     * @return The row ID of the newly inserted row, or -1 if an error occurred.
     */
    public long insertVehicle(String name, String type, double price, String imageUri, int syncStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME, name);
        cv.put(COL_TYPE, type);
        cv.put(COL_PRICE, price);
        cv.put(COL_IMAGE_URI, imageUri);
        cv.put(COL_SYNCED, syncStatus);
        // firestore_id will be null by default, which is correct
        return db.insert(TABLE_VEHICLES, null, cv);
    }

    /**
     * Updates a local vehicle record to mark it as synced with Firestore.
     * @param localId The local database ID of the vehicle.
     * @param firestoreId The document ID from Firestore.
     * @return The number of rows affected.
     */
    public int markVehicleSynced(long localId, String firestoreId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_SYNCED, 1);
        cv.put(COL_FIRESTORE_ID, firestoreId);
        return db.update(TABLE_VEHICLES, cv, COL_ID + "=?", new String[]{String.valueOf(localId)});
    }

    /**
     * Retrieves all vehicles that have not yet been synced to Firestore.
     * @return A Cursor containing the unsynced vehicles.
     */
    public Cursor getUnsyncedVehicles() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_VEHICLES, null, COL_SYNCED + " = 0", null, null, null, null);
    }

    /**
     * Retrieves all vehicles from the local database, ordered by ID descending.
     * @return A Cursor containing all vehicles.
     */
    public Cursor getAllVehicles() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_VEHICLES, null, null, null, null, null, COL_ID + " DESC");
    }

    /**
     * Deletes a vehicle from the local database using its ID.
     * @param id The local database ID of the vehicle to delete.
     * @return The number of rows deleted.
     */
    public int deleteVehicle(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_VEHICLES, null, COL_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToFirst()) {
            ContentValues cv = new ContentValues();
            cv.put(COL_ID, cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)));
            cv.put(COL_NAME, cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)));
            cv.put(COL_TYPE, cursor.getString(cursor.getColumnIndexOrThrow(COL_TYPE)));
            cv.put(COL_PRICE, cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRICE)));
            cv.put(COL_IMAGE_URI, cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE_URI)));
            cv.put(COL_SYNCED, cursor.getInt(cursor.getColumnIndexOrThrow(COL_SYNCED)));
            cv.put(COL_FIRESTORE_ID, cursor.getString(cursor.getColumnIndexOrThrow(COL_FIRESTORE_ID)));
            cv.put(COL_DELETED_AT, System.currentTimeMillis());
            db.insert(TABLE_DELETED_VEHICLES, null, cv);
        }
        cursor.close();
        return db.delete(TABLE_VEHICLES, COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    /**
     * Retrieves all deleted vehicles from the local database, ordered by deletion time descending.
     * @return A Cursor containing all deleted vehicles.
     */
    public Cursor getDeletedVehicles() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_DELETED_VEHICLES, null, null, null, null, null, COL_DELETED_AT + " DESC");
    }
}
