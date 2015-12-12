package com.pse.fotoz.controllers.photographers.shop;

import com.pse.fotoz.domain.entities.Picture;
import com.pse.fotoz.domain.entities.PictureSession;
import com.pse.fotoz.domain.entities.Shop;
import com.pse.fotoz.helpers.ModelAndViewBuilder;
import com.pse.fotoz.helpers.OwnershipHelper;
import com.pse.fotoz.helpers.Parser;
import com.pse.fotoz.helpers.PersistenceFacade;
import com.pse.fotoz.helpers.PictureSessionCodeGenerator;
import com.pse.fotoz.helpers.UserHelper;
import com.pse.fotoz.persistence.HibernateEntityHelper;
import com.pse.fotoz.persistence.HibernateException;
import com.pse.fotoz.properties.LocaleUtil;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author Gijs
 */
@Controller
@RequestMapping("/photographers/shop/{shopName}/sessions")
public class PhotographersSession {

    /**
     * Displays all picture sessions in a shop to a photographer
     *
     * @param shopName loginname of shop
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView displaySessions(
            @PathVariable("shopName") String shopName,
            HttpServletRequest request, HttpServletResponse response) {

        ModelAndView mav = ModelAndViewBuilder.empty().
                withProperties(request).
                withCookies(request,response).
                build();
        
        mav.addObject("shopName", shopName);
        mav.addObject("page", new Object() {
            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String uri = "/photographers/shop/sessions";
            public String redirect = request.getRequestURL().toString();
        });
        mav.setViewName("photographers/shop/sessions.twig");

        //get current shops sessions
        //redirects to public homepage in case of wrong user/shop combination
        try {
            
            Shop shop = Shop.getShopByLogin(shopName).orElse(null);

            if (OwnershipHelper.doesUserOwnShop(shop,
                    UserHelper.currentUsername().orElse(null))) {
                List<PictureSession> sessions = shop.getSessions();
                mav.addObject("sessions", sessions);
            } else {
                mav = new ModelAndView("redirect:/app/login");
            }
        } catch (NullPointerException ex) {
            //NullPointerException: wrong url
            mav = new ModelAndView("redirect:/app/");
        }

        return mav;
    }

    /**
     * Displays a form to create a new picture session.
     *
     * @param shopName
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/new")
    public ModelAndView showNewSessionForm(
            @PathVariable("shopName") String shopName,
            HttpServletRequest request, HttpServletResponse response) {

        ModelAndView mav = ModelAndViewBuilder.empty().
                withProperties(request).
                withCookies(request,response).
                build();

       mav.addObject("shopName", shopName);
        mav.addObject("page", new Object() {
            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String uri = "/photographers/shop/sessions";
            public String redirect = request.getRequestURL().toString();
        });
        mav.setViewName("photographers/shop/sessions_new.twig");

        //check ownership and redirect in case of wrong user/shop combination
        try {
            
            Shop shop = Shop.getShopByLogin(shopName).orElse(null);
            if (!(OwnershipHelper.doesUserOwnShop(shop,
                    UserHelper.currentUsername().orElse(null)))) {
                mav = new ModelAndView("redirect:/app/login");
            }
        } catch (NullPointerException ex) {
            //NullPointerException: wrong url
            mav = new ModelAndView("redirect:/app/");
        }

        return mav;
    }

    /**
     * Handles a request to create a new Picture Session
     *
     * @param newPicSession Picture Session to be created
     * @param resultPicSession result of Validation of Picture Session
     * @param shopName loginname of shop
     * @param request http request
     * @param redirectAttributes attributes to be added to a redirect view
     * @return corresponding MAV depending on succes of creating Picture Session
     */
    @RequestMapping(method = RequestMethod.POST, value = "/new")
    @ResponseBody
    public ModelAndView handleSessionCreation(
            @ModelAttribute(value = "newPicSession")
            @Valid PictureSession newPicSession,
            BindingResult resultPicSession,
            @PathVariable("shopName") String shopName,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        boolean sessionCreated = false;
        List<String> errors = new ArrayList<>();

        //check validation errors
        for (FieldError e : resultPicSession.getFieldErrors()) {
            errors.add(e.getDefaultMessage());
        }

        if (errors.isEmpty()) {
            try {
                //get form input
                String title = request.getParameter("title");
                String description = request.getParameter("description");
                boolean isPublic = Boolean.parseBoolean(request.getParameter("isPublic"));

                //get shop
                
                Shop shop = Shop.getShopByLogin(shopName).orElse(null);

                //check ownership
                if (OwnershipHelper.doesUserOwnShop(shop,
                        UserHelper.currentUsername().orElse(null))) {
                    //get new session code
                    String code = PictureSessionCodeGenerator.sessionCode(
                            shop.getId(),
                            shop.getSessions().size());

                    //persist new Picture Session
                    PersistenceFacade.addPictureSession(shop, code, title,
                            description, isPublic);

                    sessionCreated = true;
                }

            } catch (HibernateException ex) {
                Logger.getLogger(PhotographersSession.class.getName()).
                        log(Level.SEVERE, null, ex);
                errors.add(LocaleUtil.getProperties(request).
                        get("ERROR_INTERNALDATABASEERROR"));
            } catch (NullPointerException ex) {
                //non existing shop or user
                //no action needed. redirect will take place.
            }
        }

        ModelAndView mav;
        if (sessionCreated) {
            mav = new ModelAndView(
                    "redirect:/app/photographers/shop/" + shopName + "/sessions/");
        } else {
            //in case of wrong ownership page will be redirected once more to /app/
            redirectAttributes.addFlashAttribute("errors", errors);
            mav = new ModelAndView(
                    "redirect:/app/photographers/shop/" + shopName + "/sessions/new");
        }
        return mav;
    }

