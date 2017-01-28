package org.usfirst.frc.team1076.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SmarterDashboard extends SmartDashboard {
	public static boolean putDefaultNumber(String key, double value) {
		double realValue = getNumber(key, value);
		return putNumber(key, realValue);
	}
	
	public static boolean putDefaultString(String key, String value) {
		String realValue = getString(key, value);
		return putString(key, realValue);
	}

	public static boolean putDefaultBoolean(String key, boolean value) {
		boolean realValue = getBoolean(key, value);
		return putBoolean(key, realValue);
	}
}
