package ee.buerokratt.xtr3.domain;

import lombok.Data;

import java.util.List;

@Data
public class YamlXRoadTemplate {

    private List<String> params;

    private String service;
    private String method;

    private String envelope;
}
