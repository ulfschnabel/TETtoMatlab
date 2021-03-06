package main;

import com.theeyetribe.client.GazeManager;
import com.theeyetribe.client.IGazeListener;
import com.theeyetribe.client.GazeManager.*;
import com.theeyetribe.client.data.CalibrationResult;
import com.theeyetribe.client.data.GazeData;
import com.theeyetribe.client.ICalibrationProcessHandler;

import java.io.FileWriter;
import java.io.IOException;

public class TETHandler {
	
	final static GazeListener gazeListener = new GazeListener();
	final static CalibrationHandler calibrationHandler = new CalibrationHandler();
	public static String state;
	static GazeManager gm;
	static boolean running = false;
	static double x = 999;
	static double y = 999;
	static double fixx;
	static double fixy;
	static double fixtol;
	static boolean debug = false;
	static boolean silent = false;
	static boolean logtofile = false;
	static long logtime = 0;
	static int xoffset = 0;
	static int yoffset = 0;
	public static FileWriter logwriter;
	public static void main(String[] args)
	{
		
		if(debug) System.out.println("The eyetracking is ready to run");
			
	}
	
	
	public static void start()
	{
		if(!running)
		{
		   gm = GazeManager.getInstance();    
		   boolean success = gm.activate(ApiVersion.VERSION_1_0, ClientMode.PUSH);
		   state = Boolean.toString(success);
		   gm.addGazeListener(gazeListener);
		   if(!silent) System.out.println("Starting the eyetracking");
		   running = true;
		}
		else
		{
			if(!silent) System.out.println("The eyetracking is already running");
		}
			
	}
	
	public static void log(String file){
		logtofile = true;
		try {
			logwriter = new FileWriter(file, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Invalid file");
			e.printStackTrace();
		}
		logtime = System.currentTimeMillis();
	}
	
	public static void message(String mes)
	{
		if(TETHandler.logtofile)
		{
			try {
				//logwriter.write("Message;" + Long.toString(System.currentTimeMillis() - logtime) + ";" + mes + System.lineSeparator());
				logwriter.write(mes + ";" + Long.toString(System.currentTimeMillis() - TETHandler.logtime) + ";" + Double.toString(x) + ";" + Double.toString(y) + System.lineSeparator());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Cannot write to log");
				e.printStackTrace();
			}
		}
	}
	
	public static void stop()
	{
		if(running)
		{
			gm.removeGazeListener(gazeListener);
			if(TETHandler.logtofile)
			{
				try {
					logwriter.close();
					} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("Failed to close logwriter");
					e.printStackTrace();
				}
			}
			logtofile = false;
			if(!silent) System.out.println("Stopping the eyetracking");
			running = false;
		}
		else
		{
			if(!silent) System.out.println("The eyetracking is off");
		}
	}

	public static double getX()
	{
		return x;
		
	}
	
	public static double getY()
	{
		return y;
		
	}
	
	public static double getfixX()
	{
		return fixx;
		
	}
	
	public static double getfixY()
	{
		return fixy;
		
	}
	
	public static void setfixX(double newval)
	{
		fixx = newval;
		
	}
	
	public static void setfixY(double newval)
	{
		fixx = newval;
		
	}
	
	public static void calibrate()
	{
		if(gm.isCalibrating())
		{
			gm.calibrationAbort();
		}
		if(debug) System.out.println("Starting calibration");
		gm.calibrationStart(9, calibrationHandler);
	}
	
	public static void startcalibrationpoint(int x, int y)
	{
		gm.calibrationPointStart(x + xoffset, y + yoffset);
		
	}
	
	public static void endcalibrationpoint()
	{
		gm.calibrationPointEnd();
		
	}
	
	public static void setX(double newx)
	{
		x = newx;
	}
	
	public static void setY(double newy)
	{
		y = newy;
	}
	
	public static void fixate(double x, double y, double tol)
	{
		gazeListener.setcheckfixation(true);
		fixx = x;
		fixy = y;
		fixtol = tol;
		//System.out.println("Fixating at " + Double.toString(fixx) + "/" + Double.toString(fixy));
	}
	
	public static boolean isfixating()
	{
		return gazeListener.isfixating();
	}
	
	public static boolean iswaitingforfixation()
	{
		return gazeListener.checkingfixation() && !gazeListener.enteredfixation();
	}
	public static void setoffset(int x, int y){
		xoffset = x;
		yoffset = y;
	}
}

