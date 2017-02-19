
package org.usfirst.frc.team1076.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

import java.net.SocketException;

import org.strongback.Strongback;
import org.strongback.Strongback.Configurator;
import org.strongback.command.Command;
import org.strongback.command.CommandGroup;
import org.strongback.components.Gyroscope;
import org.strongback.components.Motor;
import org.strongback.components.PneumaticsModule;
import org.strongback.components.Solenoid;
import org.strongback.components.Switch;
import org.strongback.hardware.Hardware;
import org.strongback.mock.Mock;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

import org.usfirst.frc.team1076.robot.commands.BrakeCommand;
import org.usfirst.frc.team1076.robot.commands.ForwardWithGyro;
import org.usfirst.frc.team1076.robot.commands.SolenoidSwitcherTwoButton;
import org.usfirst.frc.team1076.robot.Gamepad.GamepadButton;
import org.usfirst.frc.team1076.robot.Gamepad.GamepadStick;
import org.usfirst.frc.team1076.robot.commands.TeleopCommand;
import org.usfirst.frc.team1076.robot.commands.TurnWithGyro;
import org.usfirst.frc.team1076.robot.commands.TurnWithVision;
import org.usfirst.frc.team1076.robot.commands.TeleopWithGyroCommand;
import org.usfirst.frc.team1076.robot.subsystems.Drivetrain;
import org.usfirst.frc.team1076.robot.subsystems.DrivetrainWithGyro;
import org.usfirst.frc.team1076.robot.subsystems.Winch;
import org.usfirst.frc.team1076.robot.vision.VisionReceiver;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	Gamepad driver = new Gamepad(0);
	Gamepad operator = new Gamepad(1);
	Command autonomousCommand;
	Motor left1 = Hardware.Motors.talonSRX(3);
	Motor left2 = Hardware.Motors.talonSRX(4);
	 Motor left = Motor.compose(left1, left2);
//	Motor left = Mock.stoppedMotor();
	Motor right1 = Hardware.Motors.talonSRX(1);
	Motor right2 = Hardware.Motors.talonSRX(2);
	Motor right = Motor.compose(right1, right2).invert();
//	Motor right = Mock.stoppedMotor();
	Motor winch1 = Hardware.Motors.talonSRX(5);
	Motor winch2 = Hardware.Motors.talonSRX(6);
	Motor winchMotors = Motor.compose(winch1, winch2);
//	Motor winchMotors = Mock.stoppedMotor();
	
	PneumaticsModule pneumatics = Hardware.pneumaticsModule(0);
//	PneumaticsModule pneumatics = Mock.pnuematicsModule();
    Solenoid shifter = Hardware.Solenoids.doubleSolenoid(0, 1, Solenoid.Direction.RETRACTING);
    Solenoid brake = Hardware.Solenoids.doubleSolenoid(2, 3, Solenoid.Direction.RETRACTING);
    Solenoid holder = Hardware.Solenoids.doubleSolenoid(4, 5, Solenoid.Direction.RETRACTING);
	
//	Solenoid shifter = Mock.manualSolenoid();
//	Solenoid brake = Mock.manualSolenoid();
//	Solenoid holder = Mock.manualSolenoid();
	
	Gyroscope gyro = Hardware.AngleSensors.gyroscope(0);
	
	DrivetrainWithGyro drivetrain = new DrivetrainWithGyro(left, right, gyro);
	Winch winch = new Winch(winchMotors);
	TeleopCommand teleopCommand = new TeleopCommand(drivetrain, driver, operator, winch);
