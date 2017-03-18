package org.usfirst.frc.team1076.robot.subsystems;

public class ArcadeNoCorrector implements ArcadeCorrector {
    double maxSpeed = 1.0;
    
    @Override
    public MotorOutput getCorrection(double forward, double rotate) {
        // To rotate counterclockwise, we want the following modification:
        // V   ^
        // Which means that left is decreased, and right is increased.
        final double left = forward + rotate;
        final double right = forward - rotate;
        
        // We don't want any motor to run faster than unit speed, so if anything
        // is larger than the max speed we'll scale them down.
        // We use the reciprocal of the max speed so that if for example maxSpeed
        // is 0.5, then we'll get 2.0 and divide by 2.0.
        final double norm = selectMaxAbs(1/maxSpeed, left, right);
        
        return new MotorOutput(left / norm, right / norm);
    }
    
    private double selectMaxAbs(double... items) {
        assert items.length > 0;
        double result = Math.abs(items[0]);
        for (double item : items) {
            result = Math.max(result, Math.abs(item));
        }
        return result;
    }
}
