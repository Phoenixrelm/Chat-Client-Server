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
   final JTextArea LOG;
   final JTextField INPUT;
   JPanel panel;
   Socket s;
   OutputStream os;
   ObjectOutputStream out;
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
                           out.writeObject(senderMsg);
                           //Clear the buffer
                           out.flush();
                           
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
                           //Input      
                           BufferedReader inFromUser =
                              new BufferedReader(new InputStreamReader(System.in));
                           //datagram socket for receiving messages       
                           DatagramSocket clientSocket = new DatagramSocket();
                           //IP being used       
                           InetAddress IPAddress = InetAddress.getByName(HOST);
                                  
                           byte[] sendData    =  new byte[1024];
                           byte[] receiveData =  new byte[1024];      
                           String sentence    =  senderMsg;
                                  
                           sendData = sentence.getBytes();
                           
                           //Create a packet to be sent       
                           DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, PORT);
                           //Send message from client       
                           clientSocket.send( sendPacket );
                                  
                           //Create empty packet
                           DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length); 
                           //Fill packet with data from server      
                           clientSocket.receive( receivePacket );       
                           
                           String modifiedSentence = new String( receivePacket.getData() );      
                           System.out.println("FROM SERVER:" + modifiedSentence);
                                 
                           //Close connection
                           clientSocket.close();
                           
                           //Append message to window   
                           append(modifiedSentence);
                           //Clear input field   
                           INPUT.setText(""); 
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
      
         System.out.println("Inside TRY Line 221");
         listenForUDP udpL = new listenForUDP();
         udpL.start();
         System.out.println("Inside Try");      
      
      
         // Create output stream
         out = new ObjectOutputStream(os);
      
         // Create input stream
         is = s.getInputStream();
         ois = new ObjectInputStream(is); 
         System.out.println("Input Stream Creat");      
      
         Object obj;
      
      
         byte[] receiveData =  new byte[1024]; 
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
      }
      catch(NullPointerException npe){
      }
   
   }
   
   public static void main(String[] args){
      ChatClient cc = new ChatClient();
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
   
   
   class listenForUDP extends Thread {    
   
      public void run() {
              // Create a byte buffer/array for the receive Datagram packet
         try {
            DatagramSocket serverSocket = new DatagramSocket(PORT);
            byte[] receiveData = new byte[1024];
         
            System.out.printf("Listening on udp:%s:%d%n",
                InetAddress.getLocalHost().getHostAddress(), PORT);     
            DatagramPacket receivePacket = new DatagramPacket(receiveData,receiveData.length);
         
            while(true)
            {
               serverSocket.receive(receivePacket);
               String sentence = new String( receivePacket.getData());
               System.out.println("RECEIVED: " + sentence);
            }
         } 
         catch (IOException e) {
            System.out.println(e);
         }    
      
      }
      
   }

       
}