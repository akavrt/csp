package com.akavrt.csp.metadata;

import java.util.Date;

/**
 * User: akavrt
 * Date: 03.03.13
 * Time: 14:23
 */
public class ProblemMetadata {
    private String name;
    private String author;
    private String description;
    private Date date;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
