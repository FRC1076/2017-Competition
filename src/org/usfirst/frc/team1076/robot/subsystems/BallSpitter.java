package org.usfirst.frc.team1076.robot.subsystems;

import org.strongback.command.Requirable;
import org.strongback.components.Motor;

public class BallSpitter implements Requirable {
    Motor ballSpitter;
    boolean isPractice;
    
    /**
     * Create a BallSpitter from one motor
     * @param BallSpitter     the motor to use for the BallSpitter
     */
    public BallSpitter(Motor ballSpitter, boolean isPractice) {
        this.ballSpitter = ballSpitter;
        this.isPractice = isPractice;
    }
    
    /**
     * Create a BallSpitter from two motors composed to work in unison
     * @param motor1    a motor for the BallSpitter
     * @param motor2    a motor for the BallSpitter
     */
    
    /**
     * Set the speed of the BallSpitter. Positive extends, negative retracts
     */
    public void setSpeed(double speed) {
        if (isPractice) {
            ballSpitter.setSpeed(speed);
        }
    }
}