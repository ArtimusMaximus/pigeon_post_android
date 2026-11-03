package com.pigeonpost.android.network;

import com.sun.net.httpserver.HttpServer;
import okhttp3.*;
import org.junit.Test;
import java.net.*;
import java.util.concurrent.*;
import static org.junit.Assert.*;

public class AiHttpClientTest {
    @Test public void aiClientKeepsAuthenticationAndConnectionsWithoutChangingOrdinaryTimeouts() {
        var base=new OkHttpClient.Builder().build();var ai=AiHttpClient.from(base);
        assertEquals(10000,base.readTimeoutMillis());assertEquals(0,base.callTimeoutMillis());
        assertEquals(120000,ai.readTimeoutMillis());assertEquals(150000,ai.callTimeoutMillis());
        assertEquals(base.connectTimeoutMillis(),ai.connectTimeoutMillis());
        assertEquals(base.writeTimeoutMillis(),ai.writeTimeoutMillis());
        assertSame(base.authenticator(),ai.authenticator());assertSame(base.connectionPool(),ai.connectionPool());
        assertSame(base.dispatcher(),ai.dispatcher());
    }
    @Test public void slowerInferenceCanFinishWhileOrdinaryRequestsStillTimeOut() throws Exception {
        var executor=Executors.newFixedThreadPool(2);
        var server=HttpServer.create(new InetSocketAddress("127.0.0.1",0),0);server.setExecutor(executor);
        server.createContext("/",exchange->{
            try {
                Thread.sleep(400);byte[] data="AI answer".getBytes(java.nio.charset.StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200,data.length);exchange.getResponseBody().write(data);
            } catch(InterruptedException interrupted) {Thread.currentThread().interrupt();}
              catch(java.io.IOException disconnected) { /* Short-deadline client already disconnected. */ }
            finally {exchange.close();}
        });
        server.start();
        var base=new OkHttpClient.Builder().readTimeout(100,TimeUnit.MILLISECONDS).build();
        var ai=AiHttpClient.from(base);
        var request=new Request.Builder().url("http://127.0.0.1:"+server.getAddress().getPort()+"/api/ai/ask").build();
        try {
            assertThrows(java.net.SocketTimeoutException.class,()->{
                try(var response=base.newCall(request).execute()){response.body().string();}
            });
            try(var response=ai.newCall(request).execute()){assertEquals("AI answer",response.body().string());}
        } finally {
            server.stop(0);executor.shutdownNow();base.connectionPool().evictAll();base.dispatcher().executorService().shutdown();
        }
    }
}
