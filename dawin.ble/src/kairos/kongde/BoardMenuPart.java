
package kairos.kongde;

import java.net.URL;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MArea;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainer;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.RowData;

public class BoardMenuPart {
	@Inject
	private EPartService partService;
	@Inject
	private EModelService modelService;
	@Inject
	private MApplication app;
	
	private ResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources());
	
	Bundle bundle = FrameworkUtil.getBundle(this.getClass());
    // use the org.eclipse.core.runtime.Path as import
    URL url = FileLocator.find(bundle, new Path("icons/categoryicon_1.png"), null);
    ImageDescriptor categoryicon_1 = ImageDescriptor.createFromURL(url);

    URL url2 = FileLocator.find(bundle, new Path("icons/categoryicon_2.png"), null);
    ImageDescriptor categoryicon_2 = ImageDescriptor.createFromURL(url2);

    URL url3 = FileLocator.find(bundle, new Path("icons/categoryicon_3.png"), null);
    ImageDescriptor categoryicon_3 = ImageDescriptor.createFromURL(url3);
    
	@PostConstruct
	public void postConstruct(Composite parent,MApplication application, EModelService service) {
		GridLayout gl_parent = new GridLayout(1, false);
		gl_parent.marginWidth = 0;
		gl_parent.verticalSpacing = 0;
		gl_parent.horizontalSpacing = 0;
		gl_parent.marginTop = 10;
		gl_parent.marginHeight = 0;
		parent.setLayout(gl_parent);
		parent.setBackground(new Color (Display.getCurrent(), 226, 228, 235));
		new Label(parent, SWT.NONE);
		
		Composite composite = new Composite(parent, SWT.BORDER);
		RowLayout rl_composite = new RowLayout(SWT.HORIZONTAL);
		rl_composite.spacing = 0;
		rl_composite.marginBottom = 0;
		rl_composite.marginTop = 0;
		rl_composite.marginRight = 0;
		rl_composite.marginLeft = 0;
		composite.setLayout(rl_composite);
		GridData gd_composite = new GridData(SWT.LEFT, SWT.FILL, false, false, 0, 0);
		gd_composite.minimumWidth = 300;
		gd_composite.heightHint = 54;
		gd_composite.widthHint = 300;
		composite.setLayoutData(gd_composite);
		composite.setBackground(new Color (Display.getCurrent(), 159, 170, 222));
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setLayoutData(new RowData(47, 33));
		lblNewLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		lblNewLabel.setBackgroundImage(resourceManager.createImage(categoryicon_1));
		new Label(parent, SWT.NONE);

		
		Button btnNewButton = new Button(parent, SWT.NONE);
		btnNewButton.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		btnNewButton.setBackground(new Color (Display.getCurrent(), 159, 170, 222));
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
		    	//List<MPart> parts = service.findElements(application, "dawin.ble.part.1",MPart.class, null);
				MPartSashContainer sashContainer = (MPartSashContainer) service.find("dawin.ble.partsashcontainer.0", application);
//				MArea area = (MArea) service.find("dawin.ble.area.0", application);
//				area.getChildren().get(0).setContainerData("300");
//				area.getChildren().get(0).setContainerData("1600");
//		    	sashContainer.getChildren().remove(1);
//		    	sashContainer.getChildren().add(1,part0);
				
		    	MPart part1 = partService.findPart("dawin.ble.part.1");
		    	partService.hidePart(part1);
		    	//part1.setVisible(false);
		    	MPart part0 = partService.findPart("dawin.ble.part.0");
		    	//part0.setVisible(true);
		    	partService.showPart(part0, PartState.ACTIVATE);
		    	//sashContainer.setContainerData("20");
			}
		});
		btnNewButton.setFont(new Font(null, "¸¼Àº °íµñ", 14, SWT.NORMAL));
		btnNewButton.setAlignment(SWT.LEFT);
		GridData gd_btnNewButton = new GridData(SWT.LEFT, SWT.FILL, false, false, 0, 0);
		gd_btnNewButton.widthHint = 300;
		gd_btnNewButton.minimumWidth = 300;
		gd_btnNewButton.heightHint = 38;
		btnNewButton.setLayoutData(gd_btnNewButton);
		btnNewButton.setText(" Dashboard");
		new Label(parent, SWT.NONE);
		
		Button btnDashboard2 = new Button(parent, SWT.NONE);
		btnDashboard2.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		btnDashboard2.setBackground(new Color (Display.getCurrent(), 159, 170, 222));
		btnDashboard2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
		        MPerspective perspective = (MPerspective) modelService.find("kairos.kongde.perspective.sensor", app);
		        if (perspective != null) {
		            partService.switchPerspective(perspective);
		        }

			}
		});
		btnDashboard2.setFont(new Font(null, "¸¼Àº °íµñ", 14, SWT.NORMAL));
		btnDashboard2.setAlignment(SWT.LEFT);
		GridData gd_btnDashboard2 = new GridData(SWT.LEFT, SWT.FILL, false, false, 0, 0);
		gd_btnDashboard2.minimumWidth = 300;
		gd_btnDashboard2.widthHint = 300;
		btnDashboard2.setLayoutData(gd_btnDashboard2);
		btnDashboard2.setText(" Real Time Sensor");
		new Label(parent, SWT.NONE);


		Composite composite2 = new Composite(parent, SWT.NONE);
		RowLayout rl_composite2 = new RowLayout(SWT.HORIZONTAL);
		rl_composite2.marginLeft = 10;
		composite2.setLayout(rl_composite2);
		GridData gd_composite2 = new GridData(SWT.LEFT, SWT.FILL, false, false, 0, 0);
		gd_composite2.minimumWidth = 300;
		gd_composite2.heightHint = 45;
		gd_composite2.widthHint = 300;
		composite2.setLayoutData(gd_composite2);
		//composite2.setBackground(new Color (Display.getCurrent(), 159, 170, 222));
		Label lblNewLabel2 = new Label(composite2, SWT.NONE);
		lblNewLabel2.setLayoutData(new RowData(50, 30));
		lblNewLabel2.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		lblNewLabel2.setBackgroundImage(resourceManager.createImage(categoryicon_2));

		Composite composite3 = new Composite(parent, SWT.NONE);
		RowLayout rl_composite3 = new RowLayout(SWT.HORIZONTAL);
		rl_composite3.marginLeft = 10;
		composite3.setLayout(rl_composite3);
		GridData gd_composite3 = new GridData(SWT.LEFT, SWT.FILL, false, false, 0, 0);
		gd_composite3.minimumWidth = 300;
		gd_composite3.heightHint = 45;
		gd_composite3.widthHint = 300;
		composite3.setLayoutData(gd_composite3);
		//composite2.setBackground(new Color (Display.getCurrent(), 159, 170, 222));
		Label lblNewLabel3 = new Label(composite3, SWT.NONE);
		lblNewLabel3.setLayoutData(new RowData(42, 42));
		lblNewLabel3.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		lblNewLabel3.setBackgroundImage(resourceManager.createImage(categoryicon_3));

