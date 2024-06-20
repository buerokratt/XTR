package ee.buerokratt.xtr3.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import ee.buerokratt.xtr3.domain.YamlXRoadTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class RequestExecutorService {

    @Autowired
    HandlebarsHelper hbsHelper;

    public String execute(YamlXRoadTemplate template, Map<String, String> params) throws IOException {
        String payload = hbsHelper.apply(template.getEnvelope(), filterParams(template, params));
        return xmlToJson(doRequest(template.getService(), template.getMethod(), payload));
    }

    private Map<String, String> filterParams(YamlXRoadTemplate template, Map<String, String> params) {
        Map<String, String> returnParams = new HashMap<>();
        params.entrySet().forEach(entry -> {
            if (template.getParams().contains(entry.getKey()))
                returnParams.put(entry.getKey(), entry.getValue());
        });
        return returnParams;
    }

    private String doRequest(String serviceURI, String method, String payload) {
        RestClient client = RestClient.create();

        log.info("Sending "+ method +"request [[" + payload + "]] to endpoint "+ serviceURI);

        return client.method(HttpMethod.valueOf(method))
                .uri(serviceURI)
                .body(payload)
                .retrieve()
                .toEntity(String.class)
                .getBody();
    }

    private String xmlToJson(String xmlPayload) throws JsonProcessingException {
        XmlMapper mapper = new XmlMapper();
        JsonNode node = mapper.readTree(xmlPayload);

        ObjectMapper jsonMapper = new ObjectMapper();
        String json = jsonMapper.writeValueAsString(node.get("Body"));

        return json;
    }

}
