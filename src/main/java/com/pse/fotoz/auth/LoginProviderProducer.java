/*
 */
package com.pse.fotoz.auth;

import com.pse.fotoz.domain.entities.ProducerAccount;
import com.pse.fotoz.helpers.PasswordHasher;
import com.pse.fotoz.persistence.HibernateEntityHelper;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class LoginProviderProducer implements AuthenticationProvider {
 
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();
        Authentication auth = null;

        Iterator<ProducerAccount> accounts = HibernateEntityHelper.all(ProducerAccount.class).
                stream().
                filter(a -> a.getLogin().equals(name)).iterator();
        
        while(accounts.hasNext()){
            ProducerAccount account = accounts.next();
            Boolean check = false;
            try{
                check = PasswordHasher.validatePassword(password, account.getPassword());
            } catch(NoSuchAlgorithmException | InvalidKeySpecException e){
                //@TODO
                //Hoe vanuit hier een exception opvangen / communiceren naar gebruiker?
            }
            
            if(check){
                List<GrantedAuthority> grantedAuths = new ArrayList();
                grantedAuths.add(new SimpleGrantedAuthority(account.getRole()));
                auth = new UsernamePasswordAuthenticationToken(name, password, grantedAuths);
            }
        }

        return auth;

    }
 
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
