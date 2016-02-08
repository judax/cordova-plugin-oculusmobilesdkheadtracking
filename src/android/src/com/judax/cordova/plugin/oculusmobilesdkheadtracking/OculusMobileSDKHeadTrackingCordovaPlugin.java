package com.judax.cordova.plugin.oculusmobilesdkheadtracking;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONException;
import org.json.JSONObject;

import com.judax.oculusmobilesdkheadtracking.OculusMobileSDKHeadTracking;
import com.judax.oculusmobilesdkheadtracking.OculusMobileSDKHeadTrackingData;
import com.judax.oculusmobilesdkheadtracking.OculusMobileSDKHeadTrackingListener;

public class OculusMobileSDKHeadTrackingCordovaPlugin extends CordovaPlugin
{
	private CallbackContext eventCallbackContext = null;
	private CallbackContext updateCallbackContext = null;
	private JSONObject eventJSONObject = new JSONObject();
	private JSONObject startJSONObject = new JSONObject();
	private JSONObject dataJSONObject = new JSONObject();
	private JSONObject orientationJSONObject = new JSONObject();
	private JSONObject linearVelocityJSONObject = new JSONObject();
	private JSONObject linearAccelerationJSONObject = new JSONObject();
	private JSONObject angularVelocityJSONObject = new JSONObject();
	private JSONObject angularAccelerationJSONObject = new JSONObject();
	private OculusMobileSDKHeadTracking oculusMobileSDKHeadTracking = new OculusMobileSDKHeadTracking();
	private OculusMobileSDKHeadTrackingListener oculusMobileSDKHeadTrackingListener = new OculusMobileSDKHeadTrackingListener()
	{
		@Override
		public void headTrackingUpdated(OculusMobileSDKHeadTracking oculusMobileSDKHeadTracking, OculusMobileSDKHeadTrackingData data)
		{
			if (updateCallbackContext != null)
			{
				try
				{
					dataJSONObject.put("timeStamp", Double.valueOf(data.timeStamp));
					orientationJSONObject.put("x", data.orientationX);
					orientationJSONObject.put("y", data.orientationY);
					orientationJSONObject.put("z", data.orientationZ);
					orientationJSONObject.put("w", data.orientationW);
					linearVelocityJSONObject.put("x", data.linearVelocityX);
					linearVelocityJSONObject.put("y", data.linearVelocityY);
					linearVelocityJSONObject.put("z", data.linearVelocityZ);
					linearAccelerationJSONObject.put("y", data.linearAccelerationY);
					linearAccelerationJSONObject.put("x", data.linearAccelerationX);
					linearAccelerationJSONObject.put("z", data.linearAccelerationZ);
					angularVelocityJSONObject.put("x", data.angularVelocityX);
					angularVelocityJSONObject.put("y", data.angularVelocityY);
					angularVelocityJSONObject.put("z", data.angularVelocityZ);
					angularAccelerationJSONObject.put("y", data.angularAccelerationY);
					angularAccelerationJSONObject.put("x", data.angularAccelerationX);
					angularAccelerationJSONObject.put("z", data.angularAccelerationZ);
					updateCallbackContext.success(dataJSONObject);
				}
				catch(JSONException e) 
				{
					// TODO: should we really notify about this error? Creating the data for the error notification can also throw a JSONException
	//				eventJSONObject.put("name", "error");
	//				eventJSONObject.put("data", "\"" + e.getMessage() + "\"");
	//				eventCallbackContext.error(eventJSONObject);
					e.printStackTrace();
				}
			}
		}
		
		@Override
		public void headTrackingStarted(OculusMobileSDKHeadTracking oculusMobileSDKHeadTracking, OculusMobileSDKHeadTrackingData data)
		{
			// If for some reason the started event is called after the call start callback context has been assigned, call the start callback
			if (eventCallbackContext != null) 
			{
				callStartEventCallback();
			}
		}
		
		@Override
		public void headTrackingError(OculusMobileSDKHeadTracking oculusMobileSDKHeadTracking, String errorMessage)
		{
		}
	};
	
	private void callStartEventCallback()
	{
		try
		{
			OculusMobileSDKHeadTrackingData data = oculusMobileSDKHeadTracking.getData();
			startJSONObject.put("xFOV", data.xFOV);
			startJSONObject.put("yFOV", data.yFOV);
			startJSONObject.put("interpupillaryDistance", data.interpupillaryDistance);
			eventJSONObject.put("name", "start");
			eventJSONObject.put("data", startJSONObject);
			eventCallbackContext.success(eventJSONObject);
		}
		catch(JSONException e)
		{
			// TODO: should we really notify about this error? Creating the data for the error notification can also throw a JSONException
//			eventJSONObject.put("name", "error");
//			eventJSONObject.put("data", "\"" + e.getMessage() + "\"");
//			eventCallbackContext.error(eventJSONObject);
			e.printStackTrace();
		}
	}
		
	@Override
	public void initialize(final CordovaInterface cordova, CordovaWebView webview)
	{
		super.initialize(cordova, webview);
		try
		{
			dataJSONObject.put("orientation", orientationJSONObject);
			dataJSONObject.put("linearVelocity", linearVelocityJSONObject);
			dataJSONObject.put("linearAcceleration", linearAccelerationJSONObject);
			dataJSONObject.put("angularVelocity", angularVelocityJSONObject);
			dataJSONObject.put("angularAcceleration", angularAccelerationJSONObject);
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
		oculusMobileSDKHeadTracking.start(cordova.getActivity());
		webview.addView(oculusMobileSDKHeadTracking.getView(), 1, 1);
	}
	
	@Override
	public void onPause(boolean multitasking)
	{
		super.onPause(multitasking);
		oculusMobileSDKHeadTracking.pause();
	}
	
	@Override
	public void onResume(boolean multitasking)
	{
		super.onResume(multitasking);
		oculusMobileSDKHeadTracking.resume();
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		oculusMobileSDKHeadTracking.stop();
	}

	@Override
	public boolean execute(String action, String rawArgs,	CallbackContext callbackContext) throws JSONException
	{
		boolean valid = false;
		if (action.equals("start"))
		{
			eventCallbackContext = callbackContext;
			// If the oculus mobile sdk head tracking has already started, call the callback
			if (oculusMobileSDKHeadTracking.hasStarted())
			{
				callStartEventCallback();
			}
		}
		else if (action.equals("setDataUpdateCallback"))
		{
			updateCallbackContext = callbackContext;
		}
		return valid;
	}
}
