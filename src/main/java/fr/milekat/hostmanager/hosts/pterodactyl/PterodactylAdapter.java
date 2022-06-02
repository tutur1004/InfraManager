package fr.milekat.hostmanager.hosts.pterodactyl;

import fr.milekat.hostmanager.Main;
import fr.milekat.hostmanager.api.classes.Instance;
import fr.milekat.hostmanager.api.classes.InstanceState;
import fr.milekat.hostmanager.api.events.ServerCreatedEvent;
import fr.milekat.hostmanager.api.events.ServerDeletedEvent;
import fr.milekat.hostmanager.api.events.ServerDeletionEvent;
import fr.milekat.hostmanager.hosts.HostExecutor;
import fr.milekat.hostmanager.hosts.exeptions.HostExecuteException;
import net.md_5.bungee.api.ProxyServer;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;

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
        //  } catch (LoginException throwable) {
        //      throw new HostExecuteException(throwable, "Account API key is incorrect");
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
        //  } catch (LoginException throwable) {
        //      throw new HostExecuteException(throwable,
        //              "Admin API key is incorrect or doesn't have the required permissions");
        //  }
        return false;
    }

    /**
     * Create a pterodactyl server !
     * @param instance server params
     */
    @Override
    public void createServer(Instance instance) throws HostExecuteException {
        JSONObject server = PterodactylRequests.setupServer(instance);

        try {
            instance.setCreation(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                    .parse(server.getString("attributes.created_at")));
        } catch (ParseException e) {
            e.printStackTrace();
            Main.getHostLogger().warning("Error while trying to parse date: " +
                    server.getString("attributes.created_at"));
        }
        instance.setServerId(server.getString("attributes.id"));
        instance.setState(InstanceState.CREATING);

        ServerCreatedEvent event = new ServerCreatedEvent(instance);
        ProxyServer.getInstance().getPluginManager().callEvent(event);
    }

    @Override
    public void deleteServer(Instance instance) throws HostExecuteException {
        ServerDeletionEvent deletionEvent = new ServerDeletionEvent(instance);
        ProxyServer.getInstance().getPluginManager().callEvent(deletionEvent);
        if (deletionEvent.isCancelled()) return;

        PterodactylRequests.deleteServer(instance);

        ServerDeletedEvent deletedEvent = new ServerDeletedEvent(instance);
        ProxyServer.getInstance().getPluginManager().callEvent(deletedEvent);
    }
}
