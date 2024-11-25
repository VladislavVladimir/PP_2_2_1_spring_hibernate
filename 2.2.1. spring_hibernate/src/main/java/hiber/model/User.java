package hiber.model;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "name")
   private String firstName;

   @Column(name = "last_name")
   private String lastName;

   @Column(name = "email")
   private String email;

   @OneToOne(cascade = CascadeType.ALL)
   @JoinColumn(name = "car_id")
   private Car car;

   public User() {}

   public User(String firstName, String lastName, String email) {
      this.setFirstName(firstName).setLastName(lastName).setEmail(email);
   }

   public User(String firstName, String lastName, String email, Car car) {
      this.setCar(car).setFirstName(firstName).setLastName(lastName).setEmail(email);

   }

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getFirstName() { return firstName; }

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