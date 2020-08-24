import java.io.File;
import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class TftpClient
{

    public static void main(String[] args)
    {
        InetAddress address;
        final int port = 8888;
        DatagramSocket clientDS = null;
        DatagramPacket packet;
        byte[] receiveBuffer = new byte[512];
        try
        {
            clientDS = new DatagramSocket();
            checkArgs(args);
            // Arrange server address to connect to
            address = InetAddress.getByName(args[0]);
            // file to be requested
            // Break into bytes
            byte[] stringByteArray = args[1].getBytes();
            // Create byte array of size stringarray + 1
            byte[] sendBuffer = new byte[stringByteArray.length + 1];
            // Add RRQ to beginning of array
            sendBuffer[0] = TftpUtil.RRQ;
            // then copy stringarray over
            for(int i = 1; i < sendBuffer.length; i++)
            {
                sendBuffer[i] = stringByteArray[i - 1];
            }
            // Create packet to be sent
            packet = new DatagramPacket(sendBuffer, sendBuffer.length, address, port);
            clientDS.send(packet);

            packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            clientDS.receive(packet);
            String received = new String(packet.getData(), 0, packet.getLength());
            System.out.println(received);




        }
        catch(Exception e)
        {
            System.err.println(e);
        }
    }
    public static void checkArgs(String[] args)
    {
        if (args.length != 2)
        {
            System.out.println("Incorrect format - USAGE: TftpClient <ip> <filename>");
            System.exit(-1);
        }
        if (!args[1].matches("\\S+\\.\\S+"))
        {
            System.out.println("Please use a valid file name! USAGE: filename.extension");
            System.exit(-1);
        }

    }
}
