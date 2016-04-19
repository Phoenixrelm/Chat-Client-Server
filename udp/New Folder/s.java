import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.lang.Integer;
import java.net.*;
import java.util.*;


public class s
{
   //private static ServerSocket servSock;
   private static final int PORT = 1234;
private static DatagramSocket dgramSocket;
	private static DatagramPacket inPacket, outPacket;
	private static byte[] buffer;
	private static int bid = 0;

   public static void main(String[] args)
   {
      System.out.println("!!!Opening port...\n");
      try
      {
         dgramSocket = new DatagramSocket(PORT);
		
      }
      catch(IOException e)
      {
         System.out.println("Unable to attach to port!");
         System.exit(1);
      }
      do
      {
         run();
      }while (true);
   }

   private static void run()
   {
		Socket link = null;
		try
		{
			String messageIn,messageOut;

			do
			{
				buffer = new byte[1024];
				inPacket = new DatagramPacket(
						buffer, buffer.length);
				dgramSocket.receive(inPacket);

				InetAddress clientAddress =
						inPacket.getAddress();
				messageOut = "On sale";
				int clientPort =
						inPacket.getPort();

				messageIn = new String(inPacket.getData(),0,
						inPacket.getLength());
				System.out.println("Server> message received:" + messageIn);
				
				messageOut = "On sale";

				// check if it is number
				try{
					Integer client_bid = Integer.decode(messageIn);
					if(client_bid > bid )
					{
						bid = client_bid;
						messageOut = "New bid is: " + bid;
						System.out.println(client_bid);
					}

					else
					{
						messageOut = "Current bid is: " +bid;
						System.out.println(client_bid);
					}
	
				}
				catch(NumberFormatException nFe){
						messageOut="Not a number";
				}
				outPacket = new DatagramPacket(
							messageOut.getBytes(),
							messageOut.length() ,clientAddress,clientPort);
				dgramSocket.send(outPacket); //
			}while (true);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

		finally
		{
			try
			{
				System.out.println("\n* Closing connection... *");
				link.close();
			}
			catch(IOException e)
			{
				System.out.println("Unable to disconnect!");
				System.exit(1);
			}
		}
	}
}
