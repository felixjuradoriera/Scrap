import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TelegramSender {

    // ⚠️ Sustituye por los tuyos
    private static final String BOT_TOKEN = "7380837153:AAHMQFIyGwO-FSwq9DvpQjnH4JroSy9tOSs";  //PRO
    //private static final String BOT_TOKEN = "7029538813:AAH2I40DoMKEWLpVph3qrWUJ3vilGTEQABg";  //PRE
    
    
   // private static final String[] CHAT_IDS = {"403482161","-1003064907759"};
   private static final String[] CHAT_IDS = {"403482161"};  //<-- este soy yo
  // private static final String[] CHAT_IDS = {"-1003064907759"}; //<-- este es el chat grupal
    
    //hola aqui
    //private static final String[] CHAT_IDS_DEBUG = {"403482161"}; //<--- este soy yo
    private static final String[] CHAT_IDS_DEBUG = {"-4914584937"}; //<-- este es el chatDebug
    
    
    private static final String[] CHAT_IDS_VIGILANTE = {"403482161"}; //<-- este es el chatDebug
    
    

    public static void sendTelegramMessage(String text) {
    	 for (String chatId : CHAT_IDS) {
        try {
            String urlString = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage";
            String urlParameters = "chat_id=" + chatId
                    + "&text=" + URLEncoder.encode(text, "UTF-8")
                    + "&parse_mode=HTML" // HTML limitado
                    + "&disable_web_page_preview=true";

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
    
    public static void sendTelegramMessageAlerta(String text , Odd odd, String chatId) {
        
            try {
                String urlString = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage";
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                
                String callBackData=odd.getMarket_id() + "|" + odd.getsFechaPartido() + "|" + odd.getEvent();

               String json="";
                if(chatId.equals("-1003064907759")) {
                	json = "{"
                            + "\"chat_id\":\"" + chatId + "\","
                            + "\"text\":\"" + text.replace("\"", "\\\"") + "\","
                            + "\"parse_mode\":\"HTML\","
                            + "\"disable_web_page_preview\":true"
                            + "}";
                	
                } else {
                	 json = "{"
                             + "\"chat_id\":\"" + chatId + "\","
                             + "\"text\":\"" + text.replace("\"", "\\\"") + "\","
                             + "\"parse_mode\":\"HTML\","
                             + "\"disable_web_page_preview\":true,"
                             + "\"reply_markup\":{"
                             + "   \"inline_keyboard\":["
                             + "       [{\"text\":\"❌ Quitar este evento de tus alertas\",\"callback_data\":\""+ callBackData +"\"}]"
                             + "   ]"
                             + "}"
                             + "}";
                }
                
               

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(json.getBytes(StandardCharsets.UTF_8));
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

    
    
    public static void sendTelegramMessageDebug(String text) {
   	 for (String chatId : CHAT_IDS_DEBUG) {
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
    
    
    public static void sendTelegramMessageVigilante() {
    	
    	 StringBuilder mensajeDebug = new StringBuilder();
         mensajeDebug.append("<b>Debug Ejecucion</b>\n");
         mensajeDebug.append("Peticiones HTTP403:  <b>").append("1").append("</b>\n");
         mensajeDebug.append("<b>Probable caída de la VPN. Avisar").append("</b>\n");
         String text=mensajeDebug.toString();
         
      	 for (String chatId : CHAT_IDS_VIGILANTE) {
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
