package com.example.weightwatchersthomasbryant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;

public class DatabaseHelper extends SQLiteOpenHelper {


    public static final String USER_TABLE = "USER_TABLE";
    public static final String COLUMN_ID = "COLUMN_ID";
    public static final String COLUMN_USER_EMAIL = "COLUMN_USER_EMAIL";
    public static final String COLUMN_USER_PASSWORD = "COLUMN_USER_PASSWORD";
    public static final String COLUMN_CURRENT_WEIGHT = "COLUMN_CURRENT_WEIGHT";
    public static final String COLUMN_GOAL_WEIGHT = "COLUMN_GOAL_WEIGHT";
    public static final String COLUMN_DATE = "COLUMN_DATE";
    public static final String WEIGHT_TABLE = "WEIGHT_TABLE";
    public static final String COLUMN_ENTRY_ID = "COLUMN_ENTRY_ID";

    public DatabaseHelper(@Nullable Context context) {
        super(context, "weightmonitor.db", null, 1);
    }

    //Called first time database is accessed.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createPrimaryTableStatement = "CREATE TABLE " + USER_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_USER_EMAIL + " TEXT, " + COLUMN_USER_PASSWORD + " TEXT)";
        String createWeightTable = "CREATE TABLE " + WEIGHT_TABLE + " (" + COLUMN_ENTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_USER_EMAIL + " TEXT, " + COLUMN_CURRENT_WEIGHT + " FLOAT, " + COLUMN_GOAL_WEIGHT + " FLOAT, " + COLUMN_DATE + " TEXT)";
        //I know I can set the date as a DATE object in SQL. Not doing that because of JAVA -> SQL date casting...
        db.execSQL(createPrimaryTableStatement);
        db.execSQL(createWeightTable);
    }

    //Called if database version number changes to prevent user database crashing when database is updated.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public User userLogin(String email, String password)
    {
        SQLiteDatabase userDB = this.getReadableDatabase();
        Cursor cs = userDB.rawQuery("SELECT * FROM " + USER_TABLE + " WHERE " + COLUMN_USER_EMAIL + " = ? AND " + COLUMN_USER_PASSWORD + " = ? ", new String[]{email, password}); //email cannot be without an argument.
        User user = null;
        if(cs.moveToFirst())
        {
            user = new User(cs.getInt(0), cs.getString(1), cs.getString(2), 0.0, 0.0);
        }
        cs.close();
        return user;
    }

    public int userExists(String email)
    {
        SQLiteDatabase userDB = this.getReadableDatabase();
        Cursor cs = userDB.rawQuery("SELECT * FROM " + USER_TABLE + " WHERE " + COLUMN_USER_EMAIL + " = ?", new String[]{email});
        int userID = -1;
        if(cs.moveToFirst())
        {
            userID = cs.getInt(0); //return user id
        }
        cs.close();
        return userID;
    }
    public boolean createUser(User user)
    {
        SQLiteDatabase userDB = this.getWritableDatabase();
        ContentValues userCV = new ContentValues();

        userCV.put(COLUMN_USER_EMAIL, user.getEmail());
        userCV.put(COLUMN_USER_PASSWORD, user.getPassword());
        //TODO: ENSURE ALL THESE VALUES ARE SET BEFORE CALLING createUser().
        //TODO: Current weight setter pops up immediately after login, first app open of day, and signup to ensure entry is properly calibrated.
        //TODO: Scan existing database for each user email in table: display for graph.
        //TODO: timestamp: 54:26 https://www.youtube.com/watch?v=312RhjfetP8&ab_channel=freeCodeCamp.org
        //TODO: add inspirational quotes for weight setter: https://www.usatoday.com/story/life/2023/11/30/positive-quotes-to-inspire/11359498002/
        //TODO: java.time.LocalDate.now().toString(); //MESSY!
        long insertUser = userDB.insert(USER_TABLE, null, userCV); //nullColumnHack would allow me to insert an empty table - May need to do this for currWeight and GoalWeight at some point.
        return insertUser != -1; //there's probably an easier way to return the user id upon creation - i'm not going to do that though because docs recommend .insert().
    }

    /*public long updateUser(User user)
    {
        SQLiteDatabase userDB = this.getReadableDatabase();
        ContentValues userCV = new ContentValues();
        userCV.put(COLUMN_CURRENT_WEIGHT, user.getCurrWeight());
        userCV.put(COLUMN_GOAL_WEIGHT, user.getCurrGoalWeight());

        String query = "UPDATE " + USER_TABLE + " SET " + COLUMN_CURRENT_WEIGHT + " = " + user.getCurrWeight() + ", " + COLUMN_GOAL_WEIGHT + " = " + user.getCurrGoalWeight() + " WHERE " + COLUMN_ID + " = " + user.getUserID();
        return userDB.update(USER_TABLE, userCV, (COLUMN_ID + " = " + user.getUserID()), null);
    }*/

    public boolean weightLoggedToday(User user)
    {
        SQLiteDatabase userDB = this.getReadableDatabase();
        //String query = "SELECT * FROM " + WEIGHT_TABLE + " WHERE " + COLUMN_USER_EMAIL + " = " + user.getEmail() + " AND " + COLUMN_DATE + " = " + java.time.LocalDate.now().toString();
        Cursor cs = userDB.rawQuery("SELECT * FROM " + WEIGHT_TABLE + " WHERE " + COLUMN_USER_EMAIL + " = ? " + " AND " + COLUMN_DATE + " = ? ", new String[]{user.getEmail(), java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))});
        boolean logged = false;
        if(cs.moveToFirst()) //entry found
        {
            logged = true;
        }
        cs.close();
        return logged;
    }
    public boolean logWeight(User user)
    {
        SQLiteDatabase userDB = this.getWritableDatabase();
        ContentValues weightCV = new ContentValues();

        weightCV.put(COLUMN_USER_EMAIL, user.getEmail());
        weightCV.put(COLUMN_CURRENT_WEIGHT, user.getCurrWeight());
        weightCV.put(COLUMN_GOAL_WEIGHT, user.getCurrGoalWeight());
        weightCV.put(COLUMN_DATE, java.time.LocalDate.now().toString());

        long insertWeight = userDB.insert(WEIGHT_TABLE, null, weightCV);
        //long updateUser = updateUser(user); user doesn't store this data
        return insertWeight != -1; //if either fails we have a problem
    }

    public Cursor returnWeightList(User user)
    {
        SQLiteDatabase userDB = this.getReadableDatabase();
        //String query = "SELECT * FROM " + WEIGHT_TABLE + " WHERE " + COLUMN_USER_EMAIL + " = " + user.getEmail();
        Cursor cs = userDB.rawQuery("SELECT * FROM " + WEIGHT_TABLE + " WHERE " + COLUMN_USER_EMAIL + " = ? ", new String[]{user.getEmail()});
        //userDB.close();
        return cs;
    }
}
