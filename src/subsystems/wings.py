from wpilib import DoubleSolenoid

class Wings:
    """
    The wings job is to raise the other two robots on the team at least 12' off
    the ground via foldable wings that the robots will climb on. There will be 2
    valves that release pressure to pistons on each wings, which will deploy
    them.

    The wings are held by a velcro "latch" that is broken when the piston is
    released. When the velcro is broken the wings are "unlocked" and can now be
    lowered and raised.
    """

    def __init__(self, left_retract, left_extend, left2_retract, left2_extend, right_retract, right_extend, right2_retract, right2_extend):
        self.solenoid_pairs = {
            "left" : SolenoidPair(left_retract, left_extend),
            "right" : SolenoidPair(right_retract, right_extend),
            "left2" : SolenoidPair(left2_retract, left2_extend),
            "right2" : SolenoidPair(right2_retract, right2_extend)
        }

    def raiser(self, solenoid_pair):
        self.solenoid_pairs[solenoid_pair].extend()

    def raiser_all(self):
        for solenoid in self.solenoid_pairs:
            self.solenoid_pairs[solenoid].extend()

    def lower(self, solenoid_pair):
        self.solenoid_pairs[solenoid_pair].retract()

    def lower_all(self):
        for solenoid in self.solenoid_pairs:
            self.solenoid_pairs[solenoid].retract()

class SolenoidPair:
    def __init__(self, retract, extend):
        self._retract = retract
        self._extend = extend

    def retract(self):
        self._retract.set(True)
        self._extend.set(False)

    def extend(self):
        self._extend.set(True)
        self._retract.set(False)
