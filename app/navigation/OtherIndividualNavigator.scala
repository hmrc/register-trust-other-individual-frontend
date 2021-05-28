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
import controllers.register.individual.mld5.routes._
import controllers.register.individual.routes._
import controllers.register.routes._
import models.ReadableUserAnswers
import models.register.pages.AddOtherIndividual
import pages.Page
import pages.register.individual._
import pages.register.individual.mld5._
import pages.register.{AddOtherIndividualPage, AddOtherIndividualYesNoPage, TrustHasOtherIndividualYesNoPage}
import play.api.mvc.Call
import sections.OtherIndividuals
import uk.gov.hmrc.http.HttpVerbs.GET

import javax.inject.Inject

class OtherIndividualNavigator @Inject()(config: FrontendAppConfig) extends Navigator {

  override def nextPage(page: Page, draftId: String, userAnswers: ReadableUserAnswers): Call = routes(draftId, config)(page)(userAnswers)

  private def hubNavigation(draftId: String, config: FrontendAppConfig): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case AddOtherIndividualPage => addOtherIndividualRoute(draftId, config)
    case AddOtherIndividualYesNoPage => addOtherIndividualYesNoRoute(draftId, config)
    case TrustHasOtherIndividualYesNoPage => ua => yesNoNav(
      ua = ua,
      TrustHasOtherIndividualYesNoPage,
      InfoController.onPageLoad(draftId),
      otherIndividualsCompletedRoute(draftId, config)
    )
  }

  private def simpleNavigation(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case NamePage(index) => _ => DateOfBirthYesNoController.onPageLoad(index, draftId)
    case DateOfBirthPage(index) => ua => navigateAwayFromDateOfBirthQuestions(draftId, index, ua.is5mldEnabled)
    case CountryOfNationalityPage(index) => ua => navigateAwayFromCountryOfNationalityQuestions(draftId, index, ua.isTaxable)
    case NationalInsuranceNumberPage(index) => ua => navigateAwayFromNinoQuestion(draftId, index, ua.is5mldEnabled)
    case CountryOfResidencePage(index) => ua => navigateAwayFromCountryOfResidencyQuestions(draftId, index, ua)
    case UkAddressPage(index) => _ => PassportDetailsYesNoController.onPageLoad(index, draftId)
    case NonUkAddressPage(index) => _ => PassportDetailsYesNoController.onPageLoad(index, draftId)
    case PassportDetailsPage(index) => ua => navigateToMentalCapacityOrCheckAnswers(draftId, index, ua.is5mldEnabled)
    case IDCardDetailsPage(index) => ua => navigateToMentalCapacityOrCheckAnswers(draftId, index, ua.is5mldEnabled)
    case MentalCapacityYesNoPage(index) => _ => CheckDetailsController.onPageLoad(index, draftId)
    case CheckDetailsPage => _ => AddOtherIndividualController.onPageLoad(draftId)
  }

  private def yesNoNavigation(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case page @ DateOfBirthYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = DateOfBirthController.onPageLoad(index, draftId),
        noCall = navigateAwayFromDateOfBirthQuestions(draftId, index, ua.is5mldEnabled)
      )
    case page @ CountryOfNationalityYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = CountryOfNationalityInTheUkYesNoController.onPageLoad(index, draftId),
        noCall = navigateAwayFromCountryOfNationalityQuestions(draftId, index, ua.isTaxable)
      )
    case page @ CountryOfNationalityInTheUkYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = navigateAwayFromCountryOfNationalityQuestions(draftId, index, ua.isTaxable),
        noCall = CountryOfNationalityController.onPageLoad(index, draftId)
      )
    case page @ NationalInsuranceYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = NationalInsuranceNumberController.onPageLoad(index, draftId),
        noCall = navigateAwayFromNinoYesNoQuestion(draftId, index, ua.is5mldEnabled)
      )
    case page @ CountryOfResidenceYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId),
        noCall = navigateAwayFromCountryOfResidencyQuestions(draftId, index, ua)
      )
    case page @ CountryOfResidenceInTheUkYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = navigateAwayFromCountryOfResidencyQuestions(draftId, index, ua),
        noCall = CountryOfResidenceController.onPageLoad(index, draftId)
      )
    case page @ AddressYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = AddressUkYesNoController.onPageLoad(index, draftId),
        noCall = navigateToMentalCapacityOrCheckAnswers(draftId, index, ua.is5mldEnabled)
      )
    case page @ AddressUkYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = UkAddressController.onPageLoad(index, draftId),
        noCall = NonUkAddressController.onPageLoad(index, draftId)
      )
    case page @ PassportDetailsYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = PassportDetailsController.onPageLoad(index, draftId),
        noCall = IDCardDetailsYesNoController.onPageLoad(index, draftId)
      )
    case page @ IDCardDetailsYesNoPage(index) => ua =>
      yesNoNav(
        ua = ua,
        fromPage = page,
        yesCall = IDCardDetailsController.onPageLoad(index, draftId),
        noCall = navigateToMentalCapacityOrCheckAnswers(draftId, index, ua.is5mldEnabled)
      )
  }

  private def navigateAwayFromDateOfBirthQuestions(draftId: String, index: Int, is5mldEnabled: Boolean): Call = {
    if (is5mldEnabled) {
      CountryOfNationalityYesNoController.onPageLoad(index, draftId)
    } else {
      NationalInsuranceYesNoController.onPageLoad(index, draftId)
    }
  }

  private def navigateAwayFromNinoYesNoQuestion(draftId: String, index: Int, is5mldEnabled: Boolean): Call = {
    if (is5mldEnabled) {
      CountryOfResidenceYesNoController.onPageLoad(index, draftId)
    } else {
      AddressYesNoController.onPageLoad(index, draftId)
    }
  }

  private def navigateAwayFromNinoQuestion(draftId: String, index: Int, is5mldEnabled: Boolean): Call = {
    if (is5mldEnabled) {
      CountryOfResidenceYesNoController.onPageLoad(index, draftId)
    } else {
      CheckDetailsController.onPageLoad(index, draftId)
    }
  }

  private def navigateAwayFromCountryOfNationalityQuestions(draftId: String, index: Int, isTaxable: Boolean): Call = {
    if (isTaxable) {
      NationalInsuranceYesNoController.onPageLoad(index, draftId)
    } else {
      CountryOfResidenceYesNoController.onPageLoad(index, draftId)
    }
  }

  private def navigateAwayFromCountryOfResidencyQuestions(draftId: String, index: Int, ua: ReadableUserAnswers): Call = {
    val isNinoDefined = ua.get(NationalInsuranceNumberPage(index)).isDefined
    val isNonTaxable = !ua.isTaxable

    (isNinoDefined, isNonTaxable) match {
      case (true, _) | (_, true) => MentalCapacityYesNoController.onPageLoad(index, draftId)
      case _ => AddressYesNoController.onPageLoad(index, draftId)
    }
  }

  private def navigateToMentalCapacityOrCheckAnswers(draftId: String, index: Int, is5mldEnabled: Boolean): Call = {
    if (is5mldEnabled) {
      MentalCapacityYesNoController.onPageLoad(index, draftId)
    } else {
      CheckDetailsController.onPageLoad(index, draftId)
    }
  }

  private def otherIndividualsCompletedRoute(draftId: String, config: FrontendAppConfig): Call = {
    Call(GET, config.registrationProgressUrl(draftId))
  }

  private def routeToOtherIndividualIndex(userAnswers: ReadableUserAnswers, draftId: String): Call = {
    val otherIndividuals = userAnswers.get(OtherIndividuals).getOrElse(List.empty)
    val index = otherIndividuals match {
      case Nil => 0
      case x if !x.last.isComplete => x.size - 1
      case x => x.size
    }
    NameController.onPageLoad(index, draftId)
  }

  private def addOtherIndividualRoute(draftId: String, config: FrontendAppConfig)(answers: ReadableUserAnswers): Call = {
    answers.get(AddOtherIndividualPage) match {
      case Some(AddOtherIndividual.YesNow) => routeToOtherIndividualIndex(answers, draftId)
      case Some(AddOtherIndividual.YesLater) => otherIndividualsCompletedRoute(draftId, config)
      case Some(AddOtherIndividual.NoComplete) => otherIndividualsCompletedRoute(draftId, config)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def addOtherIndividualYesNoRoute(draftId: String, config: FrontendAppConfig)(answers: ReadableUserAnswers): Call = {
    answers.get(AddOtherIndividualYesNoPage) match {
      case Some(true) => routeToOtherIndividualIndex(answers, draftId)
      case Some(false) => otherIndividualsCompletedRoute(draftId, config)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def routes(draftId: String, config: FrontendAppConfig): PartialFunction[Page, ReadableUserAnswers => Call] = {
    hubNavigation(draftId, config) orElse
      simpleNavigation(draftId) orElse
      yesNoNavigation(draftId)
  }

}
