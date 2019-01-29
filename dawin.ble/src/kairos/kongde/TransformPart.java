
package kairos.kongde;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import org.eclipse.swt.widgets.Composite;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;

import kairos.kongde.entity.Topic;
import kairos.kongde.entity.Transform;
import kairos.kongde.entity.TransformDetail;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.widgets.Button;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class TransformPart {

	@SuppressWarnings("serial")
	private static class ViewerLabelProvider_1 extends LabelProvider {	// combo
		public Image getImage(Object element) {
			return super.getImage(element);
		}
		public String getText(Object element) {
			Topic topic = (Topic) element;
            return topic.getName();
		}
	}
//	@SuppressWarnings("serial")
//	private static class ViewerLabelProvider extends LabelProvider {
//		public Image getImage(Object element) {
//			return super.getImage(element);
//		}
//		public String getText(Object element) {
//			//return super.getText(element);
//			Topic topic = (Topic) element;
//            return topic.getName();
//		}
//	}

	private Transform transform;
	
	private Table table;
	private Text text_length;
	private Text text_remark;
	private Text textName;
	private Composite composite_5 ;
	TableViewer tableViewer;
	ComboViewer comboViewer;
	Button btnCheckActive;
//	VoTopic[] topics = new VoTopic[] {	new VoTopic(0, "Topic 1"),
//			new VoTopic(1, "Topic 2"),
//			new VoTopic(2, "Topic 3") };
	
	Topic[] topicColumnType = new Topic[] {	new Topic(0, "Fixed"),new Topic(1, "Flexible") };

    //EntityManagerFactory emf = Persistence.createEntityManagerFactory("kairos.kongde");
    //EntityManager em = emf.createEntityManager();
	EntityManager em;
	
	int selectedIndex = 0;
	int selectedDetailIndex = 0;
	private Text txtSeq;
	private Text textTo;
	private Text textFrom;

	Transfer[] transfers  = new Transfer[] { LocalSelectionTransfer.getTransfer() };
	int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;

	private Text text_offset;

	Job job;

	@Inject
	private EPartService partService;

	@SuppressWarnings("serial")
	@PostConstruct
	public void postConstruct(Composite parent) {
		em = e4Application.em;
		
		TabFolder tabFolder = new TabFolder(parent, SWT.NONE);
		tabFolder.setBounds(0, 0, 124, 43);
		
		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("Attribute");
		
		Composite composite_3 = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(composite_3);
		GridLayout gl_composite_3 = new GridLayout(2, false);
		gl_composite_3.marginHeight = 25;
		composite_3.setLayout(gl_composite_3);
		
		Label lblName = new Label(composite_3, SWT.NONE);
		lblName.setAlignment(SWT.RIGHT);
		GridData gd_lblName = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblName.widthHint = 100;
		lblName.setLayoutData(gd_lblName);
		lblName.setText("Name");
		
		textName = new Text(composite_3, SWT.BORDER);
		textName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblFromTopic = new Label(composite_3, SWT.NONE);
		lblFromTopic.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFromTopic.setText("From Topic");
		
		textFrom = new Text(composite_3, SWT.BORDER);
		textFrom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		textFrom.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent event) {
				if(!transform.getFromTopic().equals(textFrom.getText())) {
					transform.setFromTopic(textFrom.getText());
					//transform.setFromTopic(textFrom.getText());
					//transform.setToTopic(textTo.getText());
					
					em.getTransaction().begin();
					em.merge(transform);
					em.getTransaction().commit();
				}
			}
		});
		textFrom.setText("");
		DropTarget target = new DropTarget(textFrom, operations);
		
		target.setTransfer(transfers);
				
		Label lblToTopic = new Label(composite_3, SWT.NONE);
		lblToTopic.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblToTopic.setText("To Topic");
		
				textTo = new Text(composite_3, SWT.BORDER);
				textTo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
				textTo.addFocusListener(new FocusAdapter() {
					@Override
					public void focusLost(FocusEvent event) {
						if(!transform.getToTopic().equals(textTo.getText())) {
							transform.setToTopic(textTo.getText());
							//transform.setFromTopic(textFrom.getText());
							//transform.setToTopic(textTo.getText());
							
							em.getTransaction().begin();
							em.merge(transform);
							em.getTransaction().commit();
						}
					}
				});
				
				DropTarget targetTo = new DropTarget(textTo, operations);
				
				targetTo.setTransfer(transfers);
				new Label(composite_3, SWT.NONE);
				new Label(composite_3, SWT.NONE);
				
				Label lblActive = new Label(composite_3, SWT.NONE);
				lblActive.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
				lblActive.setText("Active");
				
				
				btnCheckActive = new Button(composite_3, SWT.CHECK);
				btnCheckActive.setSelection(true);
				btnCheckActive.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						Button btn = (Button) e.getSource();
						transform.setActive(btn.getSelection());
						em.getTransaction().begin();
						em.merge(transform);
						em.getTransaction().commit();
						if(btn.getSelection()) {
							
							e4Application.transformJobManager.start(transform);
						}else {
							e4Application.transformJobManager.stop(transform);
						}
					}
				});
				
				TabItem tbtmTelegramConfig = new TabItem(tabFolder, SWT.NONE);
				tbtmTelegramConfig.setText("Telegram Config");
				
				Composite composite = new Composite(tabFolder, SWT.NONE);
				tbtmTelegramConfig.setControl(composite);
				composite.setLayout(new GridLayout(1, false));
				
				tableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
				tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event) {
						IStructuredSelection selection = (IStructuredSelection) event.getSelection();
						if (selection.size() > 0) {
							selectedDetailIndex = tableViewer.getTable().getSelectionIndex();
		
							//selectedIndex = tableViewer.getList().getSelectionIndex();
		
							IStructuredSelection structuredSelection = (IStructuredSelection) selection;
							TransformDetail transformDetail = (TransformDetail) structuredSelection.getFirstElement();
							
							txtSeq.setText(String.valueOf(transformDetail.getSeq()) );
							comboViewer.setSelection(new StructuredSelection(comboViewer.getElementAt(transformDetail.getType())));
							text_offset.setText(String.valueOf(transformDetail.getOffset()));
							text_length.setText(String.valueOf(transformDetail.getLength()));
							text_remark.setText(transformDetail.getRemark());
							
							
								
						}
					}
				});
				table = tableViewer.getTable();
				table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
				table.setHeaderVisible(true);
				
				TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(tableViewer, SWT.NONE);
				tableViewerColumn_3.setLabelProvider(new ColumnLabelProvider() {
					public Image getImage(Object element) {
						// TODO Auto-generated method stub
						return null;
					}
					public String getText(Object element) {
						TransformDetail vo = (TransformDetail)element;
						return element == null ? "" : vo.getTransformId()+"";
					}
				});
				TableColumn tblclmnId = tableViewerColumn_3.getColumn();
				tblclmnId.setText("ID");
				
				TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
				tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
					public Image getImage(Object element) {
						// TODO Auto-generated method stub
						return null;
					}
					public String getText(Object element) {
						TransformDetail vo = (TransformDetail)element;
						return element == null ? "" : vo.getSeq()+"";
					}
				});
				TableColumn tblclmnOrder = tableViewerColumn.getColumn();
				tblclmnOrder.setAlignment(SWT.CENTER);
				tblclmnOrder.setWidth(100);
				tblclmnOrder.setText("Seq");
				
				TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
				tableViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {
					public Image getImage(Object element) {
						// TODO Auto-generated method stub
						return null;
					}
					public String getText(Object element) {
						TransformDetail vo = (TransformDetail)element;
						//return element == null ? "" : vo.getType()+"";
						return vo.getType() == 0 ? "Fixed" : "Flexible";
					}
				});
				TableColumn tblclmnType = tableViewerColumn_1.getColumn();
				tblclmnType.setAlignment(SWT.CENTER);
				tblclmnType.setWidth(100);
				tblclmnType.setText("Type");
				
				TableViewerColumn tableViewerColumn_9 = new TableViewerColumn(tableViewer, SWT.NONE);
				tableViewerColumn_9.setLabelProvider(new ColumnLabelProvider() {
					public Image getImage(Object element) {
						return null;
					}
					public String getText(Object element) {
						TransformDetail vo = (TransformDetail)element;
						return element == null ? "" : vo.getOffset()+"";
					}
				});
				TableColumn tblclmnOffset = tableViewerColumn_9.getColumn();
				tblclmnOffset.setAlignment(SWT.CENTER);
				tblclmnOffset.setWidth(100);
				tblclmnOffset.setText("Offset");
				
				TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(tableViewer, SWT.NONE);
				tableViewerColumn_2.setLabelProvider(new ColumnLabelProvider() {
					public Image getImage(Object element) {
						return null;
					}
					public String getText(Object element) {
						TransformDetail vo = (TransformDetail)element;
						return element == null ? "" : vo.getLength()+"";
					}
				});
				TableColumn tblclmnLength = tableViewerColumn_2.getColumn();
				tblclmnLength.setAlignment(SWT.CENTER);
				tblclmnLength.setWidth(100);
				tblclmnLength.setText("Length");
				
				TableViewerColumn tableViewerColumn_4 = new TableViewerColumn(tableViewer, SWT.NONE);
				tableViewerColumn_4.setLabelProvider(new ColumnLabelProvider() {
					public Image getImage(Object element) {
						return null;
					}
					public String getText(Object element) {
						TransformDetail vo = (TransformDetail)element;
						return element == null ? "" : vo.getRemark();
					}
				});
				TableColumn tblclmnNewColumn = tableViewerColumn_4.getColumn();
				tblclmnNewColumn.setWidth(250);
				tblclmnNewColumn.setText("Remark");
				tableViewer.setContentProvider(ArrayContentProvider.getInstance());
				//int dndOperations = DND.DROP_COPY | DND.DROP_MOVE;
				tableViewer.addDragSupport ( operations, transfers, new DragSourceListener() {
					
					@Override
					public void dragStart(DragSourceEvent event) {
						//System.out.println("Start Drag");
						
					}
					
					@Override
					public void dragSetData(DragSourceEvent event) {
						//System.out.println("dragSetData");
		//				IStructuredSelection selection = (IStructuredSelection)LocalSelectionTransfer.getTransfer().getSelection();
		//				
						LocalSelectionTransfer transfer=LocalSelectionTransfer.getTransfer();
						if (transfer.isSupportedType(event.dataType)) {
							transfer.setSelection(tableViewer.getSelection());
						}
					}
					
					@Override
					public void dragFinished(DragSourceEvent event) {
						//System.out.println("Finish Drag");
						
					}
				} );
				
		
				tableViewer.addDropSupport ( operations, transfers, new TransformDropListener(tableViewer));
				//////////////
				
		
				composite_5 = new Composite(composite, SWT.NONE);
				composite_5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
				composite_5.setSize(616, 208);
				composite_5.setLayout(new GridLayout(1, false));
				
				Composite composite_1 = new Composite(composite_5, SWT.NONE);
				composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
				composite_1.setLayout(new GridLayout(5, false));
				
				txtSeq = new Text(composite_1, SWT.BORDER | SWT.CENTER);
				txtSeq.setText("Seq");
				txtSeq.setEditable(false);
				GridData gd_txtSeq = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
				gd_txtSeq.widthHint = 70;
				txtSeq.setLayoutData(gd_txtSeq);
				
				comboViewer = new ComboViewer(composite_1, SWT.NONE);
				Combo combo_type = comboViewer.getCombo();
				GridData gd_combo_type = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
				gd_combo_type.widthHint = 100;
				combo_type.setLayoutData(gd_combo_type);
				comboViewer.setLabelProvider(new ViewerLabelProvider_1());
				comboViewer.setContentProvider(ArrayContentProvider.getInstance());
				comboViewer.setInput(topicColumnType);
				
				text_offset = new Text(composite_1, SWT.BORDER);
				GridData gd_text_offset = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
				gd_text_offset.widthHint = 80;
				text_offset.setLayoutData(gd_text_offset);
				
				text_length = new Text(composite_1, SWT.BORDER);
				text_length.setSize(109, 31);
				GridData gd_text_length = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
				gd_text_length.widthHint = 80;
				text_length.setLayoutData(gd_text_length);
				
				text_remark = new Text(composite_1, SWT.BORDER);
				GridData gd_text_remark = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
				gd_text_remark.widthHint = 80;
				text_remark.setLayoutData(gd_text_remark);
				
				Composite composite_2 = new Composite(composite_5, SWT.NONE);
				composite_2.setLayout(new GridLayout(4, false));
				
				Button button_2 = new Button(composite_2, SWT.NONE);
				button_2.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
					}
				});
				button_2.setText("Refresh");
				
				Button btnNewButton = new Button(composite_2, SWT.NONE);
				btnNewButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						if(text_length.getText().length() > 0 && text_remark.getText().length() > 0) {
							
							TransformDetail transformDetail = new TransformDetail();
							transformDetail.setTransformId(transform.getId());
							transformDetail.setSeq(tableViewer.getTable().getItemCount());
							transformDetail.setType(((Topic)comboViewer.getStructuredSelection().getFirstElement()).getId());	// topic 객체 임시로 사용
							transformDetail.setOffset(Integer.parseInt( text_offset.getText()) );
							transformDetail.setLength(Integer.parseInt( text_length.getText()) );
							transformDetail.setRemark(text_remark.getText());
							em.getTransaction().begin();
							em.persist(transformDetail);
							em.getTransaction().commit();
							
							refreshTransformDetailList();
							
							tableViewer.setSelection(new StructuredSelection(tableViewer.getElementAt(tableViewer.getTable().getItemCount()-1)),true);
		
						}else {
							MessageDialog.openError(textName.getShell(), "Error", "Transform Detail (length or Remark) is empty.");
						}
					}
				});
				GridData gd_btnNewButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
				gd_btnNewButton.widthHint = 80;
				btnNewButton.setLayoutData(gd_btnNewButton);
				btnNewButton.setText("Add");
				
				Button btnNewButton_1 = new Button(composite_2, SWT.NONE);
				btnNewButton_1.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
						if (selection.size() > 0) {
							
							IStructuredSelection structuredSelection = (IStructuredSelection) selection;
							TransformDetail transformDetail = (TransformDetail) structuredSelection.getFirstElement();
							
							em.getTransaction().begin();
							em.remove(transformDetail);
							em.getTransaction().commit();
							
							refreshTransformDetailList(true);
							
							
							if(tableViewer.getTable().getItemCount() == 0) {
								txtSeq.setText("");
								comboViewer.setSelection(new StructuredSelection(comboViewer.getElementAt(0)));
								text_offset.setText("");
								text_length.setText("");
								text_remark.setText("");
							}else if(selectedDetailIndex == tableViewer.getTable().getItemCount()) {
								//list.setSelection(selectedIndex-1);
								tableViewer.setSelection(new StructuredSelection(tableViewer.getElementAt(selectedDetailIndex-1)),true);
							}else {
								//list.setSelection(selectedIndex);
								tableViewer.setSelection(new StructuredSelection(tableViewer.getElementAt(selectedDetailIndex)),true);
							}
						}else {
							MessageDialog.openWarning(parent.getShell(), "Warning", "Please select a Transform first.");
						}
					}
				});
				GridData gd_btnNewButton_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
				gd_btnNewButton_1.widthHint = 80;
				btnNewButton_1.setLayoutData(gd_btnNewButton_1);
				btnNewButton_1.setText("Delete");
				
				Button button_3 = new Button(composite_2, SWT.NONE);
				button_3.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
						if (selection.size() > 0) {
							IStructuredSelection structuredSelection = (IStructuredSelection) selection;
							TransformDetail transformDetail = (TransformDetail) structuredSelection.getFirstElement();
							
							//topicDetail.setSeq();
							transformDetail.setType(((Topic)comboViewer.getStructuredSelection().getFirstElement()).getId());
							transformDetail.setOffset(Integer.parseInt(text_offset.getText()));
							transformDetail.setLength(Integer.parseInt(text_length.getText()));
							transformDetail.setRemark(text_remark.getText());
							
							em.getTransaction().begin();
							em.merge(transformDetail);
							em.getTransaction().commit();

							refreshTransformDetailList();
		
							tableViewer.setSelection(new StructuredSelection(tableViewer.getElementAt(selectedDetailIndex)),true);
		
						}else {
							MessageDialog.openWarning(parent.getShell(), "Warning", "Please select a transform first.");
						}
					}
				});
				button_3.setText("Update");
				targetTo.addDropListener(new DropTargetListener() {
					
					@Override
					public void dropAccept(DropTargetEvent event) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void drop(DropTargetEvent event) {
						//String text = (String)event.data;
						IStructuredSelection selection = (IStructuredSelection) event.data;
						if (selection.size() > 0) {
							IStructuredSelection structuredSelection = (IStructuredSelection) selection;
							Topic element = (Topic) structuredSelection.getFirstElement();
							textTo.setText(element.getName());
							
							transform.setToTopic(element.getName());
							em.getTransaction().begin();
							em.merge(transform);
							em.getTransaction().commit();
						}
					}
					
					@Override
					public void dragOver(DropTargetEvent event) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void dragOperationChanged(DropTargetEvent event) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void dragLeave(DropTargetEvent event) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void dragEnter(DropTargetEvent event) {
						// TODO Auto-generated method stub
						
					}
				});
		target.addDropListener(new DropTargetListener() {
			
			@Override
			public void dropAccept(DropTargetEvent event) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void drop(DropTargetEvent event) {
				//String text = (String)event.data;
				IStructuredSelection selection = (IStructuredSelection) event.data;
				if (selection.size() > 0) {
					IStructuredSelection structuredSelection = (IStructuredSelection) selection;
					Topic element = (Topic) structuredSelection.getFirstElement();
					textFrom.setText(element.getName());
					
					transform.setFromTopic(element.getName());
					em.getTransaction().begin();
					em.merge(transform);
					em.getTransaction().commit();
				}
			}
			
			@Override
			public void dragOver(DropTargetEvent event) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void dragOperationChanged(DropTargetEvent event) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void dragLeave(DropTargetEvent event) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void dragEnter(DropTargetEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
		textName.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent event) {
				if(!transform.getName().equals(textName.getText())) {
					transform.setName(textName.getText());
					//transform.setFromTopic(textFrom.getText());
					//transform.setToTopic(textTo.getText());
					
					em.getTransaction().begin();
					em.merge(transform);
					em.getTransaction().commit();
					
					MPart part =partService.findPart("kairos.kongde.part.config");
					ConfigPart configPart = (ConfigPart)part.getObject();
					configPart.refreshConfig();
				}

			}
		});
		
		
		
	}

	@PreDestroy
	public void preDestroy() {
		em.close();
	}

	@Focus
	public void onFocus() {
		
	}

	@Persist
	public void save() {
		
	}

	
	public void refreshTransformDetailList(boolean reorder) {
		int id = transform.getId() ;

		Query q = em.createQuery("select t from TransformDetail t where t.transformId = :transformId order by t.seq");
        q.setParameter("transformId", id);
		@SuppressWarnings("unchecked")
		java.util.List<TransformDetail> transformDetails = q.getResultList();

		int i = 0;
		for (TransformDetail transformDetail2 : transformDetails) {
			//System.out.println(transformDetail2.getSeq());
			transformDetail2.setSeq(i);
			em.getTransaction().begin();
			em.merge(transformDetail2);
			em.getTransaction().commit();
			i++;
		}
		refreshTransformDetailList();

	}
	public void refreshTransformDetailList() {
		int id = transform.getId() ;

		Query q = em.createQuery("select t from TransformDetail t where t.transformId = :transformId order by t.seq");
        q.setParameter("transformId", id);
		@SuppressWarnings("unchecked")
		java.util.List<TransformDetail> transformDetails = q.getResultList();

		tableViewer.setInput(transformDetails);
		

	}

	@SuppressWarnings("serial")
	public class TransformDropListener extends ViewerDropAdapter{

		int index = 0;
		protected TransformDropListener(Viewer viewer) {
			super(viewer);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void drop(DropTargetEvent event) {
			int location = this.determineLocation(event);
			TransformDetail target = (TransformDetail) determineTarget(event);
	        //String translatedLocation ="";
	        switch (location){
	        case 1 :
	            //translatedLocation = "Dropped before the target ";
	            if(target.getSeq() == 0) {
		            index = 0;
	            }else {
		            index = target.getSeq();
	            }
	            break;
	        case 2 :
	            //translatedLocation = "Dropped after the target ";
	            index = target.getSeq() + 1;
	            break;
	        case 3 :
	            //translatedLocation = "Dropped on the target ";
	            index = target.getSeq() ;
	            break;
	        case 4 :
	            //translatedLocation = "Dropped into nothing ";
	            //this.getViewer()
	            index = tableViewer.getTable().getItemCount()-1;
	            break;
	        }
	        //System.out.println(translatedLocation);
	        //System.out.println("The drop was done on the element: " + target.getSeq() );

			super.drop(event);
		}

		@Override
		public boolean performDrop(Object data) {
			IStructuredSelection selection = (IStructuredSelection) data;
			if (selection.size() > 0) {
				IStructuredSelection structuredSelection = (IStructuredSelection) selection;
				TransformDetail element = (TransformDetail) structuredSelection.getFirstElement();
				
				@SuppressWarnings("unchecked")
				List<TransformDetail> transformDetails =  (List<TransformDetail>) tableViewer.getInput();
				//System.out.println("Source Seq :" +element.getSeq());
				int srcIndex = element.getSeq();
				if(index <= srcIndex) {
					TransformDetail item = transformDetails.get(srcIndex);
					transformDetails.remove(srcIndex);
					transformDetails.add(index, item);
				}else {
					TransformDetail item = transformDetails.get(srcIndex);
					transformDetails.remove(srcIndex);
					transformDetails.add(index -1, item);
				}
				
				int i = 0;
				for (TransformDetail transformDetail2 : transformDetails) {
					//System.out.println(transformDetail2.getSeq());
					transformDetail2.setSeq(i);
					em.getTransaction().begin();
					em.merge(transformDetail2);
					em.getTransaction().commit();
					i++;
				}
				
				selectedDetailIndex = index;
				refreshTransformDetailList();
				tableViewer.setSelection(new StructuredSelection(tableViewer.getElementAt(selectedDetailIndex)),true);

			}
			return false;
		}

		@Override
		public boolean validateDrop(Object target, int operation, TransferData transferType) {
			// TODO Auto-generated method stub
			return true;
		}
		
	}
	
	public void init(Transform transform) {
		this.transform = transform;
		
		textName.setText(transform.getName());
		textFrom.setText(transform.getFromTopic());
		textTo.setText(transform.getToTopic());
		
		btnCheckActive.setSelection(transform.getActive());
		refreshTransformDetailList();
	}
	
//	private void update() {
//		transform.setName(textName.getText());
//		transform.setFromTopic(textFrom.getText());
//		transform.setToTopic(textTo.getText());
//		
//		em.getTransaction().begin();
//		em.merge(transform);
//		em.getTransaction().commit();
//		
//	}
	
}

