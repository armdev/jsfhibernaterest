package com.web.rest.client;

import com.project.entities.User;
import com.project.services.UserService;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;

@ManagedBean(name = "postClientBean")
@ViewScoped
public class PostClientBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private UserService userService;
    private User user;
    private Long userCount;

    public PostClientBean() {
       
    }

    @PostConstruct
    public void init() {
        user = new User();
      
    }

    public String sendUserData() {
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpPost request = new HttpPost("http://localhost:8080/hibernate-web/rest/json/services/post/user/register");
            JSONObject json = new JSONObject();
            json.put("email", user.getEmail());
            json.put("firstname", user.getFirstname());
            json.put("lastname", user.getLastname());
            json.put("username", user.getUsername());
            StringEntity params = new StringEntity(json.toString());
            request.addHeader("content-type", "application/json");
            request.addHeader("charset", "utf8");
            request.setEntity(params);
            HttpResponse response = (HttpResponse) httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            System.out.println("Returned " + EntityUtils.toString(entity));
        } catch (IOException | ParseException ex) {

        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return "index";
    }

    public void doAction() {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet("http://localhost:8080/hibernate-web/rest/json/services/get/user/count");
            HttpResponse response = client.execute(request);

            HttpEntity entity = response.getEntity();
            System.out.println("Returned from web service " + EntityUtils.toString(entity));
            System.out.println("Returned from web service 1 " + entity.toString());
            setUserCount(Long.parseLong(EntityUtils.toString(entity)));
            getUserCount();
            System.out.println("user count is " + userCount);
        
        } catch (IOException | ParseException e) {
        }
       
    }

    public Long getUserCount() {
         try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet("http://localhost:8080/hibernate-web/rest/json/services/get/user/count");
            HttpResponse response = client.execute(request);

            HttpEntity entity = response.getEntity();
            System.out.println("Returned from web service " + EntityUtils.toString(entity));
            System.out.println("Returned from web service 1 " + entity.toString());
            setUserCount(Long.parseLong(EntityUtils.toString(entity)));
          
            System.out.println("user count is " + userCount);
        
        } catch (IOException | ParseException e) {
        }
        return userCount;
    }

    public void setUserCount(Long userCount) {
        this.userCount = userCount;
    }

    
    
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static void main(String args[]) {
        PostClientBean test = new PostClientBean();
test.doAction();
    }
}
