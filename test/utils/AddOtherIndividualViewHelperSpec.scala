/*
 * Copyright 2023 HM Revenue & Customs
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

import base.SpecBase
import controllers.register.individual.{routes => irts}
import models.Status.{Completed, InProgress}
import models.{FullName, UkAddress, UserAnswers}
import pages.entitystatus.OtherIndividualStatus
import pages.register.{individual => ind}
import viewmodels.{AddRow, AddToRows}


class AddOtherIndividualViewHelperSpec extends SpecBase {


  private def changeInProgressOtherIndividualRoute(index: Int): String = irts.NameController.onPageLoad(index, draftId).url
  private def changeCompleteOtherIndividualRoute(index: Int): String = irts.CheckDetailsController.onPageLoad(index, draftId).url
  private def removeOtherIndividualRoute(index: Int): String = irts.RemoveOtherIndividualController.onPageLoad(index, draftId).url

  "Add otherIndividual view helper" when {

    def helper(userAnswers: UserAnswers) = new AddOtherIndividualViewHelper(userAnswers, fakeDraftId)

    "otherIndividual" must {

      val name: FullName = FullName("First", Some("Middle"), "Last")
      val label: String = "Other Individual"

      "render a complete" in {

        val index: Int = 0

        val userAnswers = emptyUserAnswers
          .set(ind.NamePage(index), name).success.value
          .set(ind.DateOfBirthYesNoPage(index), false).success.value
          .set(ind.AddressYesNoPage(index), true).success.value
          .set(ind.AddressUkYesNoPage(index), true).success.value
          .set(ind.UkAddressPage(index), UkAddress("line1", "line2", None, None, "NE99 1NE")).success.value
          .set(ind.PassportDetailsYesNoPage(index), false).success.value
          .set(ind.IDCardDetailsYesNoPage(index), false).success.value
          .set(OtherIndividualStatus(index), Completed).success.value

        helper(userAnswers).rows mustEqual AddToRows(
          inProgress = Nil,
          complete = List(
            AddRow(
              name = name.toString,
              typeLabel = label,
              changeUrl = changeCompleteOtherIndividualRoute(index),
              removeUrl = removeOtherIndividualRoute(index)
            )
          )
        )
      }

      "render an in progress" when {

        "it has a name" in {

          val index: Int = 0

          val userAnswers = emptyUserAnswers
            .set(ind.NamePage(index), name).success.value
            .set(ind.DateOfBirthYesNoPage(index), false).success.value
            .set(OtherIndividualStatus(index), InProgress).success.value

          helper(userAnswers).rows mustEqual AddToRows(
            inProgress = List(
              AddRow(
                name = name.toString,
                typeLabel = label,
                changeUrl = changeInProgressOtherIndividualRoute(index),
                removeUrl = removeOtherIndividualRoute(index)
              )
            ),
            complete = Nil
          )
        }
      }

      "render multiple individual otherIndividuals" in {

        val name1: FullName = FullName("Name 1", Some("Middle"), "Last")
        val name2: FullName = FullName("Name 2", Some("Middle"), "Last")
        val name3: FullName = FullName("Name 3", Some("Middle"), "Last")

        val userAnswers = emptyUserAnswers
          .set(ind.NamePage(0), name1).success.value
          .set(ind.DateOfBirthYesNoPage(0), false).success.value
          .set(ind.NationalInsuranceYesNoPage(0), true).success.value
          .set(ind.NationalInsuranceNumberPage(0), "AB123456C").success.value
          .set(OtherIndividualStatus(0), Completed).success.value

          .set(ind.NamePage(1), name2).success.value
          .set(ind.DateOfBirthYesNoPage(1), false).success.value
          .set(ind.NationalInsuranceYesNoPage(1), false).success.value
          .set(ind.AddressYesNoPage(1), true).success.value
          .set(ind.AddressUkYesNoPage(1), true).success.value
          .set(ind.UkAddressPage(1), UkAddress("line1", "line2", None, None, "NE99 1NE")).success.value
          .set(ind.PassportDetailsYesNoPage(1), false).success.value
          .set(ind.IDCardDetailsYesNoPage(1), false).success.value
          .set(OtherIndividualStatus(1), Completed).success.value

          .set(ind.NamePage(2), name3).success.value
          .set(ind.DateOfBirthYesNoPage(2), false).success.value
          .set(OtherIndividualStatus(2), InProgress).success.value

        helper(userAnswers).rows mustEqual AddToRows(
          inProgress = List(
            AddRow(
              name = name3.toString,
              typeLabel = label,
              changeUrl = changeInProgressOtherIndividualRoute(2),
              removeUrl = removeOtherIndividualRoute(2)
            )
          ),
          complete = List(
            AddRow(
              name = name1.toString,
              typeLabel = label,
              changeUrl = changeCompleteOtherIndividualRoute(0),
              removeUrl = removeOtherIndividualRoute(0)
            ),
            AddRow(
              name = name2.toString,
              typeLabel = label,
              changeUrl = changeCompleteOtherIndividualRoute(1),
              removeUrl = removeOtherIndividualRoute(1)
            )
          )
        )
      }
    }
  }

}
