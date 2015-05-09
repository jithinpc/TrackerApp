package com.example.tracker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import android.content.ContentValues;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;


public class DeviceGPSLocationData
{
public ArrayList<DeviceGPSLocation> getDeviceGPSLocation(SQLiteDatabase aDatabase,String aFullWhereCriteria,String[] aValues)
{
SQLiteCursor cursor=(SQLiteCursor) aDatabase.rawQuery("select * from DeviceGPSLocation Where " + aFullWhereCriteria,aValues); 
cursor.move(1); 
ArrayList<DeviceGPSLocation> mArrayList = new ArrayList<DeviceGPSLocation>();
DeviceGPSLocation p=null;
for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
{
p=new DeviceGPSLocation(cursor.getLong(cursor.getColumnIndex("UID")),cursor.getString(cursor.getColumnIndex("Latitude"))==null ? "" : cursor.getString(cursor.getColumnIndex("Latitude")),cursor.getString(cursor.getColumnIndex("Longitude"))==null ? "" : cursor.getString(cursor.getColumnIndex("Longitude")),cursor.getString(cursor.getColumnIndex("Time"))==null ? "" : cursor.getString(cursor.getColumnIndex("Time")),cursor.getLong(cursor.getColumnIndex("SyncFlag")));
mArrayList.add(p);
}
cursor.close();
return mArrayList;
}

public String AddDeviceGPSLocation(SQLiteDatabase aDatabase,DeviceGPSLocation aDeviceGPSLocation)
{
String ResultStr="";
String MethodName;String key;
Method[] Methods;
ContentValues aValues=new ContentValues();
Object ObjectValue=null;
Class c=DeviceGPSLocation.class;
Methods=c.getMethods();
for(int i=0;i<Methods.length;i++)
{
Method mth=Methods[i];
MethodName=mth.getName();
try
{
if (MethodName.startsWith("get") && !MethodName.contains("Class"))
{
ObjectValue=mth.invoke(aDeviceGPSLocation, null);
key=mth.getName().substring(3,MethodName.length());
if (key.equals("UID")) 
{
aValues.putNull(key);
}
else
{
aValues.put(key,ObjectValue.toString());
}}
}
catch(InvocationTargetException ex)
{
}
catch(IllegalAccessException ex)
{
}
}
try
{
long id =aDatabase.insertOrThrow("DeviceGPSLocation", null, aValues);
if (id > 0) 
{
ResultStr= "Success";
}else
{
ResultStr= "DeviceGPSLocation Not Added";
}
}catch (Exception e)
{
ResultStr= e.toString();
}
return ResultStr;
}

public void ModifyDeviceGPSLocation(SQLiteDatabase aDatabase,DeviceGPSLocation aDeviceGPSLocation)throws InvocationTargetException
{
String MethodName;String key;
Method[] Methods;
ContentValues aValues=new ContentValues();
Object ObjectValue=null;
Class<DeviceGPSLocation> c=DeviceGPSLocation.class;
Methods=c.getMethods();
for(int i=0;i<Methods.length;i++)
{
Method mth=Methods[i];
MethodName=mth.getName();
try
{
if (MethodName.startsWith("get") && !MethodName.contains("Class"))
{
ObjectValue=mth.invoke(aDeviceGPSLocation, null);
key=mth.getName().substring(3,MethodName.length());
aValues.put(key,ObjectValue.toString());
}
}
catch(InvocationTargetException ex)
{
}
catch(IllegalAccessException ex)
{
}
}
aDatabase.updateWithOnConflict("DeviceGPSLocation", aValues,"UID=?",new String[]{aValues.get("UID").toString()},SQLiteDatabase.CONFLICT_REPLACE);
}


public void DeleteDeviceGPSLocation(SQLiteDatabase aDatabase,DeviceGPSLocation aDeviceGPSLocation)throws InvocationTargetException
{
String MethodName;String key;
ContentValues aValues=new ContentValues();
Object ObjectValue=null;
Class c=DeviceGPSLocation.class;
try
{
Method mth;
MethodName="getUID";
mth=	c.getMethod(MethodName,null);
ObjectValue=mth.invoke(aDeviceGPSLocation, null);
key=mth.getName().substring(3,MethodName.length());
aValues.put(key,ObjectValue.toString());
}
catch(InvocationTargetException ex)
{
}
catch(IllegalAccessException ex)
{
}
catch(NoSuchMethodException ex)
{
}
aDatabase.delete("DeviceGPSLocation","UID=?",new String[]{aValues.get("UID").toString()});
}


}

