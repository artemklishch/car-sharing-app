package com.example.carsharingapp.service.telegram;

import com.example.carsharingapp.config.TelegramBotConfig;
import com.example.carsharingapp.exceptions.ProceedingException;
import java.util.ArrayList;
import java.util.List;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class CarTelegramBot extends TelegramLongPollingBot {
    private final TelegramBotConfig config;

    private final static String HELP_TEXT = """
            This bot is created to watch car sharing notification.
            When happen any action like adding car, taking car for rent, doing payment, returning the car 
            - here will come respective notification.
            You can execute some helpful commands from the menu.
            
            Type /start to see the welcome message.
            Type /chat_id to see the the chat bot ID (if you are forget it).
            Type /help to see this message.
            """;

    @PostConstruct
    private void PostConstruct(){
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "get a welcome message"));
        listOfCommands.add(new BotCommand("/chat_id", "get chat ID"));
        listOfCommands.add(new BotCommand("/help", "info how to use this bot"));
        try {
            execute(new SetMyCommands(
                    listOfCommands, new BotCommandScopeDefault(), null)
            );
        } catch (TelegramApiException e) {
            throw new ProceedingException(
                    "Error while adding commands: " + e.getMessage()
            );
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/help":
                    sendMessage(chatId, HELP_TEXT);
                    break;
                case "/chat_id":
                    sendMessage(chatId, String.valueOf(chatId));
                    break;
                default:
                    sendMessage(chatId, "Sorry, command not found");
            }
        }
    }

    private void startCommandReceived(long chatId, String userName) {
        String answer = "Hi, " + userName + ", nice to meet you!";
        sendMessage(chatId, answer);
    }

    public void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new ProceedingException(
                    "Error while sending text: " + e.getMessage()
            );
        }
    }

}

