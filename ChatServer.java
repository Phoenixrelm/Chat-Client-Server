import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

/**
@author: Ryan Mason, Chris Dumlao
Title: Chat Server
This Server will be used to receive messages from the client and then send it to all other clients.
*/

public class ChatServer{
   final int PORT = 16789;
   ServerSocket ss = null;
   Object message;
   //UDP 
   public DatagramSocket udpSocket; // Socket open for UDP
   public ArrayList<InetAddress> clientIPs; //List of IP addresses communicating via UDP
   public ArrayList<Integer>   clientPorts; //List of Ports used for communicating via UDP
   public HashSet<String>  connectedClients;//List of Unique clients connected and communicating on our server 
                                            //Each item is a unqiue String composed of an IP address and a Port 
   
   //TCP What are these doing?
   public Vector<ClientThread> ctVector = new Vector<ClientThread>();
   public Vector<ObjectOutputStream> clients = new Vector<ObjectOutputStream>();
   public Vector<Socket> sockets = new Vector<Socket>();
  
   String timeStamp;
   
   /**Default Constructor
    *on instatiation creates GUI, and starts two threads.
    *One handling TCP communication and the other handling UDP communication.
    *
    */
   public ChatServer(){
         
     //server GUI
      JFrame window = new JFrame();
      JPanel middle = new JPanel(new GridLayout(1,1));
      JLabel jtField = new  JLabel(); 
      //Add the IP of the machine hosting the server to the GUI
      try{
         jtField.setText("This IP is: "+ InetAddress.getLocalHost());
      }
      catch(UnknownHostException uhe){
         System.err.println(uhe.getMessage());
      }
         
      middle.add(jtField);
      window.add(middle);
      window.setVisible(true);
      window.pack();
      window.setLocationRelativeTo(null);
      window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      //end of gui
     
     //Accept incoming 
      try{
         //TCP Server Socket
         ss = new ServerSocket(PORT);                
         Socket cs = null;
         String ip;
         
         //UDP
         udpSocket = new DatagramSocket( PORT );
         clientIPs = new ArrayList();
         clientPorts = new ArrayList();
         connectedClients = new HashSet();
         
         // waits for client to connect, starts thread, adds to client Vector
         while(true){
           
           //Start thread that will handle UDP Communcation
            UDPThread udpThread = new UDPThread();
            udpThread.start();
            System.out.println("Waiting for TCP Client .accept");      

            //Accept communication on out socket
            //Start thread that will handle TCP Communication
            cs = ss.accept();   
            System.out.println("SS.accept");      
            ClientThread ct = new ClientThread(cs);
            ct.start();
            ctVector.add(ct);
            System.out.println("Message from " +getIP(cs));
            
         }
      }
      catch(IOException ioe){
         System.err.println(ioe.getMessage());
      }
   
   }//end of ChatServer Class - The GUI

   //Execute Chat Server
   public static void main(String[] args){
      ChatServer cs = new ChatServer();
   }

  /**Thread for TCP Communication
   *Thread listens for incoming messages
   *and sends out message to all clients
   */
   class ClientThread extends Thread {
      
      Socket cs;
      /**Constructor
       *@param cs - Socket 
       */
      public ClientThread(Socket cs){
         this.cs = cs;
      }
        
