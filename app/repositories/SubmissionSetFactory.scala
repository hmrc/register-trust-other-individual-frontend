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

package repositories

import javax.inject.Inject
import mapping.register.OtherIndividualMapper
import models._
import pages.register.TrustHasOtherIndividualYesNoPage
import play.api.i18n.Messages
import play.api.libs.json.Json
import utils.RegistrationProgress
import utils.answers.OtherIndividualAnswersHelper
import viewmodels.{AnswerRow, AnswerSection}

class SubmissionSetFactory @Inject()(registrationProgress: RegistrationProgress,
                                     otherIndividualMapper: OtherIndividualMapper,
                                     otherIndividualAnswersHelper: OtherIndividualAnswersHelper) {

  def createFrom(userAnswers: UserAnswers)(implicit messages: Messages): RegistrationSubmission.DataSet = {
    val status = registrationProgress.otherIndividualsStatus(userAnswers)
    answerSectionsIfCompleted(userAnswers, status)

    RegistrationSubmission.DataSet(
      Json.toJson(userAnswers),
      status,
      mappedDataIfCompleted(userAnswers, status),
      answerSectionsIfCompleted(userAnswers, status)
    )
  }

  private def mappedDataIfCompleted(userAnswers: UserAnswers, status: Option[Status]) = {
    if (status.contains(Status.Completed)) {
      otherIndividualMapper.build(userAnswers) match {
        case Some(assets) => List(RegistrationSubmission.MappedPiece("trust/entities/naturalPerson", Json.toJson(assets)))
        case _ => List.empty
      }
    } else {
      List.empty
    }
  }

  def answerSectionsIfCompleted(userAnswers: UserAnswers, status: Option[Status])
                               (implicit messages: Messages): List[RegistrationSubmission.AnswerSection] = {

    val trustHasOtherIndividualYesNo = userAnswers.get(TrustHasOtherIndividualYesNoPage) match {
      case Some(true) => true
      case _ => false
    }

    if (status.contains(Status.Completed) && trustHasOtherIndividualYesNo) {

      val entitySections = List(
        otherIndividualAnswersHelper.otherIndividuals(userAnswers, canEdit = false)
      ).flatten.flatten

      val updatedFirstSection = AnswerSection(
        entitySections.head.headingKey,
        entitySections.head.rows,
        Some(Messages("answerPage.section.otherIndividuals.heading"))
      )

      val updatedSections = updatedFirstSection :: entitySections.tail

      updatedSections.map(convertForSubmission)

    } else {
      List.empty
    }
  }

  private def convertForSubmission(row: AnswerRow): RegistrationSubmission.AnswerRow = {
    RegistrationSubmission.AnswerRow(row.label, row.answer.toString, row.labelArg)
  }

  private def convertForSubmission(section: AnswerSection): RegistrationSubmission.AnswerSection = {
    RegistrationSubmission.AnswerSection(section.headingKey, section.rows.map(convertForSubmission), section.sectionKey)
  }
}