    /**
     * Displays a picture session.
     *
     * @param shopName login name of shop owning the picture session
     * @param sessionId id of picture session to be shown
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/{sessionId}", method = RequestMethod.GET)
    public ModelAndView showPictureSession(
            @PathVariable("shopName") String shopName,
            @PathVariable("sessionId") String sessionId,
            HttpServletRequest request, HttpServletResponse response) {

        ModelAndView mav = ModelAndViewBuilder.empty().
                withProperties(request).
                withCookies(request,response).
                build();

        mav.addObject("shopName", shopName);
        mav.addObject("page", new Object() {
            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String uri = "/photographers/shop/sessions";
            public String redirect = request.getRequestURL().toString();
        });
        mav.setViewName("photographers/shop/session.twig");
        

        try {
            Integer id = Parser.parseInt(sessionId).orElse(null);

            PictureSession session = HibernateEntityHelper.byId(
                    PictureSession.class, id).orElse(null);
            
            Shop shop = Shop.getShopByLogin(shopName).orElse(null);
            

            //check ownership
            if (OwnershipHelper.doesUserOwnShop(shop,
                    UserHelper.currentUsername().orElse(null)) 
                    && OwnershipHelper.doesShopOwnPictureSession(shop, session)){
                List<Picture> visiblePictures = session.
                    getPictures().stream().sorted().
                    collect(toList());
                
                mav.addObject("session", session);
                mav.addObject("pictures", visiblePictures);
            } else {
                mav = new ModelAndView("redirect:/app/login");
            }

        } catch (NullPointerException ex) {
            //non existing shop or user
            mav = new ModelAndView("redirect:/app/");
        }

        return mav;
    }
    
    
    /**
     * Changes the price of a certain picture
     * 
     * @param shopName
     * @param sessionId
     * @param request
     * @return 
     */
    @RequestMapping(value = "/{sessionId}", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView updatePriceForm(
            @PathVariable("shopName") String shopName, 
            @PathVariable("sessionId") String sessionId,
            HttpServletRequest request) {
        
        ModelAndView mav = ModelAndViewBuilder.empty().
                withProperties(request).
                build();

        mav.addObject("page", new Object() {
            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String uri = "/photographers/shop/sessions";
            public String redirect = request.getRequestURL().toString();
        });
        
        List<String> errors = new ArrayList<>();

        try {
            
            Optional<Integer> sessionIdi = Parser.
                    parseInt(sessionId);
            Optional<Double> price = Parser.
                    parseDouble(request.getParameter("price"));
            Optional<Integer> pictureId = Parser.
                    parseInt(request.getParameter("picture_id"));
            Optional<String> pictureName = Optional.ofNullable(
                    request.getParameter("picture_name"));
            
            Optional<Shop> shop = Shop.getShopByLogin(shopName);
            Optional<PictureSession> session = sessionIdi.flatMap(i ->
                    HibernateEntityHelper.byId(PictureSession.class, i));            
            Optional<Picture> picture = pictureId.flatMap(i -> 
                    HibernateEntityHelper.byId(Picture.class, i));
            
            if(Stream.of(sessionIdi, price, pictureId, pictureId, shop, session, 
                    picture).allMatch(Optional::isPresent) && 
                    shop.get().getSessions().contains(session.get()) &&
                    session.get().getPictures().contains(picture.get())) {
                PersistenceFacade.changePicturePrice(pictureId.get(), 
                        BigDecimal.valueOf(price.get()));                
                PersistenceFacade.changePictureName(pictureId.get(), 
                        pictureName.get());                
            } else {
                errors.add(LocaleUtil.getErrorProperties(request).
                    get("error_decimal_price"));
            }
            
        } catch (HibernateException ex) {
            
            errors.add(LocaleUtil.getErrorProperties(request).
                    get("error_internaldatabaseerror"));
            
        }
        
        mav.addObject("errors", errors);
        
        mav.setViewName("photographers/shop/session.twig");
                
        return mav;
    }
}
