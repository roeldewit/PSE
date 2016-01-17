package com.pse.fotoz.domain.entities;

import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity describing the various options that can be associated with a picture.
 * Currently it only supports the color edition of a picture.
 * @author Robert
 */
@Entity
@Table(name="product_options")
public class ProductOption implements HibernateEntity {
    
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "color")
    private ColorOption color;
    
    @Column(name = "offset_x_start")
    private int offsetXStart;
    
    @Column(name = "offset_x_stop")
    private int offsetXStop;
    
    @Column(name = "offset_y_start")
    private int offsetYStart;
    
    @Column(name = "offset_y_stop")
    private int offsetYStop;
    
    @Column(name = "width")
    private int width;
    
    @Column(name = "height")
    private int height;
    
    private transient Map<String, String> labels;
    
    public int getId() {
        return id;
    }

    public ColorOption getColor() {
        return color;
    }
    
    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }
    
    /**
     * Provides an internationalized label of the color of this option.
     * @pre Labels must have been set (using setLabels).
     * @return Internationalized label of the color of this option
     */
    public String getColorLabel() {
        switch (color) {
            case COLOR:
                return labels.get("color_options_color");
            case GRAYSCALE:
                return labels.get("color_options_grayscale");
            case SEPIA:
                return labels.get("color_options_sepia");
            default:
                return null;
        }
    }
    
    /**
     * Provides the file name of the image representing the different color 
     * options.
     * @return The file name.
     */
    public String getColorImage() {
        switch (color) {
            case COLOR:
                return "color.jpg";
            case GRAYSCALE:
                return "grayscale.jpg";
            case SEPIA:
                return "sepia.jpg";
            default:
                return null;
        }
    }

    public void setColor(ColorOption color) {
        this.color = color;
    }

    public int getOffsetXStart() {
        return offsetXStart;
    }

    public void setOffsetXStart(int offsetXStart) {
        this.offsetXStart = offsetXStart;
    }

    public int getOffsetXStop() {
        return offsetXStop;
    }

    public void setOffsetXStop(int offsetXStop) {
        this.offsetXStop = offsetXStop;
    }

    public int getOffsetYStart() {
        return offsetYStart;
    }

    public void setOffsetYStart(int offsetYStart) {
        this.offsetYStart = offsetYStart;
    }

    public int getOffsetYStop() {
        return offsetYStop;
    }

    public void setOffsetYStop(int offsetYStop) {
        this.offsetYStop = offsetYStop;
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
    
    public static enum ColorOption {
        COLOR, GRAYSCALE, SEPIA
    }
}
