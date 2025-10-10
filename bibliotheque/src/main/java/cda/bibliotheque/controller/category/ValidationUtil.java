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

    // Regex pour un mot de passe fort :
    // - au moins 8 caractères
    // - au moins un chiffre
    // - au moins une lettre minuscule
    // - au moins une lettre majuscule
    // - au moins un caractère spécial parmi @#$%^&+=!
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"
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

    /**
     * Vérifie si un mot de passe respecte la politique de sécurité.
     */
    public static boolean isPasswordStrong(String password) {
        if (password == null) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * Retourne la description de la politique de mot de passe pour les messages d'erreur.
     */
    public static String getPasswordPolicy() {
        return "Le mot de passe doit contenir au moins 8 caractères, incluant au minimum :\n" +
                "- Une lettre majuscule\n- Une lettre minuscule\n- Un chiffre\n- Un caractère spécial (@#$%^&+=!)";
    }
}