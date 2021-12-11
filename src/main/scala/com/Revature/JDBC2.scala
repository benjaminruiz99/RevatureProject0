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
    var exiting:Boolean = false; //boolean to keep track of whether the user is quitting this session
    while (!exiting) { //while the exit command hasn't been issued
      println("Would you like to login or signup? Enter L for login or S for signup.")
      while (!loggedin && !exiting) { //read input until user attempts to login or exit the session
        val temp: String = readLine();
        if (temp == "L") { //if user enters l ("el"), proceed to login process
          loggedin = true;
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
          Class.forName(driver)
          connection = DriverManager.getConnection(url, username, password)

          // create the statement, and run the select query
          val statement = connection.createStatement()

          val resultSet = statement.executeQuery(s"SELECT * FROM users HAVING users.username=\'$temp_username\' and users.password=\'$temp_password\';")
          if (!resultSet.next()) {
            loggedin = false;
            println("You don't have an account here");
          }
          else {
            println("the user exists");
          }
        } catch {
          case e: Throwable => e.printStackTrace
        }
        if (loggedin) {
          println("What would you like to do? D to deposit, W to withdraw, V to view balance,E to exit")
          while (!exiting) {
            val temp: String = readLine();
            if (temp == "D") {
              println("Depositing ... done!")
              println("What would you like to do? D to deposit, W to withdraw, V to view balance,E to exit")
            }
            else if (temp == "W") {
              println("Withdrawing ... done!")
              println("What would you like to do? D to deposit, W to withdraw, V to view balance,E to exit")
            }
            else if (temp == "V") {
              println("You have $100000000!")
              println("What would you like to do? D to deposit, W to withdraw, V to view balance,E to exit")
            }
            else if (temp == "E") {
              println("logging out");
              exiting = true;
            }
          }
        }
      }
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
    connection.close()
  }
}

//source:
//https://alvinalexander.com/scala/scala-jdbc-connection-mysql-sql-select-example/