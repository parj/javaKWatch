package io.github.parj;


import io.github.parj.service.K8SClient;
import lombok.Getter;
import lombok.Setter;
import org.junit.Ignore;
import org.junit.Rule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import io.fabric8.kubernetes.client.KubernetesClient;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.MalformedInputException;

//@ExtendWith({KubernetesClientExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableRuleMigrationSupport
public class ListPodsTest {
    @Rule
    public KubernetesServer server = new KubernetesServer(false);

    @LocalServerPort
    private int port;

    private URL base;

    @Autowired
    private TestRestTemplate template;

    @Autowired
    private K8SClient k8SClient;

    public URL getBase() throws MalformedURLException {
        if (this.base == null)
            this.base = new URL("http://localhost:" + port + "/");
        return this.base;
    }

    @DisplayName("Tests that the Kubernetes mock client is not null")
    @Test
    void testGetKubernetesClient() {
        KubernetesClient client = server.getClient();
        assertNotNull(client);
    }

    @DisplayName("List pods and ensure not null")
    @Ignore
    void testListPods() throws MalformedURLException {
        k8SClient.setClient(server.getClient());
        k8SClient.getClient().pods().createNew();
        ResponseEntity<String> response = template.getForEntity(getBase().toString() + "/k8s/list/pods",
                String.class);


        assertThat(response.getBody(), equalTo("Greetings from Spring Boot!"));

    }
}
