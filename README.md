Java class that allows to retrieve EyeTribe data in Matlab.

add .jar file to matlab classpath using

javaaddpath('[yourpath]/TETtoMatlab.jar')
import main.*

this adds the TETHandler class to Matlab. The Eyetribe has to be calibrated first.
Start calibration procedure with

TETHandler.calibrate

then show 9 calibration points and start sampling at each point with

TETHandler.startcalibrationpoint(x, y)

and end sampling with:

TETHandler.endcalibrationpoint

Once calibrated coordinates can be retrieved using:

TETHandler.getX
TETHandler.getY

A fixation point can be added using TETHandler.fixate(x, y). The java class will then check if the subject
is fixating this position.
TETHandler.checkfixation will return true once fixation has been entered and while it has not been left again.