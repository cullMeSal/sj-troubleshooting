package sj.sj_troubleshooting.dto;

import java.io.Serializable;

public class JwtRequestModel implements Serializable {
    private static final long serialVersionUID = 123L;
    private String email;
    private String password;
    public JwtRequestModel(){
    }
    public JwtRequestModel(String email, String password) {
        super();
        this.email = email; this.password = password;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

}