package org.usfirst.frc.team1076.robot.vision;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * This class processes JSON packets for vision.
 */
public class VisionData {
    public enum VisionStatus {
        LEFT, RIGHT, OK, ERROR
    }

    int heading = 0;
    double range = 0.0;
    VisionStatus status = VisionStatus.ERROR;
    int errorCount = 0;
    
    public VisionData() { }
    
    /**
     * Extracts data from a JSONObject. This object must have
     * the keys "status", "heading", and "range".  
     */
    public VisionData(JSONObject json) {
       update(json);
    }
    
    /**
     * Extracts data from a string formated like a JSON blob.
     */
    public VisionData(String json) {
        try {
    	    update(new JSONObject(new JSONTokener(json)));
        } catch (JSONException e) {
        }
    }
    
    public void update(JSONObject json) {
        try {
            // We didn't get a vision packet, so we can't really do anything with it
            if (!json.getString("sender").equals("vision")) {
                return;
            }
            
            switch (json.getString("status")) {
            case "left":
                status = VisionStatus.LEFT;
                errorCount = 0;
                break;
            case "right":
                status = VisionStatus.RIGHT;
                errorCount = 0;
                break;
            case "ok":
                status = VisionStatus.OK;
                errorCount = 0;
                break;
            case "error":
            default:
                status = VisionStatus.ERROR;
                errorCount += 1;
            }
            heading = json.getInt("heading");
            range = json.getDouble("range");
        } catch (JSONException e) { 
            // Malformed JSON packet, assume no useful data
        }
    }
    
    public void update(String json) {
    	try {
    		update(new JSONObject(new JSONTokener(json)));
    	} catch (JSONException e) {
    	}
    }
    
    /** 
     * The angle of the target, in degrees, from the middle of the robot.
     * Should return from -180 to 180
     */
    public int getHeading() {
        return heading;
    }

    /**
     * The distance to the target, measured in inches
     */
    public double getRange() {
        return range;
    }

    /**
     * The status of the message.
     * Left and right indicate that the robot is on that side.
     * Ok indicates that the robot is head on
     * Error indicates that the goal was not found for some reason
     */
    public VisionStatus getStatus() {
        return status;
    }
    
    /**
     * Get number of packets since last good packet received.
     * @return
     */
    public int getErrorCount() {
        return errorCount;
    }
}
