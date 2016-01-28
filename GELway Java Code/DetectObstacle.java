import lejos.subsumption.*;
import lejos.subsumption.Behavior;
import lejos.nxt.*;

/**
 * A behaviour which allows the GELway to avoid obstacles using the Ultrasonic Sensor.
 * 
 * @author Steven Jan Witzand
 * @version August 2009
 */
public class DetectObstacle implements Behavior
{
   CtrlParam ctrl = new CtrlParam();
   MotorDirection mv;
   UltrasonicSensor us = new UltrasonicSensor(SensorPort.S1);

   /**
    * DetectObstacle constructor.
    * 
    * @param ctrl
    *           The motor control parameters.
    */
   public DetectObstacle(CtrlParam ctrl)
   {
      this.ctrl = ctrl;
      mv = new MotorDirection(ctrl);
   }

   /**
    * Trigger for the Behaviour. This trigger is actioned when a distance less than 25cm is
    * detected.
    */
   public boolean takeControl()
   {
      return (us.getDistance() < 25);
   }

   /**
    * No suppression required.
    */
   public void suppress()
   {
   }

   /**
    * Action method which stops the robots current movements, reverses the robot and turns
    * 180 degrees.
    */
   public void action()
   {
      ctrl.setLeftMotorOffset(0);
      ctrl.setRightMotorOffset(0);
      ctrl.setDriveState(0);
      mv.backward(200);
      mv.right(1800);
      mv.stop(1000);
      mv.backward(100);
   }

}
