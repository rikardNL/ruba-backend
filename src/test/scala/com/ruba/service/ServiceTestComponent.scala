package com.ruba.service

import org.scalatest.{BeforeAndAfterEach, Matchers, fixture}

/**
  * Created by rikard on 2016-02-15.
  */
trait ServiceTestComponent extends fixture.FlatSpec
  with BeforeAndAfterEach
  with Matchers
