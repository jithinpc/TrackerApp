package com.example.tracker;


import org.ksoap2.serialization.SoapObject;

import android.database.sqlite.SQLiteDatabase;

public class WebServiceMethods {
	
	public String  GPSTrackerInfo(String GPSInfo,String AccelerometerInfo,SQLiteDatabase aDatabase)
	{
		String ReturnString = "";
		SoapObject result;
    	
    	try
    	{
    		
    		result=wsconnector.ConnectWS(Commondata.ProductURL,"GPSTrackerInfo", 
        			new String[]{"GPSInfo","AccelerometerInfo","MachineID"},
        			new String[]{GPSInfo,AccelerometerInfo,Commondata.DeviceID});
    		
            ReturnString=result.getPropertyAsString(0);
			
			result=null;
	    	
			
        }
	        catch(Exception ex)
	        {
	        	ReturnString=ex.toString();
	        	Commondata.WriteLog("WebServiceMethods","GPSTrackerInfo",ex.toString());
	        }finally
	        {	
	        	result=null;
	        	
	        }
		return ReturnString;		
	}

}
