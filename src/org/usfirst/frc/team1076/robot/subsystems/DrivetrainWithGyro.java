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
    
    public double P = 0.2;
    public double I = 0.0;
    public double D = 0.0;
    
    
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
        PID.withInputRange(-1.0, 1.0);
        PID.enable();
        updateProfile();
//        debugPID();
    }
    double lastGyroRate;
    @Override
    public void arcade(double forward, double rotate) {
        double left = forward + rotate;
        double right = forward - rotate;
        if (shouldForwardAssist(rotate) && Math.abs(lastGyroRate) < 0.5) {
            PID.computeOutput();
            left += computedValue;
            right -= computedValue;
//            if (computedValue > 0) {
////                Strongback.logger().debug("Right drift");
//                if (forward > 0) {
//                    left = left  - Math.abs(computedValue);
//                } else {
//                    right = right - Math.abs(computedValue);
//                }
//            } else {
////                Strongback.logger().debug("Left drift");
//                if (forward > 0) {
//                    right = right - Math.abs(computedValue);
//                } else {
//                    left = left - Math.abs(computedValue);
//                }
//            }
            // Prevent the PID Controller from accidentally kicking us out of this
            lastGyroRate = 0;
        } else {
            gyro.zero();
            PID.withTarget(0);
            lastGyroRate = gyro.getRate();            
        }
        setLeftSpeed(left);
        setRightSpeed(right);
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
