package com.example.tracker;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

public class HomeActivity extends TabActivity implements SensorEventListener{

	
	TabHost mTabHost ;
 	TabSpec tab1, tab2, tab3, tab4;
 	TextView titleTextView;
	
	protected SQLiteDatabase database;
	protected DatabaseHandler DBH;
	protected String SDCardPath;
	protected String dbPath;
	Context myContext;
	Button StartButton,StopButton,Clearbutton;
	
	GPSTracker gps;
	TextView LatitudeTextView,LongitudeTextView;
	
	Handler Autohandler=null;
	Runnable AutoRunnable;
	
	Handler Synchandler=null;
	Runnable SyncRunnable;
	
	private SensorManager sensorManager;
	
	SeekBar TimeSeek;
	Dialog UserConformationDialog,SettingsDialog;
	
	
	ImageView StartOrStopImageView;
	TextView InsuranceForTripTextView,InsureTypeTextView,CreditBalanceTextView;
	TextView SpeedTextView,DuarationTextView,DistanceTextView,AvgDriveScoreTextView;
	ImageView MenuimageView;
	int StartStopFlag=0;
	
	boolean OnMethodError=false;
	String OnMethodErrorString="";
	boolean IsSyncExecuting=false;
	EditText WSURLEdittext;
	
	DecimalFormat aDecimalFormat=new DecimalFormat("#0.00000");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.mainactivity);
		try
		{
			myContext=this;	
			
			FolderCreation();
	        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	        
			Initialize();
			ClickEvents();
			
			AssingTabValues();
			
			LocationAutoSync();
			
		}
		catch (Exception e) {
			Commondata.WriteLog("HomeActivity", "Oncreate", e.toString());
		}
		
		
	}

	
	private void AssingTabValues()
	{
		try
		{
			mTabHost = getTabHost();
	    	
			mTabHost.addTab(mTabHost.newTabSpec("Weekly").setIndicator("Weekly").setContent(R.id.tab1));
			mTabHost.addTab(mTabHost.newTabSpec("Monthly").setIndicator("Monthly").setContent(R.id.tab2));
			mTabHost.addTab(mTabHost.newTabSpec("Yearly").setIndicator("Yearly").setContent(R.id.tab3));
			
			  TextView tv =  (TextView) mTabHost.getTabWidget().getChildAt(0).findViewById(android.R.id.title); 
			   tv.setAllCaps(false);  
			   tv.setTypeface(Typeface.DEFAULT,Typeface.NORMAL);
			   tv =  (TextView) mTabHost.getTabWidget().getChildAt(1).findViewById(android.R.id.title); 
			   tv.setAllCaps(false);  
			   tv.setTypeface(Typeface.DEFAULT,Typeface.NORMAL);
			   tv =  (TextView) mTabHost.getTabWidget().getChildAt(2).findViewById(android.R.id.title); 
			   tv.setAllCaps(false);
			   tv.setTypeface(Typeface.DEFAULT,Typeface.NORMAL);
			   
			   
			   
			for (int i = 0; i < 3; i++) {
			    getTabWidget().getChildAt(i).setFocusableInTouchMode(true);
			}
			
			for(int i=0;i<mTabHost.getTabWidget().getChildCount();i++)
		    {
			    if (i == 0) 
			    	{
			    	
			    		mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(getResources().getColor(R.color.Tab_Selected));
			    		titleTextView= (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
						 titleTextView.setTextSize(15);
						 titleTextView.setTextColor(getResources().getColor(R.color.White));
			    	}

			    else 
			    	{
			    		mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(getResources().getColor(R.color.Tab_UnSelected));  //#1e90ff
			    		titleTextView= (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
						 titleTextView.setTextSize(15);
						 titleTextView.setTextColor(getResources().getColor(R.color.Tab_TextColor));
			    	}
		    } 
			
			mTabHost.setOnTabChangedListener(new OnTabChangeListener(){
				@Override
				public void onTabChanged(String tabId) {
				    // TODO Auto-generated method stub
				     for(int i=0;i<mTabHost.getTabWidget().getChildCount();i++)
				        {
				    	 mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(getResources().getColor(R.color.Tab_UnSelected)); //unselected 
				    	 titleTextView= (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
						 titleTextView.setTextSize(15);
						 titleTextView.setTextColor(getResources().getColor(R.color.Tab_TextColor));
				        }
				     mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).setBackgroundColor(getResources().getColor(R.color.Tab_Selected)); 
				     titleTextView= (TextView) mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).findViewById(android.R.id.title);
					 titleTextView.setTextSize(15);
					 titleTextView.setTextColor(getResources().getColor(R.color.White));// selected
				}
				});
		}
		catch (Exception e) {
			Commondata.WriteLog("HomeActivity", "AssingTabValues", e.toString());
		}
	}
	private void FolderCreation()
	{
		try
		{
			SDCardPath=Environment.getExternalStorageDirectory().getAbsolutePath();
			
			if (new File(SDCardPath + "/EazyTracker").exists()!=true)
			{
				new File(SDCardPath + "/EazyTracker").mkdir();
			}		
			dbPath=	new File(SDCardPath + "/EazyTracker").getAbsolutePath();
			
			DBH=new DatabaseHandler(this);
			database=DBH.getWritableDatabase();
			
			Commondata.DeviceID = Secure.getString(this.getContentResolver(),Secure.ANDROID_ID);
			
			AssignDefaultValues();
			
		}
		catch (Exception e) {
			Commondata.WriteLog("HomeActivity", "FolderCreation", e.toString());
		}
	}
	
	private void AssignDefaultValues()
	{
		try
		{
			
			Commondata.UpdateScripts(database);
			SQLiteCursor cursor=(SQLiteCursor) database.rawQuery("select * from Settings",null); 
			 cursor.move(1); 
			 for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
			 {
				 Commondata.GpsFetchTime=cursor.getInt(cursor.getColumnIndex("GPSFetchInterval"));
				 Commondata.BatteryUsage=cursor.getInt(cursor.getColumnIndex("BatteryUsage"));
				 Commondata.AutoSyncTime=cursor.getInt(cursor.getColumnIndex("AutoSync"));
				 Commondata.ProductURL=cursor.getString(cursor.getColumnIndex("ProductURL"));
				 Commondata.PostURL=cursor.getString(cursor.getColumnIndex("ProductURL"));
			 }
			 cursor.close();
		}
		catch (Exception e) {
			Commondata.WriteLog("HomeActivity", "AssignDefaultValues", e.toString());
		}
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			float[] values = event.values;
		    // Movement
		    Commondata.SensorX = values[0];
		    Commondata.SensorY = values[1];
		    Commondata.SensorZ = values[2];
		    
		    Commondata.LogReadings(Commondata.getDateTime(), Commondata.SensorX, Commondata.SensorY, Commondata.SensorZ,0.0, 2);
		    DeviceAccelerometerDetailsData accelerometerDetailsData=new DeviceAccelerometerDetailsData();
		    DeviceAccelerometerDetails accelerometerDetails=new DeviceAccelerometerDetails(0, String.valueOf(Commondata.SensorX), String.valueOf(Commondata.SensorY), String.valueOf(Commondata.SensorZ), Commondata.getDateTime(),0);
		    accelerometerDetailsData.AddDeviceAccelerometerDetails(database, accelerometerDetails);
		    accelerometerDetails=null;
		    accelerometerDetailsData=null;
		    SpeedCalculation();

		    }
		
	}
	
	private void SpeedCalculation()
	{
		try
		{
			float CalculatedSpeed=0;
			SpeedTextView.setText("");
			CalculatedSpeed=(Commondata.SensorZ*60*60)/3600;
			SpeedTextView.setText(String.valueOf((long)CalculatedSpeed)+" ");
			
		}
		catch (Exception e) {
			Commondata.WriteLog("HomeActivity", "SpeedCalculation", e.toString());
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}
	
	private void Initialize()
	{
		try
		{
			UserConformationDialog=new Dialog(this);
			
			StartOrStopImageView=(ImageView)findViewById(R.id.StartOrStopImageView);
			InsuranceForTripTextView=(TextView)findViewById(R.id.InsuranceForTripTextView);
			InsuranceForTripTextView.setTextColor(getResources().getColor(R.color.Red));
			InsureTypeTextView=(TextView)findViewById(R.id.InsureTypeTextView);
			CreditBalanceTextView=(TextView)findViewById(R.id.CreditBalanceTextView);
			StartOrStopImageView.setImageResource(R.drawable.play);
			
			
			SpeedTextView=(TextView)findViewById(R.id.SpeedTextView);
			DuarationTextView=(TextView)findViewById(R.id.DuarationTextView);
			DistanceTextView=(TextView)findViewById(R.id.DistanceTextView);
			AvgDriveScoreTextView=(TextView)findViewById(R.id.AvgDriveScoreTextView);
			
			MenuimageView=(ImageView)findViewById(R.id.MenuimageView);
			
			SettingsDialog=new Dialog(this);
		}
		catch (Exception e) {
			Commondata.WriteLog("HomeActivity", "Initialize", e.toString());
		}
	}
	

	private void ClickEvents()
	{
		try
		{
			StartOrStopImageView.setOnClickListener(new OnClickListener() {
				

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					try
					{
						Commondata.SensorX=0;
						Commondata.SensorY=0;
						Commondata.SensorZ=0;
						if(StartStopFlag==0)
						{
							StartOrStopImageView.setImageResource(R.drawable.stop);
							StartStopFlag=1;
							InsuranceForTripTextView.setText("Active");
							InsuranceForTripTextView.setTextColor(getResources().getColor(R.color.Green));
							CallSensorEvent();
							GPSFetching();
						}
						else
						{
							StartOrStopImageView.setImageResource(R.drawable.play);
							StartStopFlag=0;
							InsuranceForTripTextView.setText("Active");
							InsuranceForTripTextView.setTextColor(getResources().getColor(R.color.Red));
							RemoveHandler();
							StopAccelerometer();
							
						}
						SpeedCalculation();
						
					}
					catch (Exception e) {
						Commondata.WriteLog("HomeActivity", "ClickEvents(StartOrStopImageView.setOnClickListener)", e.toString());
					}
				}
			});
			
			MenuimageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					SettingsDialog();
					
				}
			});
			
		}
		catch (Exception e) {
			Commondata.WriteLog("HomeActivity", "ClickEvents", e.toString());
		}
		
	}
	
	
	private void GPSFetching()
	{
		try
		{
			
			gps = new GPSTracker(HomeActivity.this);
            // check if GPS enabled     
            if(gps.canGetLocation()){
                 
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();
                double Speed=gps.getSpeed();
                double Accuracy=gps.getAccuracy();
                Commondata.LogReadings(Commondata.getDateTime(), latitude, longitude,Speed,Accuracy,1);
                DeviceGPSLocationData aDeviceGPSLocationData=new DeviceGPSLocationData();
                DeviceGPSLocation aDeviceGPSLocation=new DeviceGPSLocation(0, String.valueOf(latitude), String.valueOf(longitude), Commondata.getDateTime(), 0);
                aDeviceGPSLocationData.AddDeviceGPSLocation(database, aDeviceGPSLocation);
                aDeviceGPSLocation=null;
                aDeviceGPSLocationData=null;
                SetGPSSyncInfo();
                
                
            }else{
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gps.showSettingsAlert();
            }
		}
		catch (Exception e) {
			Commondata.WriteLog("HomeActivity", "GPSFetching", e.toString());
		}
	}
	private void CallSensorEvent()
	{
		try
		{
			if(sensorManager==null)
			{
				sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
			}
			sensorManager.registerListener(this,
			        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
			        SensorManager.SENSOR_DELAY_NORMAL);
		}
		catch (Exception e) {
			Commondata.WriteLog("HomeActivity", "CallSensorEvent", e.toString());
		}
	}
	
	 private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
		    @Override
		    public void onReceive(Context ctxt, Intent intent) {
		      int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
		      if(level<Commondata.BatteryUsage)
		      {
		    	  ErrorDialog("Your Battery is Low. Please Close the App !", myContext,0);
		      }
		      
		    }
		  };

		  private void SettingsDialog()
			{

				try
				{
					if(SettingsDialog.isShowing())
					{
						SettingsDialog.dismiss();
					}
					

					SettingsDialog=new Dialog((Activity)this,R.style.UserDialog);
					SettingsDialog.setTitle("Message");
					SettingsDialog.setCancelable(false);
					SettingsDialog.setContentView(R.layout.settings_layout);
					SettingsDialog.show();
				 
					
					SeekBar GPSTrackingTimeIntervalseekBar=(SeekBar)SettingsDialog.findViewById(R.id.GPSTrackingTimeIntervalseekBar);
					SeekBar BatteryUsageLevelIndicatorseekBar=(SeekBar)SettingsDialog.findViewById(R.id.BatteryUsageLevelIndicatorseekBar);
				     final TextView GpsTimeSelectedtextView=(TextView)SettingsDialog.findViewById(R.id.GpsTimeSelectedtextView);
					 final TextView selectedBatteryTimetextView=(TextView)SettingsDialog.findViewById(R.id.selectedBatteryTimetextView);
					 ImageView CloseimageView=(ImageView)SettingsDialog.findViewById(R.id.CloseimageView);
					 SeekBar AutoSyncseekBar=(SeekBar)SettingsDialog.findViewById(R.id.AutoSyncseekBar);
					 final TextView AutoSyncTimetextView=(TextView)SettingsDialog.findViewById(R.id.AutoSyncTimetextView);
					 
					 WSURLEdittext=(EditText)SettingsDialog.findViewById(R.id.WSURLEdittext);
					 Button SubmitURLbutton=(Button)SettingsDialog.findViewById(R.id.SubmitURLbutton);
					 
					 
					 
					 AutoSyncseekBar.setProgress(Commondata.AutoSyncTime - 1);
					 AutoSyncTimetextView.setText(Commondata.AutoSyncTime+ " min");
					 GPSTrackingTimeIntervalseekBar.setProgress(Commondata.GpsFetchTime);
					 GpsTimeSelectedtextView.setText( Commondata.GpsFetchTime+ " Sec");
					 BatteryUsageLevelIndicatorseekBar.setProgress(Commondata.BatteryUsage);
					 selectedBatteryTimetextView.setText(Commondata.BatteryUsage+" %");
					 
					 WSURLEdittext.setText(Commondata.PostURL);
					 WSURLEdittext.setSelection(Commondata.PostURL.toString().trim().length());
					 
					 SubmitURLbutton.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							try
							{
								if(WSURLEdittext.getText().toString().trim().equals(""))
								{
									ErrorDialog("PostURL is Empty. Do you Want to update", myContext,1);
								}
								else
								{
									ContentValues aValues=new ContentValues();
									aValues.put("ProductURL",WSURLEdittext.getText().toString().trim());
									database.update("Settings", aValues, null, null);
									Commondata.PostURL=WSURLEdittext.getText().toString().trim();
									aValues=null;
									ErrorDialog("Your Post Url Saved Succeessfully !", myContext,0);
								}
								
							}
							catch (Exception e) {
								Commondata.WriteLog("HomeActivity", "SettingsDialog(SubmitURLbutton.setOnClickListener)", e.toString());
							}
							
						}
					});
					 
					 AutoSyncseekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
						
						@Override
						public void onStopTrackingTouch(SeekBar arg0) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onStartTrackingTouch(SeekBar arg0) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
							// TODO Auto-generated method stub
							
							AutoSyncTimetextView.setText(""+(arg1 +1)+ " min");
							Commondata.AutoSyncTime=(arg1 +1);
							ContentValues aValues=new ContentValues();
							aValues.put("AutoSync", (arg1 +1));
							database.update("Settings", aValues, null, null);
							aValues=null;
							
						}
					});
					 
					 GPSTrackingTimeIntervalseekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
						
						@Override
						public void onStopTrackingTouch(SeekBar arg0) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onStartTrackingTouch(SeekBar arg0) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
							// TODO Auto-generated method stub
							
							GpsTimeSelectedtextView.setText(""+arg1+ " Sec");
							Commondata.GpsFetchTime=arg1;
							ContentValues aValues=new ContentValues();
							aValues.put("GPSFetchInterval", arg1);
							database.update("Settings", aValues, null, null);
							aValues=null;
							
						}
					});
					 
					 BatteryUsageLevelIndicatorseekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
							
							@Override
							public void onStopTrackingTouch(SeekBar arg0) {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void onStartTrackingTouch(SeekBar arg0) {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
								// TODO Auto-generated method stub
								selectedBatteryTimetextView.setText(""+arg1 +" %");
								Commondata.BatteryUsage=arg1;
								ContentValues aValues=new ContentValues();
								aValues.put("BatteryUsage", arg1);
								database.update("Settings", aValues, null, null);
								aValues=null;
							}
						});
					 
					 CloseimageView.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							SettingsDialog.dismiss();
						}
					});
					 
					
			
				   
				}catch (Exception e) 
				{				
					Commondata.WriteLog("HomeActivity", "SettingsDialog", e.toString());
				}
			}
		 
		  
		  private void ErrorDialog(final String errormsg,Context aContext,final int Flag)
			{

				try
				{
					if(UserConformationDialog.isShowing())
					{
						UserConformationDialog.dismiss();
					}
					

					UserConformationDialog=new Dialog((Activity)aContext,R.style.UserDialog);
				    UserConformationDialog.setTitle("Message");
				    UserConformationDialog.setCancelable(false);
				    UserConformationDialog.setContentView(R.layout.error_dialog);
				    UserConformationDialog.show();
				 
				     TextView TitleisplayText=(TextView)UserConformationDialog.findViewById(R.id.TitleisplayText);
					 TextView ConformationTextview=(TextView)UserConformationDialog.findViewById(R.id.ConformationdisplayText);
					 Button ConformationOkButton=(Button)UserConformationDialog.findViewById(R.id.ConformationOkbutton);
					 Button InvisibleButton=(Button)UserConformationDialog.findViewById(R.id.Transferbutton);
					 Button ConformationCancelButton=(Button)UserConformationDialog.findViewById(R.id.ConformationCancelButton);
					 
					 InvisibleButton.setVisibility(View.GONE);
					 ConformationCancelButton.setVisibility(View.GONE);
					 
					 TitleisplayText.setText("Message");
					 
					 ConformationTextview.setText(errormsg);
					 ConformationTextview.setMovementMethod(new ScrollingMovementMethod());
					 if(errormsg.length()>65)
					 {
						 LinearLayout.LayoutParams ConformLayoutParam=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,150);
						 ConformationTextview.setLayoutParams(ConformLayoutParam);
					 }
				 
					 
					 ConformationOkButton.setText("Ok");
					 ConformationCancelButton.setText("No");
					 if(Flag==1)
					 {
						 ConformationCancelButton.setVisibility(View.VISIBLE);
						 ConformationOkButton.setText("Yes");
						 ConformationCancelButton.setText("No");
					 }
				
					 ConformationOkButton.setOnClickListener(new OnClickListener() 
					 {
						@Override
						public void onClick(View arg0) 
						{
							UserConformationDialog.dismiss();
							if(Flag==1)
							{
								ContentValues aValues=new ContentValues();
								aValues.put("ProductURL",WSURLEdittext.getText().toString().trim());
								database.update("Settings", aValues, null, null);
								Commondata.PostURL=WSURLEdittext.getText().toString().trim();
								aValues=null;
								ErrorDialog("Your Post Url Saved Succeessfully !", myContext,0);
							}
							
						}
					});
					 
					ConformationCancelButton.setOnClickListener(new OnClickListener() 
					{
					@Override
					public void onClick(View arg0) 
					{
						UserConformationDialog.dismiss();
					}
				}); 
			
				   
				}catch (Exception e) 
				{				
					Commondata.WriteLog("HomeActivity", "ErrorDialog", e.toString());
				}
			}
		 

			private void SetGPSSyncInfo()
			{
				try
				 {
					RemoveHandler();
					Autohandler = new Handler();
					final long Time=Commondata.GpsFetchTime*1000;
					AutoRunnable = new Runnable() 
					 {
						public void run() 
			             {
			            	 GPSFetching();
			            	 Autohandler.postDelayed(AutoRunnable, Time); 
			            	 
			             }

						 
			        };
			        Autohandler.postDelayed(AutoRunnable, Time); 
				
					
				 }catch (Exception e) 
				 {
					 Commondata.WriteLog("HomeActivity", "SetGPSSyncInfo", e.toString());
				 }		
			}

			
			private void RemoveHandler()
			{
				try
				{
					if(Autohandler!=null)
					{
						Autohandler.removeCallbacks(AutoRunnable);
//						sensorManager.unregisterListener(this);
//						onDestroy();
						
					}	
				}catch (Exception e) 
				 {
					Commondata.WriteLog("HomeActivity", "RemoveHandler", e.toString());
				 }	
			}
			
			
			private void LocationAutoSync()
			{
				try
				 {
					RemoveSyncHandler();
					Synchandler = new Handler();
					final long Time=Commondata.AutoSyncTime*60*1000;
					SyncRunnable = new Runnable() 
					 {
						public void run() 
			             {
			            	if(IsSyncExecuting==false && !Commondata.PostURL.equalsIgnoreCase(""))
			            	{
			            		new LocationSyncAsyncTask().execute();
			            	}
			            	 Synchandler.postDelayed(SyncRunnable, Time); 
			            	 
			             }

						 
			        };
			        Synchandler.postDelayed(SyncRunnable, Time); 
				
					
				 }catch (Exception e) 
				 {
					 Commondata.WriteLog("HomeActivity", "LocationAutoSync", e.toString());
				 }		
			}

			
			private void RemoveSyncHandler()
			{
				try
				{
					if(Synchandler!=null)
					{
						Synchandler.removeCallbacks(SyncRunnable);
						
					}	
				}catch (Exception e) 
				 {
					Commondata.WriteLog("HomeActivity", "RemoveSyncHandler", e.toString());
				 }	
			}
			
			private void StopAccelerometer()
			{
				try
				{
					sensorManager.unregisterListener(this);
				}
				catch (Exception e) 
				 {
					Commondata.WriteLog("HomeActivity", "StopAccelerometer", e.toString());
				 }	
			}
			
			@SuppressWarnings("deprecation")
			@Override
			protected void onDestroy() {
				// TODO Auto-generated method stub
				try
				{
					RemoveHandler();
					RemoveSyncHandler();
					unregisterReceiver(mBatInfoReceiver);
					
					if(database!=null)
					{
						if (database.isOpen()) {
						database.close();};
							database=null;}
				}
				catch (Exception e) {
					Commondata.WriteLog("HomeActivity", "onDestroy", e.toString());
				}
				
				
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
			  @Override
			  protected void onResume() {
			    super.onResume();
			    // register this class as a listener for the orientation and
			    // accelerometer sensors
			    try
			    {
			    	if(sensorManager!=null)
			    	{
			    		if(StartStopFlag==1)
			    		{
			    			sensorManager.registerListener(this,
							        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
							        SensorManager.SENSOR_DELAY_NORMAL);
			    		}
			    		
			    	}
					    

				    this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
			    }
			    catch (Exception e) {
					Commondata.WriteLog("HomeActivity", "onResume", e.toString());
				}
			    
			  }

			  @Override
			  protected void onPause() {
			    // unregister listener
				  super.onPause();
				  try
				  {
					  if(sensorManager!=null)
					  {
						  sensorManager.unregisterListener(this);
					  }
					    	
					    
					    unregisterReceiver(mBatInfoReceiver);
				  }
				  catch (Exception e) {
						Commondata.WriteLog("HomeActivity", "onPause", e.toString());
					}
			   
			    
			  }
		  
			  private class LocationSyncAsyncTask extends AsyncTask<Void, Integer, Void>
				{
				 private ProgressDialog dialog;
				 public LocationSyncAsyncTask() 
			        {
			            dialog = new ProgressDialog(HomeActivity.this);
			            dialog.setCancelable(false);	            
			        }
				 	@Override
					protected void onPreExecute() 
					{	
				 		IsSyncExecuting=true;
//			 			this.dialog.show();
						super.onPreExecute();
					}
					@Override
					protected Void doInBackground(Void... arg0) 
					{				
						try
						{
							if(Commondata.CheckWifiConnection(HomeActivity.this))
							{
//								SaveTrackerDetails();
								SaveTrackerDetailsinJSON();
							}
							else
							{
								OnMethodError=true;
								OnMethodErrorString="No Internet Connection ";
							}
						}
						catch (Exception e) {
							Commondata.WriteLog("HomeActivity", "LocationSyncAsyncTask(doInBackground)", e.toString());
						}
						
						
						return null;
					}
					@Override
					protected void onPostExecute(Void result) 
					{
						if (dialog.isShowing())  { dialog.dismiss(); }	  
						try
						{
							if(OnMethodErrorString.equalsIgnoreCase("No Internet Connection "))
							{
								ErrorDialog("Check Internet Connection", myContext,0);
							}
							IsSyncExecuting=false;
							super.onPostExecute(result);
						}
						catch (Exception e) {
							Commondata.WriteLog("HomeActivity", "LocationSyncAsyncTask(onPostExecute)", e.toString());
						}
						
					}
				}
			  
			  private void SaveTrackerDetails()
			     {
			     	try
			     	{
			     		String WebServiceFetchResult="";
			         	OnMethodError=false;
			         	OnMethodErrorString="";
			         	
			         	String GPSData="",AccelerometerData="";
//			         	 ' GPSInfo ==> Latitude|Longitude|Time$
//			            ' AccelerometerInfo ==> XCoordinate|YCoordinate|ZCoordinate|Time$
			         	
			         	WebServiceMethods aWebServiceMethods=new WebServiceMethods();	 
			         	
			         	database.execSQL("Update DeviceGPSLocation Set SyncFlag=1");
			         	database.execSQL("Update DeviceAccelerometerDetails Set SyncFlag=1");
			         	
			         	ArrayList<DeviceGPSLocation> GPS_Arraylist;
			         	ArrayList<DeviceAccelerometerDetails> Accelerometer_Arraylist;
			         	
			         	DeviceAccelerometerDetailsData accelerometerDetailsData=new DeviceAccelerometerDetailsData();
			         	DeviceGPSLocationData aGpsLocationData=new DeviceGPSLocationData();
			         	GPS_Arraylist=aGpsLocationData.getDeviceGPSLocation(database, " SyncFlag=1", null);
			         	Accelerometer_Arraylist=accelerometerDetailsData.getDeviceAccelerometerDetails(database," SyncFlag=1", null);
			         	accelerometerDetailsData=null;
			         	aGpsLocationData=null;
			         	
			         	for(int i=0;i<GPS_Arraylist.size();i++)
			         	{
			         		GPSData=GPSData+"$"+GPS_Arraylist.get(i).getLatitude()+"|"+GPS_Arraylist.get(i).getLongitude()+"|"+GPS_Arraylist.get(i).getTime();
			         	}
			         	
			         	for(int i=0;i<Accelerometer_Arraylist.size();i++)
			         	{
			         		AccelerometerData=AccelerometerData+"$"+Accelerometer_Arraylist.get(i).getXCoordinate()+"|"+Accelerometer_Arraylist.get(i).getYCoordinate()+"|"+Accelerometer_Arraylist.get(i).getZCoordinate()+"|"+Accelerometer_Arraylist.get(i).getTime();
			         	}
			         	
			         	if(!GPSData.equals(""))
			     		{
			         		GPSData=GPSData.substring(1, GPSData.length());
			     		}
			         	
			         	if(!AccelerometerData.equals(""))
			     		{
			         		AccelerometerData=AccelerometerData.substring(1, AccelerometerData.length());
			     		}
			         	
			         	GPS_Arraylist=null;
			         	Accelerometer_Arraylist=null;
						
						
			     		if(!GPSData.equals("") || !AccelerometerData.equals(""))
			     		{
			     			WebServiceFetchResult=aWebServiceMethods.GPSTrackerInfo(GPSData, AccelerometerData, database);
			     		}
			     		
			     		if (WebServiceFetchResult.contains("completed successfully"))
			     		{
			     			database.execSQL("Delete From DeviceGPSLocation Where SyncFlag=1");
				         	database.execSQL("Delete From DeviceAccelerometerDetails Where SyncFlag=1");
			     		}
			     		else
			     		{
			     			    OnMethodError=true;
					   	    	OnMethodErrorString="SaveTrackerDetails "+WebServiceFetchResult.toString();
			     		}
			     		
			     	
			         
			     	}catch (Exception e) 
			     	{
			 			Commondata.WriteLog("HomeActivity", "SaveTrackerDetails", e.toString());
			     		OnMethodError=true;
			     		OnMethodErrorString=e.toString();
			 		}
			     }
			  
			  
			  private void SaveTrackerDetailsinJSON()
			     {
			     	try
			     	{
			     		String WebServiceFetchResult="";
			         	OnMethodError=false;
			         	OnMethodErrorString="";
			         	
			         	String GPSData="",AccelerometerData="";String WholeString="";
//			         	 ' GPSInfo ==> Latitude|Longitude|Time$
//			            ' AccelerometerInfo ==> XCoordinate|YCoordinate|ZCoordinate|Time$
			         	
			         	WebServiceMethods aWebServiceMethods=new WebServiceMethods();	 
			         	
			         	database.execSQL("Update DeviceGPSLocation Set SyncFlag=1");
			         	database.execSQL("Update DeviceAccelerometerDetails Set SyncFlag=1");
			         	
			         	ArrayList<DeviceGPSLocation> GPS_Arraylist;
			         	ArrayList<DeviceAccelerometerDetails> Accelerometer_Arraylist;
			         	
			         	DeviceAccelerometerDetailsData accelerometerDetailsData=new DeviceAccelerometerDetailsData();
			         	DeviceGPSLocationData aGpsLocationData=new DeviceGPSLocationData();
			         	GPS_Arraylist=aGpsLocationData.getDeviceGPSLocation(database, " SyncFlag=1", null);
			         	Accelerometer_Arraylist=accelerometerDetailsData.getDeviceAccelerometerDetails(database," SyncFlag=1", null);
			         	accelerometerDetailsData=null;
			         	aGpsLocationData=null;
			         	
			         	for(int i=0;i<GPS_Arraylist.size();i++)
			         	{
			         		GPSData=GPSData+",{\"MachineID\":\""+Commondata.DeviceID+"\",\"Latitude\":"+aDecimalFormat.format(Double.parseDouble(GPS_Arraylist.get(i).getLatitude()))+",\"Longitude\":"+aDecimalFormat.format(Double.parseDouble(GPS_Arraylist.get(i).getLongitude()))+",\"Time\":\""+GPS_Arraylist.get(i).getTime()+"\",\"Class\":\"GPSEvent\"}";
//			         		GPSData=GPSData+",{\"MachineID\":\""+Commondata.DeviceID+"\",\"XCoordinate\":"+aDecimalFormat.format(Double.parseDouble(GPS_Arraylist.get(i).getLatitude()))+",\"YCoordinate\":"+aDecimalFormat.format(Double.parseDouble(GPS_Arraylist.get(i).getLongitude()))+",\"ZCoordinate\":0,\"Time\":\""+GPS_Arraylist.get(i).getTime()+"\",\"Class\":\"GPSEvent\"}";
			         	}
			         	
			         	for(int i=0;i<Accelerometer_Arraylist.size();i++)
			         	{
			         		AccelerometerData=AccelerometerData+",{\"MachineID\":\""+Commondata.DeviceID+"\",\"XCoordinate\":"+aDecimalFormat.format(Double.parseDouble(Accelerometer_Arraylist.get(i).getXCoordinate()))+",\"YCoordinate\":"+aDecimalFormat.format(Double.parseDouble(Accelerometer_Arraylist.get(i).getYCoordinate()))+",\"ZCoordinate\":"+aDecimalFormat.format(Double.parseDouble(Accelerometer_Arraylist.get(i).getZCoordinate()))+",\"Time\":\""+Accelerometer_Arraylist.get(i).getTime()+"\",\"Class\":\"AccelerometerEvent\"}";
			         	}
			         	
			         	if(!GPSData.equals(""))
			     		{
			         		GPSData=GPSData.substring(1, GPSData.length());
//			         		GPSData="\"GPSDetails\":["+GPSData+"]";
			         		
			     		}
			         	
			         	if(!AccelerometerData.equals(""))
			     		{
			         		AccelerometerData=AccelerometerData.substring(1, AccelerometerData.length());
//			         		AccelerometerData="\"AccelerometerDetails\":["+AccelerometerData+"]";
			     		}
			         	
			         	GPS_Arraylist=null;
			         	Accelerometer_Arraylist=null;
//						if(!GPSData.equals("") && !AccelerometerData.equals(""))
//						{
//							WholeString="{\"Data\":["+GPSData+","+AccelerometerData+"]}";
//						}
//						else if(!GPSData.equals(""))
//						{
//							WholeString="{\"Data\":["+GPSData+"]}";
//						}
//						else if(!AccelerometerData.equals(""))
//						{
//							WholeString="{\"Data\":["+AccelerometerData+"]}";
//						}
						
						if(!GPSData.equals("") && !AccelerometerData.equals(""))
						{
							WholeString=GPSData+","+AccelerometerData;
						}
						else if(!GPSData.equals(""))
						{
							WholeString=GPSData;
						}
						else if(!AccelerometerData.equals(""))
						{
							WholeString=AccelerometerData;
						}
			     		if(!WholeString.equals(""))
			     		{
			     			 HttpResponse response=makeRequest(Commondata.PostURL, WholeString);
			     			 String responseBody = EntityUtils.toString(response.getEntity());
			     			 String responseText=getContentCharSet(response.getEntity());
			     			 if(responseBody.contains("\"created\":true"))
			     			 {
			     				WebServiceFetchResult = "completed successfully";
			     			 }
			     			 else
			     			 {
			     				WebServiceFetchResult = "Error";
			     			 }
//			     			 if(response!=null)
//			     			 {
//			     				 WebServiceFetchResult = "completed successfully";
//			     			 }
//			     			 else
//			     			 {
//			     				WebServiceFetchResult = "Error";
//			     			 }
			     			
			     		}
			     		
			     		if (WebServiceFetchResult.contains("completed successfully"))
			     		{
			     			database.execSQL("Delete From DeviceGPSLocation Where SyncFlag=1");
				         	database.execSQL("Delete From DeviceAccelerometerDetails Where SyncFlag=1");
			     		}
			     		else
			     		{
			     			    OnMethodError=true;
					   	    	OnMethodErrorString="SaveTrackerDetails "+WebServiceFetchResult.toString();
			     		}
			     		
			     	
			         
			     	}catch (Exception e) 
			     	{
			 			Commondata.WriteLog("HomeActivity", "SaveTrackerDetails", e.toString());
			     		OnMethodError=true;
			     		OnMethodErrorString=e.toString();
			 		}
			     }
			  
			  public static HttpResponse makeRequest(String uri, String json) {
				    try {
				        HttpPost httpPost = new HttpPost(uri);
				        httpPost.setEntity(new StringEntity(json));
				        httpPost.setHeader("Accept", "application/json");
				        httpPost.setHeader("Content-type", "application/json");
				        return new  DefaultHttpClient().execute(httpPost);
				    } catch (UnsupportedEncodingException e) {
				    	Commondata.WriteLog("HomeActivity", "makeRequest", e.toString());
				        e.printStackTrace();
				    } catch (ClientProtocolException e) {
				    	Commondata.WriteLog("HomeActivity", "makeRequest", e.toString());
				        e.printStackTrace();
				    } catch (IOException e) {
				    	Commondata.WriteLog("HomeActivity", "makeRequest", e.toString());
				        e.printStackTrace();
				    }
				    return null;
				}
			  
			  public String getContentCharSet(final HttpEntity entity) throws ParseException {
				  
				  if (entity == null) { throw new IllegalArgumentException("HTTP entity may not be null"); }
				   
				  String charset = null;
				   
				  if (entity.getContentType() != null) {
				   
				  HeaderElement values[] = entity.getContentType().getElements();
				   
				  if (values.length > 0) {
				   
				  NameValuePair param = values[0].getParameterByName("charset");
				   
				  if (param != null) {
				   
				  charset = param.getValue();
				   
				  }
				   
				  }
				   
				  }
				   
				  return charset;
				   
				  }
			  
}
