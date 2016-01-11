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

    @RequestMapping(method = RequestMethod.GET, path = "/photographers/shop/{shopName}/{sessionCode}/upload")
    public ModelAndView doGet(HttpServletRequest request, HttpServletResponse response,
            @PathVariable String shopName, @PathVariable String sessionCode) {

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
        mav.addObject("shopName", shopName);//hiddenfield voor dropzone.js -> dropzone_init.js
        mav.addObject("sessionCode", sessionCode);//hiddenfield voor dropzone.js -> dropzone_init.js

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
            mav.addObject("error", labels.get("photographers_upload_error_wrongsessionorshop"));
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//niet bestaande shop/sessie
        } else if (!checkCredentials(shop, session)) {
            mav.addObject("error", labels.get("photographers_upload_error_wrongcredentials"));
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);//verkeerde credentials
        }

        return mav;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/photographers/shop/{shopName}/{sessionCode}/upload")
    public @ResponseBody
    String upload(MultipartHttpServletRequest request,
            HttpServletResponse response, @PathVariable String shopName, @PathVariable String sessionCode) {

        ServletContext context = request.getServletContext();
        String appPath = context.getRealPath(ConfigurationHelper.getShopAssetLocation());//dit bepaald de folder waar opgeslagen wordt
        String returnMessage = "";

        Iterator<String> itr = request.getFileNames();
        MultipartFile file = null;

        //check voor bestaande shop/sessie en eigendom + correcte login
        //TODO: uservriendelijke errors(deze errors zouden normaliter niet voor moeten komen dus niet cruciaal hier)
        Shop shop = Shop.getShopByLogin(shopName).orElse(null);
        PictureSession session = PictureSession.getSessionByCode(sessionCode);
        if (shop == null || session == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//niet bestaande shop/sessie
        } else if (!checkCredentials(shop, session)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);//verkeerde credentials
        } else {

            while (itr.hasNext()) {
                file = request.getFile(itr.next());
                Logger.getLogger(file.getOriginalFilename() + " uploaded! ");

                if (!file.isEmpty()) {
                    try {
                        String name = file.getOriginalFilename();

                        //hi-res naar ftp
                        String HighresPath = ("\\hi-res\\" + shop.getLogin() + "\\sessions\\" + session.getId());
                        if (FTPHelper.SendFile(file.getInputStream(), HighresPath, name)) {
                            //opslaan in db
                            BufferedImage imageOriginal = ImageIO.read(file.getInputStream());
                            if (saveUploadToDB(file, name, session, imageOriginal)) {
                                //dan als ftp+db=ok de lowres opslaan
                                File folder = new File(appPath + "\\" + shop.getLogin() + "\\sessions\\" + session.getId());
                                folder.mkdirs(); //maak folder als niet bestaat
                                BufferedImage lowResImage = Scalr.resize(imageOriginal, 800);
                                ImageIO.write(lowResImage, "jpg", new File(folder.getAbsolutePath() + "\\" +  name));
                            }

                            returnMessage = "Upload van " + name + " geslaagd!";
                        }
                    } catch (Exception e) {
                        returnMessage = "Upload mislukt => " + e.getMessage();
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

    private boolean saveUploadToDB(MultipartFile file, String title, PictureSession session, BufferedImage image) throws IOException {
        boolean returnVal = false;
        try {
            //dubbele entry binnen de sessie weghalen: dubbele bestanden worden dus effectief steeds geupdate
            if (Picture.doesFileNameExist(title, session)) {
                Picture p = Picture.getByFileNameAndSession(title, session);
                p.delete();
            }

            Picture pic1 = new Picture();
            pic1.setSession(session);
            pic1.setWidth(image.getWidth());
            pic1.setHeight(image.getHeight());
            pic1.setFileName(file.getOriginalFilename());

            //pic1.setDescription(description);
            //pic1.setPrice(new BigDecimal(0.01));
            pic1.setHidden(false);
            pic1.setApproved(Picture.Approved.PENDING);
            pic1.setSubmissionDate(new Date());
            pic1.setTitle(title);
            pic1.persist();
            returnVal = true;
        } catch (HibernateException ex) {
            Logger.getLogger(PhotographerFileUpload.class.getName()).log(Level.SEVERE, null, ex);
        }
        return returnVal;
    }

    private boolean checkCredentials(Shop s, PictureSession ps) {
        boolean returnVal = false;
        Optional<Shop> loggedInShop = UserHelper.currentShopAccount();

        if (loggedInShop.isPresent()) {
            //zijn we daadwerkelijk ingelogd onder de juiste shop?
            if (loggedInShop.get().getId() == s.getId()) {
                returnVal = OwnershipHelper.doesShopOwnPictureSession(s, ps);
            }
        }
        return returnVal;
    }

}
