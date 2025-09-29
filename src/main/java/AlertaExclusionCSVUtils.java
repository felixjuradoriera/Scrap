import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AlertaExclusionCSVUtils {
	
	private static final String CSV_EXCLUDE_ALERTS = "C:"+ File.separator +"BOT" + File.separator +"CONF"+File.separator+ "alertasExclusiones.csv";

	/**
	 * Añade un objeto AlertaExclusion al CSV si no existe ya con mismo chatId y
	 * market_id.
	 */
	public static void addIfNotExists(AlertaExclusion alerta) throws IOException {
		Path path = Paths.get(CSV_EXCLUDE_ALERTS);

		// Creamos el fichero si no existe
		if (Files.notExists(path)) {
			Files.createFile(path);
		}

		// Leemos todas las líneas
		List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

		boolean exists = false;
		for (String line : lines) {
			String[] parts = line.split(",", -1);
			if (parts.length >= 4) {
				Long chatId = Long.parseLong(parts[0]);
				String marketId = parts[1];
				if (chatId.equals(alerta.getChatId()) && marketId.equals(alerta.getMarket_id())) {
					exists = true;
					break;
				}
			}
		}

		if (!exists) {
			// Formato CSV: chatId,market_id,sFechaPartido
			String newLine = alerta.getChatId() + "," + alerta.getMarket_id() + "," + alerta.getsFechaPartido()+ "," + alerta.getEvento();
			// Añadimos al final
			Files.write(path, (newLine + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
					StandardOpenOption.APPEND);
		}
	}

	/**
	 * Lee el CSV y devuelve una lista de AlertaExclusion.
	 */
	public static List<AlertaExclusion> loadFromCSV() throws IOException {
		Path path = Paths.get(CSV_EXCLUDE_ALERTS);
		List<AlertaExclusion> lista = new ArrayList<>();

		if (Files.notExists(path)) {
			return lista; // vacío
		}

		try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.trim().isEmpty())
					continue;
				String[] parts = line.split(",", -1);
				if (parts.length >= 3) {
					AlertaExclusion alerta = new AlertaExclusion();
					alerta.setChatId(Long.parseLong(parts[0]));
					alerta.setMarket_id(parts[1]);
					alerta.setsFechaPartido(parts[2]);
					alerta.setEvento(parts[3]);
					lista.add(alerta);
				}
			}
		}

		return lista;
	}
	
	
	public static List<AlertaExclusion> filtrarAlertasPosteriores(List<AlertaExclusion> alertas) {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
	    LocalDateTime ahora = LocalDateTime.now();

	    return alertas.stream()
	            .filter(a -> {
	                try {
	                    LocalDateTime fechaAlerta = LocalDateTime.parse(a.getsFechaPartido(), formatter);
	                    return fechaAlerta.isAfter(ahora); // solo posteriores
	                } catch (Exception e) {
	                    // si hay error de parseo la excluimos
	                    return false;
	                }
	            })
	            .collect(Collectors.toList());
	}
	
	
	public static void escribirAlertasEnCsv(List<AlertaExclusion> alertas) {
	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_EXCLUDE_ALERTS, false))) {
	        // Cabecera opcional:
	        // writer.write("chatId,market_id,sFechaPartido,evento");
	        // writer.newLine();

	        for (AlertaExclusion alerta : alertas) {
	            writer.write(
	                    alerta.getChatId() + "," +
	                    alerta.getMarket_id() + "," +
	                    alerta.getsFechaPartido() + "," +
	                    (alerta.getEvento() != null ? alerta.getEvento() : "")
	            );
	            writer.newLine();
	        }

	        System.out.println("✅ CSV sobrescrito correctamente en: " + CSV_EXCLUDE_ALERTS);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	
}
