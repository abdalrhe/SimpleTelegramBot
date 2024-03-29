package com.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.meta.TelegramBotsApi;

import java.io.File;
import java.io.IOException;

public class main extends TelegramLongPollingBot {
    public static void main(String[] args) {
        main bot = new main();
        try {
            bot.run();
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void run() throws TelegramApiException {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
            System.out.println("تم تشغيل الروبوت بنجاح!");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            // تحويل chatId إلى String
            String chatIdString = chatId.toString();

            if (messageText.startsWith("https://") || messageText.startsWith("http://")) {
                try {
                    String videoUrl = messageText;
                    System.out.println("Video URL: " + videoUrl);
                    String downloadDirectory = "/home/cisco/Documents/bot-downlods/ddd/demo/src/main/java/com/example/videos/"; // تحديد المجلد الذي سيتم تنزيل الفيديو فيه

                    // إرسال رسالة "جاري تحميل الفيديو"
                    SendMessage loadingMessage = new SendMessage();
                    loadingMessage.setChatId(chatIdString);
                    loadingMessage.setText("جاري تحميل الفيديو...");
                    try {
                        execute(loadingMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }

                    // تنزيل الفيديو وحفظه في المجلد المحدد
                    String filePath = downloadVideo(videoUrl, downloadDirectory);
                    if (filePath != null) {
                        // إرسال الفيديو إلى المستخدم
                        SendVideo video = new SendVideo();
                        video.setChatId(chatIdString);
                        video.setVideo(new InputFile(new File(filePath)));
                        try {
                            execute(video);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // إرسال رسالة خطأ في حالة فشل تنزيل الفيديو
                        SendMessage errorMessage = new SendMessage();
                        errorMessage.setChatId(chatIdString);
                        errorMessage.setText("فشل تحميل الفيديو.");
                        try {
                            execute(errorMessage);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String downloadVideo(String videoUrl, String downloadDirectory) throws IOException {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"youtube-dl", "-o", downloadDirectory + "video.mp4", videoUrl});
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return downloadDirectory + "video.mp4";
            } else {
                return null;
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getBotUsername() {
        // اسم المستخدم الذي تختاره للبوت
        return "MR_cisco_robot";
    }

    @Override
    public String getBotToken() {
        // توكن البوت الذي تحصل عليه من BotFather في تلغرام
        return "5007391185:AAEWVBnieW5jdtT8aidtmQPJ0S6lLY0UwP0";
    }
}