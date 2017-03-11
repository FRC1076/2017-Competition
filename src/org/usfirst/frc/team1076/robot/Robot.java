
package org.usfirst.frc.team1076.robot;

import java.net.SocketException;

import org.strongback.Strongback;
import org.strongback.command.Command;
import org.strongback.command.CommandGroup;
import org.strongback.components.Gyroscope;
import org.strongback.components.Motor;
import org.strongback.components.PneumaticsModule;
import org.strongback.components.Solenoid;
import org.strongback.components.Switch;
import org.strongback.components.TalonSRX;
import org.strongback.components.ThreeAxisAccelerometer;
import org.strongback.hardware.Hardware;
import org.usfirst.frc.team1076.robot.commands.AccelerometerWatchdog;
import org.usfirst.frc.team1076.robot.commands.ForwardWithGyro;
import org.usfirst.frc.team1076.robot.commands.ForwardWithVision;
import org.usfirst.frc.team1076.robot.commands.RecalibrateGyro;
import org.usfirst.frc.team1076.robot.commands.TeleopWithGyroCommand;
import org.usfirst.frc.team1076.robot.commands.TurnWithGyro;
import org.usfirst.frc.team1076.robot.subsystems.DrivetrainWithGyro;
import org.usfirst.frc.team1076.robot.subsystems.DrivetrainWithVision;
import org.usfirst.frc.team1076.robot.subsystems.Winch;
import org.usfirst.frc.team1076.robot.vision.VisionReceiver;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.interfaces.Gyro;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
    org.strongback.components.ui.Gamepad strongbackDriver = Hardware.HumanInterfaceDevices.xbox360(0);
    org.strongback.components.ui.Gamepad strongbackOperator = Hardware.HumanInterfaceDevices.xbox360(1);
	IGamepad driver = new StrongbackGamepad(strongbackDriver);
	IGamepad operator = new StrongbackGamepad(strongbackOperator);
	
	final double PULSES_PER_DEGREE = 1000;
	
	TalonSRX left1 = Hardware.Motors.talonSRX(3, PULSES_PER_DEGREE).enableBrakeMode(true);
	TalonSRX left2 = Hardware.Motors.talonSRX(4, PULSES_PER_DEGREE).enableBrakeMode(true);
	Motor left = Motor.compose(left1, left2);
//	Motor left = Mock.stoppedMotor();
	TalonSRX right1 = Hardware.Motors.talonSRX(1, PULSES_PER_DEGREE).enableBrakeMode(true);
	TalonSRX right2 = Hardware.Motors.talonSRX(2, PULSES_PER_DEGREE).enableBrakeMode(true);
	Motor right = Motor.compose(right1, right2).invert();
//	Motor right = Mock.stoppedMotor();
	TalonSRX winch1 = Hardware.Motors.talonSRX(5).enableBrakeMode(true);
	TalonSRX winch2 = Hardware.Motors.talonSRX(6).enableBrakeMode(true);
	Motor winchMotors = Motor.compose(winch1, winch2);
//	Motor winchMotors = Mock.stoppedMotor();
	PneumaticsModule pneumatics = Hardware.pneumaticsModule(0);
//	PneumaticsModule pneumatics = Mock.pnuematicsModule();
    Solenoid shifter = Hardware.Solenoids.doubleSolenoid(0, 1, Solenoid.Direction.RETRACTING);
    Solenoid brake = Hardware.Solenoids.doubleSolenoid(2, 3, Solenoid.Direction.RETRACTING);
    Solenoid holder = Hardware.Solenoids.doubleSolenoid(4, 5, Solenoid.Direction.RETRACTING);
	
    Gyroscope encoderLeft1 = left1.getEncoderInput();
    Gyroscope encoderLeft2 = left2.getEncoderInput();
    Gyroscope encoderRight1 = right1.getEncoderInput();
    Gyroscope encoderRight2 = right2.getEncoderInput();
    
//	Solenoid shifter = Mock.manualSolenoid();
//	Solenoid brake = Mock.manualSolenoid();
//	Solenoid holder = Mock.manualSolenoid();
    
	Gyro wpilib_gyro = new ADXRS450_Gyro(SPI.Port.kOnboardCS0);
	Gyroscope gyro = Hardware.AngleSensors.gyroscope(wpilib_gyro);
	
	ThreeAxisAccelerometer accelerometer = Hardware.Accelerometers.builtIn();
	
	DrivetrainWithGyro drivetrain = new DrivetrainWithGyro(left, right, gyro);
	
	Winch winch = new Winch(winchMotors);
//	TeleopCommand teleopCommand = new TeleopCommand(drivetrain, driver, operator, winch);
	TeleopWithGyroCommand teleopCommand = new TeleopWithGyroCommand(drivetrain, driver, operator, winch);

