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

import base.SpecBase
import controllers.register.business.{routes => brts}
import controllers.register.individual.{routes => irts}
import controllers.routes
import models.Status.{Completed, InProgress}
import models.{FullName, UkAddress, UserAnswers}
import pages.entitystatus.{BusinessProtectorStatus, IndividualProtectorStatus}
import pages.register.business._
import pages.register.{individual => ind}
import viewmodels.{AddRow, AddToRows}


class AddAProtectorViewHelperSpec extends SpecBase {

  private def changeInProgressBusinessProtectorRoute(index: Int): String = brts.NameController.onPageLoad(index, draftId).url
  private def changeCompleteBusinessProtectorRoute(index: Int): String = brts.CheckDetailsController.onPageLoad(index, draftId).url
  private def removeBusinessProtectorRoute(index: Int): String = brts.RemoveProtectorController.onPageLoad(index, draftId).url
  
  private def changeInProgressIndividualProtectorRoute(index: Int): String = irts.NameController.onPageLoad(index, draftId).url
  private def changeCompleteIndividualProtectorRoute(index: Int): String = irts.CheckDetailsController.onPageLoad(index, draftId).url
  private def removeIndividualProtectorRoute(index: Int): String = irts.RemoveProtectorController.onPageLoad(index, draftId).url
  
  "Add a protector view helper" when {

    def helper(userAnswers: UserAnswers) = new AddAProtectorViewHelper(userAnswers, fakeDraftId)

    "business protector" must {

      val name: String = "Business"
      val label: String = "Business protector"

      "render a complete" in {

        val index: Int = 0

        val userAnswers = emptyUserAnswers
          .set(NamePage(index), name).success.value
          .set(UtrYesNoPage(index), false).success.value
          .set(AddressYesNoPage(index), true).success.value
          .set(AddressUkYesNoPage(index), true).success.value
          .set(UkAddressPage(index), UkAddress("line1", "line2", None, None, "NE99 1NE")).success.value
          .set(BusinessProtectorStatus(index), Completed).success.value

        helper(userAnswers).rows mustEqual AddToRows(
          inProgress = Nil,
          complete = List(
            AddRow(
              name = name,
              typeLabel = label,
              changeUrl = changeCompleteBusinessProtectorRoute(index),
              removeUrl = removeBusinessProtectorRoute(index)
            )
          )
        )
      }

      "render an in progress" when {

        "it has a name" in {

          val index: Int = 0

          val userAnswers = emptyUserAnswers
            .set(NamePage(index), name).success.value
            .set(UtrYesNoPage(index), false).success.value
            .set(BusinessProtectorStatus(index), InProgress).success.value

          helper(userAnswers).rows mustEqual AddToRows(
            inProgress = List(
              AddRow(
                name = name,
                typeLabel = label,
                changeUrl = changeInProgressBusinessProtectorRoute(index),
                removeUrl = removeBusinessProtectorRoute(index)
              )
            ),
            complete = Nil
          )
        }
      }

      "render multiple business protectors" in {

        val name1 = "Name 1"
        val name2 = "Name 2"
        val name3 = "Name 3"

        val userAnswers = emptyUserAnswers
          .set(NamePage(0), name1).success.value
          .set(UtrYesNoPage(0), true).success.value
          .set(UtrPage(0), "1234567890").success.value
          .set(BusinessProtectorStatus(0), Completed).success.value

          .set(NamePage(1), name2).success.value
          .set(UtrYesNoPage(1), true).success.value
          .set(UtrPage(1), "1234567890").success.value
          .set(BusinessProtectorStatus(1), Completed).success.value

          .set(NamePage(2), name3).success.value
          .set(UtrYesNoPage(2), false).success.value
          .set(BusinessProtectorStatus(2), InProgress).success.value

        helper(userAnswers).rows mustEqual AddToRows(
          inProgress = List(
            AddRow(
              name = name3,
              typeLabel = label,
              changeUrl = changeInProgressBusinessProtectorRoute(2),
              removeUrl = removeBusinessProtectorRoute(2)
            )
          ),
          complete = List(
            AddRow(
              name = name1,
              typeLabel = label,
              changeUrl = changeCompleteBusinessProtectorRoute(0),
              removeUrl = removeBusinessProtectorRoute(0)
            ),
            AddRow(
              name = name2,
              typeLabel = label,
              changeUrl = changeCompleteBusinessProtectorRoute(1),
              removeUrl = removeBusinessProtectorRoute(1)
            )
          )
        )
      }
    }

    "individual protector" must {

      val name: FullName = FullName("First", Some("Middle"), "Last")
      val label: String = "Individual protector"

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
          .set(IndividualProtectorStatus(index), Completed).success.value

        helper(userAnswers).rows mustEqual AddToRows(
          inProgress = Nil,
          complete = List(
            AddRow(
              name = name.toString,
              typeLabel = label,
              changeUrl = changeCompleteIndividualProtectorRoute(index),
              removeUrl = removeIndividualProtectorRoute(index)
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
            .set(IndividualProtectorStatus(index), InProgress).success.value

          helper(userAnswers).rows mustEqual AddToRows(
            inProgress = List(
              AddRow(
                name = name.toString,
                typeLabel = label,
                changeUrl = changeInProgressIndividualProtectorRoute(index),
                removeUrl = removeIndividualProtectorRoute(index)
              )
            ),
            complete = Nil
          )
        }
      }

      "render multiple individual protectors" in {

        val name1: FullName = FullName("Name 1", Some("Middle"), "Last")
        val name2: FullName = FullName("Name 2", Some("Middle"), "Last")
        val name3: FullName = FullName("Name 3", Some("Middle"), "Last")

        val userAnswers = emptyUserAnswers
          .set(ind.NamePage(0), name1).success.value
          .set(ind.DateOfBirthYesNoPage(0), false).success.value
          .set(ind.NationalInsuranceYesNoPage(0), true).success.value
          .set(ind.NationalInsuranceNumberPage(0), "AB123456C").success.value
          .set(IndividualProtectorStatus(0), Completed).success.value

          .set(ind.NamePage(1), name2).success.value
          .set(ind.DateOfBirthYesNoPage(1), false).success.value
          .set(ind.NationalInsuranceYesNoPage(1), false).success.value
          .set(ind.AddressYesNoPage(1), true).success.value
          .set(ind.AddressUkYesNoPage(1), true).success.value
          .set(ind.UkAddressPage(1), UkAddress("line1", "line2", None, None, "NE99 1NE")).success.value
          .set(ind.PassportDetailsYesNoPage(1), false).success.value
          .set(ind.IDCardDetailsYesNoPage(1), false).success.value
          .set(IndividualProtectorStatus(1), Completed).success.value

          .set(ind.NamePage(2), name3).success.value
          .set(ind.DateOfBirthYesNoPage(2), false).success.value
          .set(IndividualProtectorStatus(2), InProgress).success.value

        helper(userAnswers).rows mustEqual AddToRows(
          inProgress = List(
            AddRow(
              name = name3.toString,
              typeLabel = label,
              changeUrl = changeInProgressIndividualProtectorRoute(2),
              removeUrl = removeIndividualProtectorRoute(2)
            )
          ),
          complete = List(
            AddRow(
              name = name1.toString,
              typeLabel = label,
              changeUrl = changeCompleteIndividualProtectorRoute(0),
              removeUrl = removeIndividualProtectorRoute(0)
            ),
            AddRow(
              name = name2.toString,
              typeLabel = label,
              changeUrl = changeCompleteIndividualProtectorRoute(1),
              removeUrl = removeIndividualProtectorRoute(1)
            )
          )
        )
      }
    }
  }

}
