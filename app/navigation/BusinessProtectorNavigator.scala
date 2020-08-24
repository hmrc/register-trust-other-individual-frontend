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

import config.FrontendAppConfig
import javax.inject.Inject
import models.ReadableUserAnswers
import pages.Page
import pages.register.business.{AddressUkYesNoPage, AddressYesNoPage, NamePage, NonUkAddressPage, UkAddressPage, UtrPage, UtrYesNoPage}
import play.api.mvc.Call
import controllers.register.business.{routes => brts}

class BusinessProtectorNavigator @Inject()(config: FrontendAppConfig) extends Navigator {

  override def nextPage(page: Page, draftId: String, userAnswers: ReadableUserAnswers): Call = routes(draftId)(page)(userAnswers)

  private def simpleNavigation(draftId: String): PartialFunction[Page, Call] = {
    case NamePage(index) => brts.UtrYesNoController.onPageLoad(index, draftId)
    case UtrPage(index) => brts.CheckDetailsController.onPageLoad(index, draftId)
    case UkAddressPage(index) => brts.CheckDetailsController.onPageLoad(index, draftId)
    case NonUkAddressPage(index) => brts.CheckDetailsController.onPageLoad(index, draftId)
  }

  private def yesNoNavigation(draftId: String) : PartialFunction[Page, ReadableUserAnswers => Call] = {
    case UtrYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        UtrYesNoPage(index),
        brts.UtrController.onPageLoad(index, draftId),
        brts.AddressYesNoController.onPageLoad(index, draftId))
    case AddressYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        AddressYesNoPage(index),
        brts.AddressUkYesNoController.onPageLoad(index, draftId),
        brts.CheckDetailsController.onPageLoad(index, draftId))
    case AddressUkYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        AddressUkYesNoPage(index),
        brts.UkAddressController.onPageLoad(index, draftId),
        brts.NonUkAddressController.onPageLoad(index, draftId))
  }

  private def routes(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    simpleNavigation(draftId) andThen (c => (_:ReadableUserAnswers) => c) orElse
      yesNoNavigation(draftId)
  }

}

