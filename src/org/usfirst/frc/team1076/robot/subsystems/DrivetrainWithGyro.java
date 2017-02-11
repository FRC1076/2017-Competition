package org.usfirst.frc.team1076.robot.subsystems;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

import org.strongback.Strongback;
import org.strongback.components.Gyroscope;
import org.strongback.components.Motor;
import org.strongback.control.PIDController;
import org.strongback.control.SoftwarePIDController;
import org.strongback.control.SoftwarePIDController.SourceType;

public class DrivetrainWithGyro extends Drivetrain {
    public static double FORWARD_ASSIST_SENSITIVITY = 1.0;
    public static final double FORWARD_ASSIST_MAX_TURN_SPEED = 0.1;
    
    public double P = 0;
    public double I = 0;
    public double D = 0;
    
    
    public double computedValue;
    int previousSign = 0;
    Gyroscope gyro;
    SoftwarePIDController PID;
    
    public DrivetrainWithGyro(Motor left, Motor right, Gyroscope gyro) {
        super(left, right);
        this.gyro = gyro;
        PID = new SoftwarePIDController(SourceType.DISTANCE,
                                        ()->gyro.getAngle() / 45,
                                        this::getPIDOutputValue);
        PID.enable();
        updateProfile();
//        debugPID();
    }
    
    @Override
    public void arcade(double forward, double rotate) {
        double left = forward + rotate;
        double right = forward - rotate;
        if (shouldForwardAssist(rotate)) {
            PID.computeOutput();
            if (computedValue > 0) { // Drifting right 
                left = left * (1 - Math.abs(computedValue));
            } else {
                right = right * (1 - Math.abs(computedValue));
            }
        } else {
            gyro.zero();
        }
        
        leftMotor.setSpeed(left);
        rightMotor.setSpeed(right);
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
    }
}
