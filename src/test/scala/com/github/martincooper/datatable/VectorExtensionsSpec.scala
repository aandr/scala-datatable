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

import org.scalatest.{ Matchers, FlatSpec }

class VectorExtensionsSpec extends FlatSpec with Matchers {

  "Add item" should "correctly append an item to the end of a new collection" in {
    val testVector = Seq().toVector

    val modified = VectorExtensions.addItem(testVector, 9)

    modified.isSuccess should be(true)
    modified.get.length should be(1)
    modified.get should be(Seq(9).toVector)
  }

  "Add item" should "correctly append an item to the end of existing collection" in {
    val testVector = Seq(1, 2, 3, 4).toVector

    val modified = VectorExtensions.addItem(testVector, 9)

    modified.isSuccess should be(true)
    modified.get.length should be(5)
    modified.get should be(Seq(1, 2, 3, 4, 9).toVector)
  }

  "Remove item" should "correctly remove an item a collection" in {
    val testVector = Seq(1, 2, 3, 4, 5).toVector

    val modified = VectorExtensions.removeItem(testVector, 2)

    modified.isSuccess should be(true)
    modified.get.length should be(4)
    modified.get should be(Seq(1, 2, 4, 5).toVector)
  }

  "Remove item" should "correctly fail if invalid index passed" in {
    val testVector = Seq(1, 2, 3, 4, 5).toVector

    val modified = VectorExtensions.removeItem(testVector, 9)

    modified.isSuccess should be(false)
    modified.failed.get.getMessage should be("Item index out of bounds for remove.")
  }

  "Replace item" should "correctly replace an item a collection" in {
    val testVector = Seq(1, 2, 3, 4, 5).toVector

    val modified = VectorExtensions.replaceItem(testVector, 2, 9)

    modified.isSuccess should be(true)
    modified.get.length should be(5)
    modified.get should be(Seq(1, 2, 9, 4, 5).toVector)
  }

  "Replace item" should "correctly fail if invalid index passed" in {
    val testVector = Seq(1, 2, 3, 4, 5).toVector

    val modified = VectorExtensions.replaceItem(testVector, 9, 9)

    modified.isSuccess should be(false)
    modified.failed.get.getMessage should be("Item index out of bounds for replace.")
  }

  "Insert item" should "correctly insert an item into a collection" in {
    val testVector = Seq(1, 2, 3, 4, 5).toVector

    val modified = VectorExtensions.insertItem(testVector, 2, 9)

    modified.isSuccess should be(true)
    modified.get.length should be(6)
    modified.get should be(Seq(1, 2, 9, 3, 4, 5).toVector)
  }

  "Insert item" should "correctly fail if invalid index passed" in {
    val testVector = Seq(1, 2, 3, 4, 5).toVector

    val modified = VectorExtensions.insertItem(testVector, 9, 9)

    modified.isSuccess should be(false)
    modified.failed.get.getMessage should be("Item index out of bounds for insert.")
  }

  "Check Bounds" should "correctly pass if index in bounds." in {

    val validSeq = Seq(1, 2, 3, 4, 5).toVector

    VectorExtensions.outOfBounds(validSeq, 0) should be(false)
    VectorExtensions.outOfBounds(validSeq, 2) should be(false)
    VectorExtensions.outOfBounds(validSeq, 4) should be(false)
  }

  "Check Bounds" should "correctly fail if index is out of bounds." in {

    val validSeq = Seq(1, 2, 3, 4, 5).toVector

    VectorExtensions.outOfBounds(validSeq, -5) should be(true)
    VectorExtensions.outOfBounds(validSeq, -1) should be(true)
    VectorExtensions.outOfBounds(validSeq, 5) should be(true)
  }

  "Check Bounds" should "correctly fail if collection is empty" in {

    val emptySeq = Seq().toVector

    VectorExtensions.outOfBounds(emptySeq, -10) should be(true)
    VectorExtensions.outOfBounds(emptySeq, -1) should be(true)
    VectorExtensions.outOfBounds(emptySeq, -0) should be(true)
    VectorExtensions.outOfBounds(emptySeq, 1) should be(true)
    VectorExtensions.outOfBounds(emptySeq, 10) should be(true)
  }

}