//		Button btnConfig = new Button(parent, SWT.FLAT);
//		//btnConfig.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
//		//btnConfig.setBackground(new Color (Display.getCurrent(), 159, 170, 222));
//		btnConfig.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
//		btnConfig.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseUp(MouseEvent e) {
////				MPartSashContainer sashContainer = (MPartSashContainer) service.find("dawin.ble.partsashcontainer.0", application);
////		    	sashContainer.getChildren().remove(1);
////		    	sashContainer.getChildren().add(1,part1);
//				
//		    	MPart part0 = partService.findPart("dawin.ble.part.0");
//		    	partService.hidePart(part0);
//		    	//part0.setVisible(false);
//		    	MPart part1 = partService.findPart("dawin.ble.part.1");
//		    	//part1.setVisible(true);
//		    	partService.showPart(part1, PartState.ACTIVATE);
//			}
//		});
//		btnConfig.setFont(new Font(null, "¸¼Àº °íµñ", 14, SWT.NORMAL));
//		btnConfig.setAlignment(SWT.LEFT);
//		GridData gd_btnConfig = new GridData(SWT.LEFT, SWT.FILL, false, false, 0, 0);
//		gd_btnConfig.minimumWidth = 300;
//		gd_btnConfig.widthHint = 300;
//		btnConfig.setLayoutData(gd_btnConfig);
//		btnConfig.setText("Register Mote");

    	MPart part1 = partService.findPart("dawin.ble.part.1");
    	partService.hidePart(part1);
    	MPart part0 = partService.findPart("dawin.ble.part.0");
    	partService.showPart(part0, PartState.ACTIVATE);
    	
		MArea area = (MArea) service.find("dawin.ble.area.0", application);
		area.getChildren().get(0).setContainerData("300");
		area.getChildren().get(0).setContainerData("1600");

	}
}