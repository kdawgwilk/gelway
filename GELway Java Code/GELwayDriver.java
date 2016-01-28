import lejos.subsumption.*;
import lejos.nxt.*;

/**
 * A behaviour which allows the GELway to receive Bluetooth commands to turn them into 
 * actions in the robot. Note that this behaviour requires an active Bluetooth connection.
 * 
 * @author Steven Jan Witzand
 * @version August 2009
 */
public class GELwayDriver implements Behavior
{

   // KeyCodes:
   private static final int directionLeft = 4; // left
   private static final int directionRight = 6; // right
   private static final int directionForward = 2; // up
   private static final int directionBackward = 8; // down
   private static final int holdPosition = 5; // down

   BluetoothReader br;
   MotorDirection mv;
   CtrlParam ctrl;

   /**
    * GELwayDriver constructor.
    * 
    * @param ctrl
    *           The motor control parameters.
    * @param br
    *           An active Bluetooth connection.
    */
   public GELwayDriver(CtrlParam ctrl, BluetoothReader br)
   {
      this.ctrl = ctrl;
      mv = new MotorDirection(ctrl);
      this.br = br;
   }

   /**
    * Trigger for the Behaviour. Note that this always reutrns true since it is the lowest
    * level behaviour.
    */
   public boolean takeControl()
   {
      return true;
   }
   /**
    * No suppression required.
    */
   public void suppress()
   {

   }
   /**
    * Action method which converts a sent command into a movement action in the robot.
    */
   public void action()
   {
      if (br.getNewDir()) {
         LCD.clear();
         int number = br.getDir();
         switch (number)
         {
            case directionLeft:
               LCD.drawString("LEFT", 5, 5);
               mv.left(200);
               break;

            case directionRight:
               LCD.drawString("RIGHT", 5, 5);
               mv.right(200);
               break;

            case directionForward:
               LCD.drawString("FORWARD", 3, 5);
               ctrl.setLeftMotorOffset(0);
               ctrl.setRightMotorOffset(0);
               ctrl.resetDamp();
               mv.forward(100);
               // ctrl.setDriveState(1);
               break;

            case directionBackward:
               LCD.drawString("BACKWARD", 3, 5);
               ctrl.setLeftMotorOffset(0);
               ctrl.setRightMotorOffset(0);
               ctrl.resetDamp();
               mv.backward(100);
               // ctrl.setDriveState(2);
               break;

            case holdPosition:
               LCD.drawString("Stay", 3, 5);
               // mv.stop(200);
               ctrl.setDriveState(0);
               break;

            default:
               break;
         }
      }
   }
}
