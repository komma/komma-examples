package net.enilink.komma.example.smarthome.commands;

import net.enilink.komma.dm.change.IDataChangeSupport;
import net.enilink.komma.model.IModelSet;
import net.enilink.komma.model.IObject;
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
					IDataChangeSupport changeSupport = ((IObject) selected).getModel().getModelSet().getDataChangeSupport();
					IDataChangeSupport.Mode mode = changeSupport.getMode(null);
					try {
						// speed up things by disabling in-depth verification of changed triples
						changeSupport.setMode(null, IDataChangeSupport.Mode.VERIFY_NONE);

						((Switchable) selected).on(!((Switchable) selected).on());
					} finally {
						changeSupport.setMode(null, mode);
					}
				}
			}
		}
		return null;
	}
}