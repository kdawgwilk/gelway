import lejos.subsumption.*;
import lejos.util.Datalogger;
import lejos.nxt.*;
import lejos.nxt.addon.EOPD;
/**
 * A behaviour which uses a distance controller to keep the GELway at a set distance from a
 * given obstacle.
 * 
 * @author Steven Jan Witzand
 * @version August 2009
 */
public class GELwayFollower implements Behavior
{
   // KeyCodes used the drive the GELway:
   private static final int directionLeft = 4; // left
   private static final int directionRight = 6; // right
   private static final int directionForward = 2; // up
   private static final int directionBackward = 8; // down
   private static final int holdPosition = 5; // down
   MotorDirection mv;
   CtrlParam ctrl;
   UltrasonicSensor us = new UltrasonicSensor(SensorPort.S1);
   private final double K = 0.1;
   double error = 0.0;
   double setDist = 20.0;

   /**
    * GELwayFollower constructor.
    * 
    * @param ctrl
    *           The motor control parameters.
    */
   public GELwayFollower(CtrlParam ctrl)
   {
      this.ctrl = ctrl;
      mv = new MotorDirection(ctrl);
   }

   /**
    * The takeControl method always returns true as this is the lowest behaviour
    */
   public boolean takeControl() { return true }
   /**
    * No suppression required.
    */
   public void suppress(){}
   /**
    * Action method which checks the current distance from a given object and corrects this 
    * by sending a motor angle reference to the controller.
    */
   public void action()
   {
      if (ctrl.getUpright()) {
         // Measured Distance
         double dist = (double) us.getDistance();
         error = dist - setDist;
         // Power sent to the motors
         double tiltPower = (K * error);
         // Ctrl Offset Caps
         if (tiltPower > 0.5) tiltPower = 0.5;
         if (tiltPower < -0.5) tiltPower = -0.5;
         ctrl.setTiltAngle(tiltPower);
      }
      mv.delay(1);
   }
}
