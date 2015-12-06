package com.pse.fotoz.controllers.customers.shops;

import com.pse.fotoz.domain.entities.Picture;
import com.pse.fotoz.domain.entities.ProductOption;
import com.pse.fotoz.domain.entities.ProductOption.ColorOption;
import com.pse.fotoz.domain.entities.ProductType;
import com.pse.fotoz.domain.filters.PictureFilters;
import com.pse.fotoz.helpers.ModelAndViewBuilder;
import com.pse.fotoz.helpers.UserHelper;
import com.pse.fotoz.helpers.Parser;
import com.pse.fotoz.persistence.HibernateEntityHelper;
import com.pse.fotoz.properties.LocaleUtil;
import java.util.List;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller handling the display of picture pages.
 * @author Robert
 */
@Controller
@RequestMapping("/customers/pictures/")
public class CustomerPicture {
    
    /**
     * Displays a detailed page of a picture.
     * @param pictureid The identity of the picture.
     * @param request The associated request.
     * @param response The associated response.
     * @return View from "customers/pictures/picture_detail.twig".
     */
    @RequestMapping(value = "/{picture}", method = RequestMethod.GET)
    public ModelAndView displayPictureDetail(
            @PathVariable("picture") String pictureid,
            HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = ModelAndViewBuilder.empty().                
                withProperties(request).
                withCookies(request, response).
                build();
        
        mav.addObject("page", new Object() {
            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String uri = "/picture/" + pictureid + "/";
            public String redirect = request.getRequestURL().toString();
        });
        
        final Integer userid = UserHelper.currentUserAccount()
                .map(a -> a.getId())
                .orElse(Integer.MIN_VALUE);
        
        int pictureidi = Parser.parseInt(pictureid)
                .orElse(Integer.MIN_VALUE);
        
        Optional<Picture> picture = HibernateEntityHelper.
                byId(Picture.class, pictureidi);
        
        boolean hasAccess = picture.
                map(p -> PictureFilters.isVisible().test(p)).orElse(false) ||
                picture.map(p -> p.getSession().getPermittedAccounts().stream()
                        .anyMatch(c -> c.getId() == userid))
                        .orElse(false);
        
        if (hasAccess) {
            mav.addObject("picture", picture.get());
            mav.setViewName("customers/pictures/picture_detail.twig");
            
            return mav;
        } else {
            return new ModelAndView("redirect:/app/customers/shops/");
        }
    }
    
    /**
     * Displays a page to allow the user to order a picture (add to the 
     * shopping cart).
     * @param pictureid The identity of the picture.
     * @param request The associated request.
     * @param response The associated response.
     * @return View of "customers/pictures/picture_order.twig".
     */
    @RequestMapping(value = "/{picture}/order", method = RequestMethod.GET)
        public ModelAndView displayPictureOrder(
            @PathVariable("picture") String pictureid,
            final HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = displayPictureDetail(pictureid, request, response);
        
        List<ProductType> types = HibernateEntityHelper.
                all(ProductType.class);
        
        List<ProductOption> colorOptions = Stream.of(ColorOption.values()).
                map(c -> { 
                    ProductOption result = new ProductOption();
                    result.setColor(c);
                    result.setLabels(LocaleUtil.getProperties(request));
                    return result;
                }).
                collect(toList());
        
        mav.addObject("types", types);
        mav.addObject("colors", colorOptions);
        
        if (!mav.getViewName().startsWith("redirect:")) {
            mav.setViewName("customers/pictures/picture_order.twig");
        }
        
        return mav;
    }
}
