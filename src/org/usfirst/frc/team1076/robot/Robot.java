
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
import org.usfirst.frc.team1076.robot.commands.ArcadeCommand;
import org.usfirst.frc.team1076.robot.commands.ForwardWithGyro;
import org.usfirst.frc.team1076.robot.commands.ForwardWithVision;
import org.usfirst.frc.team1076.robot.commands.RecalibrateGyro;
import org.usfirst.frc.team1076.robot.commands.TeleopCommand;
import org.usfirst.frc.team1076.robot.commands.TeleopWithGyroCommand;
import org.usfirst.frc.team1076.robot.commands.TurnWithGyro;
import org.usfirst.frc.team1076.robot.subsystems.ArcadeNoCorrector;
import org.usfirst.frc.team1076.robot.subsystems.BallSpitter;
import org.usfirst.frc.team1076.robot.subsystems.Drivetrain;
import org.usfirst.frc.team1076.robot.subsystems.GyroPIDCorrector;
import org.usfirst.frc.team1076.robot.subsystems.VisionPIDCorrector;
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
    public static final boolean IS_PRACTICE = false;
    
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
	
	// FOR PRACTICE MODE ONLY
	Motor ballSpitterMotor = Hardware.Motors.talonSRX(7).enableBrakeMode(true);
	
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
	
	Winch winch = new Winch(winchMotors);
	BallSpitter ballSpitter = new BallSpitter(ballSpitterMotor, IS_PRACTICE);
//	TeleopCommand teleopCommand = new TeleopCommand(drivetrain, driver, operator, winch);
	
	Drivetrain drivetrain = new Drivetrain(left, right);
	GyroPIDCorrector gyroCorrector; 
	VisionPIDCorrector visionCorrector;
	
	TeleopCommand teleopCommand;

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
		
		SmarterDashboard.putDefaultNumber("Turn Time", RobotConstants.TURN_TIME);
		SmarterDashboard.putDefaultNumber("Turn Speed", RobotConstants.TURN_SPEED);
        SmarterDashboard.putDefaultNumber("Forward Drive Time", RobotConstants.FORWARD_DRIVE_TIME);
        SmarterDashboard.putDefaultNumber("Forward Drive Speed", RobotConstants.FORWARD_DRIVE_SPEED);
		SmarterDashboard.putDefaultNumber("Gyro P", RobotConstants.GYRO_P);
		SmarterDashboard.putDefaultNumber("Gyro I", RobotConstants.GYRO_I);
		SmarterDashboard.putDefaultNumber("Gyro D", RobotConstants.GYRO_D);

        SmarterDashboard.putDefaultNumber("Vision P", RobotConstants.VISION_P);
        SmarterDashboard.putDefaultNumber("Vision I", RobotConstants.VISION_I);
        SmarterDashboard.putDefaultNumber("Vision D", RobotConstants.VISION_D);
//        SmarterDashboard.putDefaultNumber("Vision Norm FputDefaultNumber("Vision Norm Factor);
		try {
			receiver = new VisionReceiver(IP, VISION_PORT);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		// CORRECTORS
		gyroCorrector = new GyroPIDCorrector(gyro);
		visionCorrector = new VisionPIDCorrector(receiver);
		
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
        double turn_time = SmarterDashboard.getNumber("Turn Time", RobotConstants.TURN_TIME);
        double turn_speed = SmarterDashboard.getNumber("Turn Speed", RobotConstants.TURN_SPEED)  * (commandChoice == CommandEnum.RIGHT ? -1 : 1); // Turns opposite way if on right
        
        double forward_drive_time = SmarterDashboard.getNumber("Forward Drive Time", RobotConstants.FORWARD_DRIVE_TIME);
        double forward_drive_speed = SmarterDashboard.getNumber("Forward Drive Speed", RobotConstants.FORWARD_DRIVE_SPEED);
        
        Strongback.logger().info("BEGIN SMARTDASHBOARD DUMP");
        Strongback.logger().info("Turn Time: " + turn_time);
        Strongback.logger().info("Turn Speed: " + turn_speed);
        Strongback.logger().info("Forward Drive Time: " + forward_drive_time);
        Strongback.logger().info("Forward Drive Speed: " + forward_drive_speed);
        Strongback.logger().info("END SMARTDASHBOARD DUMP");
        
        Command dropBall = null;
        if (IS_PRACTICE) {
            dropBall = Command.create(() -> {holder.extend();});
        } else {
            dropBall = Command.create(5.0, () -> {ballSpitter.setSpeed(1.0);});
        }
        
        switch (commandChoice) {
        case LEFT:
        case RIGHT:
        {
            ArcadeCommand turn = new ArcadeCommand(drivetrain, turn_speed, 0, turn_time);
            ArcadeCommand forward = new ArcadeCommand(drivetrain, forward_drive_speed, 0, forward_drive_time);
            autonomousCommand = CommandGroup.runSequentially(dropBall, forward, turn);
            break;
        }
        case CENTER: {
            ArcadeCommand forward = new ArcadeCommand(drivetrain, forward_drive_speed, 0, forward_drive_time);
            autonomousCommand = CommandGroup.runSequentially(dropBall, forward);
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
	    
	    teleopCommand = new TeleopWithGyroCommand(drivetrain, gyroCorrector, driver, operator, winch, ballSpitter);
	    
		Strongback.submit(teleopCommand);
		if (autonomousCommand != null)
			autonomousCommand.cancel();
		
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
//        Strongback.switchReactor().onTriggered(strongbackDriver.getA(), ()->{
//            Strongback.logger().info("MACRO START");
//            Strongback.submit(
//                    CommandGroup.runSequentially(
//                            new TurnToLift(receiver, drivetrain, gyroCorrector, RobotConstants.MACRO_TURN_SPEED),
////                            new ForwardToGearLift(vision, watchdog, backward),
//                            Command.create(()->{Strongback.logger().info("MACRO END");}),
//                            teleopCommand));
//            });
	
        Strongback.logger().info("END TELEOP INIT");
	}
	
	/*
	 * Read values from the SmartDashboard and update them for the
	 * drivetrain.
	 */
    private void refreshDrivetrainValues() {
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
		    System.out.println("PID correction " + gyroCorrector.computedValue);
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
