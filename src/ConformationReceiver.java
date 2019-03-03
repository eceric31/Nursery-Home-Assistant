import java.net.ServerSocket;
import java.sql.Connection;
import java.util.ArrayList;
import java.io.*;

public class ConformationReceiver implements Runnable {
    private ServerSocket server = null;
    private ArrayList<ServiceData> data;
    private DBOps dbOps = null;
    
    public ConformationReceiver(ServerSocket s, ArrayList<ServiceData> d) {
        server = s;
        data = new ArrayList<ServiceData>(d);
        dbOps = new DBOps();
    }
    
    @Override
    public void run() { 
        Connection connection = dbOps.connect();
        while(!data.isEmpty()) {
            System.out.println("Cekam primanje potvrde za neki krevet...");
            SocketOps socketOps = new SocketOps(server);
            
            int bedNum = 0;
            try {
                bedNum = socketOps.receiveResponse();
            } catch(IOException e) {
                e.printStackTrace();
                break;
            } 
            if(bedNum == 0) break;
            
            System.out.println("Primio sam potvrdu za neki krevet...");
            if(bedNum > 0 && bedNum < 27) {
                System.out.println("Krevet je broj: " + bedNum + ", a data.size(): " + data.size());   
                
                ArrayList<ServiceData> temp = new ArrayList<ServiceData>();
                for(int i = 0; i < data.size(); i++) {
                    if(Integer.valueOf(data.get(i).getBed_id()).equals(bedNum)) {
                        System.out.println(i + " " + data.size() + " " + data.get(i).getRequest_time());
                        temp.add(data.get(i));
                        data.remove(i);
                        data.trimToSize();
                    }
                }
                
                dbOps.confirm(connection, temp);
                for(int i = 0; i < temp.size(); i++) {          
                    new Thread(
                    		new MailSender(String.valueOf(bedNum), 
                    				temp.get(i).getRequest_time())).start();
                }
            }
            try {
				socketOps.purgeReceiver();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        dbOps.disconnect(connection);
        System.out.println("Zavrsen thread...");
    }

}
