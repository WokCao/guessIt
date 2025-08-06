package com.FoZ.guessIt.DTOs;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacebookUserInfoDTO {
    private String id;
    private String email;
    private String name;

    @JsonProperty("picture")
    private Picture picture;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Picture {
        @JsonProperty("data")
        private PictureData pictureData;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class PictureData {
            private int height;
            private int width;
            private String url;

            @JsonProperty("is_silhouette")
            private boolean isSilhouette;
        }
    }
}
