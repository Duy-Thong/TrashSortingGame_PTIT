package Client.controller;

public class LoginController {
    public boolean authenticate(String username, String password) {
        if (username.equals("admin") && password.equals("1234")) {
            return true;
        } else {
            return false;
        }
    }
}
