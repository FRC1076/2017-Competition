package org.usfirst.frc.team1076.robot.commands;

import org.strongback.Strongback;
import org.strongback.command.Command;
import org.usfirst.frc.team1076.robot.subsystems.Drivetrain;
import org.usfirst.frc.team1076.robot.subsystems.GyroPIDCorrector;
import org.usfirst.frc.team1076.robot.vision.VisionData;
import org.usfirst.frc.team1076.robot.vision.VisionReceiver;
import org.usfirst.frc.team1076.robot.vision.VisionData.VisionStatus;

public class TurnToLift extends Command {
    VisionReceiver receiver;
    Drivetrain drivetrain;
    VisionData data;
    GyroPIDCorrector corrector;
    double speed;
    
    
    public TurnToLift(VisionReceiver receiver, Drivetrain drivetrain,
            GyroPIDCorrector corrector, double speed) {
        this.receiver = receiver;
        this.drivetrain = drivetrain;
        this.corrector = corrector;
        this.speed = speed;
    }
    
    @Override
    public void initialize() {
        Strongback.logger().info("BEGIN TurnToLift AUTO");
        
        receiver.receive();
        data = receiver.getData();
        corrector.getGyro().zero();
    }
    
    @Override
    public boolean execute() {
        switch(data.getStatus()) {
            case NO_TARGET:
                Strongback.logger().warn("TurnToLift did not see a target in init!");
                break;
            case ERROR:
                Strongback.logger().error("TurnToLift got an errored packet!");
                break;
            case LEFT:
            case OK:
            case RIGHT:
                TurnWithGyro command = new TurnWithGyro(drivetrain, corrector, speed, data.getHeading());
                Strongback.submit(command);
                break;
        } 
        
        return true;
    }
    
    @Override
    public void end() {
        Strongback.logger().info("END TurnToLift AUTO");
    }
            

}
