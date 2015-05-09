package com.example.tracker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

public class Commondata {
	
	public static SQLiteDatabase SQLiteDatabase;
	public static float SensorX;
	public static float SensorY;
	public static float SensorZ;
	public static int GpsFetchTime;
	public static int BatteryUsage;
	public static int AutoSyncTime;
	public static String ProductURL;
	public static String DeviceID;
	public static String PostURL="http://52.16.149.182:9500/cardata/raw?token=6e2f1a1f1011ddc18cb526f8834c07becfb7453efdfca697710cd619f3c524f5caf21942c643fc54d21d6afdae8316611ed4e430ff22af50353c3f4f2fba0536";




	
	public static void LogReadings(String Date,double Latitude,double Longitude,double Speed,double Accuracy,int Type)
	 {
		 	String SDCardPath;
			if(getExternalStorage()!=null)
			{
				SDCardPath=Commondata.getExternalStorage();
			}else
			{
				SDCardPath=Environment.getExternalStorageDirectory().getAbsolutePath();
			}	
			
			File GPSfile = new File(SDCardPath + "/EazyTracker/GPS_"+GetCurrentDate()+".txt");
			File Accelerometerfile = new File(SDCardPath + "/EazyTracker/Accelerometer_"+GetCurrentDate()+".txt");
			List<String> FileList=LoadDirectoryfiles(SDCardPath + "/EazyTracker",".txt");
			
		 try 
   	 {
			 if(Type==1)
			 {
				 if (!GPSfile.exists())
		      	 {
		      		GPSfile.createNewFile();
		      	 }
		      	 FileWriter fw = new FileWriter(GPSfile.getAbsoluteFile(), true);
		           BufferedWriter bw = new BufferedWriter(fw);
		        	   bw.write("GPS :"+Date+" Latitude :==>  "+Latitude+" , Logitude :==>"+Longitude+" , Speed :==>"+Speed+" , Accuracy :==>"+Accuracy+"\r\n");
//					 
					 bw.close();
					 fw=null;bw=null;bw=null;
			 }
			 else
			 {
				 if (!Accelerometerfile.exists())
		      	 {
					 Accelerometerfile.createNewFile();
		      	 }
		      	 FileWriter fw = new FileWriter(Accelerometerfile.getAbsoluteFile(), true);
		           BufferedWriter bw = new BufferedWriter(fw);
		        	   bw.write("Accelerometer:"+Date+" X :==>  "+Latitude+" , Y :==>"+Longitude+" , Z :==>"+Speed+"\r\n");
					 
					 bw.close();
					 fw=null;bw=null;bw=null;
			 }
			 
			 
		} catch (IOException e) 
		{
			Log.e("WriteLog", e.toString());
		}
		 GPSfile=null;SDCardPath=null;
		 Accelerometerfile=null;
	 }
	
	public static String getExternalStorage()
	 {
	        String exts =  Environment.getExternalStorageDirectory().getPath();
	        try 
	        {
	           FileReader fr = new FileReader(new File("/proc/mounts"));       
	           BufferedReader br = new BufferedReader(fr);
	           String sdCard=null;
	           String line;
	           while((line = br.readLine())!=null)
	           {
	               if(line.contains("secure") || line.contains("asec")) continue;               
	               if(line.contains("fat") && !line.contains("usb") )
	               {		            	   
		               String[] pars = line.split("\\s");
		               if(pars.length<2) continue;
		               if(pars[1].equals(exts)) continue;
		               sdCard =pars[1]; 
		               break;
	               }
	           }
	          fr.close();
	          br.close();
	          return null;  

	    } catch (Exception e) 
	    {		       
	    	WriteLog("CommonData", "getExternalStorage", e.toString());
	    }
	   return null;
	}
	
	 public static boolean CheckWifiConnection(Activity act)
	 {
		 boolean isInternetConnected=false;
		 try
		 {
			 ConnectivityManager cm =
				        (ConnectivityManager)act.getSystemService(Context.CONNECTIVITY_SERVICE);
				 
				NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
			    isInternetConnected = activeNetwork != null &&
				                      activeNetwork.isConnectedOrConnecting();
		 }
		 catch (Exception e) {
			Commondata.WriteLog("CommonData", "CheckWifiConnection", e.toString());
		}
		return isInternetConnected;
	 }
	
