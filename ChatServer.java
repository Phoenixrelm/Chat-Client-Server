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
       
   public Vector<ClientThread> ctVector = new Vector<ClientThread>();
   
   public Vector<ObjectOutputStream> clients = new Vector<ObjectOutputStream>();
   public Vector<Socket> sockets = new Vector<Socket>();
   
   //DatagramSocket datagramSocket = null;
   
   public HashSet<InetAddress> udpClients = new HashSet<InetAddress>();
   
   
   String timeStamp = new SimpleDateFormat("hh:mm:ss").format(Calendar.getInstance().getTime());

   
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
         //ds = new DatagramSocket(PORT);
         
         // waits for client to connect, starts thread, adds to client Vector

         while(true){
            
            
            UDPThread udpThread = new UDPThread();
            udpThread.start();
         
            cs = ss.accept();         
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
               
               //sends the message to all the clients
               
                  for(int i=0; i < clients.size(); i++){
                     ObjectOutputStream temp = clients.get(i);
                     try{
                        System.out.println("Size: " +clients.size() +" Trying to send to client# "  + i);
                        temp.writeObject("("+timeStamp+ ") " + getIP(cs) + ": " + message);
                        temp.flush();
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
   
     // DatagramPacket dp;
   /*
      public UDPThread( DatagramPacket dgp ){
        this.dp = dgp;
      }   
   */
      public void run() {
         System.out.println("UDP Thread Started");
         
         //try{          
            //DatagramSocket datagramSocket = new DatagramSocket(PORT);             
                         
            byte[] sendData = null;            
            while(true) {
            try{
             byte[] receiveData = null; 
               receiveData =  new byte[1024];             
                               
               DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);                   
               
               //Receive message   
               datagramSocket.receive(receivePacket);
               
               
               
               InetAddress IPAddress = receivePacket.getAddress(); 
               System.out.println(IPAddress);
               if(udpClients.size() == 0){
                     udpClients.add( IPAddress );
               }
               else{
                  for(InetAddress ina: udpClients){
                     if(IPAddress != ina){
                        udpClients.add( IPAddress );
                     }
                  }
               }
                              
              
            
               sendData    =  new byte[1024]; 
                 
               //IP address in use

               
               //Format message
               String sentence = new String(receivePacket.getData());   
               sentence = "("+timeStamp+") " + receivePacket.getAddress()+": " +sentence;                
               System.out.println("RECEIVED: " + sentence);   
               
               //                              
               //int port = receivePacket.getPort(); 
               //InetAddress clientIP = receivePacket.getAddress();                   
               
               //turn data into bytes
               sendData = sentence.getBytes();
                                  
               DatagramPacket sendPacket;                  
                System.out.println("Outside For, Client Size: " + udpClients.size() );                               
               //send message 
               int i = 0;   
                for(InetAddress ina : udpClients){
                     //ObjectOutputStream temp = clients.get(i);
                     
                     try{
                        System.out.println("Size: " + ina +" Trying to send to client# "+ i );
                        
                        sendPacket = new DatagramPacket(sendData, sendData.length, ina, PORT);
                        
                        System.out.println("Packet Constructed");
                        datagramSocket.send( sendPacket );
                        
                        System.out.println("PAcket Sent");
                        i++;
                        System.out.println("Sent Packet: " + sendPacket.getLength());
                     }
                     catch(IOException ioe){
                        System.out.println("Client Disconnected #"  + i);
                        udpClients.remove(i);
                    
                     }         
                 }//end of for loop 
                           
               //datagramSocket.send(sendPacket);                        
          //end of while  
         }//end of try
         
         catch(IOException ioe){ 
            System.out.println("IEO");       
         } 
        }                
      }//end of run 
   }//end of UDP thread
    
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
}//end of chat server class
