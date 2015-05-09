package com.example.tracker;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class wsconnector 
{

	 public static SoapObject ConnectWS(String aSOAPUrl,String aMethodName,String[] aMembers,String[] aValues)
	    {
	    	SoapObject result = null;
	     	String SOAPUrl      = aSOAPUrl;
	    	String NAMESPACE = "http://tempuri.org/";
	    	String MethodName=aMethodName;
	    	@SuppressWarnings("unused")
	    	String res="";
	    	        
	    		SoapObject Request = new SoapObject(NAMESPACE, MethodName);
	    		  try    
	    	         { 
	    	        if (aMembers!=null)
	    	        {
		    	        for(int i=0;i<aMembers.length;i++)
		    	        {
		    	        	  Request.addProperty(aMembers[i],aValues[i]) ;	
		    	        }
	    	        }
	    	      
	    	        SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	    	        soapEnvelope.dotNet = true;
	    	        soapEnvelope.setOutputSoapObject(Request);
	    	        HttpTransportSE httpTransport = new HttpTransportSE(SOAPUrl,60000);
	    	      
	    	        httpTransport.call(NAMESPACE + MethodName , soapEnvelope);
	    	        
	    	        result = (SoapObject)soapEnvelope.getResponse(); 
	    	      
	    	         }
	    	        catch(Exception ex)
	    	        {
	    	        	String Errorstring="Error : ConnectWS("+MethodName+")"+String.valueOf(ex);
//	    	        	android.util.Log.e("Error ConnectWS("+MethodName+")",String.valueOf(ex) );
	    	        	SoapObject ErrorResult = new SoapObject(NAMESPACE,MethodName); 
	    	        	ErrorResult.addProperty("Error: ",Errorstring);
	    	        	result=ErrorResult;
	    	        }
	    	        return result;
	    }
	 
}
