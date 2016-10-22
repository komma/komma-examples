package net.enilink.komma.example.smarthome.edit;

import java.util.Collection;

import net.enilink.komma.common.adapter.IAdapterFactory;
import net.enilink.komma.common.util.IResourceLocator;
import net.enilink.komma.core.IEntity;
import net.enilink.komma.core.IReference;
import net.enilink.komma.edit.provider.IViewerNotification;
import net.enilink.komma.edit.provider.ReflectiveItemProvider;
import net.enilink.komma.edit.provider.ViewerNotification;
import net.enilink.komma.em.concepts.IResource;
import net.enilink.komma.example.smarthome.model.SMARTHOME;
import net.enilink.komma.example.smarthome.model.Switchable;
import net.enilink.komma.model.event.IStatementNotification;
import net.enilink.vocab.rdfs.Class;

public class SmartHomeItemProvider extends ReflectiveItemProvider {
	public SmartHomeItemProvider(IAdapterFactory adapterFactory, IResourceLocator resourceLocator,
			Collection<? extends IReference> targetTypes) {
		super(adapterFactory, resourceLocator, targetTypes);
	}

	@Override
	protected void addViewerNotifications(Collection<IViewerNotification> viewerNotifications,
			IStatementNotification notification) {
		IEntity object = resolveReference(notification.getSubject());
		if (object instanceof IResource) {
			((IResource) object).refresh(notification.getPredicate());
			viewerNotifications.add(new ViewerNotification(object, true, true));
		}
	}

	@Override
	protected boolean childRequiresName(IResource subject, IReference property, Class rangeClass) {
		return true;
	}

	@Override
	public Object getImage(Object object) {
		// use special "off" icons for switchables
		if (object instanceof Switchable && !((Switchable) object).on()) {
			String prefix = "full/obj16/";
			Collection<?> types = getTypes(object);
			if (types.contains(SMARTHOME.TYPE_SWITCH)) {
				return getImage(prefix + "Switch-off");
			} else if (types.contains(SMARTHOME.TYPE_LAMP)) {
				return getImage(prefix + "Lamp-off");
			}
		}
		return super.getImage(object);
	}
}