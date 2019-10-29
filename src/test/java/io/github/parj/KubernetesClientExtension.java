package io.github.parj;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.web.server.LocalServerPort;

import java.net.MalformedURLException;
import java.net.URL;

public class KubernetesClientExtension implements BeforeAllCallback {
    @LocalServerPort
    private int port;

    @Getter @Setter
    private URL base;

    @Override
    public void beforeAll(ExtensionContext context) throws MalformedURLException, NoSuchFieldException, IllegalAccessException {
        this.base = new URL("http://localhost:" + port + "/");
        System.out.println(base);
        context.getTestClass().get()
                .getField("base")
                .set(new ListPodsTest(), base);

    }
}
