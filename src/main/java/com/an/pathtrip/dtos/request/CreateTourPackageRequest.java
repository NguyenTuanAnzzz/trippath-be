package com.an.pathtrip.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CreateTourPackageRequest {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    @Min(1)
    private Integer durationDays;

    @NotNull
    @Min(0)
    private Integer durationNights;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal price;

    @NotBlank
    private String location;

    private List<ItineraryRequest> itineraries;

    private List<MultipartFile> images;
}