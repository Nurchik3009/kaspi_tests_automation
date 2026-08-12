package helpers;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static com.codeborne.selenide.Selenide.sessionId;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class Attachments {

    @Attachment(value = "{attachName}", type = "image/png")
    public static byte[] screenshotAs(String attachName) {
        return ((TakesScreenshot) getWebDriver())
                .getScreenshotAs(OutputType.BYTES);
    }

    @Attachment(value = "Page source", type = "text/html")
    public static byte[] pageSource() {
        return getWebDriver()
                .getPageSource()
                .getBytes(StandardCharsets.UTF_8);
    }

    @Attachment(value = "{attachName}", type = "text/plain")
    public static String attachAsText(
            String attachName,
            String message
    ) {
        return message;
    }

    public static void browserConsoleLogs() {
        try {
            attachAsText(
                    "Browser console logs",
                    String.join(
                            "\n",
                            Selenide.getWebDriverLogs("browser")
                    )
            );
        } catch (Exception e) {
            attachAsText(
                    "Browser console logs",
                    "Не удалось получить browser console logs: "
                            + e.getMessage()
            );
        }
    }

    @Attachment(
            value = "Video",
            type = "text/html",
            fileExtension = ".html"
    )
    public static String addVideo() {
        return """
                <html>
                <body>
                <video width="100%%" height="100%%"
                       controls autoplay>
                    <source src="%s" type="video/mp4">
                </video>
                </body>
                </html>
                """.formatted(getVideoUrl());
    }

    public static URL getVideoUrl() {

        String videoStorageUrl = System.getProperty(
                "video.storage.url",
                "https://selenoid.autotests.cloud/video/"
        );

        String videoUrl =
                videoStorageUrl
                        + sessionId()
                        + ".mp4";

        try {
            return new URL(videoUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(
                    "Некорректный URL видео: " + videoUrl,
                    e
            );
        }
    }
}