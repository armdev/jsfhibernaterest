package com.web.jsf.beans;

import com.project.entities.User;
import com.project.services.UserService;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;

import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author Home
 */
@Named(value = "userManagedBean")
@ViewScoped
public class UserManagedBean implements Serializable {

    private static final long serialVersionUID = 1L;
    @Inject
    private UserService userService;
    private Long userCount;

    private User user = new User();

    public UserManagedBean() {

    }

    @PostConstruct
    public void init() {
        user = new User();
    }

    public Long getUserCount() {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet("http://localhost:8080/hibernate-web/rest/json/services/get/user/count");
            HttpResponse response = client.execute(request);

            HttpEntity entity = response.getEntity();

            setUserCount(Long.parseLong(EntityUtils.toString(entity)));
            userCount = Long.parseLong(EntityUtils.toString(entity));
            System.out.println("user count is " + userCount);

        } catch (IOException | ParseException e) {
        }
        return userCount;
    }

    public void setUserCount(Long userCount) {
        this.userCount = userCount;
    }

    public List<User> getUserList() {
        return userService.getUserList(0, 1000);
    }

    public String saveUser() {
        User u = userService.getUserByEmail(user.getEmail());
        if (u != null) {
            System.out.println("User is not null, return  ");
            FacesMessage msg = new FacesMessage(getBundle().getString("emailbusy"), getBundle().getString("emailbusy"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        } else {
            userService.saveUser(user);
            return "index";
        }
    }

    public PropertyResourceBundle getBundle() {
        FacesContext context = FacesContext.getCurrentInstance();
        return context.getApplication().evaluateExpressionGet(context, "#{i18n}", PropertyResourceBundle.class);
    }

    public String updateUser() {
        if (user != null) {
            userService.updateUser(user);
        }
        return "index?faces-redirect=true";
    }

    public String deleteUser(Long id) {
        if (id != null) {
            userService.deleteUser(id);
        }
        return "index";
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
