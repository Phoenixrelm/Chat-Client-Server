import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

/**
Dev by: Ryan Mason, Arther Burgin, Chris Dumlao, Bre Dionne
due Date: 4/4/14
Title: Chat Server
This Server will be used to receive messages from the client and then send it to all other clients.
*/

public class ChatServer{
   final int PORT = 16789;
   ServerSocket ss = null;
   Object message;
   //UDP
   public DatagramSocket udpSocket;
   public ArrayList<InetAddress> clientIPs;
   public ArrayList<Integer>   clientPorts;
   public HashSet<String>  connectedClients;
   
  //TCP
   public Vector<ClientThread> ctVector = new Vector<ClientThread>();
   public Vector<ObjectOutputStream> clients = new Vector<ObjectOutputStream>();
   public Vector<Socket> sockets = new Vector<Socket>();
   
   //
   public Vector<String> msgStack = new Vector<String>();
   //Booleans keeping track of  if each thread has sent out the message to all connected clients
   public boolean tcpBool = false;
   public boolean udpBool = false;
  
   String timeStamp;
   
   public ChatServer(){
         
     //server GUI
      JFrame window = new JFrame();
      JPanel middle = new JPanel(new GridLayout(1,1));
     
      JLabel jtField = new  JLabel(); 
   
   
      try  {
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
     
     //CREATE THEADS FOR TCP AND UDP 
     
     //Create two threads
     //Create serverSocket in one
     //Create DatagramSocket in other
     
     //Execute code 
     
     //While true accept incoming 
     
      try{
         //tcp
         ss = new ServerSocket(PORT);    
         //datagramSocket = new DatagramSocket(PORT);             
           
         Socket cs = null;
         String ip;
         //udp
          udpSocket = new DatagramSocket( PORT );
          clientIPs = new ArrayList();
          clientPorts = new ArrayList();
          connectedClients = new HashSet();
         
         // waits for client to connect, starts thread, adds to client Vector
      
         while(true){
           
            UDPThread udpThread = new UDPThread();
            udpThread.start();
                     System.out.println("Waiting for TCP Client .accept");      

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

  //Thread for Communication
   class ClientThread extends Thread {
      
      Socket cs;
      
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
            
            clients.add(out);
            Object obj;
                     
            //print it to the client socket
                        
            while(true){    
               // read the objects from ois to obj
               obj = ois.readObject(); 
            
            // Determine what kind of object we got
               if(obj instanceof String){
                  message = (String)obj;
                  
                  addToStack( message.toString() );
               
               //sends the message to all the clients
               
                  for(int i=0; i < clients.size(); i++){
                     ObjectOutputStream temp = clients.get(i);
                     try{
                        System.out.println("Size: " +clients.size() +" Trying to send to client# "  + i);
                        temp.writeObject("("+timeStamp+ ") " + getIP(cs) + ": " + getBottomOfStack() );
                        temp.flush();
                        if( i == (clients.size() -1 ) ){
                           tcpBool = false;
                           removeFromStack();
                           if( stackFilled() ){
                              tcpBool = true;
                              udpBool = true;
                           }
                        }
                     }
                     catch(IOException ioe){
                        System.out.println("Client Disconnected #"  + i);
                        clients.remove(i);
                        
                     }         
                  }//end of for loop 
               }         
            }
         }//end of try
         
         catch(ClassNotFoundException CNFE){
         }
         catch(IOException ioe){       
         } 
                                                 
      }//end of run   
   }//end of tcp thread
   
   //Thread for UDP communication
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
                     //System.out.println( "Added new client | " + currentClient  );
                  }
                  
               String outgoingMsg = newTimeStamp + currentClientIP.toString() + " : " + receivedMsg;
               
               addToStack( outgoingMsg );
               
               //Convert to bytes
               byte[] outgoingByte = ( getBottomOfStack() ).getBytes();
               
               //System.out.println("... About to send out message: " + getBottomOfStack() + "\n");
               
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
                  
                  if( i == (clientIPs.size() -1 ) ){
                     udpBool = false;
                     removeFromStack();
                       if( stackFilled() ){
                         tcpBool = true;
                         udpBool = true;
                       }
                  }
                  
                  //System.out.println("Message sent to client #" + i );
               }//end of for
               
               //System.out.println("\nSuccessfully sent message to all Clients!\n\n");
              
               
            }//end of try
            catch(IOException ioe){
               System.out.println( ioe.getMessage() );
            }
         
         }//end of while
      
      }//end of run 
   }//end of UDP thread
   
   /**
    *@return time - the current time as a String
    */
   public String getCurrentTime(){
      String time = 
         new SimpleDateFormat("hh:mm:ss").format(Calendar.getInstance().getTime());
      return time;
  }
    
   /**
    * Returns an IP as a String
    *@param Socket s 
    *@return ip - the requested IP
    */
   public String getIP(Socket s){
      String ip = s.getInetAddress().toString();
      ip = ip.substring(1,ip.length());
   
      return ip;
   }
   
   public boolean stackFilled(){
      if( msgStack.size() > 1 ){
         return true;
      }
      else{
         return false;
     }
   } 
   
   /**
    **
    **@param message, A String to added the message Stack
    **/   
    public void addToStack( String message ){
      tcpBool = true;
      udpBool = true;
      System.out.println("added to Stack: " + message);
      msgStack.add( message );
    } 
    
    /*
     *Removes firsts   (1, 2)
     */
    public void removeFromStack(){
     // if(tcpBool == false && udpBool == false){
         msgStack.remove( msgStack.get( 0 ) );
     // }
    }
    
    /**
    *@return String, the the msg at the bottom of the stack
    */
    public String getBottomOfStack(){
      return msgStack.get(0);
    }
    
                                            
}//end of chat server class
