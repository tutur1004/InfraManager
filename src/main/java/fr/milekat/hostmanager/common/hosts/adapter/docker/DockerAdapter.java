package fr.milekat.hostmanager.common.hosts.adapter.docker;

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
import fr.milekat.hostmanager.api.classes.Instance;
import fr.milekat.hostmanager.common.Main;
import fr.milekat.hostmanager.common.hosts.HostExecutor;
import fr.milekat.hostmanager.common.hosts.Utils;

import java.time.Duration;
import java.util.Date;
import java.util.stream.Collectors;

public class DockerAdapter implements HostExecutor {
    private final DockerClient client;

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
        client.pingCmd().exec();
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
     * Create a docker server (In this case, a docker container) !
     *
     * @param instance server params
     */
    @Override
    public void createServer(Instance instance) {
        CreateContainerCmd createContainerCmd = client.createContainerCmd(instance.getGame().getImage())
                .withName(instance.getName())
                .withImage(instance.getGame().getImage())
                .withEnv(Utils.getEnvVars(instance)
                        .entrySet()
                        .stream()
                        .map(s -> s.getKey() + "=" + s.getValue())
                        .collect(Collectors.toList())
                )
                .withHostConfig(HostConfig.newHostConfig().withMemory((long) instance.getGame().getRequirements()));
        if (Main.getConfig().getBoolean("host.settings.share-feature.enable")) {
            createContainerCmd.withHostConfig(HostConfig.newHostConfig().withBinds(
                    new Bind(Main.getConfig().getString("host.settings.share-feature.path"),
                            new Volume("/data"),
                            AccessMode.ro)));
        }
        CreateContainerResponse containerResponse = createContainerCmd.exec();
        instance.setServerId(containerResponse.getId());

        client.connectToNetworkCmd().withContainerId(instance.getServerId())
                .withNetworkId(Main.getConfig().getString("host.docker.network-id"))
                .exec();

        // TODO: 06/09/2022 Get network ip ?
        /*
        client.inspectContainerCmd(instance.getServerId()).exec().getNetworkSettings().getNetworks()
                .entrySet()
                .stream()
                .filter(network -> Objects.requireNonNull(network.getValue().getNetworkID())
                        .equalsIgnoreCase(Main.getConfig().getString("host.docker.network-id")))
                        .forEach(instance.setIp());
        */

        client.startContainerCmd(instance.getServerId()).exec();

        instance.setCreation(new Date());
    }

    /**
     * Remove a docker container !
     * @param instance server params
     */
    @Override
    public void deleteServer(Instance instance) {
        client.removeContainerCmd(instance.getServerId()).exec();
    }
}
