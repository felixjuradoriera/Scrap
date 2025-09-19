import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TelegramSender {

    // ⚠️ Sustituye por los tuyos
    private static final String BOT_TOKEN = "7380837153:AAHMQFIyGwO-FSwq9DvpQjnH4JroSy9tOSs";
    private static final String CHAT_ID = "403482161";
    
    private static final String[] CHAT_IDS = {
            "403482161",     // tu privado
            "-1003064907759" // grupo
        };// tu chat_id de Telegram

    public static void sendTelegramMessage(String text) {
    	 for (String chatId : CHAT_IDS) {
        try {
            String urlString = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage";
            String urlParameters = "chat_id=" + chatId
                    + "&text=" + URLEncoder.encode(text, "UTF-8")
                    + "&parse_mode=HTML"; // HTML limitado

            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.write(postData);
            }

            int responseCode = conn.getResponseCode();
            System.out.println("📩 Telegram response: " + responseCode);

            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                System.out.println("📩 Respuesta Telegram: " + response);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    	 }
    }
}
