import wpilib

LEFT_HAND = 0
RIGHT_HAND = 1


class MemeBot(wpilib.IterativeRobot):

    def robotInit(self):
        self.left1 = wpilib.TalonSRX(3)
        self.left2 = wpilib.TalonSRX(4)
        self.right1 = wpilib.TalonSRX(1)
        self.right2 = wpilib.TalonSRX(2)
        self.driver = wpilib.XboxController(0)
        self.operator = wpilib.XboxController(1)

    def autonomousInit(self):
        pass

    def autonomousPerodic(self):
        pass

    def teleopInit(self):
        pass

    def teleopPerodic(self):
        pass


if __name__ == '__main__':
    wpilib.run(MemeBot)