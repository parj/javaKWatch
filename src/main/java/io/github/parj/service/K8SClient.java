package io.github.parj.service;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class K8SClient {
    private static final Logger logger = LoggerFactory.getLogger(K8SClient.class);
    private static final String K8S_URL = "https://kubernetes.default.svc";


    private static KubernetesClient client;

    public static void setClient(KubernetesClient client2) {
        client = client2;
    }

    public static KubernetesClient getClient() {
        if (client == null) {
            logger.debug("Creating Kubernetes client");
            logger.info("Connecting to Kubernetes cluster with url " + K8S_URL );
            Config config = new ConfigBuilder().withMasterUrl(K8S_URL).build();
            client = new DefaultKubernetesClient(config);
        }
        return client;
    }
}
