package kairos.kongde;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.rap.e4.E4ApplicationConfig;
import org.eclipse.rap.e4.E4EntryPointFactory;
import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.application.Application.OperationMode;
import org.eclipse.rap.rwt.client.WebClient;
import org.osgi.service.event.Event;

import kairos.kongde.entity.Rawdata;
import kairos.kongde.entity.Sensor;
import kairos.kongde.entity.Transform;

public class e4Application implements ApplicationConfiguration {

    private final static String E4XMI = "platform:/plugin/kairos.kongde/Application.e4xmi";

	@Inject
	private IEventBroker eventBroker;


    //DB db = null;
    public static EntityManagerFactory emf = Persistence.createEntityManagerFactory("kairos.kongde");
    public static EntityManager em = emf.createEntityManager();

    public static TransformJobManager transformJobManager = new TransformJobManager();
    
//	MqttClient mqttClient;
	
	@PreDestroy
	public void preDestroy() {
	}

   
    public void configure(Application application) {
    	//application.addStyleSheet( "kairos.kongde.theme", "css/business.css" );
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(WebClient.PAGE_TITLE, "Kongde");
        E4ApplicationConfig config = E4ApplicationConfig.create(E4XMI);
        //E4ApplicationConfig config = E4ApplicationConfig.create(E4XMI,"bundleclass://kairos.kongde/kairos.kongde.E4LifeCycle");
        E4EntryPointFactory entryPointFactory = new E4EntryPointFactory(config);
        
        //properties.put( WebClient.THEME_ID, "kairos.kongde.theme" );
        
        application.addEntryPoint("/kongde", entryPointFactory, properties);
        application.setOperationMode( OperationMode.SWT_COMPATIBILITY );
        
        
        // Master Mote 로부터 TCP Packet을 받아 Topic TOPIC_DEFAULT 에 발행
//        new Thread(new Runnable() {
//        	// 스레드 풀의 최대 스레드 개수를 지정합니다.
//         	private final int THREAD_CNT = 5;
//         	private ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_CNT);
//			
//			@Override
//			public void run() {
//		        ServerSocket server = null;
//		        int port = 11883;
//		        Socket socket = null;
//		        
//		        try{
//		            server = new ServerSocket(port);
//		            while(true){
//		                System.out.println("-------접속 대기중------");
//		                socket = server.accept();         // 클라이언트가 접속하면 통신할 수 있는 소켓 반환
//		                System.out.println(socket.getInetAddress()+":"+socket.getPort() + " 로 부터 연결요청이 들어옴");
//		                threadPool.execute(new ConnectionWrap(socket));
//
//		            }
//		        }catch (Exception e) {
//		                e.printStackTrace();
//		        }finally {
//		            try{
//		                server.close();
//		            }catch(Exception e){
//		                e.printStackTrace();
//		            }
//		        }
//			}
//		}).start();

        
        // TransformJobList 초기화
//        Query q = em.createQuery("select t from Transform t order by t.id");
//		List<Transform> transformList = q.getResultList();
//		for (Transform transform : transformList) {
//			e4Application.transformJobManager.add(transform);
//		}

		
//    	MqttClient mqttClient;
//        // TOPIC_DEFAULT 를 구독하여 DB에 저장
//        try {
////			try {
////				db = new DB();
////			} catch (ClassNotFoundException e1) {
////				e1.printStackTrace();
////			} catch (SQLException e1) {
////				e1.printStackTrace();
////			}
//
////			mqttClient = new MqttClient(Constant.BROKER_URI, MqttClient.generateClientId());
//			mqttClient = new MqttClient(Constant.BROKER_URI, MqttClient.generateClientId());
//			MqttConnectOptions options = new MqttConnectOptions();
//			options.setKeepAliveInterval(60000);
//			mqttClient.connect();
//			mqttClient.setCallback(new MqttCallback() {
//				
//				@Override
//				public void messageArrived(String topic, MqttMessage message) throws Exception {
//					try {
//						//DB.insertRawData(message.getPayload());
//						Rawdata rawdata = new Rawdata();
//						rawdata.setData(message.getPayload());
//						rawdata.setTopic(topic);
//						em.getTransaction().begin();
//						em.persist(rawdata);
//						em.getTransaction().commit();
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//					
////					if(i % 100 == 0) {
////						timeBefore = time;
////						time = System.currentTimeMillis();
////						System.out.println(System.currentTimeMillis()+": "+(100000/(time - timeBefore))+" "+topic+":"+message);
////					}
////					i++;
//					if(topic.equals("k/join")) {
//						String address = message.toString();
//				        Query q = em.createQuery("select t from Sensor t where t.address = :address ");
//				        q.setParameter("address", address);
//						List<Sensor> sensorList = q.getResultList();
//						
//						if(sensorList.size() == 0) {
//							Sensor sensorNew = new Sensor();
//							sensorNew.setRemark("New Sensor");
//							sensorNew.setAddress(address);
//							sensorNew.setActive(true);
//							//sensorNew.setX(x);
//							//sensorNew.setY(y);
//							
//							em.getTransaction().begin();
//							em.persist(sensorNew);
//							em.getTransaction().commit();
//							
//							try {
////								sync.syncExec(()->{
//									//eventBroker.post(topic, "sss");
////								});
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//						
//						}else {
//							for (Sensor sensor : sensorList) {
//								sensor.setActive(true);
//								em.getTransaction().begin();
//								em.persist(sensor);
//								em.getTransaction().commit();
//								//em.flush();
//							}
//						}
//
//						mqttClient.publish("k/"+address+"/c", new MqttMessage("k/joinOk".getBytes()));
//
//					}else if(topic.equals("k/timeout")) {
//						String address = message.toString();
//				        Query q = em.createQuery("select t from Sensor t where t.address = :address ");
//				        q.setParameter("address", address);
//						List<Sensor> sensorList = q.getResultList();
//						for (Sensor sensor : sensorList) {
//							sensor.setActive(false);
//							em.getTransaction().begin();
//							em.persist(sensor);
//							em.getTransaction().commit();
//							//em.flush();
//						}
//						mqttClient.publish("k/"+address+"/c", new MqttMessage("k/timeoutOk".getBytes()));
//						
//					}
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
//			mqttClient.subscribe("k/#");
//
////			try {
////				mqttClient.unsubscribe("k/#");
////				mqttClient.disconnect();
////			} catch (MqttException e) {
////				e.printStackTrace();
////			}
//
//		} catch (MqttException e) {
//			e.printStackTrace();
//		}

    }
//	@Inject
//	@Optional
//	public void subscribeAppShutdownStarted(@UIEventTopic(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE) Event event) {
//		System.out.println("app shutdown started");
//	}

}

