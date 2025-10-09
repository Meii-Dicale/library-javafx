package cda.bibliotheque.controller.category;

import java.util.regex.Pattern;

/**
 * Classe utilitaire pour la validation des champs de formulaire.
 */
public class ValidationUtil {

    // Expression régulière simple pour la validation d'email.
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * Vérifie si une chaîne de caractères est un email valide.
     */
    public static boolean isEmailValid(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Vérifie si une chaîne de caractères peut être convertie en entier.
     */
    public static boolean isInteger(String text) {
        if (text == null || text.trim().isEmpty()) return false;
        return text.matches("-?\\d+");
    }
}