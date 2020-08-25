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
import controllers.register.individual.{routes => irts}
import controllers.register.{routes => rts}
import javax.inject.Inject
import models.ReadableUserAnswers
import models.register.pages.AddOtherIndividual
import pages.Page
import pages.register.{AddOtherIndividualPage, AddOtherIndividualYesNoPage, AnswersPage, TrustHasOtherIndividualYesNoPage}
import pages.register.individual._
import play.api.mvc.Call
import sections.IndividualOtherIndividuals

class OtherIndividualNavigator @Inject()(config: FrontendAppConfig) extends Navigator {

  override def nextPage(page: Page, draftId: String, userAnswers: ReadableUserAnswers): Call = routes(draftId, config)(page)(userAnswers)

  private def simpleNavigation(draftId: String, config: FrontendAppConfig): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case AnswersPage => _ => rts.AddOtherIndividualController.onPageLoad(draftId)
    case AddOtherIndividualPage => addOtherIndividualRoute(draftId, config)
    case AddOtherIndividualYesNoPage => addOtherIndividualYesNoRoute(draftId, config)
    case TrustHasOtherIndividualYesNoPage => trustHasOtherIndividualRoute(draftId)
    case NamePage(index) => _ => irts.DateOfBirthYesNoController.onPageLoad(index, draftId)
    case DateOfBirthPage(index) => _ => irts.NationalInsuranceYesNoController.onPageLoad(index, draftId)
    case NationalInsuranceNumberPage(index) => _ => irts.CheckDetailsController.onPageLoad(index, draftId)
    case UkAddressPage(index) => _ => irts.PassportDetailsYesNoController.onPageLoad(index, draftId)
    case NonUkAddressPage(index) => _ => irts.PassportDetailsYesNoController.onPageLoad(index, draftId)
    case PassportDetailsPage(index) => _ => irts.CheckDetailsController.onPageLoad(index, draftId)
    case IDCardDetailsPage(index) => _ => irts.CheckDetailsController.onPageLoad(index, draftId)
    case CheckDetailsPage => _ => rts.AddOtherIndividualController.onPageLoad(draftId)
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

  private def otherIndividualsCompletedRoute(draftId: String, config: FrontendAppConfig): Call = {
    Call("GET", config.registrationProgressUrl(draftId))
  }

  private def trustHasOtherIndividualRoute(draftId: String)(userAnswers: ReadableUserAnswers) : Call =
    userAnswers.get(TrustHasOtherIndividualYesNoPage) match {
      case Some(true) => controllers.register.routes.InfoController.onPageLoad(draftId)
      case Some(false) => otherIndividualsCompletedRoute(draftId, config)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }

  private def routeToOtherIndividualIndex(userAnswers: ReadableUserAnswers, draftId: String): Call = {
    val individualOtherIndividuals = userAnswers.get(IndividualOtherIndividuals).getOrElse(List.empty)
    irts.NameController.onPageLoad(individualOtherIndividuals.size, draftId)
  }

  private def addOtherIndividualRoute(draftId: String, config: FrontendAppConfig)(answers: ReadableUserAnswers): Call = {
    val addAnother = answers.get(AddOtherIndividualPage)
    addAnother match {
      case Some(AddOtherIndividual.YesNow) => routeToOtherIndividualIndex(answers, draftId)
      case Some(AddOtherIndividual.YesLater) => otherIndividualsCompletedRoute(draftId, config)
      case Some(AddOtherIndividual.NoComplete) => otherIndividualsCompletedRoute(draftId, config)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def addOtherIndividualYesNoRoute(draftId: String, config: FrontendAppConfig)(answers: ReadableUserAnswers): Call = {
    val add = answers.get(AddOtherIndividualYesNoPage)

    add match {
      case Some(true) => routeToOtherIndividualIndex(answers, draftId)
      case Some(false) => otherIndividualsCompletedRoute(draftId, config)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def routes(draftId: String, config: FrontendAppConfig): PartialFunction[Page, ReadableUserAnswers => Call] = {
    simpleNavigation(draftId, config) orElse
      yesNoNavigation(draftId)
  }

}

