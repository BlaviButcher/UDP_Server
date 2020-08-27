import java.io.File;
import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class TftpClient
{
    private InetAddress address;
    private final int port = 8888;
    private DatagramSocket clientDS = null;
    private DatagramPacket packet;
    private byte[] receiveBuffer = new byte[512];
    private int block = 0;

    public void startClient(String[] args)
    {
        try
        {
            clientDS = new DatagramSocket();

            // Arrange server address to connect to
            address = InetAddress.getByName(args[0]);
            sendRequest(args[1]);


            packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            clientDS.receive(packet);
            String received = new String(packet.getData(), 0, packet.getLength());
            if (isDATA(received))
            {
                if(isExpectedBlock(received))
                {
                    System.out.println(received.substring(2));
                    block++;
                    String reply = "1" + received.substring(2);
                    byte[] sendBuffer = reply.getBytes();
                    // Create packet to be sent
                    packet = new DatagramPacket(sendBuffer, sendBuffer.length, address, port);
                    clientDS.send(packet);

                    packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                    clientDS.receive(packet);
                    String received2 = new String(packet.getData(), 0, packet.getLength());
                    System.out.println(received2);

                }
            }



        }
        catch(Exception e)
        {
            System.err.println(e);
        }
    }
    public static void main(String[] args)
    {
        checkArgs(args);
        TftpClient tftpClient = new TftpClient();
        tftpClient.startClient(args);
    }
    private boolean isExpectedBlock(String received)
    {
        byte[] receivedArray = received.getBytes();
        return receivedArray[1] == block + 1 ? true : false;
    }
    private boolean isDATA(String received)
    {
        byte[] receivedArray = received.getBytes();
        return receivedArray[0] == TftpUtil.DATA ? true : false;
    }
    public void sendRequest(String filename) throws java.io.IOException
    {
        // file to be requested
        // Break into bytes
        byte[] stringByteArray = filename.getBytes();
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
    }

    public static void checkArgs(String[] args)
    {
        if (args.length != 2)
        {
            System.out.println("Incorrect format - USAGE: TftpClient <ip> <filename>");
            System.exit(-0);
        }
        if (!args[1].matches("\\S+\\.\\S+"))
        {
            System.out.println("Please use a valid file name! USAGE: filename.extension");
            System.exit(0);
        }

    }
}
