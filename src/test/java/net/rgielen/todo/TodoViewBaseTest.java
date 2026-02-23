package net.rgielen.todo;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import net.rgielen.TestcontainersConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
abstract class TodoViewBaseTest {

    @LocalServerPort
    int port;

    @Autowired
    TodoRepository todoRepository;

    static Playwright playwright;
    static Browser browser;
    Page page;

    @BeforeAll
    static void setUpBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    }

    @AfterAll
    static void tearDownBrowser() {
        browser.close();
        playwright.close();
    }

    @BeforeEach
    void setUp() {
        todoRepository.deleteAll();
        page = browser.newPage();
    }

    @AfterEach
    void tearDown() {
        page.close();
    }

    String baseUrl() {
        return "http://localhost:" + port;
    }

    void navigateToApp() {
        page.navigate(baseUrl());
    }

    void addTodo(String text) {
        page.locator(".new-todo").fill(text);
        page.waitForResponse(
                r -> r.url().contains("/todos") && r.status() == 200,
                () -> page.locator(".new-todo").press("Enter")
        );
        page.waitForTimeout(100);
    }

    void clickAndWait(String selector) {
        page.waitForResponse(
                r -> r.status() == 200,
                () -> page.locator(selector).click()
        );
        page.waitForTimeout(100);
    }
}
