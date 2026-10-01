package com.an.pathtrip.controllers;

import com.an.pathtrip.dtos.request.CreateTourPackageRequest;
import com.an.pathtrip.dtos.response.TourPackageResponse;
import com.an.pathtrip.services.TourPackageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tour")
public class TourPackageController {

    @Autowired
    private TourPackageService service;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public TourPackageResponse createTourPackage(@ModelAttribute @Valid CreateTourPackageRequest request){
        return service.createTourPackage(request);
    }
}
