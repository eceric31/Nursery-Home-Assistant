public class Program {

	public static void main(String[] args) {
	    while(true) {   
		    System.out.println("Pokrecem servis...\n");
		    Service service = new Service();
	     	try {
				service.run();
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
        }
	}

}
