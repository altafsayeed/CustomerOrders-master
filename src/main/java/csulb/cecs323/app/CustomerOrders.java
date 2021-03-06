/*
 * Licensed under the Academic Free License (AFL 3.0).
 *     http://opensource.org/licenses/AFL-3.0
 *
 *  This code is distributed to CSULB students in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, other than educational.
 *
 *  2018 Alvaro Monge <alvaro.monge@csulb.edu>
 *
 */

package csulb.cecs323.app;

// Import all of the entity classes that we have written for this application.
import csulb.cecs323.model.*;
import org.eclipse.persistence.exceptions.DatabaseException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * A simple application to demonstrate how to persist an object in JPA.
 * <p>
 * This is for demonstration and educational purposes only.
 * </p>
 * <p>
 *     Originally provided by Dr. Alvaro Monge of CSULB, and subsequently modified by Dave Brown.
 * </p>
 * Licensed under the Academic Free License (AFL 3.0).
 *     http://opensource.org/licenses/AFL-3.0
 *
 *  This code is distributed to CSULB students in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, other than educational.
 *
 *  2021 David Brown <david.brown@csulb.edu>
 *
 */
public class CustomerOrders {
   /**
    * You will likely need the entityManager in a great many functions throughout your application.
    * Rather than make this a global variable, we will make it an instance variable within the CustomerOrders
    * class, and create an instance of CustomerOrders in the main.
    */
   private EntityManager entityManager;

   /**
    * The Logger can easily be configured to log to a file, rather than, or in addition to, the console.
    * We use it because it is easy to control how much or how little logging gets done without having to
    * go through the application and comment out/uncomment code and run the risk of introducing a bug.
    * Here also, we want to make sure that the one Logger instance is readily available throughout the
    * application, without resorting to creating a global variable.
    */
   private static final Logger LOGGER = Logger.getLogger(CustomerOrders.class.getName());

   /**
    * The constructor for the CustomerOrders class.  All that it does is stash the provided EntityManager
    * for use later in the application.
    * @param manager    The EntityManager that we will use.
    */
   public CustomerOrders(EntityManager manager) {
      this.entityManager = manager;
   }

   public static void main(String[] args) {
      LOGGER.fine("Creating EntityManagerFactory and EntityManager");
      EntityManagerFactory factory = Persistence.createEntityManagerFactory("CustomerOrders");
      EntityManager manager = factory.createEntityManager();
      // Create an instance of CustomerOrders and store our new EntityManager as an instance variable.
      CustomerOrders customerOrders = new CustomerOrders(manager);

      // PROCEDURE PART 1
      //customerOrders.promptCustomer();

      // PROCEDURE PART 2
      //customerOrders.promptProduct();

      // PROCEDURE PART 3
      while(true){
         List<Orders> orders = customerOrders.promptOrders();
      }



      //Products targetProducts = customerOrders.promptProduct();








      // Any changes to the database need to be done within a transaction.
      // See: https://en.wikibooks.org/wiki/Java_Persistence/Transactions

      /** example for transaction
       LOGGER.fine("Begin of Transaction");
       EntityTransaction tx = manager.getTransaction();
       tx.begin();
       // List of Products that I want to persist.  I could just as easily done this with the seed-data.sql
       List <Products> products = new ArrayList<Products>();
       // Load up my List with the Entities that I want to persist.  Note, this does not put them
       // into the database.
       products.add(new Products("076174517163", "16 oz. hickory hammer", "Stanely Tools", "1", 9.97, 50));
       // Create the list of owners in the database.
       customerOrders.createEntity (products);
       // Commit the changes so that the new data persists and is visible to other users.
       tx.commit();
       LOGGER.fine("End of Transaction");
       **/

   } // End of the main method


   public List<Orders> promptOrders(){
      Scanner in = new Scanner(System.in);
      List<Orders> targetOrders = null;
      Customers targetCustomer = null;
      boolean foundCustomer = false;
      while(!foundCustomer){
         System.out.println("Are you a new customer? Y/N");
         String inpNewCustomer = in.nextLine().toUpperCase();
         if(inpNewCustomer.equals("Y")){
            targetCustomer = promptNewCustomer();
            if(targetCustomer != null){
               foundCustomer = true;
            }
         }
         else if(inpNewCustomer.equals("N")){
            targetCustomer = promptCustomer();
            if(targetCustomer != null){
               foundCustomer = true;
            }
         }
         else {
            System.out.println("Input a Y or N, try again.");
         }
      }

      LocalDateTime targetDateTime = promptDateTime();



      return targetOrders;
   } //end of promptOrders