//소켓 처리용 래퍼 클래스입니다.
//class ConnectionWrap implements Runnable{
//
//	private Socket socket = null;
//
//	String topic        = Constant.TOPIC_DEFAULT;
//	//String content      = "Message from MqttPublishSample";
//	int qos             = 0;
//	String broker       = Constant.BROKER_URI;
//	String clientId     = MqttClient.generateClientId();
//	MemoryPersistence persistence = new MemoryPersistence();
//
//
//	public ConnectionWrap(Socket socket) {
//		this.socket = socket;
//	}
//
//	@Override
//	public void run() {
//		InputStream is = null;
//		InputStreamReader isr = null;
//		BufferedReader br = null;
//
//		MqttClient mqttClient = null;
//		  
//		try {
//	        is = socket.getInputStream();
//	        isr = new InputStreamReader(is);
//	        br = new BufferedReader(isr);
//	        // 클라이언트로부터 데이터를 받기 위한 InputStream 선언
//	
//	        mqttClient = new MqttClient(broker, clientId, persistence);
//	        //MqttConnectOptions connOpts = new MqttConnectOptions();
//	        //connOpts.setCleanSession(true);
//	        //connOpts.setPassword("rabbitmq".toCharArray());
//	        //mqttClient.connect(connOpts);
//	        System.out.println("Connecting to broker: "+broker);
//	        mqttClient.connect();
//	        System.out.println("Connected");
//	
//	        String data=null;
//	        int i = 0;
//	        while((data = br.readLine()) != null){
//	            MqttMessage message = new MqttMessage(data.getBytes());
//	            message.setQos(qos);
//	            mqttClient.publish(topic, message);
//	            
//	      	  if(i % 100 == 0) {
//	                System.out.println("TCP WRAP:" + i + ":" + data);
//	      	  }
//	      	  i++;
//	
//	        }
//        
//        //receiveData(data, socket);         // 받은 데이터를 그대로 다시 보내기
//        //System.out.println("****** 전송 완료 ****");
//
////        // 응답을 위해 스트림을 얻어옵니다.
////			OutputStream stream = socket.getOutputStream();
////			// 그리고 현재 날짜를 출력해줍니다.
////			stream.write(new Date().toString().getBytes());
//
//		} catch (IOException e) {
//			e.printStackTrace();
//      } catch(MqttException me) {
//          System.out.println("reason "+me.getReasonCode());
//          System.out.println("msg "+me.getMessage());
//          System.out.println("loc "+me.getLocalizedMessage());
//          System.out.println("cause "+me.getCause());
//          System.out.println("excep "+me);
//          me.printStackTrace();
//		} finally {
//			try {
//            br.close();
//            isr.close();
//            is.close();
//            socket.close(); // 반드시 종료합니다.
//            mqttClient.disconnect();
//            System.out.println(socket.getInetAddress()+":"+socket.getPort() + " 종료");
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (MqttException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//	
//}