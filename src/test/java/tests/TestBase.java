package tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.time.Duration;
import java.util.Map;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;

public class TestBase {

    @BeforeAll
    static void beforeAll() {
        Configuration.baseUrl = "https://kaspi.kz";
        Configuration.browserSize = "1920x1080";

        Configuration.pageLoadStrategy = "normal";
        Configuration.timeout = 15000;

        String remoteUrl = System.getProperty("remoteUrl", "https://user1:1234@selenoid.qa.guru/wd/hub");

        if (remoteUrl != null && !remoteUrl.isEmpty()) {
            Configuration.remote = remoteUrl;

            DesiredCapabilities capabilities = new DesiredCapabilities();

            capabilities.setCapability("selenoid:options", Map.of("enableVNC", true, "enableVideo", true, "sessionTimeout", "3m"));

            Configuration.browserCapabilities = capabilities;
        }
    }

    @BeforeEach
    void addListener() {
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());
    }

    @AfterEach
    void tearDown() {
        Selenide.closeWebDriver();
    }

    public void closeCityDialogIfPresent() {
        try {
            if (Selenide.$(".city-select").isDisplayed()) {
                Selenide.$(".city-select__close").click();
            }
        } catch (Exception ignored) {
        }
    }

    public void closeInstagramModalIfPresent() {
        var closeButton = $x("//*[local-name()='svg' and (@aria-label='Закрыть' or @aria-label='Close')]");
        if (closeButton.is(visible, Duration.ofSeconds(5))) {
            closeButton.click(com.codeborne.selenide.ClickOptions.usingJavaScript());
            closeButton.shouldNotBe(visible, Duration.ofSeconds(5));
        }
    }
}