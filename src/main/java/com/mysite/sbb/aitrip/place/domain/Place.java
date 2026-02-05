package com.mysite.sbb.aitrip.place.domain;

import com.mysite.sbb.aitrip.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "places")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String region;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false)
    private String address;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "operating_hours", length = 200)
    private String operatingHours;

    @Column(name = "estimated_stay_time")
    private Integer estimatedStayTime;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(length = 50)
    private String source;

    @Builder
    public Place(String name, String region, String category, String address,
                 BigDecimal latitude, BigDecimal longitude, String operatingHours,
                 Integer estimatedStayTime, String imageUrl, String source) {
        this.name = name;
        this.region = region;
        this.category = category;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.operatingHours = operatingHours;
        this.estimatedStayTime = estimatedStayTime;
        this.imageUrl = imageUrl;
        this.source = source;
    }

    // 장소 정보 수정
    public void update(String name, String region, String category, String address,
                       BigDecimal latitude, BigDecimal longitude, String operatingHours,
                       Integer estimatedStayTime, String imageUrl, String source) {
        this.name = name;
        this.region = region;
        this.category = category;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.operatingHours = operatingHours;
        this.estimatedStayTime = estimatedStayTime;
        this.imageUrl = imageUrl;
        this.source = source;
    }
}