	 public static void CreateTables(SQLiteDatabase db)
	 {
		 try
		 {
			 String CreateTable;
			 CreateTable="CREATE TABLE IF NOT EXISTS Settings(SettingsID INTEGER PRIMARY KEY,GPSFetchInterval INTEGER  not null,BatteryUsage INTEGER not null,ConfigValue text null)";
			 db.execSQL(CreateTable);
			 CreateTable="Insert Into Settings (SettingsID,GPSFetchInterval ,BatteryUsage,ConfigValue) Select *  from (Select (Select ifnull(Max(SettingsID),0)+1 From Settings) SettingsID,10 GPSFetchInterval ,15 BatteryUsage,'' ConfigValue) Where Not Exists (Select SettingsID,GPSFetchInterval ,BatteryUsage,ConfigValue from Settings )";
			 db.execSQL(CreateTable);
			 CreateTable="CREATE TABLE IF NOT EXISTS GPS(UID INTEGER PRIMARY KEY,Latitude double not null,Longitude double not null,Time String not null)";
			 db.execSQL(CreateTable);
			 

		 }
		 catch (Exception e) 
		    {		       
		    	WriteLog("CommonData", "CreateTables", e.toString());
		    }
	 }
	 
	 
	 public static void UpdateScripts(SQLiteDatabase aDatabase)
		{


			String SDCardPath;
			if(getExternalStorage()!=null)
			{
				SDCardPath=Commondata.getExternalStorage();
			}else
			{
				SDCardPath=Environment.getExternalStorageDirectory().getAbsolutePath();
			}	
			File file = new File(SDCardPath + "/EazyTracker/Scripts.txt");
			if (file.exists())
			{
				try {
				    BufferedReader br = new BufferedReader(new FileReader(file));
				    String line = null;					    
				    do
				    {
				    	 line = br.readLine();
				    	 if(line==null) 
				    	 {
				    		br=null;									    
				    		if(file.exists())
				    		{
				    			file.delete();
				    		}
				    		return;
				    	 }
//				    	 aDatabase.execSQL(line);
				    	 if(line.toLowerCase().contains("alter") && line.toLowerCase().contains("table") )
				    	 {					    		 
				    		 AlterTableScripts(aDatabase,line);				    		 
				    	 }else
				    	 {
				    		 aDatabase.execSQL(line);
				    	 }					    	
				    }
				    while (line!=null);
				}
				catch (IOException e) 
				{
					WriteLog("CommonData", "UpdateScripts", e.toString());
				}
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
				WriteLog("CommonData", "AlterTableScripts", e.toString());
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
		    	WriteLog("CommonData", "existsColumnInTable", e.toString());
		        return false;
		    }
		}
		
	public static void WriteLog(String Activity,String MethodName,String ErrorString)
	 {
		 	String SDCardPath;
			if(getExternalStorage()!=null)
			{
				SDCardPath=Commondata.getExternalStorage();
			}else
			{
				SDCardPath=Environment.getExternalStorageDirectory().getAbsolutePath();
			}	
			if (new File(SDCardPath + "/EazyTracker/Log").exists()!=true)
			{
				new File(SDCardPath + "/EazyTracker/Log").mkdir();
			}
			File file = new File(SDCardPath + "/EazyTracker/Log/Log_"+GetCurrentDate()+".txt");
			List<String> FileList=LoadDirectoryfiles(SDCardPath + "/EazyTracker/Log",".txt");
			
		 try 
    	 {
			 if(FileList.size()>3)
				{
				 	File DeleteFile=new File(SDCardPath + "/EazyTracker/Log/"+FileList.get(0).toString());
				 	if(DeleteFile.exists())
				 	{
				 		DeleteFile.delete();
				 	}
					FileList.remove(0);
				}
       	 if (!file.exists())
       	 {
       		 file.createNewFile();
       	 }
       	 FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
			 bw.write(android.text.format.DateFormat.format("hh:mm aa", Calendar.getInstance().getTime()).toString()+"==>"+Activity+"("+MethodName+")==>"+ErrorString+"\r\n");
			 bw.close();
			 Log.e(Activity+"("+MethodName+")",ErrorString);
			 fw=null;bw=null;bw=null;
		} catch (IOException e) 
		{
			Log.e("WriteLog", e.toString());
		}
		file=null;SDCardPath=null;
	 }
	
	 public static List<String> LoadDirectoryfiles(String Path,String FileExtension) 
	    {
		 	List<String> item = new ArrayList<String>();
		 	 String[]    extension   = {FileExtension};
		 	List<String> itemsd = new ArrayList<String>();
	    	try
	    	{		    		
	    		File directory = new File(Path);
		    	if(directory.exists()) 
		        {
		    		File[] files = directory.listFiles();

		    		Arrays.sort( files, new Comparator()
		    		{
		    		    public int compare(Object o1, Object o2) 
		    		    {
		    		        if (((File)o1).lastModified() > ((File)o2).lastModified()) {
		    		            return -1;
		    		        } else if (((File)o1).lastModified() < ((File)o2).lastModified()) {
		    		            return +1;
		    		        } else {
		    		            return 0;
		    		        }
		    		    }

		    		}); 
			          for(int i=files.length-1; i>=0; i--) 
			          {		             
			        	  for (String anExt : extension)
			              {
			                  if (files[i].getName().endsWith(anExt))
			                  {  
			                	  item.add(files[i].getName());	
			                  }
			              }		        	  
			          }
		        }  
		    	
		    return item;
	    	}catch (Exception e)
	    	{	 WriteLog("CommonData", "LoadDirectoryfiles", e.toString());
	    		 item.add("Error:"+e.toString());
	    		 return item;
			}
	    }
	 
	 public static  String GetCurrentDate()
	 {
		String CurrentDate="";
		try
		{
			Date sDate=Calendar.getInstance().getTime();
			
			if((sDate.getHours()>=0) && ((sDate.getHours()<5)))
			{
				sDate.setHours(sDate.getHours()-5);
			}						
			
			CurrentDate=android.text.format.DateFormat.format("dd-MMM-yyyy", sDate.getTime()).toString();		
			
		}catch (Exception e) 
		{
			WriteLog("CommonData", "GetCurrentDate", e.toString());
		}
		return CurrentDate;
	 }
	 
	 public static String getDateTime() {
	        SimpleDateFormat dateFormat = new SimpleDateFormat(
	                "yyyy-MM-dd HH:mm:ss.S", Locale.getDefault());
	        Date date = new Date();
	        return dateFormat.format(date);
	}

}
