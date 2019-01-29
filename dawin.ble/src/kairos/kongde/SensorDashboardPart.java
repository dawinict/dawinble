
package kairos.kongde;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

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
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;
import org.eclipse.rap.rwt.service.ServerPushSession;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import kairos.kongde.entity.Ap;
import kairos.kongde.entity.Sensor;
import kairos.kongde.entity.Tags;

import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class SensorDashboardPart {
	@Inject UISynchronize sync;
	@Inject
	private EPartService partService;
	@Inject
	private EModelService modelService;
	@Inject
	private MApplication app;
	
	List<Tags> tagList;
	List<Tags> selectedTagList = new ArrayList<Tags>();
	Point selectedPoint = new Point(0, 0);
	List<Ap> apList;
	
	
	HashMap<Integer, Integer> tagCount = new HashMap();

    EntityManagerFactory emf = Persistence.createEntityManagerFactory("kairos.kongde");
    EntityManager em = emf.createEntityManager();
    Canvas canvas;
 
	Bundle bundle = FrameworkUtil.getBundle(this.getClass());
    // use the org.eclipse.core.runtime.Path as import
    URL url2 = FileLocator.find(bundle, new Path("icons/box_replacedtime.png"), null);
    ImageDescriptor box_replacedtime = ImageDescriptor.createFromURL(url2);

    URL url3 = FileLocator.find(bundle, new Path("icons/box_long.png"), null);
    ImageDescriptor box_long = ImageDescriptor.createFromURL(url3);

    URL url4 = FileLocator.find(bundle, new Path("icons/icon_mote.png"), null);
    ImageDescriptor icon_mote = ImageDescriptor.createFromURL(url4);

    URL url5 = FileLocator.find(bundle, new Path("icons/smallbox.png"), null);
    ImageDescriptor smallbox = ImageDescriptor.createFromURL(url5);

    URL url6 = FileLocator.find(bundle, new Path("icons/icon_sensor.png"), null);
    ImageDescriptor icon_sensor = ImageDescriptor.createFromURL(url6);

    URL url7 = FileLocator.find(bundle, new Path("icons/icon_active.png"), null);
    ImageDescriptor icon_active = ImageDescriptor.createFromURL(url7);

    URL url8 = FileLocator.find(bundle, new Path("icons/icon_lowbattery copy.png"), null);
    ImageDescriptor icon_inactive = ImageDescriptor.createFromURL(url8);
 
    URL url9 = FileLocator.find(bundle, new Path("icons/icon_lowbattery.png"), null);
    ImageDescriptor icon_lowbattery = ImageDescriptor.createFromURL(url9);

    URL url10 = FileLocator.find(bundle, new Path("icons/icon_sos.png"), null);
    ImageDescriptor icon_sos = ImageDescriptor.createFromURL(url10);

    URL url11 = FileLocator.find(bundle, new Path("icons/icon_total.png"), null);
    ImageDescriptor icon_total = ImageDescriptor.createFromURL(url11);
    
    URL url12 = FileLocator.find(bundle, new Path("icons/icon_emergency.png"), null);
    ImageDescriptor icon_emergency = ImageDescriptor.createFromURL(url12);
    
    Image image_active ;
    Image image_inactive ;
    Image image_lowbattery ;
    Image image_sos ;
    
    Label lblApActive;
    Label lblApInactive;
    Label lblApLow;
    
    Label lblTagActive;
    Label lblTagInactive;
    Label lblTagLow;
    
    
	DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	DateFormat dateFormatDate = new SimpleDateFormat("yyyy.MM.dd");
	Label lblNewLabel_7;
	Label lblNewLabel_8;
	Label lblNewLabel_19;
	
    @PostConstruct
	public void postConstruct(Composite parent) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		ResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources());
		Bundle bundle = FrameworkUtil.getBundle(this.getClass());
