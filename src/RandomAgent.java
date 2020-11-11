import jade.core.Agent;

public class RandomAgent extends Agent {

	protected void setup() {
		System.out.println("RandomAgent " + getAID().getLocalName() + " is ready.");
	}
}
