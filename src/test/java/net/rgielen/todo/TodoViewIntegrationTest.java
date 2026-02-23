package net.rgielen.todo;

import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class TodoViewIntegrationTest extends TodoViewBaseTest {

    @Test
    void addTodo_appearsInList() {
        navigateToApp();
        addTodo("Buy groceries");

        assertThat(page.locator(".todo-list li")).hasCount(1);
        assertThat(page.locator(".todo-list li label")).hasText("Buy groceries");
    }

    @Test
    void addMultipleTodos_allAppearInList() {
        navigateToApp();
        addTodo("First todo");
        addTodo("Second todo");
        addTodo("Third todo");

        assertThat(page.locator(".todo-list li")).hasCount(3);
        assertThat(page.locator(".todo-count strong")).hasText("3");
    }

    @Test
    void toggleTodo_marksAsCompleted() {
        navigateToApp();
        addTodo("Toggle me");

        clickAndWait(".todo-list li .toggle");

        assertThat(page.locator(".todo-list li.completed")).hasCount(1);
        assertThat(page.locator(".todo-count strong")).hasText("0");
    }

    @Test
    void toggleTodo_marksAsActive() {
        navigateToApp();
        addTodo("Toggle me");

        clickAndWait(".todo-list li .toggle");
        clickAndWait(".todo-list li .toggle");

        assertThat(page.locator(".todo-list li.completed")).hasCount(0);
        assertThat(page.locator(".todo-count strong")).hasText("1");
    }

    @Test
    void toggleAllTodos_marksAllCompleted() {
        navigateToApp();
        addTodo("First");
        addTodo("Second");

        clickAndWait("#toggle-all");

        assertThat(page.locator(".todo-list li.completed")).hasCount(2);
        assertThat(page.locator(".todo-count strong")).hasText("0");
    }

    @Test
    void toggleAllTodos_marksAllActive() {
        navigateToApp();
        addTodo("First");
        addTodo("Second");

        clickAndWait("#toggle-all");
        clickAndWait("#toggle-all");

        assertThat(page.locator(".todo-list li.completed")).hasCount(0);
        assertThat(page.locator(".todo-count strong")).hasText("2");
    }

    @Test
    void inlineEdit_doubleClickAndEnter() {
        navigateToApp();
        addTodo("Original text");

        page.locator(".todo-list li label").dblclick();
        var editInput = page.locator(".todo-list li .edit");
        editInput.fill("Updated text");
        page.waitForResponse(
                r -> r.url().matches(".*/todos/.*") && r.status() == 200,
                () -> editInput.press("Enter")
        );

        assertThat(page.locator(".todo-list li label")).hasText("Updated text");
    }

    @Test
    void inlineEdit_escapeReverts() {
        navigateToApp();
        addTodo("Original text");

        page.locator(".todo-list li label").dblclick();
        var editInput = page.locator(".todo-list li .edit");
        editInput.fill("Changed text");
        editInput.press("Escape");

        assertThat(page.locator(".todo-list li label")).hasText("Original text");
    }

    @Test
    void deleteTodo_removesFromList() {
        navigateToApp();
        addTodo("Delete me");

        page.locator(".todo-list li").hover();
        clickAndWait(".todo-list li .destroy");

        assertThat(page.locator(".todo-list li")).hasCount(0);
    }

    @Test
    void filterActive_showsOnlyActive() {
        navigateToApp();
        addTodo("Active todo");
        addTodo("Completed todo");

        clickAndWait(".todo-list li:nth-child(2) .toggle");
        clickAndWait("a[data-filter='active']");

        assertThat(page.locator(".todo-list li")).hasCount(1);
        assertThat(page.locator(".todo-list li label")).hasText("Active todo");
    }

    @Test
    void filterCompleted_showsOnlyCompleted() {
        navigateToApp();
        addTodo("Active todo");
        addTodo("Completed todo");

        clickAndWait(".todo-list li:nth-child(2) .toggle");
        clickAndWait("a[data-filter='completed']");

        assertThat(page.locator(".todo-list li")).hasCount(1);
        assertThat(page.locator(".todo-list li label")).hasText("Completed todo");
    }

    @Test
    void filterAll_showsAllTodos() {
        navigateToApp();
        addTodo("Active todo");
        addTodo("Completed todo");

        clickAndWait(".todo-list li:nth-child(2) .toggle");
        clickAndWait("a[data-filter='completed']");
        clickAndWait("a[data-filter='all']");

        assertThat(page.locator(".todo-list li")).hasCount(2);
    }

    @Test
    void clearCompleted_removesCompletedTodos() {
        navigateToApp();
        addTodo("Keep me");
        addTodo("Complete me");

        clickAndWait(".todo-list li:nth-child(2) .toggle");
        clickAndWait(".clear-completed");

        assertThat(page.locator(".todo-list li")).hasCount(1);
        assertThat(page.locator(".todo-list li label")).hasText("Keep me");
    }

    @Test
    void footerHiddenWhenEmpty() {
        navigateToApp();

        assertThat(page.locator("#footer.hidden")).hasCount(1);
    }

    @Test
    void itemCount_singularAndPlural() {
        navigateToApp();
        addTodo("Only one");

        assertThat(page.locator(".todo-count")).containsText("1 item left");

        addTodo("Second one");

        assertThat(page.locator(".todo-count")).containsText("2 items left");
    }

    @Test
    void persistence_survivesPageReload() {
        navigateToApp();
        addTodo("Persistent todo");
        addTodo("Another one");

        page.reload();

        assertThat(page.locator(".todo-list li")).hasCount(2);
        assertThat(page.locator(".todo-list li").first().locator("label")).hasText("Persistent todo");
    }
}
