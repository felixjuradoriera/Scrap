import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class Prueba {

    // 🔹 Configuración correo
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;
    private static final String SMTP_USER = "felixjuradoriera@gmail.com";   // <-- tu Gmail
    private static final String SMTP_PASS = "tsqa youa wqef cibh";       // <-- App Password de Gmail

    private static final String EMAIL_TO = "felixjuradoriera@gmail.com";
    private static final String EMAIL_SUBJECT = "Resultados Odds (Java)";

    public static void main(String[] args) {
        String url = "https://www.ninjabet.es/get_data_sp.php";
        String urlParameters = "combinazioni=2&action=get_odds_data&uid=e7dfca01f394755c11f853602cb2608a"
                + "&refund=100&back_stake=100"
                + "&filterbookies[]=2&filterbookies[]=75&filterbookies[]=48&filterbookies[]=7&filterbookies[]=39&filterbookies[]=69"
                + "&bookies=-0,68,1,54,108,2,75,53,56,59,7,62,61,41,106,39,78,104,102,103,73,40,43,42,76,64,71,44,55,45,107,46,47,29,57,109,48,105,65,20,69,52,74"
                + "&rating-from=95&rating-to=&odds-from=2.5&odds-to=&min-liquidity=&sort-column=4&sort-direction=desc"
                + "&offset=0&date-from=&date-to=&exchange=all&exchanges=all&sport=&betfair-commission=2"
                + "&matchbook-commission=&bet-type[]=home&bet-type[]=away&rating-type=normal"
                + "&roll-real-money=100&roll-bonus=100&roll-remaining=100&roll-rating=95&tz=-120";

        try {
            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            conn.setRequestProperty("Content-Length", String.valueOf(postData.length));
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setRequestProperty("Referer", "https://www.ninjabet.es/oddsmatcher");

            conn.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.write(postData);
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Response Code : " + responseCode);

            InputStream is = (responseCode >= 200 && responseCode < 300)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }

            // 🔹 Procesar JSON con Jackson
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.toString());

            StringBuilder html = new StringBuilder();
            html.append("<html><body>");
            html.append("<h2>Resultados Odds</h2>");
            html.append("<table border='1' cellpadding='5' cellspacing='0' style='border-collapse: collapse;'>");
            html.append("<tr style='background-color:#f2f2f2;'>");
            html.append("<th>Competicion</th><th>Evento</th><th>Bookie</th><th>Rating</th><th>apuesta</th><th>Back</th><th>Lay</th>");
            html.append("</tr>");

            JsonNode dataArray = root.get("data");
            if (dataArray != null && dataArray.isArray()) {
                for (JsonNode item : dataArray) {
                    String event = item.path("event").asText();
                    String bookie = item.path("bookie_id").asText();
                    String rating = item.path("rating").asText();
                    String backOdd = item.path("back_odd").asText();
                    String layOdd = item.path("lay_odd").asText();
                    String selection = item.path("selection").asText();
                    String competition = item.path("competition").asText();
                    
                    
                    
                    
                    String nombreBookie=getNombreBookie(bookie);

                    html.append("<tr>")
                        .append("<td>").append(competition).append("</td>")
                        .append("<td>").append(event).append("</td>")
                        .append("<td>").append(nombreBookie).append("</td>")
                        .append("<td>").append(rating).append("</td>")
                        .append("<td>").append(selection).append("</td>")
                        .append("<td>").append(backOdd).append("</td>")
                        .append("<td>").append(layOdd).append("</td>")
                        .append("</tr>");
                }
            } else {
                html.append("<tr><td colspan='5'>No se encontró el array 'data' en la respuesta.</td></tr>");
            }

            html.append("</table>");
            html.append("</body></html>");

            // 🔹 Enviar resultado por email
            sendEmail(html.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔹 Método para enviar el email en HTML
    private static void sendEmail(String htmlContent) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", String.valueOf(SMTP_PORT));

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USER, SMTP_PASS);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_USER));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(EMAIL_TO));
            message.setSubject(EMAIL_SUBJECT);

            // 🔹 Contenido HTML
            message.setContent(htmlContent, "text/html; charset=UTF-8");

            Transport.send(message);

            System.out.println("✅ Correo HTML enviado correctamente.");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("❌ Error enviando correo.");
        }
    }
    
    
    private static String getNombreBookie(String bookie) {
    	String nombreBookie="";
    	
    	Map<String,String> bookies= new HashMap<String, String>();
    	
    	bookies.put("68", "1xbet");
    	bookies.put("1", "888sport");
    	bookies.put("54", "Admiral");
    	bookies.put("108", "Aupabet");
    	bookies.put("2", "Bet365");
    	bookies.put("75", "Bet777");
    	bookies.put("53", "Betfairsportbook");
    	bookies.put("56", "Betsson");
    	bookies.put("59", "Betway");
    	bookies.put("7", "Bwin");
    	bookies.put("62", "Casino Barcelona");
    	bookies.put("61", "Casino Madrid");
    	bookies.put("41", "Casinogranvía");
    	bookies.put("106", "Casumo");
    	bookies.put("39", "Codere");
    	bookies.put("78", "Dafabet");
    	bookies.put("104", "Daznbet");
    	bookies.put("102", "Ebingo");
    	bookies.put("103", "Efbet");
    	bookies.put("73", "Enracha");
    	bookies.put("40", "Goldenpark");
    	bookies.put("43", "Interwetten");
    	bookies.put("42", "Jokerbet");
    	bookies.put("76", "Juegging");
    	bookies.put("64", "Kirolbet");
    	bookies.put("71", "Leovegas");
    	bookies.put("44", "Luckia");
    	bookies.put("55", "Marathonbet");
    	bookies.put("45", "Marcaapuestas");
    	bookies.put("107", "Olybet");
    	bookies.put("46", "Paf");
    	bookies.put("47", "Paston");
    	bookies.put("29", "Pokerstars");
    	bookies.put("57", "Retabet");
    	bookies.put("109", "Solcasino");
    	bookies.put("48", "Sportium");
    	bookies.put("105", "Tonybet");
    	bookies.put("65", "Versus");
    	bookies.put("20", "Williamhill");
    	bookies.put("69", "Winamax");
    	bookies.put("52", "Yaass");
    	bookies.put("74", "Zebet");
    	
    	
    	if (bookies.containsKey(bookie)) {
    		
    		nombreBookie=bookies.get(bookie);
    	}   	
    	
    	
    	return nombreBookie;
    	
    }
    
}
