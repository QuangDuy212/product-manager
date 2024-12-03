package com.quangduy.product_manager_for_arius.configuration.es;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;

@Component
public class ESClient {

    @Bean
    public ElasticsearchClient getElasticsearchClient() {
        RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200, "http"));

        RestClientBuilder.HttpClientConfigCallback httpClientConfigCallback = new HttpClientConfigImpl();
        builder.setHttpClientConfigCallback(httpClientConfigCallback);

        RestClient restClient = builder.build();

        RestClientTransport restClientTransport = new RestClientTransport(restClient, new JacksonJsonpMapper());

        return new ElasticsearchClient(restClientTransport);
    }
}