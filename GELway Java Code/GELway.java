/* -*- tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */
import lejos.nxt.*;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import javax.bluetooth.RemoteDevice;
import lejos.subsumption.*;
import lejos.util.Datalogger;
/**
 * This is the GELways main program. It initiates the Bluetooth connection and establishes
 * the balancing and behavioural threads. Note this program has been based off Marvin the
 * Balancing robot and has been modified by myself. As such, they should be properly
 * referenced if you intend on modifying this code. The creators of Marvin are Bent Bisballe
 * Nyeng, Kasper Sohn and Johnny Rieper
 * 
 * @author Steven Jan Witzand
 * @version August 2009
 */
class GELway extends Thread
{
   static CtrlParam ctrl;
   static BluetoothReader slave;
   static BluetoothReader br;
   public static void main(String[] args)
   {
      ctrl = new CtrlParam();
      // loadMaster(); // Load the Master GELway settings
      loadSlave(); // Load the Slave GELway settings
      // loadNormal(); // Load the normal GELway settings
   }
   /**
    * Loads the master setting for the robot. Establishes the remote control and slave
    * Bluetooth connections.
    */
   public static void loadMaster() {
      LCD.drawString("Waiting...", 2, 1);
      BTConnection conn = Bluetooth.waitForConnection();
      conn.setIOMode(0); // Used when a pc connection is made
      // conn.setIOMode(NXTConnection.RAW); // Used when a phone connection is made
      LCD.clear();
      // Start Bluetooth reader thread
      br = new BluetoothReader(conn);
      br.start();
      connectMasterSlave();
      // Start Balance control thread
      BalanceController bc = new BalanceController(ctrl);
      bc.start();
      Behavior b1 = new GELwayDriver(ctrl, br); // Needed to drive robot and slave
      Behavior b2 = new DetectObstacle(ctrl); // Needed to avoid obstacles
      Behavior[] bArray = { b1, b2 };
      Arbitrator arby = new Arbitrator(bArray);
      arby.start();
   }
   /**
    * Loads the slave setting for the robot. Has the follower behaviour to keep GELway at a
    * set distance
    */
   public static void loadSlave() {
      LCD.drawString("Waiting...", 2, 1);
      BTConnection conn = Bluetooth.waitForConnection();
      conn.setIOMode(0); // Used when a pc connection is made
      // conn.setIOMode(NXTConnection.RAW); // Used when a phone connection is made
      LCD.clear();
      // Start Bluetooth reader thread
      br = new BluetoothReader(conn);
      br.start();
      // Start Balance control thread
      BalanceController bc = new BalanceController(ctrl);
      bc.start();
      // Behavior b1 = new GELwayDriver(ctrl, br); // Needed to drive robot and slave
      Behavior b1 = new GELwayFollower(ctrl); // Needed to avoid obstacles
      // Behavior b2 = new SlaveObstacle(ctrl, br); // Needed to avoid obstacles
      Behavior[] bArray = { b1 };
      Arbitrator arby = new Arbitrator(bArray);
      arby.start();
   }
   /**
    * Loads operator settings for GELway when only one robot is used
    */
   public static void loadNormal() {
      LCD.drawString("Waiting...", 2, 1);
      BTConnection conn = Bluetooth.waitForConnection();
      // conn.setIOMode(0); // Used when a pc connection is made
      conn.setIOMode(NXTConnection.RAW); // Used when a phone connection is made
      LCD.clear();
      // Start Bluetooth reader thread
      br = new BluetoothReader(conn);
      br.start();
      // Start Balance control thread
      // BalanceController bc = new BalanceController(ctrl);
      BalanceController bc = new BalanceController(ctrl);
      bc.start();
      LCD.clear();
      Behavior b1 = new GELwayDriver(ctrl, br); // Needed to drive robot and slave
      // Behavior b1 = new GELwayFollower(ctrl);
      Behavior b2 = new DetectObstacle(ctrl); // Needed to avoid obstacles
      // Behavior b3 = new KeepStraight(ctrl); // Needed to keep straight
      Behavior[] bArray = { b1, b2 };
      Arbitrator arby = new Arbitrator(bArray);
      arby.start();
   }
   /**
    * This method is used to connect the Master GELway to the Slave GELway.
    */
   public static void connectMasterSlave() {
      LCD.drawString("Master Connected", 0, 4);
      LCD.drawString("Press Enter to", 1, 6);
      LCD.drawString("Connect Slave", 2, 7);
      try {Button.ENTER.waitForPressAndRelease();} catch (InterruptedException e1) {}
      LCD.clear();
      String name = "GELwayJR";
      LCD.drawString("Connecting...", 0, 0);
      LCD.refresh();
      RemoteDevice btrd = Bluetooth.getKnownDevice(name);
      if (btrd == null) {
         LCD.clear();
         LCD.drawString("No such device", 0, 0);
         LCD.refresh();
         try {Thread.sleep(2000);} catch (InterruptedException e) {}
         System.exit(1);
      }
      BTConnection btc = Bluetooth.connect(btrd);
      // btc.setIOMode(0);
      if (btc == null) {
         LCD.clear();
         LCD.drawString("Connect fail", 0, 0);
         LCD.refresh();
         try {Thread.sleep(2000);} catch (InterruptedException e) {}
         System.exit(1);
      }
      LCD.clear();
      LCD.drawString("Connected Slave", 0, 0);
      LCD.refresh();
      slave = new BluetoothReader(btc);
      slave.start();
   }
}