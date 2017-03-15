package org.usfirst.frc.team1076.robot.commands;

import org.strongback.command.Command;
import org.strongback.command.Requirable;

/**
 * A CancelableCommand is a command which may be stopped by 
 * calling doCancel. Note that timeouts and other exit conditions
 * may still apply to the command.
 */
public abstract class CancelableCommand extends Command {
    boolean isRunning = true;
    
    public CancelableCommand(double time, Requirable... requirables) {
        super(time, requirables);
    } 
    
    /**
     * If the command is still running, return true. Note that this
     * function does not guarentee that the command is actually running,
     * and one should take care that any exit points in the command
     * also set isRunning to false.
     */
    public boolean isRunning() {
        return isRunning;
    }
    
    /**
     * Cancel this command. This command may optionally run any cleanup required
     */
    public abstract void doCancel();
}
