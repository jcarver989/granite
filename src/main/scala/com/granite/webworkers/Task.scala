package com.granite.webworkers

import com.granite.AppEvent

trait Task extends AppEvent {
  val id: String
}