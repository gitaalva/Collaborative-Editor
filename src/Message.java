import java.io.Serializable;
import java.lang.Integer;
public class Message implements Serializable {
  public int myMsg;
  public int OtherMsg;
  public static enum Op{INSERT,DELETE, NOOP};
  public Op operation;
  public int index;
  public char val;
  
  public Message(int myMsg, int OtherMsg, Op operation, int index, char val) {
    this.myMsg = myMsg;
    this.OtherMsg = OtherMsg;
    this.operation = operation;
    this.index = index;
    this.val = val;
  }
  
  public Message(Message anotherMessage) {
	  	this.myMsg = anotherMessage.myMsg;
	    this.OtherMsg = anotherMessage.OtherMsg;
	    this.operation = anotherMessage.operation;
	    this.index = anotherMessage.index;
	    this.val = anotherMessage.val;
  }
  
  // does xform of message1 and message 2 matter
  public static Message[] insert_delete(Message m1, Message m2) {
	  Message r1 = new Message(m1);
	  Message r2 = new Message(m2);
	  
	  if (m1.index > m2.index) {
		  r1.index--;
	  } else if (m1.index < m2.index) {
		  r2.index++;
	  } else if (m1.index == m2.index) {
		  r2.index++;
	  }
	  Message[] output = new Message[2];
	  output[0] = r1;
	  output[1] = r2;
	  return output;
  }
  
  public static Message[] delete_insert(Message m1, Message m2) {
	  Message r1 = new Message(m1);
	  Message r2 = new Message(m2);
	  
	  if (m1.index > m2.index) {
		  r1.index++;
	  } else if (m1.index < m2.index) {
		  r2.index--;
	  } if (m1.index == m2.index) {
		  r1.index++;
	  }
	  
	  Message[] output = new Message[2];
	  output[0] = r1;
	  output[1] = r2;
	  return output;
  }
  
  public static Message[] insert_insert(Message m1, Message m2) {
	  Message r1 = new Message(m1);
	  Message r2 = new Message(m2);
	  
	  if (m1.index > m2.index) {
		  r1.index++;
	  } else if (m1.index < m2.index) {
		  r2.index++;
	  } else {
		  System.out.println("INSERT AT SAME LOCATION");
	  }
	  
	  Message[] output = new Message[2];
	  output[0] = r1;
	  output[1] = r2;
	  return output;
  }
  
  public static Message[] delete_delete(Message m1, Message m2) {
	  Message r1 = new Message(m1);
	  Message r2 = new Message(m2);
	  if (m1.index > m2.index) {
		  // equivalent of return del x-1, del y
		  r1.index--;
	  } else if (m1.index < m2.index) {
		  r2.index--;
	  } else {
		  r1.operation = Op.NOOP;
		  r2.operation = Op.NOOP;
	  }
	  Message[] output = new Message[2];
	  output[0] = r1;
	  output[1] = r2;
	  return output;
  }
  
  public static Message[] xform(Message m1, Message m2) {
	  if (m1.operation == Op.INSERT && m2.operation == Op.INSERT ) {
		  return insert_insert(m1,m2);
	  } else if (m1.operation == Op.INSERT && m2.operation == Op.INSERT ) {
		 return delete_delete(m1, m2);
	  } else if (m1.operation == Op.INSERT && m2.operation == Op.DELETE) {
		  return insert_delete(m1, m2);
	  } else  /*if (m1.operation == Op.DELETE && m2.operation == Op.INSERT) */{
		  System.out.println("Message::xform():: WARNING WARNING WARNING UNRELIABLE MESSAGE");
		  return delete_insert(m1,m2);
	  }
  }
  
  public String toString() {
    String ret = "";
    ret += "myMsg - " + Integer.toString(myMsg) + ",";
    ret += "OtherMsg - " + Integer.toString(OtherMsg) + ",";
    
    switch(operation) {
      case INSERT:
        ret += "Operation: INSERT,";
        break;
      case DELETE:
        ret += "Operation: DELETE.";
    }
    
    ret += "index:" + Integer.toString(index) + ",";
    ret += "val" + val;
    return ret;
  }
}
