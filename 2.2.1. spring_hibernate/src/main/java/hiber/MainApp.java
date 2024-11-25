package hiber;

import hiber.config.AppConfig;
import hiber.model.Car;
import hiber.model.User;
import hiber.service.UserService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MainApp {
   public static void main(String[] args) {
      AnnotationConfigApplicationContext context =
              new AnnotationConfigApplicationContext(AppConfig.class);

      UserService userService = context.getBean(UserService.class);

      userService.add(new User("User1", "Lastname1", "user1@mail.ru",
              new Car("Model1",2021)));
      userService.add(new User("User2", "Lastname2", "user2@mail.ru",
              new Car("Model2",2022)));
      userService.add(new User("User3", "Lastname3", "user3@mail.ru",
              new Car("Model1",2021)));
      userService.add(new User("User4", "Lastname4", "user4@mail.ru"));

      System.out.println("Поиск всех пользователей:");
      userService.listUsers().forEach(System.out::println);

      System.out.println("Поиск первого пользователя с машиной модели 'Model1' и серии 2021:");
      userService.findFirstUserByCar("Model1", 2021).ifPresentOrElse(System.out::println, MainApp::printNotFoundUser);

      System.out.println("Поиск первого пользователя с машиной модели 'Model1' и серии 2021:");
      userService.findFirstUserByCar("Model2", 2022).ifPresentOrElse(System.out::println, MainApp::printNotFoundUser);

      System.out.println("Поиск первого пользователя с машиной модели 'Model13' и серии 2023:");
      userService.findFirstUserByCar("Model3", 2023).ifPresentOrElse(System.out::println, MainApp::printNotFoundUser);

      context.close();
   }

   // Метод для обработки случая, когда пользователь не найден
   public static void printNotFoundUser() {
      System.out.println("Пользователь не найден.");
   }
}