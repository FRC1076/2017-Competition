
package org.usfirst.frc.team1076.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

import java.net.SocketException;

import org.strongback.Strongback;
import org.strongback.Strongback.Configurator;
import org.strongback.command.Command;
import org.strongback.command.CommandGroup;
import org.strongback.components.Accelerometer;
import org.strongback.components.Gyroscope;
import org.strongback.components.Motor;
import org.strongback.components.PneumaticsModule;
import org.strongback.components.Solenoid;
import org.strongback.components.Switch;
import org.strongback.components.TalonSRX;
import org.strongback.components.ThreeAxisAccelerometer;
import org.strongback.hardware.Hardware;
import org.strongback.mock.Mock;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

import org.usfirst.frc.team1076.robot.commands.AccelerometerWatchdog;
import org.usfirst.frc.team1076.robot.commands.BrakeCommand;
import org.usfirst.frc.team1076.robot.commands.ForwardWithGyro;
import org.usfirst.frc.team1076.robot.commands.ForwardWithVision;
import org.usfirst.frc.team1076.robot.commands.RecalibrateGyro;
import org.usfirst.frc.team1076.robot.commands.SolenoidSwitcherOneButton;
import org.usfirst.frc.team1076.robot.commands.SolenoidSwitcherTwoButton;
import org.usfirst.frc.team1076.robot.Gamepad.GamepadButton;
import org.usfirst.frc.team1076.robot.Gamepad.GamepadStick;
import org.usfirst.frc.team1076.robot.commands.TeleopCommand;
import org.usfirst.frc.team1076.robot.commands.TurnWithGyro;
import org.usfirst.frc.team1076.robot.commands.TurnWithVision;
import org.usfirst.frc.team1076.robot.commands.SolenoidSwitcherOneButton.SwitchType;
import org.usfirst.frc.team1076.robot.commands.TeleopWithGyroCommand;
import org.usfirst.frc.team1076.robot.subsystems.Drivetrain;
import org.usfirst.frc.team1076.robot.subsystems.DrivetrainWithGyro;
import org.usfirst.frc.team1076.robot.subsystems.DrivetrainWithVision;
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
	IGamepad driver = new Gamepad(0);
	IGamepad operator = new Gamepad(1);
	
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
	public enum CommandEnum { LEFT, RIGHT, CENTER, TEST, NONE };
	CommandEnum commandChoice;
	String autonomousSmartdashboardMessage = "Autonomous Type (accepted values: are left, right, center, test, and disable)";
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
		SmarterDashboard.putDefaultNumber("Left Factor", 1);
		SmarterDashboard.putDefaultNumber("Right Factor", 1);
		// chooser.addObject("My Auto", new MyAutoCommand());
		
		SmarterDashboard.putDefaultNumber("First Drive Time", 1.4);
		SmarterDashboard.putDefaultNumber("First Drive Speed", 0.9);
		SmarterDashboard.putDefaultNumber("Turn Amount", 55.0);
		SmarterDashboard.putDefaultNumber("Turn Speed", 0.65);
        SmarterDashboard.putDefaultNumber("Second Drive Time", 2.2);
        SmarterDashboard.putDefaultNumber("Second Drive Speed", 0.65);		
        SmarterDashboard.putDefaultNumber("Center Drive Time", 5.0);
        SmarterDashboard.putDefaultNumber("Center Drive Speed", 0.65);
		SmarterDashboard.putDefaultNumber("Gyro P", 2.5);
		SmarterDashboard.putDefaultNumber("Gyro I", 0.0);
		SmarterDashboard.putDefaultNumber("Gyro D", 0.5);

        SmarterDashboard.putDefaultNumber("Vision P", 0.5);
        SmarterDashboard.putDefaultNumber("Vision I", 0.0);
        SmarterDashboard.putDefaultNumber("Vision D", 0.0);
        SmarterDashboard.putDefaultNumber("Vision Norm Factor", 45.0);

        SmarterDashboard.putDefaultNumber("Turn Final Speed", 0.0);
        SmarterDashboard.putDefaultNumber("Turn Ease Out Threshold", 5.0);
		
        SmarterDashboard.putDefaultNumber("Accelerometer Threshold", 1.0);
        
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
	    case "t":
	    case "test":
	        commandChoice = CommandEnum.TEST;
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
	    
	    // AUTONOMOUS SETUP
        double driveTime = SmarterDashboard.getNumber("First Drive Time", 2.0);
        double speed = SmarterDashboard.getNumber("First Drive Speed", 0.5);
        double turnAmount = SmarterDashboard.getNumber("Turn Amount", 60.0) * (commandChoice == CommandEnum.RIGHT ? -1 : 1); // Turns opposite way if on right
        double turn_speed = SmarterDashboard.getNumber("Turn Speed", 0.4);
        double vision_time = SmarterDashboard.getNumber("Second Drive Time", 2.5);
        double vision_speed = SmarterDashboard.getNumber("Second Drive Speed", 0.5);
        double center_drive_time = SmarterDashboard.getNumber("Center Drive Time", 5.0);
        double center_drive_speed = SmarterDashboard.getNumber("Center Drive Speed", 0.65);
        
        double turn_final_speed = SmarterDashboard.getNumber("Turn Final Speed", 0.0);
        double turn_ease_out_threshold = SmarterDashboard.getNumber("Turn Ease Out Threshold", 0.5);
        
        double accelerometer_threshold = SmarterDashboard.getNumber("Accelerometer Threshold", 1.0);
        
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
            autonomousCommand = CommandGroup.runSequentially(forward, turn, Command.pause(1.0), CommandGroup.runSimultaneously(watchdog, vision));
            break;
        }
        case CENTER: {
            ForwardWithVision vision_center = new ForwardWithVision(drivetrainVision, 10, center_drive_speed, center_drive_time);
            AccelerometerWatchdog watchdog = new AccelerometerWatchdog(accelerometer.getXDirection(), vision_center);
            watchdog.accelerometer_threshold = accelerometer_threshold;
            autonomousCommand = CommandGroup.runSimultaneously(vision_center, watchdog);
            break;
        }
        case NONE: {
            autonomousCommand = Command.create(() -> {});
            break;
        }
        case TEST: {
            //          testAuto = new ForwardWithVision(drivetrainVision, 10, vision_speed, vision_time);
            TurnWithGyro turn = new TurnWithGyro(drivetrain, turn_speed, turnAmount);
            turn.finalSpeed = turn_final_speed;
            turn.easeOutThreshold = turn_ease_out_threshold;
            autonomousCommand = turn;
            break;
        }
        }
        
        if (autonomousCommand != null)
            Strongback.submit(autonomousCommand);
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
	    refreshDrivetrainValues(); 
	    
		Strongback.submit(teleopCommand);
		if (autonomousCommand != null)
			autonomousCommand.cancel();
		
		drivetrain.updateProfile();
        driver.setDeadzone(0.2);
        operator.setDeadzone(0.2);
        // LB = brake on, no RB = brake off
        Strongback.submit(new SolenoidSwitcherOneButton(brake, driver, GamepadButton.LB, SwitchType.ON_HOLD_EXTEND));
        // RB = high gear, no RB = low gear
        Strongback.submit(new SolenoidSwitcherOneButton(shifter, driver, GamepadButton.RB, SwitchType.ON_HOLD_RETRACT));
        // LB = down, RB = up
        Strongback.submit(new SolenoidSwitcherTwoButton(holder, operator, GamepadButton.LB, GamepadButton.RB));
	}
	
	/*
	 * Read values from the SmartDashboard and update them for the
	 * drivetrain.
	 */
    private void refreshDrivetrainValues() {
        Strongback.logger().info("Refreshed PID values");        
        drivetrain.leftFactor = SmarterDashboard.getNumber("Left Factor", 1);
        drivetrain.rightFactor = SmarterDashboard.getNumber("Right Factor", 1);
        drivetrain.P = SmarterDashboard.getNumber("Gyro P", 2.5); 
	    drivetrain.I = SmarterDashboard.getNumber("Gyro I", 0); 
	    drivetrain.D = SmarterDashboard.getNumber("Gyro D", 0.5);
	    drivetrainVision.P = SmarterDashboard.getNumber("Vision P", 3.0); 
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
		    System.out.println("PID correct" + drivetrain.computedValue);
		    System.out.println("accelerometerX " + accelerometer.getXDirection().getAcceleration());
		    System.out.println("accelerometerY " + accelerometer.getYDirection().getAcceleration());
		    System.out.println("accelerometerZ " + accelerometer.getZDirection().getAcceleration());
		    System.out.println("encoder left1 " + encoderLeft1.getRate());
		    System.out.println("encoder left2 " + encoderLeft2.getRate());
		    System.out.println("encoder right1 " + encoderRight1.getRate());
		    System.out.println("encoder right2 " + encoderRight2.getRate());
		    
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