//	SendableChooser<CommandEnum> chooser = new SendableChooser<CommandEnum>();
	public enum CommandEnum { LEFT, RIGHT, CENTER, NONE };
	CommandEnum commandChoice;
	String autonomousSmartdashboardMessage = "Autonomous Type (accepted values: are left, right, center, and disable)";
	Command autonomousCommand;
	
	Switch switchRight = Hardware.Switches.normallyClosed(1);
	Switch switchLeft = Hardware.Switches.normallyClosed(0);
	VisionReceiver receiver;
	public static final String IP = "0.0.0.0"; // "10.10.76.22";
	public static final int VISION_PORT = 5880;
	DrivetrainWithVision drivetrainVision;
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	
	Command leftAuto, centerAuto, rightAuto, noAuto, testAuto;
	
	@Override
	public void robotInit() {
		Strongback.start();
		Strongback.logger().info("BEGIN ROBOT INIT");
		pneumatics.automaticMode().on();
		// Extend = low gear, retract = high gear
		shifter.extend(); // Start low gear
		// Extend = brake, retract = no brake
		brake.retract(); // Start brakes raised
		// Extend = doors up, retract = doors down
//		holder.retract(); // Start doors down
		
		// Ensure drivetrain has brake mode, not coast. 
		left1.enableBrakeMode(true);
		left2.enableBrakeMode(true);
		right1.enableBrakeMode(true);
		right2.enableBrakeMode(true);
		
		
		// SMART DASHBOARD
//		SmarterDashboard.putDefaultNumber("Left Factor", 1);
//		SmarterDashboard.putDefaultNumber("Right Factor", 1);
		// chooser.addObject("My Auto", new MyAutoCommand());
		
		SmarterDashboard.putDefaultNumber("First Drive Time", 1.4);
		SmarterDashboard.putDefaultNumber("First Drive Speed", 0.9);
		SmarterDashboard.putDefaultNumber("Turn Amount", 42.0);
		SmarterDashboard.putDefaultNumber("Turn Speed", 0.65);
        SmarterDashboard.putDefaultNumber("Second Drive Time", 5.0);
        SmarterDashboard.putDefaultNumber("Second Drive Speed", 0.65);		
        SmarterDashboard.putDefaultNumber("Center Drive Time", 5.0);
        SmarterDashboard.putDefaultNumber("Center Drive Speed", 0.65);
		SmarterDashboard.putDefaultNumber("Gyro P", 1.5);
		SmarterDashboard.putDefaultNumber("Gyro I", 0.0);
		SmarterDashboard.putDefaultNumber("Gyro D", 0.5);

        SmarterDashboard.putDefaultNumber("Vision P", 0.5);
        SmarterDashboard.putDefaultNumber("Vision I", 0.0);
        SmarterDashboard.putDefaultNumber("Vision D", 0.0);
        SmarterDashboard.putDefaultNumber("Vision Norm Factor", 45.0);

        SmarterDashboard.putDefaultNumber("Turn Final Speed", 0.0);
        SmarterDashboard.putDefaultNumber("Turn Ease Out Threshold", 5.0);
		
        SmarterDashboard.putDefaultNumber("Accelerometer Threshold", 1.0);

        SmarterDashboard.putDefaultNumber("Backward Time", 0.5);
        SmarterDashboard.putDefaultNumber("Backward Speed", -0.1);
        
		try {
			receiver = new VisionReceiver(IP, VISION_PORT);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		drivetrainVision = new DrivetrainWithVision(left, right, receiver);
		
		SmarterDashboard.putDefaultString(autonomousSmartdashboardMessage, "center");
		 
//		chooser.addObject("Left Autonomous", CommandEnum.LEFT);
//        chooser.addObject("Center Autonomous", CommandEnum.CENTER);
//        chooser.addObject("Right Autonomous", CommandEnum.RIGHT);
//        chooser.addDefault("Disable Autonomous", CommandEnum.NONE);
//        chooser.addObject("Test Autonomous", CommandEnum.TEST);

//		SmarterDashboard.putData("Auto mode", chooser);
		refreshDrivetrainValues();
		
		SmarterDashboard.putData("Recalibrate Gyro", new RecalibrateGyro(wpilib_gyro, drivetrain));
		Strongback.logger().info("END ROBOT INIT");
	}

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	@Override
	public void disabledInit() {
	    Strongback.logger().info("DISABLED");
	    Strongback.killAllCommands();
		gyro.zero();
		refreshDrivetrainValues();
//		commandChoice = chooser.getSelected();
	}
	
	

	@Override
	public void disabledPeriodic() {
        if (debugCount++ % 100 == 0) {
            if (receiver == null) {
                Strongback.logger().warn("VisionReceiver is null on IP " + IP + " and port number " + VISION_PORT);
            } else {
                receiver.receive();
                Strongback.logger().info("Vision: " + receiver.getData().toString());
            } 
        }
        Scheduler.getInstance().run();
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
	    Strongback.logger().info("BEGIN AUTONOMOUS INIT");
	    refreshDrivetrainValues();
	    
	    String stringChoice = SmarterDashboard.getString(autonomousSmartdashboardMessage, "center");
	    
	    switch(stringChoice.toLowerCase()) {
	    case "l":
	    case "left":
	        commandChoice = CommandEnum.LEFT;
	        break;
	    case "r":
	    case "right":
	        commandChoice = CommandEnum.RIGHT;
            break;
	    case "c":
	    case "center":
	        commandChoice = CommandEnum.CENTER;
            break;
	    case "d":
	    case "disable":
	        commandChoice = CommandEnum.NONE;
	        break;
        default:
            Strongback.logger().warn(stringChoice + "is not a recognized autonomous mode. Defaulting to center");
            commandChoice = CommandEnum.CENTER;
            break;

	    }
	    
	    Strongback.logger().info("USING AUTONOMOUS TYPE " + commandChoice.toString());
	    
	    // AUTONOMOUS SETUP
        double driveTime = SmarterDashboard.getNumber("First Drive Time", 1.4);
        double speed = SmarterDashboard.getNumber("First Drive Speed", 0.9);
        double turnAmount = SmarterDashboard.getNumber("Turn Amount", 42.0) * (commandChoice == CommandEnum.RIGHT ? -1 : 1); // Turns opposite way if on right
        double turn_speed = SmarterDashboard.getNumber("Turn Speed", 0.65);
        double vision_time = SmarterDashboard.getNumber("Second Drive Time", 5.0);
        double vision_speed = SmarterDashboard.getNumber("Second Drive Speed", 0.55);
        
        double center_drive_time = SmarterDashboard.getNumber("Center Drive Time", 5.0);
        double center_drive_speed = SmarterDashboard.getNumber("Center Drive Speed", 0.65);
        
        double backward_drive_time = SmarterDashboard.getNumber("Backward Time", 0.0);
        double backward_drive_speed = SmarterDashboard.getNumber("Backward Speed", 0.0);
        
        double turn_final_speed = SmarterDashboard.getNumber("Turn Final Speed", 0.0);
        double turn_ease_out_threshold = SmarterDashboard.getNumber("Turn Ease Out Threshold", 5.0);
        
        double accelerometer_threshold = SmarterDashboard.getNumber("Accelerometer Threshold", 1.0);
        
        Strongback.logger().info("BEGIN SMARTDASHBOARD DUMP");
        Strongback.logger().info("First Drive Time: " + driveTime);
        Strongback.logger().info("First Drive Speed: " + speed);
        Strongback.logger().info("Turn Amount: " + turnAmount);
        Strongback.logger().info("Second Drive Time: " + vision_time);
        Strongback.logger().info("Second Drive Speed: " + vision_speed);
        Strongback.logger().info("Center Drive Time: " + center_drive_time);
        Strongback.logger().info("Center Drive Speed: " + center_drive_speed);
        Strongback.logger().info("Backward Time: " + backward_drive_time);
        Strongback.logger().info("Backward Speed: " + backward_drive_speed);
        Strongback.logger().info("Turn Final Speed: " + turn_final_speed);
        Strongback.logger().info("Turn Ease Out Threshold: " + turn_ease_out_threshold);
        Strongback.logger().info("Accelerometer Threshold: " + accelerometer_threshold);
        Strongback.logger().info("END SMARTDASHBOARD DUMP");
        
        switch (commandChoice) {
        case LEFT:
        case RIGHT:
        {
            ForwardWithGyro forward = new ForwardWithGyro(drivetrain, speed, driveTime);
            TurnWithGyro turn = new TurnWithGyro(drivetrain, turn_speed, turnAmount);
            turn.finalSpeed = turn_final_speed;
            turn.easeOutThreshold = turn_ease_out_threshold;
            ForwardWithVision vision = new ForwardWithVision(drivetrainVision, 10, vision_speed, vision_time);
            AccelerometerWatchdog watchdog = new AccelerometerWatchdog(accelerometer.getXDirection(), vision);
            ForwardWithGyro backward = new ForwardWithGyro(drivetrain, backward_drive_speed, backward_drive_time);
            autonomousCommand = CommandGroup.runSequentially(forward, turn, Command.pause(1.0), CommandGroup.runSimultaneously(watchdog, vision), backward);
            break;
        }
        case CENTER: {
            ForwardWithVision vision_center = new ForwardWithVision(drivetrainVision, 10, center_drive_speed, center_drive_time);
            AccelerometerWatchdog watchdog = new AccelerometerWatchdog(accelerometer.getXDirection(), vision_center);
            watchdog.accelerometer_threshold = accelerometer_threshold;
            ForwardWithGyro backward = new ForwardWithGyro(drivetrain, backward_drive_speed, backward_drive_time);
            autonomousCommand = CommandGroup.runSequentially(CommandGroup.runSimultaneously(vision_center, watchdog), backward);
            break;
        }
        case NONE: {
            autonomousCommand = Command.create(() -> {});
            break;
        }
        }
        
        if (autonomousCommand != null) {
            Strongback.submit(autonomousCommand);
        }
        
        Strongback.logger().info("END AUTONOMOUS INIT");
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
        if (debugCount++ % 10 == 0) {
            if (receiver == null) {
                Strongback.logger().warn("VisionReceiver is null on IP " + IP + " and port number " + VISION_PORT);
            } else {
                receiver.receive();
                Strongback.logger().info("Vision: " + receiver.getData().toString());
            } 
        }
	}

	@Override
	public void teleopInit() {
	    Strongback.logger().info("BEGIN TELEOP INIT");
	    refreshDrivetrainValues(); 
	    
		Strongback.submit(teleopCommand);
		if (autonomousCommand != null)
			autonomousCommand.cancel();
		
		drivetrain.updateProfile();
        driver.setDeadzone(0.2);
        operator.setDeadzone(0.2);
        // LB = brake on, no RB = brake off
        Strongback.switchReactor().onTriggered(strongbackDriver.getLeftBumper(), ()->brake.extend());
        Strongback.switchReactor().onUntriggered(strongbackDriver.getLeftBumper(), ()->brake.retract());
//        Strongback.submit(new SolenoidSwitcherOneButton(brake, driver, GamepadButton.LB, SwitchType.ON_HOLD_EXTEND));
        // RB = high gear, no RB = low gear
        Strongback.switchReactor().onTriggered(strongbackDriver.getRightBumper(), ()->shifter.retract());
        Strongback.switchReactor().onUntriggered(strongbackDriver.getRightBumper(), ()->shifter.extend());
//        Strongback.submit(new SolenoidSwitcherOneButton(shifter, driver, GamepadButton.RB, SwitchType.ON_HOLD_RETRACT));
        // LB = down, RB = up
        Strongback.switchReactor().onTriggered(strongbackOperator.getLeftBumper(), ()->holder.retract());
        Strongback.switchReactor().onTriggered(strongbackOperator.getRightBumper(), ()->holder.extend());
//        Strongback.submit(new SolenoidSwitcherTwoButton(holder, operator, GamepadButton.LB, GamepadButton.RB));
        Strongback.logger().info("END TELEOP INIT");
	}
	
	/*
	 * Read values from the SmartDashboard and update them for the
	 * drivetrain.
	 */
    private void refreshDrivetrainValues() {
        Strongback.logger().info("Refreshed PID values");        
        drivetrain.leftFactor = 1; // SmarterDashboard.getNumber("Left Factor", 1);
        drivetrain.rightFactor = 1; // SmarterDashboard.getNumber("Right Factor", 1);
        drivetrain.P = SmarterDashboard.getNumber("Gyro P", 1.5); 
	    drivetrain.I = SmarterDashboard.getNumber("Gyro I", 0); 
	    drivetrain.D = SmarterDashboard.getNumber("Gyro D", 0.5);
	    drivetrainVision.P = SmarterDashboard.getNumber("Vision P", 0.5); 
	    drivetrainVision.I = SmarterDashboard.getNumber("Vision I", 0); 
	    drivetrainVision.D = SmarterDashboard.getNumber("Vision D", 0);
	    drivetrainVision.VISION_NORMAL = SmarterDashboard.getNumber("Vision Norm Factor", 45.0);
	    drivetrain.updateProfile();
	    drivetrainVision.updateProfile();
    }

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
		if (debugCount++ % 100 == 0) {
		    if (accelerometer == null) {
		        Strongback.logger().warn("Accelerometer is null!");
		    }
		    System.out.println("Gyro:"  + gyro.getAngle());
		    System.out.println("PID correction " + drivetrain.computedValue);
		}
//		drivetrain.debugPID();
	}
	
	/**
	 * This function is called periodically during test mode
	 */
	int debugCount = 0;
	public void testPeriodic() {
        if (debugCount++ % 100 == 0) {
            if (receiver == null) {
                Strongback.logger().warn("VisionReceiver is null on IP " + IP + " and port number " + VISION_PORT);
            } else {
                receiver.receive();
                Strongback.logger().info("Vision: " + receiver.getData().toString());
            }
            System.out.println("Gyro: " + gyro.getAngle());
//          System.out.println("Left Switch: " + switchLeft.isTriggered());
//          System.out.println("Right Switch: " + switchRight.isTriggered());
        }
		// LiveWindow.run();
	}
}
