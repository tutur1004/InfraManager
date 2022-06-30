package fr.milekat.hostmanager.hosts.pterodactyl;

import fr.milekat.hostmanager.Main;
import fr.milekat.hostmanager.api.classes.Instance;
import fr.milekat.hostmanager.api.classes.InstanceState;
import fr.milekat.hostmanager.api.events.ServerCreatedEvent;
import fr.milekat.hostmanager.api.events.ServerDeletedEvent;
import fr.milekat.hostmanager.api.events.ServerDeletionEvent;
import fr.milekat.hostmanager.hosts.HostExecutor;
import fr.milekat.hostmanager.hosts.bungee.ServerManager;
import fr.milekat.hostmanager.hosts.exeptions.HostExecuteException;
import fr.milekat.hostmanager.storage.exeptions.StorageExecuteException;
import net.md_5.bungee.api.ProxyServer;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;

public class PterodactylAdapter implements HostExecutor {
    /**
     * Check if pterodactyl API is reachable
     * Warning ! This method will block the thread until both keys are checked !
     */
    @Override
    public boolean checkAvailability() throws HostExecuteException {
        // TODO: 25/05/2022 all the checks
        //  Old try..
        //  try {
        //  Check if account api key is valid
        //  getAccount().retrieveAccount().execute();
        //  } catch (LoginException exception) {
        //      throw new HostExecuteException(exception, "Account API key is incorrect");
        //  }
        //  try {
        //      //  Check if admin key has all needed permissions
        //      getAdmin().retrieveAllocations().execute();
        //      getAdmin().retrieveServers().execute();
        //      //  Retrieve Location
        //      //  Retrieve Egg
        //      /*
        //      Nest nest = getAdmin().retrieveNestById(Main.getFileConfig().getLong("host.pterodactyl.nest")).execute();
        //      this.egg = Admin().retrieveEggById(nest, Main.getFileConfig().getLong("host.pterodactyl.egg")).execute();
        //      *//*
        //      this.egg = new ApplicationEggImpl(new JSONObject()
        //              .put("id", Main.getFileConfig().getString("host.pterodactyl.egg"))
        //              .put("nest", Main.getFileConfig().getString("host.pterodactyl.nest")), null);
        //      this.owner = new ApplicationUserImpl(new JSONObject()
        //              .put("id", Main.getFileConfig().getString("host.pterodactyl.account.id")), null);
        //  } catch (LoginException exception) {
        //      throw new HostExecuteException(exception,
        //              "Admin API key is incorrect or doesn't have the required permissions");
        //  }
        return true;
    }

    /**
     * Create a pterodactyl server !
     * @param instance server params
     */
    @Override
    public void createServer(Instance instance) throws HostExecuteException, StorageExecuteException {
        if (instance.getPort()==0) {
            Integer port = getAvailablePort();
            if (port!=null) {
                instance.setPort(port);
            } else {
                Main.getHostLogger().warning("No port available.");
                throw new HostExecuteException("No port available.");
            }
        }

        JSONObject server = PterodactylRequests.setupServer(instance);

        // TODO: 28/06/2022 Add manager accounts
        //  (Multi levels of permissions ? Reader, Writer, Admin ?)

        instance.setState(InstanceState.CREATING);

        if (server.has("attributes")) {
            JSONObject attributes = server.getJSONObject("attributes");
            instance.setServerId(String.valueOf(attributes.getInt("id")));
            instance.setName(instance.getName() + "_#" + instance.getServerId());
            try {
                instance.setCreation(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                        .parse(attributes.getString("created_at")
                                .replaceAll("\\+00:00$", "")
                        )
                );
            } catch (ParseException e) {
                e.printStackTrace();
                Main.getHostLogger().warning("Error while trying to parse date: " +
                        attributes.getString("created_at"));
                instance.setCreation(new Date());
            }
        } else {
            instance.setCreation(new Date());
            instance.setServerId(null);
        }

        Main.getStorage().createInstance(instance);

        ServerManager.addServer(Main.HOST_BUNGEE_SERVER_PREFIX + instance.getName(), instance.getPort());

        ServerCreatedEvent event = new ServerCreatedEvent(instance);
        ProxyServer.getInstance().getPluginManager().callEvent(event);
    }

    @Override
    public void deleteServer(Instance instance) throws HostExecuteException {
        try {
            if (Main.getStorage().getActiveInstances()
                    .stream()
                    .noneMatch(o -> o.getServerId().equalsIgnoreCase(instance.getServerId()))
            ) {
                return;
            }
        } catch (StorageExecuteException e) {
            if (Main.DEBUG) {
                Main.getHostLogger().warning("Trying to delete an invalid or inactive server");
            }
            return;
        }

        ServerDeletionEvent deletionEvent = new ServerDeletionEvent(instance);
        ProxyServer.getInstance().getPluginManager().callEvent(deletionEvent);
        if (deletionEvent.isCancelled()) return;

        PterodactylRequests.deleteServer(instance);
        ServerManager.removeServer(Main.HOST_BUNGEE_SERVER_PREFIX + instance.getName());

        try {
            instance.setState(InstanceState.TERMINATED);
            Main.getStorage().updateInstance(instance);
        } catch (StorageExecuteException e) {
            throw new HostExecuteException(e, "Can't update storage after server deletion");
        }
        ServerDeletedEvent deletedEvent = new ServerDeletedEvent(instance);
        ProxyServer.getInstance().getPluginManager().callEvent(deletedEvent);
    }

    /**
     * Get an available port
     */
    private Integer getAvailablePort() throws StorageExecuteException {
        return Main.getStorage().findAvailablePort(Main.getFileConfig()
                .getList("host.ports")
                .stream()
                .map(o -> Integer.valueOf(o.toString()))
                .collect(Collectors.toList()));
    }
}
