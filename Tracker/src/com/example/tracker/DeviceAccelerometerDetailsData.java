package com.example.tracker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import android.content.ContentValues;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;


public class DeviceAccelerometerDetailsData
{
public ArrayList<DeviceAccelerometerDetails> getDeviceAccelerometerDetails(SQLiteDatabase aDatabase,String aFullWhereCriteria,String[] aValues)
{
SQLiteCursor cursor=(SQLiteCursor) aDatabase.rawQuery("select * from DeviceAccelerometerDetails Where " + aFullWhereCriteria,aValues); 
cursor.move(1); 
ArrayList<DeviceAccelerometerDetails> mArrayList = new ArrayList<DeviceAccelerometerDetails>();
DeviceAccelerometerDetails p=null;
for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
{
p=new DeviceAccelerometerDetails(cursor.getLong(cursor.getColumnIndex("UID")),cursor.getString(cursor.getColumnIndex("XCoordinate"))==null ? "" : cursor.getString(cursor.getColumnIndex("XCoordinate")),cursor.getString(cursor.getColumnIndex("YCoordinate"))==null ? "" : cursor.getString(cursor.getColumnIndex("YCoordinate")),cursor.getString(cursor.getColumnIndex("ZCoordinate"))==null ? "" : cursor.getString(cursor.getColumnIndex("ZCoordinate")),cursor.getString(cursor.getColumnIndex("Time"))==null ? "" : cursor.getString(cursor.getColumnIndex("Time")),cursor.getLong(cursor.getColumnIndex("SyncFlag")));
mArrayList.add(p);
}
cursor.close();
return mArrayList;
}

public String AddDeviceAccelerometerDetails(SQLiteDatabase aDatabase,DeviceAccelerometerDetails aDeviceAccelerometerDetails)
{
String ResultStr="";
String MethodName;String key;
Method[] Methods;
ContentValues aValues=new ContentValues();
Object ObjectValue=null;
Class c=DeviceAccelerometerDetails.class;
Methods=c.getMethods();
for(int i=0;i<Methods.length;i++)
{
Method mth=Methods[i];
MethodName=mth.getName();
try
{
if (MethodName.startsWith("get") && !MethodName.contains("Class"))
{
ObjectValue=mth.invoke(aDeviceAccelerometerDetails, null);
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
long id =aDatabase.insertOrThrow("DeviceAccelerometerDetails", null, aValues);
if (id > 0) 
{
ResultStr= "Success";
}else
{
ResultStr= "DeviceAccelerometerDetails Not Added";
}
}catch (Exception e)
{
ResultStr= e.toString();
}
return ResultStr;
}

public void ModifyDeviceAccelerometerDetails(SQLiteDatabase aDatabase,DeviceAccelerometerDetails aDeviceAccelerometerDetails)throws InvocationTargetException
{
String MethodName;String key;
Method[] Methods;
ContentValues aValues=new ContentValues();
Object ObjectValue=null;
Class<DeviceAccelerometerDetails> c=DeviceAccelerometerDetails.class;
Methods=c.getMethods();
for(int i=0;i<Methods.length;i++)
{
Method mth=Methods[i];
MethodName=mth.getName();
try
{
if (MethodName.startsWith("get") && !MethodName.contains("Class"))
{
ObjectValue=mth.invoke(aDeviceAccelerometerDetails, null);
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
aDatabase.updateWithOnConflict("DeviceAccelerometerDetails", aValues,"UID=?",new String[]{aValues.get("UID").toString()},SQLiteDatabase.CONFLICT_REPLACE);
}


public void DeleteDeviceAccelerometerDetails(SQLiteDatabase aDatabase,DeviceAccelerometerDetails aDeviceAccelerometerDetails)throws InvocationTargetException
{
String MethodName;String key;
ContentValues aValues=new ContentValues();
Object ObjectValue=null;
Class c=DeviceAccelerometerDetails.class;
try
{
Method mth;
MethodName="getUID";
mth=	c.getMethod(MethodName,null);
ObjectValue=mth.invoke(aDeviceAccelerometerDetails, null);
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
aDatabase.delete("DeviceAccelerometerDetails","UID=?",new String[]{aValues.get("UID").toString()});
}


}

