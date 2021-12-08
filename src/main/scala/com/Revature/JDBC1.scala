package com.Revature

import java.sql.DriverManager
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException

object JDBC1 {

  def main(args: Array[String]) {
    // connect to the database named "test" on the localhost

    val driver = "com.mysql.cj.jdbc.Driver"
    val url = "jdbc:mysql://localhost:3306/test"
    val username = "root"
    val password = "Passport1999"

    var connection:Connection = DriverManager.getConnection(url, username, password)
    val statement = connection.createStatement()
    val resultSet = statement.executeQuery("SELECT * FROM users;")

    while ( resultSet.next() ) {
      println(resultSet.getString(1)+", " +resultSet.getString(2) +", " +resultSet.getString(3))
    }
    connection.close()
  }

}