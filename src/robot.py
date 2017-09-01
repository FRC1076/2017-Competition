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

        self.winch1 = wpilib.TalonSRX(5)
        self.winch2 = wpilib.TalonSRX(6)

    def autonomousInit(self):
        pass

    def autonomousPerodic(self):
        pass

    def teleopInit(self):
        pass

    def teleopPerodic(self):
        forward = self.driver.getY(RIGHT_HAND)
        rotate = self.driver.getX(LEFT_HAND)

        left = forward + rotate
        right = forward - rotate
        self.left1.set(left)
        self.left2.set(left)

        self.right1.set(right)
        self.right2.set(right)

        winch_speed = self.operator.getY(LEFT_HAND)
            self.winch1.set(winch_speed)
            self.winch2.set(winch_speed)
        if winch_speed < 0:
            winch_speed = 0



if __name__ == '__main__':
    wpilib.run(MemeBot)