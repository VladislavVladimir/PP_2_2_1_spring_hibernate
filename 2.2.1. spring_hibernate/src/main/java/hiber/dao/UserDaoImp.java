package hiber.dao;

import hiber.model.Car;
import hiber.model.User;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDaoImp implements UserDao {

   @Autowired
   private SessionFactory sessionFactory;

   @Override
   public void add(User user) {
      if (user.getCar() != null) {
         if (isValidCar(user.getCar())) { // проходим валидацию данных для автомобиля
            sessionFactory.getCurrentSession().save(user.getCar()); // Сохраняем машину
         } else {
            System.out.printf("У пользователя %s %s была найдена самодельная машина!%n",
                  user.getFirstName(), user.getLastName());
            System.out.printf("Недопустимую машину с моделью %s и версией %s изъяли у пользователя.%n",
                    user.getCar().getModel(), user.getCar().getSeries());

            user.setCar(null); // Изымаем самодельную машину
         }
      }
      sessionFactory.getCurrentSession().save(user);
   }

   private boolean isValidCar(Car car) { //машина должна иметь название модели, серия больше нуля
      return car != null && car.getModel() != null && !car.getModel().isEmpty() && car.getSeries() > 0;
   }

   @Override
   public List<User> listUsers() {
      return sessionFactory.getCurrentSession().createQuery("from User", User.class).getResultList();
   }

   /**
    * <p>Данный метод осуществляет поиск пользователей на основе переданного объекта <code>Car</code>.
    * В зависимости от состояния этого объекта, поиск может варьироваться:</p>
    *
    * <ul>
    *     <li><strong>Если объект <code>car</code> равен <code>null</code>:</strong>
    *     будут возвращены все пользователи, не имеющие привязанной машины.</li>
    *     <li><strong>Если объект <code>Car</code> не имеет корректных данных:</strong>
    *     <code>(car.getModel() = null || car.getModel().isEmpty()) && (car.getSeries() <= 0)</code>
    *     например, в случае, когда передан <code>new Car()</code>, будут найдены все пользователи,
    *     имеющие любую машину.</li>
    *     <li><strong>Если передан объект <code>сar</code> с установленными параметрами:</strong>
    *     например, <code>new Car().setModel(model)</code>, будет осуществляться поиск пользователей, у которых
    *     имеется машина, соответствующая установленным параметрам <code>сar</code>.</li>
    * </ul>
    *
    * <p>Метод поддерживает возможность ограничения количества возвращаемых результатов. Если параметр
    * <code>maxResults</code> меньше или равен 0, будут возвращены все найденные пользователи.</p>
    *
    * @param car        объект <code>Car</code>, по которому будет происходить поиск пользователей.
    *                   Может быть <code>null</code>, что приведет к поиску пользователей без машины.
    * @param maxResults максимальное количество результатов, которые необходимо вернуть.
    *                   Если значение меньше или равно 0, будут возвращены все найденные пользователи.
    * @return список пользователей, соответствующих условиям поиска, или пустой список, если соответствующих пользователей не найдено.
    */
   @Override
   public List<User> findUsersByCarMaxResults(Car car, int maxResults) {
      if (maxResults <= 0) { maxResults = Integer.MAX_VALUE; } // если число <= 0, значит мы хотим найти всех
      String hql = createHqlForFindUsersByCar(car); // строим запрос по наличию параметров машины

      Query<User> query = sessionFactory.getCurrentSession().createQuery(hql, User.class);

      if (car != null) { // если ищем пользователей с машиной
         if (car.getModel() != null && !car.getModel().isEmpty()) { // если модель присутствует в запросе
            query.setParameter("model", car.getModel()); // добавляем параметр для поиска
         }
         if (car.getSeries() > 0) { // то же самое с серией машины
            query.setParameter("series", car.getSeries());
         }
      }

      return query.setMaxResults(maxResults).getResultList();
   }

   private String createHqlForFindUsersByCar(Car car) {
      StringBuilder hql = new StringBuilder("FROM User u");
      boolean hasCondition = false;

      if (car == null) {
         hql.append(" WHERE u.car IS NULL"); // поиск пользователей без машины
      } else {
         if (car.getModel() != null && !car.getModel().isEmpty()) {
            hql.append(" WHERE u.car.model = :model"); // поиск по модели машины
            hasCondition = true;
         }
         if (car.getSeries() > 0) {
            hql.append(hasCondition ? " AND" : " WHERE").append(" u.car.series = :series"); // поиск по серии машины
            hasCondition = true;
         }
         if (!hasCondition) {
            hql.append(" WHERE u.car IS NOT NULL"); // поиск пользователей с наличием машины
         }
      }

      return hql.toString();
   }

   public List<User> findUsersMaxResults(User user, Car car, int maxResults) {
      return findUsersOrCarMaxResults(user, user, car, maxResults);
   }

   public List<Car> findCarsMaxResults(User user, Car car, int maxResults) {
      return findUsersOrCarMaxResults(car, user, car, maxResults);
   }

   private <T> List<T> findUsersOrCarMaxResults(T t, User user, Car car, int maxResults) {
      SearchType searchType = (t instanceof Car) ? SearchType.CAR : SearchType.USER;
      if (maxResults <= 0) { maxResults = Integer.MAX_VALUE; } // если число <= 0, значит мы хотим найти всех
      String hql = createHqlForFindUsersOrCar(searchType, user, car);
      Query<T> query = sessionFactory.getCurrentSession().createQuery(hql, (Class<T>) t.getClass());

      if (car != null) { // если ищем пользователей с машиной
         if (car.getId() != null && car.getId() > 0) { // Условия поиска по Car
            query.setParameter("carId", car.getId()); // добавляем параметр для поиска
         } else {
            if (propertiesValidate(car.getModel()))
               query.setParameter("model", car.getModel());
            if (propertiesValidate((car.getSeries() > 0) ? "not null" : null))
               query.setParameter("series", car.getSeries());
         }
      }

      if (user != null) { // если ищем машины с пользователем
         if (user.getId() != null && user.getId() > 0) { // Условия поиска по User
            query.setParameter("userId", user.getId());
         } else {
            if (propertiesValidate(user.getFirstName()))
               query.setParameter("firstName", user.getFirstName());
            if (propertiesValidate(user.getLastName()))
               query.setParameter("lastName", user.getLastName());
            if (propertiesValidate(user.getEmail()))
               query.setParameter("email", '%' + user.getEmail() + '%');
         }
      }

      return query.setMaxResults(maxResults).getResultList();
   }

   private String createHqlForFindUsersOrCar(SearchType searchType, User user, Car car) {
      StringBuilder hql = new StringBuilder(searchType.getFromHql());

      // Условия поиска по Car
      if (car != null) {
         if (car.getId() != null && car.getId() > 0) {
            addHearthPropertiesValidate(hql, "not null", searchType.getFirstHql(), ".id = :carId");
         } else {
            addHearthPropertiesValidate(hql, car.getModel(), searchType.getFirstHql(), ".model = :model");
            addHearthPropertiesValidate(hql, (car.getSeries() > 0) ? "not null" : null, searchType.getFirstHql(), ".series = :series");
         }
         if (user.getCar() == car && hql.length() < 13) {
            hql.append(" WHERE u.car IS NOT NULL"); // поиск пользователей с наличием машины
         }
      } else {
         if (searchType == SearchType.USER) {
            hql.append(" WHERE u.car IS NULL"); //поиск пользователей без машины
         }
      }

      // Условия поиска по User
      if (user != null) {
         if (user.getId() != null && user.getId() > 0) {
            addHearthPropertiesValidate(hql, "not null", searchType.getSecondHql(), ".id = :userId");
         } else {
            addHearthPropertiesValidate(hql, user.getFirstName(), searchType.getSecondHql(), ".firstName = :firstName");
            addHearthPropertiesValidate(hql, user.getLastName(), searchType.getSecondHql(), ".lastName = :lastName");
            addHearthPropertiesValidate(hql, user.getEmail(), searchType.getSecondHql(), ".email LIKE :email");
         }
      } else {
         if (searchType == SearchType.CAR) {
            hql.append(" WHERE с.user IS NULL"); //поиск машин без пользователя
         }
      }

      return hql.toString();
   }

   private void addHearthPropertiesValidate(StringBuilder hql, String validate, String secondHql, String suffix) {
      if (propertiesValidate(validate)) {
         hql.append(hql.length() < 13 ? " WHERE " : " AND ").append(secondHql).append(suffix);
      }
   }

   private boolean propertiesValidate(String validate) {
      return (validate != null && !validate.isEmpty());
   }

   public enum SearchType {
      USER("FROM User u","u.car", "u"),
      CAR("FROM Car c","c", "c.user");

      private final String fromHql;
      private final String firstHql;
      private final String secondHql;

      SearchType(String fromHql, String firstHql, String secondHql) {
         this.fromHql = fromHql;
         this.firstHql = firstHql;
         this.secondHql = secondHql;
      }

      public String getFirstHql() { return firstHql; }
      public String getSecondHql() { return secondHql; }
      public String getFromHql() { return fromHql; }
   }
}
