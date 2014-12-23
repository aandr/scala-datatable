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

package com.github.martincooper.datatable

import scala.util.{ Failure, Try, Success }

/** DataTable class. Handles the immutable storage of data in a Row / Column format. */
class DataTable private (tableName: String, dataColumns: Iterable[GenericColumn])
  extends IndexedSeq[DataRow] with ModifiableByName[GenericColumn, DataTable] {

  def name = tableName
  def columns = dataColumns.toVector

  /** Mappers, name to col and index to col. */
  private val columnNameMapper = columns.map(col => col.name -> col).toMap
  private val columnIndexMapper = columns.zipWithIndex.map { case (col, idx) => idx -> col }.toMap

  /** Gets column by index / name. */
  def col(columnIndex: Int): GenericColumn = columnIndexMapper(columnIndex)
  def col(columnName: String): GenericColumn = columnNameMapper(columnName)

  /** Gets column by index / name as Option in case it doesn't exist. */
  def getCol(columnIndex: Int): Option[GenericColumn] = columnIndexMapper.get(columnIndex)
  def getCol(columnName: String): Option[GenericColumn] = columnNameMapper.get(columnName)

  /** Gets typed column by index / name. */
  def colAs[T](columnIndex: Int): DataColumn[T] = columnIndexMapper(columnIndex).asInstanceOf[DataColumn[T]]
  def colAs[T](columnName: String): DataColumn[T] = columnNameMapper(columnName).asInstanceOf[DataColumn[T]]

  /** Gets typed column by index / name as Option in case it doesn't exist or invalid type. */
  def getColAs[T](columnIndex: Int): Option[DataColumn[T]] = toTypedCol(getCol(columnIndex))
  def getColAs[T](columnName: String): Option[DataColumn[T]] = toTypedCol(getCol(columnName))

  private def toTypedCol[T](column: Option[GenericColumn]): Option[DataColumn[T]] = {
    column match {
      case Some(col) => Try(col.asInstanceOf[DataColumn[T]]).toOption
      case _ => None
    }
  }

  def rowCount(): Int = {
    columns.length match {
      case 0 => 0
      case _ => columns.head.data.length
    }
  }

  override def length: Int = rowCount()

  override def apply(idx: Int): DataRow = new DataRow(this, idx)

  override def replace(columnName: String, value: GenericColumn): Try[DataTable] = {
    columns.indexWhere(_.name == columnName) match {
      case -1 => Failure(DataTableException("Column " + columnName + " not found."))
      case colIdx: Int => replace(colIdx, value)
    }
  }

  override def replace(index: Int, value: GenericColumn): Try[DataTable] = {
    val newCols = for {
      newColSet <- VectorExtensions.replaceItem(columns, index, value)
      result <- DataTable.validateDataColumns(newColSet)
    } yield newColSet

    newCols match {
      case Success(modifiedCols) => new Success[DataTable](new DataTable(name, modifiedCols))
      case Failure(ex) => Failure(DataTableException("Error replacing column at specified index.", ex))
    }
  }

  /** Creates a new table with the column inserted before the specified column. */
  override def insert(columnName: String, value: GenericColumn): Try[DataTable] = {
    columns.indexWhere(_.name == columnName) match {
      case -1 => Failure(DataTableException("Column " + columnName + " not found."))
      case colIdx: Int => insert(colIdx, value)
    }
  }

  /** Creates a new table with the column inserted at the specified index. */
  override def insert(index: Int, value: GenericColumn): Try[DataTable] = {
    val newCols = for {
      newColSet <- VectorExtensions.insertItem(columns, index, value)
      result <- DataTable.validateDataColumns(newColSet)
    } yield newColSet

    newCols match {
      case Success(modifiedCols) => new Success[DataTable](new DataTable(name, modifiedCols))
      case Failure(ex) => Failure(DataTableException("Error inserting column at specified index.", ex))
    }
  }

  /** Creates a new table with the column removed. */
  override def remove(columnName: String): Try[DataTable] = {
    columns.indexWhere(_.name == columnName) match {
      case -1 => Failure(DataTableException("Column " + columnName + " not found."))
      case colIdx: Int => remove(colIdx)
    }
  }

  /** Returns a new table with the column removed. */
  override def remove(columnIndex: Int): Try[DataTable]  = {
    VectorExtensions.removeItem(columns, columnIndex) match {
      case Success(modifiedCols) => new Success[DataTable](new DataTable(name, modifiedCols))
      case Failure(ex) => Failure(DataTableException("Error removing column at specified index.", ex))
    }
  }

  /** Returns a new table with the additional column. */
  override def add(newColumn: GenericColumn): Try[DataTable] = {
    val newColSet = columns :+ newColumn

    DataTable.validateDataColumns(newColSet) match {
      case Failure(ex) => new Failure(ex)
      case Success(_) => Success(new DataTable(name, newColSet))
    }
  }

  /** Outputs a more detailed toString implementation. */
  override def toString() = {
    val tableDetails = "DataTable:" + name + "[Rows:" + columns.head.data.length + "]"
    val colDetails = columns.map(col => "[" + col.toString + "]").mkString(" ")

    tableDetails + colDetails
  }
}

object DataTable {

  /** Builds an empty DataTable. */
  def apply(tableName: String): Try[DataTable] = {
    Success(new DataTable(tableName, Array().toIndexedSeq))
  }

  /** Validates columns and builds a new DataTable. */
  def apply(tableName: String, columns: Iterable[GenericColumn]): Try[DataTable] = {

    validateDataColumns(columns) match {
      case Failure(ex) => new Failure(ex)
      case Success(_) => Success(new DataTable(tableName, columns))
    }
  }

  def validateDataColumns(columns: Iterable[GenericColumn]): Try[Unit] = {
    val colSeq = columns.toSeq

    /** Check all columns have the same number of rows. */
    if (colSeq.groupBy(_.data.length).toSeq.length > 1)
      return Failure(DataTableException("Columns have uneven row count."))

    /** Check all columns have distinct column names. */
    if (colSeq.groupBy(_.name).toSeq.length != colSeq.length)
      return Failure(DataTableException("Columns contain duplicate names."))

    Success(Unit)
  }
}
