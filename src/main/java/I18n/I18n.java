package I18n;

import com.virtualparticle.mc.mckoth.McKoth;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class I18n {

    private static final Locale ROOT_LOCALE = Locale.ROOT;
    private static final Locale DEFAULT_LOCALE = Locale.getDefault();
    private static final String MESSAGES_BASE_NAME = "messages";

    private static I18n INSTANCE;

    private Locale locale;
    private ResourceBundle bundle;
    private boolean validLocale;

    public I18n() {
        this(DEFAULT_LOCALE);
    }

    public I18n(Locale locale) {
        this.locale = locale;
        createBundle(locale);
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public static I18n getInstance() {
        return INSTANCE == null ? INSTANCE = new I18n() : INSTANCE;
    }

    private void createBundle() {
        createBundle(locale);
    }

    private void createBundle(Locale locale) {

        try {
            bundle = ResourceBundle.getBundle(MESSAGES_BASE_NAME, locale);
            validLocale = true;
            String localeString = bundle.getLocale().toString();
            if (bundle.getLocale() == Locale.ROOT) {
                localeString = "DEFAULT";
            }
            McKoth.log(Level.INFO, formatString(bundle.getString("loadedLocale"), localeString));
        } catch (MissingResourceException e) {
            validLocale = false;
            McKoth.log(Level.SEVERE, "No localization was found!");
        }

    }

    private String formatString(String msg, String... args) {
        String s = msg;
        for (int i = 0; i < args.length; i++) {
            s = s.replace("{" + i + "}", args[i]);
        }
        return s;
    }

    public String getString(String key, String... args) {
        return formatString(bundle.getString(key), args);
    }

}
