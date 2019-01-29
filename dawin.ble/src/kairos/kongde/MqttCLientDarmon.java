 
package kairos.kongde;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.EventTopic;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.osgi.service.event.Event;

import org.eclipse.e4.core.services.events.IEventBroker;

public class MqttCLientDarmon {

	@Inject
	@Optional
	public void applicationStarted(
			@EventTopic(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE) Event event) {
		// TODO Modify the UIEvents.UILifeCycle.APP_STARTUP_COMPLETE EventTopic to a certain event you want to listen to.
		
		System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
	}

}
