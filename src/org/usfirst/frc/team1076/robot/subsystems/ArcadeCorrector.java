package org.usfirst.frc.team1076.robot.subsystems;

public interface ArcadeCorrector {
    /**
     * Mutate the left and right values.
     * This is the only time these values should be mutated.
     */
    public MotorOutput getCorrection(double forward, double rotate);
}

class MotorOutput {
    public double left;
    public double right;
    
    public MotorOutput(double left, double right) {
        super();
        this.left = left;
        this.right = right;
    }
}