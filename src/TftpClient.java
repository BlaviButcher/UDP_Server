import java.io.File;
import java.io.FileInputStream;
import java.net.DatagramSocket;

public class TftpClient
{

    public static void main(String[] args)
    {
        try
        {
            DatagramSocket clientDS = new DatagramSocket();

            File file;
            //FileInputStream fis = new FileInputStream("cat.jpg");
            byte[] buf = new byte[512];
        }
        catch(Exception e)
        {
            System.err.println(e);
        }
    }
}
