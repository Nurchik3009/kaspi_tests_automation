package tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import helpers.Attachments;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;


public class TestBase {

    @BeforeAll
    static void setup() {
        Configuration.baseUrl = "https://kaspi.kz";
        Configuration.browserSize = "1920x1080";

        String remoteUrl = System.getProperty("remoteUrl");
        if (remoteUrl != null) {
            Configuration.remote = remoteUrl;
        }

        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());
    }

    @BeforeEach
    void clearCookiesAndStorage() {
        open("/");
        Selenide.clearBrowserCookies();
        Selenide.clearBrowserLocalStorage();
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

    @AfterEach
    void addAttachments() {
        Attachments.screenshotAs("Last screenshot");
        Attachments.pageSource();
        Attachments.browserConsoleLogs();
        if (Configuration.remote != null) {
            Attachments.addVideo();
        }
    }
}