class TransformJobManager{
	List<TransformJob> transformJobList = new ArrayList<>();
	
	public void add(Transform transform) {
		TransformJob transformJob = new TransformJob(transform);
		transformJobList.add(transformJob);
		transformJob.start();
	}
	public void remove(Transform transform) {
		//transformJobList
		for (TransformJob transformJob : transformJobList) {
			if(transformJob.getTransform().getId() == transform.getId()) {
				transformJob.stop();
				transformJobList.remove(transformJob);
				break;
			}
		}
	}
	public void stop(Transform transform) {
		//transformJobList
		for (TransformJob transformJob : transformJobList) {
			if(transformJob.getTransform().getId() == transform.getId()) {
				transformJob.setTransform(transform);
				transformJob.stop();
				break;
			}
		}
	}
	public void start(Transform transform) {
		//transformJobList
		for (TransformJob transformJob : transformJobList) {
			if(transformJob.getTransform().getId() == transform.getId()) {
				transformJob.setTransform(transform);
				transformJob.start();
				break;
			}
		}
	}
}

class TransformJob implements MqttCallback {
	Transform transform;
	MqttClient mqttClient;
	
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("kairos.kongde");
    EntityManager em = emf.createEntityManager();

	public TransformJob(Transform transform) {
		super();
		this.transform = transform;
	}

	
	public Transform getTransform() {
		return transform;
	}


