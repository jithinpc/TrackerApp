package com.example.tracker;

public class DeviceAccelerometerDetails
{
long mUID;
String mXCoordinate;
String mYCoordinate;
String mZCoordinate;
String mTime;
long mSyncFlag;

DeviceAccelerometerDetails(long aUID,String aXCoordinate,String aYCoordinate,String aZCoordinate,String aTime,long aSyncFlag) 
{ 
 mUID=aUID;
 mXCoordinate=aXCoordinate;
 mYCoordinate=aYCoordinate;
 mZCoordinate=aZCoordinate;
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

public String getXCoordinate()
{
return  mXCoordinate;
}

public void setXCoordinate(String aXCoordinate)
{ 
mXCoordinate=aXCoordinate;
}

public String getYCoordinate()
{
return  mYCoordinate;
}

public void setYCoordinate(String aYCoordinate)
{ 
mYCoordinate=aYCoordinate;
}

public String getZCoordinate()
{
return  mZCoordinate;
}

public void setZCoordinate(String aZCoordinate)
{ 
mZCoordinate=aZCoordinate;
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

