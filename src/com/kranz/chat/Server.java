import java.io.*;
import java.net.*;


public class Server {
  ServerSocket server;

  //allows only 2 clients to join
  static int count = 0;
  
  //holds all the client names
  static String[] clients = new String[2];

  //holds the text-outputstreams for each client
  static PrintWriter[] printWriters = new PrintWriter[2];
  public static void main(String[] args) throws Exception {
    new Server(8088).listen();
  }

  //server constructor 
  public Server(int port) throws Exception {
    this.server = new ServerSocket(port);
  }

  //continually listens for new connections
  //each new connection is given it's ownthread
  //used code off of stackoverflow and geeks for geeks as
  //a reference
  private void listen() throws Exception {
    System.out.println("Server is listening.");
    while(count < 2) {
      Socket client = server.accept();
      System.out.println("Connection Established");
      new ChatClient(client).start();
    }
    System.out.println("server if full");
  }

  //
  class ChatClient extends Thread {
    Socket socket;
    BufferedReader in;
    PrintWriter out;
    String clientName;

    public ChatClient(Socket socket) throws Exception {
      this.socket = socket;
    }

    //code is executed for each thread
    @Override
    public void run() {
      try {
        //reads input from the client sockets
        InputStream socketInputStream = socket.getInputStream();
        InputStreamReader isr = new InputStreamReader(socketInputStream);
        in = new BufferedReader(isr);

        //writes out to the client sockets
        OutputStream socketOutputStream = socket.getOutputStream();
        out = new PrintWriter(socketOutputStream, true);
        
        //first client to join is named clientA
        //second client to join is named clientB
        if(count > 0) {
          clientName = "Client B";
          out.println("Welcome " + clientName);
          Server.printWriters[1] = out;
          Server.clients[1] = clientName;
          count++;
        } else {
          clientName = "Client A";
          out.println("Welcome " + clientName);
          Server.printWriters[0] = out;
          Server.clients[0] = clientName;
          count++;
        }

        while(true) {
          //reads in the message from the client
          String message = in.readLine();
          
          //catches null when the a client disconnects
          if(message == null) {
            return;
          }
          
          //sends the message to each client in the arraylist 
          //including itself
          for(int i = 0; i < Server.printWriters.length; i++) {
            PrintWriter writer = Server.printWriters[i];
            writer.println(clientName + ": " + message);
          }

        }
      
      } catch(Exception error) {
        System.out.println(error);
      }
    }
  }
}