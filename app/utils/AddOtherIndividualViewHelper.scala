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

package utils

import controllers.register.individual.{routes => individualRts}
import models.UserAnswers
import play.api.i18n.Messages
import sections.OtherIndividuals
import viewmodels.addAnother.OtherIndividualViewModel
import viewmodels.{AddRow, AddToRows}

class AddOtherIndividualViewHelper(userAnswers: UserAnswers, draftId : String)(implicit messages: Messages) {

  private case class InProgressComplete(inProgress : List[AddRow], complete: List[AddRow])

  private def parseName(name : Option[String]) : String = {
    val defaultValue = messages("entities.no.name.added")
    name.getOrElse(defaultValue)
  }

  private def parseOtherIndividual(otherIndividual : (OtherIndividualViewModel, Int)) : AddRow = {

    val vm = otherIndividual._1
    val index = otherIndividual._2

    AddRow(
      name = parseName(vm.name.map(_.toString)),
      typeLabel = messages("entities.otherIndividual"),
      changeUrl = if (vm.isComplete) {
        individualRts.CheckDetailsController.onPageLoad(index, draftId).url
      } else {
        individualRts.NameController.onPageLoad(index, draftId).url
      },
      removeUrl = individualRts.RemoveOtherIndividualController.onPageLoad(index, draftId).url
    )
  }

  private def otherIndividuals: InProgressComplete = {
    val otherIndividuals = userAnswers.get(OtherIndividuals).toList.flatten.zipWithIndex
    val otherIndividualsComplete = otherIndividuals.filter(_._1.isComplete).map(parseOtherIndividual)
    val otherIndividualsInProgress = otherIndividuals.filterNot(_._1.isComplete).map(parseOtherIndividual)

    InProgressComplete(inProgress = otherIndividualsInProgress, complete = otherIndividualsComplete)
  }

  def rows : AddToRows =
    AddToRows(
      inProgress = otherIndividuals.inProgress,
      complete = otherIndividuals.complete
    )

}
