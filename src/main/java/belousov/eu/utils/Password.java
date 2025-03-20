package belousov.eu.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class Password {

    private Password() {
    }

    public static String encode(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    public static boolean verify(String password, String hash) {
        return BCrypt.verifyer().verify(password.toCharArray(), hash).verified;
    }
}
