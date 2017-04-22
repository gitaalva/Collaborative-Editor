
import java.util.Queue;
import java.io.*;
import java.net.*;
import java.util.*;

public class User extends Thread {
  static int myMsg;
  static int otherMsg;
  ArrayList<Message> outgoing;
  static StringBuilder output;
  int highestIndex = 0;
  int lowestIndex = 0;
  int id;
  int myPort;
  int otherPort;
  String otherIp = null;
  
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
  
  public User(int myid, int myPort, int OtherPort, String otherip) {
	this.id = myid;
	this.myPort = myPort;
	this.otherPort = OtherPort;
    this.myMsg = 0;
    this.otherMsg = 0;
    this.otherIp = otherip;
    outgoing = new ArrayList<Message>();
  }
  
  public void setGui(GUIManager gui) {
	  this.gui = gui;
  }
  
  public synchronized void send(Message msg) {
	  outgoing.add(msg);
	  try {
		  System.out.println("User" + id+ "Sending message" + msg);
		  oos.writeObject(msg);
	  }catch (Exception e) {
		  System.out.println("User::Send() Exception when writing msg to socket"); 
	  }
  }
  
  public synchronized void receiveMessage( Message msg) {
	   // send to GUI
	  // make it non editable
	  // transform and apply
	  System.out.println(id + " Received msg " + msg);
	  for (Message m:outgoing) {
		  if (m.myMsg < msg.OtherMsg) {
			  outgoing.remove(m);
		  }
	  }
	  
	  for (int i=0; i < outgoing.size(); ++i) {
		  if (msg.operation == Message.Op.NOOP || outgoing.get(i).operation == Message.Op.NOOP)
			  continue;
		  Message[] transformed = Message.xform(msg,outgoing.get(i));
		  msg = transformed[0];
		  outgoing.set(i, transformed[1]);
	  }
	  
	  System.out.println("After transformation the message becomes" + msg);
	  
	  if (msg.operation == Message.Op.INSERT) {
		  int index = msg.index;
		  char val = msg.val;
		  gui.insertChar(index,val);
	  } else if (msg.operation == Message.Op.DELETE) {
		  int index = msg.index;
		  gui.deleteChar(index);
	  }
	  this.otherMsg += 1;
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
		        userSocket = new Socket(otherIp,otherPort);
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