   /**
    * Prompts the user to select a customer from a list of customers stored in the database
    * @return The user's desired customer
    */
   public Customers promptCustomer(){
      Scanner in = new Scanner(System.in);
      boolean foundID = false;
      Customers targetCustomer = null;
      while(!foundID){
         System.out.println("Which customer are you? Select your customer ID from the following customers:");
         List<Customers> customers = getCustomers();
         if(customers != null){
            for(Customers customer: getCustomers()){
               System.out.println("\t" + customer);
            }
            System.out.print("Type your customer id here (leave blank to skip): ");
            String id = in.nextLine();
            if(id.equals("")){
               foundID = true;
            }
            else{
               for(Customers customer: getCustomers()){
                  if(customer.getCustomer_id() == Long.parseLong(id)){
                     targetCustomer = customer;
                     foundID = true;
                  }
               }
            }
            if(!foundID){
               System.out.println("Invalid customer ID! Try again.");
            }
         }
         else {
            System.out.println("No previously existing customers, please indicate as new customer");
            foundID = true;
         }
      }
      if(targetCustomer != null){
         System.out.println("You have selected: " + targetCustomer);
      }
      return targetCustomer;
   } // end of promptCustomer

   /**
    * Prompts the user to select a product from a list of products stored in the database
    * @return The user's desired product
    */
   public Products promptProduct(){
      Scanner in = new Scanner(System.in);
      boolean foundUPC = false;
      Products targetProduct = null;
      while(!foundUPC){
         System.out.println("\nWhich product would you like? Select the desired from the following products:");
         for(Products product: getProducts()){
            System.out.println("\t" + product);
         }
         System.out.print("Type your product UPC here: ");
         String upc = in.nextLine();
         for(Products product: getProducts()){
            if(product.getUPC().equals(upc)){
               targetProduct = product;
               foundUPC = true;
            }
         }
         if(!foundUPC){
            System.out.println("Invalid product UPC! Try again.");
         }
      }
      System.out.println("You have selected: " + targetProduct);
      return targetProduct;
   } // end of promptProduct

   /**
    * Prompts the user to enter the information for a new customer, which will also add the
    * information to persist in the database
    * @return The created custoemr object
    */
   public Customers promptNewCustomer(){
      Customers targetCustomer = null;

      Scanner input = new Scanner(System.in);
      System.out.println("Hello customer, can you please enter your first name:");
      String firstName = input.nextLine();
      System.out.println("Please enter your last name:");
      String lastName = input.nextLine();
      System.out.println("Please enter your phone number:");
      String phone = input.nextLine();
      System.out.println("Please enter your street:" );
      String street = input.nextLine();
      System.out.println("and last, your zip code:");
      String zip = input.nextLine();

      targetCustomer = new Customers(lastName, firstName, street, zip, phone);

      try{
         EntityTransaction tx = this.entityManager.getTransaction();
         tx.begin();
         this.entityManager.persist(targetCustomer);
         tx.commit();
      }
      catch(DatabaseException e){
         System.out.println(e);
         System.out.println("You're not a new customer!");
         targetCustomer = null;
      }
      System.out.println("You are: " + targetCustomer);
      return targetCustomer;
   } //end of promptNewCustomer

