/*
 * Copyright (c) 2022 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.gtf.paperexample;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * <p>HTTP Client that redirects the requests over a tor-proxy to bypass request limits.</p>
 * <p>
 * This requires a docker container running, which can be started using:
 * <pre>docker container run -it -p 8118:8118 -p 9051:9051 -e PASSWORD="secure123" dperson/torproxy</pre>
 * </p>
 *
 * @author Andreas Schuler
 * @author Christoph Praschl
 * @author Andreas Pointner
 * @since 1.0
 */
public class ProxyClient {

    private static final String USER_AGENT = "Mozilla/5.0 (X11; Fedora; Linux x86_64; rv:77.0) Gecko/20100101 Firefox/77.0";
    private static final int MAX_CALLS_UNTIL_RENEW = 10;
    private static final String PROXY_PASSWORD = "secure123";
    private static final String PROXY_URL = "localhost";

    private HttpClient client;

    private int countHttpCalls;

    private static HttpRequest createRequest(String url) throws URISyntaxException {
        return HttpRequest.newBuilder()
                .uri(new URI(url))
                .setHeader("User-Agent", USER_AGENT)
                .build();
    }

    private static void renewProxy() {
        try (var socket = new Socket()) {
            socket.connect(new InetSocketAddress(PROXY_URL, 9051));
            socket.getOutputStream().write(("authenticate \"" + PROXY_PASSWORD + "\"\r\n").getBytes());
            socket.getOutputStream().write("SIGNAL NEWNYM\r\n".getBytes());
            Thread.sleep(1000);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    private void ensureClient() {
        if (countHttpCalls++ % MAX_CALLS_UNTIL_RENEW == 0) {
            renewProxy();
            client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .proxy(ProxySelector.of(InetSocketAddress.createUnresolved(PROXY_URL, 8118)))
                    .build();
        }
    }

    public String get(String url) {
        try {
            ensureClient();
            HttpResponse<String> response = client.send(createRequest(url), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IOException("Http Response returned Status Code " + response.statusCode());
            }
            return response.body();
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }
}
