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
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class TestBase {

    @BeforeAll
    static void beforeAll() {

        Configuration.baseUrl = "https://kaspi.kz";
        Configuration.browserSize = "1920x1080";
        Configuration.pageLoadStrategy = "eager";
        Configuration.timeout = 10000;

        /*
         * Локально:
         * ./gradlew test
         *
         * Jenkins:
         * ./gradlew test -DremoteUrl=https://selenoid.autotests.cloud/wd/hub
         *
         * Если remoteUrl передан — запускаем браузер через Selenoid.
         * Если remoteUrl не передан — запускаем локальный Chrome.
         */

        String remoteUrl = System.getProperty("remoteUrl");

        if (remoteUrl != null && !remoteUrl.isBlank()) {

            Configuration.remote = remoteUrl;

            DesiredCapabilities capabilities = new DesiredCapabilities();

            capabilities.setCapability("browserName", "chrome");

            capabilities.setCapability("selenoid:options", Map.of(
                    "enableVNC", true,
                    "enableVideo", true,
                    "sessionTimeout", "3m"
            ));

            Configuration.browserCapabilities = capabilities;
        }
    }

    @BeforeEach
    void addListener() {
        SelenideLogger.addListener(
                "AllureSelenide",
                new AllureSelenide()
        );
    }

    @AfterEach
    void tearDown() {

        boolean isRemote =
                System.getProperty("remoteUrl") != null
                        && !System.getProperty("remoteUrl").isBlank();

        /*
         * Сначала сохраняем стандартные Allure-вложения.
         */
        try {
            helpers.Attachments.screenshotAs("Last screenshot");
        } catch (Exception ignored) {
        }

        try {
            helpers.Attachments.pageSource();
        } catch (Exception ignored) {
        }

        try {
            helpers.Attachments.browserConsoleLogs();
        } catch (Exception ignored) {
        }

        /*
         * Закрываем браузер.
         *
         * Для Selenoid это важно, потому что после завершения
         * browser session видео становится доступным.
         */
        Selenide.closeWebDriver();

        /*
         * После закрытия remote-сессии добавляем видео в Allure.
         */
        if (isRemote) {
            try {
                helpers.Attachments.addVideo();
            } catch (Exception ignored) {
            }
        }
    }

    public void closeCityDialogIfPresent() {
        try {
            if ($(".city-select").isDisplayed()) {
                $(".city-select__close").click();
            }
        } catch (Exception ignored) {
        }
    }

    public void closeInstagramModalIfPresent() {
        try {
            var closeButton = $x(
                    "//*[local-name()='svg' and " +
                            "(@aria-label='Закрыть' or @aria-label='Close')]"
            );

            if (closeButton.is(visible, Duration.ofSeconds(5))) {
                closeButton.click(
                        com.codeborne.selenide.ClickOptions.usingJavaScript()
                );

                closeButton.shouldNotBe(
                        visible,
                        Duration.ofSeconds(5)
                );
            }
        } catch (Exception ignored) {
        }
    }
}