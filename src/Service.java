import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.util.ArrayList;

public class Service {
    
    private ArrayList<ServiceData> data = new ArrayList<ServiceData>();
    private ArrayList<String> oldData = new ArrayList<String>();
    private ServerSocket server = null;
    private SocketOps socketOps = null;
    private InetAddress clientAddress = null;
    
    public Service() {
    	try {
            if(server == null) {
            	System.out.println("Server socket je null...");
            	server = new ServerSocket(5000);
            }
            
            Socket init = server.accept();
            InetSocketAddress isa = (InetSocketAddress)init.getRemoteSocketAddress();
            clientAddress = isa.getAddress();
            init.close();
            
            System.out.println("Initial connection established...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void run() throws Exception {        
        while(true) {
            DBOps ops = new DBOps();
            Connection connection = ops.connect();
            System.out.println("\nKonektovan sam na bazu...");
            
            if(ops.checkLastData(connection, oldData)) {
                System.out.println("Ima podataka...");
                
                data = ops.loadData(connection);
                for(int i = 0; i < data.size(); i++) oldData.add(data.get(i).getRequest_time());
                System.out.println("Podaci su ucitani...");
                
                try {
					socketOps = new SocketOps(clientAddress);
				} catch (IOException e) {
					e.printStackTrace();
					server.close();
					break;
				}
                
                try {
		            socketOps.sendBedIds(Utilities.getByteArray(data));
	            } catch (IOException e) {
		            e.printStackTrace();
		            server.close();
		            break;
	            }
                socketOps.purgeSender();
                
                new Thread(new ConformationReceiver(server, data)).start();
            } else System.out.println("Nema nista novo u bazi...");
            ops.disconnect(connection);
            
            try {
               Thread.sleep(10000);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
            
            if(!data.isEmpty()) data.clear();
        }
    } 
}
