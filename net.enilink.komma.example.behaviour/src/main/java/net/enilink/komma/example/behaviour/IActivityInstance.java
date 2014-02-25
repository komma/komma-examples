package net.enilink.komma.example.behaviour;

public interface IActivityInstance {
	public final static String STATE_OPEN = "open";
	public final static String STATE_COMPLETED = "completed";

	public boolean enter();

	public boolean execute();

	public boolean leave(String transitionName);
}
