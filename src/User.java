import java.util.LinkedList;
import java.util.Queue;
import java.io.*;
import java.net.*;
import java.util.*;

public class User extends Thread {
  static int myMsg;
  static int otherMsg;
  Queue<Message> outgoing;
  static StringBuilder output;
  int highestIndex = 0;
  int lowestIndex = 0;
  int id;
  int myPort;
  int otherPort;
  
  // The input stream
  private static BufferedReader input_stream = null;
  
  private static BufferedReader inputLine = null;
  
  //------------------- Socket input data ------------------
  // The user socket
  private static Socket userSocket = null;
  // The output stream
  private static OutputStream os = null;
  // The input stream
  
  private static ServerSocket serverSocket = null;
  
  private static ObjectOutputStream oos = null;
  
  private static InputStream is = null;
  
  private static ObjectInputStream ois = null;
  private static boolean closed = false;
  
  private static String name;
  
  public GUIManager gui;
  //-------------------- Socket output data ------------------
  private class ReadSocket extends Thread {
	  //private InputStream is = null;
	  //private ObjectInputStream ois = null;
	  
	  // for now we will just directly establish connection
	  // need to figure out a way to disconnect and then work
	  /*
	  public ReadSocket(InputStream is, ObjectInputStream ois) {
		  //this.is = is;
		  //this.ois = ois;
	  }
	  
	  public void establishConnection() {
		  while (true) {
			  
		  }
	  }
	  */
	  
	  public void run() {
		  System.out.println("ReadSocket:: run() begin");
		   Message ServerMessage;
		  try {
			  System.out.println("before accepting connection");
			  ServerSocket ss = new ServerSocket(myPort);
			  Socket socket = ss.accept();
			  System.out.println("Accepted Connection");
			  is = socket.getInputStream();
			  ois = new ObjectInputStream(is);
			  while(true) {
				  ServerMessage = (Message)ois.readObject();
				  if (ServerMessage != null ) {
					  System.out.println("User id" + id + "Message Received from other client" + ServerMessage);
					  //System.out.println(Server);
					  receiveMessage(ServerMessage);
				  }
			  }
		  } catch (Exception e) {
	 		 System.out.println("Exception occured while reading from input stream on thread" + e.getMessage() );
	 	 }
		 System.out.println("ReadSocket:: run() end");
	 }
  }
  
  public User(int myid, int myPort, int OtherPort) {
	this.id = myid;
	this.myPort = myPort;
	this.otherPort = OtherPort;
    this.myMsg = 0;
    this.otherMsg = 0;
    outgoing = new LinkedList<Message>();
  }
  
  public void setGui(GUIManager gui) {
	  this.gui = gui;
  }
  
  public void send(Message msg) {
	  outgoing.add(msg);
	  //.msg.myMsg += 1;
	  try {
		  oos.writeObject(msg);
	  }catch (Exception e) {
		  System.out.println("User::Send() Exception when writing msg to socket"); 
	  }
  }
  
  public synchronized void receiveMessage( Message msg) {
	   // send to GUI
	  // make it non editable
	  // transform and apply
	  
	  if (msg.operation == Message.Op.INSERT) {
		  int index = msg.index;
		  char val = msg.val;
		  gui.insertChar(index,val);
	  } else if (msg.operation == Message.Op.DELETE) {
		  int index = msg.index;
		  gui.deleteChar(index);
	  }
  }
  
  public void run() {
	    System.out.println("Here");
	  	Thread listen_t = null;
	    name = "Listen Thread";
	    
	    ReadSocket rs = new ReadSocket();
	    listen_t = new Thread(rs,name);
	    listen_t.start();
	    System.out.println("Here");
	    boolean connected = false;
	  while (true) {
		  try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (connected)
				continue;
		  try {
		    	System.out.println("In Main");
		        userSocket = new Socket("127.0.0.1",otherPort);
		        connected = true;
		        OutputStream os = userSocket.getOutputStream();
		        oos = new ObjectOutputStream(os);
		    } catch (Exception e) {
		        System.out.println("Exception occured" + e.getMessage() );
		        connected = false;
		        //System.exit(1);
		    }
	  }
	  
  }
}
