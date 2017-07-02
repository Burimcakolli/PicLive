package cakhen.piclive.models;

/**
 * Created by Burim Cakolli on 02.07.2017.
 */

public class UserLoginDTO {
    public String Username;
    public String Password;

    public UserLoginDTO(String username, String password) {
        Username = username;
        Password = password;
    }
}
