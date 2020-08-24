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

package pages.business

import java.time.LocalDate

import models.{InternationalAddress, UkAddress, UserAnswers}
import pages.behaviours.PageBehaviours
import pages.register.business.{AddressUkYesNoPage, AddressYesNoPage, NonUkAddressPage, UkAddressPage}
import org.scalacheck.Arbitrary.arbitrary


class AddressUkYesNoPageSpec extends PageBehaviours {

  "AddressUkYesNoPage" must {

    beRetrievable[Boolean](AddressUkYesNoPage(0))

    beSettable[Boolean](AddressUkYesNoPage(0))

    beRemovable[Boolean](AddressUkYesNoPage(0))

    "remove UkAddressPage when AddressUkYesNoPage is set to false" in {
      val index = 0
      forAll(arbitrary[UserAnswers], arbitrary[String]) {
        (initial, str) =>
          val answers: UserAnswers =
            initial.set(UkAddressPage(index), UkAddress(str, str, Some(str), Some(str), str)).success.value
              .set(NonUkAddressPage(index), InternationalAddress(str, str, Some(str), str)).success.value

          val result = answers.set(AddressUkYesNoPage(index), false).success.value

          result.get(NonUkAddressPage(index)) must be(defined)
          result.get(UkAddressPage(index)) mustNot be(defined)
      }
    }

    "remove NonUkAddressPage when AddressUkYesNoPage is set to true" in {
      val index = 0
      forAll(arbitrary[UserAnswers], arbitrary[String]) {
        (initial, str) =>
          val answers: UserAnswers =
            initial.set(UkAddressPage(index), UkAddress(str, str, Some(str), Some(str), str)).success.value
              .set(NonUkAddressPage(index), InternationalAddress(str, str, Some(str), str)).success.value

          val result = answers.set(AddressUkYesNoPage(index), true).success.value

          result.get(NonUkAddressPage(index)) mustNot be(defined)
          result.get(UkAddressPage(index)) must be(defined)
      }
    }
  }
}
