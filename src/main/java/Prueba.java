import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
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
    private static final String SMTP_PASS = "tsqa youa wqef cibh";          // <-- App Password de Gmail

    private static final String EMAIL_TO = "felixjuradoriera@gmail.com";
    private static final String EMAIL_SUBJECT = "Resultados Odds (Java)";
    
    
    private static  Integer FiltroMinutosAntiguedad = 20;
    
    private static  Double restaCuotaCodere = 0.05;

    
    
    private static  String urlData = "https://www.ninjabet.es/get_data_sp.php";
    private static  String urlEvents = "https://www.ninjabet.es/get_events_sp.php";
    private static  String urlExchange = "https://ero.betfair.es/www/sports/exchange/readonly/v1/bymarket";
    
    
    private static  String uid = "e7dfca01f394755c11f853602cb2608a";
    private static  String ratingInicial="92";
    private static  String cuotaMinima="2.5";

    private static final String CSV_FILE = "oddsAnteriores.csv";
    
    static ArrayList<String> filtroBookies2UP= new ArrayList<String>(Arrays.asList("2","48","7","39","69"));
    static ArrayList<String> filtroBookies2UP2WAY= new ArrayList<String>(Arrays.asList("2","75","48","7","39","69","47"));
    static ArrayList<String> filtroBookiesVacio = new ArrayList<>();
    static ArrayList<String> filtroApuestas2UP= new ArrayList<String>(Arrays.asList("home","away"));
    static ArrayList<String> filtroApuestasHome= new ArrayList<String>(Arrays.asList("home"));
    static ArrayList<String> filtroApuestasDraw= new ArrayList<String>(Arrays.asList("draw"));
    static ArrayList<String> filtroApuestasAway= new ArrayList<String>(Arrays.asList("away"));
    

    public static void main(String[] args) {
        	
    	    	
        
        String urlParameters=crearUrlFiltroPeticionData(uid, filtroBookies2UP, ratingInicial, cuotaMinima, filtroApuestas2UP, "");

        try {
        	
        	
          
        	
        	StringBuilder response= crearPeticionData(urlParameters, urlData);
        	
        	ArrayList<Odd> lectura = new ArrayList<>();
            ArrayList<Odd> odds = new ArrayList<>();

            lectura=mapearListaResultadosData(response);
            
                         
                
            // 🔹 Leer histórico si existe
            ArrayList<Odd> oddsAnteriores = leerCSV(CSV_FILE);
            
           
			
                        
            //filtramos eventos que no interesan
            for (Odd odd : lectura) {
            	 if (!yaExistia(odd, oddsAnteriores) && odd.getTimeInMin()<=FiltroMinutosAntiguedad  && pasaFiltroDatos(odd)) {
            		 
            		 //buscamos los mejores home,away y draw para cono información complementaria
            		  odd=rellenaCuotas(odd); 		 
            		 
            		 odds.add(odd);
            	 } else {
            		 System.out.println("ODD DESCARTADO:");
            		 System.out.println(odd.toString());
            		 System.out.println("TimeinMin: " + odd.getTimeInMin());
            	 }
            }
            
            if(lectura.isEmpty()) {
            	StringBuilder mensajeDebug = new StringBuilder();
                mensajeDebug.append("<b>Debug resultados</b>\n");
            	mensajeDebug.append("La petición ha resuelto sin resultados.");
            } else if (odds.isEmpty()) {
            	StringBuilder mensajeDebug = new StringBuilder();
                mensajeDebug.append("<b>Debug resultados</b>\n");
            	mensajeDebug.append("ningún resultado ha pasado el filtro post proceso");
            } else {
            	StringBuilder mensajeDebug = new StringBuilder();
                mensajeDebug.append("<b>Debug resultados</b>\n");
            	mensajeDebug.append("Hay resultados post proceso a mostrar");
            }
            
            
            
            
                        

//            // 🔹 Generar HTML filtrando duplicados
//            StringBuilder html = new StringBuilder();
//            html.append("<html><body>");
//            html.append("<h2>Resultados Odds</h2>");
//            html.append("<table border='1' cellpadding='5' cellspacing='0' style='border-collapse: collapse;'>");
//            html.append("<tr style='background-color:#f2f2f2;'>");
//            html.append("<th>Pais</th><th>Competicion</th><th>Evento</th><th>Bookie</th><th>Rating</th><th>apuesta</th><th>Back</th><th>Lay</th><th>antiguedad</th>");
//            html.append("</tr>");
//
//            int nuevos = 0;
//            for (Odd odd : odds) {
//                    nuevos++;
//                    html.append("<tr>")
//                            .append("<td>").append(odd.getCountry()).append("</td>")
//                            .append("<td>").append(odd.getCompetition()).append("</td>")
//                            .append("<td>").append(odd.getEvent()).append("</td>")
//                            .append("<td>").append(getNombreBookie(odd.getBookie())).append("</td>")
//                            .append("<td><strong>").append(odd.getRating()).append("</strong></td>")
//                            .append("<td>").append(odd.getSelection()).append("</td>")
//                            .append("<td>").append(odd.getBackOdd()).append("</td>")
//                            .append("<td>").append(odd.getLayOdd()).append("</td>")
//                            .append("<td>").append(odd.getUpdate_time()).append("</td>")
//                            .append("</tr>");
//                
//            }
//
//            if (nuevos == 0) {
//                html.append("<tr><td colspan='9'>No hay nuevos odds respecto a la ejecución anterior.</td></tr>");
//            }
//
//            html.append("</table>");
//            html.append("</body></html>");

            // 🔹 Enviar resultado por email
           // sendEmail(html.toString());
            
            
         // 🔹 Generar mensaje de Telegram (resumen)
            StringBuilder mensaje = new StringBuilder();
            

            for (Odd odd : odds) {
            	mensaje = new StringBuilder();
            	            	
            		 if(odd.getNivelAlerta()==1) {
            			 mensaje.append("‼️🔥🔥‼️").append("\n");
            		 }
            		 if(odd.getNivelAlerta()==2) {
            			 mensaje.append("‼️‼️🔥🔥🔥🔥🔥🔥‼️‼️").append("\n");
            		 }
            		 mensaje.append("⚽ <b>").append(odd.getEvent()).append("</b>\n");
            		 mensaje.append("🏆 <b>").append(odd.getCompetition()).append(" (").append(odd.getCountry()).append(")</b>\n");
            		 mensaje.append("🗓️ <b>").append(odd.getsFechaPartido()).append("h").append("</b>\n\n");
            		 
            		 mensaje.append("🔔<u><b> 2UP SIMPLE </b></u>\n");
            		 mensaje.append("    🏛 <b>").append(getNombreBookie(odd.getBookie())).append("</b>\n");
            		 
            		 if (odd.getBookie().equals("39")) {
            			 mensaje.append("    📈 <b>").append(odd.getRating()).append("%</b> (").append(odd.getRatingOriginal()).append(")\n");
            		 } else {
            			 mensaje.append("    📈 <b>").append(odd.getRating()).append("%</b>\n");
            		 }
            		             		
            		 mensaje.append("    Ap: <b>").append(odd.getSelection()).append("</b>\n");
            		 
            		 if (odd.getBookie().equals("39")) {
            			 mensaje.append("    📋 Back: <b>").append(odd.getBackOdd()).append("</b> (").append(odd.getBackOddOriginal()).append(") | Lay: <b>").append(odd.getLayOdd()).append("</b>\n");	 
            		 } else {
            			 mensaje.append("    📋 Back: <b>").append(odd.getBackOdd()).append("</b> | Lay: <b>").append(odd.getLayOdd()).append("</b>\n"); 
            	   		 }
            	            		 
            		mensaje.append("    ⏱ ").append(odd.getUpdate_time()).append("\n");
            		mensaje.append("    🔗 <a href=\"https://www.betfair.es/exchange/football/market?id=").append(odd.getMarket_id()).append("\">Ver en Betfair</a>\n\n");
            		 
            		mensaje.append("🔔<u><b> 2WAY 2UP </b></u>\n");
             		mensaje.append("🟢<b>").append(odd.getEquipoHome()).append("</b>\n");
             		for (Odd o : odd.getMejoresHome()) {
             			 if (o.getBookie().equals("39")) {
             				mensaje.append("     ").append(getNombreBookie(o.getBookie())).append("->").append(o.getBackOdd()).append("(").append(o.getBackOddOriginal()).append(")").append("\n");	 
             			 } else {
             				mensaje.append("     ").append(getNombreBookie(o.getBookie())).append("->").append(o.getBackOdd()).append("\n"); 
             			 }
             		}
             		
             		
             		mensaje.append("🟢<b>").append("Empate").append("</b>\n");
             		for (Odd o : odd.getMejoresDraw()) {
             			if (o.getBookie().equals("39") || o.getBookie().equals("2")) {
             				mensaje.append("     ").append(getNombreBookie(o.getBookie())).append("->").append(o.getBackOdd()).append("(").append(o.getBackOddOriginal()).append(")").append("\n");	
             			} else {
             				mensaje.append("     ").append(getNombreBookie(o.getBookie())).append("->").append(o.getBackOdd()).append("\n");
             			}
             		}
             		
             		if(odd.getExchangeDraw()!=null) {
             			mensaje.append("     ").append("Exchange BACK ->").append(odd.getExchangeDraw().getBackOdd()).append("(").append(odd.getExchangeDraw().getBackOddOriginal()).append(")\n");
             			mensaje.append("       Liquidez:").append(odd.getExchangeDraw().getLayOdd()).append("€\n");
             		}
             		
             		
             		mensaje.append("🟢<b>").append(odd.getEquipoAway()).append("</b>\n");
             		for (Odd o : odd.getMejoresAway()) {
             			if (o.getBookie().equals("39")) {
             				mensaje.append("     ").append(getNombreBookie(o.getBookie())).append("->").append(o.getBackOdd()).append("(").append(o.getBackOddOriginal()).append(")").append("\n");	
             			} else {
             				mensaje.append("     ").append(getNombreBookie(o.getBookie())).append("->").append(o.getBackOdd()).append("\n");
             			}
             		}
             		
             		 if(odd.getNivelAlerta()==1) {
            			 mensaje.append("‼️🔥🔥‼️").append("\n");
            		 }
            		 if(odd.getNivelAlerta()==2) {
            			 mensaje.append("‼️‼️🔥🔥🔥🔥🔥🔥‼️‼️").append("\n");
            		 }
            		 
             	// 🔹 Enviar a Telegram
             		TelegramSender.sendTelegramMessage(mensaje.toString());	
               
            }

            
                    
            
            // 🔹 Guardar los odds actuales como histórico
            escribirCSV(CSV_FILE, lectura);

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        System.out.println("FIN EJECUCION");
    }
    
    
    private static Odd ajustaCuotaRating(Odd odd) {
    	
    		
    		if(odd.getBookie().equals("39") && !odd.getSelection().equalsIgnoreCase("empate")) {
    		System.out.println("AJUSTE CUOTA CODERE--> cuota Back: " + odd.getBackOdd() + "   rating: " + odd.getRating() + " cuota Lay: " + odd.getLayOdd() );
    		Double cuotaBack=Double.valueOf(odd.getBackOdd());
    		Double cuotaLay=Double.valueOf(odd.getLayOdd());
    		
    		Double cuotaBackReducida=0.0;
    		if(cuotaBack<=3.0) {
    			cuotaBackReducida=cuotaBack-restaCuotaCodere;	
    		} else if (cuotaBack<=4.5) {
    			cuotaBackReducida=cuotaBack-restaCuotaCodere -restaCuotaCodere;
    		} else {
    			cuotaBackReducida=cuotaBack-restaCuotaCodere -restaCuotaCodere -restaCuotaCodere;
    		}
    		    		
    		cuotaBackReducida=Math.round(cuotaBackReducida * 100.0) / 100.0;
    		Double layStake=100*cuotaBackReducida/(cuotaLay-0.02);
    		Double profit=layStake*(1-0.02)-100;
    		Double nuevoRating=((100+profit)/100)*100;
    		Double nuevoRatingRedondeado = Math.round(nuevoRating * 100.0) / 100.0;
    		
    		odd.setBackOdd(cuotaBackReducida.toString());
    		odd.setRating(nuevoRatingRedondeado.toString());
    		System.out.println("AJUSTE CUOTA CODERE--> Nueva cuota Back: " + odd.getBackOdd() + "  nuevo rating: " + odd.getRating() );
    	
    		}
    		
    		if(odd.getBookie().equals("2") && odd.getSelection().equalsIgnoreCase("empate")) {
    			
    			Double cuotaBack=Double.valueOf(odd.getBackOdd());
    			Double cuotaAumentada=cuotaBack * 1.04;
    			cuotaAumentada=Math.round(cuotaAumentada * 100.0) / 100.0;
    			
    			odd.setBackOdd(cuotaAumentada.toString());
    			
    		}
    		
    	
    	return odd;
    }
    
    private static boolean pasaFiltroDatos(Odd odd) {
    	
    	
    	//filtro Paises
    	ArrayList<String> filtroPaises=new ArrayList<String>();
    	filtroPaises.add("Argentina");
    	filtroPaises.add("Saudi Arabia");
    	filtroPaises.add("Arabia Saudí");
    	filtroPaises.add("Brasil");
    	filtroPaises.add("Estados Unidos");
    	filtroPaises.add("México");
    	filtroPaises.add("Mexico");
    	
    	   	
    	if(filtroPaises.contains(odd.getCountry())) {
    		System.out.println("Evento no pasa filtro pais --> " + odd.getCountry());
    		return false;
    	}
    	
    	
    	//filtro rating
    	// Hay un primer filtro de rating en la búsqueda que es el mínimo aqui se contrastan cuotas con ratings
    	Double rating=Double.valueOf(odd.getRating());
    	Double cuota=Double.valueOf(odd.getBackOdd());
    	if(cuota<5 && rating<95) {
    		System.out.println("Evento no pasa filtro rating/cuota --> " + odd.getRating() + "/" + odd.getBackOdd());
    		return false;
    	}
    	   
    	
    	return true;
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
                        o.getUpdate_time(), o.getCountry(), o.getTimeInMin().toString()));
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
                if (campos.length >= 10) {
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
                    o.setTimeInMin(Integer.valueOf(campos[9]));
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
    
    private static String crearUrlFiltroPeticionData(String uid, ArrayList<String> filtroBookies, String ratingInicial, String cuotaMinima, ArrayList<String> filtroApuestas, String filtroEventos) {
    	
    	String urlFiltro="";
    	
    	urlFiltro+="combinazioni=2&action=get_odds_data";
    	urlFiltro+="&uid=" + uid;
    	urlFiltro+="&refund=100&back_stake=100";
    	
    	if(!filtroEventos.isEmpty()) {
    		urlFiltro+="&name[]=" + filtroEventos;	
    	}
    	
    	if (!filtroBookies.isEmpty()) {
    		String sFiltroBookies="";
        	for (String bookie : filtroBookies) {
    			sFiltroBookies+="&filterbookies[]=" + bookie;
    		}
        	urlFiltro+=sFiltroBookies;
    	} else {
    		urlFiltro+="&filterbookies=";
    	}
    	
    	urlFiltro+="&bookies=-0,68,1,54,108,2,75,53,56,59,7,62,61,41,106,39,78,104,102,103,73,40,43,42,76,64,71,44,55,45,107,46,47,29,57,109,48,105,65,20,69,52,74";
    	
    	urlFiltro+="&rating-from=" + ratingInicial + "&rating-to=";
    	
    	urlFiltro+="&odds-from="+ cuotaMinima +"&odds-to=";
    	
    	urlFiltro+="&min-liquidity=&sort-column=4&sort-direction=desc";
    	
    	urlFiltro+="&offset=0&date-from=&date-to=&exchange=all&exchanges=all&sport=&betfair-commission=2&matchbook-commission=";
    	
    	String sFiltroTiposApuesta="";
    	for (String tipo : filtroApuestas) {
    		sFiltroTiposApuesta+="&bet-type[]=" + tipo;
		}
    	urlFiltro+=sFiltroTiposApuesta;
    	
    	urlFiltro+="&rating-type=normal";
    	
    	urlFiltro+="&roll-real-money=100&roll-bonus=100&roll-remaining=100&roll-rating=95&tz=-120";
    	    	
    	return urlFiltro;
    }
    
    private static StringBuilder crearPeticionData(String urlParameters, String urlConexion) {
    	StringBuilder response = new StringBuilder();
        try {
    	byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

        URL obj = new URL(urlConexion);
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
        String code=Integer.valueOf(responseCode).toString();
        StringBuilder mensajeDebug = new StringBuilder();
        mensajeDebug.append("<b>Debug Ejecucion</b>\n");
        
        if(responseCode!=200) {
        	mensajeDebug.append("resultado Petición HTTP: <b>").append(code).append("</b>\n");
        	//mensajeDebug.append("⚽ <b>").append(code).append("</b>\n");
                         	
        } else {
        	mensajeDebug.append("resultado Petición HTTP: <b>").append(code).append("</b>\n");
        	//mensajeDebug.append("⚽ <b>").append(code).append("</b>\n");
        }

       TelegramSender.sendTelegramMessageDebug(mensajeDebug.toString());	
        
        InputStream is = (responseCode >= 200 && responseCode < 300)
                ? conn.getInputStream()
                : conn.getErrorStream();

       
        try (BufferedReader in = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }
       
        } catch (Exception e) {
			e.printStackTrace();
			return response;
		}
        
        return response;
    	
    }
    
    
    public static List<Runner> MapearResultadosExchange(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        // Leer todo el JSON como árbol
        JsonNode root = mapper.readTree(json);

        // Navegar hasta el array de runners
        JsonNode runnersNode = root
                .path("eventTypes").get(0)
                .path("eventNodes").get(0)
                .path("marketNodes").get(0)
                .path("runners");

        // Convertir ese nodo en lista de objetos Runner
        return mapper.convertValue(runnersNode, new TypeReference<List<Runner>>() {});
    }

    
    private static ArrayList<Odd> mapearListaResultadosData(StringBuilder response) throws JsonMappingException, JsonProcessingException { 
    	
    	ArrayList<Odd> lectura=new ArrayList<Odd>();
    	
       	// 🔹 Procesar JSON con Jackson
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.toString());

        JsonNode dataArray = root.get("data");
        if (dataArray != null && dataArray.isArray()) {
            for (JsonNode item : dataArray) {
                Odd odd = new Odd();
                odd.setEvent(item.path("event").asText());
                odd.setBookie(item.path("bookie_id").asText());
                odd.setRating(item.path("rating").asText());
                odd.setRatingOriginal(item.path("rating").asText());
                odd.setBackOdd(item.path("back_odd").asText());
                odd.setBackOddOriginal(item.path("back_odd").asText());
                odd.setLayOdd(item.path("lay_odd").asText());
                odd.setSelection(item.path("selection").asText());
                odd.setCompetition(item.path("competition").asText());
                odd.setUpdate_time(item.path("update_time").asText());
                odd.setCountry(item.path("country").asText());
                odd.setMarket_id(item.path("market_id").asText());
                
                String parteHora = item.path("update_time").asText().split(":")[0];  
                Integer horaEntero = Integer.parseInt(parteHora);
                odd.setTimeInMin(horaEntero);
                
                
             // Definir el formato de entrada (tal como llega el String)
                DateTimeFormatter formatterEntrada = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                // Parsear el String a LocalDateTime
                LocalDateTime fecha = LocalDateTime.parse(item.path("open_date").asText(), formatterEntrada);
                odd.setFechaPartido(fecha);
                // Definir el formato de salida
                DateTimeFormatter formatterSalida = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                // Convertir a String en el nuevo formato
                String fechaFormateada = fecha.format(formatterSalida);
                odd.setsFechaPartido(fechaFormateada);
                
                
                //rebaja cuotas  (ej:codere)
                odd=ajustaCuotaRating(odd);
                
                
                //asignar Nivel Alerta
                Double cuota=Double.valueOf(odd.getBackOdd());
                Double rating=Double.valueOf(odd.getRating());
                Integer nivelAlerta=0;
                if(cuota>5) {
                	if(rating>93.5) {
                		nivelAlerta=1;
                	}
                	if(rating>95) {
                		nivelAlerta=2;
                	}
                	
                } else {
                	if(rating>96.25) {
                		nivelAlerta=1;
                	}
                	if(rating>97.5) {
                		nivelAlerta=2;
                	}
                	
                }
                odd.setNivelAlerta(nivelAlerta);
                
                
                lectura.add(odd);
            }
        }

    	
    	return lectura;
    	
    }
    
    public static List<Event> mapearListaResultadosEvents(String json) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, new TypeReference<List<Event>>(){});
    }
    
    
    
    private static String crearUrlFiltroPeticionExchange(String marketUid) {
    	
    	String urlFiltro="";
    	
    	urlFiltro+="currencyCode=EUR&locale=es";

    	urlFiltro+="&marketIds=" + marketUid;
    	
    	urlFiltro+="&rollupLimit=10&rollupModel=STAKE&types=MARKET_STATE,MARKET_RATES,MARKET_DESCRIPTION,EVENT,RUNNER_DESCRIPTION,RUNNER_STATE,RUNNER_EXCHANGE_PRICES_BEST,RUNNER_METADATA,MARKET_LICENCE,MARKET_LINE_RANGE_INFO";
    	
    
    	    	
    	return urlFiltro;
    }
    
    
   
    public static StringBuilder crearPeticionEvents(String name) throws IOException {
        // Montar parámetros POST
        String urlParameters = "name=" + URLEncoder.encode(name, "UTF-8");

        // Preparar la conexión
        URL url = new URL(urlEvents);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");

        // Headers principales
        conn.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        conn.setRequestProperty("Origin", "https://www.ninjabet.es");
        conn.setRequestProperty("Referer", "https://www.ninjabet.es/oddsmatcher");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36");
        conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
        //conn.setRequestProperty("Cookie", cookies);

        // Permitir envío de datos
        conn.setDoOutput(true);

        // Enviar body POST
        try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
            wr.write(urlParameters.getBytes(StandardCharsets.UTF_8));
        }

        // Leer respuesta
        int responseCode = conn.getResponseCode();
        InputStream inputStream = (responseCode == 200)
                ? conn.getInputStream()
                : conn.getErrorStream();

        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

        return response;
    }
    
    
    public static Odd rellenaCuotas(Odd odd) throws Exception {
    	
        String nombreEvento=odd.getEvent();
        StringBuilder peticionEventos=crearPeticionEvents(nombreEvento);
    	System.out.println(peticionEventos.toString());
    	
    	List<Event> eventos=mapearListaResultadosEvents(peticionEventos.toString());
    	
    	String codigosEventos="";
    	
		if (!eventos.isEmpty()) {
			for (Event event : eventos) {
				codigosEventos += event.getId() + ",";
			}
			
			codigosEventos=codigosEventos.substring(0, codigosEventos.length() - 1);
		}
		
		
		ArrayList<Odd> lectura = new ArrayList<>();
		
		String urlParameters=crearUrlFiltroPeticionData(uid, filtroBookies2UP2WAY, "1", "1", filtroApuestasHome, codigosEventos);
		StringBuilder response= crearPeticionData(urlParameters, urlData);
    	lectura=mapearListaResultadosData(response);
    	lectura.sort(Comparator.comparingDouble(o -> Double.parseDouble(o.getBackOdd())));
    	Collections.reverse(lectura);
		for (Odd o : lectura.subList(0, Math.min(3, lectura.size()))) {
			odd.setEquipoHome(o.getSelection());
			odd.getMejoresHome().add(o);
		}
		
		urlParameters=crearUrlFiltroPeticionData(uid, filtroBookiesVacio, "1", "1", filtroApuestasDraw, codigosEventos);
		response= crearPeticionData(urlParameters, urlData);
		lectura=mapearListaResultadosData(response);
		lectura.sort(Comparator.comparingDouble(o -> Double.parseDouble(o.getBackOdd())));
    	Collections.reverse(lectura);
		for (Odd o : lectura.subList(0, Math.min(3, lectura.size()))) {
			odd.getMejoresDraw().add(o);
		}
		
		
		//Buscamos la cuota "empate" en betfair exchange
		urlParameters=crearUrlFiltroPeticionExchange(odd.getMarket_id());
		response= crearPeticionData(urlParameters, urlExchange);
		List<Runner> listaExchange=MapearResultadosExchange(response.toString());
		
		Double mejorCuotaDrawExchange=0.0;
		Double liquidez=0.0;
		if (listaExchange!=null && !listaExchange.isEmpty()) {
			for (Runner runner : listaExchange) {
				if (runner.getDescription().getRunnerName().equalsIgnoreCase("empate")) {
					List<PriceSize> cuotas = runner.getExchange().getAvailableToBack();
					if(cuotas!=null && !cuotas.isEmpty()) {
					for (PriceSize p : cuotas) {
						if(p.getPrice()>mejorCuotaDrawExchange) {
							mejorCuotaDrawExchange=p.getPrice();
							liquidez=p.getSize();
						}
						}
					}
				}
			}
			
			if(mejorCuotaDrawExchange>0) {
				Odd ex=new Odd();
				ex.setBackOddOriginal(mejorCuotaDrawExchange.toString());
				double reducida=(mejorCuotaDrawExchange-1)*(0.98)+1;
				Double reducidaRedondeada = Math.round(reducida * 100.0) / 100.0;
				ex.setBackOdd(reducidaRedondeada.toString());
				ex.setLayOdd(liquidez.toString());
				odd.setExchangeDraw(ex);
			}
		}
		
		
		urlParameters=crearUrlFiltroPeticionData(uid, filtroBookies2UP2WAY, "1", "1", filtroApuestasAway, codigosEventos);
		response= crearPeticionData(urlParameters, urlData);
		lectura=mapearListaResultadosData(response);
		lectura.sort(Comparator.comparingDouble(o -> Double.parseDouble(o.getBackOdd())));
    	Collections.reverse(lectura);
		for (Odd o : lectura.subList(0, Math.min(3, lectura.size()))) {
			odd.setEquipoAway(o.getSelection());
			odd.getMejoresAway().add(o);
		}
						
    	
    	return odd;
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
        private Integer timeInMin;
        
        private String backOddOriginal;
        private String ratingOriginal;
        
        private LocalDateTime fechaPartido;
        private String sFechaPartido;
        
        private String market_id;
        
        ArrayList<Odd> mejoresHome=new ArrayList<Odd>();
        ArrayList<Odd> mejoresDraw=new ArrayList<Odd>();
        ArrayList<Odd> mejoresAway=new ArrayList<Odd>();
        
        Odd exchangeDraw;
        
        Integer nivelAlerta;
        
        String equipoHome="";
        String equipoAway="";

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
		public Integer getTimeInMin() {
			return timeInMin;
		}
		public void setTimeInMin(Integer timeInMin) {
			this.timeInMin = timeInMin;
		}
		public String getMarket_id() {
			return market_id;
		}
		public void setMarket_id(String market_id) {
			this.market_id = market_id;
		}
		public String getBackOddOriginal() {
			return backOddOriginal;
		}
		public void setBackOddOriginal(String backOddOriginal) {
			this.backOddOriginal = backOddOriginal;
		}
		public String getRatingOriginal() {
			return ratingOriginal;
		}
		public void setRatingOriginal(String ratingOriginal) {
			this.ratingOriginal = ratingOriginal;
		}
		public ArrayList<Odd> getMejoresHome() {
			return mejoresHome;
		}
		public void setMejoresHome(ArrayList<Odd> mejoresHome) {
			this.mejoresHome = mejoresHome;
		}
		public ArrayList<Odd> getMejoresDraw() {
			return mejoresDraw;
		}
		public void setMejoresDraw(ArrayList<Odd> mejoresDraw) {
			this.mejoresDraw = mejoresDraw;
		}
		public ArrayList<Odd> getMejoresAway() {
			return mejoresAway;
		}
		public void setMejoresAway(ArrayList<Odd> mejoresAway) {
			this.mejoresAway = mejoresAway;
		}
		public String getEquipoHome() {
			return equipoHome;
		}
		public void setEquipoHome(String equipoHome) {
			this.equipoHome = equipoHome;
		}
		public String getEquipoAway() {
			return equipoAway;
		}
		public void setEquipoAway(String equipoAway) {
			this.equipoAway = equipoAway;
		}
		
		public String getsFechaPartido() {
			return sFechaPartido;
		}
		public void setsFechaPartido(String sFechaPartido) {
			this.sFechaPartido = sFechaPartido;
		}
		public LocalDateTime getFechaPartido() {
			return fechaPartido;
		}
		public void setFechaPartido(LocalDateTime fechaPartido) {
			this.fechaPartido = fechaPartido;
		}
		public Odd getExchangeDraw() {
			return exchangeDraw;
		}
		public void setExchangeDraw(Odd exchangeDraw) {
			this.exchangeDraw = exchangeDraw;
		}
		public Integer getNivelAlerta() {
			return nivelAlerta;
		}
		public void setNivelAlerta(Integer nivelAlerta) {
			this.nivelAlerta = nivelAlerta;
		}
	
		
        
    }
    
    public static class Event {
        private String id;
        private String value;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
        
        
       
    }
}
