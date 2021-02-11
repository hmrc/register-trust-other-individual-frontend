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

import base.SpecBase
import config.FrontendAppConfig
import controllers.register.individual.mld5.{routes => mld5irts}
import controllers.register.individual.{routes => irts}
import controllers.register.{routes => rts}
import generators.Generators
import models._
import models.register.pages.AddOtherIndividual
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.individual._
import pages.register.individual.mld5._
import pages.register.{AddOtherIndividualPage, AddOtherIndividualYesNoPage, TrustHasOtherIndividualYesNoPage}
import play.api.mvc.Call
import utils.Constants.ES

class OtherIndividualNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new OtherIndividualNavigator(frontendAppConfig)
  val index = 0

  private def otherIndividualsCompletedRoute(draftId: String, config: FrontendAppConfig): Call = {
    Call("GET", config.registrationProgressUrl(draftId))
  }

  "OtherIndividual navigator" must {

    "a 4mld trust" must {

      "AddOtherIndividualYesNoPage -> Yes -> NamePage from AddOtherIndividualYesNoPage" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val index = 0
            val answers = userAnswers.set(AddOtherIndividualYesNoPage, true).success.value

            navigator.nextPage(AddOtherIndividualYesNoPage, fakeDraftId, answers)
              .mustBe(controllers.register.individual.routes.NameController.onPageLoad(index, fakeDraftId))
        }

      }

      "AddOtherIndividualYesNoPage -> No -> RegistrationProgress" in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers.set(AddOtherIndividualYesNoPage, false).success.value

            navigator.nextPage(AddOtherIndividualYesNoPage, fakeDraftId, answers)
              .mustBe(otherIndividualsCompletedRoute(fakeDraftId, frontendAppConfig))
        }
      }

      "AddOtherIndividualPage -> add them now -> NamePage" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val index = 0
            val answers = userAnswers.set(AddOtherIndividualPage, AddOtherIndividual.YesNow).success.value

            navigator.nextPage(AddOtherIndividualPage, fakeDraftId, answers)
              .mustBe(controllers.register.individual.routes.NameController.onPageLoad(index, fakeDraftId))
        }

      }

      "AddOtherIndividualPage -> add them later -> RegistrationProgress" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers
              .set(NamePage(0), FullName("First", None, "Last")).success.value
              .set(AddOtherIndividualPage, AddOtherIndividual.YesLater).success.value

            navigator.nextPage(AddOtherIndividualPage, fakeDraftId, answers)
              .mustBe(otherIndividualsCompletedRoute(fakeDraftId, frontendAppConfig))
        }
      }

      "AddOtherIndividualPage -> added them all -> RegistrationProgress" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>

            val answers = userAnswers
              .set(NamePage(0), FullName("First", None, "Last")).success.value
              .set(AddOtherIndividualPage, AddOtherIndividual.NoComplete).success.value

            navigator.nextPage(AddOtherIndividualPage, fakeDraftId, answers)
              .mustBe(otherIndividualsCompletedRoute(fakeDraftId, frontendAppConfig))
        }
      }

      "TrustHasOtherIndividualYesNoPage -> yes -> InfoPage" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val answers = userAnswers.set(TrustHasOtherIndividualYesNoPage, value = true).success.value

            navigator.nextPage(TrustHasOtherIndividualYesNoPage, fakeDraftId, answers)
              .mustBe(rts.InfoController.onPageLoad(fakeDraftId))
        }
      }

      "TrustHasOtherIndividualYesNoPage -> no -> RegistrationProgress" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val answers = userAnswers.set(TrustHasOtherIndividualYesNoPage, value = false).success.value

            navigator.nextPage(TrustHasOtherIndividualYesNoPage, fakeDraftId, answers)
              .mustBe(otherIndividualsCompletedRoute(fakeDraftId, frontendAppConfig))
        }
      }

      "NamePage -> DateOfBirthYesNoPage" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            navigator.nextPage(NamePage(index), draftId, userAnswers)
              .mustBe(irts.DateOfBirthYesNoController.onPageLoad(index, draftId))
        }
      }

      "DateOfBirthYesNoPage -> Yes -> DateOfBirthPage" in {
        forAll(arbitrary[UserAnswers]) {
          baseAnswers =>
            val answers = baseAnswers.set(DateOfBirthYesNoPage(index), true).success.value
            navigator.nextPage(DateOfBirthYesNoPage(index), draftId, answers)
              .mustBe(irts.DateOfBirthController.onPageLoad(index, draftId))
        }
      }

      "DateOfBirthYesNoPage -> No -> NationalInsuranceYesNoPage" in {
        forAll(arbitrary[UserAnswers]) {
          baseAnswers =>
            val answers = baseAnswers.set(DateOfBirthYesNoPage(index), false).success.value
            navigator.nextPage(DateOfBirthYesNoPage(index), draftId, answers)
              .mustBe(irts.NationalInsuranceYesNoController.onPageLoad(index, draftId))
        }
      }

      "DateOfBirthPage -> NationalInsuranceYesNoPage" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            navigator.nextPage(DateOfBirthPage(index), draftId, userAnswers)
              .mustBe(irts.NationalInsuranceYesNoController.onPageLoad(index, draftId))
        }
      }

      "NationalInsuranceYesNoPage -> Yes -> NationalInsurancePage" in {
        forAll(arbitrary[UserAnswers]) {
          baseAnswers =>
            val answers = baseAnswers.set(NationalInsuranceYesNoPage(index), true).success.value
            navigator.nextPage(NationalInsuranceYesNoPage(index), draftId, answers)
              .mustBe(irts.NationalInsuranceNumberController.onPageLoad(index, draftId))
        }
      }

      "NationalInsuranceYesNoPage -> No -> AddressYesNoPage" in {
        forAll(arbitrary[UserAnswers]) {
          baseAnswers =>
            val answers = baseAnswers.set(NationalInsuranceYesNoPage(index), false).success.value
            navigator.nextPage(NationalInsuranceYesNoPage(index), draftId, answers)
              .mustBe(irts.AddressYesNoController.onPageLoad(index, draftId))
        }
      }

      "NationalInsurancePage -> CheckDetailsPage" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            navigator.nextPage(NationalInsuranceNumberPage(index), draftId, userAnswers)
              .mustBe(irts.CheckDetailsController.onPageLoad(index, draftId))
        }
      }

      "AddressYesNoPage -> Yes -> AddressUkYesNoPage" in {
        val answers = emptyUserAnswers
          .set(AddressYesNoPage(index), true).success.value

        navigator.nextPage(AddressYesNoPage(index), draftId, answers)
          .mustBe(irts.AddressUkYesNoController.onPageLoad(index, draftId))
      }

      "AddressYesNoPage -> No -> CheckDetailsPage" in {
        val answers = emptyUserAnswers
          .set(AddressYesNoPage(index), false).success.value

        navigator.nextPage(AddressYesNoPage(index), draftId, answers)
          .mustBe(irts.CheckDetailsController.onPageLoad(index, draftId))
      }

      "AddressUkYesNoPage -> Yes -> UKAddressPage" in {
        val answers = emptyUserAnswers
          .set(AddressUkYesNoPage(index), true).success.value

        navigator.nextPage(AddressUkYesNoPage(index), draftId, answers)
          .mustBe(irts.UkAddressController.onPageLoad(index, draftId))
      }

      "AddressUkYesNoPage -> No -> NonUKAddressPage" in {
        val answers = emptyUserAnswers
          .set(AddressUkYesNoPage(index), false).success.value

        navigator.nextPage(AddressUkYesNoPage(index), draftId, answers)
          .mustBe(irts.NonUkAddressController.onPageLoad(index, draftId))
      }

      "UKAddressPage -> PassportDetailsYesNoController" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            navigator.nextPage(UkAddressPage(index), draftId, userAnswers)
              .mustBe(irts.PassportDetailsYesNoController.onPageLoad(index, draftId))
        }
      }

      "NonUKAddressPage -> PassportDetailsYesNoController" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            navigator.nextPage(NonUkAddressPage(index), draftId, userAnswers)
              .mustBe(irts.PassportDetailsYesNoController.onPageLoad(index, draftId))
        }
      }

      "PassportDetailsYesNoPage -> Yes -> PassportDetailsPage" in {
        val answers = emptyUserAnswers
          .set(PassportDetailsYesNoPage(index), true).success.value

        navigator.nextPage(PassportDetailsYesNoPage(index), draftId, answers)
          .mustBe(irts.PassportDetailsController.onPageLoad(index, draftId))
      }

      "PassportDetailsYesNoPage -> No -> IDCardDetailsYesNoPage" in {
        val answers = emptyUserAnswers
          .set(PassportDetailsYesNoPage(index), false).success.value

        navigator.nextPage(PassportDetailsYesNoPage(index), draftId, answers)
          .mustBe(irts.IDCardDetailsYesNoController.onPageLoad(index, draftId))
      }

      "IDCardDetailsYesNoPage -> Yes -> IDCardDetailsPage" in {
        val answers = emptyUserAnswers
          .set(IDCardDetailsYesNoPage(index), true).success.value

        navigator.nextPage(IDCardDetailsYesNoPage(index), draftId, answers)
          .mustBe(irts.IDCardDetailsController.onPageLoad(index, draftId))
      }

      "IDCardDetailsYesNoPage -> No -> CheckDetailsPage" in {
        val answers = emptyUserAnswers
          .set(IDCardDetailsYesNoPage(index), false).success.value

        navigator.nextPage(IDCardDetailsYesNoPage(index), draftId, answers)
          .mustBe(irts.CheckDetailsController.onPageLoad(index, draftId))
      }

      "CheckDetailsPage -> AddOtherIndividualPage" in {
        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            navigator.nextPage(CheckDetailsPage, draftId, userAnswers)
              .mustBe(rts.AddOtherIndividualController.onPageLoad(draftId))
        }
      }

    }

    "a 5mld trust" when {

      val baseAnswers = emptyUserAnswers.copy(is5mldEnabled = true)

      "Date of birth yes no page -> No -> CountryOfNationality Yes No page" in {

        val answers = baseAnswers
          .set(DateOfBirthYesNoPage(index), false).success.value

        navigator.nextPage(DateOfBirthYesNoPage(index), draftId, answers)
          .mustBe(mld5irts.CountryOfNationalityYesNoController.onPageLoad(index, draftId))
      }

      "Date of birth page -> CountryOfNationality Yes No page" in {
        navigator.nextPage(DateOfBirthPage(index), draftId, baseAnswers)
          .mustBe(mld5irts.CountryOfNationalityYesNoController.onPageLoad(index, draftId))
      }

      "CountryOfNationality yes no page -> No -> Nino yes no page" in {
        val answers = baseAnswers
          .set(CountryOfNationalityYesNoPage(index), false).success.value

        navigator.nextPage(CountryOfNationalityYesNoPage(index), draftId, answers)
          .mustBe(irts.NationalInsuranceYesNoController.onPageLoad(index, draftId))
      }

      "CountryOfNationality yes no page -> Yes -> CountryOfNationality Uk yes no page" in {
        val answers = baseAnswers
          .set(CountryOfNationalityYesNoPage(index), true).success.value

        navigator.nextPage(CountryOfNationalityYesNoPage(index), draftId, answers)
          .mustBe(mld5irts.CountryOfNationalityInTheUkYesNoController.onPageLoad(index, draftId))
      }

      "CountryOfNationalityInUK yes no page -> No -> CountryOfNationality page" in {
        val answers = baseAnswers
          .set(CountryOfNationalityInTheUkYesNoPage(index), false).success.value

        navigator.nextPage(CountryOfNationalityInTheUkYesNoPage(index), draftId, answers)
          .mustBe(mld5irts.CountryOfNationalityController.onPageLoad(index, draftId))
      }

      "CountryOfNationalityInUK yes no page -> Yes -> Nino yes no page" in {
        val answers = baseAnswers
          .set(CountryOfNationalityInTheUkYesNoPage(index), true).success.value

        navigator.nextPage(CountryOfNationalityInTheUkYesNoPage(index), draftId, answers)
          .mustBe(irts.NationalInsuranceYesNoController.onPageLoad(index, draftId))
      }

      "CountryOfNationality -> Nino Yes No page" in {

        val answers = baseAnswers
          .set(CountryOfNationalityPage(index), ES).success.value

        navigator.nextPage(CountryOfNationalityPage(index), draftId, answers)
          .mustBe(irts.NationalInsuranceYesNoController.onPageLoad(index, draftId))

      }

      "NationalInsuranceNumber yes no page -> No -> CountryOfResidence yes no page" in {
        val answers = baseAnswers
          .set(NationalInsuranceYesNoPage(index), false).success.value

        navigator.nextPage(NationalInsuranceYesNoPage(index), draftId, answers)
          .mustBe(mld5irts.CountryOfResidenceYesNoController.onPageLoad(index, draftId))
      }

      "CountryOfResidence yes no page -> No -> Address yes no page" in {
        val answers = baseAnswers
          .set(CountryOfResidenceYesNoPage(index), false).success.value

        navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, answers)
          .mustBe(irts.AddressYesNoController.onPageLoad(index, draftId))
      }

      "CountryOfResidence yes no page -> Yes -> CountryOfResidence Uk yes no page" in {
        val answers = baseAnswers
          .set(CountryOfResidenceYesNoPage(index), true).success.value

        navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, answers)
          .mustBe(mld5irts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId))
      }

      "CountryOfResidenceInUK yes no page -> No -> CountryOfResidence page" in {
        val answers = baseAnswers
          .set(CountryOfResidenceInTheUkYesNoPage(index), false).success.value

        navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), draftId, answers)
          .mustBe(mld5irts.CountryOfResidenceController.onPageLoad(index, draftId))
      }

      "CountryOfResidenceInUK yes no page -> Yes -> Address yes no page" in {
        val answers = baseAnswers
          .set(CountryOfResidenceInTheUkYesNoPage(index), true).success.value

        navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), draftId, answers)
          .mustBe(irts.AddressYesNoController.onPageLoad(index, draftId))
      }

      "CountryOfResidence (with Nino) -> MentalCapacityYesNo page" in {

        val answers = baseAnswers
          .set(NationalInsuranceYesNoPage(index), true).success.value
          .set(CountryOfResidencePage(index), ES).success.value

        navigator.nextPage(CountryOfResidencePage(index), draftId, answers)
          .mustBe(mld5irts.MentalCapacityYesNoController.onPageLoad(index, draftId))

      }

      "CountryOfResidence (with No Nino) -> Address Yes No page" in {

        val answers = baseAnswers
          .set(NationalInsuranceYesNoPage(index), false).success.value
          .set(CountryOfResidencePage(index), ES).success.value

        navigator.nextPage(CountryOfResidencePage(index), draftId, answers)
          .mustBe(irts.AddressYesNoController.onPageLoad(index, draftId))

      }

      "Address Yes No page -> No -> MentalCapacityYesNo page" in {

        val answers = baseAnswers
          .set(AddressYesNoPage(index), false).success.value

        navigator.nextPage(AddressYesNoPage(index), draftId, answers)
          .mustBe(mld5irts.MentalCapacityYesNoController.onPageLoad(index, draftId))

      }

      "PassportDetailsPage -> MentalCapacityYesNo page" in {

        navigator.nextPage(PassportDetailsPage(index), fakeDraftId, baseAnswers)
          .mustBe(mld5irts.MentalCapacityYesNoController.onPageLoad(index, fakeDraftId))

      }

      "IDCardDetails Yes No page -> No -> MentalCapacityYesNo page" in {

        val answers = baseAnswers
          .set(IDCardDetailsYesNoPage(index), false).success.value

        navigator.nextPage(IDCardDetailsYesNoPage(index), draftId, answers)
          .mustBe(mld5irts.MentalCapacityYesNoController.onPageLoad(index, draftId))

      }

      "IDCardDetails page -> MentalCapacityYesNo page" in {

        navigator.nextPage(IDCardDetailsPage(index), fakeDraftId, baseAnswers)
          .mustBe(mld5irts.MentalCapacityYesNoController.onPageLoad(index, fakeDraftId))

      }

      "Mental Capacity Yes No page -> Check details page" in {
        navigator.nextPage(MentalCapacityYesNoPage(index), draftId, baseAnswers)
          .mustBe(irts.CheckDetailsController.onPageLoad(index, draftId))
      }

    }

  }
}
