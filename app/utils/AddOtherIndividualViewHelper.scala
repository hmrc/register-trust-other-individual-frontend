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

package utils

import controllers.register.individual.{routes => individualRts}
import models.UserAnswers
import play.api.i18n.Messages
import sections.IndividualOtherIndividuals
import viewmodels.addAnother.IndividualOtherIndividualViewModel
import viewmodels.{AddRow, AddToRows}

class AddOtherIndividualViewHelper(userAnswers: UserAnswers, draftId : String)(implicit messages: Messages) {

  private case class InProgressComplete(inProgress : List[AddRow], complete: List[AddRow])

  private def parseName(name : Option[String]) : String = {
    val defaultValue = messages("entities.no.name.added")
    name.getOrElse(defaultValue)
  }

  private def parseIndividualOtherIndividual(individualOtherIndividual : (IndividualOtherIndividualViewModel, Int)) : AddRow = {

    val vm = individualOtherIndividual._1
    val index = individualOtherIndividual._2

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

  private def individualOtherIndividuals = {
    val individualOtherIndividuals = userAnswers.get(IndividualOtherIndividuals).toList.flatten.zipWithIndex
    val individualOtherIndividualsComplete = individualOtherIndividuals.filter(_._1.isComplete).map(parseIndividualOtherIndividual)
    val individualOtherIndividualsInProgress = individualOtherIndividuals.filterNot(_._1.isComplete).map(parseIndividualOtherIndividual)

    InProgressComplete(inProgress = individualOtherIndividualsInProgress, complete = individualOtherIndividualsComplete)
  }
  
  def rows : AddToRows =
    AddToRows(
      inProgress = individualOtherIndividuals.inProgress,
      complete = individualOtherIndividuals.complete
    )

}
