package com.Revature

import java.sql.DriverManager
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import java.lang.String
import scala.io.StdIn.readLine


class Bank {
  private var user_login:String = "";
  private var user_password:String = "";
  def setUserLogin(username:String):Unit= {
    this.user_login = username;
  }
  def setUserPassword(password:String):Unit= {
    this.user_password = password;
  }
  def getUserLogin():String= {
    this.user_login;
  }
  def getUserPassword():String= {
    this.user_password;
  }
}



object JDBC2 {
  def main(args: Array[String]) {
    // connect to the database named "mysql" on the localhost
    //val driver = "com.mysql.jdbc.Driver"
    val driver = "com.mysql.cj.jdbc.Driver"
    val url = "jdbc:mysql://localhost:3306/test"
    val username = "root"
    val password = "Passport1999"

    // there's probably a better way to do this
    var connection:Connection = null

    val bank_1 = new Bank(); //instantiate bank instance
    var loggedin:Boolean = false; //boolean to keep track of whether user is logged in successfully
    var signedup:Boolean = false;//boolean to keep track of whether user is creating an account
    var exiting:Boolean = false; //boolean to keep track of whether the user is quitting this session
    var desired_username:String = "";
    var desired_password:String = "placeholder_1";
    var confirm_password:String = "placeholder_2";
    while (!exiting) { //while the exit command hasn't been issued
      println("Would you like to login or signup? Enter L for login, S for signup, or E to exit.")
      while (!loggedin && !exiting) { //read input until user attempts to login or exit the session
        val temp: String = readLine();
        if (temp == "L") { //if user enters l ("el"), proceed to login process
          loggedin = true;
        }
        else if (temp == "S") {
          Class.forName(driver)
          connection = DriverManager.getConnection(url, username, password)
          while (!signedup) { //stay in while loop until user finishes signing up
            println("What would you like your username to be?")
            desired_username = readLine();
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery(s"SELECT * FROM users HAVING users.username=\'$desired_username\';")
            if (!resultSet.next()) { //enter do-while loop to create password only after user has elected a unique username
              loggedin = false;
              do { //prompt user for password before checking that user entered password correctly, looping until user does so
                println("What would you like your password to be?");
                desired_password = readLine()
                println("Confirm password please")
                confirm_password = readLine()
                if (desired_password != confirm_password) {
                  println("Passwords did not match")
                }
              } while (desired_password != confirm_password)
              signedup = true;//break out of current while loop
            }
            else {
              println("The username is already taken");
            }
          }
          if (signedup) {
            val statement = connection.createStatement()
            val resultSet = statement.executeUpdate(s"INSERT INTO users(username,password,funds) VALUES ('$desired_username','$desired_password',0);")
            println("Would you like to access your account? Y for yes, N for no.")
            val temp = readLine()
            if (temp == "N") {
              exiting = true;
            }
            else if (temp == "Y") {
              loggedin = true
            }
          }
        }
        else if (temp == "E") {
          exiting = true;
        }
      }
      if (loggedin) {
        println("What is your username?");
        val temp_username = readLine();
        println("What is your password?");
        val temp_password = readLine();

        try {
          // make the connection
          if (!signedup) {
            Class.forName(driver)
            connection = DriverManager.getConnection(url, username, password)
          }

          // create the statement, and run the select query
          val statement = connection.createStatement()

          val resultSet = statement.executeQuery(s"SELECT * FROM users HAVING users.username=\'$temp_username\' and users.password=\'$temp_password\';")
          if (!resultSet.next()) {
            loggedin = false;
            println("You don't have an account here.");
          }
          /*else {
            println("the user exists");
          }*/
        } catch {
          case e: Throwable => e.printStackTrace
        }
        if (loggedin) {
          println("What would you like to do? D to deposit, W to withdraw, V to view balance, E to exit, or DELETE to delete your account.")
          while (!exiting) {
            val temp: String = readLine();
            if (temp == "D") {
              var valid_amount = false
              while (!valid_amount) {
                println("Enter deposit amount or 0 if you would like to return to menu.")
                val deposit_amount = readLine().toInt
                if (deposit_amount > 0) {
                  valid_amount = true
                  var user_funds = 0;
                  val funds_query = connection.createStatement()
                  val funds_result = funds_query.executeQuery(s"SELECT funds FROM users WHERE users.username=\'$temp_username\' and users.password=\'$temp_password\';")
                  while (funds_result.next()) {
                    //println("You account balance is: " + user_funds.getString(1))
                    val temp = funds_result.getString(1)
                    user_funds += temp.toInt
                  }
                  user_funds += deposit_amount
                  val deposit_update = connection.createStatement()
                  val deposit_result = deposit_update.executeUpdate(s"UPDATE users SET funds=$user_funds WHERE users.username=\'$temp_username\' and users.password=\'$temp_password\';")
                  val id_query = connection.createStatement()
                  val id_result = id_query.executeQuery(s"SELECT user_id FROM users WHERE users.username=\'$temp_username\' and users.password=\'$temp_password\';")
                  while (id_result.next()) {
                    val id_string = id_result.getString(1)
                    val id_int = id_string.toInt
                    val transaction_update = connection.createStatement()
                    val transaction_result = transaction_update.executeUpdate(s"INSERT INTO transaction_history(user_id,transaction_type,transaction_amount) VALUES($id_int,'deposit',$deposit_amount)")
                  }
                  println("Depositing ... done!")
                }
                else if (deposit_amount < 0) {
                  println("Invalid amount to withdraw")
                }
                else {
                  valid_amount = true
                }
              }
              println("What would you like to do? D to deposit, W to withdraw, V to view balance,E to exit, or DELETE to delete your account.")
            }
            else if (temp == "W") {
              println("Withdrawing ... done!")
              println("What would you like to do? D to deposit, W to withdraw, V to view balance,E to exit, or DELETE to delete your account.")
            }
            else if (temp == "V") {
              val funds_statement = connection.createStatement()
              val user_funds = funds_statement.executeQuery(s"SELECT funds FROM users WHERE users.username=\'$temp_username\' and users.password=\'$temp_password\';")
              while ( user_funds.next() ) {
                println("You account balance is: " + user_funds.getString(1))
              }
              println("What would you like to do? D to deposit, W to withdraw, V to view balance,E to exit, or DELETE to delete your account.")
            }
            else if (temp == "E") {
              println("logging out");
              exiting = true;
            }
            else if (temp == "DELETE") {
              println("Are you sure you want to delete your account? Deleting your account will delete all information associated with this account. Enter Yes or No")
              var deleting = false
              while (!deleting) {
                val delete_input = readLine()
                if (delete_input == "Yes") {
                  exiting = true
                  deleting = true
                  var delete_id = 0;
                  val delete_id_query = connection.createStatement()
                  val delete_id_result = delete_id_query.executeQuery(s"SELECT user_id FROM users WHERE users.username=\'$temp_username\' and users.password=\'$temp_password\';")
                  while ( delete_id_result.next() ) {
                    //println("You account balance is: " + user_funds.getString(1))
                    val temp = delete_id_result.getString(1)
                    delete_id = temp.toInt
                    println("Deleting account with id: " + delete_id)
                  }
                  val delete_statement = connection.createStatement()
                  val delete_result = delete_statement.executeUpdate(s"DELETE FROM users WHERE users.username=\'$temp_username\' and users.password=\'$temp_password\' and user_id=$delete_id;")
                  println("Done")
                }
                else if (delete_input == "No") {
                  deleting = true
                  println("What would you like to do? D to deposit, W to withdraw, V to view balance,E to exit, or DELETE to delete your account.")
                }
              }
            }
          }
        }
      }
      connection.close()
    }

    /*
    try {
      // make the connection
      Class.forName(driver)
      connection = DriverManager.getConnection(url, username, password)

      // create the statement, and run the select query
      val statement = connection.createStatement()

      /*val resultSet = statement.executeQuery("SELECT * FROM users;")
      while ( resultSet.next() ) {
        println(resultSet.getString(1)+", " +resultSet.getString(2) +", " +resultSet.getString(3))
      }*/

      val resultSet = statement.executeUpdate("INSERT INTO users VALUES (1,'start','20-2-22')")

    } catch {
      case e: Throwable => e.printStackTrace
    }
    */
    //connection.close()
  }
}

//source:
//https://alvinalexander.com/scala/scala-jdbc-connection-mysql-sql-select-example/