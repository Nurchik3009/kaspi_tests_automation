package tests;

import com.codeborne.selenide.ClickOptions;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverConditions.url;
import static com.codeborne.selenide.WebDriverConditions.urlStartingWith;
import static io.qameta.allure.Allure.step;

@Owner("Ainur Kaliakbarova")
@Feature("Главная страница и навигация Kaspi.kz")
public class KaspiTests extends TestBase {

    @Test
    @Story("Локализация")
    @DisplayName("1. Смена языка интерфейса с русского на казахский")
    void shouldChangeLanguageToKazakh() {
        step("Открыть главную страницу Kaspi", () -> open("/"));

        step("Переключить язык на казахский", () -> {
            $("[data-lang='kk-KZ']").shouldBe(visible).click();
        });

        step("Проверить, что открылась казахская версия сайта", () -> {
            webdriver().shouldHave(urlStartingWith("https://kaspi.kz/kz"));
        });
    }

    @Test
    @Story("Локализация")
    @DisplayName("2. Переключение языка на русский")
    void shouldChangeLanguageToRussian() {
        step("Открываем казахскую версию Kaspi.kz", () -> open("https://kaspi.kz/kz/"));

        step("Переключаем язык на 'Рус'", () -> {
            $(".lang-switcher__item[data-lang='ru-RU']").shouldBe(visible).click();
        });

        step("Проверяем главный заголовок сервисов на русском языке", () -> {
            $(".services__main-title").shouldBe(visible, Duration.ofSeconds(10)).shouldHave(text("Сервисы Kaspi.kz"));
        });
    }

    @Test
    @Story("Навигация")
    @DisplayName("3. Переход в категорию Компьютеры")
    void shouldOpenComputersCategory() {
        step("Открыть главную страницу Kaspi", () -> open("/"));

        step("Кликнуть на категорию 'Компьютеры'", () -> {
            $$(".category-card").find(text("Компьютеры"))
                    .scrollIntoView(true)
                    .shouldBe(visible)
                    .click(ClickOptions.usingJavaScript());
        });

        step("Проверить переход в категорию 'Компьютеры' и сделать чистый скриншот", () -> {
            webdriver().shouldHave(url("https://kaspi.kz/shop/c/computers/?source=kaspikz"));
            $("h1").shouldBe(visible, Duration.ofSeconds(10)).shouldHave(text("Компьютеры"));
            closeCityDialogIfPresent();
            helpers.Attachments.screenshotAs("Каталог компьютеров загружен");
        });
    }

    @Test
    @Story("Поиск")
    @DisplayName("4. Поиск товара Hoco Gm22")
    void shouldSearchProductInComputersCategory() {
        step("Открыть главную страницу Kaspi", () -> open("/"));

        step("Кликнуть на категорию 'Компьютеры'", () -> {
            $$(".category-card").find(text("Компьютеры"))
                    .scrollIntoView(true)
                    .shouldBe(visible)
                    .click(ClickOptions.usingJavaScript());
        });

        step("Ввести 'Hoco Gm22' в поисковую строку и нажать Enter", () -> {
            closeCityDialogIfPresent();
            $(".search-bar__input").shouldBe(visible).setValue("Hoco Gm22").pressEnter();
        });

        step("Проверить отображение результата поиска и сделать скриншот", () -> {
            closeCityDialogIfPresent();
            // На странице поиска Kaspi названия товаров содержат класс .item-card__name
            $$(".item-card__name, .item-card__title, .item-card")
                    .find(text("Hoco Gm22"))
                    .shouldBe(visible, Duration.ofSeconds(10));
            helpers.Attachments.screenshotAs("Результаты поиска Hoco Gm22");
        });
    }

    @Test
    @Story("Навигация")
    @DisplayName("5. Переход в Kaspi Pay из футера")
    void shouldOpenKaspiGuidePartnerFromFooter() {
        step("Открыть главную страницу Kaspi", () -> open("/"));

        step("Проскроллить вниз и кликнуть на 'Kaspi Pay' в футере", () -> {
            $$("a").find(text("Kaspi Pay"))
                    .scrollIntoView(true)
                    .shouldBe(visible)
                    .click(ClickOptions.usingJavaScript());
        });

        step("Проверить, что произошел переход на страницу Kaspi Pay", () -> {
            webdriver().shouldHave(url("https://kaspi.kz/kaspipay"));
        });
    }

    @Test
    @Story("Шапка сайта")
    @DisplayName("6. Проверка отображения ключевых элементов в шапке сайта")
    void shouldVerifyHeaderElements() {
        step("Открыть главную страницу Kaspi", () -> open("/"));

        step("Проверить наличие основных разделов и переключателей языка в шапке", () -> {
            $$(".header__nav-link").find(text("Клиентам")).shouldBe(visible);
            $$(".header__nav-link").find(text("Бизнесу")).shouldBe(visible);
            $$(".header__nav-link").find(text("Kaspi Гид")).shouldBe(visible);
            $("[data-lang='kk-KZ']").shouldBe(visible).shouldHave(text("Қаз"));
            $("[data-lang='ru-RU']").shouldBe(visible).shouldHave(text("Рус"));
        });
    }

    @Test
    @Story("Навигация")
    @DisplayName("7. Возврат на главную страницу Kaspi.kz по клику на логотип из Kaspi Гид")
    void shouldReturnToMainPageFromKaspiGuideLogo() {
        step("Открыть страницу Kaspi Гид для партнеров", () -> {
            open("https://guide.kaspi.kz/partner/ru");
        });

        step("Кликнуть по логотипу Kaspi в шапке", () -> {
            $("img[alt='logo'], img[src*='Logo.svg']").shouldBe(visible).click();
        });

        step("Проверить переход на главную страницу Kaspi.kz", () -> {
            webdriver().shouldHave(url("https://kaspi.kz/"));
        });
    }

    @Test
    @Story("Социальные сети")
    @DisplayName("8. Переход в Instagram Kaspi.kz из футера с закрытием модального окна")
    void shouldOpenInstagramPageAndCloseModal() {
        step("Открыть главную страницу Kaspi", () -> open("/"));

        step("Кликнуть на иконку Instagram в футере", () -> {
            $("a[href*='instagram.com/kaspi.kz']").scrollIntoView(true).shouldBe(visible).click();
        });

        step("Переключиться на новую вкладку и закрыть окно авторизации Instagram", () -> {
            switchTo().window(1);
            webdriver().shouldHave(url("https://www.instagram.com/kaspi.kz/"));
            closeInstagramModalIfPresent();
            helpers.Attachments.screenshotAs("Страница Kaspi.kz в Instagram без модалки");
        });
    }
}