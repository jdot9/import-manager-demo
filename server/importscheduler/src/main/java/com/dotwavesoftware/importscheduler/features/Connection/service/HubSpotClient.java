package com.dotwavesoftware.importscheduler.features.Connection.service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import com.dotwavesoftware.importscheduler.features.Connection.model.entity.ConnectionEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
// import com.dotwavesoftware.importscheduler.Model.HubSpotList;
import com.dotwavesoftware.importscheduler.features.Connection.repository.ConnectionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.dotwavesoftware.importscheduler.features.Connection.model.entity.ConnectionEntity;

@Service
public class HubSpotClient {
    
    private final WebClient webClient;
    private ConnectionRepository connectionRepository;
    private static final Logger logger = Logger.getLogger(HubSpotClient.class.getName());
    public HubSpotClient(@Value("${api.hubspot.baseurl}") String baseUrl, 
                                 ConnectionRepository connectionRepository) 
    {
        this.connectionRepository = connectionRepository;
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
public Mono<List<JsonNode>> getAllContacts(int id) {
    return getAllContactsFlux(id)
            .flatMapIterable(response -> response.get("results")) // extract contacts from each page
            .collectList(); // gather into a single List
}

public Flux<JsonNode> getAllContactsFlux(int id) {
    logger.info("Getting contacts from HubSpot.");
    Optional<ConnectionEntity> connection = connectionRepository.findById(id);
    String token = connection.get().getHubspotAccessToken();
    logger.info("Getting access token from connection " + id);
    // Start with an initial empty request (no "after")
    return fetchContactsPage(token, null)
            .expand(response -> {
                JsonNode paging = response.get("paging");
                if (paging != null && paging.get("next") != null) {
                    String nextAfter = paging.get("next").get("after").asText();
                    return fetchContactsPage(token, nextAfter);
                } else {
                    return Mono.empty(); // no more pages
                }
            });
}
private Mono<JsonNode> fetchContactsPage(String token, String after) {
    String uri = "/crm/v3/objects/contacts?limit=100";
    if (after != null) {
        uri += "&after=" + after;
    }
    return webClient.get()
            .uri(uri)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .retrieve()
            .bodyToMono(JsonNode.class);
}
/* 
    public Mono<List<HubSpotList>> getAllLists(int id) {
        Map<String, Object> requestBody = Map.of(
                "processingTypes", new String[] { "DYNAMIC", "MANUAL", "SNAPSHOT" }
        );
        Optional<ConnectionEntity> connection = connectionRepository.findById(id);
        logger.info("Getting access token from connection " + id);
        logger.info("Attempting to retrieve lists from HubSpot.");
        return webClient.post()
                .uri("/crm/v3/lists/search")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + connection.get().getHubspotAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(json -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode root = mapper.readTree(json);
    
                        // Make sure you're using the correct root key: "results" (most common for HubSpot)
                        JsonNode results = root.path("lists");
    
                        if (!results.isArray()) {
                            return Mono.error(new RuntimeException("Expected an array under 'results'"));
                        }
                        
                        List<HubSpotList> lists = new ArrayList<>();
                        for (JsonNode node : results) {
                            HubSpotList list = new HubSpotList();
                            list.setListId(node.path("listId").asText()); // or "listId" if that's correct
                            list.setProcessingType(node.path("processingType").asText());
                            list.setName(node.path("name").asText());
                            list.setListSize(node.path("additionalProperties").path("hs_list_size").asText());
                            list.setLastUpdated(node.path("updatedAt").asText());
                            
                            // Parse to Instant
                            Instant instant = Instant.parse(list.getLastUpdated());
                            // Convert to local ZonedDateTime (e.g., system default zone)
                            ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
                            // Define desired format
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy, hh:mm a");
                            // Format the date
                            list.setLastUpdated(zdt.format(formatter));
                            list.setObjectTypeId(node.path("objectTypeId").asText());
                            if (list.getObjectTypeId().equals("0-1")) {
                                list.setObjectTypeId("CONTACT");
                            } else if (list.getObjectTypeId().equals("0-2")) {
                                list.setObjectTypeId("COMPANY");
                            } else if (list.getObjectTypeId().equals("0-3")) {
                                list.setObjectTypeId("DEAL");
                            } else if (list.getObjectTypeId().equals("0-5")) {
                                list.setObjectTypeId("TICKET");
                            } 
                            lists.add(list);
                        }
                        
                        return Mono.just(lists);
    
                    } catch (Exception e) {
                        logger.warning("Failed to retrieve lists from HubSpot");
                        return Mono.error(new RuntimeException("Failed to parse nested JSON", e));
                    }
                });
    }
    */
    public Mono<Boolean> testHubSpotConnection(String accessToken) {
        logger.info("Validating HubSpot access token.");
        
        // Validate that access token is provided
        if (accessToken == null || accessToken.trim().isEmpty()) {
            logger.warning("Access token is missing or empty.");
            return Mono.just(false);
        }
        
        logger.info("Testing access token against HubSpot API...");
        return webClient.get()
                .uri("/crm/v3/objects/contacts?limit=1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .exchangeToMono(response -> {
                    int status = response.statusCode().value();
                    if (status == 200) {
                        logger.info("HubSpot connection validated successfully.");
                        return Mono.just(true);
                    } else if (status == 401) {
                        logger.warning("HubSpot authentication failed - invalid access token.");
                        return Mono.just(false);
                    } else {
                        // Throw other unexpected status codes as 500 or log them as needed
                        logger.warning("Unexpected failure when authenticating access token. Status: " + status);
                        return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                                "Unexpected response from HubSpot: " + status));
                    }
                });
    }
}