	public void setTransform(Transform transform) {
		this.transform = transform;
	}


	@Override
	public void connectionLost(Throwable cause) {
		// TODO Auto-generated method stub
		
	}
	ByteArrayOutputStream output = new ByteArrayOutputStream();
	byte[] buffer = new byte[500];
	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		System.out.println("["+ transform.getId()+"from]" + message.toString());
		byte[] payload = message.getPayload();
		output.reset();
		
		
		for (TransformDetail transformDetail : transformDetails) {
			System.arraycopy(payload, transformDetail.getOffset(), buffer, 0, transformDetail.getLength());
			output.write(buffer, 0, transformDetail.getLength());
		}
		

		MqttMessage messagePub = new MqttMessage(output.toByteArray());
//        message.setQos(0);
		System.out.println("["+ transform.getId()+"to  ]" + messagePub.toString());
		mqttClient.publish(transform.getToTopic(), messagePub);
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub
		
	}
	
	List<TransformDetail> transformDetails;
	@SuppressWarnings("unchecked")
	public void start() {
		if(transform.getActive()) {
			try {
				em.clear();
				Query q = em.createQuery("select t from TransformDetail t where t.transformId = :transformId order by t.seq");
		        q.setParameter("transformId", transform.getId());

				transformDetails = q.getResultList();


				//client = new MqttClient("tcp://127.0.0.1:1883", MqttClient.generateClientId());
				//mqttClient = new MqttClient("tcp://192.168.0.210:1883", MqttClient.generateClientId());
				mqttClient = new MqttClient(Constant.BROKER_URI, "Transform"+transform.getId());
				//client = new MqttClient("tcp://192.168.0.210:1883", "guest");
				//MqttConnectOptions connOpts = new MqttConnectOptions();
				//connOpts.setPassword("user1".toCharArray());
				mqttClient.connect();
				mqttClient.setCallback(this);
				mqttClient.subscribe(transform.getFromTopic());
//		           MqttMessage message = new MqttMessage();
//		           message.setPayload("A single message from my computer fff".getBytes());
//		           client.publish("Kongde MQTT Test", message);
				
				
			}catch (MqttException e) {
				e.printStackTrace();
			}
		}
	}
	public void stop() {
		if(mqttClient.isConnected()) {
			try {
				mqttClient.unsubscribe(transform.getFromTopic());
				mqttClient.disconnect();
			} catch (MqttException e) {
				e.printStackTrace();
			}
		}
	}
}
