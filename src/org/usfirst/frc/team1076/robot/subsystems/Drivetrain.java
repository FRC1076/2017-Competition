package org.usfirst.frc.team1076.robot.subsystems;

import org.strongback.command.Requirable;
import org.strongback.components.Motor;

/**
 * This subsystem represents the motors of the robot.
 * Note that this class assumes that all motors are forward facing.
 */
public class Drivetrain implements Requirable {
    double maxSpeed = 1.0;
	public double leftFactor = 1.0;
	public double rightFactor = 1.0;
	Motor leftMotor;
	Motor rightMotor;
	boolean enabled = true;
	
	/**
	 * Constructs on instance of LeftRightMotors, passing it two motor controllers
	 * that represent the left and right motors.
	 * @param leftMotor
	 * @param rightMotor
	 */
	public Drivetrain(Motor left, Motor right) {
		this.leftMotor = left;
		this.rightMotor = right;
	}

	/**
	 * Sets the speed of the left motor.
	 * @param speed in the range -1 to 1 inclusive.
	 */
	public void setLeftSpeed(double speed) {
	    if (enabled) {
	        leftMotor.setSpeed(speed * leftFactor);
	    } else {
	        leftMotor.setSpeed(0);
	    }
	}

	/**
	 * Sets the speed of the right motor.
	 * @param speed in the range -1 to 1 inclusive.
	 */
	public void setRightSpeed(double speed) {
	    if (enabled) {
	        rightMotor.setSpeed(speed * rightFactor);
	    } else {
	        rightMotor.setSpeed(0);
	    }
		
	}
	
	/**
	 * Sets the speed of both left and right motors at the same time, so the robot can move
	 * forwards or backwards.
	 * @param speed in the range -1 to 1 inclusive.
	 */
	public void setSpeed(double speed) {
		setLeftSpeed(speed);
		setRightSpeed(speed);
	}
	
	/**
	 * Stops left and right motors.
	 */
	public void stop() {
		setSpeed(0);
	}
	
	public void arcade(double forward, double rotate) {
	    // To rotate counterclockwise, we want the following modification:
        // V   ^
        // Which means that left is decreased, and right is increased.
        final double left = forward + rotate;
        final double right = forward - rotate;
        
        // We don't want any motor to run faster than unit speed, so if anything
        // is larger than the max speed we'll scale them down.
        // We use the reciprocal of the max speed so that if for example maxSpeed
        // is 0.5, then we'll get 2.0 and divide by 2.0.
        final double norm = selectMaxAbs(1/maxSpeed, left, right);
        
        setLeftSpeed(left / norm);
        setRightSpeed(right / norm);
	}
	
    private double selectMaxAbs(double... items) {
        assert items.length > 0;
        double result = Math.abs(items[0]);
        for (double item : items) {
            result = Math.max(result, Math.abs(item));
        }
        return result;
    }
    
    public void enable() {
        enabled = true;
    }
    
    public void disable() {
        enabled = false;
    }
}

