package yuseong.com.guchung.personal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpstageOcrResponseDto {

    private String apiVersion;
    private double confidence;
    private String mimeType;
    private String modelVersion;
    private String text;
    private List<Page> pages;

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Page {
        private int id;
        private double confidence;
        private int width;
        private int height;
        private String text;
        private List<Word> words;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Word {
        private int id;
        private String text;
        private double confidence;
        private BoundingBox boundingBox;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BoundingBox {
        private List<Vertex> vertices;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Vertex {
        private int x;
        private int y;
    }
}