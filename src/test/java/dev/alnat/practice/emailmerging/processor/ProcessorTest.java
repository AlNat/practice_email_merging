package dev.alnat.practice.emailmerging.processor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by @author AlNat on 21.01.2022.
 * Licensed by Apache License, Version 2.0
 */
class ProcessorTest {

    /**
     * Тест-кейс
     * Из задачи
     */
    @Test
    void testOriginal() {
        List<String> raws = List.of(
            "user1 -> xxx@ya.ru, foo@gmail.com, lol@mail.ru",
            "user2 -> foo@gmail.com, ups@pisem.net",
            "user3 -> xyz@pisem.net, vasya@pupkin.com",
            "user4 -> ups@pisem.net, aaa@bbb.ru",
            "user5 -> xyz@pisem.net"
        );

        List<String> result = Processor.process(raws);

        Assertions.assertEquals(2, result.size());
        result.sort(Comparator.naturalOrder()); // Для корректной работы тестов

        Assertions.assertEquals("user4 -> aaa@bbb.ru, foo@gmail.com, lol@mail.ru, ups@pisem.net, xxx@ya.ru", result.get(0));
        Assertions.assertEquals("user5 -> vasya@pupkin.com, xyz@pisem.net", result.get(1));
    }

    /**
     * Тест-кейс
     * Несколько пользователей с уникальным email-ами
     * Потом пользователь со всеми этими email-ами
     *
     * Ожидается последний пользователь со всеми email-ами
     */
    @Test
    void testSingleUserWithFewEmails() {
        List<String> raws = List.of(
                "user1 -> test-1@ya.ru",
                "user2 -> test-2@ya.ru",
                "user3 -> test-3@ya.ru",
                "user4 -> test-1@ya.ru, test-2@ya.ru, test-3@ya.ru"
        );

        List<String> result = Processor.process(raws);

        Assertions.assertEquals(1, result.size());
        result.sort(Comparator.naturalOrder()); // Для корректной работы тестов

        Assertions.assertEquals("user4 -> test-1@ya.ru, test-2@ya.ru, test-3@ya.ru", result.get(0));
    }

    /**
     * Тест-кейс
     * Несколько пользователей с попарно совпадающими email-ами
     *
     * Ожидается последний пользователь со всеми email-ами
     */
    @Test
    void testParingMergingUser() {
        List<String> raws = List.of(
                "user1 -> test-1@ya.ru, test-3@ya.ru",
                "user2 -> test-2@ya.ru, test-4@ya.ru",
                "user3 -> test-3@ya.ru, test-5@ya.ru",
                "user4 -> test-4@ya.ru, test-6@ya.ru",
                "user5 -> test-5@ya.ru, test-7@ya.ru",
                "user6 -> test-6@ya.ru, test-8@ya.ru"
        );

        List<String> result = Processor.process(raws);

        Assertions.assertEquals(2, result.size());
        result.sort(Comparator.naturalOrder()); // Для корректной работы тестов

        Assertions.assertEquals("user5 -> test-1@ya.ru, test-3@ya.ru, test-5@ya.ru, test-7@ya.ru", result.get(0));
        Assertions.assertEquals("user6 -> test-2@ya.ru, test-4@ya.ru, test-6@ya.ru, test-8@ya.ru", result.get(1));
    }

    /**
     * Тест-кейс
     * Один пользователь с длинным список email-ов
     *
     * Ожидается один пользователь со всеми email-ами
     */
    @Test
    void testSingleUserInOneRaw() {
        List<String> raws = Collections.singletonList(
                "user1 -> test-1@ya.ru, test-3@ya.ru, test-5@ya.ru, test-7@ya.ru"
        );

        List<String> result = Processor.process(raws);

        Assertions.assertEquals(1, result.size());
        result.sort(Comparator.naturalOrder()); // Для корректной работы тестов

        Assertions.assertEquals("user1 -> test-1@ya.ru, test-3@ya.ru, test-5@ya.ru, test-7@ya.ru", result.get(0));
    }

    /**
     * Тест-кейс
     * Много пользователей с одним email-ом
     *
     * Ожидается один пользователь со одиним email-ом
     */
    @Test
    void testSingleUserInAllRaw() {
        List<String> raws = List.of(
                "user1 -> test@ya.ru",
                "user2 -> test@ya.ru",
                "user3 -> test@ya.ru",
                "user4 -> test@ya.ru",
                "user5 -> test@ya.ru",
                "user6 -> test@ya.ru",
                "user7 -> test@ya.ru",
                "user8 -> test@ya.ru",
                "user9 -> test@ya.ru",
                "user10 -> test@ya.ru"
        );

        List<String> result = Processor.process(raws);

        Assertions.assertEquals(1, result.size());
        result.sort(Comparator.naturalOrder()); // Для корректной работы тестов

        Assertions.assertEquals("user10 -> test@ya.ru", result.get(0));
    }

}
