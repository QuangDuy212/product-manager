package com.quangduy.product_manager_for_arius.configuration.es;

// import javax.net.ssl.SSLContext;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
// import org.apache.http.ssl.SSLContextBuilder;
// import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientConfigImpl implements HttpClientConfigCallback {

    @Override
    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
        try {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials("elastic",
                    "lyUQxxheK-0QGg6LtDwG");
            credentialsProvider.setCredentials(AuthScope.ANY, usernamePasswordCredentials);
            httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);

            // String trustLocationStore = "";
            // File trustLocationFile = new File(trustLocationStore);

            // SSLContextBuilder sslContextBuilder =
            // SSLContexts.custom().loadTrustMaterial(trustLocationFile,
            // "6xWwC1g*mdEfICNIkRtK".toCharArray());
            // SSLContext sslContext = sslContextBuilder.build();
            // httpClientBuilder.setSSLContext(sslContext);

        } catch (Exception e) {
        }
        return httpClientBuilder;
    }
}
