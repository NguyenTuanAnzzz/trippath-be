package com.an.pathtrip.dtos.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourImageResponse {

    private Long id;

    private String imageUrl;

    private Boolean thumbnail;
}