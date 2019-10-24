package io.github.parj.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.ContainerState;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.Watcher;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class WebHookMessage {
    @Getter @Setter private String action;
    @Getter @Setter private String clusterName;
    @Getter @Setter private String name;
    @Getter @Setter private String podName;
    @Getter @Setter private String namespace;
    @Getter @Setter private Map<String, String> labels;
    @Getter @Setter private ContainerState state;
    @Getter @Setter private boolean ready;
    @Getter @Setter private int restartCount;

    public String toJSONString() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }

    public WebHookMessage() { }

    public WebHookMessage(Watcher.Action action, Pod pod) {
        WebHookMessage message = new WebHookMessage();
        message.setAction(action.toString());
        message.setClusterName(pod.getMetadata().getClusterName());
        message.setName(pod.getMetadata().getLabels().get("app"));
        message.setNamespace(pod.getMetadata().getNamespace());
        message.setPodName(pod.getMetadata().getName());
        message.setLabels(pod.getMetadata().getLabels());

        //Kubernetes when starting up, there is no status
        if (pod.getStatus().getContainerStatuses().size() > 0) {
            message.setState(pod.getStatus().getContainerStatuses().get(0).getState());
            message.setReady(pod.getStatus().getContainerStatuses().get(0).getReady());
            message.setRestartCount(pod.getStatus().getContainerStatuses().get(0).getRestartCount());
        }
    }
}
