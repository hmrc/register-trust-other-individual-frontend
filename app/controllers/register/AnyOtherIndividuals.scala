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

package controllers.register

import models.{OtherIndividuals, ReadableUserAnswers}
import sections.{OtherIndividualsView => OtherIndividualsSection}

trait AnyOtherIndividuals {

  def otherIndividuals(userAnswers: ReadableUserAnswers): OtherIndividuals = OtherIndividuals(userAnswers.get(OtherIndividualsSection).getOrElse(List.empty))

  def isAnyOtherIndividualAdded(userAnswers: ReadableUserAnswers): Boolean = {
    val otherIndividualLists: OtherIndividuals = otherIndividuals(userAnswers)

    otherIndividualLists.individuals.nonEmpty

  }
}