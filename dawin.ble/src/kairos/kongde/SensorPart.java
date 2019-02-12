
package kairos.kongde;


import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import kairos.kongde.entity.Ap;
import kairos.kongde.entity.Rawdata;
import kairos.kongde.entity.Sensor;
import kairos.kongde.entity.Tags;
import kairos.kongde.entity.Topic;
import kairos.kongde.entity.Transform;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.rap.rwt.scripting.ClientListener;
import org.eclipse.rap.rwt.widgets.WidgetUtil;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;

public class SensorPart {
	private Ap ap;
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("kairos.kongde");
    EntityManager em = emf.createEntityManager();
	
	TreeViewer treeViewer;

	static List<Ap> apList = new ArrayList<Ap>();

	@Inject UISynchronize sync;
	@Inject
	private EPartService partService;
	@Inject
	private EModelService modelService;
	@Inject
	private MApplication app;
	
	private Text text;
	Composite child;
	
	float levelScale = 1.0f;
	SensorWidget sensorWidget;
	private Text text_1;
	private Text text_2;
	
	Bundle bundle = FrameworkUtil.getBundle(this.getClass());
	
    URL url9 = FileLocator.find(bundle, new Path("icons/categoryicon_1.png"), null);
    ImageDescriptor categoryicon_1 = ImageDescriptor.createFromURL(url9);

    URL url10 = FileLocator.find(bundle, new Path("icons/categoryicon_2.png"), null);
    ImageDescriptor categoryicon_2 = ImageDescriptor.createFromURL(url10);

    URL url11 = FileLocator.find(bundle, new Path("icons/categoryicon_3.png"), null);
    ImageDescriptor categoryicon_3 = ImageDescriptor.createFromURL(url11);

//	MqttClient mqttClient;
	ResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources());
	
	Cursor handc = new Cursor(Display.getCurrent(),SWT.CURSOR_HAND);
	
	@SuppressWarnings("serial")
	@PostConstruct
	public void postConstruct(Composite parent, EMenuService menuService, IEventBroker eventBroker,ESelectionService selectionService) {
		
		Bundle bundle = FrameworkUtil.getBundle(this.getClass());
//      URL url = FileLocator.find(bundle, new Path("icons/floor.jpg"), null);
      URL url = FileLocator.find(bundle, new Path("icons/map.png"), null);
      ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(url);
		Image image = resourceManager.createImage(imageDescriptor);

      URL url2 = FileLocator.find(bundle, new Path("icons/wig_32.png"), null);
      ImageDescriptor imageDescriptor2 = ImageDescriptor.createFromURL(url2);
		Image image2 = resourceManager.createImage(imageDescriptor2);

		GridLayout gl_parent = new GridLayout(2, false);
		gl_parent.verticalSpacing = 0;
		gl_parent.marginWidth = 0;
		gl_parent.marginHeight = 0;
		gl_parent.horizontalSpacing = 0;
		gl_parent.marginTop = 20;
		parent.setLayout(gl_parent);
		parent.setBackground(new Color (Display.getCurrent(), 226, 228, 235));
		
		Composite composite_5 = new Composite(parent, SWT.NONE);
		GridLayout gl_composite_5 = new GridLayout(1, false);
		gl_composite_5.marginTop = 10;
		gl_composite_5.verticalSpacing = 0;
		gl_composite_5.horizontalSpacing = 0;
		gl_composite_5.marginWidth = 0;
		gl_composite_5.marginHeight = 0;
		composite_5.setLayout(gl_composite_5);
		GridData gd_composite_5 = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		gd_composite_5.minimumWidth = 300;
		gd_composite_5.widthHint = 300;
		composite_5.setLayoutData(gd_composite_5);

		Composite composite_4 = new Composite(composite_5, SWT.NONE);
		GridData gd_composite_4 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite_4.heightHint = 45;
		composite_4.setLayoutData(gd_composite_4);
		composite_4.setBackground(new Color (Display.getCurrent(), 159, 170, 222));
		
		Label lblNewLabel = new Label(composite_4, SWT.NONE);
		lblNewLabel.setLocation(40, 3);
		lblNewLabel.setSize(47, 33);
		lblNewLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		lblNewLabel.setBackgroundImage(resourceManager.createImage(categoryicon_1));

		Composite composite_6 = new Composite(composite_5, SWT.NONE);
		GridLayout gl_composite_6 = new GridLayout(1, false);
		gl_composite_6.marginLeft = 40;
		composite_6.setLayout(gl_composite_6);
		GridData gd_composite_6 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite_6.heightHint = 45;
		composite_6.setLayoutData(gd_composite_6);
		composite_6.setBackground(new Color (Display.getCurrent(), 159, 170, 222));
		
		Label lblNewLabel2 = new Label(composite_6, SWT.NONE);
		lblNewLabel2.setFont(new Font(null, "∏º¿∫ ∞ÌµÒ", 18, SWT.NORMAL));
		lblNewLabel2.setText("Dashboard");
		lblNewLabel2.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		lblNewLabel2.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Composite composite_7 = new Composite(composite_5, SWT.NONE);
		GridLayout gl_composite_7 = new GridLayout(1, false);
		gl_composite_7.marginLeft = 40;
		composite_7.setLayout(gl_composite_7);
		GridData gd_composite_7 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite_7.heightHint = 45;
		composite_7.setLayoutData(gd_composite_7);
		composite_7.setBackground(new Color (Display.getCurrent(), 159, 170, 222));
		
		Label lblNewLabel3 = new Label(composite_7, SWT.NONE);
		lblNewLabel3.setFont(new Font(null, "∏º¿∫ ∞ÌµÒ", 18, SWT.NORMAL));
		lblNewLabel3.setText("Real Time Sensor");
		lblNewLabel3.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		lblNewLabel3.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Composite composite_8 = new Composite(composite_5, SWT.NONE);
		GridData gd_composite_8 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite_8.heightHint = 45;
		composite_8.setLayoutData(gd_composite_8);
		//composite_8.setBackground(new Color (Display.getCurrent(), 159, 170, 222));
		
		Label lblNewLabel4 = new Label(composite_8, SWT.NONE);
		lblNewLabel4.setLocation(40, 7);
		lblNewLabel4.setSize(50, 30);
		lblNewLabel4.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		lblNewLabel4.setBackgroundImage(resourceManager.createImage(categoryicon_2));

		Composite composite_9 = new Composite(composite_5, SWT.NONE);
		GridLayout gl_composite_9 = new GridLayout(1, false);
		gl_composite_9.marginLeft = 40;
		composite_9.setLayout(gl_composite_9);
		GridData gd_composite_9 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite_9.heightHint = 45;
		composite_9.setLayoutData(gd_composite_9);
		
		Label lblNewLabel5 = new Label(composite_9, SWT.NONE);
		lblNewLabel5.setFont(new Font(null, "∏º¿∫ ∞ÌµÒ", 18, SWT.NORMAL));
		lblNewLabel5.setText("Mote Status");
		
		Composite composite_10 = new Composite(composite_5, SWT.NONE);
		GridLayout gl_composite_10 = new GridLayout(1, false);
		gl_composite_10.marginLeft = 40;
		composite_10.setLayout(gl_composite_10);
		GridData gd_composite_10 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite_10.heightHint = 45;
		composite_10.setLayoutData(gd_composite_10);
		
		Label lblNewLabel6 = new Label(composite_10, SWT.NONE);
		lblNewLabel6.setFont(new Font(null, "∏º¿∫ ∞ÌµÒ", 18, SWT.NORMAL));
		lblNewLabel6.setText("Sensor Status");
		
		Composite composite_11 = new Composite(composite_5, SWT.NONE);
		GridLayout gl_composite_11 = new GridLayout(1, false);
		gl_composite_11.marginLeft = 40;
		composite_11.setLayout(gl_composite_11);
		GridData gd_composite_11 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite_11.heightHint = 45;
		composite_11.setLayoutData(gd_composite_11);
		
		Label lblNewLabel7 = new Label(composite_11, SWT.NONE);
		lblNewLabel7.setFont(new Font(null, "∏º¿∫ ∞ÌµÒ", 18, SWT.NORMAL));
		lblNewLabel7.setText("Network Status");
		
		Composite composite_12 = new Composite(composite_5, SWT.NONE);
		composite_12.setLayout(new GridLayout(1, false));
		GridData gd_composite_12 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite_12.heightHint = 45;
		composite_12.setLayoutData(gd_composite_12);
		
		Label label = new Label(composite_12, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		
		Composite composite_13 = new Composite(composite_5, SWT.NONE);
		GridData gd_composite_13 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite_13.heightHint = 45;
		composite_8.setLayoutData(gd_composite_13);
		//composite_8.setBackground(new Color (Display.getCurrent(), 159, 170, 222));
		
		Label lblNewLabel8 = new Label(composite_13, SWT.NONE);
		lblNewLabel8.setLocation(40, 2);
		lblNewLabel8.setSize(42, 42);
		lblNewLabel8.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		lblNewLabel8.setBackgroundImage(resourceManager.createImage(categoryicon_3));

		Composite composite_14 = new Composite(composite_5, SWT.NONE);
		GridLayout gl_composite_14 = new GridLayout(1, false);
		gl_composite_14.marginLeft = 40;
		composite_14.setLayout(gl_composite_14);
		GridData gd_composite_14 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite_14.heightHint = 45;
		composite_14.setLayoutData(gd_composite_14);
		
		Label lblNewLabel9 = new Label(composite_14, SWT.NONE);
		lblNewLabel9.setFont(new Font(null, "∏º¿∫ ∞ÌµÒ", 18, SWT.NORMAL));
		lblNewLabel9.setText("Configuration");
		
		Composite composite_17 = new Composite(composite_5, SWT.NONE);
		GridLayout gl_composite_17 = new GridLayout(1, false);
		gl_composite_17.marginLeft = 40;
		composite_17.setLayout(gl_composite_17);
		GridData gd_composite_17 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite_17.heightHint = 45;
		composite_17.setLayoutData(gd_composite_17);
		
		Label lblNewLabel10 = new Label(composite_17, SWT.NONE);
		lblNewLabel10.setFont(new Font(null, "∏º¿∫ ∞ÌµÒ", 18, SWT.NORMAL));
		lblNewLabel10.setText("Setup Gateway");
		
		Composite composite_16 = new Composite(composite_5, SWT.NONE);
		GridLayout gl_composite_16 = new GridLayout(1, false);
		gl_composite_16.marginLeft = 40;
		composite_16.setLayout(gl_composite_16);
		GridData gd_composite_16 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite_16.heightHint = 45;
		composite_16.setLayoutData(gd_composite_16);
		
		Label lblNewLabel11 = new Label(composite_16, SWT.NONE);
		lblNewLabel11.setCursor(handc);
		lblNewLabel11.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
		        MPerspective perspective = (MPerspective) modelService.find("dawin.ble.perspective.moteconfig", app);
		        if (perspective != null) {
		            partService.switchPerspective(perspective);
		        }
			}
		});
		lblNewLabel11.setFont(new Font(null, "∏º¿∫ ∞ÌµÒ", 18, SWT.NORMAL));
		lblNewLabel11.setText("Register Mote");
		
		Composite composite_18 = new Composite(composite_5, SWT.NONE);
		GridLayout gl_composite_18 = new GridLayout(1, false);
		gl_composite_18.marginLeft = 40;
		composite_18.setLayout(gl_composite_18);
		GridData gd_composite_18 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite_18.heightHint = 45;
		composite_18.setLayoutData(gd_composite_18);
		
		Label lblNewLabel12 = new Label(composite_18, SWT.NONE);
		lblNewLabel12.setFont(new Font(null, "∏º¿∫ ∞ÌµÒ", 18, SWT.NORMAL));
		lblNewLabel12.setText("Register Sensor");
		
		Composite composite_19 = new Composite(composite_5, SWT.NONE);
		composite_19.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
		        MPerspective perspective = (MPerspective) modelService.find("dawin.ble.perspective.dashboard", app);
		        if (perspective != null) {
		            partService.switchPerspective(perspective);
		        }
			}
		});
		GridLayout gl_composite_19 = new GridLayout(1, false);
		gl_composite_19.marginLeft = 40;
		composite_19.setLayout(gl_composite_19);
		GridData gd_composite_19 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite_19.heightHint = 45;
		composite_19.setLayoutData(gd_composite_19);
		
		Label lblNewLabel13 = new Label(composite_19, SWT.NONE);
		lblNewLabel13.setCursor(handc);
		lblNewLabel13.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
		        MPerspective perspective = (MPerspective) modelService.find("dawin.ble.perspective.dashboard", app);
		        if (perspective != null) {
		            partService.switchPerspective(perspective);
		        }
			}
		});
		lblNewLabel13.setFont(new Font(null, "∏º¿∫ ∞ÌµÒ", 18, SWT.NORMAL));
		lblNewLabel13.setText("LBS Dashboard");
		
		Composite composite_20 = new Composite(composite_5, SWT.NONE);
		composite_20.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
		        MPerspective perspective = (MPerspective) modelService.find("kairos.kongde.perspective.sensor", app);
		        if (perspective != null) {
		            partService.switchPerspective(perspective);
		        }
			}
			
		});

		GridLayout gl_composite_20 = new GridLayout(1, false);
		gl_composite_20.marginLeft = 40;
		composite_20.setLayout(gl_composite_20);
		GridData gd_composite_20 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite_20.heightHint = 45;
		composite_20.setLayoutData(gd_composite_20);
		
		Label lblNewLabel14 = new Label(composite_20, SWT.NONE);
		lblNewLabel14.setCursor(handc);
		lblNewLabel14.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
		        MPerspective perspective = (MPerspective) modelService.find("kairos.kongde.perspective.sensor", app);
		        if (perspective != null) {
		            partService.switchPerspective(perspective);
		        }
			}
		});
		lblNewLabel14.setFont(new Font(null, "∏º¿∫ ∞ÌµÒ", 18, SWT.NORMAL));
		lblNewLabel14.setText("LBS Map");
		
		Composite composite_1 = new Composite(parent, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		GridLayout gl_composite_1 = new GridLayout(2, false);
		gl_composite_1.marginHeight = 0;
		composite_1.setLayout(gl_composite_1);
		composite_1.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		treeViewer = new TreeViewer(composite_1, SWT.BORDER);
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
//				partService.activate(partService.findPart("kairos.kongde.part.config"));
				// selection set
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				IStructuredSelection structuredSelection = (IStructuredSelection) selection;
				selectionService.setSelection(structuredSelection);
				
//				IStructuredSelection thisSelection = (IStructuredSelection) event.getSelection();
		        //Object selectedNode = thisSelection.getFirstElement();
		        //viewer.setExpandedState(selectedNode,  !viewer.getExpandedState(selectedNode));
		        Object element = selection.getFirstElement();
		        if (element instanceof Ap) {
	            	Ap ap = (Ap) element;
		        	
//					Collection<MPart> parts = partService.getParts();
//					for (MPart part : parts) {
//						if(part.getLabel().equals("AP["+sensor.getAddress()+"] ")) {
//							partService.activate(part);
//							
//							SensorPart sensorPart = (SensorPart)part.getObject();
//							sensorPart.init(sensor);
//							return;
//						}
//					}
	            	init(ap);
		        	
//	            	MPart part = partService.createPart("kairos.kongde.partdescriptor.sensorpart");
//		        	part.setLabel("AP["+sensor.getAddress()+"] ");
//		        	partService.showPart(part, PartState.ACTIVATE);
//		        	
//					SensorPart sensorPart = (SensorPart)part.getObject();
//					sensorPart.init(sensor);
	            }
			}
		});
		menuService.registerContextMenu(treeViewer.getControl(), "dawin.ble.popupmenu.config");
		
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				//TreeViewer viewer = (TreeViewer) event.getViewer();
		        IStructuredSelection thisSelection = (IStructuredSelection) event.getSelection();
		        //Object selectedNode = thisSelection.getFirstElement();
		        //viewer.setExpandedState(selectedNode,  !viewer.getExpandedState(selectedNode));
		        Object element = thisSelection.getFirstElement();
		        if (element instanceof Ap) {
	            	Ap ap = (Ap) element;
		        	
//					Collection<MPart> parts = partService.getParts();
//					for (MPart part : parts) {
//						if(part.getLabel().equals("AP["+sensor.getAddress()+"] ")) {
//							partService.activate(part);
//							
//							SensorPart sensorPart = (SensorPart)part.getObject();
//							sensorPart.init(sensor);
//							return;
//						}
//					}
	            	init(ap);
		        	
//	            	MPart part = partService.createPart("kairos.kongde.partdescriptor.sensorpart");
//		        	part.setLabel("AP["+sensor.getAddress()+"] ");
//		        	partService.showPart(part, PartState.ACTIVATE);
//		        	
//					SensorPart sensorPart = (SensorPart)part.getObject();
//					sensorPart.init(sensor);
	            }
			}
		});
		Tree tree = treeViewer.getTree();
		tree.setCursor(handc);
		tree.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1));
		tree.setSize(164, 288);
		
		TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
		treeViewerColumn.setLabelProvider(new ColumnLabelProvider() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 952866509572761159L;

			private ResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources());
			
			Bundle bundle = FrameworkUtil.getBundle(this.getClass());
	        // use the org.eclipse.core.runtime.Path as import
	        URL url = FileLocator.find(bundle, new Path("icons/topic16.png"), null);
	        ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(url);
	        
	        URL url2 = FileLocator.find(bundle, new Path("icons/folder.png"), null);
	        ImageDescriptor imageDescriptorFolder = ImageDescriptor.createFromURL(url2);

	        URL url3 = FileLocator.find(bundle, new Path("icons/beacon16.png"), null);
	        ImageDescriptor imageDescriptorSensor = ImageDescriptor.createFromURL(url3);

	        URL url4 = FileLocator.find(bundle, new Path("icons/transform16.png"), null);
	        ImageDescriptor imageDescriptorTransform = ImageDescriptor.createFromURL(url4);

			public Image getImage(Object element) {
				if (element instanceof String) {
					return resourceManager.createImage(imageDescriptorFolder);
	            }else
				if (element instanceof Ap) {
					//Topic topic = (Topic) element;
					return resourceManager.createImage(imageDescriptorSensor);
	            }
	            return null;
			}
			public String getText(Object element) {
				//return element == null ? "" : element.toString();
				if (element instanceof String) {
					//String list = (String[]) element;
					return element.toString();
	            }else
				if (element instanceof Topic) {
					Topic topic = (Topic) element;
					return topic.getName();
	            }else
				if (element instanceof Transform) {
					Transform transform = (Transform) element;
					return transform.getName();
	            }else
				if (element instanceof Ap) {
					Ap ap = (Ap) element;
					return ap.getApid()+"";
	            }
	            return null;
			}

			@Override
			public Color getForeground(Object element) {
				if (element instanceof Sensor) {
					if (((Sensor) element).getActive() ) {
						return Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN);
					}else {
						return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
					}
				}
				return super.getForeground(element);
			}
