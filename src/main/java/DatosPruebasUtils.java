import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DatosPruebasUtils {
	
	private static final String JSON_FILE = "C:"+ File.separator +"BOT" + File.separator +"CONF"+File.separator+ "datosPruebas.json";
	
	public static void guardarJsonEnArchivo(StringBuilder json) {
	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(JSON_FILE))) {
	        writer.write(json.toString());
	        System.out.println("✅ JSON guardado en: " + JSON_FILE);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	
	
	public static StringBuilder leerJsonDeArchivo() {
	    StringBuilder contenido = new StringBuilder();
	    try (BufferedReader reader = new BufferedReader(new FileReader(JSON_FILE))) {
	        String linea;
	        while ((linea = reader.readLine()) != null) {
	            contenido.append(linea).append("\n");
	        }
	        System.out.println("✅ JSON leído de: " + JSON_FILE);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return contenido;
	}
	
}
