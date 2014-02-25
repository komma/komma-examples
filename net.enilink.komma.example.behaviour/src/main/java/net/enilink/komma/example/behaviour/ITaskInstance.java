package net.enilink.komma.example.behaviour;

public interface ITaskInstance extends IActivityInstance {
	public final static String STATE_AWAITING_COMPLETION = "awaiting_completion";

	public boolean complete(String transitionName);
}
