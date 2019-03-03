import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DBOps {
	
	private Connection connection = null;
	private Statement statement = null;
	private String sql = null;
	
	public Connection connect() {
		try {
			Class.forName("org.sqlite.JDBC");
			String dbPath = "/home/samirtest/Desktop/PatientDB.db";
			connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}
	
	public void disconnect(Connection connection) {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void confirm(Connection connection, ArrayList<ServiceData> data) {
		try {
			statement = connection.createStatement();
			//patient_log = naziv tabele
			//u tabeli ce biti bed_id, request_time, service_time, serviced (= da li je servisiran)
			sql = new String("UPDATE patient_log "
					+ "SET serviced = 'DA', "
					+ "service_time = '" + Utilities.getCurrentTime() + "' " 
					+ "WHERE bed_id = " + data.get(0).getBed_id() + " AND serviced = 'NE';");
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean checkLastData(Connection connection, ArrayList<String> oD) {
		try {
			statement = connection.createStatement();
			sql = new String("SELECT *"
					+ " FROM patient_log WHERE serviced = 'NE'"
					+ " ORDER BY request_time DESC LIMIT 20;");
			ResultSet rs = statement.executeQuery(sql);
			
			if(!rs.isBeforeFirst()) {
				System.out.println("Result set size je nula...");
				return false;
			}
			ArrayList<String> dbData = new ArrayList<String>();
			ArrayList<String> oldData = new ArrayList<String>(oD);
			while(rs.next()) {
			    dbData.add(rs.getString("request_time"));
			}
			
			if(!(oldData.containsAll(dbData) && dbData.containsAll(oldData))) {
				System.out.println("Liste su razlicite...");
				return true;
			} else System.out.println("Liste su iste...");
	    }
	    catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public ArrayList<ServiceData> loadData(Connection connection) {
		ArrayList<ServiceData> data = new ArrayList<ServiceData>();
		try {
			statement = connection.createStatement();
			sql = new String("SELECT bed_id, request_time, serviced"
					+ " FROM patient_log ORDER BY request_time DESC LIMIT 20;");
			ResultSet rs = statement.executeQuery(sql);
			
			while(rs.next()) {
				ServiceData d = new ServiceData();
				d.setBed_id(rs.getInt("bed_id"));
				d.setRequest_time(rs.getString("request_time"));
				d.setService_time("");
				d.setServiced(rs.getString("serviced"));
				if(d.getServiced().equals("NE")) data.add(d);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return data;
	}
}
