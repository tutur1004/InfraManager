package fr.milekat.infra.manager.common.hosts.adapter.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.AccessMode;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import fr.milekat.infra.manager.api.classes.Instance;
import fr.milekat.infra.manager.common.Main;
import fr.milekat.infra.manager.common.hosts.HostExecutor;
import fr.milekat.infra.manager.common.hosts.Utils;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DockerAdapter implements HostExecutor {
    private final DockerClient client;
    private final List<String> networkIds = Main.getConfig().getStringList("host.docker.networks-id");
    private final String networkName = Main.getConfig().getString("host.docker.network-name");

    public DockerAdapter() {
        DockerClientConfig standard = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(Main.getConfig().getString("host.docker.endpoint"))
                .build();
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(standard.getDockerHost())
                .sslConfig(standard.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();
        client = DockerClientImpl.getInstance(standard, httpClient);
        if (Main.DEBUG) {
            Main.getLogger().info("Docker version: " + client.versionCmd().exec().getVersion() +
                    " Docker API version: " + client.versionCmd().exec().getApiVersion());
        }
    }

    /**
     * Check if docker client is reachable
     * Warning ! This method will block the thread until the check is passed !
     */
    @Override
    public boolean checkAvailability() {
        return client!=null;
    }

    /**
     * Create a host server (In this case, a docker container) !
     * @param instance server params
     */
    @Override
    public void createServer(@NotNull Instance instance) {
        CreateContainerCmd createContainerCmd = client.createContainerCmd(instance.getGame().getImage())
                .withName(instance.getName())
                .withImage(instance.getGame().getImage())
                .withEnv(Utils.getEnvVars(instance)
                        .entrySet()
                        .stream()
                        .map(s -> s.getKey() + "=" + s.getValue())
                        .collect(Collectors.toList())
                )
                .withStdinOpen(true)
                .withTty(true);
        //  Add mount point for share-feature
        if (Main.getConfig().getBoolean("host.settings.share-feature.enable")) {
            createContainerCmd.withHostConfig(HostConfig.newHostConfig().withBinds(new Bind(
                    Main.getConfig().getString("host.settings.share-feature.path"),
                    new Volume("/data"),
                    AccessMode.ro)));
        }
        //  Create container
        CreateContainerResponse containerResponse = createContainerCmd.exec();
        instance.setServerId(containerResponse.getId());
        //  Add docker networks
        networkIds.forEach(network -> client.connectToNetworkCmd()
                .withContainerId(instance.getServerId())
                .withNetworkId(network)
                .exec()
        );
        //  Start container
        client.startContainerCmd(instance.getServerId()).exec();
        //  Remove unused networks (Default)
        client.inspectContainerCmd(instance.getServerId()).exec()
                .getNetworkSettings()
                .getNetworks()
                .forEach((name, network) -> {
                    if (name.equalsIgnoreCase(networkName)) {
                        instance.setHostname(network.getIpAddress());
                    } else {
                        if (!networkIds.contains(network.getNetworkID())) {
                            client.disconnectFromNetworkCmd()
                                    .withContainerId(instance.getServerId())
                                    .withNetworkId(Objects.requireNonNull(network.getNetworkID()))
                                    .exec();
                        }
                    }
                });
        //  Update creation date
        instance.setCreation(new Date());
    }

    /**
     * Remove a docker container !
     * @param instance server params
     */
    @Override
    public void deleteServer(@NotNull Instance instance) {
        client.removeContainerCmd(instance.getServerId()).withForce(true).exec();
    }
}
