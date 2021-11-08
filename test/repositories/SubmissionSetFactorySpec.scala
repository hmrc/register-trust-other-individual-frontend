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

import base.SpecBase
import models.RegistrationSubmission.AnswerSection
import models.Status.Completed
import models.{RegistrationSubmission, UserAnswers}
import pages.entitystatus.OtherIndividualStatus
import pages.register.TrustHasOtherIndividualYesNoPage
import play.api.libs.json.{JsNull, Json}

import scala.collection.immutable.Nil

class SubmissionSetFactorySpec extends SpecBase {

  "Submission set factory" must {

    val factory = injector.instanceOf[SubmissionSetFactory]

    "return no answer sections if no otherIndividuals" in {

      factory.createFrom(emptyUserAnswers) mustBe RegistrationSubmission.DataSet(
        Json.toJson(emptyUserAnswers),
        List(RegistrationSubmission.MappedPiece("trust/entities/naturalPerson", JsNull)),
        List.empty
      )
    }

    "return completed answer sections" when {

      "trust has otherIndividuals is set to 'false'" must {
        "return an empty list" in {
          val userAnswers: UserAnswers = emptyUserAnswers
            .set(TrustHasOtherIndividualYesNoPage, false).success.value

          factory.createFrom(userAnswers) mustBe RegistrationSubmission.DataSet(
            Json.toJson(userAnswers),
            List(RegistrationSubmission.MappedPiece("trust/entities/naturalPerson", JsNull)),
            List.empty
          )
        }
      }

      "only one otherIndividual" must {
        "have 'OtherIndividuals' as section key" when {

          "other Individual only" in {
            val userAnswers: UserAnswers = emptyUserAnswers
              .set(OtherIndividualStatus(0), Completed).success.value
              .set(TrustHasOtherIndividualYesNoPage, true).success.value

            factory.answerSections(userAnswers) mustBe
              List(
                AnswerSection(
                  headingKey = Some("answerPage.section.otherIndividual.subheading"),
                  rows = Nil,
                  sectionKey = Some("answerPage.section.otherIndividuals.heading"),
                  headingArgs = Seq("1")
                )
              )
          }
        }
      }

      "more than one OtherIndividual" must {
        "have 'OtherIndividuals' as section key of the topmost section" when {

          "Other Individuals" in {
            val userAnswers: UserAnswers = emptyUserAnswers
              .set(OtherIndividualStatus(0), Completed).success.value
              .set(OtherIndividualStatus(1), Completed).success.value
              .set(TrustHasOtherIndividualYesNoPage, true).success.value

            factory.answerSections(userAnswers) mustBe
              List(
                AnswerSection(
                  headingKey = Some("answerPage.section.otherIndividual.subheading"),
                  rows = Nil,
                  sectionKey = Some("answerPage.section.otherIndividuals.heading"),
                  headingArgs = Seq("1")
                ),
                AnswerSection(
                  headingKey = Some("answerPage.section.otherIndividual.subheading"),
                  rows = Nil,
                  sectionKey = None,
                  headingArgs = Seq("2")
                )
              )
          }
        }
      }
    }
  }
}
