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

package navigation

import config.FrontendAppConfig
import controllers.register.individual.{routes => irts}
import controllers.register.individual.mld5.{routes => mld5irts}
import controllers.register.{routes => rts}
import javax.inject.Inject
import models.ReadableUserAnswers
import models.register.pages.AddOtherIndividual
import pages.Page
import pages.register.{AddOtherIndividualPage, AddOtherIndividualYesNoPage, AnswersPage, TrustHasOtherIndividualYesNoPage}
import pages.register.individual._
import pages.register.individual.mld5._
import play.api.mvc.Call
import sections.OtherIndividualsView

class OtherIndividualNavigator @Inject()(config: FrontendAppConfig) extends Navigator {

  override def nextPage(page: Page, draftId: String, userAnswers: ReadableUserAnswers): Call = routes(draftId, config)(page)(userAnswers)

  private def simpleNavigation(draftId: String, config: FrontendAppConfig): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case AnswersPage => _ => rts.AddOtherIndividualController.onPageLoad(draftId)
    case AddOtherIndividualPage => addOtherIndividualRoute(draftId, config)
    case AddOtherIndividualYesNoPage => addOtherIndividualYesNoRoute(draftId, config)
    case TrustHasOtherIndividualYesNoPage => trustHasOtherIndividualRoute(draftId)
    case NamePage(index) => _ => irts.DateOfBirthYesNoController.onPageLoad(index, draftId)
    case DateOfBirthPage(index) => ua => navigateAwayFromDateOfBirthQuestions(draftId, index, ua.is5mldEnabled)
    case CountryOfNationalityPage(index) => _ => irts.NationalInsuranceYesNoController.onPageLoad(index, draftId)
    case NationalInsuranceNumberPage(index) => ua => navigateAwayFromNinoQuestion(draftId, index, ua.is5mldEnabled)
    case CountryOfResidencePage(index) => ua => navigateAwayFromCountryOfResidencyQuestions(draftId, index, ua)
    case UkAddressPage(index) => _ => irts.PassportDetailsYesNoController.onPageLoad(index, draftId)
    case NonUkAddressPage(index) => _ => irts.PassportDetailsYesNoController.onPageLoad(index, draftId)
    case PassportDetailsPage(index) => ua => navigateToMentalCapacityOrCheckAnswers(draftId, index, ua.is5mldEnabled)
    case IDCardDetailsPage(index) => ua => navigateToMentalCapacityOrCheckAnswers(draftId, index, ua.is5mldEnabled)
    case MentalCapacityYesNoPage(index) => _ => irts.CheckDetailsController.onPageLoad(index, draftId)
    case CheckDetailsPage => _ => rts.AddOtherIndividualController.onPageLoad(draftId)
  }

  private def yesNoNavigation(draftId: String) : PartialFunction[Page, ReadableUserAnswers => Call] = {
    case page @ DateOfBirthYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = irts.DateOfBirthController.onPageLoad(index, draftId),
        noCall = navigateAwayFromDateOfBirthQuestions(draftId, index, ua.is5mldEnabled))
    case page @ CountryOfNationalityYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = mld5irts.CountryOfNationalityInTheUkYesNoController.onPageLoad(index, draftId),
        noCall = irts.NationalInsuranceYesNoController.onPageLoad(index, draftId)
      )
    case page @ CountryOfNationalityInTheUkYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = irts.NationalInsuranceYesNoController.onPageLoad(index, draftId),
        noCall = mld5irts.CountryOfNationalityController.onPageLoad(index, draftId)
      )
    case page @ NationalInsuranceYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = irts.NationalInsuranceNumberController.onPageLoad(index, draftId),
        noCall = navigateAwayFromNinoYesNoQuestion(draftId, index, ua.is5mldEnabled))
    case page @ CountryOfResidenceYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = mld5irts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId),
        noCall = navigateAwayFromCountryOfResidencyQuestions(draftId, index, ua))
    case page @ CountryOfResidenceInTheUkYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = navigateAwayFromCountryOfResidencyQuestions(draftId, index, ua),
        noCall = mld5irts.CountryOfResidenceController.onPageLoad(index, draftId))
    case page @ AddressYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = irts.AddressUkYesNoController.onPageLoad(index, draftId),
        noCall = navigateToMentalCapacityOrCheckAnswers(draftId, index, ua.is5mldEnabled))
    case page @ AddressUkYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = irts.UkAddressController.onPageLoad(index, draftId),
        noCall = irts.NonUkAddressController.onPageLoad(index, draftId))
    case page @ PassportDetailsYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = irts.PassportDetailsController.onPageLoad(index, draftId),
        noCall = irts.IDCardDetailsYesNoController.onPageLoad(index, draftId))
    case page @ IDCardDetailsYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = irts.IDCardDetailsController.onPageLoad(index, draftId),
        noCall = navigateToMentalCapacityOrCheckAnswers(draftId, index, ua.is5mldEnabled))
  }

  private def navigateAwayFromDateOfBirthQuestions(draftId: String, index: Int, is5mldEnabled: Boolean): Call = {
    if (is5mldEnabled) {
      mld5irts.CountryOfNationalityYesNoController.onPageLoad(index, draftId)
    } else {
      irts.NationalInsuranceYesNoController.onPageLoad(index, draftId)
    }
  }

  private def navigateAwayFromNinoYesNoQuestion(draftId: String, index: Int, is5mldEnabled: Boolean): Call = {
    if (is5mldEnabled) {
      mld5irts.CountryOfResidenceYesNoController.onPageLoad(index, draftId)
    } else {
      irts.AddressYesNoController.onPageLoad(index, draftId)
    }
  }

  private def navigateAwayFromNinoQuestion(draftId: String, index: Int, is5mldEnabled: Boolean): Call = {
    if (is5mldEnabled) {
      mld5irts.CountryOfResidenceYesNoController.onPageLoad(index, draftId)
    } else {
      irts.CheckDetailsController.onPageLoad(index, draftId)
    }
  }

  private def navigateAwayFromCountryOfResidencyQuestions(draftId: String, index: Int, ua: ReadableUserAnswers): Call = {
    ua.get(NationalInsuranceYesNoPage(index)) match {
      case Some(true) => mld5irts.MentalCapacityYesNoController.onPageLoad(index, draftId)
      case _ => irts.AddressYesNoController.onPageLoad(index, draftId)
    }
  }

  private def navigateToMentalCapacityOrCheckAnswers(draftId: String, index: Int, is5mldEnabled: Boolean): Call = {
    if (is5mldEnabled) {
      mld5irts.MentalCapacityYesNoController.onPageLoad(index, draftId)
    } else {
      irts.CheckDetailsController.onPageLoad(index, draftId)
    }
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
    val otherIndividuals = userAnswers.get(OtherIndividualsView).getOrElse(List.empty)
    irts.NameController.onPageLoad(otherIndividuals.size, draftId)
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

