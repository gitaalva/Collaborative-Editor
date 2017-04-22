
public class Main {
	public static void main(String[] args) {
		 int userNumber = Integer.parseInt(args[0]);
		 int myPort = Integer.parseInt(args[1]);
		 int otherPort = Integer.parseInt(args[2]);
		 String otherIp = args[3];
		 User user1 = new User(userNumber, myPort, otherPort,otherIp);
		 GUIManager gui = new GUIManager(user1,0);
		 user1.setGui(gui);
		 user1.start();
		 gui.showGUI();
		
	}

}
