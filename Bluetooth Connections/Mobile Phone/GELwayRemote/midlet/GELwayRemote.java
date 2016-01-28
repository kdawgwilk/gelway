import processing.core.*; import processing.bluetooth.*; import processing.core.*; 


public class GELwayRemote extends PMIDlet
{

    final String SOFTKEY_HELP = "Help";
    final String SOFTKEY_BAT = "Battery";
    final String SOFTKEY_BACK = "Back";
    final int STATE_START = 0;
    final int STATE_FIND = 1;
    final int STATE_CONNECTED = 2;
    final int STATE_HELP = 3;
    int state;
    Bluetooth bt;
    Service services[];
    Client cl;
    String msg;
    PFont font;

    public void setup()
    {
        font = loadFont();
        textFont(font);
        bt = new Bluetooth(this, 4353L);
        state = 0;
    }

    public void destroy()
    {
        background(255);
        fill(0);
        text("Exiting...", 2, 2, width - 4, height - 4);
        bt.stop();
        pause(2000L);
    }

    public void draw()
    {
        background(255);
        if(state == 0)
        {
            fill(0);
            PImage pimage = loadImage("icon.png");
            image(pimage, width / 2 - pimage.width / 2, 5);
            textAlign(0);
            text("NXT-Symbian v0.1\nap0cn3t@gmail.com\n\nPress any key to search for the NXT", 2, pimage.height + 10, width - 4, height - 4);
        } else
        if(state == 1)
        {
            fill(0);
            textAlign(2);
            if(services == null)
            {
                text("Looking for NXT...\n\n" + msg, 2, 2, width - 4, height - 4);
            } else
            {
                String s = "Choose NXT port:\n";
                String m = "";
                for(int i = 0; i < length(services); i++)
                {
                   // s = i + (". ") + services[i].device.name +('\n').toString();
                }

                text(s, 2, 2, width - 4, height - 4);
            }
        } else
        if(state == 2)
        {
            noFill();
            textAlign(0);
            text("-= NXTSymbian =-\n\nPress left softkey for Help\n\n\n" + msg, 2, 2, width - 4, height - 4);
        } else
        if(state == 3)
        {
            fill(0);
            textAlign(0);
            text("KEYS:\n2,4,6,8 - Drive\n5 - Break 1 - Beep\n3 - Start remotec\n9 - Stop remotec\n" +
"*#0 - Trigger1,2,3\n7 Bat status\nAny key to get back"
, 2, 2, width - 4, height - 4);
        }
    }

    public void libraryEvent(Object obj, int i, Object obj1)
    {
        if(obj == bt)
        {
            switch(i)
            {
            case 1: // '\001'
                msg = "Device found: " + ((Device)obj1).address;
                break;

            case 2: // '\002'
                msg = "Found " + length((Device[])obj1) + " devices\nSearching for serial port service...";
                break;

            case 3: // '\003'
                msg = "Found serial port on " + ((Service[])obj1)[0].device.address;
                break;

            case 4: // '\004'
                services = (Service[])obj1;
                msg = "Search complete. Pick one.";
                break;

            case 5: // '\005'
                cl = (Client)obj1;
                msg = "Client Connected!?";
                break;
            }
        }
    }

    public void softkeyPressed(String s)
    {
        if(s.equals("Help"))
        {
            state = 3;
            softkey("Back");
        } else
        if(s.equals("Back"))
        {
            state = 2;
            softkey("Help");
        }
    }

    public void keyPressed()
    {
        if(state == 0)
        {
            services = null;
            bt.find();
            state = 1;
            msg = "";
        } else
        if(state == 3)
        {
            state = 2;
        } else
        if(state == 1)
        {
            if(services != null && key >= '0' && key <= '9')
            {
                int i = key - 48;
                if(i < length(services))
                {
                    msg = "Connecting...";
                    cl = services[i].connect();
                    cl.flush();
                    state = 2;
                    softkey("Help");
                    msg = "Connected";
                }
            }
        } else
        if(state == 2)
        {
            nxtkey(keyCode, key);
        }
    }

    public void nxtkey(int i, int j)
    {
        switch(i)
        {
        case 1: // '\001'
            msg = "FWD";
            nxt_send(2);
            break;

        case 6: // '\006'
            msg = "RWD";
            nxt_send(6);
            break;

        case 2: // '\002'
            msg = "Left";
            nxt_send(4);
            break;

        case 5: // '\005'
            msg = "Right";
            nxt_send(6);
            break;

        case 3: // '\003'
        case 4: // '\004'
        default:
            switch(j)
            {
            case 49: // '1'
                msg = "Beep!";
                nxt_send(1);
                break;

            case 50: // '2'
                msg = "FWD";
                nxt_send(2);
                break;

            case 51: // '3'
                msg = "Start remotec";
                nxt_send(3);
                break;

            case 52: // '4'
                msg = "Left";
                nxt_send(4);
                break;

            case 53: // '5'
                msg = "Stop";
                nxt_send(5);
                break;

            case 54: // '6'
                msg = "Right";
                nxt_send(6);
                break;

            case 55: // '7'
                nxt_send(7);
                break;

            case 56: // '8'
                msg = "RWD";
                nxt_send(8);
                break;

            case 57: // '9'
                msg = "Stop remotec";
                nxt_send(9);
                break;

            case 42: // '*'
                msg = "Trigger1";
                nxt_send(10);
                break;

            case 48: // '0'
                msg = "Trigger2";
                nxt_send(11);
                break;

            case 35: // '#'
                msg = "Trigger3";
                nxt_send(12);
                break;
            }
            break;
        }
    }


    public void pause(long l)
    {
        try
        {
            Thread.sleep(l);
        }
        catch(Exception _ex) { }
    }

    public void nxt_send(int comm)
    {
        cl.writeInt(comm);
        cl.flush();
    }


    }

    