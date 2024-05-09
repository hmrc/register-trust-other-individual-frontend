/*
 * Copyright 2024 HM Revenue & Customs
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
import viewmodels.addAnother.OtherIndividualViewModel

class OtherIndividualsSpec extends SpecBase {

  val name: FullName = FullName("First", None, "Last")

  val max: Int = 25

  val individual: OtherIndividualViewModel = OtherIndividualViewModel(Some(name), Completed)

  "otherIndividuals model" must {

    "determine isMaxedOut" when {

      "otherIndividuals maxed out" in {
        val otherIndividuals = OtherIndividuals(
          individuals = List.fill(max)(individual)
        )

        otherIndividuals.isMaxedOut mustEqual true
      }

      "otherIndividuals not maxed out" in {
        val otherIndividuals = OtherIndividuals(
          individuals = List.fill(max - 1)(individual)
        )

        otherIndividuals.isMaxedOut mustEqual false
      }
    }
  }
}
