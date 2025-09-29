import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class UsersUtils {
	
	
	private static final String CSV_USERS = "C:"+ File.separator +"BOT" + File.separator +"CONF"+File.separator+ "users.csv";
	
	
	
    public static List<User> readUsers() {
        List<User> users = new ArrayList<>();

        if (!Files.exists(Paths.get(CSV_USERS))) {
            return users; // si no existe, devolvemos lista vacía
        }

        try (BufferedReader br = new BufferedReader(new FileReader(CSV_USERS))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    User user = new User();
                    user.setChatId(Long.valueOf(parts[0].trim()));
                    user.setName(parts[1].trim());
                    users.add(user);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return users;
    }


}
