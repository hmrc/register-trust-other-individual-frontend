/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models

import base.SpecBase
import models.Status._
import models.register.pages.IndividualOrBusinessToAdd
import viewmodels.addAnother.BusinessProtectorViewModel

class ProtectorsSpec extends SpecBase {

  val name: String = "Name"

  val max: Int = 25

  val business: BusinessProtectorViewModel = BusinessProtectorViewModel(Some(name), Completed)

  val prefix: String = IndividualOrBusinessToAdd.prefix

  "Protectors model" must {

    "determine maxed-out options" when {

      "protectors maxed out" in {
        val protectors = Protectors(
          businesses = List.fill(max)(business)
        )

        protectors.maxedOutOptions.size mustEqual (1)
      }

      "protectors not maxed out" in {
        val protectors = Protectors(
          businesses = List.fill(max - 1)(business)
        )

        protectors.maxedOutOptions.size mustEqual (0)
      }
    }
  }
}
