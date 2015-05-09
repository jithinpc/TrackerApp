package com.example.tracker;


import java.io.File;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class BaseActivity extends Activity{

	protected SQLiteDatabase database;
	protected String SDCardPath;
	protected String dbPath;

	
	protected void onCreate(Bundle savedInstanceState) 
	{
		try
		{	
			SDCardPath=Environment.getExternalStorageDirectory().getAbsolutePath();
			if(Commondata.getExternalStorage()!=null)
			{
				SDCardPath=Commondata.getExternalStorage();
			}else
			{
//				Toast.makeText(this, "SDCard not found", Toast.LENGTH_LONG).show();
			}		
			if (new File(SDCardPath + "/EazyTracker").exists()!=true)
			{
				new File(SDCardPath + "/EazyTracker").mkdir();
			}		
			dbPath=	new File(SDCardPath + "/EazyTracker").getAbsolutePath();
			
			super.onCreate(savedInstanceState);
			database=this.openOrCreateDatabase(dbPath + "/EazyTracker.sqlite",MODE_MULTI_PROCESS,null);
			Commondata.SQLiteDatabase=database;
		}
		catch(Exception ex)
		{
			Log.d("Exception",ex.toString());
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(database!=null)
		{
			if (database.isOpen()) {
			database.close();};
				database=null;}
		super.onDestroy();
	}
	
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		if(database!=null)
		{
			if (database.isOpen()) {
			database.close();};
				database=null;}

		super.finalize();
	}


}
