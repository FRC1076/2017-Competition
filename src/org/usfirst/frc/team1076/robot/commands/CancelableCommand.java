package org.usfirst.frc.team1076.robot.commands;

import org.strongback.command.Command;
import org.strongback.command.Requirable;

public abstract class CancelableCommand extends Command {
    boolean isRunning = true;
    
    public CancelableCommand(double time, Requirable... requirables) {
        super(time, requirables);
    } 
    
    public boolean isRunning() {
        return isRunning;
    }
    
    public abstract void doCancel();
}
