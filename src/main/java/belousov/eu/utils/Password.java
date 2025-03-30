package belousov.eu.utils;


public class Password {

    private Password() {
    }

    //TODO
    public static String encode(String password) {
        return password;
//        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    public static boolean verify(String password, String hash) {
        return password.equals(hash);
//
//        return BCrypt.verifyer().verify(password.toCharArray(), hash).verified;
    }
}
