package hiber.model;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {

   @Id
   @Column(name = "user_id")
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "name")
   private String firstName;

   @Column(name = "last_name")
   private String lastName;

   @Column(name = "email")
   private String email;

   @OneToOne
   @JoinColumn(name = "car_id")
   private Car car;

   public User() {}
   
   public User(String firstName, String lastName, String email) {
      this.firstName = firstName;
      this.lastName = lastName;
      this.email = email;
   }

   public User(String firstName, String lastName, String email, Car car) {
      this.firstName = firstName;
      this.lastName = lastName;
      this.email = email;
      this.car = car;
   }

   public Car getCar() {
      return car;
   }

   public User setCar(Car car) {
      this.car = car;
      if (car != null && car.getUser() != this) {
         car.setUser(this);
      }
      return this;
   }

   public Long getId() {
      return id;
   }

   public User setId(Long id) {
      this.id = id;
      return this;
   }

   public String getFirstName() {
      return firstName;
   }

   public User setFirstName(String firstName) {
      this.firstName = firstName;
      return this;
   }

   public String getLastName() {
      return lastName;
   }

   public User setLastName(String lastName) {
      this.lastName = lastName;
      return this;
   }

   public String getEmail() {
      return email;
   }

   public User setEmail(String email) {
      this.email = email;
      return this;
   }

   @Override
   public String toString() {
      return "User{" +
              "id=" + id +
              ", firstName='" + firstName + '\'' +
              ", lastName='" + lastName + '\'' +
              ", email='" + email + '\'' +
              ", car=" + car +
              '}';
   }
}
