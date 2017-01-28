
package org.usfirst.frc.team1076.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

import org.strongback.Strongback;
import org.strongback.components.Motor;
import org.strongback.hardware.Hardware;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

import org.usfirst.frc.team1076.robot.commands.ExampleCommand;
import org.usfirst.frc.team1076.robot.commands.TeleopCommand;
import org.usfirst.frc.team1076.robot.subsystems.Drivetrain;
import org.usfirst.frc.team1076.robot.subsystems.ExampleSubsystem;
import org.usfirst.frc.team1076.robot.vision.VisionReceiver;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
    public static final ExampleSubsystem exampleSubsystem = new ExampleSubsystem();
	public static OI oi;
	Gamepad gamepad = new Gamepad(0);
	Command autonomousCommand;
	Motor left = Hardware.Motors.talonSRX(0).invert(); // This motor is placed backwards on the robot
	Motor right = Hardware.Motors.talonSRX(1);
	Drivetrain drivetrain = new Drivetrain(left, right);
	TeleopCommand teleopCommand = new TeleopCommand(gamepad, drivetrain);
	SendableChooser<Command> chooser = new SendableChooser<>();

	VisionReceiver receiver;
    public static final String IP = "0.0.0.0"; //"10.10.76.2"; 
    public static final int VISION_PORT = 5880;
    
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
	    Strongback.start();
      gamepad = new Gamepad(0);
      SmarterDashboard.putDefaultNumber("Left Factor", 1);
    SmarterDashboard.putDefaultNumber("Right Factor", 1);

		chooser.addDefault("Default Auto", new ExampleCommand());
		// chooser.addObject("My Auto", new MyAutoCommand());
		SmarterDashboard.putDefaultNumber("Show Vision", 1);
        try {
            receiver = new VisionReceiver(IP, VISION_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
		SmarterDashboard.putData("Auto mode", chooser);
	}

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	@Override
	public void disabledInit() {
        drivetrain.leftFactor = SmarterDashboard.getNumber("Left Factor", 1);
        drivetrain.rightFactor = SmarterDashboard.getNumber("Right Factor", 1);
	}
 
int debugCount = 0;
@Override
public void disabledPeriodic() {
		Scheduler.getInstance().run();
		if (debugCount++ % 100 == 0 && SmarterDashboard.getNumber("Show Vision", 0) == 1) {
		    if (receiver == null) {
		        Strongback.logger().warn("VisionReceiver is null on IP " + IP + " and port number " + VISION_PORT);
		    } else {
	            receiver.receive();
		        Strongback.logger().info(receiver.getData().toString());
		    }
		    
		}
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmarterDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString code to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional commands to the
	 * chooser code above (like the commented example) or additional comparisons
	 * to the switch structure below with additional strings & commands.
	 */
	@Override
	public void autonomousInit() {
		autonomousCommand = chooser.getSelected();

		/*
		 * String autoSelected = SmarterDashboard.getString("Auto Selector",
		 * "Default"); switch(autoSelected) { case "My Auto": autonomousCommand
		 * = new MyAutoCommand(); break; case "Default Auto": default:
		 * autonomousCommand = new ExampleCommand(); break; }
		 */

		// schedule the autonomous command (example)
		if (autonomousCommand != null)
			autonomousCommand.start();
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
	}

	@Override
	public void teleopInit() {
		// This makes sure that the autonomous stops running when
        // teleop starts running. If you want the autonomous to 
        // continue until interrupted by another command, remove
        // this line or comment it out.
    	Strongback.logger().info("I LIVE!");
    	Strongback.submit(teleopCommand);
        if (autonomousCommand != null) autonomousCommand.cancel();
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
        LiveWindow.run();
    }
}
