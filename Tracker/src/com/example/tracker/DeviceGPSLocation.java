package com.example.tracker;

public class DeviceGPSLocation
{
long mUID;
String mLatitude;
String mLongitude;
String mTime;
long mSyncFlag;

DeviceGPSLocation(long aUID,String aLatitude,String aLongitude,String aTime,long aSyncFlag) 
{ 
 mUID=aUID;
 mLatitude=aLatitude;
 mLongitude=aLongitude;
 mTime=aTime;
 mSyncFlag=aSyncFlag;
 } 

public long getUID()
{
return  mUID;
}

public void setUID(long aUID)
{ 
mUID=aUID;
}

public String getLatitude()
{
return  mLatitude;
}

public void setLatitude(String aLatitude)
{ 
mLatitude=aLatitude;
}

public String getLongitude()
{
return  mLongitude;
}

public void setLongitude(String aLongitude)
{ 
mLongitude=aLongitude;
}

public String getTime()
{
return  mTime;
}

public void setTime(String aTime)
{ 
mTime=aTime;
}

public long getSyncFlag()
{
return  mSyncFlag;
}

public void setSyncFlag(long aSyncFlag)
{ 
mSyncFlag=aSyncFlag;
}

} 

