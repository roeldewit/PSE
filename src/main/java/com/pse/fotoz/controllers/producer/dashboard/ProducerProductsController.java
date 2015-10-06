package com.pse.fotoz.controllers.producer.dashboard;

import com.pse.fotoz.dbal.HibernateEntityHelper;
import com.pse.fotoz.dbal.entities.ProductType;
import com.pse.fotoz.helpers.Configuration.ConfigurationHelper;
import com.pse.fotoz.helpers.mav.ModelAndViewBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller handling the display of product dashboard for producer.
 * @author Gijs
 */
@Controller
@RequestMapping("/producer/dashboard/products")
public class ProducerProductsController {

    /**
     * Displays all the products to the producers.
     * @param request
     * @return 
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView displayProducts(HttpServletRequest request) {
        ModelAndView mav = ModelAndViewBuilder.empty().
                    withProperties(request).
                    build();
        
        List<ProductType> products = HibernateEntityHelper.all(ProductType.class);

        mav.addObject("products", products);
        mav.addObject("page", new Object() {
            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String uri = "/producer/dashboard/products";
            public String redirect = request.getRequestURL().toString();
        });
        mav.setViewName("producer/dashboard/products.twig");

        return mav;
    }
    
    /**
     * Displays a form to add new products to the system to the producer.
     * @param request
     * @return 
     */
    @RequestMapping(method = RequestMethod.GET, value = "/new")
    public ModelAndView provideNewProductForm(HttpServletRequest request) {
        ModelAndView mav = ModelAndViewBuilder.empty().
                    withProperties(request).
                    build();
        
        mav.addObject("page", new Object() {
            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String uri = "/producer/dashboard/products";
            public String redirect = request.getRequestURL().toString();
        });
        mav.setViewName("producer/dashboard/products_new.twig");

        return mav;
    }
    
    //TODO: extensie checken, checken voor dubbel, localisatie, errormeldingen
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView handleFileUpload(
        @RequestParam("file") MultipartFile file, HttpServletRequest request) {

        ServletContext context = request.getServletContext();
        String appPath = context.getRealPath(ConfigurationHelper.getProductTypeAssetLocation());//dit bepaald de folder waar opgeslagen wordt
        String filename = "";

        if (!file.isEmpty()) {
            try {
                filename = file.getOriginalFilename();
                String totalname = appPath + "\\" + filename;
                file.transferTo(new File(totalname));
                
                ProductType t = new ProductType();
                t.setName(request.getParameter("name"));
                t.setDescription(request.getParameter("description"));
                t.setPrice(new BigDecimal(request.getParameter("price")));
                t.setStock(new Integer(request.getParameter("stock")));
                t.setFilename(totalname);
                t.setHeight(new Integer(request.getParameter("height")));
                t.setWidth(new Integer(request.getParameter("width")));
                
                t.persist();
                
                //returnMessage = "Upload van " + name + " geslaagd!";
            } catch (Exception e) {
                //returnMessage = "Upload van " + name + " mislukt => " + e.getMessage();
            }
        } else {
            //returnMessage = "Upload van " + name + " mislukt, bestand was leeg.";
        }

        ModelAndView mav = ModelAndViewBuilder.empty().
                    withProperties(request).
                    build();
        
        List<ProductType> products = HibernateEntityHelper.all(ProductType.class);

        mav.addObject("products", products);
        mav.addObject("page", new Object() {
            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String uri = "/producer/dashboard/products";
            public String redirect = request.getRequestURL().toString();
        });
        mav.setViewName("producer/dashboard/products.twig");

        return mav;
    }
    
    /**
     * Handles a request to add a new product to the system by the producer.
     * @param request
     * @return 
     */
    @RequestMapping(method = RequestMethod.POST, value = "/new")
    public ModelAndView handleNewProductForm(HttpServletRequest request) {
        String name = request.getParameter("name");

        
        
        
        
        
//        String login = request.getParameter("login");
//        String password = request.getParameter("password");
//        String name = request.getParameter("name");
//        String address = request.getParameter("address");
//        String city = request.getParameter("city");
//        String email = request.getParameter("email");
//        String phone = request.getParameter("phone");
//        
//        List<String> errors = new ArrayList<>();
//        
//        ModelAndView mav = ModelAndViewBuilder.empty().
//                    withProperties(request).
//                    build();
//        
//        try {
//            InputValidator.ValidationResult result = PersistenceFacade.addShop(login, password, 
//                    name, address, city, email, phone, 
//                    LocaleUtil.getProperties(request));
//            
//            if (result.status() == InputValidator.ValidationStatus.OK) {
//                mav.setViewName("producer/dashboard/shops_new_success.twig");
//            } else {
//                errors.addAll(result.errors());
//                mav.setViewName("producer/dashboard/shops_new.twig");
//            }
//        } catch (HibernateException ex) {
//            Logger.getLogger(ProducerShopsController.class.getName()).
//                    log(Level.SEVERE, null, ex);
//            errors.add(LocaleUtil.getProperties(request).
//                    get("ERROR_INTERNALDATABASEERROR"));
//            
//            mav.setViewName("producer/dashboard/shops_new.twig");
//        }
//        
//        
//        mav.addObject("errors", errors);
//        
//        mav.addObject("page", new Object() {
//            public String lang = request.getSession().
//                    getAttribute("lang").toString();
//            public String uri = "/producer/dashboard/shops";
//            public String redirect = request.getRequestURL().toString();
//        });
        
        ModelAndView mav = ModelAndViewBuilder.empty().
                    withProperties(request).
                    build();
        mav.setViewName("producer/dashboard/products.twig");
        return mav;
    }
    
}
