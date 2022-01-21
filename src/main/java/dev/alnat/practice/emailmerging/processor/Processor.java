package dev.alnat.practice.emailmerging.processor;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Процессор по задаче
 *
 * Нужно создать новый экземпляр и удалить его после выполнения
 *
 * Created by @author AlNat on 21.01.2022.
 * Licensed by Apache License, Version 2.0
 */
@NoArgsConstructor
public class Processor {

    private final Map<String, String> emailToUserMap = new HashMap<>(128);
    private final Map<String, List<String>> userToEmailMap = new HashMap<>(128);


    public static List<String> process(final List<String> raws) {
        Processor processor = new Processor();
        return processor.processRaws(raws);
    }


    public List<String> processRaws(final List<String> raws) {
        final List<String> mergedUsers = new LinkedList<>();

        for (final String raw : raws) {
            // Парсим строку
            final UserEmailRaw dto = new UserEmailRaw(raw);

            // Проверяем наличие пользователей по кажому email с новой строки
            for (String email : dto.getEmailList()) {
                if (emailToUserMap.containsKey(email)) {
                    String oldUser = emailToUserMap.get(email);

                    // Можно после цикла сделать distinct на списке, но удобнее сразу делать
                    if (!mergedUsers.contains(oldUser)) {
                        mergedUsers.add(emailToUserMap.get(email));
                    }
                }
            }

            // Теперь возможны 2 вариант:
            // 1 -- пользователь ни с кем не совпал -- добавляем его как нового в обе MAP
            if (mergedUsers.isEmpty()) {
                append(dto.getUser(), dto.getEmailList());
                continue;
            }

            // Кейс 2 -- когда нашелся один или более пользователь -- и тогда мы должны всех их слить в одного.
            // Перекладываем список для эффективности и потому что dto возвращает не модифицируемый список :)
            final List<String> newUserEmails = new LinkedList<>(dto.getEmailList());

            // Меняем имена каждому пользователю на последнего -- так удобнее
            for (String foundUser : mergedUsers) {
                final List<String> prevUserEmails = userToEmailMap.get(foundUser);
                userToEmailMap.remove(foundUser);

                for (String email : prevUserEmails) {
                    emailToUserMap.remove(email);

                    // И добавляем только не совпадающий email для обеспечения уникальности
                    if (!newUserEmails.contains(email)) {
                        newUserEmails.add(email);
                    }
                }
            }

            // Теперь сохраняем последнего пользователя как нового
            append(dto.getUser(), newUserEmails);

            mergedUsers.clear(); // Переиспользуем список
        }

        // Теперь выводим в нужном формате
        var res = printResult();

        // Чистим за собой для нового использования
        emailToUserMap.clear();
        userToEmailMap.clear();

        return res;
    }

    private void append(final String user, final List<String> emails) {
        for (String email : emails) {
            emailToUserMap.put(email, user);
        }
        userToEmailMap.put(user, emails);
    }

    private List<String> printResult() {
        return userToEmailMap.entrySet()
                .stream()
                // Собираем итоговую строку, значения собираются через StringJoiner
                .map(e -> {
                    //noinspection StringBufferReplaceableByString
                    var builder = new StringBuilder();
                    builder.append(e.getKey()).append(" -> ");
                    // Сортируем для красоты
                    builder.append(e.getValue().stream().sorted().collect(Collectors.joining(", ")));
                    return builder.toString();
                })
                .collect(Collectors.toList());
    }

    /**
     * DTO представление строки
     *
     * Created by @author AlNat on 21.01.2022.
     * Licensed by Apache License, Version 2.0
     */
    @Data
    private static class UserEmailRaw {

        private String user;
        private List<String> emailList;

        public UserEmailRaw(final String raw) {
            final String[] spited = raw.split("->");
            assert spited.length == 2 : "Raw " + " is malformed!";

            this.user = spited[0].strip();

            final String emailInRaw = spited[1].strip();
            this.emailList = Arrays.stream(emailInRaw.split(",")).map(String::strip).toList();
            assert !emailList.isEmpty() : "Raw " + " is malformed!";
        }

    }


}
