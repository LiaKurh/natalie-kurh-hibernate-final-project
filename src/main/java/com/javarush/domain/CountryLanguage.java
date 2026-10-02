package com.javarush.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Table(name = "country_language")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class CountryLanguage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "language", length = 30, nullable = false)
    private String language;

    @Column(name = "is_official", columnDefinition = "BIT", nullable = false)
    private Boolean isOfficial;

    @Column(name = "percentage", precision = 4, scale = 1, nullable = false)
    private BigDecimal percentage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    @ToString.Exclude
    private Country country;
}
