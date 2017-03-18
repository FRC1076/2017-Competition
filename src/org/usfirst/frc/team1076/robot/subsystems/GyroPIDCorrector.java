package org.usfirst.frc.team1076.robot.subsystems;

import org.strongback.Strongback;
import org.strongback.components.Gyroscope;
import org.strongback.control.SoftwarePIDController;
import org.strongback.control.SoftwarePIDController.SourceType;

public class GyroPIDCorrector implements ArcadeCorrector {
    public static final double FORWARD_ASSIST_MAX_TURN_SPEED = 0.1;
    
    public double P = 0.0;
    public double I = 0.0;
    public double D = 0.0;
    
    double lastGyroRate;
    public double computedValue;
    int previousSign = 0;
    Gyroscope gyro;
    SoftwarePIDController PID;
    
    
    public GyroPIDCorrector(Gyroscope gyro) {
        this.gyro = gyro;
        PID = new SoftwarePIDController(SourceType.DISTANCE,
                                        ()->gyro.getAngle() / 45,
                                        this::getPIDOutputValue);
        PID.withInputRange(-1.0, 1.0);
        PID.enable();
        updateProfile();
    }
    
    public MotorOutput getCorrection(double forward, double rotate) {
        double left = forward + rotate;
        double right = forward - rotate;
        // The Math.abs(lastGyroRate) < 0.5 is used to prevent exiting the 
        // PID loop through its own corrections. This means that once inside the
        // way to exit the PID mode is to make shouldForwardAssist false (aka: turning manually) 
        if (shouldForwardAssist(rotate) && Math.abs(lastGyroRate) < 0.5) {
            PID.computeOutput();
            if (forward != 0) {
                left += computedValue;
                right -= computedValue;
            }
            // Prevent the PID Controller from accidentally kicking us out of this
            lastGyroRate = 0;
        } else {
            gyro.zero();
            PID.withTarget(0);
            lastGyroRate = gyro.getRate();            
        }
        return new MotorOutput(left, right);
    }
    
    public void getPIDOutputValue(double value) {
        this.computedValue = value;
    }
    
    public void updateProfile() {
        PID.withProfile(0, P, I, D);
    }
    
    public void debugPID() {
        Strongback.logger().info("P" + PID.getGainsForCurrentProfile().getP());
        Strongback.logger().info("I" + PID.getGainsForCurrentProfile().getI());
        Strongback.logger().info("D" + PID.getGainsForCurrentProfile().getD());
    }
    
    public boolean shouldForwardAssist(double rotate) {
        return Math.abs(rotate) < FORWARD_ASSIST_MAX_TURN_SPEED;
    }
    
    public Gyroscope getGyro() {
        return gyro;
    }}
