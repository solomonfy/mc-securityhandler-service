package com.medochemie.ordermanagement.SecurityHandler.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerateEmail {

    private static final String company_domain = "@company.org";
    private static final Logger logger = LoggerFactory.getLogger(GenerateEmail.class);

    public static String generateEmail(String firstName, String lastName){
        StringBuilder email = new StringBuilder();
        if (!firstName.isEmpty() && !lastName.isEmpty())
            {
                try {
                    email = new StringBuilder(firstName.toLowerCase() + "_" + lastName.toLowerCase() + company_domain);
                } catch (Exception exception){
                    logger.error("Can't generate email: {}", exception.getMessage());
                }
            }
        return String.valueOf(email);
    }
}
