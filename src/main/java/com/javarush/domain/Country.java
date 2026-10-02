package com.javarush.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "country")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code", length = 3, nullable = false)
    private String code;

    @Column(name = "code_2", length = 2, nullable = false)
    private String alternativeCode;

    @Column(name = "name", length = 52, nullable = false)
    private String name;

    @Column(name = "continent", nullable = false)
    @Enumerated(EnumType.STRING)
    private Continent continent;

    @Column(name = "region", length = 26, nullable = false)
    private String region;

    @Column(name = "surface_area", precision = 10, scale = 2, nullable = false)
    private BigDecimal surfaceArea;

    @Column(name = "indep_year")
    private Short independenceYear;

    @Column(name = "population", nullable = false)
    private Integer population;

    @Column(name = "life_expectancy", precision = 3, scale = 1)
    private BigDecimal lifeExpectancy;

    @Column(name = "gnp", precision = 10, scale = 2)
    private BigDecimal GNP;

    @Column(name = "gnpo_id", precision = 10, scale = 2)
    private BigDecimal GNPOId;

    @Column(name = "local_name", length = 45, nullable = false)
    private String localName;

    @Column(name = "government_form", length = 45, nullable = false)
    private String governmentForm;

    @Column(name = "head_of_state", length = 60)
    private String headOfState;

    @OneToOne
    @JoinColumn(name = "capital")
    @ToString.Exclude
    private City city;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Set<CountryLanguage> languages;
}
