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
   DatagramSocket clientSocket;
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
    System.out.println("HOST = "+HOST);
   
   ///////////////////Check Protocol///////////////////////
   
   //creation of the gui
      JFrame window = new JFrame();
      panel = new JPanel(new BorderLayout());
      JButton sendButton = new JButton("Send");
      sendButton.setEnabled(false);
   
   
   
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
      catch(UnknownHostException ukh){
      
      }
      catch(IOException ioe){
      }
    }else if(n == JOptionPane.NO_OPTION){
      try{
       protocol = "udp";
       clientSocket = new DatagramSocket();
       sendButton.setEnabled(true);

      BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

      byte[] receiveData = new byte[1024];

      String sentence = inFromUser.readLine();
      }
      catch(UnknownHostException ukh){ }
      catch(IOException ioe){ }             
    }
    else{
      System.out.println("Hey you closed me :(");// pressed close
      
      System.exit(0);

    }
 
      
      
      
      
      
   //////////////////////////////////////////////////////////////////   
            
      sendButton.addActionListener(
            new ActionListener(){
            
               public void actionPerformed(ActionEvent ae){
               
                 // TEXT_INPUT: takes in the users input and sends it to the server\
                  String senderMsg = null;
                  senderMsg = INPUT.getText();
                   if(senderMsg != "" || senderMsg != null){
                     if(protocol == "tcp")   
                        try {
                           System.out.println("ABOUT TO SEND: " +senderMsg);
                           out.writeObject(senderMsg);
                           out.flush();
                           INPUT.setText("");
                           }
                        catch(UnknownHostException uhe) {
                           append("Unable to connect to host.");
                        
                        }
                        catch(IOException ie) {   
                        
                        }                        
                      else if(protocol == "udp"){
                     System.out.println("UDP Send button");//pressed udp

                          try {
                               byte[] sendData = senderMsg.getBytes();
                               InetAddress address = InetAddress.getByName(HOST);
         
                               DatagramPacket sendPacket = new DatagramPacket(sendData,sendData.length, address,PORT);
                               
                               System.out.println("ABOUT TO SEND: " +sendPacket+ "NO Function to send");
                               
                                clientSocket.send(sendPacket);
            
                                // DatagramPacket receivePacket =
                                //    new DatagramPacket(receiveData, receiveData.length);
                           
                                // clientSocket.receive(receivePacket);
                                 
                                 
                           
                                 //String modifiedSentence = new String(receivePacket.getData(),
                                                                    //  0, receivePacket.getLength());
                           
                                // System.out.println("FROM SERVER: " + modifiedSentence);
                           
                                 clientSocket.close();
                          }
                          catch(UnknownHostException uhe) {
                                 append("Unable to connect to host.");
                        
                          }
                           catch(IOException ie) {   
                        
                          }
   
                      }
                      else{
                      }           
                     
                     
                     
                     }
                  else{
                     System.out.println("CAN NOT SEND: " +senderMsg);
                  }
                     
                     
                     
                     
                     

               }
            });
     
      exitMenu.addActionListener(
            new ActionListener(){
            
               public void actionPerformed(ActionEvent ae){
                  System.exit(0);
               }
            });
   
        
      try{
         // Create output stream
         out = new ObjectOutputStream(os);
      
         
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
      }
   
   }
   public static void main(String[] args){
   
      ChatClient cc = new ChatClient();
   }
   
  
   public void append(String s)
   {
      LOG.append(s + "\n");
      panel.scrollRectToVisible(LOG.getBounds());
      LOG.setCaretPosition(LOG.getText().length());
   }      
}