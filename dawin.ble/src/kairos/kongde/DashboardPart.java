
package kairos.kongde;

import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.wb.swt.SWTResourceManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import kairos.kongde.entity.Ap;
import kairos.kongde.entity.Tags;

public class DashboardPart {
	private static class ContentProvider_1 implements IStructuredContentProvider {
		public Object[] getElements(Object inputElement) {
			return apList.toArray();
		}
		public void dispose() {
		}
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}
	private static class ContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object inputElement) {
			//return new Object[0];
			return tagList.toArray();
		}
		public void dispose() {
		}
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}
	@Inject UISynchronize sync;
	@Inject
	private EPartService partService;
	@Inject
	private EModelService modelService;
	@Inject
	private MApplication app;
	
	static List<Tags> tagList = new ArrayList<Tags>();
	List<Tags> selectedTagList = new ArrayList<Tags>();
	Point selectedPoint = new Point(0, 0);
	static List<Ap> apList = new ArrayList<Ap>();
	
	
	HashMap<Integer, Integer> tagCount = new HashMap();

    EntityManagerFactory emf = Persistence.createEntityManagerFactory("kairos.kongde");
    EntityManager em = emf.createEntityManager();
    
	Bundle bundle = FrameworkUtil.getBundle(this.getClass());
    // use the org.eclipse.core.runtime.Path as import
    URL url2 = FileLocator.find(bundle, new Path("icons/titleicon_dashboard.png"), null);
    ImageDescriptor titleicon_dashboard = ImageDescriptor.createFromURL(url2);

    URL url3 = FileLocator.find(bundle, new Path("icons/box_dashboard.png"), null);
    ImageDescriptor box_dashboard = ImageDescriptor.createFromURL(url3);
    
    URL url4 = FileLocator.find(bundle, new Path("icons/icon_mote.png"), null);
    ImageDescriptor icon_mote = ImageDescriptor.createFromURL(url4);
   
    URL url5 = FileLocator.find(bundle, new Path("icons/icon_sensor.png"), null);
    ImageDescriptor icon_sensor = ImageDescriptor.createFromURL(url5);

    URL url6 = FileLocator.find(bundle, new Path("icons/icon_Alert.png"), null);
    ImageDescriptor icon_Alert = ImageDescriptor.createFromURL(url6);
    
    URL url7 = FileLocator.find(bundle, new Path("icons/titleicon_Network.png"), null);
    ImageDescriptor titleicon_Network = ImageDescriptor.createFromURL(url7);
    
    URL url8 = FileLocator.find(bundle, new Path("icons/slice_page1.png"), null);
    ImageDescriptor slice_page1 = ImageDescriptor.createFromURL(url8);
    
    URL url9 = FileLocator.find(bundle, new Path("icons/categoryicon_1.png"), null);
    ImageDescriptor categoryicon_1 = ImageDescriptor.createFromURL(url9);

    URL url10 = FileLocator.find(bundle, new Path("icons/categoryicon_2.png"), null);
    ImageDescriptor categoryicon_2 = ImageDescriptor.createFromURL(url10);

    URL url11 = FileLocator.find(bundle, new Path("icons/categoryicon_3.png"), null);
    ImageDescriptor categoryicon_3 = ImageDescriptor.createFromURL(url11);

    Label lblApActive;
    Label lblApInactive;
    Label lblTagActive;
    Label lblTagInactive;
    Label lblAlertActive;
    Label lblAlertInactive;
    Label lblDate, lblTime;
    
    private Table table;
    TableViewer tableViewer;
    TableViewer tableViewer_1;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    DateFormat dateFmt1 = new SimpleDateFormat("yyyy-MM-dd");
    DateFormat dateFmt2 = new SimpleDateFormat("HH:mm:ss");
    
	@PostConstruct
	public void postConstruct(Composite parent) {
		
		ResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources());
		Bundle bundle = FrameworkUtil.getBundle(this.getClass());
