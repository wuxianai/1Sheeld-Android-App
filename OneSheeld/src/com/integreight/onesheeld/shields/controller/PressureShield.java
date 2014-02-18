package com.integreight.onesheeld.shields.controller;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;

import com.integreight.firmatabluetooth.ShieldFrame;
import com.integreight.onesheeld.enums.UIShield;
import com.integreight.onesheeld.utils.ControllerParent;

public class PressureShield extends ControllerParent<PressureShield> implements
		SensorEventListener {
	private SensorManager mSensorManager;
	private Sensor mPressure;
	private PressureEventHandler eventHandler;
	private ShieldFrame frame;
	Handler handler;
	int PERIOD = 1000;
	boolean flag = false;
	boolean isHandlerLive = false;

	private final Runnable processSensors = new Runnable() {
		@Override
		public void run() {
			// Do work with the sensor values.

			flag = true;
			// The Runnable is posted to run again here:
			handler.postDelayed(this, PERIOD);
		}
	};

	public PressureShield() {
	}

	public PressureShield(Activity activity, String tag) {
		super(activity, tag);
		getApplication().getAppFirmata().initUart();
	}

	@Override
	public ControllerParent<PressureShield> setTag(String tag) {
		getApplication().getAppFirmata().initUart();

		mSensorManager = (SensorManager) getApplication().getSystemService(
				Context.SENSOR_SERVICE);
		mPressure = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

		return super.setTag(tag);
	}

	public void setPressureEventHandler(PressureEventHandler eventHandler) {
		this.eventHandler = eventHandler;
		CommitInstanceTotable();
	}

	@Override
	public void onNewShieldFrameReceived(ShieldFrame frame) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if (flag) {
			frame = new ShieldFrame(UIShield.PRESSURE_SHIELD.getId(), (byte) 0,
					ShieldFrame.DATA_SENT);
			frame.addByteArgument((byte) Math.round(event.values[0]));
			activity.getThisApplication().getAppFirmata()
					.sendShieldFrame(frame);

			Log.d("Sensor Data of X", event.values[0] + "");
			eventHandler.onSensorValueChangedFloat(event.values[0]+"");

			//
			flag = false;
		}

	}

	// Register a listener for the sensor.
	public void registerSensorListener() {
		if (mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null) {
			// Success! There's sensor.
			if (!isHandlerLive) {
				handler = new Handler();
				mSensorManager.registerListener(this, mPressure,
						SensorManager.SENSOR_DELAY_NORMAL);
				handler.post(processSensors);
				eventHandler.isDeviceHasSensor(true);
				isHandlerLive = true;
			} else {
				Log.d("Your Sensor is registered", "Pressure");
			}
		} else {
			// Failure! No sensor.
			Log.d("Device dos't have Sensor ", "Pressure");
			eventHandler.isDeviceHasSensor(false);

		}
	}

	// Unregister a listener for the sensor .
	public void unegisterSensorListener() {
		// mSensorManager.unregisterListener(this);
		if (mSensorManager != null && handler != null && mPressure != null) {

			mSensorManager.unregisterListener(this, mPressure);
			mSensorManager.unregisterListener(this);
			handler.removeCallbacks(processSensors);
			handler.removeCallbacksAndMessages(null);
			isHandlerLive = false;
		}
	}

	public static interface PressureEventHandler {

		void onSensorValueChangedFloat(String value);

		void onSensorValueChangedByte(String value);

		void isDeviceHasSensor(Boolean hasSensor);

	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		this.unegisterSensorListener();

	}

}