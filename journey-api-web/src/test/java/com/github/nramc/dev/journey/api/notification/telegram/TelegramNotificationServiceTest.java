package com.github.nramc.dev.journey.api.notification.telegram;

import com.github.nramc.dev.journey.api.notification.NotificationData;
import com.github.nramc.dev.journey.api.notification.telegram.TelegramProperties.ParseMode;
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
        telegramNotificationService.sendNotification(NotificationData.of("Hello World"));

        verify(telegramGateway).sendMessage("Hello World");
    }

    @Test
    void sendNotification_shouldPassTextUnmodified() {
        NotificationData notificationData = NotificationData.of("Journey: user registered <b>john@example.com</b>");
        telegramNotificationService.sendNotification(notificationData);

        verify(telegramGateway).sendMessage(notificationData.message());
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
        telegramNotificationService.notify(NotificationData.of("New user signed up"));

        verify(telegramGateway).sendMessage(assertArg(msg -> assertThat(msg).isNotBlank()));
    }

    @Test
    void notify_messageShouldContainBellEmoji() {
        telegramNotificationService.notify(NotificationData.of("New user signed up"));

        verify(telegramGateway).sendMessage(assertArg(msg ->
                assertThat(msg).contains("🔔")));
    }

    @Test
    void notifyAdmin_messageShouldContainBoldNotificationHeader() {
        telegramNotificationService.notify(NotificationData.of("New user signed up"));

        verify(telegramGateway).sendMessage(assertArg(msg ->
                assertThat(msg).contains("<b>Admin Notification</b>")));
    }

    @Test
    void notify_messageShouldContainOriginalNotificationText() {
        var notificationData = NotificationData.of("New user registered: john@example.com");
        telegramNotificationService.notify(notificationData);

        verify(telegramGateway).sendMessage(assertArg(msg ->
                assertThat(msg).contains(notificationData.message())));
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
