package com.granite.webworkers

import com.granite.AppEvent

trait WebWorkerTask extends AppEvent {
  val id: String
}