import processing.core.*; 
import processing.bluetooth.*; 
public class GELwayRemote extends PMIDlet{
/*
 * GELwayRemote
 * This program is used to connect a mobile device to the GELway robot. The mobile 
 * is used as a remote control and controls the GELway's movements. Each button on 
 * the mobile phone sends a integer command which can is received by the GELway. The
 * mobile phone does not actually drive the GELway around, it just sends an integer 
 * value. The GELway uses the received integers to handle events such as moving 
 * around and resetting the robot.
 *
 * @Author Steven Witzand
 * @Version 0.2
 * @Note This program is based off Pedro Miguel's NXTSymbian v0.1 and has been 
 * modified to suit the needs of the GELway robot.
 */


final String SOFTKEY_HELP = "Help";
final String SOFTKEY_BAT   = "Battery";
final String SOFTKEY_BACK   = "Back";

final int STATE_START = 0;
final int STATE_FIND = 1;
final int STATE_CONNECTED = 2;
final int STATE_HELP = 3;

int state;

Bluetooth bt;
Service[] services;
Client cl;

String msg;
PFont font;

/**
 * Setup method used load the font and the type of Bluetooth connection
 */
public void setup() 
{
  font = loadFont();
  textFont(font);
  bt = new Bluetooth(this, Bluetooth.UUID_SERIALPORT);
  state = STATE_START;
}

/**
 * Called when the program is exiting, stopping the Bluetooth connection.
 */
public void destroy() 
{
  background(0,226,167);
  fill(0);
  text("Exiting...", 2, 2, width - 4, height - 4);
  bt.stop();
  pause(2000);
}

/**
 * Setup method used load the font and the type of Bluetooth connection
 */
public void draw() 
{
  background(0,226,167); // background colour of the program
  // start up screen of the program
  if (state == STATE_START) 
  {
    fill(0);
    PImage b;
    b = loadImage("GELway2.png");
    image(b, (width/2)-(b.width/2), 5);
    textAlign(CENTER);
    text("GELwayRemote v0.2\nModified By Witzand\n\nPress any key to search for...
          ...active Bluetooth devices.", 2, b.height + 10, width - 4, height - 4);
  }
  // state used to search for active Bluetooth devices. Also used to connect to
  // establish a Bluetooth connection.
  else if (state == STATE_FIND) 
  {
    fill(0);
    textAlign(LEFT);
    if (services == null) {text("Looking for NXT...\n\n" + msg, 2, 2,... 
          ...width-4, height-4);}
    else 
    {
      String msg_aux = "Choose NXT port:\n";
      for (int i = 0; i < length(services); i++)
        msg_aux += i + ". " + services[i].device.name + "\n";
      text(msg_aux, 2, 2, width-4, height-4);
    }
  }
  // Screen used to display what current command has been sent to the GELway.
  else if (state == STATE_CONNECTED) 
  {
    noFill();
    textAlign(CENTER);
    text("GELway Remote\n\nPress left softkey for Commands\n\n\n" + msg, 2, 2,...
           ...width-4, height-4);
  }
  // State which describes what each mobile phone button does.
  else if (state == STATE_HELP) 
  {
    fill(0);
    textAlign(CENTER);
    text("Commands:\n2: Forward\n4: Left\n6: Right\n8: Reverse\n\n1:...
           ...Reset GELway\n\nPress any key to return", 2, 2, width-4, height-4);
  }
}

/**
 * This method handle searching for, connection to, and handling of the Bluetooth 
 * connection.
 * @Param library type of library sent to the method, expecting to be Bluetooth 
 *        library.
 * @Param event type of Bluetooth event
 * @Param data the Bluetooth datatype, holding information such as Bluetooth name 
 *        and address
 */
public void libraryEvent(Object library, int event, Object data) {
  if (library == bt) {
    switch (event) {
    case Bluetooth.EVENT_DISCOVER_DEVICE:
      msg = "Device found: " + ((Device) data).address;
      break;
    case Bluetooth.EVENT_DISCOVER_DEVICE_COMPLETED:
      msg = "Found " + length((Device[]) data) + " devices...
              ...\nSearching for serial port service...";
      break;
    case Bluetooth.EVENT_DISCOVER_SERVICE:      
      msg = "Found serial port on " + ((Service[]) data)[0].device.address;
      break;
    case Bluetooth.EVENT_DISCOVER_SERVICE_COMPLETED:
      services = (Service[]) data;
      msg = "Search complete. Pick one.";
      break;
    case Bluetooth.EVENT_CLIENT_CONNECTED:
      cl = (Client) data;
      msg = "Client Connected!?"; 
      break;
    }
  }
}

/**
 * This method brings up the help screen and returns back to the main screen.
 * @Param label string which is used to select states for the program.
 */
public void softkeyPressed(String label) 
{
  if (label.equals(SOFTKEY_HELP)) 
  {
    state = STATE_HELP;
    softkey(SOFTKEY_BACK);
  }
  else if(label.equals(SOFTKEY_BAT)) {}
    else if(label.equals(SOFTKEY_BACK)) 
    {
    state = STATE_CONNECTED;
    softkey(SOFTKEY_HELP);
  }
}

/**
 * This method handles key events in the programs and alters the state according to 
 * which buttons are pressed.
 */
public void keyPressed() 
{
  if (state == STATE_START) 
  {
    services = null;
    bt.find();
    state = STATE_FIND;
    msg = "";
  }
  else if(state == STATE_HELP) 
  {
    state = STATE_CONNECTED;
  } 
  else if (state == STATE_FIND) 
  {
    if (services != null) 
    {
      if ((key >= '0') && (key <= '9')) 
      {
        int i = key - '0';
        if (i < length(services)) 
        {
          msg = "Connecting...";
          cl = services[i].connect();
          state = STATE_CONNECTED;
          softkey(SOFTKEY_HELP);
          msg = "Connected";
        }
      }
    }
  }
  else if (state == STATE_CONNECTED) 
  {
    nxtkey(keyCode,key);
  }
}

/**
 * This method is called when a button is pressed by the mobile and sends the 
 * corresponding integer
 * corresponding to what button was pressed.
 * @Param aKeyCode what joystick button was pressed
 * @Param akey what mobile button is pressed
 */
public void nxtkey(int akeyCode, int akey) 
{
  // joystick buttons
  switch(akeyCode) 
  {
    case UP:
      msg = "FWD";
      //nxt_send(fwd);
      nxt_send(2);
      break; 
    case DOWN:
      msg = "RWD";
      nxt_send(8);
      break;
    case LEFT:
       msg = "Left";
      nxt_send(4);
      break;
    case RIGHT:
      msg = "Right";
      nxt_send(6);
      break;
    // mobile buttons pressed
    default:
      switch(akey) 
      {
      case '1':
        msg = "Reset";
        nxt_send(1);
        break;
      case '2':
        msg = "FWD";
        nxt_send(2);
        break;
      case '3':
        msg = "...";
        nxt_send(3);
        break;
      case '4':
        msg = "Left";
        nxt_send(4);
        break;
      case '5':
        msg = "Stop";
        nxt_send(5);
        break;
      case '6':
        msg = "Right";
        nxt_send(6);
        break;
      case '7':
        nxt_send(7);
        break;
      case '8':
        msg = "RWD";
        nxt_send(8);
        break;
      case '9':
        msg = "Stop remotec";
        nxt_send(9);
        break;
      case '*':
        msg = "Trigger1";
        nxt_send(10);
        break;
      case '0':
        msg = "Trigger2";
        nxt_send(11);
        break;
      case '#':
        msg = "Trigger3";
        nxt_send(12);
        break;
      }
    }
}

/**
 * Delays the programs for a specified period of milliseconds
 * @Param millis how long the program is delayed, in milliseconds.
 */
public void pause(long millis) {
  try { Thread.sleep(millis);  }  catch( Exception e) { }
}  

/**
 * This method sends and flushes commands to the GELway over Bluetooth
 * @Param command integer to be sent to the GELway
 */
public void nxt_send(int command){
  cl.writeInt(command);
  cl.flush(); 
}

/**
 * This method receives information from the GELway. Note this is currently not 
 * used in the program, but was kept for future work.
 */
public byte[] nxt_rcv() {
  byte[] buffer = null;
  int length = -1;
  do {
    length = cl.read();
  } while (length < 0);
  int len_aux = cl.read();
  length = (0xFF & length) | ((0xFF & len_aux) << 8);
  buffer = new byte[length];
  cl.readBytes(buffer);
  return buffer;
}

}