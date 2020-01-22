import javax.swing.*;
import java.awt.event.*;
import java.awt.FlowLayout;
import java.io.*;
import java.net.Socket;


/*
  The client part of the chat application. The GUI is built using swing and awt.
  Because I use the FlowLayout the components are arranged from left to right inside
  the container in the order they are added. When one row is filled the a new row is 
  started.
*/

public class Client {
  public static void main(String[] args) throws Exception {
    Client client = new Client();
    client.startChat();

  }

  Socket socket;
  
  //creates frame with the title ChatApp
  static JFrame chatApp = new JFrame("ChatApp");
  
  //where the client messages are displayed
  static JTextArea textArea = new JTextArea(25, 35);

  //where the user can enter text
  static JTextField textField = new JTextField(35);
  static JButton sendButton = new JButton("send");

  //handles out I/O operations
  static BufferedReader in;
  static PrintWriter out;

  //class constructor
  Client() {
    chatApp.setLayout(new FlowLayout());

    //When text runs off of the textArea a scroller will appear
    chatApp.add(new JScrollPane(textArea));

    chatApp.add(textField);
    chatApp.add(sendButton);

    //Send messages cannot be deleted. The area is immutable
    textArea.setEditable(false);

    //trigges the event listener when the send button is pressed
    //or if the focus is on the textField and enter is clicked
    sendButton.addActionListener(new Listener());
    textField.addActionListener(new Listener());

    //ensures when the application is closed the program terminates
    chatApp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    //closes the socket once the application window is closed
    chatApp.addWindowListener(new WindowListener() {
      @Override
      public void windowClosing(WindowEvent event) {
        try{
          socket.close();
        } catch(Exception error) {
          System.out.println(error);
        }
      }
      @Override public void windowOpened(WindowEvent e) {}
      @Override public void windowIconified(WindowEvent e) {}            
      @Override public void windowDeiconified(WindowEvent e) {}            
      @Override public void windowDeactivated(WindowEvent e) {}            
      @Override public void windowActivated(WindowEvent e) {}
      @Override public void windowClosed(WindowEvent e) {}
    });

    chatApp.setSize(300, 300);
    
    //shows the chat window upon running the main function
    chatApp.setVisible(true);
  }

  //client socket code
  void startChat() throws Exception {
    socket = new Socket("0.0.0.0", 8088);

    // reads input from the server
    InputStream socketInputStream = socket.getInputStream();
    InputStreamReader isr = new InputStreamReader(socketInputStream);
    in = new BufferedReader(isr);

    // writes out to the server sockets
    OutputStream socketOutputStream = socket.getOutputStream();
    out = new PrintWriter(socketOutputStream, true);
    
    //socket is continually listening for messages from the server
    //anytime a message is recieved it is added to the textArea
    while (true) {
      String message = in.readLine();
      
      if(message == null) {
        break; 
      }
      
      textArea.append(message + "\n");
    }

  }
  //event listener which sends the entered text to the server
  class Listener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent event) {
      String message = Client.textField.getText();
      Client.out.println(message);
      Client.textField.setText("");
    }
  }

}