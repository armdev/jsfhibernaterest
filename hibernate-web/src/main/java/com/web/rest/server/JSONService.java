package com.web.rest.server;

import com.google.gson.Gson;
import com.project.entities.User;
import com.project.services.UserService;

import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import jersey.repackaged.com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Component
@Path("/json/services")
@Service
public class JSONService {

    private static final Logger log = Logger.getLogger(JSONService.class);
    @Inject
    private UserService userService;

    @Transactional
    @POST
    @Path("/post/user/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response saveUser(String jsonObject) {

        Integer status = 0;
        if (jsonObject == null) {
            return Response.status(ResponseStatus.INVALID_JSON).entity(ResponseStatus.JSON_IS_NULL).build();
        }

        boolean validJson = false;
        try {
            validJson = this.isValidJSON(jsonObject);
        } catch (IOException ex) {
        }

        if (!validJson) {
            return Response.status(ResponseStatus.INVALID_JSON).entity(ResponseStatus.JSON_IS_INVALID).build();
        }
        ObjectMapper mapper = new ObjectMapper();
        User user = new User();
        JsonFactory factory = mapper.getJsonFactory();
        JsonParser jp;
        try {
            jp = factory.createJsonParser(jsonObject.toString());
            JsonNode input = mapper.readTree(jp);
            final JsonNode firstnmae = input.get("firstname");
            final JsonNode lastname = input.get("lastname");
            final JsonNode username = input.get("username");
            final JsonNode email = input.get("email");
            user.setEmail(email.getTextValue());
            user.setFirstname(firstnmae.getTextValue());
            user.setLastname(lastname.getTextValue());
            Long retUserId = userService.saveUser(user);
            if (retUserId != null) {
                return Response.status(ResponseStatus.RECORD_SAVED).entity(ResponseStatus.RECORD_SAVED).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.status(ResponseStatus.SAVE_FAILED).entity(ResponseStatus.SAVE_FAILED).build();
    }

    @GET
    @Path("/get/user/count")
    @Consumes(MediaType.APPLICATION_JSON)
    public Long getUsersCount() {
        Long count = userService.getUsersCount();
        System.out.println("User count is  " + count);
        String json = new Gson().toJson(count.toString());
        return count;
    }

    @GET
    @Path("/get/user/userbyid")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(@QueryParam("userId") Long userId) {
        User user = userService.getUserById(userId);
        String json = new Gson().toJson(user);
        return Response.status(200).entity(json).build();
    }

    @GET
    @Path("/get/user/allusers")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCognitives() {
        List<User> userList = userService.getUserList(0, 100);
        String json = new Gson().toJson(userList);
        return Response.status(200).entity(json).build();
    }

    @POST
    @Path("/post/user/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteById(@QueryParam("userId") Long userId) {

        userService.deleteUser(userId);

        return Response.status(200).entity("Ok").build();
    }

    private boolean isValidJSON(final String json) throws IOException {
        boolean valid = false;
        try {
            final JsonParser parser = new ObjectMapper().getJsonFactory()
                    .createJsonParser(json);
            while (parser.nextToken() != null) {
            }
            valid = true;
        } catch (JsonParseException jpe) {
            //jpe.printStackTrace();
            valid = false;
        }
        return valid;
    }

}
