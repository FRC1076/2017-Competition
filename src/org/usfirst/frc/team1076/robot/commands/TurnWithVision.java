package org.usfirst.frc.team1076.robot.commands;

import org.strongback.command.Command;
import org.usfirst.frc.team1076.robot.subsystems.Drivetrain;
import org.usfirst.frc.team1076.robot.vision.VisionReceiver;

/**
 * This command attempts to turn the robot such that it is
 * facing the target. If no target is found, the robot will
 * continue to turn. The command 
 *
 */
public class TurnWithVision extends Command {
    Drivetrain drivetrain;
    VisionReceiver receiver;
    double speed;
    
    public TurnWithVision(Drivetrain drivetrain, VisionReceiver reciever, double speed) {
        /* This command will force time-out after 5 seconds 
         * to prevent the command from never finishing.
         */
        super(5.0);
        this.drivetrain = drivetrain;
        this.receiver = reciever;
        this.speed = speed;
    }

    @Override
    public boolean execute() {
        receiver.receive();
        int heading = receiver.getData().getHeading();
        int errorCount = receiver.getData().getErrorCount();
        /* The heading essentially acts as the direction to turn.
         * Negative = left, positive = right.
         * 
         * To turn left, we need right positive and left negative
         * To turn right, we need left negative and left positive
         * 
         * We can do easily by taking the opposite of the sign of
         * the heading for the left, and the sign of the heading for the right. 
         * 
         */
        if (errorCount < 5) {
            drivetrain.setLeftSpeed(speed * Math.signum(heading));
            drivetrain.setRightSpeed(-speed * Math.signum(heading));
            return heading == 0;
        } else {
            /* This indicates that our vision data is likely out
             * of date. The only thing we can really do is keep
             * turning and hope that we pick up a target. 
             */
            drivetrain.setLeftSpeed(speed);
            drivetrain.setRightSpeed(-speed);
            return false;
        }
    }

    public void end() {
        drivetrain.stop();
    }

}
