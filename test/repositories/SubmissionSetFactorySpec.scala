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

import base.SpecBase
import models.RegistrationSubmission.AnswerSection
import models.Status.{Completed, InProgress}
import models.UserAnswers
import pages.entitystatus.OtherIndividualStatus
import pages.register.TrustHasOtherIndividualYesNoPage

import scala.collection.immutable.Nil

class SubmissionSetFactorySpec extends SpecBase {

  "Submission set factory" must {

    val factory = injector.instanceOf[SubmissionSetFactory]

    "return no answer sections if no completed otherIndividuals" in {

      factory.answerSectionsIfCompleted(emptyUserAnswers, Some(InProgress))
        .mustBe(Nil)
    }

    "return completed answer sections" when {

      "trust has otherIndividuals is set to 'false'" must {
          "return an empty list" in {
            val userAnswers: UserAnswers = emptyUserAnswers
              .set(TrustHasOtherIndividualYesNoPage, false).success.value

            factory.answerSectionsIfCompleted(userAnswers, Some(Completed)) mustBe
              List.empty
          }
      }

      "only one otherIndividual" must {
        "have 'OtherIndividuals' as section key" when {

          "individual otherIndividual only" in {
            val userAnswers: UserAnswers = emptyUserAnswers
              .set(OtherIndividualStatus(0), Completed).success.value
              .set(TrustHasOtherIndividualYesNoPage, true).success.value

            factory.answerSectionsIfCompleted(userAnswers, Some(Completed)) mustBe
              List(
                AnswerSection(
                  Some("Individual otherIndividual 1"),
                  Nil,
                  Some("OtherIndividuals")
                )
              )
          }
        }
      }

      "more than one OtherIndividual" must {
        "have 'OtherIndividuals' as section key of the topmost section" when {

          "Individual otherIndividuals" in {
            val userAnswers: UserAnswers = emptyUserAnswers
              .set(OtherIndividualStatus(0), Completed).success.value
              .set(OtherIndividualStatus(1), Completed).success.value
              .set(TrustHasOtherIndividualYesNoPage, true).success.value

            factory.answerSectionsIfCompleted(userAnswers, Some(Completed)) mustBe
              List(
                AnswerSection(
                  Some("Individual otherIndividual 1"),
                  Nil,
                  Some("OtherIndividuals")
                ),
                AnswerSection(
                  Some("Individual otherIndividual 2"),
                  Nil,
                  None
                )
              )
          }

        }
      }

    }
  }

}
