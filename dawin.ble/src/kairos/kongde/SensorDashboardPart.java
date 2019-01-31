
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
	
	List<Tags> tagList = new ArrayList<Tags>();
	List<Tags> selectedTagList = new ArrayList<Tags>();
	Point selectedPoint = new Point(0, 0);
	List<Ap> apList;
	
	
	HashMap<Integer, Integer> tagCount = new HashMap();
	HashMap<Integer, Integer> sosCount = new HashMap();

    EntityManagerFactory emf = Persistence.createEntityManagerFactory("kairos.kongde");
    EntityManager em = emf.createEntityManager();
    Canvas canvas;
 
	Bundle bundle = FrameworkUtil.getBundle(this.getClass());
    // use the org.eclipse.core.runtime.Path as import
    URL url2 = FileLocator.find(bundle, new Path("icons/slice_page2_1.png"), null);
    ImageDescriptor slice_page2_1 = ImageDescriptor.createFromURL(url2);

    URL url3 = FileLocator.find(bundle, new Path("icons/box_long.png"), null);
    ImageDescriptor box_long = ImageDescriptor.createFromURL(url3);

    URL url4 = FileLocator.find(bundle, new Path("icons/icon_mote.png"), null);
    ImageDescriptor icon_mote = ImageDescriptor.createFromURL(url4);

    URL url5 = FileLocator.find(bundle, new Path("icons/smallbox.png"), null);
    ImageDescriptor smallbox = ImageDescriptor.createFromURL(url5);

    URL url6 = FileLocator.find(bundle, new Path("icons/icon_sensor.png"), null);
    ImageDescriptor icon_sensor = ImageDescriptor.createFromURL(url6);

    URL url7 = FileLocator.find(bundle, new Path("icons/moteicon_active.png"), null);
    ImageDescriptor icon_active = ImageDescriptor.createFromURL(url7);

    URL url8 = FileLocator.find(bundle, new Path("icons/moteicon_inactive.png"), null);
    ImageDescriptor icon_inactive = ImageDescriptor.createFromURL(url8);
 
    URL url9 = FileLocator.find(bundle, new Path("icons/moteicon_lowbattery.png"), null);
    ImageDescriptor icon_lowbattery = ImageDescriptor.createFromURL(url9);

    URL url10 = FileLocator.find(bundle, new Path("icons/moteicon_sos.png"), null);
    ImageDescriptor moteicon_sos = ImageDescriptor.createFromURL(url10);

    URL url11 = FileLocator.find(bundle, new Path("icons/icon_total.png"), null);
    ImageDescriptor icon_total = ImageDescriptor.createFromURL(url11);
    
    URL url12 = FileLocator.find(bundle, new Path("icons/icon_emergency.png"), null);
    ImageDescriptor icon_emergency = ImageDescriptor.createFromURL(url12);
    
    URL url13 = FileLocator.find(bundle, new Path("icons/slice_page2_2.png"), null);
    ImageDescriptor slice_page2_2 = ImageDescriptor.createFromURL(url13);
    
    URL url14 = FileLocator.find(bundle, new Path("icons/category_mote.png"), null);
    ImageDescriptor category_mote = ImageDescriptor.createFromURL(url14);

    URL url15 = FileLocator.find(bundle, new Path("icons/popup_inactive_1.png"), null);
    ImageDescriptor popup_inactive_1 = ImageDescriptor.createFromURL(url15);

    URL url16 = FileLocator.find(bundle, new Path("icons/Triangle.png"), null);
    ImageDescriptor Triangle = ImageDescriptor.createFromURL(url16);

    URL url17 = FileLocator.find(bundle, new Path("icons/Triangle2.png"), null);
    ImageDescriptor Triangle2 = ImageDescriptor.createFromURL(url17);

    Image image_active ;
    Image image_inactive ;
    Image image_lowbattery ;
    Image image_sos ;
    Image image_popup_inactive_1;
    
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
	Label lblNewLabel_24;

	ResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources());

    @PostConstruct
	public void postConstruct(Composite parent) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Bundle bundle = FrameworkUtil.getBundle(this.getClass());
