package etu.nic.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoreApplication.class, args);
    }

    //todo транзакции, exeptionhandler, контроллер переделать, эксепшены в сервис для хттп запросов
}
