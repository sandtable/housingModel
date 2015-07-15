package testing;

public class IntrospectMessage extends Message {
	private IntrospectMessage() {	
	}
	
	public static IntrospectMessage instance = new IntrospectMessage();
}
