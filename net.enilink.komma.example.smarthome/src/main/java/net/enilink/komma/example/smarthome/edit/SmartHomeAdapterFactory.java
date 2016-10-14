package net.enilink.komma.example.smarthome.edit;

import java.util.Collection;

import net.enilink.komma.edit.provider.ReflectiveItemProviderAdapterFactory;
import net.enilink.komma.em.concepts.IClass;
import net.enilink.komma.em.concepts.IProperty;
import net.enilink.komma.example.smarthome.SmartHomePlugin;
import net.enilink.komma.example.smarthome.model.SMARTHOME;
import net.enilink.komma.example.smarthome.model.Switch;

public class SmartHomeAdapterFactory extends ReflectiveItemProviderAdapterFactory {
	public SmartHomeAdapterFactory() {
		super(SmartHomePlugin.INSTANCE, SMARTHOME.NAMESPACE_URI);
	}

	@Override
	protected Object createItemProvider(Object object, Collection<IClass> types, Object providerType) {
		if (object instanceof Switch) {
			return new SmartHomeItemProvider(this, resourceLocator, types) {
				@Override
				public String getText(Object object) {
					return ((Switch) object).label();
				}
			};
		}
		if (!(object instanceof IClass || object instanceof IProperty)) {
			return new SmartHomeItemProvider(this, resourceLocator, types);
		}
		return null;
	}
}