package hiber;

import hiber.config.AppConfig;
import hiber.model.Car;
import hiber.model.User;
import hiber.service.UserService;
import hiber.service.util.Select;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainApp {
   public static void main(String[] args) {
      AnnotationConfigApplicationContext context = 
            new AnnotationConfigApplicationContext(AppConfig.class);

      UserService userService = context.getBean(UserService.class);

      generateRandomUsers(50).forEach(userService::add);
      testMethodsUserService(userService);
      testMethodsUtilSelect(userService);

      context.close();
   }

   private static List<User> generateRandomUsers(int numberOfUsers) {
      List<User> users = new ArrayList<>(numberOfUsers);
      final String[] firstNames = {
              "Юлия", "Алиса", "Мария", "Дарья", "Анна", "Михаил", "Харитон", "Антон", "Сергей", "Иван"};
      final String[] lastNames = {
              "Кузнецов", "Орлов", "Иванов", "Федоров", "Лебедев", "Кузьмин", "Толстов", "Зайцев", "Беляев", "Ильин"};
      int countWomanNames = 5; //Количество женских имен
      int carProbability = 80; // вероятность появления машины у пользователя
      int choiceFirstName, choicelastNames;
      Random random = new Random();

      for (int i = 0; i < numberOfUsers; i++) {
         choiceFirstName = random.nextInt(firstNames.length);
         choicelastNames = random.nextInt(lastNames.length);
         users.add(new User().setFirstName(firstNames[choiceFirstName])
                 .setLastName(lastNames[choicelastNames] + (choiceFirstName + 1 > countWomanNames ? "" : 'а'))
                 .setEmail(generateEmail())
                 .setCar(random.nextInt(100) < carProbability ? generateRandomCar() : null));
      }
      return users;
   }

   private static String generateEmail() {
      Random random = new Random();
      final String[] emailPrefixes = {"myMail", "box", "envelope", "message", "info"};
      final String[] emailSuffixes = {"Job", "Online", "Mail", "Service", "Support"};
      final String[] emailDomains = {"example.com", "test.com", "gmail.ru", "mail.ru"};
      String prefix = emailPrefixes[random.nextInt(emailPrefixes.length)];
      String suffix = emailSuffixes[random.nextInt(emailSuffixes.length)];
      String domain = emailDomains[random.nextInt(emailDomains.length)];

      return String.format("%s%s%d@%s", prefix, suffix,random.nextInt(888) + 88, domain);
   }

   private static Car generateRandomCar() {
      final String[] models = {
              "АвтоВАЗ", "УАЗ", "ГАЗ", "Москвич", "BMW", "Mercedes-Benz", "Audi", "КАМАЗ"};
      final int[] series = {2020, 2021, 2022, 2023, 2024};
      Random random = new Random();
      return new Car(models[random.nextInt(models.length)],  series[random.nextInt(series.length)]);
   }

   private static void testMethodsUserService(UserService userService) {
      // Тестирование различных методов и их всевозможных перегрузок
      testService(userService.listUsers(), "Поиск всех пользователей:");
      testService(userService.findUsersByCar(new Car("АвтоВАЗ", 2022), 3),
              "Поиск первых 3 пользователей с машиной Car{model='АвтоВАЗ', series=2022}:");
      testService(userService.findUsersByCar(new Car("Москвич", 2020), 3),
              "Поиск всех пользователей с машиной Москвич (2020):");
      testService(userService.findFirstUserByCar("Audi", 2023).stream().toList(),
              "Поиск первого пользователя с машиной Audi (2023):");
      testService(userService.findFirstUserWithoutCar().stream().toList(),
              "Поиск первого пользователя без машины:");
      testService(userService.findUsersWithoutCar(3),
              "Поиск первых 3 пользователей без машины");
      testService(userService.findUsersWithoutCar(), "Поиск всех пользователей без машины");
      testService(userService.findUsersByCar("КАМАЗ", 2021),
              "Поиск всех пользователей с машиной КАМАЗ (2021):");
      testService(userService.findUsersByCarModel("BMW", 10),
              "Поиск до 20 пользователей с машиной модели BMW:");
      testService(userService.findUsersByCarSeries(2024, 5),
              "Поиск до 5 пользователей с машиной серии (2024):");
      testService(userService.findUsersWithCar( 4),
              "Поиск до 4 пользователей с любой машиной:");
      testService(userService.findUsersWithCar(),
              "Поиск всех пользователей с любой машиной:");
   }

   private static void testMethodsUtilSelect(UserService userService) {
      Select select = new Select(userService);
      System.out.println("Тестирую Util.Select");

      testService(select.from().users().search(15), "Поиск до 15 пользователей");
      testService(select.from().users().where().uParamAnd("Юлия",null,null)
                      .cParam(null, 2024).search(),
              "Поиск пользователей c именем Юлия и серией машины 2024");
      testService(select.from().cars().search(10), "Поиск до 10 машин");
      testService(select.from().cars().where().uParam("Михаил", "", "").search(),
              "Поиск машин чьих владельцев зовут Михаил");
      testService(select.from().users().where().uUserAnd(new User().setFirstName("Михаил")).cCar(new Car()).search(),
              "Поиск всех пользователей с именем Михаил, у которых есть машина");
      testService(select.from().users().where().uUserAnd(new User().setFirstName("Михаил")).cCar(null).search(),
              "Поиск всех пользователей с именем Михаил, у которых нет машины");
      testService(select.from().users().where().uParamAnd("","","mail.ru").cCar(null).search(),
              "Поиск пользователей без машины c доменом почты mail.ru");
      testService(select.from().users().where().uId(14L).search(),
              "Поиск пользователя с id 14");
      testService(select.from().users().where().cParam("АвтоВАЗ", null).search(),
              "Поиск пользователей с машиной АвтоВАЗ");
      testService(select.from().users().where().uUserAnd(new User().setEmail("Support")).cCar(null).search(),
              "Поиск пользователей с подстрокой Support в почте, у которых нет машины");
      testService(select.from().cars().where().cParam("Mercedes-Benz",null).search() ,
              "Поиск машин по модели Mercedes-Benz");
      testService(select.from().users().where().cCar(new Car()).search(),
              "Поиск всех пользователей, у которых есть машина");
      select.setDefaultMaxResult(10);
      System.out.println("Теперь по умолчанию ищем до 10 результатов");
      testService(select.from().users().where().cParam(null, 2024).search(),
              "Поиск пользователей c машиной серии 2024");
      testService(select.from().users().where().uParam("Иван", "", "").search(),
              "Поиск пользователей c именем Иван");
      testService(select.from().users().where().cCar(null).search(),
              "Поиск пользователей без машины");
      testService(select.from().users().where().uParam("","","example.com").search(),
              "Поиск пользователей c доменом почты example.com");

   }

   private static <T> void testService(List<T> users, String testingMessage) {
      System.out.println(testingMessage);
      if (users == null || users.isEmpty()) {
         System.out.println("По данному запросу ничего не найдено");
      } else {
         users.forEach(System.out::println);
      }
   }
}
