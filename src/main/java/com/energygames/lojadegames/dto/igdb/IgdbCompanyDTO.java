package com.energygames.lojadegames.dto.igdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para empresas (desenvolvedoras/publishers) na IGDB
 * Ref: https://api-docs.igdb.com/#involved-company
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IgdbCompanyDTO {

    private Long id;
    
    @JsonProperty("company")
    private Long companyId; // ID da empresa (precisa buscar em /companies)
    
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

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
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
}
