package io.github.parj.controller;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.github.parj.exception.NotFoundException;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodList;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;

@RestController
@RequestMapping("/list")
public class ListPodsController {

    private KubernetesClient getClient() {
        Config config = new ConfigBuilder().withMasterUrl("https://kubernetes.default.svc").build();
        return new DefaultKubernetesClient(config);
    }

    @RequestMapping(value = "/pods", method = RequestMethod.GET)
    public HashMap<String, String> listPods() {
        HashMap<String, String> map = new HashMap<>();
        PodList pods = getClient().pods().list();

        for (Pod pod : pods.getItems())
            map.put("pod_" + pod.hashCode(), pod.getMetadata().getName());

        return map;
    }

    @RequestMapping(value = "/ns", method = RequestMethod.GET)
    public HashMap<String, String> listNamespaces() {
        HashMap<String, String> map = new HashMap<>();
        NamespaceList myNs = getClient().namespaces().list();
        for (Namespace ns : myNs.getItems())
            map.put("ns_" + ns.hashCode(), ns.getMetadata().getName());

        return map;
    }
}
