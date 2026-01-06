package com.dotwavesoftware.importscheduler.features.Connection.service;
import java.io.StringReader;
import java.util.Base64;
import java.util.HashMap;
import java.util.Optional;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import com.dotwavesoftware.importscheduler.features.Connection.model.entity.ConnectionEntity;
import com.dotwavesoftware.importscheduler.features.Connection.repository.ConnectionRepository;
import reactor.core.publisher.Mono;
@Service
public class Five9Client {
    private final WebClient webClient;
    private static final Logger logger = Logger.getLogger(Five9Client.class.getName());
    private ConnectionRepository connectionRepository;
    public Five9Client(@Value("${api.five9.baseurl}") String baseUrl, ConnectionRepository connectionRepository) 
    {
        this.connectionRepository = connectionRepository;
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
    public Mono<Boolean> testFive9Connection(String base64Credentials) {
        String soapRequest = """
<env:Envelope xmlns:env="http://schemas.xmlsoap.org/soap/envelope/"
xmlns:ser="http://service.admin.ws.five9.com/">
    <env:Header/>
    <env:Body>
        <ser:addRecordToList>
            <listName>JASON_TEST_LIST</listName>
            <listUpdateSettings>
                <fieldsMapping>
                    <columnNumber>1</columnNumber>
                    <fieldName>number1</fieldName>
                    <key>true</key>
                </fieldsMapping>
                <fieldsMapping>
                    <columnNumber>2</columnNumber>
                    <fieldName>first_name</fieldName>
                    <key>false</key>
                </fieldsMapping>
                <fieldsMapping>
                    <columnNumber>3</columnNumber>
                    <fieldName>last_name</fieldName>
                    <key>false</key>
                </fieldsMapping>
                <separator>,</separator>
                <skipHeaderLine>false</skipHeaderLine>
                <callNowMode>ANY</callNowMode>
                <cleanListBeforeUpdate>false</cleanListBeforeUpdate>
                <crmAddMode>ADD_NEW</crmAddMode>
                <crmUpdateMode>UPDATE_FIRST</crmUpdateMode>
                <listAddMode>ADD_FIRST</listAddMode>
            </listUpdateSettings>
            <record>
                <fields></fields>
                <fields></fields>
                <fields></fields>
            </record>
        </ser:addRecordToList>
    </env:Body>
</env:Envelope>
""";
        // Removes extra quotes
        String credentials = base64Credentials.replaceAll("^\"|\"$", "");
        logger.info("Testing Five9 Connection.");
        return webClient.post()
                .uri("/wsadmin/AdminWebService")
                .header(HttpHeaders.CONTENT_TYPE, "text/xml;charset=UTF-8")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials)
                .bodyValue(soapRequest)
                .exchangeToMono(response -> {
                    int status = response.statusCode().value();
                    if (status == 200) {
                        return Mono.just(true);
                    } else if (status == 401) {
                        return Mono.just(false);
                    } else {
                        // Throw other unexpected status codes as 500 or log them as needed
                        logger.warning("Unexpected response from Five9 API.");
                        return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                                "Unexpected response from Five9: " + status));
                    }
                });
    }
 /* 
    public Mono<HashMap<String, String>> getDialingLists(String base64Credentials) {
        String soapRequest = """
            <env:Envelope xmlns:env="http://schemas.xmlsoap.org/soap/envelope/"
            xmlns:ser="http://service.admin.ws.five9.com/">
                <env:Header/>
                <env:Body>
                    <ser:getListsInfo>
                    </ser:getListsInfo>
                </env:Body>
            </env:Envelope> 
            """;
        
        String credentials = base64Credentials.replaceAll("^\"|\"$", "");
        logger.info("Getting Dialing lists from Five9 API.");
        return webClient.post()
            .uri("/wsadmin/AdminWebService")
            .header(HttpHeaders.CONTENT_TYPE, "text/xml;charset=UTF-8")
            .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials)
            .bodyValue(soapRequest)
            .retrieve()
            .bodyToMono(String.class)
            .flatMap(responseXml -> {
                try {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    factory.setNamespaceAware(true);
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document doc = builder.parse(new InputSource(new StringReader(responseXml)));
    
                    // Create a HashMap to store name -> size
                    HashMap<String, String> dialingList = new HashMap<>();
    
                    NodeList listNames = doc.getElementsByTagName("name");
                    NodeList listSizes = doc.getElementsByTagName("size");
    
                    for (int i = 0; i < listNames.getLength(); i++) {
                        String listName = listNames.item(i).getTextContent();
                        String size = (i < listSizes.getLength()) ? listSizes.item(i).getTextContent() : "";
                        dialingList.put(listName, size);
                    }
    
                    return Mono.just(dialingList);
    
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.warning("Failed to parse SOAP response");
                    return Mono.error(new RuntimeException("Failed to parse SOAP response", e));
                }
            });
    }
    */
        public Mono<HashMap<String, String>> getDialingLists(int five9ConnectionId) {
        String soapRequest = """
            <env:Envelope xmlns:env="http://schemas.xmlsoap.org/soap/envelope/"
            xmlns:ser="http://service.admin.ws.five9.com/">
                <env:Header/>
                <env:Body>
                    <ser:getListsInfo>
                    </ser:getListsInfo>
                </env:Body>
            </env:Envelope> 
            """;
        Optional<ConnectionEntity> connection = connectionRepository.findById(five9ConnectionId);
        String username = connection.get().getFive9Username();
        String password = connection.get().getFive9Password();
        String credentials = username +":"+ password;
        String base64credentials = Base64.getEncoder().encodeToString(credentials.getBytes());
       // logger.warning(base64credentials);
       // String credentials = base64Credentials.replaceAll("^\"|\"$", "");
        logger.info("Getting Dialing lists from Five9 API.");
        return webClient.post()
            .uri("/wsadmin/AdminWebService")
            .header(HttpHeaders.CONTENT_TYPE, "text/xml;charset=UTF-8")
            .header(HttpHeaders.AUTHORIZATION, "Basic " + base64credentials)
            .bodyValue(soapRequest)
            .retrieve()
            .bodyToMono(String.class)
            .flatMap(responseXml -> {
                try {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    factory.setNamespaceAware(true);
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document doc = builder.parse(new InputSource(new StringReader(responseXml)));
    
                    // Create a HashMap to store name -> size
                    HashMap<String, String> dialingList = new HashMap<>();
    
                    NodeList listNames = doc.getElementsByTagName("name");
                    NodeList listSizes = doc.getElementsByTagName("size");
    
                    for (int i = 0; i < listNames.getLength(); i++) {
                        String listName = listNames.item(i).getTextContent();
                        String size = (i < listSizes.getLength()) ? listSizes.item(i).getTextContent() : "";
                        dialingList.put(listName, size);
                    }
    
                    return Mono.just(dialingList);
    
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.warning("Failed to parse SOAP response");
                    return Mono.error(new RuntimeException("Failed to parse SOAP response", e));
                }
            });
    }
}
