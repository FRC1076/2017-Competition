package org.usfirst.frc.team1076.robot.commands;

import org.strongback.Strongback;
import org.usfirst.frc.team1076.robot.subsystems.Drivetrain;
import org.usfirst.frc.team1076.robot.subsystems.VisionPIDCorrector;
import org.usfirst.frc.team1076.robot.vision.VisionReceiver;

public class ForwardWithVision extends CancelableCommand {
    Drivetrain drivetrain;
    VisionReceiver receiver;
    VisionPIDCorrector corrector;
    double speed;
    double goalDistance;
    
    public ForwardWithVision(Drivetrain drivetrain, VisionPIDCorrector corrector, double distance, double speed, double time) {
        super(time, drivetrain);
        this.drivetrain = drivetrain;
        this.receiver = corrector.getReceiver();
        this.speed = speed;
        this.goalDistance = distance;
        this.corrector = corrector;
    }
    
    @Override
    public void initialize() {
        super.initialize();
        Strongback.logger().info("BEGIN ForwardWithVision AUTO");
    }
    
    @Override
    public boolean execute() {
        drivetrain.arcade(speed, 0, corrector);
        return !isRunning;
    }
    
    @Override
    public void end() {
        Strongback.logger().info("END ForwardWithVision AUTO");
        drivetrain.stop();
    }

    @Override
    public void doCancel() {
        isRunning = false;
    }
}
