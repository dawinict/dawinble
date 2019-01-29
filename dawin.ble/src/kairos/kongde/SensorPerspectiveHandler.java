 
package kairos.kongde;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public class SensorPerspectiveHandler {
	@Execute
	public void execute(MApplication app, EPartService partService, EModelService modelService) {
        MPerspective perspective = (MPerspective) modelService.find("dawin.ble.perspective.dashboard", app);
        if (perspective != null) {
            partService.switchPerspective(perspective);
        }
		
//        MWindow window = (MWindow)modelService.find("kairos.kongde.trimmedwindow.kongdesensormonitoringetl", app);
//        window.setLabel("BLE 위치 모니터링"+"          - AP 대시보드");

	}
		
}