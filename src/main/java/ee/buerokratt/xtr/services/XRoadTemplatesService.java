package ee.buerokratt.xtr.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.buerokratt.xtr.domain.YamlXRoadTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class XRoadTemplatesService {

    final ObjectMapper mapper;

    String configPath;

    Map<String, Map<String, YamlXRoadTemplate>> services;

    @Autowired
    public XRoadTemplatesService(@Qualifier("ymlMapper") ObjectMapper mapper,
                                 @Value("${application.dslPath}") String configPath) {
        this.mapper = mapper;
        this.configPath = configPath;
        this.services = new HashMap<>();
        readServices();
    }

    public void readServices() {
        readServices(configPath);
    }

    public void readServices(String path) {
        try {
            Files.walk(Paths.get(path))
                    .filter(f -> !f.toFile().isDirectory())
                    .forEach(file -> readServicesFromFile(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void readServicesFromFile(Path filePath) {
        File file = filePath.toFile();
        log.info("Loading XRoad requests from " + file.getPath());
        YamlXRoadTemplate service = readService(file);
        log.info("Loaded service: " + service.toString());

        String[] pathParts = file.getPath().split("/");
        String groupName = pathParts[pathParts.length-2];
        String serviceName = pathParts[pathParts.length-1].substring(0, pathParts[pathParts.length-1].indexOf(".y"));
        addService(groupName, serviceName, service);
    }

    private YamlXRoadTemplate readService(File serviceFile) {
        try {
            YamlXRoadTemplate map = mapper.readValue(serviceFile, YamlXRoadTemplate.class);
            return map;
        } catch (IOException e) {
            log.error("Could not create service", e);
            throw new RuntimeException(e);
        }
    }

    private void addService(String groupName, String serviceName, YamlXRoadTemplate template) {
        if (!this.services.containsKey(groupName))
            this.services.put(groupName, new HashMap<>());
        log.info("Adding "+ serviceName + " to service " + groupName);
        this.services.get(groupName).put(serviceName, template);
    }

    public YamlXRoadTemplate getService(String group, String service) {
        return services.get(group).get(service);
    }

}
