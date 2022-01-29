package org.vitalii.vorobii;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientConfiguration {
    private String targetUrl;
    // Mime-type to Serializer
    private Map<String, Serializer> serializerConfig;
    private Map<String, Deserializer> deserializerConfig;
}
