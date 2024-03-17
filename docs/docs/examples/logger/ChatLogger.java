
import org.relayirc.chatengine.*;
import org.relayirc.util.Debug;

///////////////////////////////////////////////////////////////
/** 
 * ChatLogger will connect to Undernet, join the #chatzone channel,
 * print out 10 messages and then quit. 
 */
public class ChatLogger extends ServerAdapter {

   private Server _server;
   public static ChatLogger chatLogger; // Exposed for ChannelLogger

   /** 
    * Construct ChatLogger. Creates server, adds self as listener 
	 * connects to the chat server.
	 */
   public ChatLogger() {
      System.out.println("ChatLogger - Relay IRC Programming Example");	   
		_server = new Server("irc.linux.com",6667,"n/a","n/a");
      _server.addServerListener(this);
		_server.connect("logdaddy","logdaddy_","pbunyan","Paul Bunyan");
   }
	/** Once connected, join #chatzone. */ 
   public void onConnect(ServerEvent event) {
	   System.out.println("Connected to "+event.getServer());
	   _server.sendJoin("#debian");
   }
   /** Once channel is joined, create ChannelLogger to listen to log it. */
   public void onChannelJoin(ServerEvent event) {
		Channel chan = (Channel)event.getChannel();
	   System.out.println("Joined "+chan);
		chan.addChannelListener(new ChannelLogger());
   }
   /** Request disconnection from the chat server. */
   public void stop() {
	   System.out.println("Stopping...");
	   _server .disconnect();	
   }
   /** Once disconnected, print farewell message and exit. */
   public void onDisconnect(ServerEvent event) {
	   System.out.println("Disconnected: good-bye!");
	   System.exit(0);
   }
	/** Main method. */
   public static void main(String args[]) {
      //Debug.setDebug(true);
		chatLogger = new ChatLogger();
	}
}

///////////////////////////////////////////////////////////////
/** 
 * ChannelLogger will print out 10 messages and then ask 
 * that ChatLogger to disconnect from the chat server.
 */
class ChannelLogger extends ChannelAdapter {
   private int _msgCount = 0;

   public void onMessage(ChannelEvent event) {
      System.out.println(
         event.getOriginNick()+" says "+(String)event.getValue());

		if (_msgCount++ > 10) {
		   ChatLogger.chatLogger.stop();
		}
	}
}

