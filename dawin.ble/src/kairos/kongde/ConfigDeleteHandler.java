 
package kairos.kongde;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.viewers.IStructuredSelection;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;

public class ConfigDeleteHandler {
	@Execute
	public void execute(MPart part) {
		SensorPart configPart = (SensorPart)part.getObject();
		configPart.deleteEntity();
	}
	
	
	@CanExecute
	public boolean canExecute(@Named(IServiceConstants.ACTIVE_SELECTION) @Optional Object selection) {
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		Object object = structuredSelection.getFirstElement();
		if(object instanceof String) {
			return false;
		}else {
			return true;
		}
	}
		
}