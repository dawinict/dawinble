
package kairos.kongde;

import java.net.URL;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import kairos.kongde.entity.Rawdata;
import kairos.kongde.entity.Sensor;
import kairos.kongde.entity.Topic;
import kairos.kongde.entity.Transform;

import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;


public class ConfigPart {
	@Inject UISynchronize sync;
	
	TreeViewer treeViewer;
	
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("kairos.kongde");
	EntityManager em = emf.createEntityManager();

	static List<Topic> topicList;
	static List<Transform> transformList;
	static List<Sensor> sensorList;
	
//	MqttClient mqttClient;

	
	
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
	        return new Object[] { "AP"};
		}
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof String) {
				if(parentElement.toString().equals("Topic")) {
					return topicList.toArray();
				}else if(parentElement.toString().equals("Transform")) {
					return transformList.toArray();
				}else if(parentElement.toString().equals("AP")) {
					return sensorList.toArray();
				}
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

	@Inject
	private EPartService partService;

	@SuppressWarnings("serial")
	@PostConstruct
	public void postConstruct(Composite parent, EMenuService menuService, IEventBroker eventBroker,ESelectionService selectionService) {
		treeViewer = new TreeViewer(parent, SWT.BORDER);
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				partService.activate(partService.findPart("kairos.kongde.part.config"));
				// selection set
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				IStructuredSelection structuredSelection = (IStructuredSelection) selection;
				selectionService.setSelection(structuredSelection);

				// evaluate all @CanExecute methods
				//eventBroker.send(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, UIEvents.ALL_ELEMENT_ID);

				// evaluate a context via a selector
//				Selector s = (a selector that an MApplicationElement or an ID);
//				eventBroker.send(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, s);
			}
		});
		menuService.registerContextMenu(treeViewer.getControl(), "kairos.kongde.popupmenu.config");
