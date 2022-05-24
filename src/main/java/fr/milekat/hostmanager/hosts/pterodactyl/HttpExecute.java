package fr.milekat.hostmanager.hosts.pterodactyl;

import fr.milekat.hostmanager.Main;
import fr.milekat.hostmanager.hosts.exeptions.HostExecuteException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpExecute {
    public static JSONObject execute(URL url, String method, String key, String body) throws HostExecuteException {
        try  {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod(method);
            connection.setRequestProperty("Authorization", "Bearer " + key);
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = body.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            } catch (IOException exception) {
                if (Main.DEBUG) {
                    Main.getHostLogger().warning("Url: " + url);
                    Main.getHostLogger().warning("RequestMethod: " + method);
                    Main.getHostLogger().warning("Bearer: " + key);
                    Main.getHostLogger().warning("Body: " + body);
                    exception.printStackTrace();
                }
                throw new HostExecuteException(exception, "Pterodactyl API call error, during output writing, URL: " + url);
            }

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))
            ) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return new JSONObject(response.toString());
            } catch (IOException exception) {
                if (Main.DEBUG) {
                    Main.getHostLogger().warning("Url: " + url);
                    Main.getHostLogger().warning("RequestMethod: " + method);
                    Main.getHostLogger().warning("Bearer: " + key);
                    Main.getHostLogger().warning("Body: " + body);
                    exception.printStackTrace();
                }
                throw new HostExecuteException(exception,
                        "Pterodactyl API call error, during request read, URL: " + url);
            }
        } catch (IOException exception)  {
            if (Main.DEBUG) {
                Main.getHostLogger().warning("Url: " + url);
                Main.getHostLogger().warning("RequestMethod: " + method);
                Main.getHostLogger().warning("Bearer: " + key);
                Main.getHostLogger().warning("Body: " + body);
                exception.printStackTrace();
            }
            throw new HostExecuteException(exception, "Pterodactyl API call error during request, URL: " + url);
        }
    }
}
