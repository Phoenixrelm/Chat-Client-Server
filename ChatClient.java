import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

/**
Dev by: Ryan Mason, Arther Burgin, Chris Dumlao, Bre Dionne
due Date: 4/4/14
Title: Chat Client
This client will be used to send messages to the server and receive them.
****IMPORTANT YOU NEED TO PUT THE IP OF THE COMPUTER THAT YOU ARE RUNNING THE SERVER ON****
HOLA 
*/


public class ChatClient{
   private final int PORT = 16789;
   private final String  HOST = "localhost";
   final JTextArea LOG;
   final JTextField INPUT;
   JPanel panel;
   Socket s;
   OutputStream os;
   ObjectOutputStream out;
   InputStream is;
   ObjectInputStream ois;
  
   public ChatClient(){
   
   
   
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
   
   
   
   
      try{
         s = new Socket(HOST, PORT);
         os = s.getOutputStream();
      
         sendButton.setEnabled(true);
      }
      catch(UnknownHostException ukh){
      
      }
      catch(IOException ioe){
      
      }
      
      
            
      sendButton.addActionListener(
            new ActionListener(){
            
               public void actionPerformed(ActionEvent ae){
               
                 // TEXT_INPUT: takes in the users input and sends it to the server\
                  String senderMsg = INPUT.getText();
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