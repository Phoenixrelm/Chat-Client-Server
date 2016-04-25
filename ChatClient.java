import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

/**
Dev by: Ryan Mason, Chris Dumlao
due Date: 5/2/146
Title: Chat Client
This client will be used to send messages to the server and receive them.
****IMPORTANT YOU NEED TO PUT THE IP OF THE COMPUTER THAT YOU ARE RUNNING THE SERVER ON****
*/


public class ChatClient{
   private final int PORT = 16789;
   private String  HOST = "Localhost";
   private static DatagramSocket dgm;
   private static DatagramPacket rPacket, sPacket;
   final JTextArea LOG;
   final JTextField INPUT;
   JPanel panel;
   Socket s;
   OutputStream os;
   ObjectOutputStream outStream;
   InputStream is;
   ObjectInputStream ois;
   JFrame frame = new JFrame("Network Protocol");
   String protocol = null;
  
   public ChatClient(){
   
   ///////////////////Check Protocol///////////////////////
      Object[] options = {"TCP/IP",
                    "UDP"};
      int n = JOptionPane.showOptionDialog(frame,
         "Please Select a Protocol",
         "Network Protocol",
         JOptionPane.YES_NO_OPTION,
         JOptionPane.QUESTION_MESSAGE,
         null,     //do not use a custom Icon
         options,  //the titles of buttons
         options[0]); //default button title
    
    
      HOST = JOptionPane.showInputDialog("Please enter the server's IP address: \nDefaults to localHost");
      
      System.out.println("HOST = "+ HOST);
   
   ///////////////////Check Protocol///////////////////////
   
   //creation of the gui
      JFrame window = new JFrame();
      panel = new JPanel(new BorderLayout());
      JButton sendButton = new JButton("Send");
      sendButton.setEnabled(false);
      window.getRootPane().setDefaultButton(sendButton);
   
      JMenuBar menuBar = new JMenuBar();
     
      JMenu fileMenu = new JMenu("File");
      menuBar.add(fileMenu);
   
      JMenuItem exitMenu = new JMenuItem("Exit");
      fileMenu.add(exitMenu);
      
      window.setJMenuBar(menuBar);
   
   // Log (main text on top)
      LOG = new JTextArea();
      JScrollPane scrollPaneLOG = new JScrollPane(LOG);
      LOG.setRows(25);
      LOG.setEditable(false);
      
      // INPUT (user input on bottom)
      INPUT = new JTextField();
      window.addWindowListener( 
         new WindowAdapter() {
            public void windowOpened( WindowEvent e ){
               INPUT.requestFocus();
            }
         }); 
      JScrollPane scrollPaneINPUT = new JScrollPane(INPUT);
      INPUT.setMinimumSize(new Dimension(250, 10));
      INPUT.setPreferredSize(new Dimension(250, 10));   
      
      panel.add(scrollPaneLOG , BorderLayout.NORTH);
      panel.add(scrollPaneINPUT , BorderLayout.CENTER);
      panel.add(sendButton , BorderLayout.EAST);
          
      window.add(panel);
      window.setVisible(true);
      window.pack();
      window.setLocationRelativeTo(null);
      window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   
      ////////////////////////////////TCP or UDP socket select///////////////////////////////////  
      //if press tcp
      if (n == JOptionPane.YES_OPTION){
         protocol = "tcp";
      //make some if pressed tcp Conmnect to server using TCP Socket
         System.out.println("tcp Protocol");
         try{
            s = new Socket(HOST, PORT);
            os = s.getOutputStream();
         
            sendButton.setEnabled(true);
         }
         //Exceptions
         catch(UnknownHostException ukh){
         }
         catch(IOException ioe){
         }
      }
      else if(n == JOptionPane.NO_OPTION){
         protocol = "udp";
         sendButton.setEnabled(true);            
      }
      else{
         System.out.println("Hey you closed me :(");// pressed close
         System.exit(0);
      }

      /*
       * Send button event
       */      
      sendButton.addActionListener(
            new ActionListener(){
            
               public void actionPerformed(ActionEvent ae){
               
                 // TEXT_INPUT: takes in the users input and sends it to the server\
                  String senderMsg = null;
                  senderMsg = INPUT.getText();
                  //Validates that a message was typed
                  if(senderMsg.length() == 0){
                     System.out.println("ERROR: NoTextInputed");
                  }
                  else{
                     //Check which protocol
                     //Check if TCP
                     if(protocol == "tcp"){   
                        try {
                           //See what message is about to be sent
                           System.out.println("ABOUT TO SEND: " +senderMsg);
                           
                           //Send message to outputstream
                           outStream.writeObject(senderMsg);
                           //Clear the buffer
                           outStream.flush();
                           
                           //reset output field
                           INPUT.setText("");
                        }
                        catch(UnknownHostException uhe) {
                           append("Unable to connect to host."); 
                        }
                        catch(IOException ie) {   
                           append("Unable to send message"); 
                        }
                     }
                     //Checks if UDP                        
                     else if(protocol.equals("udp") ){
                        System.out.println("UDP Send button");//pressed udp
                     
                        try {
                        do{
                            DatagramPacket receivePacket = null;
                            DatagramPacket sendPacket    = null;
                            
                            //Input from user     
                            BufferedReader inFromUser =
                                 new BufferedReader(new InputStreamReader( System.in ));
                                 
                           //datagram socket for connection     
                            DatagramSocket clientSocket  = new DatagramSocket();
                            
                            //IP being used       
                            InetAddress IPAddress = InetAddress.getByName(HOST);
                            
                           //do{                                
                              byte[] sendData    =  new byte[1024];
                                    
                              
                              //Users Message
                              String sentence    =  senderMsg;
                                     
                              sendData = sentence.getBytes();
            
                              //Create a packet to be sent       
                              sendPacket = 
                                 new DatagramPacket(
                                    sendData, 
                                    sendData.length, 
                                    IPAddress, 
                                    PORT
                              );
                              
                              //Send message from client       
                              clientSocket.send( sendPacket );
                                     
                              //Receive Packets
                              /*
                              receivePacket = 
                                 new DatagramPacket(
                                    receiveData, 
                                    receiveData.length
                              );
                                     
                              clientSocket.receive( receivePacket );       
                              
                              String modifiedSentence = new String( receivePacket.getData() );      
                              System.out.println("FROM SERVER:" + modifiedSentence);
                                    
                              //Close connection
                              //clientSocket.close();
                              
                              Append message to window   
                              append(modifiedSentence);
                              //Clear input field   
                              INPUT.setText(""); 
                             // printOut( "Client has connection: " + clientSocket.isConnected() );
                              */
                           }while( true );
                        }
                        catch(UnknownHostException uhe) {
                           append("Unable to connect to host.");
                        }
                        catch(IOException ie) {   
                           append("Unable to send message");
                        } 
                     }//end of if else(udp)
                  }//End of else
               }//end of Action Performed
            });
     
      //Exit Client
      exitMenu.addActionListener(
            new ActionListener(){  
               public void actionPerformed(ActionEvent ae){
                  System.exit(0);
               }
            });
            
      try{
      
         dgm = new DatagramSocket();
     
         byte[] receiveData =  new byte[1024];
         
         //Receiving Packet
         rPacket = 
            new DatagramPacket(
						receiveData, 
                  receiveData.length
            );
         //receive packet
         dgm.receive( rPacket ); 
         
         
         String receivedMsg = 
            new String(
               rPacket.getData(),
					0, 
               rPacket.getLength()
         );
         
         printOut(receivedMsg);
      
         // Create output stream
        outStream = new ObjectOutputStream(os);
      
         // Create input stream
         is = s.getInputStream();
         ois = new ObjectInputStream(is); 
           
         Object obj;
         
         while(true){
            obj = ois.readObject();
         
            append(obj.toString());
         }
      
      }
      catch(IOException ioe){
         append("Server Offline...");
         JOptionPane.showMessageDialog(null, "Server Offline...");
         System.exit(0);                     
      }
      catch(ClassNotFoundException cnfe){
         append(" 2" +cnfe.getMessage());
         //ErrorDialog ed = new ErrorDialog("Error",  "Unexpected Error has occured");
         printOut("Class not found err");
      }
      catch(NullPointerException npe){
         printOut("Null pointer exception");
      }

   
   }

   public static void main(String[] args){
      ChatClient cc = new ChatClient();
   }
   
   
   public static void printOut(String msg){
      System.out.println(msg);
   }
   
  /**
   *Appends string to the chat window
   *@String s - string to be appended to the window
   */
   public void append(String s)
   {
      LOG.append(s + "\n");
      panel.scrollRectToVisible(LOG.getBounds());
      LOG.setCaretPosition(LOG.getText().length());
   }      
}