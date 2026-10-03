package com.an.pathtrip.services;

import com.an.pathtrip.dtos.request.CreateTourPackageRequest;
import com.an.pathtrip.dtos.response.TourImageResponse;
import com.an.pathtrip.dtos.response.TourItineraryResponse;
import com.an.pathtrip.dtos.response.TourPackageResponse;
import com.an.pathtrip.exceptions.AppException;
import com.an.pathtrip.models.TourImage;
import com.an.pathtrip.models.TourItinerary;
import com.an.pathtrip.models.TourPackage;
import com.an.pathtrip.repositories.TourPackageRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class TourPackageService {

    @Autowired
    private TourPackageRepository tourPackageRepo;

    @Autowired
    private CloudinaryService cloudinaryService;


    public TourPackageResponse createTourPackage(@Valid CreateTourPackageRequest request) {
        if(tourPackageRepo.findByName(request.getName()).isPresent()){
            throw new AppException("Tên tour đã tồn tại", 409);
        }

        if (request.getDurationDays() < 1) {
            throw new AppException(
                    "Thời gian tour phải ít nhất 1 ngày",
                    400
            );
        }

        if (request.getDurationDays() != request.getItineraries().size()) {
            throw new AppException(
                    "Số lượng lịch trình phải bằng số ngày của tour",
                    400
            );
        }

        TourPackage tourPackage = new TourPackage();
        tourPackage.setName(request.getName());
        tourPackage.setDescription(request.getDescription());
        tourPackage.setDurationDays(request.getDurationDays());
        tourPackage.setDurationNights(request.getDurationNights());
        tourPackage.setPrice(request.getPrice());
        tourPackage.setLocation(request.getLocation());

        if(request.getItineraries() != null){
            List<TourItinerary> itineraries = request.getItineraries().stream().map(item ->{
                TourItinerary itinerary = new TourItinerary();
                itinerary.setTourPackage(tourPackage);
                itinerary.setDayNumber(item.getDayNumber());
                itinerary.setTitle(item.getTitle());
                itinerary.setDescription(item.getDescription());
                return itinerary;
            }).toList();
            tourPackage.setItineraries(itineraries);
        }



        if (request.getImages() != null) {

            for (int i = 0; i < request.getImages().size(); i++) {

                MultipartFile m = request.getImages().get(i);

                if (m != null && !m.isEmpty()) {

                    TourImage tourImage = new TourImage();

                    String url = cloudinaryService.upload(m);

                    tourImage.setImageUrl(url);
                    tourImage.setTourPackage(tourPackage);
                    tourImage.setThumbnail(i == 0);

                    tourPackage.getImages().add(tourImage);
                }
            }
        }

        TourPackage savedTourPackage = tourPackageRepo.save(tourPackage);

        TourPackageResponse response = new TourPackageResponse();

        response.setId(savedTourPackage.getId());
        response.setName(savedTourPackage.getName());
        response.setDescription(savedTourPackage.getDescription());
        response.setDurationDays(savedTourPackage.getDurationDays());
        response.setDurationNights(savedTourPackage.getDurationNights());
        response.setPrice(savedTourPackage.getPrice());
        response.setLocation(savedTourPackage.getLocation());
        response.setCreatedAt(savedTourPackage.getCreatedAt());

        List<TourItineraryResponse> itineraries =
                savedTourPackage.getItineraries()
                        .stream()
                        .map(item -> {
                            TourItineraryResponse itineraryResponse =
                                    new TourItineraryResponse();

                            itineraryResponse.setId(item.getId());
                            itineraryResponse.setDayNumber(item.getDayNumber());
                            itineraryResponse.setTitle(item.getTitle());
                            itineraryResponse.setDescription(item.getDescription());

                            return itineraryResponse;
                        })
                        .toList();

        response.setItineraries(itineraries);

        List<TourImageResponse> images =
                savedTourPackage.getImages()
                        .stream()
                        .map(item -> {
                            TourImageResponse imageResponse =
                                    new TourImageResponse();

                            imageResponse.setId(item.getId());
                            imageResponse.setImageUrl(item.getImageUrl());
                            imageResponse.setThumbnail(item.getThumbnail());

                            return imageResponse;
                        })
                        .toList();

        response.setImages(images);

        return response;

    }
}
