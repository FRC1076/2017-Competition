package org.usfirst.frc.team1076.robot;

import org.strongback.command.CommandGroup;
import org.usfirst.frc.team1076.robot.commands.AccelerometerWatchdog;
import org.usfirst.frc.team1076.robot.commands.ForwardWithGyro;
import org.usfirst.frc.team1076.robot.commands.ForwardWithVision;

/**
 * This class is a group of commands designed to drive towards the lift using vision
 * It will run ForwardWithVision along with an AutonomousWatchdog. It then backsup a small distance once
 * striking the gearlift.
 * 
 *
 */
public class ForwardToGearLift extends CommandGroup {
    public ForwardToGearLift(ForwardWithVision forward, AccelerometerWatchdog watchdog, ForwardWithGyro backwards) {
        sequentially(simultaneously(forward, watchdog), backwards);
    }
}