//		MenuManager menuManager = new MenuManager();
//		Menu menu = menuManager.createContextMenu(treeViewer.getTree());
//        // set the menu on the SWT widget
//		treeViewer.getTree().setMenu(menu);
//        // register the menu with the framework
//		menuService.registerContextMenu(treeViewer.getControl(),menuManager.getId() );
//
//        // make the viewer selection available
//		menuService.registerContextMenu(treeViewer.getControl(),menuManager.getId() );
//		//setSelectionProvider(treeViewer);
		
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				//TreeViewer viewer = (TreeViewer) event.getViewer();
		        IStructuredSelection thisSelection = (IStructuredSelection) event.getSelection();
		        //Object selectedNode = thisSelection.getFirstElement();
		        //viewer.setExpandedState(selectedNode,  !viewer.getExpandedState(selectedNode));
		        Object element = thisSelection.getFirstElement();
		        if (element instanceof Topic) {
		        	Topic topic = (Topic) element;
		        	
					Collection<MPart> parts = partService.getParts();
					for (MPart part : parts) {
						if(part.getLabel().equals("Topic["+topic.getId()+"] "+ topic.getName())) {
							partService.activate(part);
							
				        	TopicPart topicPart = (TopicPart)part.getObject();
				        	topicPart.init(topic);
							return;
						}
					}

		        	MPart part = partService.createPart("kairos.kongde.partdescriptor.topicpart");
		        	part.setLabel("Topic["+topic.getId()+"] "+ topic.getName());
		        	partService.showPart(part, PartState.ACTIVATE);
		        	
		        	TopicPart topicPart = (TopicPart)part.getObject();
		        	topicPart.init(topic);
		        	//topicPart.refreshTopicDetailList();
	            }else if (element instanceof Transform) {
	            	Transform transform = (Transform) element;
		        	
					Collection<MPart> parts = partService.getParts();
					for (MPart part : parts) {
						if(part.getLabel().equals("Transform["+transform.getId()+"] "+ transform.getName())) {
							partService.activate(part);
							
							TransformPart transformPart = (TransformPart)part.getObject();
							transformPart.init(transform);
							return;
						}
					}

		        	MPart part = partService.createPart("kairos.kongde.partdescriptor.transformpart");
		        	part.setLabel("Transform["+transform.getId()+"] "+ transform.getName());
		        	partService.showPart(part, PartState.ACTIVATE);
		        	
					TransformPart transformPart = (TransformPart)part.getObject();
					transformPart.init(transform);
	            }else if (element instanceof Sensor) {
	            	Sensor sensor = (Sensor) element;
		        	
					Collection<MPart> parts = partService.getParts();
					for (MPart part : parts) {
						if(part.getLabel().equals("AP["+sensor.getAddress()+"] ")) {
							partService.activate(part);
							
							SensorPart sensorPart = (SensorPart)part.getObject();
//							sensorPart.init(sensor);
							return;
						}
					}

		        	MPart part = partService.createPart("kairos.kongde.partdescriptor.sensorpart");
		        	part.setLabel("AP["+sensor.getAddress()+"] ");
		        	partService.showPart(part, PartState.ACTIVATE);
		        	
					SensorPart sensorPart = (SensorPart)part.getObject();
//					sensorPart.init(sensor);
	            }
			}
		});
		@SuppressWarnings("unused")
		Tree tree = treeViewer.getTree();
		
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
				if (element instanceof Topic) {
					//Topic topic = (Topic) element;
					return resourceManager.createImage(imageDescriptor);
	            }else
				if (element instanceof Sensor) {
					//Topic topic = (Topic) element;
					return resourceManager.createImage(imageDescriptorSensor);
	            }else
				if (element instanceof Transform) {
					//Topic topic = (Topic) element;
					return resourceManager.createImage(imageDescriptorTransform);
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
				if (element instanceof Sensor) {
					Sensor sensor = (Sensor) element;
					return sensor.getAddress();
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

		TreeColumn trclmnName = treeViewerColumn.getColumn();
		trclmnName.setWidth(300);
		trclmnName.setText("name");
		
		//treeViewer.setLabelProvider(new ViewerLabelProvider());
		treeViewer.setContentProvider(new TreeContentProvider());
		refreshConfig();
		treeViewer.setInput("root");
		
		int dndOperations = DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK;
		Transfer[] transfers  = new Transfer[] { LocalSelectionTransfer.getTransfer() };
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


//        try {
//			mqttClient = new MqttClient(Constant.BROKER_URI, MqttClient.generateClientId());
//			mqttClient.connect();
//			mqttClient.setCallback(new MqttCallback() {
//				
//				@Override
//				public void messageArrived(String topic, MqttMessage message) throws Exception {
//					sync.asyncExec(()->{
//						refreshConfig();
//					});
//				}
//				
//				@Override
//				public void deliveryComplete(IMqttDeliveryToken token) {
//					// TODO Auto-generated method stub
//					
//				}
//				
//				@Override
//				public void connectionLost(Throwable cause) {
//					System.out.println("Mqtt connectionLost");
//
//					try {
//						mqttClient.reconnect();
//					} catch (MqttException e) {
//						e.printStackTrace();
//					}
//					
//				}
//			});
//			mqttClient.subscribe("k/+/c");
//			//mqttClient.subscribe("k/#");
//
//		} catch (MqttException e) {
//			e.printStackTrace();
//		}
		

	}

	@PreDestroy
	public void preDestroy() {
//		try {
//			mqttClient.unsubscribe("k/+/c");
//			mqttClient.disconnect();
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

	@SuppressWarnings("unchecked")
	public void refreshTopicList() {
        Query q = em.createQuery("select t from Topic t order by t.id");
		topicList = q.getResultList();
        //treeViewer.setInput("root");
	}
	@SuppressWarnings("unchecked")
	public void refreshTransformList() {
        Query q = em.createQuery("select t from Transform t order by t.id");
		transformList = q.getResultList();

        //treeViewer.setInput("root");
	}
	@SuppressWarnings("unchecked")
	public void refreshSensorList() {
        Query q = em.createQuery("select t from Sensor t order by t.address");
		sensorList = q.getResultList();
		for (Sensor sensor : sensorList) {
			if(sensor.getActive() == false) {
				Collection<MPart> parts = partService.getParts();
				for (MPart part : parts) {
					if(part.getLabel().equals("Sensor["+sensor.getAddress()+"] ")) {
						partService.hidePart(part, true);
						break;
					}
				}

			}
		}
        //treeViewer.setInput("root");
	}
	public void refreshConfig() {
		em.clear();
		refreshTopicList();
		refreshTransformList();
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
				
			}else if(name.equals("AP")) {
				Sensor sensorNew = new Sensor();
				sensorNew.setRemark("New AP");
				sensorNew.setAddress("New AP");
				sensorNew.setX(20);
				sensorNew.setY(20);
				
				em.getTransaction().begin();
				em.persist(sensorNew);
				em.getTransaction().commit();
				
				refreshConfig();
				
				int idNew = sensorNew.getId();
				
				
				TreeItem[] treeItems = treeViewer.getTree().getItems();
				for (TreeItem item : treeItems) {
                    
                    if(item.getText().equals("AP")) {
                    	TreeItem[] subItems = item.getItems();
                    	for (TreeItem treeItem : subItems) {
                    		Sensor sensor = (Sensor)treeItem.getData();
                    		if(sensor.getId() == idNew) {
                    			treeViewer.setSelection(new StructuredSelection(sensor), true);
                    			
                    			
								MPart part = partService.createPart("kairos.kongde.partdescriptor.sensorpart");
								part.setLabel("AP"+sensor.getId()+"] " );
								partService.showPart(part, PartState.ACTIVATE);
								SensorPart sensorPart = (SensorPart)part.getObject();
//								sensorPart.init(sensor);

                    			break;
                    		}
						}
                    }
				}
				
			}		
			
		}else { // 엔티티 선택의 경우
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
				
			}else if(object instanceof Sensor) {
				
				Sensor sensor = (Sensor) structuredSelection.getFirstElement();
				
				Collection<MPart> parts = partService.getParts();
				for (MPart mPart : parts) {
					if(mPart.getLabel().equals("AP["+sensor.getAddress()+"] ")) {
						partService.hidePart(mPart, true);
					}
				}
				
//				Query q = em.createQuery("DELETE FROM Sensor t where t.sensord = :sensorId ");
//		        q.setParameter("transformId", transform.getId());

				em.getTransaction().begin();
				//q.executeUpdate();
				em.remove(sensor);
				em.getTransaction().commit();
				
			}
			
			refreshConfig();
			
		}else {
			MessageDialog.openWarning(treeViewer.getControl().getShell(), "Warning", "Please select a model first.");
		}

		
	}
	
	@Inject
	@Optional
	private void subscribeTopicTodoUpdated(@UIEventTopic("k/join") String todo) {
		refreshConfig();
	}
}