//        URL url = FileLocator.find(bundle, new Path("icons/floor.jpg"), null);
        URL url = FileLocator.find(bundle, new Path("icons/map.png"), null);
        ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(url);
		Image image = resourceManager.createImage(imageDescriptor);
		image_active = resourceManager.createImage(icon_active);
		image_inactive = resourceManager.createImage(icon_inactive);
		image_lowbattery = resourceManager.createImage(icon_lowbattery);
		image_sos = resourceManager.createImage(icon_sos);
		
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		GridData gd_composite_1 = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_composite_1.minimumWidth = 1780;
		composite_1.setLayoutData(gd_composite_1);
		GridLayout gl_composite_1 = new GridLayout(2, false);
		gl_composite_1.marginWidth = 0;
		gl_composite_1.marginHeight = 0;
		gl_composite_1.horizontalSpacing = 0;
		composite_1.setLayout(gl_composite_1);
		
		Composite composite_3 = new Composite(composite_1, SWT.NONE);
		GridData gd_composite_3 = new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1);
		gd_composite_3.heightHint = 226;
		gd_composite_3.widthHint = 633;
		composite_3.setLayoutData(gd_composite_3);
		GridLayout gl_composite_3 = new GridLayout(2, false);
		gl_composite_3.marginLeft = 60;
		gl_composite_3.verticalSpacing = 0;
		gl_composite_3.marginHeight = 0;
		gl_composite_3.horizontalSpacing = 0;
		gl_composite_3.marginWidth = 0;
		composite_3.setLayout(gl_composite_3);
		composite_3.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		composite_3.setBackgroundImage(resourceManager.createImage(box_replacedtime));
		
		Label lblNewLabel = new Label(composite_3, SWT.NONE);
		lblNewLabel.setFont(new Font(null, "¸¼Àº °íµñ", 34, SWT.NORMAL));
		lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));
		lblNewLabel.setText("Replaced");
		lblNewLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		lblNewLabel_7 = new Label(composite_3, SWT.NONE);
		lblNewLabel_7.setFont(new Font(null, "¸¼Àº °íµñ", 24, SWT.NORMAL));
		lblNewLabel_7.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblNewLabel_7.setText("New Label");
		lblNewLabel_7.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Label lblNewLabel_5 = new Label(composite_3, SWT.NONE);
		lblNewLabel_5.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, true, 1, 1));
		lblNewLabel_5.setFont(new Font(null, "¸¼Àº °íµñ", 34, SWT.NORMAL));
		lblNewLabel_5.setText("Time");
		lblNewLabel_5.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		lblNewLabel_8 = new Label(composite_3, SWT.NONE);
		lblNewLabel_8.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblNewLabel_8.setFont(new Font(null, "¸¼Àº °íµñ", 38, SWT.NORMAL));
		lblNewLabel_8.setText("New Label");
		lblNewLabel_8.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Composite composite_4 = new Composite(composite_1, SWT.NONE);
		GridLayout gl_composite_4 = new GridLayout(12, false);
		gl_composite_4.marginHeight = 30;
		gl_composite_4.marginLeft = 60;
		composite_4.setLayout(gl_composite_4);
		GridData gd_composite_4 = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_composite_4.heightHint = 226;
		gd_composite_4.widthHint = 1148;
		composite_4.setLayoutData(gd_composite_4);
		composite_4.setBounds(0, 0, 64, 64);
		composite_4.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		composite_4.setBackgroundImage(resourceManager.createImage(box_long));
		
		Label lblNewLabel_9 = new Label(composite_4, SWT.NONE);
		lblNewLabel_9.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
		lblNewLabel_9.setText("Total");
		lblNewLabel_9.setFont(new Font(null, "¸¼Àº °íµñ", 20, SWT.NORMAL));
		lblNewLabel_9.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Label lblNewLabel_18 = new Label(composite_4, SWT.NONE);
		lblNewLabel_18.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
		lblNewLabel_18.setText("SOS");
		lblNewLabel_18.setFont(new Font(null, "¸¼Àº °íµñ", 20, SWT.NORMAL));
		lblNewLabel_18.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Label lblNewLabel_28 = new Label(composite_4, SWT.NONE);
		lblNewLabel_28.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
		lblNewLabel_28.setText("Emergency Zone");
		lblNewLabel_28.setFont(new Font(null, "¸¼Àº °íµñ", 20, SWT.NORMAL));
		lblNewLabel_28.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Label lblNewLabel_15 = new Label(composite_4, SWT.NONE);
		
		Label lblNewLabel_17 = new Label(composite_4, SWT.NONE);
		GridData gd_lblNewLabel_17 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblNewLabel_17.heightHint = 64;
		gd_lblNewLabel_17.widthHint = 49;
		lblNewLabel_17.setLayoutData(gd_lblNewLabel_17);
		lblNewLabel_17.setText("");
		lblNewLabel_17.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		lblNewLabel_17.setBackgroundImage(resourceManager.createImage(icon_total));
		
		lblNewLabel_19 = new Label(composite_4, SWT.NONE);
		lblNewLabel_19.setText("48");
		lblNewLabel_19.setFont(new Font(null, "¸¼Àº °íµñ", 30, SWT.NORMAL));
		lblNewLabel_19.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Label lblNewLabel_20 = new Label(composite_4, SWT.NONE);
		lblNewLabel_20.setText("Person");
		lblNewLabel_20.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Label lblNewLabel_21 = new Label(composite_4, SWT.NONE);
		GridData gd_lblNewLabel_21 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblNewLabel_21.heightHint = 64;
		gd_lblNewLabel_21.widthHint = 56;
		lblNewLabel_21.setLayoutData(gd_lblNewLabel_21);
		lblNewLabel_21.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		lblNewLabel_21.setBackgroundImage(resourceManager.createImage(icon_emergency));
		
		Label lblNewLabel_24 = new Label(composite_4, SWT.NONE);
		lblNewLabel_24.setText(" 0");
		lblNewLabel_24.setFont(new Font(null, "¸¼Àº °íµñ", 30, SWT.NORMAL));
		lblNewLabel_24.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Label lblNewLabel_26 = new Label(composite_4, SWT.NONE);
		lblNewLabel_26.setText("Person");
		lblNewLabel_26.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		new Label(composite_4, SWT.NONE);
		
		Label lblNewLabel_27 = new Label(composite_4, SWT.NONE);
		
		Label lblNewLabel_25 = new Label(composite_4, SWT.NONE);
		GridData gd_lblNewLabel_25 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblNewLabel_25.heightHint = 64;
		gd_lblNewLabel_25.widthHint = 56;
		lblNewLabel_25.setLayoutData(gd_lblNewLabel_25);
		lblNewLabel_25.setText("");
		lblNewLabel_25.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		lblNewLabel_25.setBackgroundImage(resourceManager.createImage(icon_emergency));
		
		Label lblNewLabel_10 = new Label(composite_4, SWT.NONE);
		lblNewLabel_10.setText("New Label");
		lblNewLabel_10.setText(" 0");
		lblNewLabel_10.setFont(new Font(null, "¸¼Àº °íµñ", 30, SWT.NORMAL));
		lblNewLabel_10.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Label lblNewLabel_12 = new Label(composite_4, SWT.NONE);
		lblNewLabel_12.setText("Person");
		lblNewLabel_12.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Composite composite_21 = new Composite(composite, SWT.NONE);
		composite_21.setLayout(new GridLayout(3, false));
		composite_21.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
				
				Composite composite_22 = new Composite(composite_21, SWT.NONE);
				GridData gd_composite_22 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
				gd_composite_22.widthHint = 100;
				composite_22.setLayoutData(gd_composite_22);
				composite_22.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
				
				canvas = new Canvas(composite_21, SWT.NONE);
				canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
				canvas.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseDown(MouseEvent e) {
						selectedTagList.clear();
						for (Ap ap : apList) {
							if(ap.getX()  < e.x && e.x < ap.getX() + 60 && ap.getY()  < e.y && e.y < ap.getY() + 60) {
								selectedPoint.x = ap.getX();
								selectedPoint.y = ap.getY();
								for (Tags tag1 : tagList) {
									activeTagCnt++;
									if(tag1.getApid() == ap.getApid()) {
										selectedTagList.add(tag1);
									}
								}
								break;
							}
						}
						canvas.redraw();
					}
				});
				GridLayout gl_canvas = new GridLayout(1, false);
				canvas.setLayout(gl_canvas);
				new Label(composite_21, SWT.NONE);
				
						canvas.addPaintListener(new PaintListener() {
							public void paintControl(PaintEvent event) {
								// ¹è°æÈ­¸é ¸Ê ±×¸®±â
								event.gc.drawImage(image, 0, 0);
								
								if(apList != null) {
									for(Ap ap : apList) {
										if(ap.getBatt() < 0.3 && ap.getAct() == 2) {
											event.gc.drawImage(image_lowbattery, ap.getX(), ap.getY());
											event.gc.setBackground(new Color(event.display, new RGB(242, 165, 0)));
										}else if(ap.getAct() == 2) {
											//event.gc.setBackground(new Color(event.display, new RGB(0, 255, 0)));
											event.gc.drawImage(image_active, ap.getX(), ap.getY());
											event.gc.setBackground(new Color(event.display, new RGB(48, 138, 249)));
										}else {
											//event.gc.setBackground(new Color(event.display, new RGB(255, 0, 0)));
											event.gc.drawImage(image_inactive, ap.getX(), ap.getY());
											event.gc.setBackground(new Color(event.display, new RGB(167, 167, 167)));
										}
										//event.gc.fillOval(ap.getX()-25, ap.getY()-25, 50, 50);
										
										//event.gc.setBackground(event.display.getSystemColor(SWT.COLOR_TRANSPARENT));
										event.gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
										event.gc.drawText("#"+ap.getApid(), ap.getX()+50, ap.getY()+70);
										if(tagCount.get(ap.getApid()) != null) {
											event.gc.drawText(""+tagCount.get(ap.getApid()), ap.getX()+55, ap.getY()+40);
										}
									}
									
				//					for(Ap ap : apList) {
				//						//System.out.println("ZZZZ" +sensor.getAddress()+ sensor.getX());
				//						event.gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
				//						
				//						//event.gc.setForeground(new Color(event.display, new RGB(0, 0, 0)));
				//						event.gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
				//						if(tagCount.get(ap.getApid()) != null) {
				//							event.gc.drawText(""+tagCount.get(ap.getApid()), ap.getX()+50, ap.getY()+30);
				//						}
				//					}
									
									if(selectedTagList.size() > 0) {
										event.gc.setBackground(new Color(event.display, new RGB(255, 255, 0)));
										//event.gc.fillOval(ap.getX()-25, ap.getY()-25, 50, 50);
										event.gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
										
										int fontHeight = 14;
										int pointY = 10;
										for (Tags tag : selectedTagList) {
											event.gc.drawText("Tag:"+tag.getTagid()+" , RSSI:"+tag.getRssi(), selectedPoint.x+40, selectedPoint.y + pointY+20);
											pointY = pointY + fontHeight;
										}
				
									}
								}
								
							}
						});
		
		Composite composite_2 = new Composite(composite, SWT.NONE);
		GridData gd_composite_2 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_composite_2.heightHint = 150;
		composite_2.setLayoutData(gd_composite_2);
		composite_2.setLayout(new GridLayout(4, false));
		
		Composite composite_5 = new Composite(composite_2, SWT.NONE);
		
		Button btnNewButton = new Button(composite_5, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
		        MPerspective perspective = (MPerspective) modelService.find("dawin.ble.perspective.dashboard", app);
		        if (perspective != null) {
		            partService.switchPerspective(perspective);
		        }

			}
		});
		btnNewButton.setBounds(0, 0, 76, 25);
		btnNewButton.setText("Board1");
		
		Composite composite_6 = new Composite(composite_2, SWT.NONE);
		GridData gd_composite_6 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_composite_6.heightHint = 140;
		gd_composite_6.widthHint = 472;
		composite_6.setLayoutData(gd_composite_6);
		GridLayout gl_composite_6 = new GridLayout(7, false);
		gl_composite_6.marginTop = 15;
		gl_composite_6.marginRight = 5;
		gl_composite_6.marginLeft = 45;
		composite_6.setLayout(gl_composite_6);
		composite_6.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		composite_6.setBackgroundImage(resourceManager.createImage(smallbox));
		
		Composite composite_9 = new Composite(composite_6, SWT.NONE);
		GridData gd_composite_9 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_composite_9.widthHint = 80;
		composite_9.setLayoutData(gd_composite_9);
		composite_9.setBounds(0, 0, 64, 64);
		composite_9.setLayout(new GridLayout(1, false));
		composite_9.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Label lblNewLabel_2 = new Label(composite_9, SWT.NONE);
		GridData gd_lblNewLabel_2 = new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1);
		gd_lblNewLabel_2.heightHint = 45;
		gd_lblNewLabel_2.widthHint = 50;
		lblNewLabel_2.setLayoutData(gd_lblNewLabel_2);
		lblNewLabel_2.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		lblNewLabel_2.setBackgroundImage(resourceManager.createImage(icon_mote));
		
		Label lblNewLabel_3 = new Label(composite_9, SWT.NONE);
		lblNewLabel_3.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_3.setAlignment(SWT.CENTER);
		lblNewLabel_3.setText("Mote");
		lblNewLabel_3.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Label label = new Label(composite_6, SWT.SEPARATOR | SWT.VERTICAL);
		label.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Composite composite_10 = new Composite(composite_6, SWT.NONE);
		GridData gd_composite_10 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_composite_10.widthHint = 80;
		composite_10.setLayoutData(gd_composite_10);
		composite_10.setBounds(0, 0, 64, 64);
		composite_10.setLayout(new GridLayout(1, false));
		composite_10.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		lblApActive = new Label(composite_10, SWT.NONE);
		lblApActive.setFont(new Font(null, "¸¼Àº °íµñ", 34, SWT.NORMAL));
		lblApActive.setAlignment(SWT.CENTER);
		lblApActive.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		lblApActive.setBounds(0, 0, 100, 103);
		lblApActive.setText(" 8");
		lblApActive.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Label lblNewLabel_1 = new Label(composite_10, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setAlignment(SWT.CENTER);
		lblNewLabel_1.setBounds(0, 0, 56, 15);
		lblNewLabel_1.setText("Active");
		lblNewLabel_1.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Label label_1 = new Label(composite_6, SWT.SEPARATOR | SWT.VERTICAL);
		
		Composite composite_11 = new Composite(composite_6, SWT.NONE);
		GridData gd_composite_11 = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1);
		gd_composite_11.widthHint = 80;
		composite_11.setLayoutData(gd_composite_11);
		composite_11.setBounds(0, 0, 64, 64);
		composite_11.setLayout(new GridLayout(1, false));
		composite_11.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		lblApInactive = new Label(composite_11, SWT.NONE);
		lblApInactive.setFont(new Font(null, "¸¼Àº °íµñ", 34, SWT.NORMAL));
		lblApInactive.setAlignment(SWT.CENTER);
		lblApInactive.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		lblApInactive.setBounds(0, 0, 100, 103);
		lblApInactive.setText(" 8");
		lblApInactive.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Label lblNewLabel_4 = new Label(composite_11, SWT.NONE);
		lblNewLabel_4.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_4.setAlignment(SWT.CENTER);
		lblNewLabel_4.setBounds(0, 0, 56, 15);
		lblNewLabel_4.setText("Inactive");
		lblNewLabel_4.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Label label_2 = new Label(composite_6, SWT.SEPARATOR | SWT.VERTICAL);
		
		Composite composite_12 = new Composite(composite_6, SWT.NONE);
		GridData gd_composite_12 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_composite_12.widthHint = 80;
		composite_12.setLayoutData(gd_composite_12);
		composite_12.setBounds(0, 0, 64, 64);
		composite_12.setLayout(new GridLayout(1, false));
		composite_12.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));

		lblApLow = new Label(composite_12, SWT.NONE);
		lblApLow.setFont(new Font(null, "¸¼Àº °íµñ", 34, SWT.NORMAL));
		lblApLow.setAlignment(SWT.CENTER);
		lblApLow.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		lblApLow.setBounds(0, 0, 100, 103);
		lblApLow.setText(" 8");
		lblApLow.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Label lblNewLabel_6 = new Label(composite_12, SWT.NONE);
		lblNewLabel_6.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_6.setAlignment(SWT.CENTER);
		lblNewLabel_6.setBounds(0, 0, 56, 15);
		lblNewLabel_6.setText("Low");
		lblNewLabel_6.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));

		
		Composite composite_7 = new Composite(composite_2, SWT.NONE);
		GridData gd_composite_7 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_composite_7.heightHint = 140;
		gd_composite_7.widthHint = 472;
		composite_7.setLayoutData(gd_composite_7);
		GridLayout gl_composite_7 = new GridLayout(7, false);
		gl_composite_7.marginTop = 15;
		gl_composite_7.marginLeft = 45;
		composite_7.setLayout(gl_composite_7);
		composite_7.setBackgroundImage(resourceManager.createImage(smallbox));
		
		Composite composite_16 = new Composite(composite_7, SWT.NONE);
		GridData gd_composite_16 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_composite_16.widthHint = 80;
		composite_16.setLayoutData(gd_composite_16);
		composite_16.setLayout(new GridLayout(1, false));
		composite_16.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Label lblTagIcon = new Label(composite_16, SWT.NONE);
		GridData gd_lblTagIcon = new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1);
		gd_lblTagIcon.heightHint = 45;
		gd_lblTagIcon.widthHint = 50;
		lblTagIcon.setLayoutData(gd_lblTagIcon);
		lblTagIcon.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		lblTagIcon.setBackgroundImage(resourceManager.createImage(icon_sensor));
		
		Label lblNewLabel_13 = new Label(composite_16, SWT.NONE);
		lblNewLabel_13.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_13.setAlignment(SWT.CENTER);
		lblNewLabel_13.setText("Tag");
		lblNewLabel_13.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Label label2 = new Label(composite_7, SWT.SEPARATOR | SWT.VERTICAL);
		label2.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Composite composite_17 = new Composite(composite_7, SWT.NONE);
		GridData gd_composite_17 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_composite_17.widthHint = 80;
		composite_17.setLayoutData(gd_composite_17);
		composite_17.setBounds(0, 0, 64, 64);
		composite_17.setLayout(new GridLayout(1, false));
		composite_17.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		lblTagActive = new Label(composite_17, SWT.NONE);
		lblTagActive.setFont(new Font(null, "¸¼Àº °íµñ", 34, SWT.NORMAL));
		lblTagActive.setAlignment(SWT.CENTER);
		lblTagActive.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		lblTagActive.setBounds(0, 0, 100, 103);
		lblTagActive.setText(" 8");
		lblTagActive.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Label lblNewLabel_11 = new Label(composite_17, SWT.NONE);
		lblNewLabel_11.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_11.setAlignment(SWT.CENTER);
		lblNewLabel_11.setBounds(0, 0, 56, 15);
		lblNewLabel_11.setText("Active");
		lblNewLabel_11.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Label label_11 = new Label(composite_7, SWT.SEPARATOR | SWT.VERTICAL);
		label_11.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Composite composite_18 = new Composite(composite_7, SWT.NONE);
		GridData gd_composite_18 = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1);
		gd_composite_18.widthHint = 80;
		composite_18.setLayoutData(gd_composite_18);
		composite_18.setBounds(0, 0, 64, 64);
		composite_18.setLayout(new GridLayout(1, false));
		composite_18.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		lblTagInactive = new Label(composite_18, SWT.NONE);
		lblTagInactive.setFont(new Font(null, "¸¼Àº °íµñ", 34, SWT.NORMAL));
		lblTagInactive.setAlignment(SWT.CENTER);
		lblTagInactive.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		lblTagInactive.setBounds(0, 0, 100, 103);
		lblTagInactive.setText(" 8");
		lblTagInactive.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Label lblNewLabel_14 = new Label(composite_18, SWT.NONE);
		lblNewLabel_14.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_14.setAlignment(SWT.CENTER);
		lblNewLabel_14.setBounds(0, 0, 56, 15);
		lblNewLabel_14.setText("Inactive");
		lblNewLabel_14.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Label label_22 = new Label(composite_7, SWT.SEPARATOR | SWT.VERTICAL);
		label_22.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Composite composite_19 = new Composite(composite_7, SWT.NONE);
		GridData gd_composite_19 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_composite_19.widthHint = 80;
		composite_19.setLayoutData(gd_composite_19);
		composite_19.setBounds(0, 0, 64, 64);
		composite_19.setLayout(new GridLayout(1, false));
		composite_19.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));

		lblTagLow = new Label(composite_19, SWT.NONE);
		lblTagLow.setFont(new Font(null, "¸¼Àº °íµñ", 34, SWT.NORMAL));
		lblTagLow.setAlignment(SWT.CENTER);
		lblTagLow.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		lblTagLow.setBounds(0, 0, 100, 103);
		lblTagLow.setText(" 8");
		lblTagLow.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Label lblNewLabel_16 = new Label(composite_19, SWT.NONE);
		lblNewLabel_16.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_16.setAlignment(SWT.CENTER);
		lblNewLabel_16.setBounds(0, 0, 56, 15);
		lblNewLabel_16.setText("Low");
		lblNewLabel_16.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		new Label(composite_6, SWT.NONE);
		new Label(composite_6, SWT.NONE);
		new Label(composite_6, SWT.NONE);
		new Label(composite_6, SWT.NONE);
		new Label(composite_6, SWT.NONE);
		new Label(composite_6, SWT.NONE);
		new Label(composite_6, SWT.NONE);

		
		Composite composite_8 = new Composite(composite_2, SWT.NONE);
		composite_8.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
		composite_8.setLayout(new GridLayout(4, false));
		
		Composite composite_13 = new Composite(composite_8, SWT.NONE);
		composite_13.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
		composite_13.setLayout(new GridLayout(1, false));

		Label lblNewLabel_22 = new Label(composite_13, SWT.NONE);
		GridData lblNewLabel_32 = new GridData(SWT.CENTER, SWT.FILL, false, true, 1, 1);
		lblNewLabel_32.heightHint = 146;
		lblNewLabel_32.widthHint = 128;
		lblNewLabel_22.setLayoutData(lblNewLabel_32);
		lblNewLabel_22.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		lblNewLabel_22.setBackgroundImage(resourceManager.createImage(icon_active));
		
		Label lblNewLabel_23 = new Label(composite_13, SWT.NONE);
		lblNewLabel_23.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_23.setAlignment(SWT.CENTER);
		lblNewLabel_23.setText("Active");
		lblNewLabel_23.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));

		Composite composite_14 = new Composite(composite_8, SWT.NONE);
		composite_14.setLayout(new GridLayout(1, false));

		Label lblNewLabel_33 = new Label(composite_14, SWT.NONE);
		GridData gd_lblNewLabel_33 = new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1);
		gd_lblNewLabel_33.heightHint = 146;
		gd_lblNewLabel_33.widthHint = 128;
		lblNewLabel_33.setLayoutData(gd_lblNewLabel_33);
		lblNewLabel_33.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		lblNewLabel_33.setBackgroundImage(resourceManager.createImage(icon_inactive));
		
		Label lblNewLabel_34 = new Label(composite_14, SWT.NONE);
		lblNewLabel_34.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_34.setAlignment(SWT.CENTER);
		lblNewLabel_34.setText("Inactive");
		lblNewLabel_34.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Composite composite_15 = new Composite(composite_8, SWT.NONE);
		composite_15.setLayout(new GridLayout(1, false));

		Label lblNewLabel_35 = new Label(composite_15, SWT.NONE);
		GridData gd_lblNewLabel_35 = new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1);
		gd_lblNewLabel_35.heightHint = 146;
		gd_lblNewLabel_35.widthHint = 128;
		lblNewLabel_35.setLayoutData(gd_lblNewLabel_35);
		lblNewLabel_35.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		lblNewLabel_35.setBackgroundImage(resourceManager.createImage(icon_lowbattery));
		
		Label lblNewLabel_36 = new Label(composite_15, SWT.NONE);
		lblNewLabel_36.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_36.setAlignment(SWT.CENTER);
		lblNewLabel_36.setText("Low Battery");
		lblNewLabel_36.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));

		
		Composite composite_20 = new Composite(composite_8, SWT.NONE);
		composite_20.setLayout(new GridLayout(1, false));

		Label lblNewLabel_37 = new Label(composite_20, SWT.NONE);
		GridData gd_lblNewLabel_37 = new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1);
		gd_lblNewLabel_37.heightHint = 146;
		gd_lblNewLabel_37.widthHint = 128;
		lblNewLabel_37.setLayoutData(gd_lblNewLabel_37);
		lblNewLabel_37.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		lblNewLabel_37.setBackgroundImage(resourceManager.createImage(icon_sos));
		
		Label lblNewLabel_38 = new Label(composite_20, SWT.NONE);
		lblNewLabel_38.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_38.setAlignment(SWT.CENTER);
		lblNewLabel_38.setText("SOS");
		lblNewLabel_38.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		//canvas.setBackgroundImage(image);

