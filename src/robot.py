import wpilib
import ctre
from subsystems.wings import Wings
LEFT_HAND = 0
RIGHT_HAND = 1

#wing IDs

LEFT_RETRACT_ID = 1
LEFT_EXTEND_ID = 2
RIGHT_RETRACT_ID = 3
RIGHT_EXTEND_ID = 4
CENTER_RETRACT_ID = 5
CENTER_EXTEND_ID = 6


class MemeBot(wpilib.IterativeRobot):

    def robotInit(self):
        self.left1 = ctre.WPI_TalonSRX(3)
        self.left2 = ctre.WPI_TalonSRX(4)
        self.right1 = ctre.WPI_TalonSRX(1)
        self.right2 = ctre.WPI_TalonSRX(2)

        self.driver = wpilib.XboxController(0)
        self.operator = wpilib.XboxController(1)

        self.winch1 = ctre.WPI_TalonSRX(5)
        self.winch2 = ctre.WPI_TalonSRX(6)

        self.wings = Wings(
            left_retract=wpilib.Solenoid(LEFT_RETRACT_ID),
            right_retract=wpilib.Solenoid(RIGHT_RETRACT_ID),
            left_extend=wpilib.Solenoid(LEFT_EXTEND_ID),
            right_extend=wpilib.Solenoid(RIGHT_EXTEND_ID),
            center_extend=wpilib.Solenoid(CENTER_EXTEND_ID),
            center_retract=wpilib.Solenoid(CENTER_RETRACT_ID),
        )

    def robotPeriodic(self):
        pass
        
    def teleopInit(self):
        print("teleop start!")

    def teleopPeriodic(self):
        forward = self.driver.getY(RIGHT_HAND)
        rotate = self.driver.getX(LEFT_HAND)

        left = forward + rotate
        right = forward - rotate
        self.left1.set(left)
        self.left2.set(left)

        self.right1.set(right)
        self.right2.set(right)

        # winch_speed = self.operator.getY(LEFT_HAND)
        #     self.winch1.set(winch_speed)
        #     self.winch2.set(winch_speed)
        # if winch_speed < 0:
        #     winch_speed = 0


        # if activate_left:
        #     self.wings.raise_left()
        # if activate_right:
        #     self.wings.raise_right()
        # if activate_left_released:
        #     self.wings.lower_left
        #     self.left_activated = True
        # if activate_right_released:
        #     self.wings.lower_right()
        #     self.right_activated = True
        

        up = self.operator.getYButton()
        down = self.operator.getAButton()
        print("teleop period")
        if up:
            self.wings.raise_left()
            self.wings.raise_right()
            self.wings.raise_center()
            print("up!")
        if down: 
            self.wings.lower_left()
            self.wings.lower_right()
            self.wings.lower_center()
            print("down!")

    def autonomousInit(self):
        pass

    def autonomousPeriodic(self):
        pass


if __name__ == '__main__':
    wpilib.run(MemeBot)