package dawin.ble.daemon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Main {
	public static Properties properties = new Properties();
	
	private static String dbConnect = "jdbc:mariadb://localhost:3306/dawinble";
	private static String dbUser = "root";
	private static String dbPassword = "dawinit1";
	private static String restConnect = "http://59.6.192.225:9988/";
	
	public static void main(String[] args) {
		try {
			Class.forName("org.mariadb.jdbc.Driver");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat dateFormatDate = new SimpleDateFormat("yyyy-MM-dd");
		
        String configFile = "config.xml";
        File file = new File(configFile);
       
        if(file.exists()){

            InputStream inputStream;
			try {
				inputStream = new FileInputStream(configFile);
	            properties.loadFromXML(inputStream);
	            inputStream.close();
	            dbConnect = properties.getProperty("dbConnect");
	            dbUser = properties.getProperty("dbUser");
	            dbPassword = properties.getProperty("dbPassword");
	            restConnect = properties.getProperty("restConnect");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.out.println(" Config Parsing Error");
				System.exit(1);
			} catch (InvalidPropertiesFormatException e) {
				e.printStackTrace();
				System.out.println(" Config Parsing Error");
				System.exit(1);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println(" Config Parsing Error");
				System.exit(1);
			} finally {
			}
            
        }else{
//            properties.setProperty("relayCenterID","000");
//            properties.setProperty("aliveSecond","3");
//            properties.setProperty("ackMilliSecond","1500");
//            
//            OutputStream outputStream = new FileOutputStream(file);
//            properties.storeToXML(outputStream, "VHF-DSC 力绢扁 汲沥 颇老");
//            outputStream.close();
        	System.out.println(" No Config File");
			System.exit(1);
        }

		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		
		Runnable runnableTags = new Runnable() {
			
			@Override
			public void run() {
				System.out.println("----------------------------------------");
				//-- Rest 贸府
		        CloseableHttpClient httpclient = HttpClients.createDefault();
		        Connection connection = null ;
		        try {
					connection = DriverManager.getConnection(dbConnect,dbUser,dbPassword);

		            HttpGet httpGet = new HttpGet(restConnect +"tags");
		            //HttpGet httpGet = new HttpGet("http://59.6.192.225:9988/apdevs");
		            
		            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

		                @Override
		                public String handleResponse(
		                        final HttpResponse response) throws ClientProtocolException, IOException {
		                    int status = response.getStatusLine().getStatusCode();
		                    if (status >= 200 && status < 300) {
		                        HttpEntity entity = response.getEntity();
		                        return entity != null ? EntityUtils.toString(entity) : null;
		                    } else {
		                        throw new ClientProtocolException("Unexpected response status: " + status);
		                    }
		                }

		            	};
		            
		            String responseBody = httpclient.execute(httpGet, responseHandler);
		            
		            System.out.println(responseBody);

					//-- JSON 贸府
		            JSONParser jsonParser = new JSONParser();
		            //JSONObject jsonObj = (JSONObject) jsonParser.parse(responseBody);
		            JSONArray jsonArray = (JSONArray) jsonParser.parse(responseBody);
		            for (int i = 0; i < jsonArray.size(); i++) {
		            	JSONObject tempObj = (JSONObject) jsonArray.get(i);
//		            	System.out.println(tempObj.get("tm"));
		            	String tm = (String) tempObj.get("tm");
		            	JSONArray apArray = (JSONArray) tempObj.get("apdevs");
		            	for (int j = 0; j < apArray.size(); j++) {
		            		JSONObject apObj = (JSONObject) apArray.get(j);
		            		//System.out.println("*" + apObj.get("apdev"));
			            	int apid = Integer.parseInt(""+ apObj.get("apdev"));
		            		JSONArray tagArray = (JSONArray) apObj.get("tags");
		            		for (int k = 0; k < tagArray.size(); k++) {
		            			JSONObject tagObj = (JSONObject) tagArray.get(k);
//		            			System.out.println("**" + tagObj.get("tagid"));
//		            			System.out.println("***" + tagObj.get("rssi"));
//		            			System.out.println("***" + tagObj.get("sos"));
//		            			System.out.println("***" + tagObj.get("batt"));
		            			int tagid = Integer.parseInt(""+  tagObj.get("tagid"));
		            			int rssi = Integer.parseInt(""+ tagObj.get("rssi"));
		            			int sos = Integer.parseInt(""+  tagObj.get("sos"));
		            			float batt = Float.parseFloat(""+  tagObj.get("batt"));
		            			
		            			String sql = new StringBuilder()
		            					.append("INSERT INTO tags (\n")
		            					.append("	time ,\n")
		            					.append("	apid,\n")
		            					.append("	tagid,\n")
		            					.append("	rssi,\n")
		            					.append("	sos,\n")
		            					.append("	batt\n")
		            					.append("	)\n")
		            					.append("VALUES\n")
		            					.append("	(\n")
		            					.append("		?,\n")
		            					.append("		?,\n")
		            					.append("		?,\n")
		            					.append("		?,\n")
		            					.append("		?,\n")
		            					.append("		?\n")
		            					.append("	);\n")
		            					.toString();
		            					
		            			PreparedStatement insertStatement = connection.prepareStatement(sql);
//		            			String date = dateFormatDate.format(new Date());
		            			Timestamp timeStamp = new Timestamp(dateFormat.parse(tm).getTime());
		            			insertStatement.setTimestamp(1, timeStamp);
		            			insertStatement.setInt(2, apid);
		            			insertStatement.setInt(3, tagid);
		            			insertStatement.setInt(4, rssi);
		            			insertStatement.setInt(5, sos);
		            			insertStatement.setFloat(6, batt);
		            			
		            			insertStatement.executeUpdate();
//		            			System.out.println(timeStamp + "***" + apid+":"+tagid+":"+rssi+":"+sos+":"+batt);
		            			
		            			insertStatement.close();
		            			connection.commit();

							}
						}
					}
//					JSONArray list = new JSONArray();

		        } catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (java.text.ParseException e) {
					e.printStackTrace();
				} finally {
		            try {
						httpclient.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						if(connection != null && !connection.isClosed()){
							connection.close();
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
		        }

				
			}
		};
		Runnable runnableApdevs= new Runnable() {
			
			@Override
			public void run() {
				//-- Rest 贸府
		        CloseableHttpClient httpclient = HttpClients.createDefault();
		        Connection connection = null ;
		        try {
					connection = DriverManager.getConnection(dbConnect,dbUser,dbPassword);
		            HttpGet httpGet = new HttpGet(restConnect+"apdevs");
		            
		            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

		                @Override
		                public String handleResponse(
		                        final HttpResponse response) throws ClientProtocolException, IOException {
		                    int status = response.getStatusLine().getStatusCode();
		                    if (status >= 200 && status < 300) {
		                        HttpEntity entity = response.getEntity();
		                        return entity != null ? EntityUtils.toString(entity) : null;
		                    } else {
		                        throw new ClientProtocolException("Unexpected response status: " + status);
		                    }
		                }

		            	};
		            
		            String responseBody = httpclient.execute(httpGet, responseHandler);
		            System.out.println("----------------------------------------");
		            System.out.println(responseBody);

					//-- JSON 贸府
		            JSONParser jsonParser = new JSONParser();
		            //JSONObject jsonObj = (JSONObject) jsonParser.parse(responseBody);
		            JSONArray jsonArray = (JSONArray) jsonParser.parse(responseBody);
		            for (int i = 0; i < jsonArray.size(); i++) {
		            	JSONObject tempObj = (JSONObject) jsonArray.get(i);
		            	System.out.println(tempObj.get("apdev"));
		            	System.out.println(tempObj.get("mac"));
		            	System.out.println(tempObj.get("act"));
		            	System.out.println(tempObj.get("batt"));
		            	int apid = Integer.parseInt(""+  tempObj.get("apdev"));
		            	String mac = ""+  tempObj.get("mac");
		            	int act = Integer.parseInt(""+  tempObj.get("act"));
		            	float batt = Float.parseFloat(""+  tempObj.get("batt"));
		            	
		        		String sql = new StringBuilder()
		        				.append("UPDATE\n")
		        				.append("	ap\n")
		        				.append("SET\n")
//		        				.append("	remark = ?,\n")
//		        				.append("	x = ?,\n")
//		        				.append("	y = ?,\n")
		        				.append("	mac = ?,\n")
		        				.append("	act = ?,\n")
		        				.append("	batt = ? \n")
//		        				.append("	apid = ? \n")		
		        				.append("WHERE\n")
		        				.append("	apid = ?\n")
		        				.toString();
            					
            			PreparedStatement insertStatement = connection.prepareStatement(sql);
            			insertStatement.setString(1, mac);
            			insertStatement.setInt(2, act);
            			insertStatement.setFloat(3, batt);
            			insertStatement.setInt(4, apid);
            			
            			insertStatement.executeUpdate();

            			insertStatement.close();
					}

		        } catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
		            try {
						httpclient.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						if(connection != null && !connection.isClosed()){
							connection.close();
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
		        }

				
			}
		};
	    service.scheduleAtFixedRate(runnableTags, 0, 5000, TimeUnit.MILLISECONDS);
	    service.scheduleAtFixedRate(runnableApdevs, 0, 5000, TimeUnit.MILLISECONDS);
		
//		service.scheduleWithFixedDelay(runnableTags, 0, 5, TimeUnit.SECONDS);
//		service.scheduleWithFixedDelay(runnableApdevs, 0, 10, TimeUnit.SECONDS);
	

		

	}

}
