package io.github.parj.controller;

import io.github.parj.exception.NotFoundException;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodList;
import io.kubernetes.client.util.Config;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;

@RestController
@RequestMapping("/list")
public class ListPodsController {

    @RequestMapping(value = "/pods", method = RequestMethod.GET)
    public HashMap<String, String> listPods() {
        HashMap<String, String> map = new HashMap<>();
        try {
            ApiClient client = Config.defaultClient();
            Configuration.setDefaultApiClient(client);

            CoreV1Api api = new CoreV1Api();
            V1PodList list = api.listPodForAllNamespaces(null, null, null, null, null, null, null, null);

            for (V1Pod item : list.getItems()) {
                map.put("pod_" + item.hashCode(), item.getMetadata().getName());
            }

            return map;
        } catch (IOException | ApiException e) {
            System.err.println("Error while getting pods");
            System.err.println(e.getStackTrace());
            throw new NotFoundException(e.getMessage());
        }
    }
}
