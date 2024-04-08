package com.jobosint.collaboration.tool;

import com.jobosint.collaboration.annotation.Tool;

import java.util.Scanner;

public interface HumanLiaison {
    @Tool(name = "HumanLiaison", description = "Act as a liaison between humans and agents")
    default String prompt(String prompt) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(prompt);
        return scanner.nextLine();
    }
}
