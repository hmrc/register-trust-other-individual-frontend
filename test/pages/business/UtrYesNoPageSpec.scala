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

import models.{InternationalAddress, UkAddress, UserAnswers}
import pages.behaviours.PageBehaviours
import pages.register.business.{AddressUkYesNoPage, AddressYesNoPage, NonUkAddressPage, UkAddressPage, UtrPage, UtrYesNoPage}
import org.scalacheck.Arbitrary.arbitrary

class UtrYesNoPageSpec extends PageBehaviours {

  "UtrYesNoPage" must {

    beRetrievable[Boolean](UtrYesNoPage(0))

    beSettable[Boolean](UtrYesNoPage(0))

    beRemovable[Boolean](UtrYesNoPage(0))

    "remove relevant Data when UtrYesNoPage is set to true" in {
      val index = 0
      forAll(arbitrary[UserAnswers], arbitrary[String]) {
        (initial, str) =>
          val answers: UserAnswers = initial.set(AddressYesNoPage(index), true).success.value
            .set(AddressUkYesNoPage(index), true).success.value
            .set(UkAddressPage(index), UkAddress(str, str, Some(str), Some(str), str)).success.value
            .set(NonUkAddressPage(index), InternationalAddress(str, str, Some(str), str)).success.value

          val result = answers.set(UtrYesNoPage(index), true).success.value

          result.get(AddressYesNoPage(index)) mustNot be(defined)
          result.get(AddressUkYesNoPage(index)) mustNot be(defined)
          result.get(UkAddressPage(index)) mustNot be(defined)
          result.get(NonUkAddressPage(index)) mustNot be(defined)
      }
    }

    "remove relevant Data when UtrYesNoPage is set to false" in {
      val index = 0
      forAll(arbitrary[UserAnswers], arbitrary[String]) {
        (initial, str) =>
          val answers: UserAnswers = initial.set(UtrYesNoPage(index), true).success.value
            .set(UtrPage(index), "1234567890").success.value

          val result = answers.set(UtrYesNoPage(index), false).success.value

          result.get(UtrPage(index)) mustNot be(defined)
      }
    }

  }
}
