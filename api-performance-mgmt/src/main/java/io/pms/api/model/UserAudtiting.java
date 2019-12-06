package io.pms.api.model;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserAudtiting implements AuditorAware<String>{

    @Override
    public String getCurrentAuditor() {

        String uname = SecurityContextHolder.getContext().getAuthentication().getName();
        return uname;
    }

}