/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pse.fotoz.dbal.entities;

import com.pse.fotoz.dbal.HibernateEntityHelper;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.DecimalMin;

/**
 *
 * @author René
 */
@Entity
@Table(name = "pictures")
public class Picture implements HibernateEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    //TODO:pictures_high_res
    @ManyToOne
    @JoinColumn(name = "picture_session_id", nullable = false)
    private PictureSession session;

    @Basic
    @Column(name = "width")
    private int width;

    @Basic
    @Column(name = "height")
    private int height;

    @Basic
    @Column(name = "filename")
    private String fileName;

    @Basic
    @Column(name = "title")
    private String title;

    @Basic
    @Column(name = "description")
    private String description;

    @Basic
    @Column(name = "price")
    @DecimalMin(value="0.01", message="{error_decimal_price}")
    private BigDecimal price;

    @Basic
    @Column(name = "hidden")
    private boolean hidden;

    @Enumerated(EnumType.STRING)
    @Column(name = "approved")
    private Approved approved;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "submission_date")
    private Date submissionDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PictureSession getSession() {
        return session;
    }

    public void setSession(PictureSession session) {
        this.session = session;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public Approved getApproved() {
        return approved;
    }

    public void setApproved(Approved approved) {
        this.approved = approved;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }

    public static enum Approved {

        YES, NO, PENDING
    }

    public String getURI() {
        return "/assets/common/img/" + this.fileName;

    }

    /**
     * Checks if a filename already exists within the session
     *
     * @param fileName
     * @param session
     * @return true if filename exists within the given session
     */
    public static boolean doesFileNameExist(String fileName, PictureSession session) {

        Long count = HibernateEntityHelper.all(Picture.class).
                stream().
                filter(s -> s.getFileName().equals(fileName)).
                filter(s -> s.getSession().getId() == session.getId()).
                count();

        return count > 0;
    }

    public static Picture getByFileNameAndSession(String fileName, PictureSession session) {
        Picture returnPic = null;

        returnPic = HibernateEntityHelper.all(Picture.class).
                stream().
                filter(s -> s.getFileName().equals(fileName)).
                filter(s -> s.getSession().getId() == session.getId()).
                findAny().
                orElse(null);

        return returnPic;
    }

}
