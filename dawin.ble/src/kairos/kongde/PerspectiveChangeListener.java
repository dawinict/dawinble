 
package kairos.kongde;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.UIEvents.EventTags;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.osgi.service.event.Event;

public class PerspectiveChangeListener {
	@Execute
	public void execute() {
		
	}

//	@Inject
//	private EModelService modelService;
	
	@Inject
	private EPartService partService;
	
//	@Inject
//	@Optional
//	private void activate(@UIEventTopic(UIEvents.UILifeCycle.PERSPECTIVE_OPENED) Event event) {
//		Object element = event.getProperty(UIEvents.EventTags.ELEMENT);
//		System.out.println("SSSSS");
//		if (element != null && element instanceof MPerspective) {
//			MPerspective perspective = (MPerspective) element;
////			if (perspective.getElementId().equals("kairos.kongde.perspective.topic") && perspective.getTags().contains("active")) {
////				//expensive call here
////			}
//			if (perspective.getElementId().equals("kairos.kongde.perspective.topic") ) {
//		          MPart part = partService.createPart("kairos.kongde.partdescriptor.topicpart");
//		          //part.setLabel("New Dynamic Part");
//		          partService.showPart(part, PartState.ACTIVATE);
//			}
//			if(perspective.getElementId().equals("kairos.kongde.perspective.transform")) {
//		          //partService.showPart("kairos.kongde.partdescriptor.topicpart", PartState.ACTIVATE);
//		          
//		          MPart part = partService.createPart("kairos.kongde.partdescriptor.topicpart");
//		          //part.setLabel("New Dynamic Part");
//		          partService.showPart(part, PartState.CREATE);
//				
//			}
//		}
//	}
	
	@Inject
	@Optional
	public void subscribeTopicSelectedElement(@UIEventTopic(UIEvents.ElementContainer.TOPIC_SELECTEDELEMENT) Event event) {
		//Object element = event.getProperty(UIEvents.EventTags.ELEMENT);
		@SuppressWarnings("unused")
		Object newValue = event.getProperty(EventTags.NEW_VALUE);
		//System.out.println("XXXXX");
		
//		if (newValue instanceof MTrimmedWindow) {
//	          MPart part = partService.createPart("kairos.kongde.partdescriptor.topicpart");
//	          //part.setLabel("New Dynamic Part");
//	          partService.showPart(part, PartState.CREATE);
//
////	          MPart partTransform = partService.createPart("kairos.kongde.partdescriptor.transformpart");
////	          partService.showPart(partTransform, PartState.CREATE);
//		}
//
//	    // only run this, if the NEW_VALUE is a MPerspective
//		if (!(newValue instanceof MPerspective)) {
//		  return;
//		}
//
//		MPerspective perspective = (MPerspective) newValue;
//		if(perspective.getElementId().equals("kairos.kongde.perspective.topic")) {
//	          //partService.showPart("kairos.kongde.partdescriptor.topicpart", PartState.ACTIVATE);
//	          
//	          MPart part = partService.createPart("kairos.kongde.partdescriptor.topicpart");
//	          //part.setLabel("New Dynamic Part");
//	          partService.showPart(part, PartState.CREATE);
//			
//		}
//
//		if(perspective.getElementId().equals("kairos.kongde.perspective.transform")) {
//	          //partService.showPart("kairos.kongde.partdescriptor.topicpart", PartState.ACTIVATE);
//	          
//	          MPart part = partService.createPart("kairos.kongde.partdescriptor.topicpart");
//	          //part.setLabel("New Dynamic Part");
//	          partService.showPart(part, PartState.CREATE);
//	          
//	          MPart part2 = partService.createPart("kairos.kongde.partdescriptor.topicpart");
//	          partService.showPart(part2, PartState.CREATE);
//
//			//System.out.println(perspective.getElementId());
//			
//			// Get the MWindow, where we want to change the label
//			//MWindow topLevelWindowOfPerspective = modelService.getTopLevelWindowFor(perspective);
//
//			//topLevelWindowOfPerspective.setLabel(perspective.getLabel());
//	          MPart partTransform = partService.createPart("kairos.kongde.partdescriptor.transformpart");
//	          //part.setLabel("New Dynamic Part");
//	          partService.showPart(partTransform, PartState.CREATE);
//			
//		}
	}
}