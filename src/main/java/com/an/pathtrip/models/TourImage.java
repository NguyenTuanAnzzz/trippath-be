package com.an.pathtrip.models;

import com.an.pathtrip.models.TourPackage;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tour_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "package_id",
            nullable = false
    )
    private TourPackage tourPackage;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "is_thumbnail", nullable = false)
    @Builder.Default
    private Boolean thumbnail = false;
}