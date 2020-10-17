package wordfeudapi.domain;

import com.google.gson.Gson;

/**
 * @author Pierre Ingmansson
 */
public class User {
    private String username;
    private int id;
    private String email;
    private String sessionId;

    public User(final String username, final int id, final String email, final String sessionId) {
        this.username = username;
        this.id = id;
        this.email = email;
        this.sessionId = sessionId;
    }

    public User(int id) {
        this.id = id;
    }

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(final String sessionId) {
        this.sessionId = sessionId;
    }

    public static User fromJson(final String json) {
        return new Gson().fromJson(json, User.class);
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", id=" + id +
                ", email='" + email + '\'' +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}