class GazeListener implements IGazeListener
{
	static boolean keptfixation = false;
	static boolean checkfixation = false;
	static boolean entfixation = false;
	static boolean lastround = true;
	static int badcount = 0;
	static boolean onegood = false;
	static boolean gototarget = false;
    public void onGazeUpdate(GazeData gazeData)
    {
    	if(gazeData.smoothedCoordinates.x != 0 && gazeData.smoothedCoordinates.y != 0)
    	{
    		onegood = true;
    		badcount = 0;
	        TETHandler.setX(gazeData.smoothedCoordinates.x - TETHandler.xoffset);
	        TETHandler.setY(gazeData.smoothedCoordinates.y - TETHandler.yoffset);
	        if(TETHandler.logtofile)
	        {
	        	try {
	        		TETHandler.logwriter.write("Coords;" + Long.toString(System.currentTimeMillis() - TETHandler.logtime) + ";" + Double.toString(gazeData.smoothedCoordinates.x) + ";" + Double.toString(gazeData.smoothedCoordinates.y) + System.lineSeparator());
	        	} catch (IOException e) {
	        		// TODO Auto-generated catch block
	        		System.out.println("Cannot write to log");
	        		e.printStackTrace();
	        	}
	        }
	        if(checkfixation)
	        {
	        	boolean thisround = true;
	        	if(TETHandler.x <= TETHandler.fixx - TETHandler.fixtol || TETHandler.x >= TETHandler.fixx + TETHandler.fixtol || TETHandler.y <= TETHandler.fixy - TETHandler.fixtol || TETHandler.y >= TETHandler.fixy + TETHandler.fixtol)
	        	{
	        		if(entfixation)
	        		{
	        			keptfixation = false;
	        			checkfixation = false;
	        			System.out.println("Stopped fixating");
	        			TETHandler.message("Stopped fixating");
	        			
	        		}
	        		thisround = false;
	        		//System.out.println("Not fixating");
	        		
	        	}
	        	if(!lastround && thisround)
        		{
	        		entfixation = true;
	        		System.out.println("Started fixating");
	        		TETHandler.message("Started fixating");
	        		keptfixation = true;
        		}
	        	lastround = thisround;
	        }
    	}
    	else{
    		if(TETHandler.logtofile)
	        {
	        	try {
	        		TETHandler.logwriter.write("BadCoords;" + Long.toString(System.currentTimeMillis() - TETHandler.logtime) + ";" + Double.toString(gazeData.smoothedCoordinates.x) + ";" + Double.toString(gazeData.smoothedCoordinates.y) + System.lineSeparator());
	        	} catch (IOException e) {
	        		// TODO Auto-generated catch block
	        		System.out.println("Cannot write to log");
	        		e.printStackTrace();
	        	}
	        }
    		if(onegood)
    		{
    			badcount = badcount + 1;
    			if(badcount == 3 && keptfixation)
    			{
    				keptfixation = false;
        			checkfixation = false;
        			onegood = false;
        			// Can only enter fixation if the last coordinate was outside!
        			lastround = false;
        			System.out.println("Stopped fixating due to bad data");
        			TETHandler.message("Stopped fixating due to bad data");
    			}
    		}
    	}
    }
    
    public boolean isfixating()
    {
    	return keptfixation;
    }
    
    public boolean enteredfixation()
    {
    	return entfixation;
    }
    
    public boolean checkingfixation()
    {
    	return checkfixation;
    }
    
    public void setcheckfixation(boolean newstate)
    {
    	checkfixation = newstate;
    	entfixation = false;
    	onegood = false;
    	//Otherwise we cannot request new fixation while subject is already fixating
    	lastround = false;
    	System.out.println("Waiting for fixation");
    	TETHandler.message("Waiting for fixation");
    }
	
}

class CalibrationHandler implements ICalibrationProcessHandler
{

	public void onCalibrationProgress(double n)
	{
		if(TETHandler.debug) System.out.println("Calibrating");
	}
	
	public void onCalibrationResult(CalibrationResult result)
	{
		if(TETHandler.debug) System.out.println("Calibration finished");
	}
	
	public void onCalibrationProcessing()
	{
		if(TETHandler.debug) System.out.println("Calibration processing");
	}
	
	public void onCalibrationStarted()
	{
		if(TETHandler.debug) System.out.println("Calibration started");
	}
	
}
