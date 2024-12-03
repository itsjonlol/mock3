package vttp2023.batch3.ssf.frontcontroller.models;

public class User {
    private String name;
    private Integer noOfAttempts;
    private Boolean isDisabled;
    
    public User() {
        
       
    }
    
    public User(String name, Integer noOfAttempts) {
        this.name = name;
        this.noOfAttempts = noOfAttempts;
       
    }



    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getNoOfAttempts() {
        return noOfAttempts;
    }
    public void setNoOfAttempts(Integer noOfAttempts) {
        this.noOfAttempts = noOfAttempts;
    }

   
    

    
}
