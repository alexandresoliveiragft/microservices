package dev.alexandreoliveira.microservices.cardsapi.helpers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Random;

public class CreditCardNumbersHelper {

    public static String masterCardNumber() {
        // really random
        if (LocalDateTime.now().isBefore(LocalDateTime.of(LocalDate.now(), LocalTime.NOON))) {
            int randomNumber = new Random().ints(2221, 2720).findFirst().getAsInt();
            return String.valueOf(randomNumber);
        }

        int randomNumber = new Random().ints(51, 55).findFirst().getAsInt();
        return String.valueOf(randomNumber);
    }

    public static String visaNumber() {
        return String.valueOf(4);
    }
}
