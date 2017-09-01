from unittest import mock

def test_drive(robot):
    robot.robotInit()
    
    assert robot.left1.get() == 0
    # Force Y stick to be 1
    robot.driver.getY = mock.MagicMock(return_value=1)
    robot.teleopPerodic()

    assert robot.left1.get() == 1

def test_winch(robot):
    robot.robotInit()

    robot.operator.getY = mock.MagicMock(return_value=1)
    robot.teleopPerodic()

    assert robot.winch1.get() == 1

    robot.operator.getY = mock.MagicMock(return_value=-1)
    robot.teleopPerodic()

    assert robot.winch1.get() == 0