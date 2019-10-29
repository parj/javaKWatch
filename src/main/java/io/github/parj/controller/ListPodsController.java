package io.github.parj.controller;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.github.parj.service.K8SClient;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/list")
public class ListPodsController {
    private static final Logger logger = LoggerFactory.getLogger(ListPodsController.class);

    @Autowired
    private K8SClient k8SClient;

    @RequestMapping(value = "/pods", method = RequestMethod.GET)
    public HashMap<String, String> listPods() {
        logger.info("Trying to list Pods");

        HashMap<String, String> map = new HashMap<>();
        PodList pods = k8SClient.getClient().pods().list();
        logger.info("Found " + pods.getItems().size() + " pods");

        for (Pod pod : pods.getItems())
            map.put("pod_" + pod.hashCode(), pod.getMetadata().getName());

        return map;
    }

    @RequestMapping(value = "/ns", method = RequestMethod.GET)
    public HashMap<String, String> listNamespaces() {
        logger.info("Trying to list Namespaces");

        HashMap<String, String> map = new HashMap<>();
        NamespaceList myNs = k8SClient.getClient().namespaces().list();
        logger.info("Found " + myNs.getItems().size() + " namespaces");

        for (Namespace ns : myNs.getItems())
            map.put("ns_" + ns.hashCode(), ns.getMetadata().getName());

        return map;
    }
}
