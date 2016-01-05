/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pse.fotoz.controllers.photographers.shop;

import com.pse.fotoz.domain.entities.Picture;
import com.pse.fotoz.domain.entities.PictureSession;
import com.pse.fotoz.domain.entities.Shop;
import com.pse.fotoz.helpers.ConfigurationHelper;
import com.pse.fotoz.helpers.FTPHelper;
import com.pse.fotoz.helpers.OwnershipHelper;
import com.pse.fotoz.helpers.UserHelper;
import com.pse.fotoz.persistence.HibernateException;
import com.pse.fotoz.properties.LocaleUtil;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author René
 */
@Controller
public class PhotographerFileUpload {

    @RequestMapping(method = RequestMethod.GET, 
            path = "/photographers/shop/{shopName}/{sessionCode}/upload")
    public ModelAndView doGet(HttpServletRequest request, 
            HttpServletResponse response, @PathVariable String shopName, 
            @PathVariable String sessionCode) {

        ModelAndView mav = new ModelAndView();
        Map<String, String> labels;
        mav.setViewName("/photographers/account/upload.twig");

        try {
            labels = LocaleUtil.getProperties(
                    request.getSession().getAttribute("lang").toString());
        } catch (IllegalArgumentException | NullPointerException e) {
            request.getSession().setAttribute("lang", "nl");
            labels = LocaleUtil.getProperties(
                    request.getSession().getAttribute("lang").toString());
        }
        //hiddenfields voor dropzone.js -> dropzone_init.js
        mav.addObject("shopName", shopName);
        mav.addObject("sessionCode", sessionCode);

        mav.addObject("labels", labels);
        mav.addObject("page", new Object() {
            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String redirect = request.getRequestURL().toString();
        });

        //check voor bestaande shop/sessie en eigendom  + correcte login
        Shop shop = Shop.getShopByLogin(shopName).orElse(null);
        PictureSession session = PictureSession.getSessionByCode(sessionCode);
        if (shop == null || session == null) {
            //niet bestaande shop/sessie
            mav.addObject("error", labels.
                    get("photographers_upload_error_wrongsessionorshop"));
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else if (!checkCredentials(shop, session)) {
            //verkeerde credentials
            mav.addObject("error", labels.
                    get("photographers_upload_error_wrongcredentials"));
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        return mav;
    }

    @RequestMapping(method = RequestMethod.POST, 
            path = "/photographers/shop/{shopName}/{sessionCode}/upload")
    public @ResponseBody
    String upload(MultipartHttpServletRequest request,
            HttpServletResponse response, @PathVariable String shopName, 
            @PathVariable String sessionCode) {

        ServletContext context = request.getServletContext();
        //dit bepaald de folder waar opgeslagen wordt
        String appPath = context.getRealPath(
                ConfigurationHelper.getShopAssetLocation());
        String returnMessage = "";


        //check voor bestaande shop/sessie en eigendom + correcte login
        //TODO: uservriendelijke errors(deze errors zouden normaliter niet 
        //voor moeten komen dus niet cruciaal hier)
        Shop shop = Shop.getShopByLogin(shopName).orElse(null);
        PictureSession session = PictureSession.getSessionByCode(sessionCode);
        
        if (shop == null || session == null) {
            //niet bestaande shop/sessie
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else if (!checkCredentials(shop, session)) {
            //verkeerde credentials
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            for (Entry<String, MultipartFile> e : 
                    request.getFileMap().entrySet() ) {
                MultipartFile file = e.getValue();
                String name = e.getKey();
                Logger.getLogger(file.getOriginalFilename() + " uploaded! ");
                if (!file.isEmpty()) {
                    try {
                        //hi-res naar ftp
                        String HighresPath = ("\\hi-res\\" + shop.getLogin() + 
                                "\\sessions\\" + session.getId());
                        if (FTPHelper.SendFile(file.getInputStream(), 
                                HighresPath, name)) {
                            //opslaan in db
                            BufferedImage imageOriginal = ImageIO.
                                    read(file.getInputStream());
                            if (saveUploadToDB(file, name, session, 
                                    imageOriginal)) {
                                //dan als ftp+db=ok de lowres opslaan
                                File folder = new File(appPath + "\\" + 
                                        shop.getLogin() + "\\sessions\\" + 
                                        session.getId());
                                //maak folder als niet bestaat
                                folder.mkdirs();
                                BufferedImage lowResImage = Scalr.
                                        resize(imageOriginal, 800);
                                ImageIO.write(lowResImage, "jpg", 
                                        new File(folder.getAbsolutePath() + 
                                                "\\" +  name));
                            }

                            returnMessage = "Upload van " + name + " geslaagd!";
                        }
                    } catch (Exception ex) {
                        returnMessage = "Upload mislukt => " + ex.getMessage();
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    }
                } else {
                    returnMessage = "Upload van mislukt, bestand was leeg.";
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            }
        }
        return returnMessage;
    }

    private boolean saveUploadToDB(MultipartFile file, String title, 
            PictureSession session, BufferedImage image) throws IOException {
        try {
            //dubbele entry binnen de sessie weghalen: dubbele bestanden worden 
            //dus effectief steeds geupdate
            if (Picture.doesFileNameExist(title, session)) {
                Picture p = Picture.getByFileNameAndSession(title, session);
                p.delete();
            }

            Picture pic1 = new Picture();
            pic1.setSession(session);
            pic1.setWidth(image.getWidth());
            pic1.setHeight(image.getHeight());
            pic1.setFileName(file.getOriginalFilename());

            pic1.setHidden(false);
            pic1.setApproved(Picture.Approved.PENDING);
            pic1.setSubmissionDate(new Date());
            pic1.setTitle(title);
            pic1.persist();
            return true;
        } catch (HibernateException ex) {
            Logger.getLogger(PhotographerFileUpload.class.getName()).
                    log(Level.SEVERE, null, ex);
            return false;
        }
    }

    private boolean checkCredentials(Shop s, PictureSession ps) {
        Optional<Shop> loggedInShop = UserHelper.currentShopAccount();

        if (loggedInShop.isPresent()) {
            //zijn we daadwerkelijk ingelogd onder de juiste shop?
            if (loggedInShop.get().getId() == s.getId()) {
                return OwnershipHelper.doesShopOwnPictureSession(s, ps);
            }
        }
        
        return false;
    }

}
