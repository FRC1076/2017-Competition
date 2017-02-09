package org.usfirst.frc.team1076.robot.subsystems;

import org.strongback.components.Gyroscope;
import org.strongback.components.Motor;

public class DrivetrainWithGyro extends Drivetrain {
    public static double FORWARD_ASSIST_SENSITIVITY = 1.0;
    public static final double FORWARD_ASSIST_MAX_TURN_SPEED = 0.1;
    int previousSign = 0;
    Gyroscope gyro;

    public DrivetrainWithGyro(Motor left, Motor right, Gyroscope gyro) {
        super(left, right);
        this.gyro = gyro;
    }
    
    @Override
    public void arcade(double forward, double rotate) {
        final int sign = (int) Math.signum(forward);
        if (sign != previousSign) {
            gyro.zero();
            previousSign = sign;
        }

        // The normGyroRate should be equal to rotate under ideal conditions
        final double normGyroAngle = Math.signum(forward) * FORWARD_ASSIST_SENSITIVITY * gyro.getAngle() / 360; 
        // TODO: Finish this left/right calculation
        double left = forward + rotate;
        double right = forward - rotate;

        if (shouldForwardAssist(rotate)) {
            if (normGyroAngle > 0) { // Drifting right
                left = left * (1 - Math.abs(normGyroAngle));
                //            System.out.println("Left Corrected: " + left);
            } else {
                // left = (Math.abs(left) * (1 + Math.abs(normGyroAngle)));
                right = right * (1 - Math.abs(normGyroAngle));
                //            System.out.println("Right Corrected: " + right);
            }
        } else {
            //  turning
            gyro.zero();
        }

        setLeftSpeed(left);
        setRightSpeed(right);
    }
    
    public boolean shouldForwardAssist(double rotate) {
        return Math.abs(rotate) < FORWARD_ASSIST_MAX_TURN_SPEED;
    }
    
    public Gyroscope getGyro() {
        return gyro;
    }
}