//        URL url = FileLocator.find(bundle, new Path("icons/floor.jpg"), null);
        URL url = FileLocator.find(bundle, new Path("icons/map.png"), null);
        ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(url);
		Image image = resourceManager.createImage(imageDescriptor);
		GridLayout gl_parent = new GridLayout(2, false);
		gl_parent.marginTop = 20;
		gl_parent.horizontalSpacing = 0;
		gl_parent.marginWidth = 0;
		gl_parent.marginHeight = 0;
		gl_parent.verticalSpacing = 0;
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
		GridData gd_composite_5 = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
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
		lblNewLabel2.setFont(new Font(null, "¸¼Àº °íµñ", 18, SWT.NORMAL));
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
		lblNewLabel3.setFont(new Font(null, "¸¼Àº °íµñ", 18, SWT.NORMAL));
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
		lblNewLabel5.setFont(new Font(null, "¸¼Àº °íµñ", 18, SWT.NORMAL));
		lblNewLabel5.setText("Mote Status");
		
		Composite composite_10 = new Composite(composite_5, SWT.NONE);
		GridLayout gl_composite_10 = new GridLayout(1, false);
		gl_composite_10.marginLeft = 40;
		composite_10.setLayout(gl_composite_10);
		GridData gd_composite_10 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite_10.heightHint = 45;
		composite_10.setLayoutData(gd_composite_10);
		
		Label lblNewLabel6 = new Label(composite_10, SWT.NONE);
		lblNewLabel6.setFont(new Font(null, "¸¼Àº °íµñ", 18, SWT.NORMAL));
		lblNewLabel6.setText("Sensor Status");
		
		Composite composite_11 = new Composite(composite_5, SWT.NONE);
		GridLayout gl_composite_11 = new GridLayout(1, false);
		gl_composite_11.marginLeft = 40;
		composite_11.setLayout(gl_composite_11);
		GridData gd_composite_11 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite_11.heightHint = 45;
		composite_11.setLayoutData(gd_composite_11);
		
		Label lblNewLabel7 = new Label(composite_11, SWT.NONE);
		lblNewLabel7.setFont(new Font(null, "¸¼Àº °íµñ", 18, SWT.NORMAL));
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
		lblNewLabel9.setFont(new Font(null, "¸¼Àº °íµñ", 18, SWT.NORMAL));
		lblNewLabel9.setText("Configuration");
		
		Composite composite_17 = new Composite(composite_5, SWT.NONE);
		GridLayout gl_composite_17 = new GridLayout(1, false);
		gl_composite_17.marginLeft = 40;
		composite_17.setLayout(gl_composite_17);
		GridData gd_composite_17 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite_17.heightHint = 45;
		composite_17.setLayoutData(gd_composite_17);
		
		Label lblNewLabel10 = new Label(composite_17, SWT.NONE);
		lblNewLabel10.setFont(new Font(null, "¸¼Àº °íµñ", 18, SWT.NORMAL));
		lblNewLabel10.setText("Setup Gateway");
		
		Composite composite_16 = new Composite(composite_5, SWT.NONE);
		GridLayout gl_composite_16 = new GridLayout(1, false);
		gl_composite_16.marginLeft = 40;
		composite_16.setLayout(gl_composite_16);
		GridData gd_composite_16 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite_16.heightHint = 45;
		composite_16.setLayoutData(gd_composite_16);
		
		Label lblNewLabel11 = new Label(composite_16, SWT.NONE);
		lblNewLabel11.setCursor(SWTResourceManager.getCursor(SWT.CURSOR_HAND));
		lblNewLabel11.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
		        MPerspective perspective = (MPerspective) modelService.find("dawin.ble.perspective.moteconfig", app);
		        if (perspective != null) {
		            partService.switchPerspective(perspective);
		        }
			}
		});
		lblNewLabel11.setFont(new Font(null, "¸¼Àº °íµñ", 18, SWT.NORMAL));
		lblNewLabel11.setText("Register Mote");
		
		Composite composite_18 = new Composite(composite_5, SWT.NONE);
		GridLayout gl_composite_18 = new GridLayout(1, false);
		gl_composite_18.marginLeft = 40;
		composite_18.setLayout(gl_composite_18);
		GridData gd_composite_18 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite_18.heightHint = 45;
		composite_18.setLayoutData(gd_composite_18);
		
		Label lblNewLabel12 = new Label(composite_18, SWT.NONE);
		lblNewLabel12.setFont(new Font(null, "¸¼Àº °íµñ", 18, SWT.NORMAL));
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
		lblNewLabel13.setCursor(SWTResourceManager.getCursor(SWT.CURSOR_HAND));
		lblNewLabel13.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
		        MPerspective perspective = (MPerspective) modelService.find("kairos.kongde.perspective.sensor", app);
		        if (perspective != null) {
		            partService.switchPerspective(perspective);
		        }
			}
		});
		lblNewLabel13.setFont(new Font(null, "¸¼Àº °íµñ", 18, SWT.NORMAL));
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
		lblNewLabel14.setCursor(SWTResourceManager.getCursor(SWT.CURSOR_HAND));
		lblNewLabel14.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
		        MPerspective perspective = (MPerspective) modelService.find("kairos.kongde.perspective.sensor", app);
		        if (perspective != null) {
		            partService.switchPerspective(perspective);
		        }
			}
		});
		lblNewLabel14.setFont(new Font(null, "¸¼Àº °íµñ", 18, SWT.NORMAL));
		lblNewLabel14.setText("LBS Map");
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		GridLayout gl_composite = new GridLayout(1, false);
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		gl_composite.horizontalSpacing = 0;
		gl_composite.verticalSpacing = 0;
		composite.setLayout(gl_composite);
		composite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Composite composite_15 = new Composite(composite, SWT.NONE);
		GridData gd_composite_15 = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_composite_15.heightHint = 528;
		composite_15.setLayoutData(gd_composite_15);
		composite_15.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		composite_15.moveAbove(lblApActive);
		
		lblApActive = new Label(composite_15, SWT.NONE);
		lblApActive.setAlignment(SWT.RIGHT);
		lblApActive.setFont(SWTResourceManager.getFont("±¼¸²", 38, SWT.BOLD));
		lblApActive.setBounds(300, 200, 50, 61);
		lblApActive.setText("99");
		lblApActive.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		lblApInactive = new Label(composite_15, SWT.NONE);
		lblApInactive.setAlignment(SWT.RIGHT);
		lblApInactive.setFont(SWTResourceManager.getFont("±¼¸²", 38, SWT.BOLD));
		lblApInactive.setBounds(300, 260, 50, 61);
		lblApInactive.setText("99");
		lblApInactive.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		lblTagActive = new Label(composite_15, SWT.NONE);
		lblTagActive.setAlignment(SWT.RIGHT);
		lblTagActive.setFont(SWTResourceManager.getFont("±¼¸²", 38, SWT.BOLD));
		lblTagActive.setBounds(800, 200, 50, 61);
		lblTagActive.setText("99");
		lblTagActive.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		lblTagInactive = new Label(composite_15, SWT.NONE);
		lblTagInactive.setAlignment(SWT.RIGHT);
		lblTagInactive.setFont(SWTResourceManager.getFont("±¼¸²", 38, SWT.BOLD));
		lblTagInactive.setBounds(800, 260, 50, 61);
		lblTagInactive.setText(" 0");
		lblTagInactive.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		lblAlertActive = new Label(composite_15, SWT.NONE);
		lblAlertActive.setAlignment(SWT.RIGHT);
		lblAlertActive.setFont(SWTResourceManager.getFont("±¼¸²", 38, SWT.BOLD));
		lblAlertActive.setBounds(1300, 200, 41, 61);
		lblAlertActive.setText(" 0");
		lblAlertActive.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		lblAlertInactive = new Label(composite_15, SWT.NONE);
		lblAlertInactive.setAlignment(SWT.RIGHT);
		lblAlertInactive.setFont(SWTResourceManager.getFont("±¼¸²", 38, SWT.BOLD));
		lblAlertInactive.setBounds(1300, 260, 41, 61);
		lblAlertInactive.setText(" 0");
		lblAlertInactive.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Label lblNewLabel_4 = new Label(composite_15, SWT.NONE);
		lblNewLabel_4.setBounds(0, 0, 1647, 600);
		lblNewLabel_4.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		lblNewLabel_4.setBackgroundImage(resourceManager.createImage(slice_page1));
		
		Label lbldate_1 = new Label(composite_15, SWT.SHADOW_IN);
		lbldate_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		lbldate_1.setForeground(SWTResourceManager.getColor(0, 0, 0));
		lbldate_1.setFont(SWTResourceManager.getFont("³ª´®°íµñÄÚµù", 20, SWT.NORMAL));
		lbldate_1.setBounds(1350, 90, 50, 20);
		lbldate_1.setText("Date ");
		
		lblDate = new Label(composite_15, SWT.NONE);
		lblDate.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		lblDate.setForeground(SWTResourceManager.getColor(0, 0, 0));
		lblDate.setFont(SWTResourceManager.getFont("³ª´®°íµñÄÚµù", 16, SWT.NORMAL));
		lblDate.setBounds(1400, 93, 155, 20);
		lblDate.setText("2019-02-02");

		Label lblinterval = new Label(composite_15, SWT.NONE);
		lblinterval.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		lblinterval.setForeground(SWTResourceManager.getColor(0, 0, 0));
		lblinterval.setFont(SWTResourceManager.getFont("³ª´®°íµñÄÚµù", 20, SWT.NORMAL));
		lblinterval.setBounds(1350, 120, 200, 20);
		lblinterval.setText("Time Interval  5 sec ");
		
		Composite composite_3 = new Composite(composite, SWT.NONE);
		GridLayout gl_composite_3 = new GridLayout(2, true);
		gl_composite_3.marginRight = 50;
		gl_composite_3.marginLeft = 65;
		composite_3.setLayout(gl_composite_3);
		composite_3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite_3.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Composite composite_2 = new Composite(composite_3, SWT.NONE);
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite_2.setSize(438, 130);
		composite_2.setLayout(new GridLayout(1, false));
		composite_2.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		tableViewer_1 = new TableViewer(composite_2, SWT.BORDER | SWT.FULL_SELECTION);
		table_1 = tableViewer_1.getTable();
		table_1.setHeaderVisible(true);
		GridData gd_table_1 = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_table_1.heightHint = 130;
		table_1.setLayoutData(gd_table_1);
		
		TableViewerColumn tableViewerColumn_6 = new TableViewerColumn(tableViewer_1, SWT.NONE);
		tableViewerColumn_6.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				return element == null ? "" :((Ap)element).getApid() +""  ;
			}
		});
		TableColumn tblclmnNewColumn_5 = tableViewerColumn_6.getColumn();
		tblclmnNewColumn_5.setAlignment(SWT.CENTER);
		tblclmnNewColumn_5.setWidth(100);
		tblclmnNewColumn_5.setText("Mote ID");
		
		TableViewerColumn tableViewerColumn_7 = new TableViewerColumn(tableViewer_1, SWT.NONE);
		tableViewerColumn_7.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				// TODO Auto-generated method stub
				return element == null ? "" :((Ap)element).getRemark() +""  ;
			}
		});
		TableColumn tblclmnNewColumn_6 = tableViewerColumn_7.getColumn();
		tblclmnNewColumn_6.setAlignment(SWT.CENTER);
		tblclmnNewColumn_6.setWidth(300);
		tblclmnNewColumn_6.setText("Description");
		
		TableViewerColumn tableViewerColumn_8 = new TableViewerColumn(tableViewer_1, SWT.NONE);
		tableViewerColumn_8.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				String tmp = "";
				switch (((Ap)element).getAct()) {
				case 2:
					tmp = "Active";
					break;

				case 1:
					tmp = "Waiting";
					break;

				default:
					tmp = "Inactive";
					break;
				}
				return element == null ? "" :tmp  ;
			}
		});
		TableColumn tblclmnNewColumn_7 = tableViewerColumn_8.getColumn();
		tblclmnNewColumn_7.setAlignment(SWT.CENTER);
		tblclmnNewColumn_7.setWidth(100);
		tblclmnNewColumn_7.setText("Active");
		
		TableViewerColumn tableViewerColumn_9 = new TableViewerColumn(tableViewer_1, SWT.NONE);
		tableViewerColumn_9.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				return element == null ? "" :((Ap)element).getBatt() +""  ;
			}
		});
		TableColumn tblclmnNewColumn_8 = tableViewerColumn_9.getColumn();
		tblclmnNewColumn_8.setAlignment(SWT.CENTER);
		tblclmnNewColumn_8.setWidth(100);
		tblclmnNewColumn_8.setText("Battery");
		
		Composite composite_1 = new Composite(composite_3, SWT.V_SCROLL);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite_1.setSize(438, 0);
		composite_1.setLayout(new GridLayout(1, false));
		composite_1.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
				tableViewer = new TableViewer(composite_1, SWT.BORDER);
				table = tableViewer.getTable();
				table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
				table.setHeaderVisible(true);
				
				TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
				tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
					public Image getImage(Object element) {
						// TODO Auto-generated method stub
						return null;
					}
					public String getText(Object element) {
						// TODO Auto-generated method stub
						//return element == null ? "" : element.toString();
						return element == null ? "" : dateFormat.format( new Date( ((Tags)element).getTime().getTime() ) ) ;
					}
				});
				TableColumn tblclmnTime = tableViewerColumn.getColumn();
				tblclmnTime.setAlignment(SWT.CENTER);
				tblclmnTime.setWidth(200);
				tblclmnTime.setText("Time");
				
				TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
				tableViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {
					public Image getImage(Object element) {
						// TODO Auto-generated method stub
						return null;
					}
					public String getText(Object element) {
						return element == null ? "" :((Tags)element).getApid() +""  ;
					}
				});
				TableColumn tblclmnNewColumn = tableViewerColumn_1.getColumn();
				tblclmnNewColumn.setAlignment(SWT.CENTER);
				tblclmnNewColumn.setWidth(80);
				tblclmnNewColumn.setText("Mote ID");
				
				TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(tableViewer, SWT.NONE);
				tableViewerColumn_2.setLabelProvider(new ColumnLabelProvider() {
					public Image getImage(Object element) {
						// TODO Auto-generated method stub
						return null;
					}
					public String getText(Object element) {
						return element == null ? "" :((Tags)element).getTagid() +""  ;
					}
				});
				TableColumn tblclmnNewColumn_1 = tableViewerColumn_2.getColumn();
				tblclmnNewColumn_1.setAlignment(SWT.CENTER);
				tblclmnNewColumn_1.setWidth(80);
				tblclmnNewColumn_1.setText("Tag ID");
				
				TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(tableViewer, SWT.NONE);
				tableViewerColumn_3.setLabelProvider(new ColumnLabelProvider() {
					public Image getImage(Object element) {
						// TODO Auto-generated method stub
						return null;
					}
					public String getText(Object element) {
						return element == null ? "" :((Tags)element).getBatt() +""  ;
					}
				});
				TableColumn tblclmnNewColumn_2 = tableViewerColumn_3.getColumn();
				tblclmnNewColumn_2.setAlignment(SWT.CENTER);
				tblclmnNewColumn_2.setWidth(80);
				tblclmnNewColumn_2.setText("Battery");
				
				TableViewerColumn tableViewerColumn_4 = new TableViewerColumn(tableViewer, SWT.NONE);
				tableViewerColumn_4.setLabelProvider(new ColumnLabelProvider() {
					public Image getImage(Object element) {
						// TODO Auto-generated method stub
						return null;
					}
					public String getText(Object element) {
						return element == null ? "" :((Tags)element).getRssi() +""  ;
					}
				});
				TableColumn tblclmnNewColumn_3 = tableViewerColumn_4.getColumn();
				tblclmnNewColumn_3.setAlignment(SWT.CENTER);
				tblclmnNewColumn_3.setWidth(80);
				tblclmnNewColumn_3.setText("RSSI");
				
				TableViewerColumn tableViewerColumn_5 = new TableViewerColumn(tableViewer, SWT.NONE);
				tableViewerColumn_5.setLabelProvider(new ColumnLabelProvider() {
					public Image getImage(Object element) {
						// TODO Auto-generated method stub
						return null;
					}
					public String getText(Object element) {
						String tmp = "";
						switch (((Tags)element).getSos()) {
						case 0:
							tmp = "";
							break;
						default:
							tmp = "SOS";
							break;
						}
						return element == null ? "" :tmp  ;
						//return element == null ? "" :((Tags)element).getSos() +""  ;
					}
				});
				TableColumn tblclmnNewColumn_4 = tableViewerColumn_5.getColumn();
				tblclmnNewColumn_4.setAlignment(SWT.CENTER);
				tblclmnNewColumn_4.setWidth(80);
				tblclmnNewColumn_4.setText("SOS");
				tableViewer.setContentProvider(new ContentProvider());
		tableViewer_1.setContentProvider(new ContentProvider_1());
		
