package hook;

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.thucydides.core.util.SystemEnvironmentVariables;

public class Start {

    public static String getProperty(String key) {
        try {
            return EnvironmentSpecificConfiguration.from(SystemEnvironmentVariables.createEnvironmentVariables()).getProperty(key);
        } catch (Exception e) {
            System.out.println(key + " ========> this key is not set.");
            return "";
        }
    }

}
