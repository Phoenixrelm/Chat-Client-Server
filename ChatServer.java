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
   DatagramSocket ds = null;
   Object message;
   
   public Vector<ClientThread> ctVector = new Vector<ClientThread>();
   public Vector<ObjectOutputStream> clients = new Vector<ObjectOutputStream>();
   public Vector<Socket> sockets = new Vector<Socket>();
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
     
     //Exicite code 
     
     //While true accpet incoming 
     
      try{
         //tcp
         ss = new ServerSocket(PORT);       
         Socket cs = null;
         String ip;
         //udp
         ds = new DatagramSocket(PORT); 
   
         
         // waits for client to connect, starts thread, adds to client Vector
         while(true){
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
   
   }

   public static void main(String[] args){
      ChatServer cs = new ChatServer();
   }

   
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
                  }  
               }
  ///////////////////////////////////////////////////////////////////////////// 
                          byte[] receiveData = new byte[1024];
                          byte[] sendData = new byte[1024];             
                          DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);                   
                         
                          ds.receive(receivePacket);  
                                           
                          String sentence = new String( receivePacket.getData());                   
                          System.out.println("RECEIVED: " + sentence);    
                                        
                          InetAddress IPAddress = receivePacket.getAddress();                  
                          int port = receivePacket.getPort();                  
                          String capitalizedSentence = sentence.toUpperCase();                  
                          sendData = capitalizedSentence.getBytes();                   
                          DatagramPacket sendPacket =                   
                                         new DatagramPacket(sendData, sendData.length, IPAddress, port);                  
                          ds.send(sendPacket);     
               
               
            }
         }
         catch(ClassNotFoundException CNFE){
         }
         catch(IOException ioe){
                  
        }                                       

         
      }
   } 
   
   public String getIP(Socket s){
      String ip = s.getInetAddress().toString();
      ip = ip.substring(1,ip.length());
   
      return ip;
   }                                            
}
