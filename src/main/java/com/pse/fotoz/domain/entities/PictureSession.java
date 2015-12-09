package com.pse.fotoz.domain.entities;

import com.pse.fotoz.persistence.HibernateEntityHelper;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Robert
 */
@Entity
@Table(name = "picture_sessions")
public class PictureSession implements HibernateEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @OneToMany(mappedBy = "session", fetch = FetchType.EAGER)
    private Set<Picture> pictures;

    @Basic
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Basic
    @Column(name = "title")
    @Size(min = 1, message = "{error_size_title}")
    private String title;

    @Basic
    @Column(name = "description")
    @Size(min = 1, message = "{error_size_description}")
    private String description;

    @Basic
    @Column(name = "public")
    @NotNull(message = "{error_notnull_public}")
    private boolean isPublic;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "permittedSessions")
    private Set<CustomerAccount> permittedAccounts;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public Set<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(Set<Picture> pictures) {
        this.pictures = pictures;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Set<CustomerAccount> getPermittedAccounts() {
        return permittedAccounts;
    }

    public void setPermittedAccounts(Set<CustomerAccount> permittedAccounts) {
        this.permittedAccounts = permittedAccounts;
    }

    public static PictureSession getSessionByCode(String code) {
        return HibernateEntityHelper
                .find(PictureSession.class, "code", code)
                .stream().findAny().orElse(null);
    }

    public boolean doesPictureSessionOwnPicture(Picture p) {
        return (this.getId() == p.getSession().getId());
    }

}
