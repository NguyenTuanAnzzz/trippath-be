package com.an.pathtrip.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "tour_itinerary",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_package_day",
                        columnNames = {"package_id", "day_number"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourItinerary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "package_id",
            nullable = false
    )
    private TourPackage tourPackage;

    @Column(name = "day_number", nullable = false)
    private Integer dayNumber;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;
}