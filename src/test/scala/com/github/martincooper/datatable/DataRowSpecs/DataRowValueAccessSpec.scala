/**
 * Copyright 2014 Martin Cooper
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.martincooper.datatable.DataRowSpecs

import com.github.martincooper.datatable.{ DataColumn, DataRow, DataTable }
import org.scalatest.{ FlatSpec, Matchers }

import scala.util.Success

class DataRowValueAccessSpec extends FlatSpec with Matchers {

  private def buildTestTable(): DataTable = {
    val dataColOne = new DataColumn[Int]("ColOne", (0 to 10) map { i => i })
    val dataColTwo = new DataColumn[String]("ColTwo", (0 to 10) map { i => "Value : " + i })
    val dataColThree = new DataColumn[Boolean]("ColThree", (0 to 10) map { i => true })

    DataTable("TestTable", Seq(dataColOne, dataColTwo, dataColThree)).get
  }

  "A DataRow" can "access an untyped and unchecked cell value by column index" in {
    val dataRow = DataRow(buildTestTable(), 5).get
    dataRow(1) should be("Value : 5")
  }

  "A DataRow" should "fail when accessing an untyped and unchecked cell value by invalid column index" in {
    val dataRow = DataRow(buildTestTable(), 5).get

    val result = intercept[NoSuchElementException] {
      dataRow(500)
    }

    result.getMessage should be("key not found: 500")
  }

  "A DataRow" can "access an untyped and unchecked cell value by column name" in {
    val dataRow = DataRow(buildTestTable(), 5).get
    dataRow("ColTwo") should be("Value : 5")
  }

  "A DataRow" should "fail when accessing an untyped and unchecked cell value by invalid column name" in {
    val dataRow = DataRow(buildTestTable(), 5).get

    val result = intercept[NoSuchElementException] {
      dataRow("InvalidColumnName")
    }

    result.getMessage should be("key not found: InvalidColumnName")
  }

  "A DataRow" can "access an untyped and checked cell value by column index" in {
    val dataRow = DataRow(buildTestTable(), 5).get
    dataRow.get(1) should be(Success("Value : 5"))
  }

  "A DataRow" should "fail when accessing an untyped and checked cell value by invalid column index" in {
    val dataRow = DataRow(buildTestTable(), 5).get

    val result = dataRow.get(500)

    result.isFailure should be(true)
    result.failed.get.getMessage should be("Specified column index not found.")
  }

  "A DataRow" can "access an untyped and checked cell value by column name" in {
    val dataRow = DataRow(buildTestTable(), 5).get
    dataRow.get("ColTwo") should be(Success("Value : 5"))
  }

  "A DataRow" should "fail when accessing an untyped and checked cell value by invalid column name" in {
    val dataRow = DataRow(buildTestTable(), 5).get

    val result = dataRow.get("InvalidColumnName")

    result.isFailure should be(true)
    result.failed.get.getMessage should be("Specified column name not found.")
  }

  "A DataRow" can "access a typed and unchecked cell value by column index" in {
    val dataRow = DataRow(buildTestTable(), 5).get
    dataRow.as[String](1) should be("Value : 5")
  }

  "A DataRow" should "fail when accessing a typed and unchecked cell value by invalid column index" in {
    val dataRow = DataRow(buildTestTable(), 5).get

    val result = intercept[NoSuchElementException] {
      dataRow.as[String](500)
    }

    result.getMessage should be("key not found: 500")
  }

  "A DataRow" should "fail when accessing a typed and unchecked cell value by column index and invalid type" in {
    val dataRow = DataRow(buildTestTable(), 5).get

    val result = intercept[ClassCastException] {
      dataRow.as[Int](1)
    }

    result.getMessage should be("java.lang.String cannot be cast to java.lang.Integer")
  }

  "A DataRow" can "access a typed and unchecked cell value by column name" in {
    val dataRow = DataRow(buildTestTable(), 5).get
    dataRow.as[String]("ColTwo") should be("Value : 5")
  }

  "A DataRow" should "fail when accessing a typed and unchecked cell value by invalid column name" in {
    val dataRow = DataRow(buildTestTable(), 5).get

    val result = intercept[NoSuchElementException] {
      dataRow.as[String]("InvalidColumnName")
    }

    result.getMessage should be("key not found: InvalidColumnName")
  }

  "A DataRow" should "fail when accessing a typed and unchecked cell value by column name and invalid type" in {
    val dataRow = DataRow(buildTestTable(), 5).get

    val result = intercept[ClassCastException] {
      dataRow.as[Int]("ColTwo")
    }

    result.getMessage should be("java.lang.String cannot be cast to java.lang.Integer")
  }

  "A DataRow" can "access a typed and checked cell value by column index" in {
    val dataRow = DataRow(buildTestTable(), 5).get
    dataRow.getAs[String](1) should be(Success("Value : 5"))
  }

  "A DataRow" should "fail when accessing a typed and checked cell value by invalid column index" in {
    val dataRow = DataRow(buildTestTable(), 5).get

    val result = dataRow.getAs[String](500)

    result.isFailure should be(true)
    result.failed.get.getMessage should be("Specified column index not found.")
  }

  "A DataRow" should "fail when accessing a typed and checked cell value by column index and invalid type" in {
    val dataRow = DataRow(buildTestTable(), 5).get

    val result = dataRow.getAs[Int](1)

    result.isFailure should be(true)
    result.failed.get.getMessage should be("Column type doesn't match type requested.")
  }

  "A DataRow" can "access a typed and checked cell value by column name" in {
    val dataRow = DataRow(buildTestTable(), 5).get
    dataRow.getAs[String]("ColTwo") should be(Success("Value : 5"))
  }

  "A DataRow" should "fail when accessing a typed and checked cell value by invalid column name" in {
    val dataRow = DataRow(buildTestTable(), 5).get

    val result = dataRow.getAs[String]("InvalidColumnName")

    result.isFailure should be(true)
    result.failed.get.getMessage should be("Specified column name not found.")
  }

  "A DataRow" should "fail when accessing a typed and checked cell value by column name and invalid type" in {
    val dataRow = DataRow(buildTestTable(), 5).get

    val result = dataRow.getAs[Int]("ColTwo")

    result.isFailure should be(true)
    result.failed.get.getMessage should be("Column type doesn't match type requested.")
  }
}
