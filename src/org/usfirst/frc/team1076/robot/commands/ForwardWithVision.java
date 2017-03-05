package org.usfirst.frc.team1076.robot.commands;

import org.strongback.Strongback;
import org.strongback.command.Command;
import org.usfirst.frc.team1076.robot.subsystems.Drivetrain;
import org.usfirst.frc.team1076.robot.subsystems.DrivetrainWithVision;
import org.usfirst.frc.team1076.robot.vision.VisionReceiver;

public class ForwardWithVision extends CancelableCommand {
    Drivetrain drivetrain;
    VisionReceiver receiver;
    double speed;
    double goalDistance;
    
    public ForwardWithVision(DrivetrainWithVision drivetrain, double distance, double speed, double time) {
        super(time);
        this.drivetrain = drivetrain;
        this.receiver = drivetrain.getReceiver();
        this.speed = speed;
        this.goalDistance = distance;
    }
    
    @Override
    public boolean execute() {
        drivetrain.arcade(speed, 0);
        return !isRunning;
    }
    
    @Override
    public void end() {
        drivetrain.stop();
    }

    @Override
    public void doCancel() {
        // so this needs to make the command stop running
        // we can do that by returning true in execute
        // currently we're returning false
        // so instead, we should return the opposite of isRunning
        // do that
        // also make sure that doCancel ensures the cancel thing
        isRunning = false;
    }
}
