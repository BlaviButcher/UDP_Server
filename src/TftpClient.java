import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.Scanner;

public class TftpClient
{
    private InetAddress address;
    private final int port = 8888;
    private DatagramSocket clientDS = null;
    private DatagramPacket packet;
    private byte[] receiveBuffer = new byte[512];
    private ByteArrayOutputStream outputArray = new ByteArrayOutputStream();
    private OutputStream outputStream;

    public static void main(String[] args)
    {
        checkArgs(args);
        TftpClient tftpClient = new TftpClient();
        tftpClient.startClient(args);
    }

    public void startClient(String[] args)
    {
        byte block = 0;
        try
        {
            clientDS = new DatagramSocket();

            // Arrange server address to connect to
            address = InetAddress.getByName(args[0]);
            // send RRQ
            sendRequest(args[1]);
            getOutputFile(args[2]);

            while (true)
            {
                packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                clientDS.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                if (isDATA(received))
                {
                    if (isExpectedBlock(received, block))
                    {
                        displayPacketContents(received);
                        outputArray.write(received.getBytes(), 2, packet.getLength() - 2);
                        block++;
                        acknowledgePacket(block);
                        if (isLastPacket(received))
                        {
                            outputArray.writeTo(outputStream);
                            break;
                        }
                    }
                    // retransmit ACK
                    else
                    {
                        acknowledgePacket(block--);
                    }
                }
                else if (isERROR(received))
                {
                    displayPacketContents(received);
                }
            }
            clientDS.close();

        }
        catch(Exception e)
        {
            System.err.println(e);
        }
    }

    private void getOutputFile(String path)
    {
        try
        {
            outputStream = new FileOutputStream(path);
        }
        catch (FileNotFoundException e)
        {
            System.err.println("Output file not found or cannot be created. Exiting....");
            System.exit(0);
        }
    }
    private void displayPacketContents(String contents)
    {
        // get bytes of string
        byte[] contentArray = contents.getBytes();
        // make new array size of contentArray - 2, so it cant fit file contents but not flag and block
        byte[] fileArray = new byte[contentArray.length - 2];
        for(int i = 0; i < fileArray.length; i++)
        {
            fileArray[i] = contentArray[i + 2];
        }
        System.out.print(new String(fileArray));
    }
    private void acknowledgePacket(byte block) throws java.io.IOException
    {
        byte[] byteReply = new byte[2];
        byteReply[0] = TftpUtil.ACK;
        byteReply[1] = block;
        SocketAddress sa = packet.getSocketAddress();
        // Create packet to be sent
        packet = new DatagramPacket(byteReply, byteReply.length, sa);
        clientDS.send(packet);
    }
    private boolean isExpectedBlock(String received, byte block)
    {
        byte[] receivedArray = received.getBytes();
        return (receivedArray[1] == block + 1);
    }
    // check that DATA flag was returned
    private boolean isDATA(String received)
    {
        byte[] receivedArray = received.getBytes();
        return (receivedArray[0] == TftpUtil.DATA);
    }
    // check if ERROR flag was returned
    private boolean isERROR(String received)
    {
        byte[] receivedArray = received.getBytes();
        return (receivedArray[0] == TftpUtil.ERROR);
    }

    private boolean isLastPacket(String s)
    {
        // if length is less than max stored in packet
        byte[] array = s.getBytes();
        return (array.length < 512);
    }
    // send RRQ
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

    // check args are appropriate format
    public static void checkArgs(String[] args)
    {
        // check correct # of args
        if (args.length != 3)
        {
            System.out.println("Incorrect format - USAGE: TftpClient <ip> <filename>");
            System.exit(0);
        }
        // check filename format
        if (!args[1].matches("\\S+[\\.\\S+]?"))
        {
            System.out.println("Please use a valid file name! USAGE: filename.extension");
            System.exit(0);
        }

    }
}
