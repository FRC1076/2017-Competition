package org.usfirst.frc.team1076.robot.subsystems;

import org.strongback.Strongback;
import org.strongback.command.Requirable;
import org.strongback.components.Motor;

public class Winch implements Requirable {
    Motor winch;

    /**
     * Create a winch from one motor
     * @param winch     the motor to use for the winch
     */
    public Winch(Motor winch) {
        this.winch = winch;
    }
    
    /**
     * Create a winch from two motors composed to work in unison
     * @param motor1    a motor for the winch
     * @param motor2    a motor for the winch
     */
    public Winch(Motor motor1, Motor motor2) {
        this(Motor.compose(motor1, motor2));
    }
    
    /**
     * Set the speed of the winch. Positive extends, negative retracts
     */
//    public void setSpeed(double speed) {
//        winch.setSpeed(speed);
//    }
    
    /**
     * Extend the winch at the set speed
     * @param speed
     */
    public void extend(double speed) {
        if (speed >= 0) {
            winch.setSpeed(speed);
        }
    }
    
    /**
     * Retract the winch at the set speed
     * @param speed
     */
//    public void retract(double speed) {
//        winch.setSpeed(-speed);
//    }
}
