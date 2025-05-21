package org.example;

import com.sun.net.httpserver.HttpServer;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.User;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class KiteSessionManager {
    private final String apiKey, apiSecret;
    private final int port;
    private final String callbackPath;
    private KiteConnect kiteConnect;
    private final String userId;

    KiteSessionManager(String apiKey, String apiSecret, int port, String callbackPath, String userId) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.port = port;
        this.callbackPath = callbackPath;
        this.userId = userId;
    }

    KiteConnect login() throws KiteSessionManagerException {
        String loginUrl = "https://kite.zerodha.com/connect/login?v=3&api_key="
                + URLEncoder.encode(apiKey, StandardCharsets.UTF_8);
        HttpServer server;
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            throw new KiteSessionManagerException("Unable to create Http server with local port", e);
        }
        server.setExecutor(Executors.newCachedThreadPool());
        CompletableFuture<String> tokenFuture = new CompletableFuture<>();
        server.createContext(callbackPath, exchange -> {
            String query = exchange.getRequestURI().getQuery();
            Map<String, String> params = Arrays.stream(query.split("&"))
                    .map(s -> s.split("=", 2))
                    .collect(Collectors.toMap(
                            kv -> URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                            kv -> URLDecoder.decode(kv[1], StandardCharsets.UTF_8)
                    ));
            String requestToken = params.get("request_token");

            String html = "<html><body><h3>Login successful! You can close this window.</h3></body></html>";
            exchange.sendResponseHeaders(200, html.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(html.getBytes());
            }
            tokenFuture.complete(requestToken);
            server.stop(0);
        });
        server.start();
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(URI.create(loginUrl));
            } catch (IOException e) {
                throw new KiteSessionManagerException("Unable to browse to the login URI", e);
            }
        }
        String requestToken;
        try {
            requestToken = tokenFuture.get(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new KiteSessionManagerException("Possibly happens when the server thread is blocked", e);
        } catch (ExecutionException e) {
            throw new KiteSessionManagerException("don't know why this would occur", e);
        } catch (TimeoutException e) {
            throw new KiteSessionManagerException("Server timed out while trying to fetch access token", e);
        }

        kiteConnect = new KiteConnect(apiKey);
        kiteConnect.setUserId(userId);
        try {
            User user = kiteConnect.generateSession(requestToken, apiSecret);
            kiteConnect.setAccessToken(user.accessToken);
            kiteConnect.setPublicToken(user.publicToken);
        } catch (KiteException e) {
            throw new KiteSessionManagerException("Kite related error while generting sessions", e);
        } catch (IOException e) {
            throw new KiteSessionManagerException("Network related error", e);
        }
        return kiteConnect;
    }
}
