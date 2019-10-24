package io.github.parj.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.*;
import io.github.parj.model.WebHookMessage;
import org.asynchttpclient.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import static org.asynchttpclient.Dsl.asyncHttpClient;

@Service
public class WebHooks {
    private static final Logger logger = LoggerFactory.getLogger(WebHooks.class);
    private static HashMap<String, URL> listOfHooks = new HashMap<String, URL>();

    public void addHook(String name, String url) throws MalformedURLException {
        URL url1 = new URL(url);
        listOfHooks.put(name, url1);
        logger.info("Added hook. Name: {}, URL: {} ", name, url );
    }

    public void purgeHooks() {
        listOfHooks.clear();
        logger.info("Cleared all hooks");
    }

    public void removeHook(String name) {
        if (listOfHooks.containsKey(name)) {
            listOfHooks.remove(name);
            logger.info("Removed hook. Name: {}", name);
        }
        else
            throw new NoSuchElementException(name + "was not found in webhooks list");
    }

    public Map<String, URL> listHooks() {
        return listOfHooks;
    }

    @Async
    public void triggerPostToAllHooks(WebHookMessage message) {
        logger.info("Trigger post request to {} number of hooks", listHooks().size());
        for(Map.Entry<String, URL> hook : listOfHooks.entrySet()) {
            URL url = hook.getValue();
            try {
                Future<Response> whenResponse = asyncHttpClient().preparePost(url.toString())
                        .setBody(message.toJSONString())
                        .execute();
                logger.debug("Sent request to the URL: {}", url.toString());
            } catch (JsonProcessingException e) {
                logger.info("Unable to convert message to JSON. Exception is {}", e.getMessage());
                e.printStackTrace(System.err);
            }
        }
    }

    @Async
    public void listenToK8SEvents() {
        final CountDownLatch closeLatch = new CountDownLatch(1);
        Config config = new ConfigBuilder().build();

        try (final KubernetesClient client = new DefaultKubernetesClient(config)) {
            try (Watch watch = client.pods().inAnyNamespace().watch(new Watcher<Pod>() {
                @Override
                public void eventReceived(Action action, Pod resource) {
                    logger.info("{}: {}: {}: {}: {}", action, resource.getMetadata().getResourceVersion(),
                            resource.getMetadata().getLabels().get("app"),
                            resource.getMetadata().getName(),
                            resource.getMetadata().getNamespace());

                    WebHookMessage message = new WebHookMessage(action, resource);
                    triggerPostToAllHooks(message);
                }

                @Override
                public void onClose(KubernetesClientException e) {
                    logger.info("Watcher onClose");
                    if (e != null) {
                        logger.error(e.getMessage(), e);
                        closeLatch.countDown();
                    }
                }
            })) {
                closeLatch.await();
            } catch (KubernetesClientException | InterruptedException e) {
                logger.error("Could not start listening to Kubernetes", e);
                e.printStackTrace(System.err);
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Could not start listening to Kubernetes. Reason: " + e.getMessage()
                );
            }
        } catch (Exception e) {
            logger.error("Could not start listening to Kubernetes", e);
            e.printStackTrace();

            Throwable[] suppressed = e.getSuppressed();
            if (suppressed != null) {
                for (Throwable t : suppressed) {
                    logger.error(t.getMessage(), t);
                }
            }

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Could not start listening to Kubernetes. Reason: " + e.getMessage()
            );
        }
    }
}
