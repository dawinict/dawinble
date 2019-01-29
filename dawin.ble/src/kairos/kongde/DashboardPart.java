
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
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Color;
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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.custom.ScrolledComposite;

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
	
	static List<Tags> tagList;
	List<Tags> selectedTagList = new ArrayList<Tags>();
	Point selectedPoint = new Point(0, 0);
	static List<Ap> apList;
	
	
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
    
    
    Label lblApActive;
    Label lblApInactive;
    Label lblTagActive;
    Label lblTagInactive;
    Label lblAlertActive;
    Label lblAlertInactive;
   
    
    private Table table;
    TableViewer tableViewer;
    TableViewer tableViewer_1;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
	@PostConstruct
	public void postConstruct(Composite parent) {
		
		ResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources());
		Bundle bundle = FrameworkUtil.getBundle(this.getClass());
//        URL url = FileLocator.find(bundle, new Path("icons/floor.jpg"), null);
        URL url = FileLocator.find(bundle, new Path("icons/map.png"), null);
        ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(url);
		Image image = resourceManager.createImage(imageDescriptor);
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setBackground(new Color (Display.getCurrent(), 226, 228, 235));
		composite.setLayout(new GridLayout(1, false));
		
		Composite composite_4 = new Composite(composite, SWT.NONE);
		composite_4.setLayout(new GridLayout(2, false));
		GridData gd_composite4 = new GridData(SWT.FILL, SWT.FILL, false, false, 0, 1);
		gd_composite4.heightHint = 95;
		composite_4.setLayoutData(gd_composite4);
		composite_4.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Label lblNewLabel4 = new Label(composite_4, SWT.NONE);
		GridData gd_lblNewLabel4 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 2);
		gd_lblNewLabel4.heightHint = 95;
		gd_lblNewLabel4.widthHint = 93;
		lblNewLabel4.setLayoutData(gd_lblNewLabel4);
		lblNewLabel4.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		lblNewLabel4.setBackgroundImage(resourceManager.createImage(titleicon_dashboard));
		
		Label lblNewLabel = new Label(composite_4, SWT.NONE);
		GridData gd_lblNewLabel = new GridData(SWT.LEFT, SWT.BOTTOM, false, true, 1, 1);
		gd_lblNewLabel.heightHint = 50;
		lblNewLabel.setLayoutData(gd_lblNewLabel);
		lblNewLabel.setFont(new Font(null, "Courier New", 16, SWT.BOLD));
		lblNewLabel.setText("Dashboard");
		lblNewLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Label lblNewLabel_1 = new Label(composite_4, SWT.NONE);
		GridData gd_lblNewLabel_1 = new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1);
		gd_lblNewLabel_1.heightHint = 50;
		lblNewLabel_1.setLayoutData(gd_lblNewLabel_1);
		lblNewLabel.setFont(new Font(null, "¸¼Àº °íµñ", 16, SWT.NORMAL));
		lblNewLabel_1.setText("Mote Sensor devices status monitoring");
		lblNewLabel_1.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Composite composite_3 = new Composite(composite, SWT.NONE);
		composite_3.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		GridLayout gl_composite_3 = new GridLayout(3, true);
		gl_composite_3.marginWidth = 0;
		gl_composite_3.marginHeight = 0;
		gl_composite_3.horizontalSpacing = 0;
		gl_composite_3.verticalSpacing = 0;
		composite_3.setLayout(gl_composite_3);
		
		Composite composite_5 = new Composite(composite_3, SWT.NONE);
		GridLayout gl_composite_5 = new GridLayout(2, false);
		gl_composite_5.horizontalSpacing = 0;
		gl_composite_5.marginWidth = 0;
		composite_5.setLayout(gl_composite_5);
		GridData gd_composite_5 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_composite_5.minimumHeight = 246;
		gd_composite_5.minimumWidth = 539;
		gd_composite_5.heightHint = 246;
		gd_composite_5.widthHint = 539;
		composite_5.setLayoutData(gd_composite_5);
		composite_5.setBounds(0, 0, 64, 64);
		composite_5.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		composite_5.setBackgroundImage(resourceManager.createImage(box_dashboard));
		
		Composite composite_8 = new Composite(composite_5, SWT.NONE);
		composite_8.setLayout(new GridLayout(1, false));
		composite_8.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		composite_8.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Label lblNewLabel_2 = new Label(composite_8, SWT.NONE);
		GridData gd_lblNewLabel_2 = new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1);
		gd_lblNewLabel_2.heightHint = 103;
		gd_lblNewLabel_2.widthHint = 87;
		lblNewLabel_2.setLayoutData(gd_lblNewLabel_2);
		lblNewLabel_2.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		lblNewLabel_2.setBackgroundImage(resourceManager.createImage(icon_mote));
		
		Label lblNewLabel_3 = new Label(composite_8, SWT.NONE);
		lblNewLabel_3.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_3.setAlignment(SWT.CENTER);
		lblNewLabel_3.setText("Mote");
		
		Composite composite_9 = new Composite(composite_5, SWT.NONE);
		GridLayout gl_composite_9 = new GridLayout(2, true);
		gl_composite_9.marginRight = 50;
		composite_9.setLayout(gl_composite_9);
		composite_9.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		composite_9.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		lblApActive = new Label(composite_9, SWT.NONE);
		lblApActive.setFont(new Font(null, "¸¼Àº °íµñ", 34, SWT.NORMAL));
		lblApActive.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblApActive.setBounds(0, 0, 56, 15);
		lblApActive.setText("99");
		
		Label lblNewLabel_6 = new Label(composite_9, SWT.NONE);
		lblNewLabel_6.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1));
		lblNewLabel_6.setText("Active");
		
		Label label = new Label(composite_9, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		label.setBounds(0, 0, 64, 2);
		
		lblApInactive = new Label(composite_9, SWT.NONE);
		lblApInactive.setFont(new Font(null, "¸¼Àº °íµñ", 34, SWT.NORMAL));
		lblApInactive.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblApInactive.setBounds(0, 0, 56, 15);
		lblApInactive.setText("99");
		
		Label lblNewLabel_7 = new Label(composite_9, SWT.NONE);
		lblNewLabel_7.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblNewLabel_7.setText("Inactive");
		
		Composite composite_6 = new Composite(composite_3, SWT.NONE);
		composite_6.setLayout(new GridLayout(2, false));
		GridData gd_composite_6 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_composite_6.minimumHeight = 246;
		gd_composite_6.minimumWidth = 539;
		gd_composite_6.widthHint = 539;
		gd_composite_6.heightHint = 246;
		composite_6.setLayoutData(gd_composite_6);
		composite_6.setBounds(0, 0, 64, 64);
		composite_6.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		composite_6.setBackgroundImage(resourceManager.createImage(box_dashboard));
		
		
		Composite composite_10 = new Composite(composite_6, SWT.NONE);
		composite_10.setLayout(new GridLayout(1, false));
		composite_10.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		composite_10.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Label lblNewLabel_12 = new Label(composite_10, SWT.NONE);
		GridData gd_lblNewLabel_12 = new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1);
		gd_lblNewLabel_12.heightHint = 103;
		gd_lblNewLabel_12.widthHint = 101;
		lblNewLabel_12.setLayoutData(gd_lblNewLabel_12);
		lblNewLabel_12.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		lblNewLabel_12.setBackgroundImage(resourceManager.createImage(icon_sensor));
		
		Label lblNewLabel_13 = new Label(composite_10, SWT.NONE);
		lblNewLabel_13.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_13.setAlignment(SWT.CENTER);
		lblNewLabel_13.setText("Sensor");
		
		Composite composite_11 = new Composite(composite_6, SWT.NONE);
		GridLayout gl_composite_11 = new GridLayout(2, true);
		gl_composite_11.marginRight = 50;
		composite_11.setLayout(gl_composite_11);
		composite_11.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		composite_11.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		lblTagActive = new Label(composite_11, SWT.NONE);
		lblTagActive.setFont(new Font(null, "¸¼Àº °íµñ", 34, SWT.NORMAL));
		lblTagActive.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblTagActive.setBounds(0, 0, 56, 15);
		lblTagActive.setText("99");
		
		Label lblNewLabel_16 = new Label(composite_11, SWT.NONE);
		lblNewLabel_16.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1));
		lblNewLabel_16.setText("Active");
		
		Label label11 = new Label(composite_11, SWT.SEPARATOR | SWT.HORIZONTAL);
		label11.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		label11.setBounds(0, 0, 64, 2);
		
		lblTagInactive = new Label(composite_11, SWT.NONE);
		lblTagInactive.setFont(new Font(null, "¸¼Àº °íµñ", 34, SWT.NORMAL));
		lblTagInactive.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblTagInactive.setBounds(0, 0, 56, 15);
		lblTagInactive.setText("0 ");
		
		Label lblNewLabel_17 = new Label(composite_11, SWT.NONE);
		lblNewLabel_17.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblNewLabel_17.setText("Inactive");

		Composite composite_7 = new Composite(composite_3, SWT.NONE);
		composite_7.setLayout(new GridLayout(2, false));
		GridData gd_composite_7 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_composite_7.minimumHeight = 246;
		gd_composite_7.minimumWidth = 539;
		gd_composite_7.widthHint = 539;
		gd_composite_7.heightHint = 246;
		composite_7.setLayoutData(gd_composite_7);
		composite_7.setBounds(0, 0, 64, 64);
		composite_7.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		composite_7.setBackgroundImage(resourceManager.createImage(box_dashboard));

		Composite composite_12 = new Composite(composite_7, SWT.NONE);
		composite_12.setLayout(new GridLayout(1, false));
		composite_12.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		composite_12.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Label lblNewLabel_22 = new Label(composite_12, SWT.NONE);
		GridData gd_lblNewLabel_22 = new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1);
		gd_lblNewLabel_22.heightHint = 98;
		gd_lblNewLabel_22.widthHint = 93;
		lblNewLabel_22.setLayoutData(gd_lblNewLabel_22);
		lblNewLabel_22.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		lblNewLabel_22.setBackgroundImage(resourceManager.createImage(icon_Alert));
		
		Label lblNewLabel_23 = new Label(composite_12, SWT.NONE);
		lblNewLabel_23.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_23.setAlignment(SWT.CENTER);
		lblNewLabel_23.setText("Alert");
		
		Composite composite_13 = new Composite(composite_7, SWT.NONE);
		GridLayout gl_composite_13 = new GridLayout(2, true);
		gl_composite_13.marginRight = 50;
		composite_13.setLayout(gl_composite_13);
		composite_13.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		composite_13.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		lblAlertActive = new Label(composite_13, SWT.NONE);
		lblAlertActive.setFont(new Font(null, "¸¼Àº °íµñ", 34, SWT.NORMAL));
		lblAlertActive.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblAlertActive.setBounds(0, 0, 56, 15);
		lblAlertActive.setText("2 ");
		
		Label lblNewLabel_26 = new Label(composite_13, SWT.NONE);
		lblNewLabel_26.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1));
		lblNewLabel_26.setText("Active");
		
		Label label21 = new Label(composite_13, SWT.SEPARATOR | SWT.HORIZONTAL);
		label21.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		label21.setBounds(0, 0, 64, 2);
		
		lblAlertInactive = new Label(composite_13, SWT.NONE);
		lblAlertInactive.setFont(new Font(null, "¸¼Àº °íµñ", 34, SWT.NORMAL));
		lblAlertInactive.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblAlertInactive.setBounds(0, 0, 56, 15);
		lblAlertInactive.setText("0 ");
		
		Label lblNewLabel_27 = new Label(composite_13, SWT.NONE);
		lblNewLabel_27.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblNewLabel_27.setText("Inactive");
		//canvas.setBackgroundImage(image);

		
		Composite composite_14 = new Composite(composite, SWT.NONE);
		composite_14.setLayout(new GridLayout(2, false));
		GridData gd_composite14 = new GridData(SWT.FILL, SWT.FILL, false, false, 0, 1);
		gd_composite14.heightHint = 95;
		composite_14.setLayoutData(gd_composite14);
		composite_14.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Label lblNewLabel14 = new Label(composite_14, SWT.NONE);
		GridData gd_lblNewLabel14 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 2);
		gd_lblNewLabel14.heightHint = 70;
		gd_lblNewLabel14.widthHint = 102;
		lblNewLabel14.setLayoutData(gd_lblNewLabel14);
		lblNewLabel14.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		lblNewLabel14.setBackgroundImage(resourceManager.createImage(titleicon_Network));
		
		Label lblNewLabel15 = new Label(composite_14, SWT.NONE);
		GridData gd_lblNewLabel15 = new GridData(SWT.LEFT, SWT.BOTTOM, false, true, 1, 1);
		gd_lblNewLabel15.heightHint = 50;
		lblNewLabel15.setLayoutData(gd_lblNewLabel15);
		lblNewLabel15.setFont(new Font(null, "Courier New", 16, SWT.BOLD));
		lblNewLabel15.setText("Network / Mote Statistics");
		lblNewLabel15.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Label lblNewLabel_15 = new Label(composite_14, SWT.NONE);
		GridData gd_lblNewLabel_15 = new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1);
		gd_lblNewLabel_15.heightHint = 50;
		lblNewLabel_15.setLayoutData(gd_lblNewLabel_15);
		lblNewLabel_15.setFont(new Font(null, "¸¼Àº °íµñ", 16, SWT.NORMAL));
		lblNewLabel_15.setText("Network statistics data monitoring");
		lblNewLabel_15.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TRANSPARENT));
		
		Composite composite_2 = new Composite(composite, SWT.NONE);
		GridData gd_composite_2 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite_2.heightHint = 130;
		composite_2.setLayoutData(gd_composite_2);
		composite_2.setLayout(new GridLayout(1, false));
		
		tableViewer_1 = new TableViewer(composite_2, SWT.BORDER | SWT.FULL_SELECTION);
		table_1 = tableViewer_1.getTable();
		table_1.setHeaderVisible(true);
		GridData gd_table_1 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
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
		tblclmnNewColumn_5.setWidth(100);
		tblclmnNewColumn_5.setText("Mote #");
		
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
		tblclmnNewColumn_6.setWidth(200);
		tblclmnNewColumn_6.setText("Remark");
		
		TableViewerColumn tableViewerColumn_8 = new TableViewerColumn(tableViewer_1, SWT.NONE);
		tableViewerColumn_8.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				return element == null ? "" :((Ap)element).getAct() +""  ;
			}
		});
		TableColumn tblclmnNewColumn_7 = tableViewerColumn_8.getColumn();
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
		tblclmnNewColumn_8.setWidth(100);
		tblclmnNewColumn_8.setText("Battery");
		tableViewer_1.setContentProvider(new ContentProvider_1());
		
		Composite composite_1 = new Composite(composite, SWT.V_SCROLL);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite_1.setLayout(new GridLayout(1, false));

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
		tblclmnTime.setWidth(180);
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
		tblclmnNewColumn.setWidth(100);
		tblclmnNewColumn.setText("Mote #");
		
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
		tblclmnNewColumn_1.setWidth(100);
		tblclmnNewColumn_1.setText("Tag #");
		
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
		tblclmnNewColumn_2.setWidth(100);
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
		tblclmnNewColumn_3.setWidth(100);
		tblclmnNewColumn_3.setText("RSSI");
		
		TableViewerColumn tableViewerColumn_5 = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn_5.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				return element == null ? "" :((Tags)element).getSos() +""  ;
			}
		});
		TableColumn tblclmnNewColumn_4 = tableViewerColumn_5.getColumn();
		tblclmnNewColumn_4.setWidth(100);
		tblclmnNewColumn_4.setText("SOS");
		tableViewer.setContentProvider(new ContentProvider());
		
//		ServerPushSession pushSession = new ServerPushSession();
		Runnable runnable = new Runnable() {
		
			@Override
			public void run() {
				while(true){
					em.clear();
					refreshSensorList();
					//System.out.println("ZZZZ");
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
	private Table table_1;

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

//		if(!canvas.isDisposed()) {
			sync.syncExec(()->{
				//String apStatus = "È°¼º : " + activeCnt + " | ºñÈ°¼º : " + inactiveCnt;
				//String tagStatus = "È°¼º : " + activeTagCnt + " | SOS : " + sosTagCnt;

				
				lblApActive.setText(activeCnt+"");
				lblApInactive.setText(inactiveCnt+"");

				lblTagActive.setText(activeTagCnt+"");
				
				tableViewer.setInput(tagList);
				//tableViewer.refresh();
				tableViewer_1.setInput(apList);
			});
//			sync.syncExec(()->{
//				canvas.redraw();
////				pushSession.stop();
//			});

//		}

	}
}