import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class SmsNotification implements Notification {
	
    @Override
    public void send(String recipient, String message) {
        String text = "[SMS to " + recipient + "] " + message;
        System.out.println("\n" + text);
        
        try (PrintWriter out = new PrintWriter(new FileWriter("notifications_log.txt", true))) {
            out.println(text);
            out.println("-----");
        } catch (IOException e) {
            System.out.println("Could not write to log: " + e.getMessage());
        }
    }
    
}