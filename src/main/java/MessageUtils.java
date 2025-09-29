import java.util.HashMap;
import java.util.Map;

public class MessageUtils {
	
	
	
	
	public static StringBuilder createAlerta(Odd odd) {
		
	 	StringBuilder mensaje = new StringBuilder();
    	
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
		
		
		
		return mensaje;
		
		
		
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
	
	

}
