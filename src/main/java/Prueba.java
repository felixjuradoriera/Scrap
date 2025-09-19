import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.*;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class Prueba {

    // 🔹 Configuración correo
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;
    private static final String SMTP_USER = "felixjuradoriera@gmail.com";   // <-- tu Gmail
    private static final String SMTP_PASS = "tsqa youa wqef cibh";          // <-- App Password de Gmail

    private static final String EMAIL_TO = "felixjuradoriera@gmail.com";
    private static final String EMAIL_SUBJECT = "Resultados Odds (Java)";

    private static final String CSV_FILE = "oddsAnteriores.csv";

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

            ArrayList<Odd> odds = new ArrayList<>();

            JsonNode dataArray = root.get("data");
            if (dataArray != null && dataArray.isArray()) {
                for (JsonNode item : dataArray) {
                    Odd odd = new Odd();
                    odd.setEvent(item.path("event").asText());
                    odd.setBookie(item.path("bookie_id").asText());
                    odd.setRating(item.path("rating").asText());
                    odd.setBackOdd(item.path("back_odd").asText());
                    odd.setLayOdd(item.path("lay_odd").asText());
                    odd.setSelection(item.path("selection").asText());
                    odd.setCompetition(item.path("competition").asText());
                    odd.setUpdate_time(item.path("update_time").asText());
                    odd.setCountry(item.path("country").asText());
                    odds.add(odd);
                }
            }

            // 🔹 Leer histórico si existe
            ArrayList<Odd> oddsAnteriores = leerCSV(CSV_FILE);

            // 🔹 Generar HTML filtrando duplicados
            StringBuilder html = new StringBuilder();
            html.append("<html><body>");
            html.append("<h2>Resultados Odds</h2>");
            html.append("<table border='1' cellpadding='5' cellspacing='0' style='border-collapse: collapse;'>");
            html.append("<tr style='background-color:#f2f2f2;'>");
            html.append("<th>Pais</th><th>Competicion</th><th>Evento</th><th>Bookie</th><th>Rating</th><th>apuesta</th><th>Back</th><th>Lay</th><th>antiguedad</th>");
            html.append("</tr>");

            int nuevos = 0;
            for (Odd odd : odds) {
                if (!yaExistia(odd, oddsAnteriores)) {
                    nuevos++;
                    html.append("<tr>")
                            .append("<td>").append(odd.getCountry()).append("</td>")
                            .append("<td>").append(odd.getCompetition()).append("</td>")
                            .append("<td>").append(odd.getEvent()).append("</td>")
                            .append("<td>").append(getNombreBookie(odd.getBookie())).append("</td>")
                            .append("<td><strong>").append(odd.getRating()).append("</strong></td>")
                            .append("<td>").append(odd.getSelection()).append("</td>")
                            .append("<td>").append(odd.getBackOdd()).append("</td>")
                            .append("<td>").append(odd.getLayOdd()).append("</td>")
                            .append("<td>").append(odd.getUpdate_time()).append("</td>")
                            .append("</tr>");
                }
            }

            if (nuevos == 0) {
                html.append("<tr><td colspan='9'>No hay nuevos odds respecto a la ejecución anterior.</td></tr>");
            }

            html.append("</table>");
            html.append("</body></html>");

            // 🔹 Enviar resultado por email
            sendEmail(html.toString());
            
            
         // 🔹 Generar mensaje de Telegram (resumen)
            StringBuilder mensaje = new StringBuilder();
            mensaje.append("📊 <b>Resultados Odds</b>\n\n");

            for (Odd odd : odds) {
                mensaje.append("⚽ <b>").append(odd.getEvent()).append("</b>\n")
                       .append("🏆 ").append(odd.getCompetition()).append(" (").append(odd.getCountry()).append(")\n")
                       .append("Casa: ").append(getNombreBookie(odd.getBookie())).append("\n")
                       .append("Rating: <b>").append(odd.getRating()).append("</b>\n")
                       .append("Apuesta: ").append(odd.getSelection()).append("\n")
                       .append("Back: ").append(odd.getBackOdd()).append(" | Lay: ").append(odd.getLayOdd()).append("\n")
                       .append("⏱ ").append(odd.getUpdate_time()).append("\n\n");
            }

            // 🔹 Enviar a Telegram
            TelegramSender.sendTelegramMessage(mensaje.toString());
            
            

            // 🔹 Guardar los odds actuales como histórico
            escribirCSV(CSV_FILE, odds);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔹 Método para comprobar si ya existía en el histórico
    private static boolean yaExistia(Odd nuevo, ArrayList<Odd> anteriores) {
        for (Odd o : anteriores) {
            if (o.getEvent().equals(nuevo.getEvent())
                    && o.getBookie().equals(nuevo.getBookie())
                    && o.getRating().equals(nuevo.getRating())
                    && o.getSelection().equals(nuevo.getSelection())) {
                return true;
            }
        }
        return false;
    }

    // 🔹 Guardar odds en CSV
    private static void escribirCSV(String file, ArrayList<Odd> odds) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (Odd o : odds) {
                pw.println(String.join(";",
                        o.getEvent(), o.getBookie(), o.getRating(), o.getBackOdd(),
                        o.getLayOdd(), o.getSelection(), o.getCompetition(),
                        o.getUpdate_time(), o.getCountry()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 🔹 Leer odds desde CSV
    private static ArrayList<Odd> leerCSV(String file) {
        ArrayList<Odd> lista = new ArrayList<>();
        File f = new File(file);
        if (!f.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] campos = line.split(";");
                if (campos.length >= 9) {
                    Odd o = new Odd();
                    o.setEvent(campos[0]);
                    o.setBookie(campos[1]);
                    o.setRating(campos[2]);
                    o.setBackOdd(campos[3]);
                    o.setLayOdd(campos[4]);
                    o.setSelection(campos[5]);
                    o.setCompetition(campos[6]);
                    o.setUpdate_time(campos[7]);
                    o.setCountry(campos[8]);
                    lista.add(o);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // 🔹 Enviar email HTML
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
            message.setContent(htmlContent, "text/html; charset=UTF-8");
            Transport.send(message);
            System.out.println("✅ Correo HTML enviado correctamente.");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("❌ Error enviando correo.");
        }
    }

    private static String getNombreBookie(String bookie) {
        Map<String, String> bookies = new HashMap<>();
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

        return bookies.getOrDefault(bookie, bookie);
    }

    // 🔹 Clase Odd
    public static class Odd {
        private String event;
        private String bookie;
        private String rating;
        private String backOdd;
        private String layOdd;
        private String selection;
        private String competition;
        private String update_time;
        private String country;

        // getters y setters
        public String getEvent() { return event; }
        public void setEvent(String event) { this.event = event; }
        public String getBookie() { return bookie; }
        public void setBookie(String bookie) { this.bookie = bookie; }
        public String getRating() { return rating; }
        public void setRating(String rating) { this.rating = rating; }
        public String getBackOdd() { return backOdd; }
        public void setBackOdd(String backOdd) { this.backOdd = backOdd; }
        public String getLayOdd() { return layOdd; }
        public void setLayOdd(String layOdd) { this.layOdd = layOdd; }
        public String getSelection() { return selection; }
        public void setSelection(String selection) { this.selection = selection; }
        public String getCompetition() { return competition; }
        public void setCompetition(String competition) { this.competition = competition; }
        public String getUpdate_time() { return update_time; }
        public void setUpdate_time(String update_time) { this.update_time = update_time; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
    }
}
