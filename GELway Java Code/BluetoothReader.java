import java.io.*;
import lejos.nxt.comm.*;
/**
 * This thread constantly listens to Bluetooth commands sent to the GELway
 * 
 * @author Steven Jan Witzand
 * @version August 2009
 */
public class BluetoothReader extends Thread
{
   DataInputStream istream;
   DataOutputStream ostream;
   private int dir;
   private boolean newDir = true;
   /**
    * BluetoothReader constructor.
    * 
    * @param conn The Bluetooth Connection set up with the GELway
    */
   public BluetoothReader(BTConnection conn)
   {
      istream = conn.openDataInputStream();
      ostream = conn.openDataOutputStream();
   }
   /**
    * BluetoothReader thread which constantly runs waiting for new commands
    */
   public void run()
   {
      dir = 0;
      while (true) {
         while (true) {
            try {
               newDir = false;
               dir = istream.readInt();
               newDir = true;
               try {Thread.sleep(100);} catch (InterruptedException e) {}
               if (dir == -1) break;
            } catch (IOException e) {}
         }
      }
   }
   /**
    * Reutrns the current commands send over Bluetooth
    * @return the current command sent over Bluetooth
    */
   public int getDir() { return dir;}
   /**
    * Used to check if a new commands has been sent.
    * 
    * @return is a current commands has been sent over Bluetooth
    */
   public boolean getNewDir(){return newDir;}

   /**
    * Used to send a commands to the Bluetooth device the robot is connected with.
    * 
    * @param command command to be sent to connected Bluetooth device
    */
   public void sendCommand(int command)
   {
      try {
         ostream.writeInt(command);
         ostream.flush();
      } catch (IOException e) {}
   }
}