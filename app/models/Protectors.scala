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

import models.register.pages.IndividualOrBusinessToAdd
import viewmodels.RadioOption
import viewmodels.addAnother.{BusinessProtectorViewModel, IndividualProtectorViewModel}

case class Protectors(individuals: List[IndividualProtectorViewModel] = Nil,
                      businesses: List[BusinessProtectorViewModel] = Nil
                      ) {

  type ProtectorOption = (Int, IndividualOrBusinessToAdd)
  type ProtectorOptions = List[ProtectorOption]

  private val options: ProtectorOptions = {
    (individuals.size, IndividualOrBusinessToAdd.Individual) ::
    (businesses.size, IndividualOrBusinessToAdd.Business) ::
      Nil
  }

  val maxedOutOptions: List[RadioOption] = {

    options.filter(x => x._1 >= 25).map {
      x => RadioOption(IndividualOrBusinessToAdd.prefix, x._2.toString)
    }
  }

}
