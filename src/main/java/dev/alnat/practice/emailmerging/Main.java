package dev.alnat.practice.emailmerging;

import dev.alnat.practice.emailmerging.processor.Processor;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by @author AlNat on 21.01.2022.
 * Licensed by Apache License, Version 2.0
 */
public class Main {

    public static void main(String[] args) {
        List<String> inputData = new LinkedList<>();

        // Читаем данные из stdin
        Scanner input = new Scanner(System.in);
        while (input.hasNext()) {
            inputData.add(input.nextLine());
        }
        input.close();

        // Пишем их
        List<String> result = Processor.process(inputData);
        for(String s : result) {
            System.out.println(s);
        }
    }

}
