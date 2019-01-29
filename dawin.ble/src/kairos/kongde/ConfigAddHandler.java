 
package kairos.kongde;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.viewers.IStructuredSelection;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;

public class ConfigAddHandler {
	@Execute
	public void execute(MPart part) {
		SensorPart configPart = (SensorPart)part.getObject();
		configPart.addEntity();
	}
	
	
	@CanExecute
	public boolean canExecute(@Named(IServiceConstants.ACTIVE_SELECTION) @Optional Object selection,ESelectionService selectionService) {
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		Object object = structuredSelection.getFirstElement();
		
		IStructuredSelection structuredSelection2 = (IStructuredSelection) selectionService.getSelection();
		Object object2 = structuredSelection2.getFirstElement();
		
		if(object instanceof String) {
			return true;
		}else {
			return false;
		}
	}
		
}