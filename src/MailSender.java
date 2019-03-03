public class MailSender implements Runnable
{
    private String bed_id;
    private String request_time;
    private MailServer mailServer = null;
    
    public MailSender(String bI, String rT)
    {
        bed_id = bI;
        request_time = rT;
        mailServer = new MailServer();
    }

    @Override
    public void run() {
        mailServer.sendRequestMail(bed_id, request_time);
        mailServer.sendConformationMail(bed_id);
    }
}
