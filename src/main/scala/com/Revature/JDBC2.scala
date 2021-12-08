package com.Revature

import java.sql.DriverManager
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException

object JDBC2 {
  def main(args: Array[String]) {
    // connect to the database named "mysql" on the localhost
    //val driver = "com.mysql.jdbc.Driver"
    val driver = "com.mysql.cj.jdbc.Driver"
    val url = "jdbc:mysql://localhost:3306/test"
    val username = "root"
    val password = "p4ssword"

    // there's probably a better way to do this
    var connection:Connection = null

    try {
      // make the connection
      Class.forName(driver)
      connection = DriverManager.getConnection(url, username, password)

      // create the statement, and run the select query
      val statement = connection.createStatement()
      val resultSet = statement.executeQuery("SELECT * FROM users;")
      while ( resultSet.next() ) {
        println(resultSet.getString(1)+", " +resultSet.getString(2) +", " +resultSet.getString(3))
      }
    } catch {
      case e: Throwable => e.printStackTrace
    }
    connection.close()
  }
}

//source:
//https://alvinalexander.com/scala/scala-jdbc-connection-mysql-sql-select-example/