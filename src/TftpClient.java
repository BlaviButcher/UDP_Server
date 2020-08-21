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
            Scanner scanner = new Scanner(System.in);
            InetAddress serverAddress = InetAddress.getByName("192.168.1.66");

            byte[] stringByteArray = "cat.jpg".getBytes();
            byte[] dataSend = new byte[stringByteArray.length + 1];
            dataSend[0] = TftpUtil.RRQ;
            for(int i = 1; i < dataSend.length; i++)
            {
                dataSend[i] = stringByteArray[i - 1];
            }
            String s = new String(dataSend);
            System.out.println(s);


            DatagramPacket sentDP = new DatagramPacket(dataSend, dataSend.length, serverAddress, 8888);
            clientDS.send(sentDP);
            //FileInputStream fis = new FileInputStream("cat.jpg");
            byte[] buf = new byte[512];
        }
        catch(Exception e)
        {
            System.err.println(e);
        }
    }
}
