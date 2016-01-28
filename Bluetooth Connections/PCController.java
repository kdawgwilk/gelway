/* -*- tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*- */
import lejos.pc.comm.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * A simple PC GUI used to connect and send commands to the GELway.
 * 
 * @author Steven Jan Witzand
 * @version August 2009
 */
class PCController extends JPanel
{
   // Defined movement commands
   private static final int directionLeft = 37; // left
   private static final int directionRight = 39; // right
   private static final int directionForward = 38; // up
   private static final int directionBackward = 40; // down
   private static final int reset = 10; // down

   static DataOutputStream dos;
   static NXTComm nxtComm;
   static JButton connect;
   static JButton endConnect;
   static JTextField nxt_name;
   static JTextField bluetooth;
   static JTextField text;

   public static void main(String[] args)
   {
      // Initiate bluetooth communication.
      // Create window

      JFrame frame = new JFrame("GELway Control");
      JPanel name = new JPanel();
      JPanel send = new JPanel();
      name.setLayout(new GridLayout(2, 2));
      send.setLayout(new GridLayout(1, 2));
      frame.setLayout(new GridLayout(4, 1));

      connect = new JButton("Connect NXT");
      endConnect = new JButton("End Connection");
      endConnect.setEnabled(false);
      text = new JTextField();
      // Master GELway
      nxt_name = new JTextField("GELway");
      bluetooth = new JTextField("00:16:53:09:85:B5");
      // Slave GELway
      // nxt_name = new JTextField("GELwayJR");
      // bluetooth = new JTextField("00:16:53:03:6A:A8");
      JLabel nxt_name1 = new JLabel("NXT Name:");
      JLabel blue_add = new JLabel("Bluetooth Address:");
      JTextArea help = new JTextArea(
            "Place cursor in text field on the right to send commands.");
      help.setEditable(false);
      help.setLineWrap(true);
      help.setAlignmentX(CENTER_ALIGNMENT);
      help.setBackground(blue_add.getBackground());

      name.add(nxt_name1);
      name.add(nxt_name);
      name.add(blue_add);
      name.add(bluetooth);
      send.add(help);
      send.add(text);

      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setSize(new Dimension(350, 200));
      frame.setVisible(true);
      frame.add(name);
      frame.add(connect);
      frame.add(send);
      frame.add(endConnect);

      // Put an action command for the Connect Button
      connect.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent ae)
         {
            if (ae.getSource() == connect) {
               connectNXT();
            }
         }
      });
      // Put an action command for the End Connection Button
      endConnect.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent ae)
         {
            if (ae.getSource() == endConnect) {
               try {
                     dos.close();
                     nxtComm.close();
               } catch (IOException e) {
               }
               connect.setEnabled(true);
               endConnect.setEnabled(false);
            }
         }
      });

      // add a key listener to the text filed to send commands on Bluetooth connection
      text.addKeyListener(new KeyListener()
      {
         public void keyReleased(KeyEvent e)
         {
            try {
               int keyPressed = e.getKeyCode();
               int direction;
               switch (keyPressed)
               {
                  case directionLeft:
                     direction = 4;
                     break;
                  case directionRight:
                     direction = 6;
                     break;
                  case directionForward:
                     direction = 2;
                     break;
                  case directionBackward:
                     direction = 8;
                     break;
                  case reset:
                     direction = 1;
                     break;
                  default:
                     direction = 5;
                     break;
               }
                  dos.writeInt(direction);
                  dos.flush();
            } catch (java.io.IOException e2) {
               System.out.println("IOException");
               return;
            }
         }

         public void keyPressed(KeyEvent e){}
         public void keyTyped(KeyEvent e){}
      });
      // add Enter listeners to easily connect to NXT
      nxt_name.addKeyListener(new KeyListener()
      {
         public void keyReleased(KeyEvent e)
         {
            int keyPressed = e.getKeyCode();
            if (keyPressed == KeyEvent.VK_ENTER) {
               connectNXT();
            }
         }

         public void keyPressed(KeyEvent e){}
         public void keyTyped(KeyEvent e){}
      });

      bluetooth.addKeyListener(new KeyListener()
      {
         public void keyReleased(KeyEvent e)
         {
            int keyPressed = e.getKeyCode();
            if (keyPressed == KeyEvent.VK_ENTER) {
               connectNXT();
            }
         }

         public void keyPressed(KeyEvent e){}
         public void keyTyped(KeyEvent e){}
      });
   }

   /**
    * Connect to an NXT device and open input and output streams.
    */
   public static void connectNXT()
   {
      System.out.println("Connecting to GELway...");
      nxtComm = null;
      try {
         nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
         NXTInfo nxtInfo = new NXTInfo(NXTCommFactory.BLUETOOTH, nxt_name.getText(), 
               bluetooth.getText());
         nxtComm.open(nxtInfo);
      } catch (lejos.pc.comm.NXTCommException e1) {
         System.out.println("NXTCommException " + e1.getMessage());
         return;
      }
      System.out.println("Connected.");

      // Attach output stream to the bluetooth connection
      OutputStream os = nxtComm.getOutputStream();
      dos = new DataOutputStream(os);
      connect.setEnabled(false);
      endConnect.setEnabled(true);
   }
}