//		ServerPushSession pushSession = new ServerPushSession();
		Runnable runnable = new Runnable() {
		
			@Override
			public void run() {
				while(true){
					em.clear();
					refreshSensorList();
					sync.syncExec(()->{
						canvas.redraw();
//						pushSession.stop();
					});

//					System.out.println("ZZZZ");
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				}
			}
		};
//		pushSession.start();
			Thread uiUpdateThread = new Thread(runnable);
//			uiUpdateThread.setDaemon(true);
			uiUpdateThread.start();
			
//			 String code =   "window.setInterval( function() {\n"
//		               + "  rwt.remote.Connection.getInstance().send();\n"
//		               + "}, 5000 );"; // polling interval in ms
//		 JavaScriptExecutor executor = RWT.getClient().getService( JavaScriptExecutor.class );
//		 if( executor != null ) {
//		     executor.execute( code );
//		 }
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

	@SuppressWarnings("unchecked")
	public void refreshSensorList() {
        Query qMaxTime = em.createQuery("select t from Tags t order by t.time desc");
        qMaxTime.setFirstResult(0);
        qMaxTime.setMaxResults(1);
        Tags tag = (Tags) qMaxTime.getSingleResult();

        Query q = em.createQuery("select t.apid as apid, count(t.apid) As cnt from Tags t where t.time = :time group by t.apid");
        q.setParameter("time", tag.getTime());
        
        List<Object[]>results = q.getResultList();
        
        tagCount.clear();
        for (Object[] result : results) {
        	//String name = (String) result[0];
        	int apid = ((Number) result[0]).intValue();
        	int count = ((Number) result[1]).intValue();
        	
        	tagCount.put(apid, count);
        }

        Query qTags = em.createQuery("select t from Tags t where t.time = :time");
        qTags.setParameter("time", tag.getTime());
        tagList = qTags.getResultList();
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

		// È­¸é ¾÷µ¥ÀÌÆ®

		if(!canvas.isDisposed()) {
			sync.syncExec(()->{
				//String apStatus = "È°¼º : " + activeCnt + " | ºñÈ°¼º : " + inactiveCnt;
				//String tagStatus = "È°¼º : " + activeTagCnt + " | SOS : " + sosTagCnt;

				lblNewLabel_7.setText(dateFormatDate.format(new Date( tag.getTime().getTime() ) ));
				lblNewLabel_8.setText(dateFormat.format(new Date( tag.getTime().getTime() ) ));
				
				lblNewLabel_19.setText(activeTagCnt+"");
				
				lblApActive.setText(activeCnt+"");
				lblApInactive.setText(inactiveCnt+"");
				lblApLow.setText(0+"");

				lblTagActive.setText(activeTagCnt+"");
				lblTagInactive.setText(0+"");
				lblTagLow.setText(0+"");
				
			});
			sync.syncExec(()->{
				canvas.redraw();
//				pushSession.stop();
			});

		}

	}
}