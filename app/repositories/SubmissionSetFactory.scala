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

package repositories

import mapping.register.OtherIndividualMapper
import models._
import pages.register.TrustHasOtherIndividualYesNoPage
import play.api.i18n.Messages
import play.api.libs.json.{JsNull, JsValue, Json}
import utils.answers.OtherIndividualAnswersHelper
import viewmodels.{AnswerRow, AnswerSection}

import javax.inject.Inject

class SubmissionSetFactory @Inject()(otherIndividualMapper: OtherIndividualMapper,
                                     otherIndividualAnswersHelper: OtherIndividualAnswersHelper) {

  def createFrom(userAnswers: UserAnswers)(implicit messages: Messages): RegistrationSubmission.DataSet = {

    RegistrationSubmission.DataSet(
      data = Json.toJson(userAnswers),
      registrationPieces = mappedData(userAnswers),
      answerSections = answerSections(userAnswers))
  }

  private def mappedPieces(otherIndividualsJson: JsValue) =
    List(RegistrationSubmission.MappedPiece("trust/entities/naturalPerson", otherIndividualsJson))

  private def mappedData(userAnswers: UserAnswers): List[RegistrationSubmission.MappedPiece] = {
      otherIndividualMapper.build(userAnswers) match {
        case Some(otherIndividuals) => mappedPieces(Json.toJson(otherIndividuals))
        case _ => mappedPieces(JsNull)
      }
  }

  def answerSections(userAnswers: UserAnswers)
                    (implicit messages: Messages): List[RegistrationSubmission.AnswerSection] = {

    val trustHasOtherIndividualYesNo = userAnswers.get(TrustHasOtherIndividualYesNoPage).contains(true)

    if (trustHasOtherIndividualYesNo) {

      val entitySections = List(
        otherIndividualAnswersHelper.otherIndividuals(userAnswers)
      ).flatten.flatten

      val updatedFirstSection = entitySections.head.copy(sectionKey = Some("answerPage.section.otherIndividuals.heading"))

      val updatedSections = updatedFirstSection :: entitySections.tail

      updatedSections.map(convertForSubmission)

    } else {
      List.empty
    }
  }

  private def convertForSubmission(section: AnswerSection): RegistrationSubmission.AnswerSection = {
    RegistrationSubmission.AnswerSection(
      headingKey = section.headingKey,
      rows = section.rows.map(convertForSubmission),
      sectionKey = section.sectionKey,
      headingArgs = section.headingArgs.map(_.toString)
    )
  }

  private def convertForSubmission(row: AnswerRow): RegistrationSubmission.AnswerRow = {
    RegistrationSubmission.AnswerRow(
      label = row.label,
      answer = row.answer.toString,
      labelArg = row.labelArg
    )
  }

}
