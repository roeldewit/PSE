package com.pse.fotoz.controllers.producer.dashboard;

import com.pse.fotoz.domain.entities.ProductType;
import com.pse.fotoz.helpers.ConfigurationHelper;
import com.pse.fotoz.helpers.ModelAndViewBuilder;
import com.pse.fotoz.helpers.Parser;
import com.pse.fotoz.helpers.PersistenceFacade;
import com.pse.fotoz.persistence.HibernateEntityHelper;
import com.pse.fotoz.persistence.HibernateException;
import com.pse.fotoz.properties.LocaleUtil;
import com.pse.fotoz.validators.MultipartFileValidator;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller handling the display of product dashboard for producer.
 *
 * @author Gijs
 */
@Controller
@RequestMapping("/producer/dashboard/products")
public class ProducerProducts {

    /**
     * Displays all the Product Types to the producers.
     *
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView displayProducts(HttpServletRequest request) {
        ModelAndView mav = ModelAndViewBuilder.empty().
                withProperties(request).
                build();

        List<ProductType> products = HibernateEntityHelper.
                all(ProductType.class);

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
     * Displays a form to add a new Product Type to the system to the producer.
     *
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

    /**
     * Handles a request to add a new Product Type to the system
     * @param newProdType validated Product Type
     * @param resultProdType result of validation
     * @param request http request
     * @param file picture of new Product Type
     * @return corresponding MAV depending on succes of adding Product Type
     */
    @RequestMapping(method = RequestMethod.POST, value = "/new")
    @ResponseBody
    public ModelAndView handleFileUpload(@ModelAttribute(value = "newProdType")
            @Valid ProductType newProdType,
            BindingResult resultProdType,
            HttpServletRequest request,
            @RequestParam("file") MultipartFile file) {

        ModelAndView mav = ModelAndViewBuilder.empty().
                withProperties(request).
                build();
        mav.setViewName("producer/dashboard/products_new.twig");
        
        List<String> errors = new ArrayList<>();

        //check validation errors
        for (FieldError e : resultProdType.getFieldErrors()) {
            errors.add(e.getDefaultMessage());
        }

        //validate file
        errors.addAll(MultipartFileValidator.validate(
                file,
                LocaleUtil.getErrorProperties(request)));

        if (errors.isEmpty()) {
            String filename = file.getOriginalFilename();

            try {
                //move file
                ServletContext context = request.getServletContext();
                String appPath = context.getRealPath(
                        ConfigurationHelper.getProductTypeAssetLocation());
                String totalname = appPath + "\\" + filename;
                file.transferTo(new File(totalname));
                
                //persist new Product Type
                String name = request.getParameter("name");
                String description = request.getParameter("description");
                BigDecimal price = new BigDecimal(
                        request.getParameter("price"));
                int stock = Integer.parseInt(request.getParameter("stock"));
                int width = Integer.parseInt(request.getParameter("width"));
                int height = Integer.parseInt(request.getParameter("height"));
                int xStart = Integer.parseInt(request.getParameter("overlayXStart"));
                int xStop = Integer.parseInt(request.getParameter("overlayXStop"));
                int yStart = Integer.parseInt(request.getParameter("overlayYStart"));
                int yStop = Integer.parseInt(request.getParameter("overlayYStop"));
                PersistenceFacade.addProductType(
                        name, description, price, stock, filename, width, 
                        height, xStart, xStop, yStart, yStop);
                
                //no errors found. change viewname for succesfull add
                mav.setViewName("producer/dashboard/products_new_success.twig");
            } catch (IOException ex) {
                Logger.getLogger(ProducerProducts.class.getName()).
                        log(Level.SEVERE, null, ex);
                errors.add(LocaleUtil.getProperties(request).
                        get("ERROR_IOERROR"));
            } catch (HibernateException ex) {
                Logger.getLogger(ProducerShops.class.getName()).
                        log(Level.SEVERE, null, ex);
                errors.add(LocaleUtil.getProperties(request).
                        get("ERROR_INTERNALDATABASEERROR"));
            }
        }

        mav.addObject("errors", errors);
        mav.addObject("page", new Object() {
            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String uri = "/producer/dashboard/products";
            public String redirect = request.getRequestURL().toString();
        });

        return mav;
    }

    /**
     * Ajax endpoint handling the request for editing the value of a product
     * type's stock.
     * @param request The associated request.
     * @return 200 on OK, 503 on any error.
     */
    @RequestMapping(method = RequestMethod.POST, value = "/ajax/edit-stock")
    public ResponseEntity<String> editStock(HttpServletRequest request) {
        JSONObject json;
            
        try {
            String data = request.getReader().lines().
                    reduce("", (s1, s2) -> s1 + s2);

            json = new JSONObject(data);
            int id = json.getInt("product_type_id");
            int stock = json.getInt("amount");
            
            Optional<ProductType> type = HibernateEntityHelper.
                    byId(ProductType.class, id);
            
            if (type.isPresent()) {
                PersistenceFacade.setStock(type.get(), stock);
                
                return ResponseEntity.ok().body("ok"); 
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                    body("corrupt form data");                
            }
        } catch (IOException | JSONException | HibernateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                    body("corrupt form data");
        }
    }
}
