package tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.time.Duration;
import java.util.Map;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class TestBase {

    @BeforeAll
    static void setupEnvironment() {
        Configuration.baseUrl = System.getProperty("baseUrl", "https://kaspi.kz");
        Configuration.browser = System.getProperty("browser", "chrome");
        Configuration.browserSize = System.getProperty("browserSize", "1920x1080");
        Configuration.timeout = 15000;

        // ВАЖНО: Не ждем полной загрузки тяжелых ресурсов/баннеров Kaspi
        Configuration.pageLoadStrategy = "eager";

        org.openqa.selenium.chrome.ChromeOptions options = new org.openqa.selenium.chrome.ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");

        String remote = System.getProperty("remote", "https://user1:1234@selenoid.autotests.cloud/wd/hub");
        if (remote != null && !remote.isEmpty()) {
            Configuration.remote = remote;

            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability("selenoid:options", Map.<String, Object>of(
                    "enableVNC", true,
                    "enableVideo", true,
                    "enableLog", true
            ));
            capabilities.setCapability(org.openqa.selenium.chrome.ChromeOptions.CAPABILITY, options);
            Configuration.browserCapabilities = capabilities;
        }

        System.out.println("URL: " + Configuration.baseUrl);
        System.out.println("Browser: " + Configuration.browser);
        System.out.println("Browser size: " + Configuration.browserSize);
        System.out.println("Remote: " + Configuration.remote);
    }

    @BeforeEach
    public void setUp() {
        SelenideLogger.addListener("allure", new AllureSelenide()
                .screenshots(true)
                .savePageSource(true));
    }

    @AfterEach
    public void addAttachments() {
        helpers.Attachments.screenshotAs("Last screenshot");
        helpers.Attachments.pageSource();
        helpers.Attachments.browserConsoleLogs();
        helpers.Attachments.addVideo();

        SelenideLogger.removeListener("allure");
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