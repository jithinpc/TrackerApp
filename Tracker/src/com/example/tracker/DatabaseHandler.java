package com.example.tracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler  extends SQLiteOpenHelper 
{

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "EazyTracker";
	
	public DatabaseHandler(Context context) 
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		try
		{   
		
		 String CreateTable;
		 CreateTable="CREATE TABLE IF NOT EXISTS Settings(SettingsID INTEGER PRIMARY KEY,GPSFetchInterval INTEGER  not null,BatteryUsage INTEGER not null,AutoSync INTEGER not null,ConfigValue text null,ProductURL text null)";
		 db.execSQL(CreateTable);
		 CreateTable="Insert Into Settings (SettingsID,GPSFetchInterval ,BatteryUsage,AutoSync,ConfigValue,ProductURL) Select *  from (Select (Select ifnull(Max(SettingsID),0)+1 From Settings) SettingsID,5 GPSFetchInterval ,15 BatteryUsage,1 AutoSync,'' ConfigValue,'http://52.16.149.182:9500/cardata/raw?token=6e2f1a1f1011ddc18cb526f8834c07becfb7453efdfca697710cd619f3c524f5caf21942c643fc54d21d6afdae8316611ed4e430ff22af50353c3f4f2fba0536' ProductURL) Where Not Exists (Select SettingsID,GPSFetchInterval ,BatteryUsage,AutoSync,ConfigValue,ProductURL from Settings )";
		 db.execSQL(CreateTable);
		 CreateTable="CREATE TABLE IF NOT EXISTS DeviceGPSLocation(UID INTEGER PRIMARY KEY AUTOINCREMENT,Latitude text not null,Longitude text not null,Time text not null,SyncFlag INTEGER not null)";
		 db.execSQL(CreateTable);
		 CreateTable="CREATE TABLE IF NOT EXISTS DeviceAccelerometerDetails(UID INTEGER PRIMARY KEY AUTOINCREMENT,XCoordinate text not null,YCoordinate text not null,ZCoordinate text not null,Time text not null,SyncFlag INTEGER not null)";
		 db.execSQL(CreateTable);
		 
		 CreateTable="Alter Table Settings add AutoSync INTEGER default(1);";
		 AlterTableScripts(db,CreateTable);
		 CreateTable="Alter Table Settings add ProductURL text default('http://52.16.149.182:9500/cardata/raw?token=6e2f1a1f1011ddc18cb526f8834c07becfb7453efdfca697710cd619f3c524f5caf21942c643fc54d21d6afdae8316611ed4e430ff22af50353c3f4f2fba0536');";
		 AlterTableScripts(db,CreateTable);
			
		}catch (Exception e) 
		{
			
			Log.e("Error :onCreate(SQLite)",e.toString());
		}
	}
	
	private static void AlterTableScripts(SQLiteDatabase database,String AlterTable)
	{
		try
		{	
			 if(AlterTable.toLowerCase().contains("alter") && AlterTable.toLowerCase().contains("table") )
	    	 {					    		 
	    		String[] split= AlterTable.split(" ");
	    		if(!existsColumnInTable(database,split[2].toString().trim(),split[4].toString().trim()))
	    		{
	    			database.execSQL(AlterTable);
	    		}					    		 
	    	 }
		}
		catch (Exception e) 
		{
			Commondata.WriteLog("DatabaseHandler", "AlterTableScripts", e.toString());
		}		
	}
	public static boolean existsColumnInTable(SQLiteDatabase inDatabase, String inTable, String columnToCheck) 
	{
	    try
	    {
	    	String ExecQuery="SELECT * FROM " + inTable + " LIMIT 0";
	        Cursor mCursor  = inDatabase.rawQuery(ExecQuery, null );
	        
	        if(mCursor.getColumnIndex(columnToCheck) != -1)
	            return true;
	        else
	            return false;

	    }catch (Exception e)
	    {
	    	Commondata.WriteLog("DatabaseHandler", "existsColumnInTable", e.toString());
	        return false;
	    }
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

}
