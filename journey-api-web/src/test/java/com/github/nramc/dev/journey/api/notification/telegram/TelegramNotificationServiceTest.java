package com.github.nramc.dev.journey.api.notification.telegram;

import com.github.nramc.dev.journey.api.notification.NotificationData;
import com.github.nramc.dev.journey.api.notification.NotificationData.NotificationType;
import com.github.nramc.dev.journey.api.notification.telegram.TelegramProperties.ParseMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.assertArg;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class TelegramNotificationServiceTest {
    private static final TelegramProperties PROPERTIES_ENABLED = TelegramProperties.builder()
            .enabled(true)
            .botToken("token")
            .channelId("channelId")
            .parseMode(ParseMode.HTML)
            .baseUrl("https://api.telegram.org/bot")
            .build();
    private static final TelegramProperties PROPERTIES_DISABLED = PROPERTIES_ENABLED
            .toBuilder()
            .enabled(false)
            .build();
    @Mock
    private TelegramGateway telegramGateway;

    private TelegramNotificationService telegramNotificationService;

    @BeforeEach
    void setup() {
        telegramNotificationService = new TelegramNotificationService(telegramGateway, PROPERTIES_ENABLED);
    }

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
    void sendNotification_whenTelegramDisabled_shouldSkip() {
        var disabledService = new TelegramNotificationService(telegramGateway, PROPERTIES_DISABLED);

        disabledService.sendNotification(NotificationData.of("Hello World"));

        verifyNoInteractions(telegramGateway);
    }

    @Test
    void sendNotification_withEmailOnlyType_shouldSkip() {
        var notificationData = NotificationData.builder()
                .message("Email-only event")
                .type(NotificationType.EMAIL_ONLY)
                .build();

        telegramNotificationService.sendNotification(notificationData);

        verifyNoInteractions(telegramGateway);
    }

    @Test
    void sendNotification_withNullNotificationData_shouldThrowNpe() {
        assertThatThrownBy(() -> telegramNotificationService.sendNotification(null))
                .isInstanceOf(NullPointerException.class);

        verifyNoInteractions(telegramGateway);
    }

    @Test
    void sendNotification_withBlankMessage_shouldThrowException() {
        var notificationData = NotificationData.of("   ");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> telegramNotificationService.sendNotification(notificationData))
                .withMessage("Notification message cannot be null or blank");

        verifyNoInteractions(telegramGateway);
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
    void sendNotification_withParseMode_shouldPassArgumentsAsIs() {
        telegramNotificationService.sendNotification("", null);

        verify(telegramGateway).sendMessage("", null);
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
    void notify_withEmailOnlyType_shouldSkip() {
        var notificationData = NotificationData.builder()
                .message("Email-only event")
                .type(NotificationType.EMAIL_ONLY)
                .build();

        telegramNotificationService.notify(notificationData);

        verifyNoInteractions(telegramGateway);
    }

    @Test
    void notify_whenTelegramDisabled_shouldSkip() {
        var disabledService = new TelegramNotificationService(telegramGateway, PROPERTIES_DISABLED);

        disabledService.notify(NotificationData.of("New user signed up"));

        verifyNoInteractions(telegramGateway);
    }

    @Test
    void notify_withBlankMessage_shouldThrowException() {
        var notificationData = NotificationData.of("\t\n");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> telegramNotificationService.notify(notificationData))
                .withMessage("Notification message cannot be null or blank");

        verifyNoInteractions(telegramGateway);
    }

    @Test
    void notify_shouldUseExpectedAdminFormat() {
        var notificationData = NotificationData.of("System event");

        telegramNotificationService.notify(notificationData);

        verify(telegramGateway).sendMessage(assertArg(msg ->
                assertThat(msg)
                        .startsWith("🔔 <b>Admin Notification</b>")
                        .contains("System event")));
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
