package com.energygames.lojadegames.model;

import java.util.HashSet;
import java.util.Set;

import com.energygames.lojadegames.enums.TipoPlataformaEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Entidade que representa uma plataforma de jogos (console, PC, mobile, etc).
 * Relacionamento N:N com Produto, pois um jogo pode estar em múltiplas plataformas.
 */
@Entity
@Table(name = "tb_plataformas")
public class Plataforma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome da plataforma é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    @Column(unique = true, nullable = false, length = 100)
    private String nome; // Ex: "PlayStation 5", "Xbox Series X", "PC (Windows)"

    @Column(unique = true, length = 100)
    private String slug; // Ex: "playstation-5", "xbox-series-x", "pc-windows"

    @Column(name = "id_igdb")
    private Integer idIgdb; // ID da plataforma na API IGDB

    @Size(max = 20, message = "Abreviação não pode ultrapassar 20 caracteres")
    @Column(length = 20)
    private String abreviacao; // Ex: "PS5", "XSX", "PC"

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private TipoPlataformaEnum tipo; // CONSOLE, PC, MOBILE, etc.

    @Column(name = "geracao")
    private Integer geracao; // Ex: 9 para PlayStation 5 / Xbox Series (9ª geração)

    @Column(length = 500)
    private String logo; // URL do logo da plataforma

    @NotNull
    @Column(columnDefinition = "boolean default true")
    private Boolean ativa = true;

    @ManyToMany(mappedBy = "plataformas")
    @JsonIgnoreProperties("plataformas")
    private Set<Produto> produtos = new HashSet<>();

    // Construtores
    public Plataforma() {
    }

    public Plataforma(String nome, String slug, TipoPlataformaEnum tipo) {
        this.nome = nome;
        this.slug = slug;
        this.tipo = tipo;
        this.ativa = true;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Integer getIdIgdb() {
        return idIgdb;
    }

    public void setIdIgdb(Integer idIgdb) {
        this.idIgdb = idIgdb;
    }

    public String getAbreviacao() {
        return abreviacao;
    }

    public void setAbreviacao(String abreviacao) {
        this.abreviacao = abreviacao;
    }

    public TipoPlataformaEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoPlataformaEnum tipo) {
        this.tipo = tipo;
    }

    public Integer getGeracao() {
        return geracao;
    }

    public void setGeracao(Integer geracao) {
        this.geracao = geracao;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Boolean getAtiva() {
        return ativa;
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }

    public Set<Produto> getProdutos() {
        return produtos;
    }

    public void setProdutos(Set<Produto> produtos) {
        this.produtos = produtos;
    }
}