//		ServerPushSession pushSession = new ServerPushSession();
		Runnable runnable = new Runnable() {
		
			@Override
			public void run() {
				refreshSensorList();
//				
//				while(true){
//					refreshSensorList();
//					try {
//						Thread.sleep(5000);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
			}
		};

	    ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
	    service.scheduleAtFixedRate(runnable, 0, 5000, TimeUnit.MILLISECONDS);
	 


	/*		
			Thread uiUpdateThread = new Thread(runnable);

			uiUpdateThread.start();
*/			
	}

	@PreDestroy
	public void preDestroy() {
		
	}

	@Focus
	public void onFocus() {
		
	}

	@Persist
	public void save() {
		
	}
	
	int activeCnt = 0;
	int inactiveCnt = 0;

	int activeTagCnt = 0;
	int sosTagCnt = 0;
	private Table table_1;
	private Timestamp time_s = Timestamp.valueOf("1900-01-01 00:00:00") ;

	@SuppressWarnings("unchecked")
	public void refreshSensorList() {
		em.clear();
		em.getEntityManagerFactory().getCache().evictAll();
        Query qMaxTime = em.createQuery("select t from Tags t order by t.time desc ");
        qMaxTime.setFirstResult(0);
        qMaxTime.setMaxResults(1);
        Tags tag = (Tags) qMaxTime.getSingleResult();
        
        if (time_s.compareTo( tag.getTime() ) < 0 )  {
        	time_s = tag.getTime();
	        // ÅÂ±× ¸®½ºÆ® Áß Áßº¹ Á¦°Å 
	        Query qTags = em.createQuery("select t from Tags t where t.time = :time");
	        qTags.setParameter("time", tag.getTime());
	        List<Tags> tagListTemp = qTags.getResultList();
	        List<Tags> tagUniq = new ArrayList<Tags>();
	        tagList.clear();
	        for (Tags tag1 : tagListTemp) {
	        	int sw = 0;
	        	for (Tags t : tagUniq ) {
	        		if (tag1.getTagid() == t.getTagid()) {
	        			sw = 1; break ;
	        		}
	        	}
	        	if (sw == 0) tagUniq.add(tag1) ;
	        }
			for (Tags tag1 : tagUniq) {
				Tags s_tag = tag1 ;
				for (Tags tag2 : tagListTemp) {
					if(s_tag.getTagid() == tag2.getTagid()) {
						if(s_tag.getRssi() > tag2.getRssi()) { // tag2ÀÇ ½ÅÈ£°¡ ´õ ¼¼¸é
							s_tag = tag2 ;
						}
					}
				}
				tagList.add(s_tag) ;
			}
        }
        
        activeTagCnt = 0;
        sosTagCnt = 0;
		for (Tags tag1 : tagList) {
			activeTagCnt++;
			if(tag1.getSos() != 0) {
				sosTagCnt++;
			}
		}
        
        Query q2 = em.createQuery("select t from Ap t order by t.apid");
        apList = q2.getResultList();

		activeCnt = 0;
		inactiveCnt = 0;
		for (Ap ap : apList) {
			if(ap.getAct() == 2) {
				activeCnt++;
			}else {
				inactiveCnt++;
			}
		}

		
//        Query q = em.createQuery("select t.apid as apid, count(t.apid) As cnt from Tags t where t.time = :time group by t.apid");
//        q.setParameter("time", tag.getTime());
//        
//        List<Object[]>results = q.getResultList();
        
        tagCount.clear();
//        for (Object[] result : results) {
//        	//String name = (String) result[0];
//        	int apid = ((Number) result[0]).intValue();
//        	int count = ((Number) result[1]).intValue();
//        	
//        	tagCount.put(apid, count);
//        }
		for (Ap ap : apList) {
			int count = 0;
			for (Tags tag1 : tagList) {
				if(ap.getApid() == tag1.getApid()) {
					count++;
				}
			}
			if(count > 0) {
				tagCount.put(ap.getApid(), count);
			}
		}

		// È­¸é ¾÷µ¥ÀÌÆ®

//		if(!canvas.isDisposed()) {
			sync.syncExec(()->{
				//String apStatus = "È°¼º : " + activeCnt + " | ºñÈ°¼º : " + inactiveCnt;
				//String tagStatus = "È°¼º : " + activeTagCnt + " | SOS : " + sosTagCnt;

				lblDate.setText(dateFormat.format(time_s ) );
				lblApActive.setText(activeCnt+"");
				lblApInactive.setText(inactiveCnt+"");

				lblTagActive.setText(activeTagCnt+"");
				
				tableViewer_1.setInput(apList);
				tableViewer_1.refresh();
				tableViewer.setInput(tagList);
				tableViewer.refresh();
			});
//			sync.syncExec(()->{
//				canvas.redraw();
////				pushSession.stop();
//			});

//		}

	}
}