//			@Override
//			public Color getBackground(Object element) {
//				if (element instanceof Sensor) {
//					if (((Sensor) element).getActive() ) {
//						return Display.getCurrent().getSystemColor(SWT.COLOR_GREEN);
//					}else {
//						return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
//					}
//				}
//				return super.getBackground(element);
//			}
			@Override
			public void dispose() {
				resourceManager.dispose();
				resourceManager = null;
				super.dispose();
			}

		});

		TreeColumn treeColumn = treeViewerColumn.getColumn();
		treeColumn.setWidth(300);
		treeColumn.setText("name");
		
				TabFolder tabFolder = new TabFolder(composite_1, SWT.NONE);
				tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
				tabFolder.setSize(0, 288);
				
				TabItem tbtmId = new TabItem(tabFolder, 0);
				tbtmId.setText("ID\uC124\uC815");
				
				Composite composite = new Composite(tabFolder, SWT.NONE);
				tbtmId.setControl(composite);
				GridLayout gl_composite = new GridLayout(2, false);
				gl_composite.marginHeight = 25;
				composite.setLayout(gl_composite);
				
				Label lblRemark = new Label(composite, SWT.NONE);
				GridData gd_lblRemark = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
				gd_lblRemark.widthHint = 100;
				lblRemark.setLayoutData(gd_lblRemark);
				lblRemark.setText("\uC124\uBA85");
				lblRemark.setAlignment(SWT.RIGHT);
				
				text = new Text(composite, SWT.BORDER);
				text.addFocusListener(new FocusAdapter() {
					@Override
					public void focusLost(FocusEvent event) {
						ap.setRemark(text.getText());
						
						em.getTransaction().begin();
						em.merge(ap);
						em.getTransaction().commit();
						
//				MPart part =partService.findPart("kairos.kongde.part.config");
//				ConfigPart configPart = (ConfigPart)part.getObject();
//				configPart.refreshConfig();
						refreshConfig();
					}
				});
				text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
				
				Label lblAddress = new Label(composite, SWT.NONE);
				lblAddress.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
				lblAddress.setAlignment(SWT.RIGHT);
				lblAddress.setText("ID");
				
				text_1 = new Text(composite, SWT.BORDER);
				text_1.addFocusListener(new FocusAdapter() {
					@Override
					public void focusLost(FocusEvent event) {
						ap.setApid(Integer.parseInt(text_1.getText()));
						
						em.getTransaction().begin();
						em.merge(ap);
						em.getTransaction().commit();
						
//				MPart part =partService.findPart("kairos.kongde.part.config");
//				ConfigPart configPart = (ConfigPart)part.getObject();
//				configPart.refreshConfig();
						refreshConfig();
					}
				});
				text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
				
				Label lblMacAddress = new Label(composite, SWT.NONE);
				lblMacAddress.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
				lblMacAddress.setText("MAC Address");
				lblMacAddress.setAlignment(SWT.RIGHT);
				
				text_2 = new Text(composite, SWT.BORDER);
				text_2.setEditable(false);
				text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
				//		canvas.setData("x", 100);
				//		canvas.setData("y", 100);
				//		canvas.addPaintListener(new PaintListener() {
				//			public void paintControl(PaintEvent event) {
				//				System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXX");
				//
				//				//event.gc.drawText("You can draw text directly on a canvas", 60, 60);
				//				
				//				//ImageData.
				//				float scaleTo = event.height* 1.0f / event.width;
				//				
				//				if(scale > scaleTo) {	// ºº∑Œê¨√„. ∞°∑Œ∞° ¡ŸæÓµÎ.
				//					event.gc.drawImage(image, 0,0,image.getBounds().width,image.getBounds().height,0,0,(int)(event.height/scale),event.height);
				//					
				//				}else {					// ∞°∑Œê¨√„. ºº∑Œ∞° ¡ŸæÓµÎ.
				//					event.gc.drawImage(image, 0,0,image.getBounds().width,image.getBounds().height,0,0,event.width,(int) (event.width*scale));
				//				}
				//				event.gc.setBackground(display.getSystemColor(SWT.COLOR_CYAN));
				//				//event.gc.fillOval(10, 10, 10, 10);
				//				event.gc.fillOval((int)canvas.getData("x"), (int)canvas.getData("y"), 10, 10);
				//			}
				//		});
				//String dataUrl = "data:image/jpeg;charset=utf-8;base64,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"
				//	+ "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"
				//	+ "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"		
				//	+ "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ZqAYWYofmNlsu6To1WWDa9r8RjP5VzxX3as+jOFZygzTwySyuFPSVl3ULA6nD2dN+Ngb+S3ENb33ZX98NO+NI/zwd92V/fDTvjSP88cd+OU/fNSfjrXpYO/HKfvmpPx1r0sB8rzPlBxxt5yuUtTjRJbUZDZKCRY2N+HDhj777sr++GnfGkf54rKhtMyTT6xSaO9muiJeq630MJVUWgpZaaLitIv2rJSSfIOOLLvxyn75qT8da9LAc992V/fDTvjSP88Hfdlf3w0740j/ADxx345T981J+OtelimzXnRLdL15PzDlx2eFjszJqNBRY3tZae1ytxseRsDqAXXfdlf3w0740j/PB33ZX98NO+NI/wA8Z/KedpLqZfflXcrsqDgEbqU9JBTxvfUs+ax4ecDli/78cp++ak/HWvSwHPfdlf3w0740j/PHWMzZQS+qSmt0sPLSlCnBIb1FKSSkE35AqVb7Z8uPvvxyn75qT8da9LFa1tLyW7mOVl0Zpowfiwo05Se6DWsoecfQDpvwF2FC/jJI4W4hZ992V/fDTvjSP88Hfdlf3w0740j/ADxx345T981J+Otelg78cp++ak/HWvSwHPfdlf3w0740j/PB33ZX98NO+NI/zxmc4Z2qTSoxyTXsqvJKHOsibNTcKujRos4L8N5cHzcfEbLLedIbtJQvNGYMvtVDeOhaYs5Bb061aLXUT4On/IeCAtO+7K/vhp3xpH+eDvuyv74ad8aR/njjvxyn75qT8da9LB345T981J+OtelgPlrM+UGS4pmuUtBdXrcKZDY1KsBc8eJsAPuDH333ZX98NO+NI/zxW0naTkyqv1NhnNNFUqmTTCd0VBpRSsNNuWVx7J0upNvIQfHix78cp++ak/HWvSwFfX80wnqY43l7NdHjzrpLbj8hCkAA8QRfjcXHi53vigypmfNTVVWrOObcouwNLoSIMkherUjQbK5C2vhc+e9+Gv78cp++ak/HWvSxjsx54zO1VnBles5Nep2hG7MqckOhVxqBs4ARzt5r+MALDZd92V/fDTvjSP8APB33ZX98NO+NI/zxHgZyy+qDHVUcyUVMstIL4amt6A5pGoJurle9sd/fjlP3zUn4616WA577sr++GnfGkf54+GMz5QjNJYj1yltNp8FCJDYA+0AcfYzflRRCRmalEk2A6616WKvLW03JGZaFDr0HNlEdjzm960tqotLQpNyAQoHiOHPAWnfdlf3w0740j/PB33ZX98NO+NI/zxx345T981J+Otelg78cp++ak/HWvSwHPfdlf3w0740j/PB33ZX98NO+NI/zxx345T981J+OteljNZoztUmX0nKVeyo+0Q3cS5iAUn1TXdQcHA+pWskkHVe4PZDTd92V/fDTvjSP88Hfdlf3w0740j/PFFlLOqnae6rOVey2zMDtm0w5qNBb3aOJus8devx8OA421G878cp++ak/HWvSwHPfdlf3w0740j/PB33ZX98NO+NI/wA8cd+OU/fNSfjrXpYO/HKfvmpPx1r0sB8uZnyg64265XKWpbKiptRkNkoJBBIN+HAkfaJx9992V/fDTvjSP88VlS2mZJptVo9KezXRUvViQ6wwlVRaClFthx5WkX7RCWyT5Bc+LFl345T981J+OtelgOe+7K/vhp3xpH+eDvuyv74ad8aR/njjvxyn75qT8da9LFPm"		
				//	+ "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"		
				//	+ "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"		
				//	+ "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"		
				//	+ "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"		
				//	+ "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"		
				//	+ "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"		
				//	+ "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"		
				//	+ "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"		
				//	+ "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"		
				//	+ "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"		
				//	+ "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"		
				//	+ "YUpx/eq6qwthsX3AsEF5RHADmo/c5YQG09DqulXsq0uKXqbzcC0HlIUB3Bpx131HzDxePj4sA+65SanJqUp+Kh5Da1wgpuShL1lBwi4IkJtcKItby8eOKSRkSpPPqCX3IzSH247bUVgMobT1d1FkhMoWumS4LeLgRYjGxmRXQ/I9aSPZof7vc91HnwGK7vljqUj2+jh19z3JPnwC/wA4ZCzFmaNTkPVmtIWxVUkCI641rSJVllSUyrGyFOE2SLi97gCy+6XcVUPYVl2IGJwSxmHKbQW88hSSE1ymi9gs+TxDD1nLk09uJIahvXVVNwSqc6Rpdl7tRAChxss/d8vjXO3zZzVNpeQKBlWmvs095daoM7fzJbq2giPVoD602F+0pLakpFrFRSCQO0AfmDBgwBhe9Ij9YHaX/I+tfkL2GFhe9Ij9YHaX/I+tfkL2AznRfqNXb6NWyZtnLzzqE5GoASsSGhqHc5jjYqvhnd1K572H/jLPpYRnRp2tU6m9HTZZT15JzzIVGyVQmS7Gy3KdaWUwGBqQsJspJtwI4EccMj5M9N94G0H/AMLS/RwGr7qVz3sP/GWfSwd1K572H/jLPpYynyZ6b7wNoP8A4Wl+jg+TPTfeBtB/8LS/RwGr7qVz3sP/ABln0sHdSue9h/4yz6WMp8mem+8DaD/4Wl+jg+TPTfeBtB/8LS/RwGqNUrlv1MP/ABln0sI7omLcczj0hFuslpR2rzLoJBI+ZdN8Y4YY52z0236gNoP/AIWl+jhYdDypN1jMm36ptRJkZEjapMWGZcdTDyPmXTeC21AKSfMcB6RwYMGAr8vfOCm/wNn+4MWGK/L3zgpv8DZ/uDFhgDCHo/7OjNP809C/xmrYfGEPR/2dGaf5p6F/jNWwD4wYMGAwOao2cZrGYzkqpMwsyMPRW6e5IYQ62IqkN6roURdJUZBuFAlTabkhOk1WSXtpbUSmO5thFWYnJ01lSJsmOhbtNsVoU4qIhTV0OKabSQm9lXNitWGJUKNCqK0Pu75p9vgl5h5bTgHjTqSRdPAdk3FwDa4xzT6PDpqnHWd6489YOPPOqdcUByTqUSQkXJCRYAkkAXOAy01Tys/UxUltDbpTD1pQsqSlXV6ncBRAuPPYfaGJedJGZmanlxNCpNMlsqqlpC5dRcjKbR1WRcoShhzWb6eBKeZ4iwvXZhbqi9olP7nSorPCLffR1OXO4qduS02xPrrGahLouqp0onugbWgOD9zP/ZsBhKxC26jN6JkGVQWqc/X4m7YqTrjyW46WJO+DBaANzdu2tKeIPkud6E7Uri68rEeMBMkf+uOK6xmrrFI1VSlE90E2tT3B+0u/ZsWu4zb++tJ/F7n5/AVOXpObBHmaaNST80JXOpuD9tV/1fFr1rN37y0n8aO/6fGJr+ba1k+hGQyWJMpdQnFTbQQ0XyJASAjersLJWVHio2QbW8VPJ2wV5iU40mKpTDYkjfqci2UppAUgBCFrWQvtAFIULp4arjAa3aDOzCltcGFDiuNPUue4425MU1fSuMEKBS2o37axbha/M+KDApubocp5RokBYlVh98WzBJFvUVJt7D8C9/PheZ12sSXYFUmvOL106hvXYLLZVJU91Jam2rEpXu9ZCyDYFKvIoJ0mS3KtW48ypPVGitodzDPbY9TQtKm20rbCkq5FJKCR9vkDfAKzpjVTa9SNmNFGRI7tPq0rMqWmzT6h1510JU86pG6kNoQQN1rvqB9THPik9GRMpdK6dlClyKpn2sxJLzekMzYjTLrbYUUp3qWYzqEEoQggIU4LEXIN7/ecnaxWsyTqFDiRpjWWJ6pj9QcedUGHXn5CAWUB4oICHUo0ltIT2gL8Cn09S4GYDTYlsxN+wN8OpJ+lHwsB587yuk0q6htJqIJdSlAVHAIF+0pdoZsBrcPDVe3nF+TkrpNi5TtJqHBaUJuwLg9i6jaGRpBHMEns8hfD5rlRnZdhpnVHMT621uBpCIdFclurUQTZLTIWtXAEmyTYAk2AxMiN1mdFZmRsyoU1IbS62VQAklKhcGxII4HkRfAefnsj9ItMN5Te02srkIZSA11FvQpz1RJSF9XvoCdPa0hV1kaBpJx0yshdJJ5S0v7RZr6Gzr4xUq1KuogpBh8Ryvex7XI2th/VmZUaDAXUZ+Y3VMoUlGmLSFyXVKUoJAS01qWo3I8FJsLk8ATjvgd16nCj1CLmQbmU0l5re00tL0qFxdCyFJNjxSoAjkQDgPPOSdmm2bIUypTaIukCXWVJk1SW5CkLclOpddcSrssoClDrDgIsmyUpAvYFVLtin9LGl5fpDlDp8eoqdqlOEtmnUUSltp63FINpLrCbpCFcATdaUJ4JUVp9OVeRU6JT3anPzIosM6QoMUpT7hKlBICW29S1kqUBZIJ44qqq7U6tl6FUotfWWJE+nqQHqYqO4PXrI7TblloIPApUAQeYwCYy9kvbexXomepUelRq/Jy9Hpc7dwng2hCHt+W0gNcV711291abJABPBRuqq50hI8Jp3rdMQpciMkkRZA7KnkpKeLXhHeKFjYWPhc7O1MCv6R/zja5fUSfSxS5yar0OgrlmuB/cyobgbbhJKlESWiABq4k2sMAvo6NvC2kLfnQUPKdUUJFNkKQEAFSSpRSClRUhsaQkiyidVxbHRuekFoS2mTSglZLadcST2UgKTddmjpuOVtQ4+LxskVPMApJqRrUayX9xq3Kdxztr3v0nwtPPhbFnBazBNhMTDXQyX2ku7tyCkKRcA2Pa5i+AUqvlhCVLbfpdy8lKNUOSnmq6lLs2bAFa+KdV9PnBPW7H2+uNONOOUpbYAQErhSDdQCeJG7tp7A4i54ch4nN3PzD74m/iSfSxls01GuRl9QGZkIbQfVVKjbkSFaSRHbdSsaXCBrJ8SUnkCpaA8j7VH+kPQdtcFeVmEdak0SJFqciJRGlx4kRcqWA4p15bbqBdSzqQhRCU3CQoEYf42SbXUJ0IlbO0p4gAUVPn+xec4yuZZdYXtIrUeq5riJm960FndLjpQ8D1maQ2pJXcr0rSrkDpcRccbn0TIjVqMy5JfzMy000lTi1KhpASkC5JJVyAwCjXkzbBliI9UHMyZFp7ZWwgqZpKW0qUp5CE61aE2A1Xvx5WtxuMB3T2lQoOa6nSpWz96dlaVJiR40eAgPTy3FbcBbKWSoFaXNNgFG4Ngo8MPivTps6nqiQM6sdZ61GQnRTwtbbm+bPgBVyoAhWk+LiRbGLyBDzCxNz2y9mhtx1GapSXHFQ207xXUI11ad4LcPEBgFfVcx7Ynafl2tv0zI9Qm12S0xOZVAWp2npUvSVr1RN4QNSiN6hkACy1NKskz8xwdt0F1pGTqBkHNCSthL64sFlpLQcK7klbQSbbpF+PDeJJsm6sPXcV0P275WL6+HrVv3YfZsFHbqcaAtqt1pioyFOvOIdQyhgJZUQWm9IdNyhBSgqv2inVYXsA85u1vpZzaeY8vZVl5bbKG1Mx3W2VJHqY4AFqw0haxa2qwKQCsKbTmKDnLpJQ9uzGzur0Wn0WNW4qtQVlRtVPcS0zJeIakM6i8pJLKSQ3pUVOi+ltKl+wy41vD22/C+mR9M59kwrsxqQeknsy0lJ9ZVLkQf3I75FHAZjNVF2xZaZpEiPSclVFipTkU9YTQVMusqWUpSrQqNcgjVzKb24E3xmu+HpDUzcVGnZGyWhTkNEtLjcfQpGuNvlJJREOm2tQ4kG3EhPIem9pLNEqFDTS6tmCNTXVOolRkPSm2BJcZWlSEKK+OjeFu5QQoXTZSTY4UDVZaXRCFZ7piVqhqQGzuNSCaeNLRsvipIsg+Upv47YDAVaqdIORU1xatkTIshwz2Yin3GipCnet7htRcMOxG8ZRxv8A5Yt8lSM9ZgzSrNe1vKOVFRIVMaQ1vg5pSN7FdvZUQAcJhBsCTYcgDq2cyoQps0tL2iUptJrDbyVeo8S1V3nQB2/KlP3FfaxXynTWMtzIFPrsGqyH6YgNNMbvWo7qjjs2V5Ukf/ScBsjQMsyWIrz2yvKjzfdF9IQqE4pXgPHwOpatNvHa1xbnwx2w6XQ4sWJAa2bZcTCkpkKXGajvFtQLgBC2"		
				//	+ "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"		
				//	+ "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"		
				//	+ "lgnSgH6EjgRzwHZUM3VGm1KrpmMTDCiSYr63URCFhKtCUniO1co8YQE8bk4uKXWDUo8Kc3JnoS9OeIBgqPDQ6LghJCgbXBBIIII4YqFU2jVOpjM8iXMW5NkRExg4ywCdSY7iUg6NSvBBCbkeEbcTj7zBVVwZ1JYfzNPpTEioS9b6kRUEqS0shILrZTxufFfhgLdLzz7LTCJszU5HloB6iu1ytIH0HLEKC4/Hy3CjOS5epqg7tQEJdtQbQDY6OXDnhRzNpmfGWa8KbImOPUuVKYo+8rNIYFSihLC1PpUWDoVdb1kEXO5Njxvi3oebqtJolObn5vltBdOdadcTUKYtKWAyChyxjhR1lKRptcavC4HAMTMEp3u3EImzPCHOCsftMr4H/H3MWjstzfSfXs23XmOcBfka+B/VjNQq25NWsKrNRqKVyXIzcpDcYhaWzIIJKG9JtpI7NuPPkQerPCdpShU+8qcErLjIjiRHbKxI3RsVFSdJSVFiw4adCidQXZIQNtcla9l9aSZUpV5Q4LhqSPZfGSkWxs6lLfgQZdQ0Pr6qhx/SVujVoWpVr6OF7c8JzaC3tRbyNU++2ct2n9TjB/SxHSOvhSA6BoGoAq1m99NhwAuLN6pJpvUpfdBLXVNDnWLBHsWpWvwU38G/Lj5MBSVFEmk5HdckBwdUUJix1h9lOhL6Hj20IJR2RxIBtjO9/MTVHCIri1SwtxhKMyVEqfCX32zuxue1YtEm+ngRz4jGg6zKVkxb0R8CYta2WXdTQUlSnw2k6ltqAtccSDa3I2sapxvPKlNFurpSUJUFXmwiFq38g3WOrXX6mptPAosUXseZCpn52hTqSptlsLTUQ9CYcTmSoqQ45ojCyLsjUbvgWIAJSRfljoaz9T3qs7IbbCm5UdplpQzJUSlxwb1WhJ3Nys74CxAHZPHji3mLztFpxkGrkdVDrziuvQVLW2ExSlKvWoCraXuyAj2TwvGnqBzwma5EXVlLW0yHVqVUIJ1pUlaQkkRBqAU0+Snsn1QdocLAO59gMokynGShqJKQw8pWZajpbcUp5WhR3Nwr1AiwBF1J488RoOd4cajgvN6EwUsRZC1ZjqKUsuFpY0rsydKrx1Cw1C6gL+PFq41nk75CKuN4uS2W1CfCBQAt4KCT1UhIKlMmxC+DZ7XE6uiJ37v05LndUhT6WXUrTOgpUlBaWVJTeKQgHWx2SFn1MjVzKg+TneIhySFx1JMRCHZAVmSogsBbyUAueo8L74HhfgD4+GNjk+QuTQ4M5Ad3cla5SAl994aXHkLHbUgKXwUOJAJxmCjOw3oNUBUpKQkidCGlQeBUUetuxdCXUm+vwwQE8NOnyyX5FJhietDspKiy64ssqKlodbQSVJbSk3KeYSAfIOWAwzGbocCC/EfQn5lFuFJdOYai2ltdnmxrSGSG7lo2SnUBcDHe3nyCtb5MVaFRmUSnkKzLUdTTanGQFL9R4D1wk3FzYE28WO1pzN8qM7JYqaw1IWh9i86EhSGSl9WhN4x0dhTaSCFnseED2j2tNZ6TvEO1dC1qbQ2FCbCAS4HWdSkjq3ZJCXuJ1C7lwkCwSGcy/nSAwmsEJQ5qkuTjozJURu2VNxwFq9R8Cy0nVzsRw8WLlOeIjrzTTUNxbklh+S0hGZKiVOtt9bJUgFoXSdwribHiOHPHRSJWdpjc/TVlEsPLikuToPhJQwdSbRRZWnUNZ1C6idHLTY6M7h1K1VNshDT6VBU2GpK1kS9ClARwSQVNG4KR6n4P0wX2R6miswX6lEUpTLsuyFNTZUlKtKYySQ442lR4g8LC1uGEbtQdeT0qNlKVJeS2UZus6hClrSruBTeFikm1h5PHh7ZWM9cV1usPJekNSdOtbsd1WkpjqAKkNISbavpR93mUdtVfDvSh2Qtsrc1Roub2Sw0WwSDRoK94OCR9GBxueBseScB6Kmvnfv8AzQqfs0PnD+yj7FjgvnfL+aFU9vo/cf2JP2LHMzrO/ketqp7ND5use6j4WOD1nfL9bVT2+j9tY9yT8LARZLInoiR11KpoSKkp6/Ub+xSd6B7F4ygDzXwudvOc6RkfINBr1doC8yxE1mhRTT50ZCGi49VoDbbt1skXaWpLqeF9TSLFJ7Q3tb6wIkJRjVLhWmfCcZsfmgjhbVhMdMbffIVoetqckd82VuLq2in5+036VRP/AAMB6mwYMGAML3pEfrA7S/5H1r8hewwsL3pEfrA7S/5H1r8hewGF2C5cbzh0WtktHzJkihVunIyTQHW2p0grQT3NaSFFBZIB0rUOZ8IjDbU1WlyY81eVqSZEVK0sumcrWgKACgDuLi+lN/LYYXnRergY6NWyZnuRU3NGRqANSI90n5nMcQb8Rhnd8I/eSrfFf/fAZdzJZqM+PVswZEy9UZ9PmuzIEh+Upa4qlOhwFBUxdKgpKDceNIPiGOG8lFsO09ORsvppK0tuCEiUpKEyEyXJJdsGLBRec3mocdd1c+ONJHzVHlJWqPSKqsNuKaUREIstJsocT4iMAzVHMpUIUiql9LaXSjqp4IJIBve3NJ+9gM5ByYuhpisZayRQabHakJdeZYmrQlxKY62UCwYtwSpIHi0pA8QGOY+SGaXAciULIGXYK7LUypuSRu3DHEcKBDFxZoJb4fQAJ5C2NE/mqPGWy2/SaqlUhzdNDqpOpelSrc/pUqP3MfT2Z2o7Lkh6j1ZLbSStauqngkC5PPAUBykHZndeTkWhGqKQ0lya3NUh9e7bW2m7iWAo2Q44kceSyOWFd0S1Pqzj0hDJaQ24dq0zUlCypIPcum8iQL/eGHgjMiHW0ut0arFK0hST1U8Qfu4R/RMd32cekI5u1o1bV5nZWmyh8y6bzGA9E4MGDAV+XvnBTf4Gz/cGLDFfl75wU3+Bs/3BiwwBhD0f9nRmn+aehf4zVsPjCHo/7OjNP809C/xmrYB8YMGDAGODyxzgwCUpTkfLG1KecwGJS+u1FEphx91lrfNEVexvqurmDxtbeAc7kzKQ9QaJm+o1iXmOghur11L7Cmqo2slpMWR4YJGk6lHxnna+kJSNZVP1xIH/AOif7ip4u69wmUSxPzwPj/6s/gKiu5yyiqRRynNNIOmoAm05rgNy78LFr36ZP99VH+Ptelj7r3CTR+fzxHj+wu4trDz/AH8AukVOK5Mjz4WYosJp5dVDM7W2ppRE2MSkFXZNwhYsONtViCLiimTJ7aqP3dznRK/Ljul0PblAQwNcNIJCHEBSkKDruo21aTZKeATo6tnZeTYTjqKUucHp8wqCFOakjrTbQsENrJ7T6b8AAAePIGpe25oZceSvLqtDDbji3iuS20NFtQ1rjJAPG9jb7/DAZraVUt5ssdZYnQakqCy6GKcy8pl4HQtnSpwyrqTocXY2ULWNuRGj2WlnvbaEOHNjK74Ksp9LSWtKnVPSVlYN1aiUqSdWpQVe+pV7mBm/bSlWVcwluhJfNNjqTLbjSJC3WiW216SkR7ghLzZPk1W52BsMsycxVBD9Qg5eilD1entqS5mGQkpdZLzCxwY5XZJB8YI8RwGF2/1WG8/kuiJlSXJzGYX5bsdegqbYVHnoS4oI4pBUlSQTwJBHMY9D0v52RP8AuG/7ox5Q2rxcyubUIr7+WaUpSKM+QHay87p01FslYKopOpJUNPEcSo3Tx1+lqZKzf3NiWolItuG/+lHfpR/1bAWNTrsSluhp5l9dkb11TaQQy3e2tVyDbnwFzwPDhiHnCoTKdT2nYh8JxZdAe3RUhDLjhAXpVpJLY8Xl4jnjEbQJmYBMHXKVlpCzFVpEiprKj6hKHYuwPGR93Tix2izMxpoqDNpGXm2tb/GTU3Ci/VX/ACxxx8mA0yZ1Sbyi/LW8hU2Ow8jeFQKVONlSQonSBxKb30248vFgynMnSWZiJrmsMv6WvVt6QnSm4KtCb9rUeXjtfGfVMzL3lTlIpGXzHLUs60VRzc6da+I9b20fd5Y+cjTMxqjzjBpOWnEdZ4mHVHA3fSPJHPH/ANsBrYNeiT5ZhttPIKkLcaWtICXkIUEqUmxJsCpPhAX1Ai4446c1/Oxj+Mqd+WM4wuT5eYDX1iNSssKd3L+tLFTcDg4R"		
				//	+ "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"		
				//	+ "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"		
				//	+ "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"		
				//	+ "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"		
				//	+ "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"		
				//	+ "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"		
				//	+ "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"		
				//	+ "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"		
				//	+ "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"		
				//	+ "6RfSB2uu7TssUDK0SVFy0tqI/UEU+nImqBM1La3DJWppxgXcipKktrCC4lV7mwmV/PFeXVqVm7K9GmRsy5fcSumOvylblUVSkhyA6ntnqy07zkCoKKFJ0ltODD2NV4cCMwTFhMIkzJDSLttgLcVvAo3I4mwClHzAnEHPzRfobLQYW6VTY/YSLnwxxtY3tzKbG4BFjexx+znahW8/qlVo7PKgwqKAwlCpjXqd1dojXo1BWknUOYCBYHVa6zFXs6EwgjIKt2KizoK6oyFEaja4AIB+6beU4MvlilPiVAvRZhtFWFW7AWSJHI7sbo9u+m7fB5It2LYsaTFcj5ZebXGWyVykHtt7vX2mxcI0I0jha2kX0k8b3P33fzz/APh8Pxsz6OOidVs9yoxYTkAA6kK+ezP0Kgr6XzYD4FdQ6+451GQlD85EsElHZaQxGNz2uZ1psPt3tirgsVCfnJcpmlnd7uOp0KWOAE+aRrs4B2fCT2V9r6XmcZnzPmYdnEWJIr+Rny3IZk2UxNQ4EIYisrdcWQk6UpbjLVx+5e2EllnpMbY6ft/qoqGVKscmuJU03AVSmEJSlIkrCkzQ4Vqs4tvgWhcuuJ4BpJWHqzPGZadsvy80xQqcqdW6u+in0SmBwqXLlFNkIBJ7LSEp1KVcJQhJJI546dmOz2Ll2l1E5iMas1+oTTKq9SeYSTIkKbQSE3HBtF9CE8LJA4XKiUlTttZXtdqG0XMmRp8iE3TIsCjNokpcdpw1OqmrSgpSkqcJYF76rNkXSCQdFC6X2R406pw5y6VTJDMoJej1KsJYcQrctkkaW1pUjjwWFG45hJukA64FDopojijR4JI6xx6uj6dfmxWbPYNAm5aix+4DKVxGm21rdhJSlwlCV3QSO0LKsSOFwocwcKCn9LnLjdFhPTqPEp8Cql8RKhLq6UR3Drc4XDZUm+hdtSQDp86bxco9MPIELKdKQ5Py6FJaZaUhVd0uIKlBPaTueFtVzx4AHngHtmqlUeFTky2aLT9bKluAGOixKWlkA8OXDFfQciUeoZYojj70ltxuE0tLsYojuJK45bVZTSUqHZcUOfjB5gHCXrfTFyxU0pixcnVGrxOsLZMmkl6a06Q3ZaUllhShbeAXUACUqsTpOOqD0ymGadELeRMwIYLSN3vaFMbARw5rLaGxYEXVwSOfAYDVZOo71L2u7ToKJ0ufEYo1KRHSqWsux0hp8FDjhWCu5GoXuRqJudQs0JkdYkSh1Kdfdxf3cr3Zfw8eQdgeZ9tqtq+bp21XL1WNQzPTXFGJUKB1NpPVkRWULaXGRIJIcfcbOstgpDa9PaNvTk7MG9ec6llRLofbj6AqJOSSA4tVx6zJPAH73jvgL6ZHXup9oU7hMZ/dyvpWfh4VWTMjZekZFoNXm0IznqhV4ZccdUgpCQ+E6UjgQOyDY344sM21atZnyFmeRScouMoZnFtT9Nq6oUthMdpp5TiVux0lsFLZTyudYFrG4ocqVqr0fIGz6lTsnMuPTnkSt4uW7IeQGJbSDvQ1HVclTye0Lj7pAwDOiZByityKFZHjK1B/UOxxssAfReIYo8uZHysvL7zq8nR1LDdQXqum4UJT1j4XPFpFrFQQ5GKslNWb31/UZ/0SwR+4vJjNZQqecUUedDrmzKPBcSJaI2lcx4vIcdccSo6YNkK7dtJPi5+QNbJyDlJDssJyPGTojsqSLp7JK3AT4XmH3sVeXch5TNFbJyVHJEhhAN08EkNXHheO5+/jP7Xcz5yp+Qswz8p5SdjVARI6W5EOlS5shhvfkPusR3YiEPPIZWtaEKNlLSgaV30HN9FqdtMq+zua5nalSZjzNVipiP16nIpMhV0MlxtLbSXN4yklOh4m69SxyQMBudnmVqJT6g7KgZZ6s+JG7DjDgbXotJuAoKBA4Dh5sbiFGXvYnrKeexI/dyvdE/Dxk8ovVd+O1ATleiXjzXFFZqTnb1CRYe1uFtJxfQ49a30MDLNBPZkW+aLnH1RP/VsBiZlHqDyYLDmX5r1R7hTgJi6k83pJ0JRITp1NBWmOyTqsobxAAtqGJO1fKL7+ybOtIMabLkN0ZU1rXKUob5lTr7dkqWrgHG02HHHfDi1SHOloVRqAreUNyaUGoODctLajoBN49rFTDivtlWNHMZqT3XVuZZy+4w5GYUQKk4UqSVucvW3G4vgFN0WKoiVs5bpbNRdlGnPlm6lL1JQhTbCAbEcxGKhw8FSeJ54ZscOd36odTnzuieNfln/Dx5g2dZW2xZDpridmtHVJQxmd6jZhWzHizUR2YzqW1LSl1bLqlAJCtSQ4e0v1Mi1nzCZzVPqz9LjZzy+JgYaUtKiyhxSVF5KU6TH13FnDYjhqwF2pLi8l1JBW4NUeoDmv6od+Hi92ITzVNjGQqkWt2ZeWKU/ovfTriNKtfx2vjH0XJO0mfQty7X6WWZBkNrGlAJSp5ZVx6vccScVuQNnG2GjZFy7R6NtOdYgQKTDixWtENW7ZbYQlCbqgFRskAXJJ85wFY930tdIuozcs3fEeqLMyFqaQJLJosIAa1i6dK9B7JF+IOLTOOxXLuf647mTNmw6PNqL7rb7j/d1TZUtDaW0myHALhCEi9r8BhZZwpHSRyFt/ylLy2KrXabWpDb1eqDdJjSY91J3BQt5IZLA3caOlRQy4UoUtyxIsfVXW84fvHSPxq7/psAnc15Ez2zkiDk7ImW2sq0KDO6/UI6JLMoSmErLy2gXFakKU7pWVhV+Ch9EFJSdKaqTuY9k64TcRbLWY56lrkIG8SepR7hu2qyt2HibG1k8+WPYlVezk9S5jKaDSVKcjuJATVHbklJHD1thF7PstZtodCjxa1kR5Mhpan0LYk1HUlSgE+CIqQg7u4JSTe1jzJxU7Zt3rtiIs0705nh66ao+aPqYqqo9GM/8AjBleaVZl27KYjQXdyYr7SGhqW6RCUGt8AnjZAbVa6jpQeF9INPMfqjGxrYS8ymldXRX6Y4FaQtvk4qPuBpOpakXCQkG5V4xxw0Wapm6PmOsKjbKq0dbccrUKhUEKdALyUrVaOTfSAOJNgALkDESLNq8aep4bI1p0ONojJRLmIWy4kAaQoRQb8UWHMXAHMY83To9dTj9jyxPV1U7vb7UGLV3h6P6xgycosqj5YpUdTJaLUNlG7K9e7sgDTq+ityv47Xxi4lE2pp2/1KuvZshqyIqgsNN0gJb3yZRUrS57WC+Ckvm5kEWWBo5FN/DqWfWYrLTGzxDTaUJCULlVAqSLcieoG5Hj4n7ZwkKdmHpTK6RciO9licvLRJQ/GVSZKKcmGlEhTWiWYO+3oK2DftBTinkWShA0V9nYW0Jm5M24jMTzx8OfHv8Ai4U6S96XBtp0zJeWsxvxKipgqVWnZjocjIUGwt1BKQUo5czY3PE8bY1OVqhS6vnGVPoL9P7mpj7hLLUPdPJfToU4oruNSbKaFtNgRzve8mUjNcyQqVK2XU995bS2FOLcmqUW1DSpBJp/EEcLeS48eJNLfzdTkNw2NnbEOGlalFEVU3s6iSopR1FKSSSTzFySb42ubJ19VH7qc47YwzVpr0x93i69p8NpOzmusRWks2hFDe6b8DiLaQPJwsB5MefDmCHTqFQMqGpMTWEZhff3stDrD5jBqUlTig9wVqUluyQrUErKlCxST6Ml1HOrw3KcgtvtKSnUH1zQCq1yNPUVAgHkTxPOwxAeZzPJCUSdlFKdANxr64oD/wDl2Ouk2dtCxRNNdmZ455x2YbWrF6iMTS8vdboq2st5CczXSXzAarEdyY3V02ZEhyKpKnJKxZRKt6QtPCyQCAq9vS+zVUprY7Rpkkr66iipdWtxA3gdDRJKgrhqB538Yx2mDXjz2QUbycUS/wBHYmonZ7aYMVjZxDS0AQGg/OSgg8xYU/kbm/Dx466zQ6/VURRFmY455x2zM+La7ZvXIiIpVGTarUZtfqUaSl4Nd1S2tC22RufmeyvdOFFzr1E/S+xquONgwCwzb2JH3seV+jVUukyrNucfki5MnyGo0iyBUMuLpTLb+ltPrF1iKtbzJ0PpO8CTu0xV2BdXf0IavtCt+oFn4zP/AEfiHq9g6+bnoW8xiOUx"		
				//	+ "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"		
				//	+ "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"		
				//	+ "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"		
				//	+ "ADuArKNleMmZWd5Iq6QqekoJqUkak9WYFwdfEXBF/MR4sWnezB+rar+NJP5zHHd+V72Kr/tR/wA7jjvgk+9mqf7Uf87gPrvZg/VtV/Gkn85hQVloUja/MpbUWrO9egPSEOqlFaQhqKhCjd1y/FTgFwDyUL8LYb3fBK97NV/2o/53CozrUH522ajNLoc1nVlmo+yuNpHsiDe6FqPi8nk8VzgGLMS/v5Hrap+zQ/25r3UfDxwpL+9c9b1T2+j9ua9yT8PH1Nhvb+R6x/boY9uucfVh5sfK4b28cHUP3cgW6857knzYDPuS5U6jw5T0adqGYN3YOt6SEVQIHDXzskX898KTpjBwbFaHqanJHfNlbi64gp+ftN8QUT/VhqR4zgoMH1nYnMjoBEtfC1XUSLW8gJvjK7es3N7OcgUDNMvKMSsNt1qgxRFlS1KbCn6tAaS7ZSSNTZWFp4cFpSeYGAfmDHGOcAYXvSI/WB2l/wAj61+QvYYWF70iP1gdpf8AI+tfkL2AXOxqXLqXRT2PwcqbQqbQqjHyhl1T5cktdtsUxpK2lagopN1BQsAdSACbEgsvLsl2l5mrNSrG1SFVaVO0qgwXHY7YgHUolIUmylixABUeSRe5uo4XYhTKrW+ibsjhZdzGqjzk5My44qQ28lCtCaezqSboX/5WuBe4uks2kUyqwc1Vmr1PNqJNImtsCDTladMRxIIeVrN1HUq1heyQCLePAJDa+5SKlUI0zvQqmbobr8yEtVDr6WHIhcklClOIFilvdvLcLza94kspSlBBN6avsREN7QYcPIGanXJzD6YM6mZkbRK3nWZCGlQ1PNIaZUlEtak2U8lKEoCkqAUTo9rkRtyqxKh3g5izVFelyohNBqJbXFcMlSCXWwm4b0PKcLqV60lgJCCFKvm6llSJ1jMVIc2ZZ3qFNiPS3Y7bVadSmcXpO6SkPFlLiUJbUVI3LzpbQNQBUNYCRs0zHW9leXqiuDkrNWZqo1IWhEiu5giKdmhaColtxEdpSU3YYbSHGGwm43hbsSp17PNqD2aIE9/ONIg5WlRJq4rcZyrJf3zaQPVUrLbYKSSbFOpPDwr3AQ0WnTKRRESIGy3aG29mdufGebazFIUujBxRTrvuDI0pSUFBLS1epktoeOlTvoDZPPQ9kGlu1Siy6BJUHiqm1KWl6RFG+XpQpdgCALabDSE6QkqSAohpDmjK9v1SUz4436WEX0TH2JOcekI/GebdbXtWmFK0KCkqHcum8iOeH8ZVMt7ajfhE/wCeEH0UFJVnXpCqQoEHavM4g3/6LpuA9D4MGDAV+XvnBTf4Gz/cGLDFfl75wU3+Bs/3BiwwBhD0f9nRmn+aehf4zVsPjCHo/wCzozT/ADT0L/GatgHxgwYMAYMGDAUGeI+vL79QQm7tL9fI4XJCAd4kDyqaLiP/AK/u4gpVGLwKXGFAuCxCWbEbxPHwsaxSUqSUrAKTzB5HGHyy3JYo8CG844tcQCGpQLnFTLqWifC8qDgO1JY3B7TPsQ+hZ9xHwsZahlr5JObrlu2iFa4bt7E7yuq33sbAb0MHi77EPG57iPhYX0moZiy9tMrM9vLVWmU2oJaS69GhvuKu20oJ0EKAPaUQbk8ja1jgNtAqzeV6lKfkkKpVQWl51xGk9UeS202VKSlR9TUNBJAshSVKV2Vko0zGZMuyWkvx69TnWli6VolNqSoeUEHjjADPjmq3ebnA9q/zse8rf2Tzf/cnz2juZtirSpa8hZnUpSSSo0hwknQriTr8/wD9pwDHfq2XZTDkaTU6c6y6gocbW82pK0kWIIJsQR4sZxvLeSWQhDGaZ7aG1qWhCcwv2QSCOF3OAsSAPEPtYzozZD3l/kfZo8O/zmc+nB+n8wH3R57dZzTDLOnvBzMexpv3Gct4KBfw+XC/2rYDUJy/k9BSU5uqQKSoj/nC9zUbk+yeMn+vHAy3kwIS2nNlRCUt7kDvhe4I4cPZPMMZo5shFSlHZ/mcXJNjRnPK4beH8K33D90764SVAfI/zMQDz7jOeVHw/g//AHDAaONQsu0p92bQM7SYkl4pLm+qnWWXSPp23VEXN+Kk6VkWGuwGJZqNYcQGlZ3y2i/BbjcMhVvggyCEm17E6h5jyxjzmqGpspOQMzDskXNHc+kWPp/hH7x+72d9kPfavkfZo8K9u4zl/ZSfp/tD7Z+8GkXQMovzm6pPzZIlzG7FLr1UBShQAGpDYs22eHNCU47FUfKK9yHM0vrSwgtoSqrEp0m3ApKrK8EcweWMonNUPQB3g5mtpAv3Gc+lQL+H5r/aI+1jnvsiXJ+R9mjjxt3Gc+H8Pz2+4cBqzSMqFttkZskpQ03uUBNXIsjh2eCuI7I/4JwN0nKTLSWGc0vNtpaLISmqWARw4cD5ufP75xlk5shBSf8AmBmc2Un/AKGc49pv4fm/+4Y6u+qGWbd4GZhdFr9xnLeAsX8P4X/2nzXDPbRYMCHl2c1TS3KjorTKUSXJRedX6gzcKUQSrjwuVE8MNCdE9Wm3o9L8GPf1bx7xf2PnhJ51rU6ruyqTTcrymI6qiZb5fp623miEN2SribC1yQfERx44c81ynF6baTRD2Y4HYTx9UX8LngO1yJ6u/wDMal+32v274DX2PHCInqkb5jUv2/I/bvgvcPY+WOtxdP37/rmie32voE8tDXLtcscIXT9cb1zRPb7/ANAn6V7n2uWA7oUT1eMO49L8GXze4ezJ+x4jpifM5B7j0z50XvvuPgjj7HzxAZrcVxal0Snw6uYSJheVHjgMNkPci6pQQSC2sFKdSkkcU4O9/MD7G4rCKYx1dlhhbFPdUhKkKUEkKdU1vD4PhI3ZF/HgJVWl0+BIkRn6dS+sPNR1NR2nFOPOAOK4obS0Vq85AOI+muVCQ4hrKUWnx5E9IbecU0+6d2gagGwpKUjUyQCVnnxT4sTIOXotKMhim0GBGQ5UW1uBqW4neLLaDqUQi6lcfCNziU1DklyL6xYuahI/d7vE2f8AgcMBU0/K1MVKhy51HqM6QuO69rffa0hzW2QtDaVpbQoFR7SUg8eeI+YIu9pEdowqqgKZhDUmcU24Ocbh0EYvIUOSTAHUWCDDd4dfd49pr4GIFQopqVGeZkwk6UUxh1Km6m+2pKkpcIIKUg/cv5vHgMHWJapTNblR8h5lpc5L9FeL0uau7jipTSUglbyUrKASLhd0gc0m2OuXFYqFAqN8jy5T4ejNRpUuqRlRy71dhxgvJ66LDeqbcSlJNgpFiDbG7qmSostUtmUzNdb3sNZQrMU4i6XgpNxfjxAPHyY6+8KmjrUdNPeS3ImIbeQK5LAdTuEp0rH0Q08OPi4csBR7MKp3ckz5ganSQmquthbNUQ8nT65KEhSZLguEFAPa5gm1iCdrCaVeAOoVQ3hu8pvPtNcR6riuy5k2nZaKE0WiR44l1J9x61ReOtQDwB4p8g5+Px4sIUSSTTwILHGE6bded+ma+BgOlbau5qz1Cp/OkG/XeHgnj7LyxKltK30r1hVB241vXvL1T/vcRXIknuYs9RY+dAN+vO8tJ+BiVMiSd9LvBY8OLf1877p/2MBxulGS6O59U9vt/u37Cjh7Lhabimst0NuflzOk5aKg/KZejzSpplxS5Y7Hq3ZUnT/9yePHDLMSUZLvrFjhPb/dzvuCPgYqIuT1sCK1GelstdflaW0VZ2ybl82F2ybcTgFrQsuRF1JuE5lupymWp85htLE9pp5LDT12gLSBrWFs6gCEjdovxIJPERb9PrkSjIhy4cQpLrURNXRvSNyA2pbQkkXu3KBsm3Zbt4K7b6Bs7gtSYjrbDm8tKkBw1FZWHC6CV6i1cH1Rdj4gogWHDEcbNqMiSMxOUpt6e1TUvJkOTl69QaWkEqDYUeBIte2AsqwpiPmSYuZTqqWVQYyFJDynSQZFraUuKJB5crYqoEGgQ65OmO0aulsVCOYqT15aUITGRa6FuFCVJUV6ShKbAgDlxt6pTHJWZp0V6C3oep8dtYRUXkq0qkWNlBIIPHmCCOYxDibM8vwam1Mj0ZwOw6mHGiuvzlgLU0yolQUSFcW0eFe1iORNw0MZG8ENaafVdKpsgj13bnvvEXbg"		
				//	+ "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"		
				//	+ "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"		
				//	+ "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"		
				//	+ "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"		
				//	+ "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"		
				//	+ "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"		
				//	+ "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"		
				//	+ "d+WUPfVR/jzXpYV/ylvRJ+tx2e/iGP6OD5S3ok/W47PfxDH9HANDvyyh76qP8ea9LB35ZQ99VH+PNelhX/KW9En63HZ7+IY/o4PlLeiT9bjs9/EMf0cA0O/LKHvqo/x5r0sHfllD31Uf4816WFf8pb0Sfrcdnv4hj+jg+Ut6JP1uOz38Qx/RwDQ78soe+qj/AB5r0sHfllD31Uf4816WFf8AKW9En63HZ7+IY/o4PlLeiT9bjs9/EMf0cA0O/LKHvqo/x5r0sHfllD31Uf4816WFf8pb0Sfrcdnv4hj+jg+Ut6JP1uOz38Qx/RwDQ78soe+qj/HmvSwd+WUPfVR/jzXpYV/ylvRJ+tx2e/iGP6OD5S3ok/W47PfxDH9HANDvyyh76qP8ea9LGA6QWbMrSdg20hiPmWlOuuZRrKUIRMbUpRMJ6wAB4nFd8pb0Sfrcdnv4hj+jg+Ut6JP1uOz38Qx/RwH10X81ZWjdGrZPGlZkpbLrWRqAhbbkxtKkKFPYBBBNwcM3vxyf76qP8ea9LCw+Us6JP1uOz38Qx/RwfKW9En63HZ7+IY/o4Bn9+OUB/wDFVH+PNeligzNtNgUmTCbo0qk1Fl+5kOJqCCWrLbFtI56kqXYkixAJ7IURj/lLeiT9bjs9/EMf0cHylvRJ+tx2e/iGP6OAZ4zllC36qqP8ea9LHPfllD31Uf4816WFf8pb0Sfrcdnv4hj+jg+Ut6JP1uOz38Qx/RwDPOcsoW/VVR/jzXpYR3RKkx5ecOkHIiSG3mnNq0wocbWFJUO5dN4gjgcaL5S3ok/W47PfxDH9HDB2f7MNnWyiju5e2Z5IouV6a/IVLdi0qE3GbcfUlKVOKSgC6ilCBc8bJSOQGA0+DBgwFfl75wU3+Bs/3Biwwo6HtMryKJTkCJAsIjI4tr+kHw8Tfkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngGfgwsPkn1/6kp/4Nfp4Pkn1/wCpKf8Ag1+ngP/Z"		
				//	;
				//		String scriptCode = 
				//				"var points = [];"
				//				+ "var img = new Image();"
				////				+ "img.addEventListener('load', function() {"
				////				+ "}, false);"
				//				+ "img.src = '"+dataUrl+"';" 
				//				+ "" + 
				//				"var handleEvent = function( event ) {" + 
				//				"  var widget = event.widget;"
				//				+ "switch( event.type ) {" + 
				//				"    case SWT.MouseMove:"
				//				+ "		console.log('x:'+event.x + ' y:'+event.y); "
				//				+ " widget.setData('x', event.x );"
				//				+ " widget.setData('x', event.x );"
				////				+ " widget.redraw();" +
				////				+ "    points.push( [ event.x, event.y ] );" 
				//				+ "      widget.redraw();"
				//				+ "	break;" + 
				//				"    case SWT.Paint:"
				////				+ "		console.log(widget);"
				////				+ "		console.log(widget.getData('sensor'));"
				////				+ "		console.log(widget.getData('image'));"
				////				"      if( points.length > 1 ) {" + 
				////				"        event.gc.lineWidth = 4;" + 
				////				"        event.gc.beginPath();" + 
				////				"        event.gc.moveTo( points[ 0 ][ 0 ], points[ 0 ][ 1 ] );" + 
				////				"        for( var i = 1; i < points.length; i++ ) {" + 
				////				"          event.gc.lineTo( points[ i ][ 0 ] , points[ i ][ 1 ] );" + 
				////				"        }" + 
				////				"        event.gc.stroke();"+
				////				"      }" 
				////				+ "    event.gc.setBackground(SWT.COLOR_CYAN);"
				//				+ "		event.gc.aetAlpha(0);"
				//				+ "		event.gc.drawImage(img,0,0);"
				////				+ "var img = new Image();" + 
				////				"  img.onload = function() {" + 
				////				"    ctx.drawImage(img, 0, 0);" + 
				////				"    ctx.beginPath();" + 
				////				"    ctx.moveTo(30, 96);" + 
				////				"    ctx.lineTo(70, 66);" + 
				////				"    ctx.lineTo(103, 76);" + 
				////				"    ctx.lineTo(170, 15);" + 
				////				"    ctx.stroke();" + 
				////				"  };" + 
				////				"  img.src = 'https://mdn.mozillademos.org/files/5395/backdrop.png';"
				////				+ "		event.gc.beginPath();"
				////				+ "		event.gc.fillStyle = '#9ea7b8';"
				////				+ "		event.gc.opacity = 0.2;" + 
				////				"		event.gc.arc(widget.getData('x'), widget.getData('y'), 20, 0, 2 * Math.PI);"
				////				+ "		event.gc.fill();" 
				////				+ "		event.gc.stroke();"
				//				+ "    break;"
				//				+ "    case SWT.MouseDown:"
				////				+ "		document.body.style.cursor = \"url('http://bringerp.free.fr/Files/RotMG/cursor.gif'), auto\";"
				//				+ "		document.body.style.cursor = \"url('"+dataUrl+"'), auto\";"
				//				+ "		console.log('yyyyyyyyyyyy');"
				//				+ "    break;"
				//				+ "    case SWT.MouseUp:"
				//				+ "		document.body.style.cursor = \"auto\";"
				//				+ "		console.log('yyyyyyyyyyyy');"
				//				+ "    break;"
				//				+ "  }" + 
				//				"};";
				//		ClientListener listener = new ClientListener( scriptCode );
				
						
					    
					    TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
					    tbtmNewItem.setText("\uC704\uCE58\uC124\uC815");
					    
					    Composite composite_3 = new Composite(tabFolder, SWT.NONE);
					    tbtmNewItem.setControl(composite_3);
					    composite_3.setLayout(new GridLayout(1, false));
					    
					    ScrolledComposite scrolledComposite_1 = new ScrolledComposite(composite_3, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
					    
					    	    scrolledComposite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
					    	    scrolledComposite_1.setExpandVertical(true);
					    	    scrolledComposite_1.setExpandHorizontal(true);
					    	    scrolledComposite_1.setBounds(0, 0, 440, 270);
					    	    
					    	    		child = new Composite(scrolledComposite_1, SWT.NONE);
					    	    		child.setBackgroundMode(SWT.INHERIT_FORCE);
					    	    		child.addControlListener(new ControlAdapter() {
					    	    			@Override
					    	    			public void controlResized(ControlEvent e) {
					    	    				//ImageData imgData = image.getImageData();
					    	    				
//				float scaleTo = child.getClientArea().height * 1.0f / child.getClientArea().width;
//				
//				Image tmpImage = null;
//				if(scale > scaleTo) {	// ºº∑Œê¨√„. ∞°∑Œ∞° ¡ŸæÓµÎ.
//					//event.gc.drawImage(image, 0,0,image.getBounds().width,image.getBounds().height,0,0,(int)(event.height/scale),event.height);
//					ImageData tmpImageData = imageData.scaledTo((int)(child.getClientArea().height/scale),child.getClientArea().height);
//					tmpImage = new Image(display, tmpImageData);
//					
//				}else {					// ∞°∑Œê¨√„. ºº∑Œ∞° ¡ŸæÓµÎ.
//					//event.gc.drawImage(image, 0,0,image.getBounds().width,image.getBounds().height,0,0,event.width,(int) (event.width*scale));
//					ImageData tmpImageData = imageData.scaledTo(child.getClientArea().width,(int) (child.getClientArea().width*scale));
//					tmpImage = new Image(display, tmpImageData);
//				}
//
//				child.setBackgroundImage(tmpImage);
					    	    			}
					    	    		});
					    	    		//child.setLayout(null);
					    	    		//child.setBackgroundImage(image);
	    scrolledComposite_1.addControlListener(new ControlAdapter() {
	    	@Override
	    	public void controlResized(ControlEvent e) {
	    		//int width = scrolledComposite_1.getClientArea().width;
	    		scrolledComposite_1.setMinSize( child.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
	    		//scrolledComposite_1.setMinSize(scrolledComposite_1.getClientArea().width,scrolledComposite_1.getClientArea().height);
	    	}
	    });
	    scrolledComposite_1.setContent(child);
	    
	    sensorWidget = new SensorWidget(child, SWT.NONE);
	    sensorWidget.setLocation(247, 174);
	    sensorWidget.setBackgroundImage(image2);
	    
		ImageData imageData = image.getImageData();
		float scale = imageData.height* 1.0f / imageData.width;

	    Label lblFloorImg = new Label(child, SWT.NONE);
	    lblFloorImg.setBounds(0, 0, imageData.width, imageData.height);
	    lblFloorImg.setSize(imageData.width, imageData.height);
	    lblFloorImg.setBackgroundImage(image);
	    
	    DropTarget dropTarget_1 = new DropTarget(child, DND.DROP_MOVE);
	    dropTarget_1.addDropListener(new DropTargetAdapter() {
	    	@Override
	    	public void drop(DropTargetEvent event) {
	    		int x = (int) (child.toControl(event.x, event.y).x / levelScale);
	    		int y = (int) (child.toControl(event.x, event.y).y / levelScale);
	    		
	    		ap.setX(x);
	    		ap.setY(y);
	    		sensorWidget.setLocation( (int) (x * levelScale) -16, (int) (y * levelScale) - 16);
	    		
	    		em.getTransaction().begin();
	    		em.merge(ap);
	    		em.getTransaction().commit();

	    	}
	    });
	    dropTarget_1.setTransfer(new Transfer[] { LocalSelectionTransfer.getTransfer()});

		int dndOperations = DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK;
		Transfer[] transfers  = new Transfer[] { LocalSelectionTransfer.getTransfer() };

		treeViewer.setContentProvider(new TreeContentProvider());
		treeViewer.setInput("root");
		treeViewer.addDragSupport ( dndOperations, transfers, new DragSourceListener() {
			
			@Override
			public void dragStart(DragSourceEvent event) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void dragSetData(DragSourceEvent event) {
				LocalSelectionTransfer transfer=LocalSelectionTransfer.getTransfer();
				if (transfer.isSupportedType(event.dataType)) {
					Object object = event.getSource();
					if(object instanceof String) {
						
					}else {
						transfer.setSelection(treeViewer.getSelection());
					}
				}
			}
			
			@Override
			public void dragFinished(DragSourceEvent event) {
				// TODO Auto-generated method stub
				
			}
		} );
		refreshConfig();

		

		ImageData xx = image2.getImageData();
		xx.transparentPixel = xx.getPixel(0, 0);
		image2 = new Image(null, xx);
		
		
		
		Display display = Display.getCurrent();
		//canvas.setBackgroundImage(image);
		String xxx = "data:image/png;charset=utf-8;base64," + Base64.getEncoder().encodeToString( imageData.data);
		
		//String xxx = "data:image/gif;base64,R0lGODlhCwALAIAAAAAA3pn/ZiH5BAEAAAEALAAAAAALAAsAAAIUhA+hkcuO4lmNVindo7qyrIXiGBYAOw==";
		WidgetUtil.registerDataKeys("image");
		WidgetUtil.registerDataKeys("sensor");
//	    composite_2.setLayout(new GridLayout(1, false));
//
//	    
//	    LineChart  lineChart = new LineChart( composite_2, SWT.NONE );
//	    lineChart.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
//	    lineChart.setXAxisLabel( "Sample#" );
//	    lineChart.setYAxisLabel( "g" );
//	    lineChart.setYAxisFormat( "d" );
//
//	    DataGroup[] dataGroups = new DataGroup[]{new DataGroup(dataItems, "FFT", CATEGORY_10[ 0 ])};
//	    lineChart.setItems( dataGroups);
	    
	    
//	    final DefaultPieDataset result = new DefaultPieDataset();
//	    result.setValue("Linux", 29);
//	    result.setValue("Mac", 20);
//	    result.setValue("Windows", 51);
//	    final PieDataset dataset = result;
//	    
//	    final JFreeChart chart = ChartFactory.createPieChart3D("Operating Systems", dataset, true, true, false);
//	    final PiePlot3D plot = (PiePlot3D) chart.getPlot();
//	    plot.setStartAngle(290);
//	    plot.setDirection(Rotation.CLOCKWISE);
//	    plot.setForegroundAlpha(0.5f);

	    
//	    //lineChart.setRedraw(false);
//	    new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				while(true) {
//					
////					list.remove(0);
////					list.add(new DataItem( (5 * Math.random()) , System.currentTimeMillis() + "", CATEGORY_10[ 0 ]));
////
////					DataItem[] dataItems = list.toArray(new DataItem[256]);
//					
//					for (int i = 0; i < dataItems.length-1; i++) {
//						dataItems[i] = dataItems[i+1];
//					}
//					//dataItems[dataItems.length-1] = new DataItem( (5 * Math.random()) , System.currentTimeMillis() + "", CATEGORY_10[ 0 ]);
//					dataItems[dataItems.length-1] = new DataItem( (3 ) , System.currentTimeMillis() + "", CATEGORY_10[ 0 ]);
//							
//					//lineChart.setItems( dataGroups);
//					sync.asyncExec(()->{
//						//barChart.redraw();
//						barChart.setRedraw(false);
//					    barChart.setItems(dataItems  );
//						barChart.setRedraw(true);
//
////					    DataGroup[] dataGroups = new DataGroup[]{new DataGroup(dataItems, "FFT", CATEGORY_10[ 0 ])};
////					    lineChart.setItems( dataGroups);
//					});
//					try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
//		}).start();

		
		//	    lineChart.setItems( new DataGroup[] {
//	    	      new DataGroup( createRandomPoints(), "Series 1", CATEGORY_10[ 0 ] ),
//	    	      new DataGroup( createRandomPoints(), "Series 2", CATEGORY_10[ 1 ] )
//	    	    });
	}

	@PreDestroy
	public void preDestroy() {
		//System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
//		try {
//			if(mqttClient != null) {
//				mqttClient.unsubscribe("k/"+sensor.getAddress()+"/b");
//				mqttClient.unsubscribe("k/"+sensor.getAddress()+"/v");
//				mqttClient.disconnect();
//			}
//		} catch (MqttException e) {
//			e.printStackTrace();
//		}
	}

	@Focus
	public void onFocus() {
		
	}

	@Persist
	public void save() {
		
	}
	
	public void init(Ap ap) {
		this.ap = ap;
		text.setText(ap.getRemark());
		text_1.setText(ap.getApid()+"");
		text_2.setText(ap.getMac());

		sensorWidget.setLocation(ap.getX()-16, ap.getY()-16);
		

	}
	@SuppressWarnings("serial")
	private static class TreeContentProvider implements ITreeContentProvider {
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
		public void dispose() {
		}
		public Object[] getElements(Object inputElement) {
			//return getChildren(inputElement);
			//List<String> list = (List<String>) inputElement;
	        //return new Object[] { "Sensor", "Topic", "Transform" };
	        return new Object[] { "Slave Mote"};
		}
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof String) {
				if(parentElement.toString().equals("Slave Mote")) {
					return apList.toArray();
				}
            }else {
            	return new Object[] { };
            }

			return new Object[] { "item_0", "item_1", "item_2" };
		}
		public Object getParent(Object element) {
			return null;
		}
		public boolean hasChildren(Object element) {
			if (element instanceof Topic) {
				return false;
			}else
			if (element instanceof Transform) {
				return false;
			}else
			if (element instanceof Sensor) {
				return false;
			}
			return getChildren(element).length > 0;
		}
	}
	@SuppressWarnings("unchecked")
	public void refreshSensorList() {
        Query q = em.createQuery("select t from Ap t order by t.apid");
		apList = q.getResultList();
//		for (Ap ap : apList) {
//			if(ap.getAct() != 2) {
//				Collection<MPart> parts = partService.getParts();
//				for (MPart part : parts) {
//					if(part.getLabel().equals("Sensor["+sensor.getAddress()+"] ")) {
//						partService.hidePart(part, true);
//						break;
//					}
//				}
//
//			}
//		}
        //treeViewer.setInput("root");
	}
	public void refreshConfig() {
		em.clear();
        refreshSensorList();
        treeViewer.refresh();
	}
	
	public void addEntity() {
		
		IStructuredSelection structuredSelection = (IStructuredSelection) treeViewer.getSelection();
		Object object = structuredSelection.getFirstElement();
		if(object instanceof String) {
			String name = object.toString();
			if(name.equals("Topic")) {
				Topic topicNew = new Topic();
				topicNew.setName("New Topic");
				em.getTransaction().begin();
				em.persist(topicNew);
				em.getTransaction().commit();
				
				//topicList.add(topicNew);
				//treeViewer.refresh();
				refreshConfig();
				
				int idNew = topicNew.getId();
				
				
				TreeItem[] treeItems = treeViewer.getTree().getItems();
				for (TreeItem item : treeItems) {
                    
                    if(item.getText().equals("Topic")) {
                    	TreeItem[] subItems = item.getItems();
                    	for (TreeItem treeItem : subItems) {
                    		Topic topic = (Topic)treeItem.getData();
                    		if(topic.getId() == idNew) {
                    			treeViewer.setSelection(new StructuredSelection(topic), true);
                    			
                    			
								MPart part = partService.createPart("kairos.kongde.partdescriptor.topicpart");
								part.setLabel("Topic["+topic.getId()+"] "+ topic.getName() );
								partService.showPart(part, PartState.ACTIVATE);
								TopicPart topicPart = (TopicPart)part.getObject();
								topicPart.init(topic);

                    			break;
                    		}
						}
                    }
				}
				
			}else if(name.equals("Transform")) {
				Transform transformNew = new Transform();
				transformNew.setName("New Transform");
				transformNew.setFromTopic("From ?");
				transformNew.setToTopic("To ?");
				transformNew.setActive(false);
				
				em.getTransaction().begin();
				em.persist(transformNew);
				em.getTransaction().commit();
				
				refreshConfig();
				
				int idNew = transformNew.getId();
				
				
				TreeItem[] treeItems = treeViewer.getTree().getItems();
				for (TreeItem item : treeItems) {
                    
                    if(item.getText().equals("Transform")) {
                    	TreeItem[] subItems = item.getItems();
                    	for (TreeItem treeItem : subItems) {
                    		Transform transform = (Transform)treeItem.getData();
                    		if(transform.getId() == idNew) {
                    			treeViewer.setSelection(new StructuredSelection(transform), true);
                    			
                    			
								MPart part = partService.createPart("kairos.kongde.partdescriptor.transformpart");
								part.setLabel("Transform["+transform.getId()+"] "+ transform.getName() );
								partService.showPart(part, PartState.ACTIVATE);
								TransformPart transformPart = (TransformPart)part.getObject();
								transformPart.init(transform);

								e4Application.transformJobManager.add(transform);
                    			break;
                    		}
						}
                    }
				}
				
			}else if(name.equals("Slave Mote")) {
				Ap apNew = new Ap();
				apNew.setRemark("New Slave Mote");
				apNew.setMac("New Slave Mote");
				apNew.setX(20);
				apNew.setY(20);
				
				em.getTransaction().begin();
				em.persist(apNew);
				em.getTransaction().commit();
				
				refreshConfig();
				
				int idNew = apNew.getId();
				
				
				TreeItem[] treeItems = treeViewer.getTree().getItems();
				for (TreeItem item : treeItems) {
                    
                    if(item.getText().equals("Slave Mote")) {
                    	TreeItem[] subItems = item.getItems();
                    	for (TreeItem treeItem : subItems) {
                    		Ap ap = (Ap)treeItem.getData();
                    		if(ap.getId() == idNew) {
                    			treeViewer.setSelection(new StructuredSelection(ap), true);
                    			
                    			
//								MPart part = partService.createPart("kairos.kongde.partdescriptor.sensorpart");
//								part.setLabel("AP"+ap.getId()+"] " );
//								partService.showPart(part, PartState.ACTIVATE);
//								SensorPart sensorPart = (SensorPart)part.getObject();
//								sensorPart.init(ap);
                    			init(ap);
                    			break;
                    		}
						}
                    }
				}
				
			}		
			
		}else { // ø£∆º∆º º±≈√¿« ∞ÊøÏ
		}

	}
	public void deleteEntity() {
		IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
		if (selection.size() > 0) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			Object object = structuredSelection.getFirstElement();
			
			if(object instanceof Topic) {
				
				Topic topic = (Topic) structuredSelection.getFirstElement();

				//MPart part = partService.findPart("kairos.kongde.partdescriptor.topicpart");
				//partService.hidePart(part, true);
				
				Collection<MPart> parts = partService.getParts();
				for (MPart mPart : parts) {
					if(mPart.getLabel().equals("Topic["+topic.getId()+"] "+ topic.getName())) {
						partService.hidePart(mPart, true);
					}
				}
				
				Query q = em.createQuery("DELETE FROM TopicDetail t where t.topicId = :topicId ");
		        q.setParameter("topicId", topic.getId());

				em.getTransaction().begin();
				q.executeUpdate();
				em.remove(topic);
				em.getTransaction().commit();
				
			}else if(object instanceof Transform) {
				
				Transform transform = (Transform) structuredSelection.getFirstElement();
				
				e4Application.transformJobManager.remove(transform);

				Collection<MPart> parts = partService.getParts();
				for (MPart mPart : parts) {
					if(mPart.getLabel().equals("Transform["+transform.getId()+"] "+ transform.getName())) {
						partService.hidePart(mPart, true);
					}
				}
				
				Query q = em.createQuery("DELETE FROM TransformDetail t where t.transformId = :transformId ");
		        q.setParameter("transformId", transform.getId());

				em.getTransaction().begin();
				q.executeUpdate();
				em.remove(transform);
				em.getTransaction().commit();
				
			}else if(object instanceof Ap) {
				
				Ap ap = (Ap) structuredSelection.getFirstElement();
				
//				Collection<MPart> parts = partService.getParts();
//				for (MPart mPart : parts) {
//					if(mPart.getLabel().equals("AP["+sensor.getAddress()+"] ")) {
//						partService.hidePart(mPart, true);
//					}
//				}
//				
//				Query q = em.createQuery("DELETE FROM Sensor t where t.sensord = :sensorId ");
//		        q.setParameter("transformId", transform.getId());

				em.getTransaction().begin();
				//q.executeUpdate();
				em.remove(ap);
				em.getTransaction().commit();
				
			}
			
			refreshConfig();
			
		}else {
			MessageDialog.openWarning(treeViewer.getControl().getShell(), "Warning", "Please select a model first.");
		}

		
	}

}