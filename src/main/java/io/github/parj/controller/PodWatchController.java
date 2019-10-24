package io.github.parj.controller;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.*;
import io.github.parj.model.WebHookMessage;
import io.github.parj.service.WebHooks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

@RestController
@RequestMapping("/watch")
public class PodWatchController {
    private static final Logger logger = LoggerFactory.getLogger(PodWatchController.class);

    @Autowired
    private WebHooks webHooks;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.OK)
    public void saveWebHook(@RequestParam String name, @RequestParam String url) {
        try {
            webHooks.addHook(name, url);
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Unable to add URL. Reason: " + e.getMessage()
            );
        }
    }

    @PostMapping("/purge")
    @ResponseStatus(HttpStatus.OK)
    public void purgeHooks() {
        webHooks.purgeHooks();
    }

    @PostMapping("/remove")
    @ResponseStatus(HttpStatus.OK)
    public void removeWebHook(@RequestParam String name) {
        try {
            webHooks.removeHook(name);
        } catch(NoSuchElementException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Key " + name +" not found"
            );
        }
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, URL> listHooks() {
        return webHooks.listHooks();
    }

    @GetMapping("/start")
    @ResponseStatus(HttpStatus.OK)
    public void listenToK8SEvents() {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            webHooks.listenToK8SEvents();
        });
    }
}