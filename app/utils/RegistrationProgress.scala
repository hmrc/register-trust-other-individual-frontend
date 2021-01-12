/*
 * Copyright 2021 HM Revenue & Customs
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

import controllers.register.AnyOtherIndividuals
import models.register.pages.AddOtherIndividual
import models.{ReadableUserAnswers, Status}
import pages.QuestionPage
import pages.register.{AddOtherIndividualPage, TrustHasOtherIndividualYesNoPage}
import play.api.libs.json.Reads
import sections.OtherIndividualsView
import viewmodels.addAnother._

class RegistrationProgress extends AnyOtherIndividuals {

  def otherIndividualsStatus(userAnswers: ReadableUserAnswers): Option[Status] = {

    if (!isAnyOtherIndividualAdded(userAnswers)) {
      userAnswers.get(TrustHasOtherIndividualYesNoPage) match {
        case Some(true) => Some(Status.InProgress)
        case Some(false) => Some(Status.Completed)
        case _ => None
      }
    } else {

      val statusList: List[IsComplete] = List(
        AddingOtherIndividualsIsComplete,
        OtherIndividualsAreComplete
      )

      statusList match {
        case Nil => None
        case list =>

          val complete = list.forall(isComplete => isComplete(userAnswers))

          Some(if (complete) {
            Status.Completed
          } else {
            Status.InProgress
          })
      }
    }
  }

  sealed trait IsComplete {
    def apply(userAnswers: ReadableUserAnswers): Boolean
  }

  sealed class ListIsComplete[T <: OtherIndividualViewModel](section: QuestionPage[List[T]])
                                                      (implicit reads: Reads[T]) extends IsComplete {

    override def apply(userAnswers: ReadableUserAnswers): Boolean = {
      userAnswers.get(section) match {
        case Some(otherIndividuals) => !otherIndividuals.exists(_.status == Status.InProgress)
        case _ => true
      }
    }
  }

  private object AddingOtherIndividualsIsComplete extends IsComplete {
    override def apply(userAnswers: ReadableUserAnswers): Boolean =
      userAnswers.get(AddOtherIndividualPage).contains(AddOtherIndividual.NoComplete)
  }

  private object OtherIndividualsAreComplete extends ListIsComplete(OtherIndividualsView)
}
