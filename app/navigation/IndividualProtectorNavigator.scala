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

package navigation

import controllers.register.{routes => rts}
import controllers.register.individual.{routes => irts}
import javax.inject.Inject
import models.ReadableUserAnswers
import pages.Page
import pages.register.individual._
import play.api.mvc.Call

class IndividualProtectorNavigator @Inject()() extends Navigator {

  override def nextPage(page: Page, draftId: String, userAnswers: ReadableUserAnswers): Call = routes(draftId)(page)(userAnswers)

  private def simpleNavigation(draftId: String): PartialFunction[Page, Call] = {
    case NamePage(index) => irts.DateOfBirthYesNoController.onPageLoad(index, draftId)
    case DateOfBirthPage(index) => irts.NationalInsuranceYesNoController.onPageLoad(index, draftId)
    case NationalInsuranceNumberPage(index) => irts.CheckDetailsController.onPageLoad(index, draftId)
    case UkAddressPage(index) => irts.PassportDetailsYesNoController.onPageLoad(index, draftId)
    case NonUkAddressPage(index) => irts.PassportDetailsYesNoController.onPageLoad(index, draftId)
    case PassportDetailsPage(index) => irts.CheckDetailsController.onPageLoad(index, draftId)
    case IDCardDetailsPage(index) => irts.CheckDetailsController.onPageLoad(index, draftId)
    case CheckDetailsPage => rts.AddAProtectorController.onPageLoad(draftId)
  }

  private def yesNoNavigation(draftId: String) : PartialFunction[Page, ReadableUserAnswers => Call] = {
    case DateOfBirthYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        DateOfBirthYesNoPage(index),
        irts.DateOfBirthController.onPageLoad(index, draftId),
        irts.NationalInsuranceYesNoController.onPageLoad(index, draftId))
    case NationalInsuranceYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        NationalInsuranceYesNoPage(index),
        irts.NationalInsuranceNumberController.onPageLoad(index, draftId),
        irts.AddressYesNoController.onPageLoad(index, draftId))
    case AddressYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        AddressYesNoPage(index),
        irts.AddressUkYesNoController.onPageLoad(index, draftId),
        irts.CheckDetailsController.onPageLoad(index, draftId))
    case AddressUkYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        AddressUkYesNoPage(index),
        irts.UkAddressController.onPageLoad(index, draftId),
        irts.NonUkAddressController.onPageLoad(index, draftId))
    case PassportDetailsYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        PassportDetailsYesNoPage(index),
        irts.PassportDetailsController.onPageLoad(index, draftId),
        irts.IDCardDetailsYesNoController.onPageLoad(index, draftId))
    case IDCardDetailsYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        IDCardDetailsYesNoPage(index),
        irts.IDCardDetailsController.onPageLoad(index, draftId),
        irts.CheckDetailsController.onPageLoad(index, draftId))
  }

  private def routes(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    simpleNavigation(draftId) andThen (c => (_:ReadableUserAnswers) => c) orElse
      yesNoNavigation(draftId)
  }

}

