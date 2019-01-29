
package kairos.kongde;

import javax.annotation.PostConstruct;
import org.eclipse.swt.widgets.Composite;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;

import kairos.kongde.entity.Topic;
import kairos.kongde.entity.TopicDetail;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;

public class TopicPart {

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
	@SuppressWarnings("serial")
	private static class ViewerLabelProvider extends LabelProvider {
		public Image getImage(Object element) {
			return super.getImage(element);
		}
		public String getText(Object element) {
			//return super.getText(element);
			Topic topic = (Topic) element;
            return topic.getName();
		}
	}

	private Topic topic;
	
	private Table table;
	private Text text_length;
	private Text text_remark;
	private Text text_2;
	private Composite composite_5 ;
	TableViewer tableViewer;
	ComboViewer comboViewer;
	
//	VoTopic[] topics = new VoTopic[] {	new VoTopic(0, "Topic 1"),
//			new VoTopic(1, "Topic 2"),
//			new VoTopic(2, "Topic 3") };
	
	Topic[] topicColumnType = new Topic[] {	new Topic(0, "Fixed"),new Topic(1, "Flexible") };

    EntityManagerFactory emf = Persistence.createEntityManagerFactory("kairos.kongde");
    EntityManager em = emf.createEntityManager();

	int selectedIndex = 0;
	int selectedDetailIndex = 0;
	private Text txtSeq;

