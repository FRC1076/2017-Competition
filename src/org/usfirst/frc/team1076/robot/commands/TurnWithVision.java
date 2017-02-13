package org.usfirst.frc.team1076.robot.commands;

import org.strongback.command.Command;
import org.usfirst.frc.team1076.robot.subsystems.Drivetrain;
import org.usfirst.frc.team1076.robot.vision.VisionReceiver;

public class TurnWithVision extends Command {

    Drivetrain drivetrain;
    VisionReceiver receiver;
    double speed;
    public TurnWithVision(Drivetrain drivetrain, VisionReceiver reciever, double speed) {
        this.drivetrain = drivetrain;
        this.receiver = reciever;
        this.speed = speed;
    }

    @Override
    public boolean execute() {
        receiver.receive();
        int heading = receiver.getData().getHeading();
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
        drivetrain.setLeftSpeed(speed * Math.signum(heading));
        drivetrain.setRightSpeed(-speed * Math.signum(heading));

        return heading == 0;
    }

    public void end() {
        drivetrain.stop();
    }

}
