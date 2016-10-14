package net.enilink.komma.example.smarthome.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import net.enilink.komma.example.smarthome.model.Switchable;

public class SwitchHandler extends AbstractHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection) {
			for (Object selected : ((IStructuredSelection) selection).toList()) {
				if (selected instanceof Switchable) {
					((Switchable) selected).on(!((Switchable) selected).on());
				}
			}
		}
		return null;
	}
}