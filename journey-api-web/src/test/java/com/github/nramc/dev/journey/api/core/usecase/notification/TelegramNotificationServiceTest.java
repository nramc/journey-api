package com.github.nramc.dev.journey.api.core.usecase.notification;

import com.github.nramc.dev.journey.api.gateway.telegram.TelegramGateway;
import com.github.nramc.dev.journey.api.gateway.telegram.TelegramProperties.ParseMode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.assertArg;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TelegramNotificationServiceTest {

    @Mock
    private TelegramGateway telegramGateway;

    @InjectMocks
    private TelegramNotificationService telegramNotificationService;

    @Test
    void contextTest_shouldInitialiseCorrectly() {
        assertThat(telegramGateway).isNotNull();
        assertThat(telegramNotificationService).isNotNull();
    }

    @Test
    void sendNotification_shouldDelegateToGatewayWithSameText() {
        telegramNotificationService.sendNotification("Hello World");

        verify(telegramGateway).sendMessage("Hello World");
    }

    @Test
    void sendNotification_shouldPassTextUnmodified() {
        String message = "Journey: user registered <b>john@example.com</b>";
        telegramNotificationService.sendNotification(message);

        verify(telegramGateway).sendMessage(message);
    }

    @Test
    void sendNotification_withParseMode_shouldDelegateWithBothArguments() {
        telegramNotificationService.sendNotification("*bold*", ParseMode.MARKDOWN_V2);

        verify(telegramGateway).sendMessage("*bold*", ParseMode.MARKDOWN_V2);
    }

    @Test
    void sendNotification_withHtmlParseMode_shouldDelegateWithHtmlMode() {
        telegramNotificationService.sendNotification("<b>important</b>", ParseMode.HTML);

        verify(telegramGateway).sendMessage("<b>important</b>", ParseMode.HTML);
    }

    @Test
    void sendNotification_withNoneParseMode_shouldDelegateWithNoneMode() {
        telegramNotificationService.sendNotification("plain text", ParseMode.NONE);

        verify(telegramGateway).sendMessage("plain text", ParseMode.NONE);
    }

    @Test
    void notify_shouldDelegateToGateway() {
        telegramNotificationService.notify("New user signed up");

        verify(telegramGateway).sendMessage(assertArg(msg -> assertThat(msg).isNotBlank()));
    }

    @Test
    void notify_messageShouldContainBellEmoji() {
        telegramNotificationService.notify("New user signed up");

        verify(telegramGateway).sendMessage(assertArg(msg ->
                assertThat(msg).contains("🔔")));
    }

    @Test
    void notifyAdmin_messageShouldContainBoldNotificationHeader() {
        telegramNotificationService.notify("New user signed up");

        verify(telegramGateway).sendMessage(assertArg(msg ->
                assertThat(msg).contains("<b>Admin Notification</b>")));
    }

    @Test
    void notify_messageShouldContainOriginalNotificationText() {
        String notificationText = "New user registered: john@example.com";
        telegramNotificationService.notify(notificationText);

        verify(telegramGateway).sendMessage(assertArg(msg ->
                assertThat(msg).contains(notificationText)));
    }

    @Test
    void notifyError_shouldDelegateToGateway() {
        telegramNotificationService.notifyError("MongoDB connection failed");

        verify(telegramGateway).sendMessage(assertArg(msg -> assertThat(msg).isNotBlank()));
    }

    @Test
    void notifyError_messageShouldContainAlertEmoji() {
        telegramNotificationService.notifyError("MongoDB connection failed");

        verify(telegramGateway).sendMessage(assertArg(msg ->
                assertThat(msg).contains("🚨")));
    }

    @Test
    void notifyError_messageShouldContainBoldErrorAlertHeader() {
        telegramNotificationService.notifyError("MongoDB connection failed");

        verify(telegramGateway).sendMessage(assertArg(msg ->
                assertThat(msg).contains("<b>Error Alert</b>")));
    }

    @Test
    void notifyError_messageShouldContainOriginalErrorSummary() {
        String errorSummary = "MongoDB connection failed after 3 retries";
        telegramNotificationService.notifyError(errorSummary);

        verify(telegramGateway).sendMessage(assertArg(msg ->
                assertThat(msg).contains(errorSummary)));
    }

    @Test
    void parseMode_htmlApiValue_shouldMatchTelegramApiSpec() {
        assertThat(ParseMode.HTML.mode()).isEqualTo("HTML");
    }

    @Test
    void parseMode_markdownV2ApiValue_shouldMatchTelegramApiSpec() {
        assertThat(ParseMode.MARKDOWN_V2.mode()).isEqualTo("MarkdownV2");
    }

    @Test
    void parseMode_noneApiValue_shouldBeEmptyStringToTriggerJsonExclusion() {
        assertThat(ParseMode.NONE.mode()).isEmpty();
    }
}