      public void run() {
         
         OutputStream os;
         ObjectOutputStream out;
         
         try{
            // takes in the string from the client Socket
            InputStream is = cs.getInputStream();  
            ObjectInputStream ois = new ObjectInputStream(is);  
            os = cs.getOutputStream();
            out = new ObjectOutputStream(os); 
            
            //add objectoutput stream of to array
            clients.add(out);
            Object obj;
                     
            //print it to the client socket
                        
            while(true){    
               // read the objects from ois to obj
               obj = ois.readObject(); 
            
            //Determine what kind of object we got
            //if object is a string
               if(obj instanceof String){
                  message = (String)obj;
               
               //sends the message to all the clients
               //iterates through array of clients connected
                  for(int i=0; i < clients.size(); i++){
                     //get output stream of current iteration
                     ObjectOutputStream temp = clients.get(i);
                     try{
                        //Printing out to server console how many clients connected and which client is
                        //about to receive a message
                        System.out.println("Size: " +clients.size() +" Trying to send to client# "  + i);
                        
                        //Write message out to the current iterations outputstream
                        temp.writeObject("["+getCurrentTime()+ "] IP:" + getIP(cs) + ": " + message);
                        //flush output stream memory
                        temp.flush();
                     }
                     catch(IOException ioe){
                        //If failed to write to outputstream
                        //remove that outputstream
                        System.out.println("Client Disconnected #"  + i);
                        clients.remove(i);  
                     }         
                  }//end of for loop 
               }         
            }
         }//end of try
         catch(ClassNotFoundException CNFE){
            System.exit(0);
         }
         catch(IOException ioe){
            System.exit(0);       
         } 
                                                 
      }//end of run   
   }//end of tcp thread class
   
   /**Thread for UDP Communication
   *Thread listens for incoming messages
   *and sends out message to all clients
   */
   class UDPThread extends Thread { 
   
      //Buffer
      byte[] byteBuffer; 
      
      public void run() {
         while(true){
            try{
               // sets/resets buffer size
               byteBuffer = new byte[1024];
               
               //create new Packet
               DatagramPacket udpPacket = new DatagramPacket( byteBuffer, byteBuffer.length );
               
               //Receive
               udpSocket.receive( udpPacket );
               //New Timestamp of when the message was received;
               String newTimeStamp = "[" + getCurrentTime() + "]"; 
               
               //Create string with received message from bytes
               String receivedMsg = new String( byteBuffer, byteBuffer.length );
               
               //Get the IP and Port of where the message came from
               InetAddress currentClientIP = udpPacket.getAddress();
               int currentClientPort       = udpPacket.getPort();
               
               //Unique string to keep track of clients
               String currentClient = "Client IP: " + currentClientIP.toString() + " | Port: " + currentClientPort;
                  
                  //If the client that just sent the 
                  //Received message has not connected before
                  //Add the client to list of connected clients,
                  //And store their IP and PORT
                  if( !connectedClients.contains( currentClient ) ){
                     connectedClients.add( currentClient );
                     clientIPs.add( currentClientIP );
                     clientPorts.add( currentClientPort );
                     System.out.println( "Added new client | " + currentClient  );
                  }
                  
               String outgoingMsg = "["+newTimeStamp+"] IP:" + currentClientIP.toString() + " : " + receivedMsg;
               
               //Convert to bytes
               byte[] outgoingByte = ( outgoingMsg ).getBytes();
               
               System.out.println("... About to send out message: " + outgoingMsg + "\n");
               
               //For every client connected
               for( int i=0; i < clientIPs.size(); i++ ){
                  //get their IP and Port
                  InetAddress clientIp = clientIPs.get(i);
                  int clientPort = clientPorts.get(i);
                  
                  //construct Packet
                  udpPacket = 
                     new DatagramPacket( 
                        outgoingByte,
                        outgoingByte.length, 
                        clientIp, 
                        clientPort 
                     );
                  //send message out
                  udpSocket.send( udpPacket );
                  
                  System.out.println("Message sent to client #" + i );
               }//end of for
               
               System.out.println("\nSuccessfully sent message to all Clients!\n\n");
              
               
            }//end of try
            catch(IOException ioe){
               System.out.println( ioe.getMessage() );
            }
         
         }//end of while
      
      }//end of run 
   }//end of UDP thread
   
   /**
    * Returns the current time as a String
    *@return String time - the current time as a String
    */
   public String getCurrentTime(){
      String time = 
         new SimpleDateFormat("hh:mm:ss").format(Calendar.getInstance().getTime());
      return time;
  }
    
   /**
    * Returns an IP as a String
    *@param s - Socket
    *@return ip -  String of the requested IP
    */
   public String getIP(Socket s){
      String ip = s.getInetAddress().toString();
      ip = ip.substring(1,ip.length());
   
      return ip;
   }                                            
}//end of chat server class
