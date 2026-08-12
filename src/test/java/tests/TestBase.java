package tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.time.Duration;
import java.util.Map;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class TestBase {

    @BeforeAll
    static void beforeAll() {
        Configuration.baseUrl = "https://kaspi.kz";
        Configuration.browserSize = System.getProperty("browserSize", "1920x1080");
        Configuration.browser = System.getProperty("browser", "chrome");

        String remoteUrl = System.getProperty("remoteUrl", "https://user1:1234@selenoid.autotests.cloud/wd/hub");
        if (!remoteUrl.isEmpty()) {
            Configuration.remote = remoteUrl;

            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability("selenoid:options", Map.<String, Object>of(
                    "enableVNC", true,
                    "enableVideo", true
            ));
            Configuration.browserCapabilities = capabilities;
        }

        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());
    }

    @AfterEach
    void addAttachments() {
        helpers.Attachments.screenshotAs("Final screenshot");
        helpers.Attachments.pageSource();
        helpers.Attachments.browserConsoleLogs();
        helpers.Attachments.addVideo();
        closeWebDriver();
    }

    public void closeCityDialogIfPresent() {
        if ($(".dialog").is(visible)) {
            if ($(".dialog__close").is(visible)) {
                $(".dialog__close").click(com.codeborne.selenide.ClickOptions.usingJavaScript());
            } else if ($$(".dialog__link").find(text("Алматы")).is(visible)) {
                $$(".dialog__link").find(text("Алматы")).click();
            }
            $(".dialog").shouldNotBe(visible, Duration.ofSeconds(5));
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