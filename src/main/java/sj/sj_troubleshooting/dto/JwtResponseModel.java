package sj.sj_troubleshooting.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class JwtResponseModel implements Serializable {
    @Value("${tokenValidity}")
    private int tokenValidity;
    private static final long serialVersionUID = 1L;
    private final String token;
    private final Instant expiredAt;
    public JwtResponseModel(String token){
        this.token = token;
        this.expiredAt = Instant.now().plus(tokenValidity, ChronoUnit.MILLIS);
    }
    public String getToken(){
        return token;
    }

    public Instant getExpiredAt() {
        return expiredAt;
    }
}
