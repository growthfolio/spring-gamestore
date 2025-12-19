package com.energygames.lojadegames.dto.igdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para involved companies expandido da IGDB
 * Inclui dados da empresa quando expandido na query
 * Ref: https://api-docs.igdb.com/#involved-company
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IgdbInvolvedCompanyDTO {

    private Long id;
    
    @JsonProperty("company")
    private IgdbCompanyDetailDTO company; // Objeto da empresa (expandido)
    
    private Boolean developer;
    private Boolean publisher;
    private Boolean porting;
    private Boolean supporting;

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IgdbCompanyDetailDTO getCompany() {
        return company;
    }

    public void setCompany(IgdbCompanyDetailDTO company) {
        this.company = company;
    }

    public Boolean getDeveloper() {
        return developer;
    }

    public void setDeveloper(Boolean developer) {
        this.developer = developer;
    }

    public Boolean getPublisher() {
        return publisher;
    }

    public void setPublisher(Boolean publisher) {
        this.publisher = publisher;
    }

    public Boolean getPorting() {
        return porting;
    }

    public void setPorting(Boolean porting) {
        this.porting = porting;
    }

    public Boolean getSupporting() {
        return supporting;
    }

    public void setSupporting(Boolean supporting) {
        this.supporting = supporting;
    }

    /**
     * DTO para detalhes da empresa
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IgdbCompanyDetailDTO {
        private Long id;
        private String name;
        private String slug;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSlug() {
            return slug;
        }

        public void setSlug(String slug) {
            this.slug = slug;
        }
    }
}