   /**
    * Prompts the user to input either a past date time when the order was placed, or to select the current date time
    * @return User's desired and valid date time
    */
   public LocalDateTime promptDateTime(){
      Scanner in = new Scanner(System.in);
      boolean foundDateTime = false;
      LocalDateTime targetDateTime = null;
      while(!foundDateTime){
         System.out.println("\nWhat date was the order placed (year-month-date)? Leave blank if you want to place it right now:");
         System.out.println("Ensure single digits have a 0 in front i.e 2022-04-07");
         String inputDate = in.nextLine();
         LocalDateTime currentTime = LocalDateTime.now();
         if(inputDate.equals("")){
            foundDateTime = true;
            targetDateTime = currentTime;
         }
         else {
            System.out.println("\nWhat time was the order placed (hour:minutes)?:");
            System.out.println("Ensure single digits have a 0 in front i.e 07:03");
            String inputTime = in.nextLine();

            // We'll ignore seconds
            String inputDateTime = inputDate + "T" + inputTime + ":00";
            try{
               targetDateTime = LocalDateTime.parse(inputDateTime);
               if(targetDateTime.isBefore(currentTime)){
                  foundDateTime = true;
               }
            }
            catch (Exception e) {
               System.out.println("Invalid format.");
            }
         }
         if(!foundDateTime){
            System.out.println("Invalid date time! Ensure your selected date and time is before the present. Try again.");
         }
      }
      System.out.println("You have selected: " + targetDateTime);
      return targetDateTime;
   } // end of promptDateTime

   /**
    * Create and persist a list of objects to the database.
    * @param entities   The list of entities to persist.  These can be any object that has been
    *                   properly annotated in JPA and marked as "persistable."  I specifically
    *                   used a Java generic so that I did not have to write this over and over.
    */
   public <E> void createEntity(List <E> entities) {
      for (E next : entities) {
         LOGGER.info("Persisting: " + next);
         // Use the CustomerOrders entityManager instance variable to get our EntityManager.
         this.entityManager.persist(next);
      }

      // The auto generated ID (if present) is not passed in to the constructor since JPA will
      // generate a value.  So the previous for loop will not show a value for the ID.  But
      // now that the Entity has been persisted, JPA has generated the ID and filled that in.
      for (E next : entities) {
         LOGGER.info("Persisted object after flush (non-null id): " + next);
      }
   } // End of createEntity member method

   /**
    * Think of this as a simple map from a String to an instance of Products that has the
    * same name, as the string that you pass in.  To create a new Cars instance, you need to pass
    * in an instance of Products to satisfy the foreign key constraint, not just a string
    * representing the name of the style.
    * @param UPC        The name of the product that you are looking for.
    * @return           The Products instance corresponding to that UPC.
    */
   public Products getProduct (String UPC) {
      // Run the native query that we defined in the Products entity to find the right style.
      List<Products> products = this.entityManager.createNamedQuery("ReturnProduct",
              Products.class).setParameter(1, UPC).getResultList();
      if (products.size() == 0) {
         // Invalid style name passed in.
         return null;
      } else {
         // Return the style object that they asked for.
         return products.get(0);
      }
   }// End of the getProduct method

   public List<Products> getProducts () {
      // Run the native query that we defined in the Products entity to find the right style.
      List<Products> products = this.entityManager.createNamedQuery("ReturnProduct",
              Products.class).setParameter(1, "*").getResultList();
      if (products.size() == 0) {
         // Invalid style name passed in.
         return null;
      } else {
         // Return the style object that they asked for.
         return products;
      }
   }// End of the getProduct method

   /**
    * Think of this as a simple map from a String to an instance of Customers that has the
    * same name, as the string that you pass in.  To create a new Cars instance, you need to pass
    * in an instance of Products to satisfy the foreign key constraint, not just a string
    * representing the name of the style.
    * @param customer_ID        The name of the product that you are looking for.
    * @return           The Customers instance corresponding to that customer_ID.
    */
   public Customers getCustomer (String customer_ID) {
      // Run the native query that we defined in the Products entity to find the right style.
      List<Customers> customers = this.entityManager.createNamedQuery("ReturnCustomer",
              Customers.class).setParameter(1, customer_ID).getResultList();
      if (customers.size() == 0) {
         // Invalid style name passed in.
         return null;
      } else {
         // Return the style object that they asked for.
         return customers.get(0);
      }
   }// End of the getCustomer method

   public List<Customers> getCustomers() {
      // Run the native query that we defined in the Products entity to find the right style.
      List<Customers> customers = this.entityManager.createNamedQuery("ReturnCustomer",
              Customers.class).setParameter(1, "*").getResultList();
      if (customers.size() == 0) {
         // Invalid style name passed in.
         return null;
      } else {
         // Return the style object that they asked for.
         return customers;
      }
   }// End of the getCustomer method
} // End of CustomerOrders class