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
import pages.entitystatus.{BusinessProtectorStatus, IndividualProtectorStatus}
import pages.register.TrustHasProtectorYesNoPage

import scala.collection.immutable.Nil

class SubmissionSetFactorySpec extends SpecBase {

  "Submission set factory" must {

    val factory = injector.instanceOf[SubmissionSetFactory]

    "return no answer sections if no completed protectors" in {

      factory.answerSectionsIfCompleted(emptyUserAnswers, Some(InProgress))
        .mustBe(Nil)
    }

    "return completed answer sections" when {

      "trust has protectors is set to 'false'" must {
          "return an empty list" in {
            val userAnswers: UserAnswers = emptyUserAnswers
              .set(TrustHasProtectorYesNoPage, false).success.value

            factory.answerSectionsIfCompleted(userAnswers, Some(Completed)) mustBe
              List.empty
          }
      }

      "only one protector" must {
        "have 'Protectors' as section key" when {
          "business protector only" in {
            val userAnswers: UserAnswers = emptyUserAnswers
              .set(BusinessProtectorStatus(0), Completed).success.value
              .set(TrustHasProtectorYesNoPage, true).success.value

            factory.answerSectionsIfCompleted(userAnswers, Some(Completed)) mustBe
              List(
                AnswerSection(
                  Some("Business protector 1"),
                  Nil,
                  Some("Protectors")
                )
              )
          }

          "individual protector only" in {
            val userAnswers: UserAnswers = emptyUserAnswers
              .set(IndividualProtectorStatus(0), Completed).success.value
              .set(TrustHasProtectorYesNoPage, true).success.value

            factory.answerSectionsIfCompleted(userAnswers, Some(Completed)) mustBe
              List(
                AnswerSection(
                  Some("Individual protector 1"),
                  Nil,
                  Some("Protectors")
                )
              )
          }
        }
      }

      "more than one Protector" must {
        "have 'Protectors' as section key of the topmost section" when {
          "Business protectors" in {
            val userAnswers: UserAnswers = emptyUserAnswers
              .set(BusinessProtectorStatus(0), Completed).success.value
              .set(BusinessProtectorStatus(1), Completed).success.value
              .set(TrustHasProtectorYesNoPage, true).success.value

            factory.answerSectionsIfCompleted(userAnswers, Some(Completed)) mustBe
              List(
                AnswerSection(
                  Some("Business protector 1"),
                  Nil,
                  Some("Protectors")
                ),
                AnswerSection(
                  Some("Business protector 2"),
                  Nil,
                  None
                )
              )
          }

          "Individual protectors" in {
            val userAnswers: UserAnswers = emptyUserAnswers
              .set(IndividualProtectorStatus(0), Completed).success.value
              .set(IndividualProtectorStatus(1), Completed).success.value
              .set(TrustHasProtectorYesNoPage, true).success.value

            factory.answerSectionsIfCompleted(userAnswers, Some(Completed)) mustBe
              List(
                AnswerSection(
                  Some("Individual protector 1"),
                  Nil,
                  Some("Protectors")
                ),
                AnswerSection(
                  Some("Individual protector 2"),
                  Nil,
                  None
                )
              )
          }

          "Individual and Business protectors" in {
            val userAnswers: UserAnswers = emptyUserAnswers
              .set(IndividualProtectorStatus(0), Completed).success.value
              .set(BusinessProtectorStatus(0), Completed).success.value
              .set(TrustHasProtectorYesNoPage, true).success.value

            factory.answerSectionsIfCompleted(userAnswers, Some(Completed)) mustBe
              List(
                AnswerSection(
                  Some("Individual protector 1"),
                  Nil,
                  Some("Protectors")
                ),
                AnswerSection(
                  Some("Business protector 1"),
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