//        URL url = FileLocator.find(bundle, new Path("icons/floor.jpg"), null);
        URL url = FileLocator.find(bundle, new Path("icons/map.png"), null);
        ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(url);
		Image image = resourceManager.createImage(imageDescriptor);
		image_active = resourceManager.createImage(icon_active);
		image_inactive = resourceManager.createImage(icon_inactive);
		image_lowbattery = resourceManager.createImage(icon_lowbattery);
		image_sos = resourceManager.createImage(moteicon_sos);
		image_popup_inactive_1 = resourceManager.createImage(popup_inactive_1);
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayout(null);
		GridData gd_composite_1 = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_composite_1.minimumHeight = 268;
		gd_composite_1.heightHint = 268;
		gd_composite_1.widthHint = 1920;
		gd_composite_1.minimumWidth = 1920;
		composite_1.setLayoutData(gd_composite_1);
		composite_1.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		composite_1.setBackgroundImage(resourceManager.createImage(slice_page2_1));
		
		lblNewLabel_7 = new Label(composite_1, SWT.NONE);
		lblNewLabel_7.setLocation(350, 60);
		lblNewLabel_7.setSize(152, 45);
		lblNewLabel_7.setFont(new Font(null, "¸¼Àº °íµñ", 28, SWT.NORMAL));
		lblNewLabel_7.setText("New Label");
		lblNewLabel_7.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		lblNewLabel_7.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		
		lblNewLabel_8 = new Label(composite_1, SWT.NONE);
		lblNewLabel_8.setLocation(350, 100);
		lblNewLabel_8.setSize(244, 68);
		lblNewLabel_8.setFont(new Font(null, "¸¼Àº °íµñ", 44, SWT.NORMAL));
		lblNewLabel_8.setText("New Label");
		lblNewLabel_8.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		lblNewLabel_8.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		
		lblNewLabel_19 = new Label(composite_1, SWT.NONE);
		lblNewLabel_19.setLocation(880, 110);
		lblNewLabel_19.setSize(44, 54);
		lblNewLabel_19.setText("48");
		lblNewLabel_19.setFont(new Font(null, "¸¼Àº °íµñ", 40, SWT.NORMAL));
		lblNewLabel_19.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		lblNewLabel_19.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		
		lblNewLabel_24 = new Label(composite_1, SWT.NONE);
		lblNewLabel_24.setLocation(1280, 110);
		lblNewLabel_24.setSize(36, 54);
		lblNewLabel_24.setText(" 0");
		lblNewLabel_24.setFont(new Font(null, "¸¼Àº °íµñ", 40, SWT.NORMAL));
		lblNewLabel_24.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		lblNewLabel_24.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		
		Label lblNewLabel_10 = new Label(composite_1, SWT.NONE);
		lblNewLabel_10.setLocation(1670, 110);
		lblNewLabel_10.setSize(36, 54);
		lblNewLabel_10.setText(" 0");
		lblNewLabel_10.setFont(new Font(null, "¸¼Àº °íµñ", 40, SWT.NORMAL));
		lblNewLabel_10.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		lblNewLabel_10.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		
		lblNewLabel = new Label(composite_1, SWT.NONE);
		lblNewLabel.setBounds(1050, 133, 18, 13);
		lblNewLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		lblNewLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		lblNewLabel_1 = new Label(composite_1, SWT.NONE);
		lblNewLabel_1.setBounds(1090, 130, 56, 15);
		lblNewLabel_1.setText("0");
		lblNewLabel_1.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		lblNewLabel_1.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		lblNewLabel_2 = new Label(composite_1, SWT.NONE);
		lblNewLabel_2.setBounds(1450, 133, 18, 13);
		lblNewLabel_2.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		lblNewLabel_2.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		lblNewLabel_3 = new Label(composite_1, SWT.NONE);
		lblNewLabel_3.setBounds(1490, 130, 56, 15);
		lblNewLabel_3.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		lblNewLabel_3.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Composite composite_21 = new Composite(composite, SWT.NONE);
		composite_21.setLayout(new GridLayout(3, false));
		composite_21.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
				
				Composite composite_22 = new Composite(composite_21, SWT.NONE);
				GridData gd_composite_22 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
				gd_composite_22.widthHint = 200;
				composite_22.setLayoutData(gd_composite_22);
				composite_22.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
				
				canvas = new Canvas(composite_21, SWT.NONE);
				canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
				canvas.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseDown(MouseEvent e) {
						selectedTagList.clear();
						for (Ap ap : apList) {
							if(ap.getX()  < e.x && e.x < ap.getX() + 100 && ap.getY()  < e.y && e.y < ap.getY() + 100) {
								selectedPoint.x = ap.getX();
								selectedPoint.y = ap.getY();
								for (Tags tag1 : tagList) {
									
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
										if(sosCount.get(ap.getApid()) > 0) {	// SOS
											event.gc.drawImage(image_sos, ap.getX(), ap.getY());
											event.gc.setBackground(new Color(event.display, new RGB(242, 165, 0)));
										}else if(ap.getBatt() < 0.3 && ap.getAct() == 2) {	// Low Battery
											event.gc.drawImage(image_lowbattery, ap.getX(), ap.getY());
											event.gc.setBackground(new Color(event.display, new RGB(242, 165, 0)));
										}else if(ap.getAct() == 2) {	// Active
											//event.gc.setBackground(new Color(event.display, new RGB(0, 255, 0)));
											event.gc.drawImage(image_active, ap.getX(), ap.getY());
											event.gc.setBackground(new Color(event.display, new RGB(48, 138, 249)));
										}else {	// InActive
											//event.gc.setBackground(new Color(event.display, new RGB(255, 0, 0)));
											event.gc.drawImage(image_inactive, ap.getX(), ap.getY());
											event.gc.setBackground(new Color(event.display, new RGB(167, 167, 167)));
										}
										//event.gc.fillOval(ap.getX()-25, ap.getY()-25, 50, 50);
										
										// AP ID ±×¸®±â
										//event.gc.setBackground(event.display.getSystemColor(SWT.COLOR_TRANSPARENT));
										event.gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
										event.gc.setFont(new Font(null, "¸¼Àº °íµñ", 16, SWT.NORMAL));
										event.gc.drawText("#"+ap.getApid(), ap.getX()+70, ap.getY()+110);
										// APÀÇ Tag °¹¼ö ±×¸®±â
										if(tagCount.get(ap.getApid()) != null) {
											event.gc.setFont(new Font(null, "¸¼Àº °íµñ", 32, SWT.NORMAL));
											event.gc.drawText(""+tagCount.get(ap.getApid()), ap.getX()+70, ap.getY()+60);
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
										event.gc.drawImage(image_popup_inactive_1, selectedPoint.x+50, selectedPoint.y);
										
										
										event.gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
										event.gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
										
										int fontHeight = 14;
										int pointX = 130;
										int pointY = 80;
										int row = 0;
										for (Tags tag : selectedTagList) {
											
											//event.gc.drawText("Tag:"+tag.getTagid()+" , RSSI:"+tag.getRssi(), selectedPoint.x+150, selectedPoint.y + pointY+20);
											event.gc.drawText(""+tag.getTagid() , selectedPoint.x+pointX, selectedPoint.y + pointY);
											row++;
											pointY = pointY + fontHeight;
											if(row == 10) {
												row = 0;
												pointX = pointX + 50;
												pointY = 80;
											}
										}
				
									}
								}
								
							}
						});
		
		Composite composite_2 = new Composite(composite, SWT.NONE);
		GridData gd_composite_2 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite_2.widthHint = 1800;
		gd_composite_2.heightHint = 150;
		composite_2.setLayoutData(gd_composite_2);
		composite_2.setLayout(new GridLayout(2, false));
		
		Composite composite_5 = new Composite(composite_2, SWT.NONE);
		GridData gd_composite_5 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_composite_5.minimumWidth = 999;
		gd_composite_5.heightHint = 142;
		gd_composite_5.widthHint = 999;
		composite_5.setLayoutData(gd_composite_5);
		composite_5.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		composite_5.setBackgroundImage(resourceManager.createImage(slice_page2_2));
		
		Label lblBack = new Label(composite_5, SWT.NONE);
		lblBack.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
		        MPerspective perspective = (MPerspective) modelService.find("dawin.ble.perspective.dashboard", app);
		        if (perspective != null) {
		            partService.switchPerspective(perspective);
		        }
			}
		});
		lblBack.setBounds(41, 50, 69, 54);
		lblBack.setText("");
		lblBack.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		lblApActive = new Label(composite_5, SWT.NONE);
		lblApActive.setFont(new Font(null, "¸¼Àº °íµñ", 34, SWT.NORMAL));
		lblApActive.setAlignment(SWT.CENTER);
		lblApActive.setBounds(230, 50, 41, 61);
		lblApActive.setText(" 8");
		lblApActive.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		lblApInactive = new Label(composite_5, SWT.NONE);
		lblApInactive.setFont(new Font(null, "¸¼Àº °íµñ", 34, SWT.NORMAL));
		lblApInactive.setAlignment(SWT.CENTER);
		lblApInactive.setBounds(340, 50, 41, 61);
		lblApInactive.setText(" 8");
		lblApInactive.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
				lblApLow = new Label(composite_5, SWT.NONE);
				lblApLow.setFont(new Font(null, "¸¼Àº °íµñ", 34, SWT.NORMAL));
				lblApLow.setAlignment(SWT.CENTER);
				lblApLow.setBounds(460, 50, 41, 61);
				lblApLow.setText(" 8");
				lblApLow.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
				
				lblTagActive = new Label(composite_5, SWT.NONE);
				lblTagActive.setFont(new Font(null, "¸¼Àº °íµñ", 34, SWT.NORMAL));
				lblTagActive.setAlignment(SWT.CENTER);
				lblTagActive.setBounds(660, 50, 41, 61);
				lblTagActive.setText(" 8");
				lblTagActive.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
				
				lblTagInactive = new Label(composite_5, SWT.NONE);
				lblTagInactive.setFont(new Font(null, "¸¼Àº °íµñ", 34, SWT.NORMAL));
				lblTagInactive.setAlignment(SWT.CENTER);
				lblTagInactive.setBounds(770, 50, 41, 61);
				lblTagInactive.setText(" 8");
				lblTagInactive.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
				
						lblTagLow = new Label(composite_5, SWT.NONE);
						lblTagLow.setFont(new Font(null, "¸¼Àº °íµñ", 34, SWT.NORMAL));
						lblTagLow.setAlignment(SWT.CENTER);
						lblTagLow.setBounds(880, 50, 41, 61);
						lblTagLow.setText(" 8");
						lblTagLow.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));

		
		Composite composite_9 = new Composite(composite_2, SWT.NONE);
		composite_9.setLayout(null);
		GridData gd_composite_9 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_composite_9.minimumWidth = 900;
		gd_composite_9.widthHint = 900;
		composite_9.setLayoutData(gd_composite_9);
		
		Composite composite_8 = new Composite(composite_9, SWT.NONE);
		composite_8.setBounds(100, 5, 311, 101);
		composite_8.setLayout(new GridLayout(1, false));
		composite_8.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		composite_8.setBackgroundImage(resourceManager.createImage(category_mote));
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

	int beforeTagCnt = 0;
	int beforeSosTagCnt = 0;
	Label lblNewLabel;
	Label lblNewLabel_1;
	Label lblNewLabel_2;
	Label lblNewLabel_3;

	@SuppressWarnings("unchecked")
	public void refreshSensorList() {
		em.clear();
		em.getEntityManagerFactory().getCache().evictAll();
        Query qMaxTime = em.createQuery("select t from Tags t order by t.time desc");
        qMaxTime.setFirstResult(0);
        qMaxTime.setMaxResults(1);
        Tags tag = (Tags) qMaxTime.getSingleResult();

        // ÅÂ±× ¸®½ºÆ® Áß Áßº¹ Á¦°Å 
        Query qTags = em.createQuery("select t from Tags t where t.time = :time");
        qTags.setParameter("time", tag.getTime());
        List<Tags> tagListTemp = qTags.getResultList();
        tagList.clear();
		for (Tags tag1 : tagListTemp) {
			int cnt = 0;
			for (Tags tag2 : tagList) {
				if(tag2.getTagid() == tag1.getTagid()) {
					if(tag1.getRssi() < tag2.getRssi()) { // tag1ÀÇ ½ÅÈ£°¡ ´õ ¼¼¸é
						tagList.remove(tag2);
						tagList.add(tag1);
					}
					cnt++;
					break;
				}
			}
			if(cnt == 0) {
				tagList.add(tag1);
			}
		}
       
		beforeTagCnt = activeTagCnt;
		beforeSosTagCnt = sosTagCnt;
		
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
//		for (Ap ap : apList) {
//			System.out.println(ap.getApid() +":" +ap.getAct());
//		}
		
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

        sosCount.clear();
		for (Ap ap : apList) {
			int count = 0;
			for (Tags tag1 : tagList) {
				if(ap.getApid() == tag1.getApid() && tag1.getSos() != 0) {
					count++;
				}
			}
			//if(count > 0) {
			sosCount.put(ap.getApid(), count);
			//}
		}
		

		// È­¸é ¾÷µ¥ÀÌÆ®

		if(!canvas.isDisposed()) {
			sync.syncExec(()->{
				//String apStatus = "È°¼º : " + activeCnt + " | ºñÈ°¼º : " + inactiveCnt;
				//String tagStatus = "È°¼º : " + activeTagCnt + " | SOS : " + sosTagCnt;

				lblNewLabel_7.setText(dateFormatDate.format(new Date( tag.getTime().getTime() ) ));
				lblNewLabel_8.setText(dateFormat.format(new Date( tag.getTime().getTime() ) ));
				
				lblNewLabel_19.setText(activeTagCnt+"");
				lblNewLabel_24.setText(sosTagCnt+"");
				
				lblApActive.setText(activeCnt+"");
				lblApInactive.setText(inactiveCnt+"");
				lblApLow.setText(0+"");

				lblTagActive.setText(activeTagCnt+"");
				lblTagInactive.setText(0+"");
				lblTagLow.setText(0+"");
				
				if(activeTagCnt == beforeTagCnt) {
					lblNewLabel.setText("-");
					lblNewLabel.setBackgroundImage(null);
				}else if(activeTagCnt > beforeTagCnt) {
					lblNewLabel.setText("");
					lblNewLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
					lblNewLabel.setBackgroundImage(resourceManager.createImage(Triangle));
				}else {
					lblNewLabel.setText("");
					lblNewLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
					lblNewLabel.setBackgroundImage(resourceManager.createImage(Triangle2));
				}
				lblNewLabel_1.setText( "" + Math.abs(activeTagCnt - beforeTagCnt) );

				if(sosTagCnt == beforeSosTagCnt) {
					lblNewLabel_2.setText("-");
					lblNewLabel_2.setBackgroundImage(null);
				}else if(sosTagCnt > beforeSosTagCnt) {
					lblNewLabel_2.setText("");
					lblNewLabel_2.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
					lblNewLabel_2.setBackgroundImage(resourceManager.createImage(Triangle));
				}else {
					lblNewLabel_2.setText("");
					lblNewLabel_2.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
					lblNewLabel_2.setBackgroundImage(resourceManager.createImage(Triangle2));
				}
				lblNewLabel_3.setText( "" + Math.abs(sosTagCnt - beforeSosTagCnt) );

			});
			sync.syncExec(()->{
				canvas.redraw();
//				pushSession.stop();
			});

		}

	}
}