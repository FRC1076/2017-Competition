package org.usfirst.frc.team1076.robot.subsystems;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

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
    
    int previousSign = 0;
    Gyroscope gyro;
    SoftwarePIDController PID;
    
    public void adjustParameters(double value) {
        if (value > 0) { // Drifting right 
            leftMotor.setSpeed(leftMotor.getSpeed() * (1 - Math.abs(value)));  
        } else {  
            rightMotor.setSpeed(rightMotor.getSpeed() * (1 - Math.abs(value)));  
        } 

    }
    
    public DrivetrainWithGyro(Motor left, Motor right, Gyroscope gyro) {
        super(left, right);
        this.gyro = gyro;
        PID = new SoftwarePIDController(SourceType.DISTANCE,
                                        ()->gyro.getAngle() / 45,
                                        this::adjustParameters);
        PID.enable();
        PID.withProfile(0, P, I, D);
    }
    
    public void updateProfile() {
        PID.withProfile(0, P, I, D);
    }
    
    @Override
    public void arcade(double forward, double rotate) {
        double left = forward + rotate;
        double right = forward - rotate;
        leftMotor.setSpeed(left);
        rightMotor.setSpeed(right);
        if (shouldForwardAssist(rotate)) {
            PID.computeOutput();
        } else {
            //  turning
            gyro.zero();
        }
    }
    
    public boolean shouldForwardAssist(double rotate) {
        return Math.abs(rotate) < FORWARD_ASSIST_MAX_TURN_SPEED;
    }
    
    public Gyroscope getGyro() {
        return gyro;
    }
}
