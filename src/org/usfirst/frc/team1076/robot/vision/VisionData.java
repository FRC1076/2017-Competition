package org.usfirst.frc.team1076.robot.vision;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.strongback.Strongback;

/**
 * This class processes JSON packets for vision.
 */
public class VisionData {
    public enum VisionStatus {
        LEFT, RIGHT, OK, ERROR
    }

    double heading = 0;
    double range = 0.0;
    VisionStatus status = VisionStatus.ERROR;
    int errorCount = 0;
    String json;
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
        this.json = json.toString();
        // The json packet must have these fields, otherwise this is a malformed packet
        if (json.has("sender") && json.has("status") && json.has("heading") && json.has("range")) {
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
            heading = json.getDouble("heading");
            range = json.getDouble("range");
        } else { 
            // Malformed or incomplete JSON packet. This is likely an error.
            errorCount += 1;
            status = VisionStatus.ERROR;
            Strongback.logger().warn("Bad JSON Packet");
        }
    }
    
    public void update(String json) {
    	try {
    		update(new JSONObject(new JSONTokener(json)));
    	} catch (JSONException e) {
            errorCount += 1;
            status = VisionStatus.ERROR;
    	}
    }
    
    /** 
     * The angle of the target, in degrees, from the middle of the robot.
     * Should return from -180 to 180
     */
    public double getHeading() {
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
    
    public String toString() {
        return json;
    }
}
