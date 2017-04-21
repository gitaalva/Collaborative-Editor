import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
public class Server extends Thread {
	public static void main(String args[]) {
		System.out.println("Server thread start");
		int port = 5000;
		//int port = 2002;
		try {
			ServerSocket ss = new ServerSocket(port);
			Socket s = ss.accept();
			InputStream is = s.getInputStream();
			ObjectInputStream ois = new ObjectInputStream(is);
			OutputStream os = s.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(os);
			int count = 0;
			while (count <= 100) {
				Message to = (Message)ois.readObject();
				if (to!=null){
					System.out.println(to);
				}
				count++;
			}
			is.close();
			s.close();
			ss.close();
		}catch(Exception e){
			System.out.println(e);
		}
		System.out.println("Server thread end");
	}
}