package tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.time.Duration;
import java.util.Map;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class TestBase {

    @BeforeAll
    static void setupEnvironment() {
        Configuration.baseUrl = System.getProperty("baseUrl", System.getProperty("url", "https://kaspi.kz"));
        Configuration.browser = System.getProperty("browser", "chrome");
        Configuration.browserSize = System.getProperty("browserSize", "1920x1080");
        Configuration.timeout = 15000; // Увеличим таймаут для удаленного сервера

        String remote = System.getProperty("remote", "https://user1:1234@selenoid.autotests.cloud/wd/hub");

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable-infobars");
        chromeOptions.addArguments("--disable-popup-blocking");
        chromeOptions.addArguments("--disable-notifications");
        chromeOptions.addArguments("--lang=ru-RU");
        chromeOptions.addArguments("--user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);

        if (remote != null && !remote.isEmpty()) {
            Configuration.remote = remote;
            capabilities.setCapability("selenoid:options", Map.<String, Object>of(
                    "enableVNC", true,
                    "enableVideo", true,
                    "enableLog", true
            ));
        }

        Configuration.browserCapabilities = capabilities;
    }

    @BeforeEach
    public void setUp() {
        SelenideLogger.addListener("allure", new AllureSelenide()
                .screenshots(true)
                .savePageSource(true));
    }

    @AfterEach
    public void addAttachments() {
        if (WebDriverRunner.hasWebDriverStarted()) {
            helpers.Attachments.screenshotAs("Last screenshot");
            helpers.Attachments.pageSource();
            helpers.Attachments.browserConsoleLogs();
            helpers.Attachments.addVideo();
        }

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