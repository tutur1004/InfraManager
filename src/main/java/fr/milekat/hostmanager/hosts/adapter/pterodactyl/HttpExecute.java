package fr.milekat.hostmanager.hosts.adapter.pterodactyl;

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
        return execute(url, method, key, body, null);
    }

    public static JSONObject execute(URL url, String method, String key, String body, String ignoreErr)
            throws HostExecuteException {
        try  {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod(method);
            connection.setRequestProperty("Authorization", "Bearer " + key);
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");

            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            connection.connect();

            if (body!=null) {
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = body.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                } catch (IOException exception) {
                    if (exception.getMessage().contains(ignoreErr)) return null;
                    debug(url, method, key, body);
                    throw new HostExecuteException(exception, "Pterodactyl API call error, during output writing, URL: " + url);
                }
            }

            try (InputStreamReader iSR = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8);
                 BufferedReader br = new BufferedReader(iSR)) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                if (response.length()==0) return new JSONObject();
                return new JSONObject(response.toString());
            } catch (IOException exception) {
                if (exception.getMessage().contains(ignoreErr)) return null;
                debug(url, method, key, body);
                throw new HostExecuteException(exception,
                        "Pterodactyl API call error, during request read, URL: " + url);
            }
        } catch (IOException exception)  {
            if (exception.getMessage().contains(ignoreErr)) return null;
            debug(url, method, key, body);
            throw new HostExecuteException(exception, "Pterodactyl API call error during request, URL: " + url);
        }
    }

    private static void debug(URL url, String method, String key, String body) {
        if (Main.DEBUG) {
            Main.getHostLogger().warning("Url: " + url);
            Main.getHostLogger().warning("RequestMethod: " + method);
            Main.getHostLogger().warning("Bearer: " + key);
            Main.getHostLogger().warning("Body: " + body);
        }
    }
}
