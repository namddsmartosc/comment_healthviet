package step;

import net.serenitybdd.core.Serenity;
import net.serenitybdd.screenplay.actions.Clear;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.ensure.Ensure;
import net.serenitybdd.screenplay.questions.Text;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.waits.WaitUntil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;
import static net.thucydides.core.webdriver.ThucydidesWebDriverSupport.getDriver;

public class CommonUtils {

    public static void pause(int time) {
        try {
            if (time > 1000) {
                for (int i = 0; i < time / 1000; i++) {
                    System.out.println("Sleep " + (i + 1) + "s");
                    Thread.sleep(1000);
                }
            } else {
                Thread.sleep(time);
            }
        } catch (InterruptedException e) {
            /* Clean up whatever needs to be handled before interrupting  */
            Thread.currentThread().interrupt();
        }
    }

    public static void scrollElementIntoMiddle(WebElement element) {
        String scrollElementIntoMiddle = "arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});";
        ((JavascriptExecutor) getDriver()).executeScript(scrollElementIntoMiddle, element);
        pause(1000);
    }

    public static void scrollElementIntoMiddle(String xpath) {
        WebElement element = getDriver().findElement(By.xpath(xpath));
        scrollElementIntoMiddle(element);
    }

    public static void waitElement(String xpath) {
        Target targetLocators = Target.the(xpath).locatedBy(xpath);
        waitElement(targetLocators);
    }

    public static void waitElement(Target targetLocators) {
        theActorInTheSpotlight().attemptsTo(
                WaitUntil.the(targetLocators, isVisible()).forNoMoreThan(30).seconds());
    }

    public static void typeText(String xpath, String value) {
        Target targetLocators = Target.the(xpath).locatedBy(xpath);
        typeText(targetLocators, value);
    }

    public static void typeText(Target targetLocators, String value) {
        theActorInTheSpotlight().attemptsTo(
                WaitUntil.the(targetLocators, isVisible()).forNoMoreThan(30).seconds(),
                Clear.field(targetLocators),
                Enter.theValue(value).into(targetLocators));
    }

    public static void clickElement(String xpath) {
        Target targetLocators = Target.the(xpath).locatedBy(xpath);
        clickElement(targetLocators);
    }

    public static void clickElement(Target targetLocators) {
        theActorInTheSpotlight().attemptsTo(
                WaitUntil.the(targetLocators, isVisible()).forNoMoreThan(30).seconds(),
                Click.on(targetLocators));
    }

    public static void clickElementByJs(String xpath) {
        WebElement element = Serenity.getDriver().findElement(By.xpath(xpath));
        JavascriptExecutor executor = (JavascriptExecutor) Serenity.getDriver();
        executor.executeScript("arguments[0].click();", element);
        pause(1000);
    }

    public static String getText(String xpath) {
        Target targetLocators = Target.the(xpath).locatedBy(xpath);
        theActorInTheSpotlight().attemptsTo(Ensure.that(targetLocators).isDisplayed());
        return theActorInTheSpotlight().asksFor(Text.of(targetLocators));
    }

    public static List<String> readFileToList(String fileName) {
        List<String> listLine = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(fileName)), StandardCharsets.UTF_8))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                listLine.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return listLine;
    }

    public static double randomNumberGenerator(long lowerLimit, long upperLimit) {
        double randomNumber = Math.random() * (upperLimit - lowerLimit + 1) + lowerLimit;
        return Math.round(randomNumber * 100) / 100.0;
    }

    public static WebElement existElement(String xpath) {
        try {
            pause(500);
            return getDriver().findElement(By.xpath(xpath));
        } catch (Exception e) {
            return null;
        }
    }
}