	@Inject
	private EPartService partService;

	
	@SuppressWarnings("serial")
	@PostConstruct
	public void postConstruct(Composite parent) {
		//listViewer.setInput(topics);

		TabFolder tabFolder = new TabFolder(parent, SWT.NONE);
		tabFolder.setBounds(0, 0, 183, 63);
		
		TabItem tbtmBasic = new TabItem(tabFolder, SWT.NONE);
		tbtmBasic.setText("Attribute");
		
		Composite composite_6 = new Composite(tabFolder, SWT.NONE);
		tbtmBasic.setControl(composite_6);
		GridLayout gl_composite_6 = new GridLayout(2, false);
		gl_composite_6.marginHeight = 25;
		composite_6.setLayout(gl_composite_6);
		
		Label lblName = new Label(composite_6, SWT.NONE);
		lblName.setAlignment(SWT.RIGHT);
		GridData gd_lblName = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblName.widthHint = 100;
		lblName.setLayoutData(gd_lblName);
		lblName.setText("Name");
		
		text_2 = new Text(composite_6, SWT.BORDER);
		text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_2.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent event) {
				topic.setName(text_2.getText());
				
				em.getTransaction().begin();
				em.merge(topic);
				em.getTransaction().commit();
				
				MPart part =partService.findPart("kairos.kongde.part.config");
				ConfigPart configPart = (ConfigPart)part.getObject();
				configPart.refreshConfig();
			}
		});
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		
		TabItem tbtmFormat = new TabItem(tabFolder, SWT.NONE);
		tbtmFormat.setText("Message Format");
		
		Composite composite_3 = new Composite(tabFolder, SWT.NONE);
		tbtmFormat.setControl(composite_3);
		composite_3.setLayout(new GridLayout(1, false));
		
		tableViewer = new TableViewer(composite_3, SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection.size() > 0) {
					selectedDetailIndex = tableViewer.getTable().getSelectionIndex();

					//selectedIndex = tableViewer.getList().getSelectionIndex();

					IStructuredSelection structuredSelection = (IStructuredSelection) selection;
					TopicDetail topicDetail = (TopicDetail) structuredSelection.getFirstElement();
					
					txtSeq.setText(String.valueOf(topicDetail.getSeq()) );
					comboViewer.setSelection(new StructuredSelection(comboViewer.getElementAt(topicDetail.getType())));
					text_length.setText(String.valueOf(topicDetail.getSize()));
					text_remark.setText(topicDetail.getRemark());
					
					
						
				}
			}
		});
		table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setSize(0, 0);
		table.setHeaderVisible(true);
		
		TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn_3.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				TopicDetail vo = (TopicDetail)element;
				return element == null ? "" : vo.getTopicId()+"";
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
				TopicDetail vo = (TopicDetail)element;
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
				TopicDetail vo = (TopicDetail)element;
				//return element == null ? "" : vo.getType()+"";
				return vo.getType() == 0 ? "Fixed" : "Flexible";
			}
		});
		TableColumn tblclmnType = tableViewerColumn_1.getColumn();
		tblclmnType.setAlignment(SWT.CENTER);
		tblclmnType.setWidth(100);
		tblclmnType.setText("Type");
		
		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn_2.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				TopicDetail vo = (TopicDetail)element;
				return element == null ? "" : vo.getSize()+"";
			}
		});
		TableColumn tblclmnLength = tableViewerColumn_2.getColumn();
		tblclmnLength.setAlignment(SWT.CENTER);
		tblclmnLength.setWidth(100);
		tblclmnLength.setText("Length");
		
		TableViewerColumn tableViewerColumn_4 = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn_4.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				TopicDetail vo = (TopicDetail)element;
				return element == null ? "" : vo.getRemark();
			}
		});
		TableColumn tblclmnNewColumn = tableViewerColumn_4.getColumn();
		tblclmnNewColumn.setWidth(250);
		tblclmnNewColumn.setText("Remark");
		
		composite_5 = new Composite(composite_3, SWT.NONE);
		composite_5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		composite_5.setSize(616, 208);
		composite_5.setLayout(new GridLayout(1, false));
		
		Composite composite_1 = new Composite(composite_5, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		composite_1.setLayout(new GridLayout(4, false));
		
		txtSeq = new Text(composite_1, SWT.BORDER | SWT.CENTER);
		txtSeq.setText("Seq");
		txtSeq.setEditable(false);
		GridData gd_txtSeq = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_txtSeq.widthHint = 99;
		txtSeq.setLayoutData(gd_txtSeq);
		
		comboViewer = new ComboViewer(composite_1, SWT.NONE);
		Combo combo_type = comboViewer.getCombo();
		GridData gd_combo_type = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_combo_type.widthHint = 100;
		combo_type.setLayoutData(gd_combo_type);
		comboViewer.setLabelProvider(new ViewerLabelProvider_1());
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setInput(topicColumnType);
		
		text_length = new Text(composite_1, SWT.BORDER);
		text_length.setSize(109, 31);
		GridData gd_text_length = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_text_length.widthHint = 100;
		text_length.setLayoutData(gd_text_length);
		
		text_remark = new Text(composite_1, SWT.BORDER);
		GridData gd_text_remark = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text_remark.widthHint = 100;
		text_remark.setLayoutData(gd_text_remark);
		
		Composite composite_2 = new Composite(composite_5, SWT.NONE);
		composite_2.setLayout(new GridLayout(4, false));
		
		Button button_2 = new Button(composite_2, SWT.NONE);
		button_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refreshTopicDetailList();
			}
		});
		button_2.setText("Refresh");
		
		Button btnNewButton = new Button(composite_2, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(text_length.getText().length() > 0 && text_remark.getText().length() > 0) {
					
					TopicDetail topicDetail = new TopicDetail();
					topicDetail.setTopicId(topic.getId());
					topicDetail.setSeq(tableViewer.getTable().getItemCount());
					topicDetail.setType(((Topic)comboViewer.getStructuredSelection().getFirstElement()).getId());	// topic 객체 임시로 사용
					topicDetail.setSize(Integer.parseInt( text_length.getText()) );
					topicDetail.setRemark(text_remark.getText());
					em.getTransaction().begin();
					em.persist(topicDetail);
					em.getTransaction().commit();
					
					refreshTopicDetailList();
					
					tableViewer.setSelection(new StructuredSelection(tableViewer.getElementAt(tableViewer.getTable().getItemCount()-1)),true);

				}else {
					MessageDialog.openError(text_2.getShell(), "Error", "Topic Detail (length or Remark) is empty.");
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
					TopicDetail topicDetail = (TopicDetail) structuredSelection.getFirstElement();
					
					em.getTransaction().begin();
					em.remove(topicDetail);
					em.getTransaction().commit();
					
					refreshTopicDetailList();
					
					if(tableViewer.getTable().getItemCount() == 0) {
						txtSeq.setText("");
						comboViewer.setSelection(new StructuredSelection(comboViewer.getElementAt(0)));
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
					MessageDialog.openWarning(parent.getShell(), "Warning", "Please select a topic first.");
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
					TopicDetail topicDetail = (TopicDetail) structuredSelection.getFirstElement();
					
					//topicDetail.setSeq();
					topicDetail.setType(((Topic)comboViewer.getStructuredSelection().getFirstElement()).getId());
					topicDetail.setSize(Integer.parseInt(text_length.getText()));
					topicDetail.setRemark(text_remark.getText());
					
					em.getTransaction().begin();
					em.merge(topicDetail);
					em.getTransaction().commit();
					
					refreshTopicDetailList();

					tableViewer.setSelection(new StructuredSelection(tableViewer.getElementAt(selectedDetailIndex)),true);

				}else {
					MessageDialog.openWarning(parent.getShell(), "Warning", "Please select a topic first.");
				}
			}
		});
		button_3.setText("Update");
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		
		
	}

	@PreDestroy
	public void preDestroy() {
		em.close();
		this.topic = null;
	}

	@Focus
	public void onFocus() {
		
	}

	@Persist
	public void save() {
		
	}

	public void init(Topic topic) {
		text_2.setText(topic.getName());
		this.topic = topic;
		refreshTopicDetailList();
	}
	public void refreshTopicDetailList() {
		int id = topic.getId() ;

		Query q = em.createQuery("select t from TopicDetail t where t.topicId = :topicId order by t.seq");
        q.setParameter("topicId", id);
		@SuppressWarnings("unchecked")
		java.util.List<TopicDetail> topicDetails = q.getResultList();

		tableViewer.setInput(topicDetails);

	}
}