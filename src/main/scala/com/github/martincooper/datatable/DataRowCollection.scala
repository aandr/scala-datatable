/**
 * Copyright 2014-2015 Martin Cooper
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

package com.github.martincooper.datatable

import scala.collection.{ mutable, IndexedSeqLike }
import scala.util.{ Success, Failure, Try }

/** Stores a column and the value to put in it. */
case class ColumnValuePair(column: GenericColumn, value: DataValue)

/** Implements a collection of DataRows with additional immutable modification methods implemented. */
class DataRowCollection(dataTable: DataTable)
    extends IndexedSeq[DataRow]
    with IndexedSeqLike[DataRow, DataRowCollection] {

  val table = dataTable

  override def apply(columnIndex: Int): DataRow = table(columnIndex)

  override def length: Int = dataTable.rowCount

  override def newBuilder: mutable.Builder[DataRow, DataRowCollection] =
    DataRowCollection.newBuilder(table)

  /** Returns a new table with the additional row data appended. */
  def add(rowValues: DataValue*): Try[DataTable] = {
    add(rowValues)
  }

  /** Returns a new table with the additional row data appended. */
  def add(rowValues: Iterable[DataValue]): Try[DataTable] = {
    mapValuesToColumns(rowValues.toIndexedSeq) match {
      case Success(colMap) => addValues(colMap)
      case Failure(ex) => Failure(ex)
    }
  }

  /** Returns a new table with the row data inserted at the specified location. */
  def insert(rowIndex: Int, rowValues: DataValue*): Try[DataTable] = {
    insert(rowIndex, rowValues)
  }

  /** Returns a new table with the row data inserted at the specified location. */
  def insert(rowIndex: Int, rowValues: Iterable[DataValue]): Try[DataTable] = {
    mapValuesToColumns(rowValues.toIndexedSeq) match {
      case Success(colMap) => insertValues(rowIndex, colMap)
      case Failure(ex) => Failure(ex)
    }
  }

  /** Returns a new table with the values at the specified index replaced with the new values. */
  def replace(rowIndex: Int, rowValues: DataValue*): Try[DataTable] = {
    replace(rowIndex, rowValues)
  }

  /** Returns a new table with the values at the specified index replaced with the new values. */
  def replace(rowIndex: Int, rowValues: Iterable[DataValue]): Try[DataTable] = {
    Failure(DataTableException("Not Implemented."))
  }

  /** Creates a new table with the row removed. */
  def remove(rowToRemove: DataRow): Try[DataTable] = {
    indexFromRow(rowToRemove).map(remove).flatten
  }

  /** Returns a new table with the row removed. */
  def remove(rowIndex: Int): Try[DataTable] = {
    removeRow(rowIndex)
  }

  private def insertValues(rowIndex: Int, columnValues: IndexedSeq[ColumnValuePair]): Try[DataTable] = {
    val newCols = allOrFirstFail(columnValues.map(item => item.column.insert(rowIndex, item.value)))
    buildTable(newCols)
  }

  private def addValues(columnValues: IndexedSeq[ColumnValuePair]): Try[DataTable] = {
    val newCols = allOrFirstFail(columnValues.map(item => item.column.add(item.value)))
    buildTable(newCols)
  }

  /** Builds a new DataTable from the columns provided if successful. */
  private def buildTable(columns: Try[Seq[GenericColumn]]): Try[DataTable] = {
    columns match {
      case Success(cols) => DataTable(table.name, cols)
      case Failure(ex) => Failure(ex)
    }
  }

  /** Maps each value to the column it should go in. With only values,  */
  private def mapValuesToColumns(values: IndexedSeq[DataValue]): Try[IndexedSeq[ColumnValuePair]] = {
    values.length == table.columns.length match {
      case false => Failure(DataTableException("Number of values does not match number of columns."))
      case _ => Success(createIndexedColumnValuePair(values))
    }
  }

  /** Creates a collection of column to value pairs by column index. */
  private def createIndexedColumnValuePair(values: IndexedSeq[DataValue]): IndexedSeq[ColumnValuePair] = {
    values.zipWithIndex.map {
      case (value, index) => ColumnValuePair(table.columns(index), value)
    }
  }

  /** Removes the item from each column and builds a new DataTable. */
  private def removeRow(rowIndex: Int): Try[DataTable] = {
    val newCols = allOrFirstFail(table.columns.map(col => col.remove(rowIndex)))
    buildTable(newCols)
  }

  /** Gets the row index from the DataRow, ensuring it belongs to the correct table. */
  private def indexFromRow(dataRow: DataRow): Try[Int] = {
    dataRow.table eq table match {
      case true => Success(dataRow.rowIndex)
      case _ => Failure(DataTableException("DataRow specified does not belong to this table."))
    }
  }

  /** Returns all the values, or the first failure (if any). */
  private def allOrFirstFail[A](trySeq: Seq[Try[A]]) =
    Try(trySeq.map(_.get))
}

object DataRowCollection {

  /** Builder for a new DataRowCollection. */
  def newBuilder(dataTable: DataTable): mutable.Builder[DataRow, DataRowCollection] =
    Vector.newBuilder[DataRow] mapResult (vector => new DataRowCollection(dataTable))

  /** Builds a DataRowCollection. */
  def apply(dataTable: DataTable): DataRowCollection = {
    new DataRowCollection(dataTable)
  }
}