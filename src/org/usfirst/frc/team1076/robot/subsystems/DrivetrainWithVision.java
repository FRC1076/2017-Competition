package org.usfirst.frc.team1076.robot.subsystems;

import org.strongback.Strongback;
import org.strongback.components.Motor;
import org.strongback.control.SoftwarePIDController;
import org.strongback.control.SoftwarePIDController.SourceType;
import org.usfirst.frc.team1076.robot.vision.VisionReceiver;

public class DrivetrainWithVision extends Drivetrain {
    public static final int ERROR_TRESHHOLD = 5;
    public double VISION_NORMAL = 45.0;
    public double P = 0.0;
    public double I = 0.0;
    public double D = 0.0;
    
    public double computedValue;
    int previousSign = 0;
    VisionReceiver receiver;
    SoftwarePIDController PID;
    
    public DrivetrainWithVision(Motor left, Motor right, VisionReceiver receiver) {
        super(left, right);
        this.receiver = receiver;
        PID = new SoftwarePIDController(SourceType.DISTANCE,
                                        ()->receiver.getData().getHeading()/VISION_NORMAL,
                                        this::getPIDOutputValue);
        PID.withInputRange(-1.0, 1.0);
        PID.withTarget(0);
        PID.enable();
        updateProfile();
        
//        debugPID();
    }
    
    @Override
    public void arcade(double forward, double rotate) {
        double left = forward;
        double right = forward;
        receiver.receive();
        // Only do PID if we're reasonably sure that our data is up to date
//        Strongback.logger().info("Error count: " + receiver.getData().getErrorCount());
        if (receiver.getData().getErrorCount() < ERROR_TRESHHOLD) {
            PID.computeOutput();
            left += computedValue;
            right -= computedValue;
//            Strongback.logger().info("PID change: " + computedValue + " | Heading: " + receiver.getData().getHeading());
        }
        setLeftSpeed(left);
        setRightSpeed(right);
    }
    
    public void getPIDOutputValue(double value) {
        this.computedValue = value;
    }
    
    public void updateProfile() {
        if (P == 0 && I == 0 && D == 0) {
            Strongback.logger().warn("Vision PID is all zero!");
        }
        PID.withProfile(0, P, I, D);
    }
    
    public void debugPID() {
        Strongback.logger().info("P" + PID.getGainsForCurrentProfile().getP());
        Strongback.logger().info("I" + PID.getGainsForCurrentProfile().getI());
        Strongback.logger().info("D" + PID.getGainsForCurrentProfile().getD());
    }
    
    public VisionReceiver getReceiver() {
        return receiver;
    }

}
