package testing;

public class Message {
	static public interface IReceiver {
		boolean receive(Message message);
	}
}
