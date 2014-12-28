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

package com.github.martincooper.datatable.DataSort

import com.github.martincooper.datatable.{ DataView, DataTable }

import scala.util.Try

object DataSort {

  /** Performs a quick sort of the DataTable, returning a sorted DataView. */
  def quickSort(table: DataTable, sortItems: Iterable[SortItem]): Try[DataView] = {
    DataView(DataTable("Not Yet Implemented").get)
  }

  /** Performs a quick sort of a DataView, returning a sorted DataView. */
  def quickSort(dataView: DataView, sortItems: Iterable[SortItem]): Try[DataView] = {
    DataView(DataTable("Not Yet Implemented").get)
  }
}
