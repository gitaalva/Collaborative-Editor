import java.util.LinkedList;
import java.util.Queue;
import java.io.*;
import java.net.*;
import java.util.*;

public class Client extends Thread {
  static int myMsg;
  static int otherMsg;
  Queue<Message> outgoing;
  static StringBuilder output;
  int highestIndex = 0;
  int lowestIndex = 0;
  
  // The input stream
  private static BufferedReader input_stream = null;
  
  private static BufferedReader inputLine = null;
  
  //------------------- Socket input data ------------------
  // The user socket
  private static Socket userSocket = null;
  // The output stream
  private static OutputStream os = null;
  // The input stream
  
  private static ObjectOutputStream oos = null;
  
  private static InputStream is = null;
  
  private static ObjectInputStream ois = null;
  private static boolean closed = false;
  
  private static String name;
  
  //-------------------- Socket output data ------------------

  
  public Client() {
    this.myMsg = 0;
    this.otherMsg = 0;
    outgoing = new LinkedList<Message>();
    
    // create a new String builder class and fill it with space 
    // initially
    output = new StringBuilder(100);
    for (int i=0; i<100; i++) {
      output.append(' ');
    }
  }
  
  public static void main(String[] args) throws IOException {
	/*
    if (args.length != 2 )
    {
        System.out.println("Invalid number of arguments. Please provide IP address  and port number of the server");
        System.exit(1);
    }
    
    // The default port.
    int portNumber = Integer.parseInt(args[1]);
    // The default host.
    String host = args[0];
    /*
     * Open a socket on a given host and port. Open input and output streams.
     */
  
    
    //Message toSend = new Message(0,0,Message.Op.INSERT,1,'a');
	System.out.println("In Main");
    try {
    	System.out.println("In Main");
        userSocket = new Socket("10.0.0.245",5000);
        OutputStream os = userSocket.getOutputStream();
        oos = new ObjectOutputStream(os);
        is = userSocket.getInputStream();
        System.out.println("a");
		ois = new ObjectInputStream(is);
		System.out.println("b");
		inputLine = new BufferedReader(new InputStreamReader(System.in));
    } catch (Exception e) {
        System.out.println("Exception occured" + e.getMessage() );
        System.exit(1);
    }
    
    Thread listen_t = null;
    name = "Listen Thread";
    
    Client c1 = new Client();
    listen_t = new Thread(c1,name);
    listen_t.start();
    
    String terminalInput;
   // Client c1 = new Client();
    System.out.println("Reading terminal input");
    while (true)
    {
    	terminalInput = inputLine.readLine();
    	if (terminalInput != null)
    	{
    		System.out.println("Reading terminal input");
    		if (terminalInput.equals("exit")) {
    			break;
    		}
    		
    		if (terminalInput.equals("display")) {
    			System.out.println(output);
    		} else {
	    		if (terminalInput.length() == 3) {
	    			Message m1 = new Message(myMsg,otherMsg,Message.Op.INSERT,
	    									Character.getNumericValue(terminalInput.charAt(1)),
	    									terminalInput.charAt(2));
	    			System.out.println("Exectuing Message" + m1);
	    			c1.executeMessage(0,m1);
	    		} else if(terminalInput.length() == 2){
	    			Message m1 = new Message(myMsg, otherMsg,Message.Op.DELETE,
	    						Character.getNumericValue(terminalInput.charAt(1)),
	    						' ');
	    			
	    			c1.executeMessage(0,m1);
	    		}
    		}
    	}
    }
    
    try {
    	listen_t.join();
    }
    catch (Exception e) {
    	System.out.println("Error occurred while waiting for listening thread to die" + e.getMessage() );
    }
    
    //oos.writeObject(toSend);
    oos.close();
    os.close();
    userSocket.close();
  }
  
  public void insert(int index, char c) {
	  if (highestIndex < index) {
		  output.setCharAt(index, c);
		  highestIndex = c;
		  return;
	  }
	  char prevChar = c;
	  char currentChar;
	  
	  // there are some edge cases to handle here.
	  // what is highestIndex is the edge of the document
	  for (int i=index; i <= highestIndex+1; i++) {
		  currentChar = output.charAt(i);
		  output.setCharAt(i,prevChar);
		  prevChar = currentChar;
	  }
	  highestIndex++;
  }
  
  public void delete(int index) {
	  for (int i=index; i < highestIndex; i++) {
		  output.setCharAt(i, output.charAt(i+1));
	  }
	  highestIndex--;
  }
  
  public synchronized boolean applyMessage(Message msg) {
	  switch(msg.operation) {
	  case INSERT:
		  insert(msg.index,msg.val);
		  break;
	  case DELETE:
		  delete(msg.index);
		  break;
	  default:
		  System.out.println("applyMessage() :: INVALID COMMAND");
	  }
	  return true;
  }
  
  public void send(Message msg) {
	  try {
		  oos.writeObject(msg);
	  }catch (Exception e) {
		  System.out.println("Client::Send() Exception when writing msg to socket"); 
	  }
  }
  
  public synchronized void executeMessage(int owner, Message msg) {
	   if (owner == 0) {
		   applyMessage(msg);
		   outgoing.add(msg);
		   send(msg);
		   myMsg += 1;
	   } else {
		   // For now let's not do anything
		   for (Message m: outgoing) {
			   if (m.myMsg < msg.OtherMsg) {
				   outgoing.remove(m);
			   }
		   }
	   }
  }
  
  public void Run() {
	  Message ServerMessage;
	  try {
		  while(true) {
			  ServerMessage = (Message)ois.readObject();
			  if (ServerMessage != null ) {
				  executeMessage(1,ServerMessage);
			  }
		  }
	  } catch (Exception e) {
 		 System.out.println("Exception occured while reading from input stream on thread" + e.getMessage() );
 	 }
  }

}
