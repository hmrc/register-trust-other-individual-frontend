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

import models.UserAnswers
import play.api.i18n.Messages
import viewmodels.{AddRow, AddToRows}
import viewmodels.addAnother.{BusinessProtectorViewModel, IndividualProtectorViewModel}
import controllers.register.business.{routes => businessRts}
import controllers.register.individual.{routes => individualRts}
import sections.{BusinessProtectors, IndividualProtectors}

class AddAProtectorViewHelper(userAnswers: UserAnswers, draftId : String)(implicit messages: Messages) {

  private case class InProgressComplete(inProgress : List[AddRow], complete: List[AddRow])

  private def parseName(name : Option[String]) : String = {
    val defaultValue = messages("entities.no.name.added")
    name.getOrElse(defaultValue)
  }

  private def parseBusinessProtector(businessProtector : (BusinessProtectorViewModel, Int)) : AddRow = {

    val vm = businessProtector._1
    val index = businessProtector._2

    AddRow(
      name = parseName(vm.name.map(_.toString)),
      typeLabel = messages("entities.protector.business"),
      changeUrl = if (vm.isComplete) {
        businessRts.CheckDetailsController.onPageLoad(index, draftId).url
      } else {
        businessRts.NameController.onPageLoad(index, draftId).url
      },
      removeUrl = businessRts.RemoveProtectorController.onPageLoad(index, draftId).url
    )
  }


  private def businessProtectors = {
    val businessProtectors = userAnswers.get(BusinessProtectors).toList.flatten.zipWithIndex
    val businessProtectorsComplete = businessProtectors.filter(_._1.isComplete).map(parseBusinessProtector)
    val businessProtectorsInProgress = businessProtectors.filterNot(_._1.isComplete).map(parseBusinessProtector)

    InProgressComplete(inProgress = businessProtectorsInProgress, complete = businessProtectorsComplete)
  }

  private def parseIndividualProtector(individualProtector : (IndividualProtectorViewModel, Int)) : AddRow = {

    val vm = individualProtector._1
    val index = individualProtector._2

    AddRow(
      name = parseName(vm.name.map(_.toString)),
      typeLabel = messages("entities.protector.individual"),
      changeUrl = if (vm.isComplete) {
        individualRts.CheckDetailsController.onPageLoad(index, draftId).url
      } else {
        individualRts.NameController.onPageLoad(index, draftId).url
      },
      removeUrl = individualRts.RemoveProtectorController.onPageLoad(index, draftId).url
    )
  }

  private def individualProtectors = {
    val individualProtectors = userAnswers.get(IndividualProtectors).toList.flatten.zipWithIndex
    val individualProtectorsComplete = individualProtectors.filter(_._1.isComplete).map(parseIndividualProtector)
    val individualProtectorsInProgress = individualProtectors.filterNot(_._1.isComplete).map(parseIndividualProtector)

    InProgressComplete(inProgress = individualProtectorsInProgress, complete = individualProtectorsComplete)
  }
  
  def rows : AddToRows =
    AddToRows(
      inProgress = businessProtectors.inProgress ++ individualProtectors.inProgress,
      complete = businessProtectors.complete ++ individualProtectors.complete
    )

}
