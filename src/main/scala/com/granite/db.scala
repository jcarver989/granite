package com.granite

import org.scalajs.dom.raw.Storage
import org.scalajs.dom.window
import upickle.default.{ Reader, Writer, read, write }

/** Represents a data store, ex localStorage */
abstract class Db[T] {
  def save(key: String, data: T): Unit
  def load(key: String): Option[T]
}

/** A data-store for client side local storage */
class LocalStorage[T]()(implicit w: Writer[T], r: Reader[T]) extends Db[T] {
  private val store: Storage = window.localStorage
  override def save(key: String, data: T): Unit = {
    store.setItem(key, write(data))
  }

  override def load(key: String): Option[T] = {
    Option(store.getItem(key)).map(read[T])
  }
}