//	TeleopWithGyroCommand teleopCommand = new TeleopWithGyroCommand(drivetrain, driver, operator, winch);
	SendableChooser<Command> chooser = new SendableChooser<>();
	Switch switchRight = Hardware.Switches.normallyClosed(1);
	Switch switchLeft = Hardware.Switches.normallyClosed(0);
	VisionReceiver receiver;
	public static final String IP = "0.0.0.0"; // "10.10.76.22";
	public static final int VISION_PORT = 5880;
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		Strongback.start();
		
		pneumatics.automaticMode().on();
		shifter.retract();
		brake.retract();
		holder.retract();
		
		SmarterDashboard.putDefaultNumber("Deadzone", 0.2);
		
		SmarterDashboard.putDefaultNumber("Left Factor", 1);
		SmarterDashboard.putDefaultNumber("Right Factor", 1);
		// chooser.addObject("My Auto", new MyAutoCommand());
		SmarterDashboard.putDefaultNumber("Show Vision", 1);
		SmarterDashboard.putDefaultNumber("Teleop Sensitivity", 1.0);
		
		SmarterDashboard.putDefaultNumber("Drive Time", 5.0);
		SmarterDashboard.putDefaultNumber("Turn Amount", 60.0);
		SmarterDashboard.putDefaultNumber("Speed", 0.25);
		
		SmarterDashboard.putDefaultNumber("P", 0.0);
		SmarterDashboard.putDefaultNumber("I", 0.0);
		SmarterDashboard.putDefaultNumber("D", 0.0);
		
		SmarterDashboard.putDefaultNumber("Turn Reduction Factor", 0.7);
		SmarterDashboard.putDefaultNumber("Turn Reduction Threshold", 30);
		
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
	    Strongback.killAllCommands();
		gyro.zero();
		drivetrain.updateProfile();
	}
	
	int debugCount = 0;

	@Override
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
		if (debugCount++ % 100 == 0 && SmarterDashboard.getNumber("Show Vision", 0) == 1) {
//			if (receiver == null) {
//				Strongback.logger().warn("VisionReceiver is null on IP " + IP + " and port number " + VISION_PORT);
//			} else {
//				receiver.receive();
//				Strongback.logger().info(receiver.getData().toString());
//			}
			System.out.println("Gyro: " + gyro.getAngle());
//			System.out.println("P: " + drivetrain.P);
//			System.out.println("I: " + drivetrain.I);
//			System.out.println("D: " + drivetrain.D);
//			System.out.println("Left Switch: " + switchLeft.isTriggered());
//			System.out.println("Right Switch: " + switchRight.isTriggered());

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
	    refreshDrivetrainValues();
	    
        double driveTime = SmarterDashboard.getNumber("Drive Time", 5.0);
        double turnAmount = SmarterDashboard.getNumber("Turn Amount", 60.0);
        double speed = SmarterDashboard.getNumber("Speed", 0.25);
	    ForwardWithGyro forward = new ForwardWithGyro(gyro, drivetrain, speed, driveTime);
	    TurnWithGyro turn = new TurnWithGyro(gyro, drivetrain, speed, turnAmount);
	    turn.reduction_factor = SmarterDashboard.getNumber("Turn Reduction Factor", 0.7);
	    turn.reduction_threshold = SmarterDashboard.getNumber("Turn Reduction Threshold", 30);
	    TurnWithVision vision_turn = new TurnWithVision(drivetrain, receiver, speed);
	    autonomousCommand = CommandGroup.runSequentially(forward, turn, vision_turn, forward);
//		autonomousCommand = new ForwardWithGyro(gyro, drivetrain, 0.25, driveTime);
//		autonomousCommand = turn;
		/*
		 * String autoSelected = SmarterDashboard.getString("Auto Selector",
		 * "Default"); switch(autoSelected) { case "My Auto": autonomousCommand
		 * = new MyAutoCommand(); break; case "Default Auto": default:
		 * autonomousCommand = new ExampleCommand(); break; }
		 */
        if (debugCount++ % 100 == 0) {
//            Strongback.logger().info("Gyro: " + gyro.getAngle());
        };
		// schedule the autonomous command (example)
		if (autonomousCommand != null)
			Strongback.submit(autonomousCommand);
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
		if (debugCount++ % 100 == 0) {
		    Strongback.logger().info(receiver.getData().toString());
		}
	}

	@Override
	public void teleopInit() {
	    refreshDrivetrainValues(); 
	    
	    DrivetrainWithGyro.FORWARD_ASSIST_SENSITIVITY = SmarterDashboard.getNumber("Teleop Sensitivity", 1.0);
	    
		Strongback.logger().info("I LIVE!");
		Strongback.submit(teleopCommand);
		if (autonomousCommand != null)
			autonomousCommand.cancel();
		drivetrain.updateProfile();
        driver.deadzone = SmarterDashboard.getNumber("Deadzone", 0.2);
        Strongback.submit(new BrakeCommand(brake, driver));
        Strongback.submit(new SolenoidSwitcherTwoButton(shifter, driver, GamepadButton.LB, GamepadButton.RB));
        Strongback.submit(new SolenoidSwitcherTwoButton(holder, operator, GamepadButton.LB, GamepadButton.RB));
	}
	
	/*
	 * Read values from the SmartDashboard and update them for the
	 * drivetrain.
	 */
    private void refreshDrivetrainValues() {
        drivetrain.leftFactor = SmarterDashboard.getNumber("Left Factor", 1);
        drivetrain.rightFactor = SmarterDashboard.getNumber("Right Factor", 1);
        drivetrain.P = SmarterDashboard.getNumber("P", 0); 
	    drivetrain.I = SmarterDashboard.getNumber("I", 0); 
	    drivetrain.D = SmarterDashboard.getNumber("D", 0);
    }

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
//		System.out.println(gyro.getAngle());
		if (debugCount++ % 100 == 0) {
//		    Strongback.logger().info("PID Change: " + drivetrain.computedValue);
		    Strongback.logger().info("Shifter: " + shifter.getDirection().toString());
		    Strongback.logger().info("Brakes: " + brake.getDirection().toString());
		    Strongback.logger().info("Holder: " + holder.getDirection().toString());
		};
		
//		drivetrain.debugPID();
	}

	/**
	 * This function is called periodically during test mode
	 */
	public void testPeriodic() {
		LiveWindow.run();
	}
}
