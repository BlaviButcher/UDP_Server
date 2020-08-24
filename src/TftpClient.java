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
        try
        {
            DatagramSocket clientDS = new DatagramSocket();
            checkArgs(args);
            // Arrange server address to connect to
            InetAddress serverAddress = InetAddress.getByName("192.168.1.66");
            // file to be requested
            // Break into bytes
            byte[] stringByteArray = "cat.jpg".getBytes();
            // Create byte array of size stringarray + 1
            byte[] dataSend = new byte[stringByteArray.length + 1];
            // Add RRQ to beginning of array
            dataSend[0] = TftpUtil.RRQ;
            // then copy stringarray over
            for(int i = 1; i < dataSend.length; i++)
            {
                dataSend[i] = stringByteArray[i - 1];
            }
            // Create packet to be sent
            DatagramPacket sentDP = new DatagramPacket(dataSend, dataSend.length, serverAddress, 8888);
            clientDS.send(sentDP);
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
