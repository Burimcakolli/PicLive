package cakhen.piclive.models;

/**
 * Created by Burim Cakolli on 29.06.2017.
 */

public class UserRegisterDTO {
    public String Username;
    public String Password;
    public String ConfirmPassword;

    public UserRegisterDTO(String username, String password, String confirmPassword) {
        Username = username;
        Password = password;
        ConfirmPassword = confirmPassword;
    }
}
