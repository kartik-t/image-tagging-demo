package com.example.demo.entity;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;

@Entity
@Table(name = "image")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private String tags;
    private String filePath;
    private String url;
    @Column
    private String label;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTags() {
    return tags;
}

    public void setTags(String tags) {
        this.tags = tags;
    }
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLabel() {
        if(StringUtils.isBlank(label)) {
            return id.toString();
        }
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
