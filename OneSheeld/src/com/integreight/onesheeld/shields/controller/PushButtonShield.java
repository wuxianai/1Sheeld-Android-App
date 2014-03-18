package com.integreight.onesheeld.shields.controller;

import android.app.Activity;

import com.integreight.firmatabluetooth.ShieldFrame;
import com.integreight.onesheeld.enums.UIShield;
import com.integreight.onesheeld.model.ArduinoConnectedPin;
import com.integreight.onesheeld.utils.BitsUtils;
import com.integreight.onesheeld.utils.ControllerParent;

public class PushButtonShield extends ControllerParent<PushButtonShield> {
	private int connectedPin;
	private boolean isButtonOn;

	public PushButtonShield() {
		super();
		requiredPinsIndex = 0;
		shieldPins = new String[] { "Push" };
	}

	public PushButtonShield(Activity activity, String tag) {
		super(activity, tag);
	}

	@Override
	public void setConnected(ArduinoConnectedPin... pins) {
		connectedPin = pins[0].getPinID();
		super.setConnected(pins);
	}

	public boolean isButtonOn() {
		return isButtonOn;
	}

	private ShieldFrame sf;
	private byte toggle = 0;
	private static final byte DATA_IN = 0x01;

	public void setButton(boolean isButtonOn) {
		this.isButtonOn = isButtonOn;
		activity.getThisApplication().getAppFirmata()
				.digitalWrite(connectedPin, isButtonOn);
		toggle = isButtonOn ? BitsUtils.setBit(toggle, 1) : BitsUtils.resetBit(
				toggle, 1);
		sf = new ShieldFrame(UIShield.PUSHBUTTON_SHIELD.getId(), DATA_IN);
		sf.addByteArgument(toggle);
		getApplication().getAppFirmata().sendShieldFrame(sf);
		CommitInstanceTotable();
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNewShieldFrameReceived(ShieldFrame frame) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

}
