package com.pse.fotoz.validators;

import com.pse.fotoz.helpers.ConfigurationHelper;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

/**
 * Validates a MultiPartFile
 *
 * @author Gijs
 */
public class MultipartFileValidator {

    /**
     * Method to validate a MultiPartFile, in this case a picture
     *
     * @param file MultiPartFile to be validated
     * @param errormessages messages to be returned on failure of validation
     * @return List of errormessages. Empty when validation succeeded
     */
    public static List<String> validate(MultipartFile file,
            Map<String, String> errormessages) {

        List<String> errors = new ArrayList<>();
		
        if (file.isEmpty()) {
            // empty uploads
            errors.add(errormessages.get("error_multipartfile_emptyfile"));
        } else if (file.getOriginalFilename().isEmpty()) {
            // empty file names
            errors.add(errormessages.get("error_multipartfile_emptyfilename"));
        } else if ((file.getSize() / 1000)
                > ConfigurationHelper.getMaxmultipartsizeinkb()) {
            //check filesize in kb
            errors.add(
                MessageFormat.format(
                        errormessages.get("error_multipartfile_sizeexceeded"),
                        ConfigurationHelper.getMaxmultipartsizeinkb()));
        } else {
            // whitelist-based validation of file extensions
            String lowerCaseFileName = file.getOriginalFilename().toLowerCase();

            boolean isAllowedExtension = false;

            for (String allowedExtension : ConfigurationHelper.
                    getExtensionwhitelist()) {
                if (lowerCaseFileName.endsWith(allowedExtension)) {
                    isAllowedExtension = true;
                    break;
                }
            }

            if (!isAllowedExtension) {
                errors.add(errormessages.
                        get("error_multipartfile_wrongextension"));
            }
        }
        
        return errors;
    }
}
